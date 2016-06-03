package com.kingwaytek.cpami.bykingTablet.app.ui;

import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.location.Location;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.content.ContextCompat;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Switch;
import android.widget.TextView;

import com.google.android.gms.common.GooglePlayServicesNotAvailableException;
import com.google.android.gms.common.GooglePlayServicesRepairableException;
import com.google.android.gms.common.api.PendingResult;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.location.places.AutocompletePrediction;
import com.google.android.gms.location.places.AutocompletePredictionBuffer;
import com.google.android.gms.location.places.Place;
import com.google.android.gms.location.places.PlaceLikelihood;
import com.google.android.gms.location.places.PlaceLikelihoodBuffer;
import com.google.android.gms.location.places.Places;
import com.google.android.gms.location.places.ui.PlaceAutocomplete;
import com.google.android.gms.location.places.ui.PlacePicker;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.BitmapDescriptor;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;
import com.kingwaytek.cpami.bykingTablet.R;
import com.kingwaytek.cpami.bykingTablet.app.model.DataArray;
import com.kingwaytek.cpami.bykingTablet.app.model.items.ItemsMyPOI;
import com.kingwaytek.cpami.bykingTablet.app.ui.poi.UiMyPoiInfoActivity;
import com.kingwaytek.cpami.bykingTablet.app.web.WebAgent;
import com.kingwaytek.cpami.bykingTablet.callbacks.OnPhotoRemovedCallBack;
import com.kingwaytek.cpami.bykingTablet.hardware.MyLocationManager;
import com.kingwaytek.cpami.bykingTablet.utilities.BitmapUtility;
import com.kingwaytek.cpami.bykingTablet.utilities.DialogHelper;
import com.kingwaytek.cpami.bykingTablet.utilities.FavoriteHelper;
import com.kingwaytek.cpami.bykingTablet.utilities.ImageSelectHelper;
import com.kingwaytek.cpami.bykingTablet.utilities.JsonParser;
import com.kingwaytek.cpami.bykingTablet.utilities.PolyHelper;
import com.kingwaytek.cpami.bykingTablet.utilities.PopWindowHelper;
import com.kingwaytek.cpami.bykingTablet.utilities.SettingManager;
import com.kingwaytek.cpami.bykingTablet.utilities.Utility;

import java.io.InputStream;
import java.util.ArrayList;

/**
 * 地址查詢、關鍵字查詢、周邊景點 and blah blah blah~<br>
 * 都可以在這裡做！
 *
 * @author Vincent (2016/4/15).
 */
public class UiMainMapActivity extends BaseGoogleApiActivity implements TextWatcher, GoogleMap.OnMapLongClickListener,
        OnPhotoRemovedCallBack {

    private boolean isFirstTimeRun = true;  //每次startActivity過來這個值都會被重設，除非設為static

    private Marker myNewMarker;
    private Marker lastAroundPoiMarker;
    private Marker selectedMarker;

    private Marker myPositionMarker;
    private Polyline polyLine;

    private ArrayList<Marker> myPoiMarkerList;

    private ImageView poiImageView;
    private String photoPath;

    @Override
    protected void onApiReady() {
        //showRightButtons(true);
        putMarkersAndSetListener();
        registerPreferenceChangedListener();
        Log.i(TAG, "onApiReady!!!");
    }

    @Override
    protected String getActionBarTitle() {
        return getString(R.string.poi_search_location);
    }

    @Override
    protected void setListener() {
        searchText.addTextChangedListener(this);
        searchText.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                searchLocation((String)parent.getItemAtPosition(position));
                clearSearchText();
                hideKeyboard(true);
            }
        });
    }

    @Override
    public void onStop() {
        super.onStop();
        unRegisterPreferenceChangedListener();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        searchText.removeTextChangedListener(this);
        map.setOnMapLongClickListener(null);
    }

    private void putMarkersAndSetListener() {
        map.setOnMapLongClickListener(this);
        map.setOnMarkerClickListener(this);

        if (isFirstTimeRun) {
            if (SettingManager.MarkerFlag.getMyPoiFlag())
                new PutAllMyPoiMarkers().execute();

            isFirstTimeRun = false;
        }
    }

    @Override
    public boolean onMarkerClick(Marker marker) {
        selectedMarker = marker;

        if (FavoriteHelper.isPoiExisted(marker.getPosition().latitude, marker.getPosition().longitude))
            showMarkerButtonLayout(true, true);
        else
            showMarkerButtonLayout(true, false);

        return false;
    }

    @Override
    public View getInfoWindow(Marker marker) {
        if (notNull(marker.getSnippet())) {
            String key = String.valueOf(marker.getPosition().latitude) + String.valueOf(marker.getPosition().longitude);
            View view;

            if (markerTypeMap.containsKey(key)) {
                switch (markerTypeMap.get(key)) {
                    case R.drawable.ic_my_poi:
                        view = getPoiInfoWindowView(marker.getPosition());
                        break;

                    default:
                        view = getDefaultInfoWindowView();
                        break;
                }
            }
            else
                view = getDefaultInfoWindowView();

            TextView markerTitle = (TextView) view.findViewById(R.id.marker_title);
            TextView markerSnippet = (TextView) view.findViewById(R.id.marker_snippet);

            markerTitle.setText(marker.getTitle());
            markerSnippet.setText(marker.getSnippet());

            return view;
        }
        else
            return super.getInfoWindow(marker);
    }

    @Override
    public void onInfoWindowClick(Marker marker) {
        switch (ENTRY_TYPE) {
            case ENTRY_TYPE_DEFAULT:
                selectedMarker = marker;
                String key = String.valueOf(marker.getPosition().latitude) + String.valueOf(marker.getPosition().longitude);

                if (markerTypeMap.containsKey(key)) {
                    switch (markerTypeMap.get(key)) {
                        case R.drawable.ic_end:
                            editMyPoi(marker.getPosition(), null);
                            break;

                        case R.drawable.ic_my_poi:
                            if (FavoriteHelper.isPoiExisted(marker.getPosition().latitude, marker.getPosition().longitude)) {
                                Intent intent = new Intent(this, UiMyPoiInfoActivity.class);
                                Bundle bundle = new Bundle();
                                bundle.putSerializable(BUNDLE_MY_POI_INFO, FavoriteHelper.getMyPoiItem());

                                intent.putExtras(bundle);
                                intent.putExtra(BUNDLE_MAP_TO_POI_INFO, true);
                                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);

                                startActivityForResult(intent, REQUEST_RELOAD_MARKER);
                            }
                            break;

                        case R.drawable.ic_search_result:
                        case R.drawable.ic_around_poi:
                            editMyPoi(marker.getPosition(), marker.getTitle());
                            break;
                    }
                }
                break;

            case ENTRY_TYPE_LOCATION_SELECT:
                Intent intent = new Intent();
                Bundle bundle = new Bundle();

                bundle.putString(BUNDLE_LOCATION_TITLE, marker.getTitle());
                bundle.putParcelable(BUNDLE_LOCATION_LATLNG, marker.getPosition());

                intent.putExtras(bundle);

                setResult(RESULT_OK, intent);
                finish();
                break;
        }
    }

    private View getDefaultInfoWindowView() {
        return getLayoutInflater().inflate(R.layout.inflate_marker_poi_info_window, null);
    }

    private View getPoiInfoWindowView(LatLng latLng) {
        View view = getLayoutInflater().inflate(R.layout.inflate_marker_my_poi_wndow, null);

        ImageView markerPoiPhoto = (ImageView) view.findViewById(R.id.marker_poiPhoto);

        String photoPath = "";

        if (FavoriteHelper.isPoiExisted(latLng.latitude, latLng.longitude))
            photoPath = FavoriteHelper.getMyPoiItem().PHOTO_PATH;

        if (notNull(photoPath) && !photoPath.isEmpty()) {
            int imgSize = getResources().getDimensionPixelSize(R.dimen.poi_photo_edit_view);
            markerPoiPhoto.setImageBitmap(BitmapUtility.getDecodedBitmap(photoPath, imgSize, imgSize));
        }
        else
            markerPoiPhoto.setVisibility(View.GONE);

        return view;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        super.onOptionsItemSelected(item);

        switch (item.getItemId()) {
            case ACTION_SWITCH:
                showSwitchPopView();
                break;

            case ACTION_AROUND:
                goToPlacePicker();
                break;
        }

        return true;
    }

    private void showSwitchPopView() {
        View view = PopWindowHelper.getMarkerSwitchWindowView(searchTextLayout);

        Switch switch_myPoi = (Switch) view.findViewById(R.id.switch_my_poi);

        switch_myPoi.setChecked(SettingManager.MarkerFlag.getMyPoiFlag());

        switch_myPoi.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                SettingManager.MarkerFlag.setMyPoiFlag(isChecked);
            }
        });
    }

    private void goToPlacePicker() {
        try {
            if (Build.VERSION.SDK_INT < 21) {
                Intent intent = new PlaceAutocomplete.IntentBuilder(PlaceAutocomplete.MODE_OVERLAY).build(this);
                startActivityForResult(intent, REQUEST_PLACE_PICKER);
            }
            else {
                PlacePicker.IntentBuilder pickerBuilder = new PlacePicker.IntentBuilder();
                startActivityForResult(pickerBuilder.build(this), REQUEST_PLACE_PICKER);
            }
        }
        catch (GooglePlayServicesNotAvailableException | GooglePlayServicesRepairableException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        Log.i(TAG, "onActivityResult: " + requestCode + " " + resultCode);

        if (resultCode == RESULT_OK) {
            switch (requestCode) {
                case REQUEST_PLACE_PICKER:
                    Place place = PlacePicker.getPlace(this, data);
                    putMarkerAndMoveCameraByPlace(place);
                    break;

                case REQUEST_PHOTO_FROM_GALLERY:
                case REQUEST_PHOTO_FROM_CAMERA:
                    getPhotoPathAndSetImageView(requestCode, data);
                    break;

                case REQUEST_RELOAD_MARKER:
                    if (FavoriteHelper.isPoiExisted(selectedMarker.getPosition().latitude, selectedMarker.getPosition().longitude)) {
                        ItemsMyPOI poiItem = FavoriteHelper.getMyPoiItem();
                        if (notNull(poiItem))
                            reloadMarker(poiItem.TITLE, poiItem.DESCRIPTION, false);
                    }
                    break;
            }
        }
        else if (resultCode == RESULT_DELETE) {
            switch (requestCode) {
                case REQUEST_RELOAD_MARKER:
                    double[] latLng = data.getDoubleArrayExtra(BUNDLE_DELETE_POI);
                    removePoiAndMarker(latLng[0], latLng[1]);
                    break;
            }
        }
    }

    private void putMarkerAndMoveCameraByPlace(Place place) {
        Utility.toastShort(place.getName().toString());
        moveCameraAndZoom(place.getLatLng(), 17);

        if (notNull(lastAroundPoiMarker))
            lastAroundPoiMarker.remove();

        MarkerOptions marker = new MarkerOptions();

        marker.position(place.getLatLng());
        marker.title(place.getName().toString());
        marker.snippet(place.getAddress().toString());

        InputStream is = getResources().openRawResource(+R.drawable.ic_around_poi);
        marker.icon(BitmapDescriptorFactory.fromBitmap(BitmapFactory.decodeStream(is)));

        setMarkerTypeMap(place.getLatLng().latitude, place.getLatLng().longitude, R.drawable.ic_around_poi);

        lastAroundPoiMarker = map.addMarker(marker);

        if (notNull(lastAroundPoiMarker))
            lastAroundPoiMarker.showInfoWindow();

        closeInputStream(is);
    }

    // TODO 這裡尚未實際應用到!!!
    @Override
    protected void onLocateMyPosition(Location location) {
        try {
            PendingResult<PlaceLikelihoodBuffer> results = Places.PlaceDetectionApi.getCurrentPlace(googleApiClient, null);

            results.setResultCallback(new ResultCallback<PlaceLikelihoodBuffer>() {
                @Override
                public void onResult(@NonNull PlaceLikelihoodBuffer placeLikelihoods) {
                    for (PlaceLikelihood placeLikelihood : placeLikelihoods) {
                        Log.i(TAG, String.format("Place '%s' has likelihood: %g",
                                placeLikelihood.getPlace().getName(),
                                placeLikelihood.getLikelihood()));
                    }
                    placeLikelihoods.release();
                }
            });
        }
        catch (SecurityException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

    @Override
    public void onTextChanged(CharSequence s, int start, int before, int count) {}

    @Override
    public void afterTextChanged(final Editable s) {
        if (s.length() > 0) {
            LatLngBounds latLngBounds = map.getProjection().getVisibleRegion().latLngBounds;
            PendingResult<AutocompletePredictionBuffer> results = Places.GeoDataApi.getAutocompletePredictions(
                    googleApiClient, s.toString(), latLngBounds, null);

            final ArrayList<String> nameList = new ArrayList<>();

            results.setResultCallback(new ResultCallback<AutocompletePredictionBuffer>() {
                @Override
                public void onResult(@NonNull AutocompletePredictionBuffer autocompletePredictions) {
                    for (AutocompletePrediction prediction : autocompletePredictions) {
                        Log.i(TAG, String.format("PlaceId: '%s', PrimaryText: '%s', SecondaryText: '%s', Type: '%s'",
                                prediction.getPlaceId(),
                                prediction.getPrimaryText(null),
                                prediction.getSecondaryText(null),
                                prediction.getPlaceTypes()));

                        nameList.add(prediction.getPrimaryText(null).toString());
                    }
                    autocompletePredictions.release();
                    setAutoCompleteText(nameList);

                    Log.i(TAG, "TextChanged: " + s.length());
                }
            });
        }
        else {
            searchText.setAdapter(null);
            searchText.dismissDropDown();
            Log.i(TAG, "TextChanged: empty!");
        }
    }

    private void setAutoCompleteText(ArrayList<String> nameList) {
        searchText.setAdapter(getSimpleAdapter(nameList));
        searchText.setDropDownBackgroundResource(R.drawable.background_search_adapter);
        if (searchText.getText().length() > 0)
            searchText.showDropDown();
    }

    @Override
    public void onMapLongClick(LatLng latLng) {
        putNewMarker(latLng);
    }

    private void putNewMarker(LatLng latLng) {
        MarkerOptions marker = new MarkerOptions();
        marker.position(latLng);
        marker.title("選擇點位：");
        marker.snippet(String.valueOf(latLng.latitude + "\n" + latLng.longitude));

        InputStream is = getResources().openRawResource(+R.drawable.ic_end);
        marker.icon(BitmapDescriptorFactory.fromBitmap(BitmapFactory.decodeStream(is)));

        setMarkerTypeMap(latLng.latitude, latLng.longitude, R.drawable.ic_end);

        if (notNull(myNewMarker))
            myNewMarker.remove();

        myNewMarker = map.addMarker(marker);
        myNewMarker.showInfoWindow();

        if (map.getCameraPosition().zoom < 16)
            moveCameraAndZoom(latLng, 16);
        else
            moveCamera(latLng);

        onMarkerClick(myNewMarker);

        closeInputStream(is);
    }

    private void editMyPoi(final LatLng latLng, @Nullable String title) {
        View view = PopWindowHelper.getPoiEditWindowView(this, searchTextLayout);

        final EditText poiTitle = (EditText) view.findViewById(R.id.edit_poiTitle);
        final EditText poiContent = (EditText) view.findViewById(R.id.edit_poiContent);
        poiImageView = (ImageView) view.findViewById(R.id.image_poiPhoto);
        TextView poiLatLng = (TextView) view.findViewById(R.id.text_poiLatLng);
        Button poiBtnSave = (Button) view.findViewById(R.id.btn_poiSave);
        Button poiBtnCancel = (Button) view.findViewById(R.id.btn_poiCancel);

        if (notNull(title)) {
            poiTitle.setText(title);
            poiTitle.setSelection(title.length());
        }

        String poiLocation = String.valueOf("\n" + latLng.latitude + ",\n" + latLng.longitude);
        poiLatLng.setText(getString(R.string.poi_lat_lng, poiLocation));

        photoPath = "";
        final boolean isPoiExisted = FavoriteHelper.isPoiExisted(latLng.latitude, latLng.longitude);

        poiBtnSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String title = poiTitle.getText().toString();
                String content = poiContent.getText().toString();

                if (!title.isEmpty()) {
                    if (isPoiExisted) {
                        FavoriteHelper.updateMyPoi(title, content, photoPath);
                        Utility.toastShort(getString(R.string.poi_update_done));
                        PopWindowHelper.dismissPopWindow();

                        reloadMarker(title, content, false);
                    }
                    else {
                        FavoriteHelper.addMyPoi(title, content, latLng.latitude, latLng.longitude, photoPath);
                        Utility.toastShort(getString(R.string.poi_save_done));
                        PopWindowHelper.dismissPopWindow();

                        reloadMarker(title, content, true);
                    }
                }
                else
                    Utility.toastShort(getString(R.string.poi_require_title));
            }
        });

        poiBtnCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                PopWindowHelper.dismissPopWindow();
            }
        });

        if (isPoiExisted) {
            final ItemsMyPOI poiItem = FavoriteHelper.getMyPoiItem();
            if (notNull(poiItem)) {
                TextView poiBanner = (TextView) view.findViewById(R.id.poiBanner);
                Button poiBtnDelete = (Button) view.findViewById(R.id.btn_poiDelete);

                poiBanner.setText(getString(R.string.poi_edit_a_exist_one));
                poiBtnDelete.setVisibility(View.VISIBLE);

                poiBtnDelete.setOnClickListener(getDeleteClickListener(poiItem));

                poiTitle.setText(poiItem.TITLE);
                poiTitle.setSelection(poiItem.TITLE.length());
                poiContent.setText(poiItem.DESCRIPTION);

                photoPath = poiItem.PHOTO_PATH;

                Log.i(TAG, "PhotoPathFromFavorite: " + poiItem.PHOTO_PATH);

                if (!poiItem.PHOTO_PATH.isEmpty())
                    setPoiImageView(poiItem.PHOTO_PATH);
            }
        }
        setImageClickListener();
    }

    private void setImageClickListener() {
        if (photoPath == null || photoPath.isEmpty())
            poiImageView.setOnClickListener(ImageSelectHelper.getImageClick(this, null));
        else
            poiImageView.setOnClickListener(ImageSelectHelper.getImageClick(this, this));
    }

    private View.OnClickListener getDeleteClickListener(final ItemsMyPOI poiItem) {
        return new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DialogHelper.showDeleteConfirmDialog(UiMainMapActivity.this, poiItem.TITLE, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        removePoiAndMarker(poiItem.LAT, poiItem.LNG);
                        PopWindowHelper.dismissPopWindow();
                    }
                });
            }
        };
    }

    private void removePoiAndMarker(double lat, double lng) {
        FavoriteHelper.removeMyPoi(lat, lng);

        for (int i = 0; i < myPoiMarkerList.size(); i++) {
            Marker marker = myPoiMarkerList.get(i);

            if (marker.getPosition().latitude == lat && marker.getPosition().longitude == lng) {
                marker.remove();
                myPoiMarkerList.remove(i);
                Utility.toastShort(getString(R.string.poi_delete_done));

                showMarkerButtonLayout(false, false);
                break;
            }
        }
    }

    private void reloadMarker(String title, String snippet, boolean addToList) {
        if (notNull(selectedMarker)) {
            selectedMarker.setTitle(title);
            selectedMarker.setSnippet(snippet);

            InputStream is = this.getResources().openRawResource(+R.drawable.ic_my_poi);
            Bitmap bitmap = BitmapFactory.decodeStream(is);

            selectedMarker.setIcon(BitmapDescriptorFactory.fromBitmap(bitmap));
            closeInputStream(is);

            setMarkerTypeMap(selectedMarker.getPosition().latitude, selectedMarker.getPosition().longitude, R.drawable.ic_my_poi);

            selectedMarker.showInfoWindow();
            onMarkerClick(selectedMarker);

            if (addToList)
                myPoiMarkerList.add(selectedMarker);

            SettingManager.MarkerFlag.setMyPoiFlag(true);

            Log.i(TAG, "PoiListSize: " + myPoiMarkerList.size());
        }
    }

    private void getPhotoPathAndSetImageView(int requestCode, Intent data) {
        photoPath = ImageSelectHelper.getPhotoPath(this, requestCode, data);
        setPoiImageView(photoPath);
        setImageClickListener();
    }

    private void setPoiImageView(String photoPath) {
        int reqSize = getResources().getDimensionPixelSize(R.dimen.poi_photo_edit_view);
        poiImageView.setImageBitmap(BitmapUtility.getDecodedBitmap(photoPath, reqSize, reqSize));
    }

    @Override
    public void onPhotoRemoved() {
        photoPath = "";
        poiImageView.setImageResource(R.drawable.selector_add_photo);
        setImageClickListener();
    }

    @Override
    protected void onMarkerEditClick() {
        editMyPoi(selectedMarker.getPosition(), null);
    }

    private class PutAllMyPoiMarkers extends AsyncTask<Void, Void, Void> {

        @Override
        protected void onPreExecute() {
            showLoadingCircle(true);
            removeAllMyPoiMarkers();

            myPoiMarkerList = new ArrayList<>();
        }

        @Override
        protected Void doInBackground(Void... params) {
            ArrayList<ItemsMyPOI> myPoiList = DataArray.getMyPOI();

            if (notNull(myPoiList)) {
                InputStream is = getResources().openRawResource(+R.drawable.ic_my_poi);
                BitmapDescriptor iconBitmap = BitmapDescriptorFactory.fromBitmap(BitmapFactory.decodeStream(is));

                for (ItemsMyPOI poiItem : myPoiList) {
                    final MarkerOptions marker = new MarkerOptions();

                    marker.position(new LatLng(poiItem.LAT, poiItem.LNG));
                    marker.title(poiItem.TITLE);
                    marker.snippet(poiItem.DESCRIPTION);
                    marker.icon(iconBitmap);

                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            myPoiMarkerList.add(map.addMarker(marker));
                        }
                    });

                    setMarkerTypeMap(poiItem.LAT, poiItem.LNG, R.drawable.ic_my_poi);
                }
                closeInputStream(is);
            }
            else
                Utility.showToastOnNewThread(getString(R.string.poi_empty));

            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            showLoadingCircle(false);
            checkIntentAndDoActions();
        }
    }

    private void removeAllMyPoiMarkers() {
        showLoadingCircle(true);

        if (notNull(myPoiMarkerList) && !myPoiMarkerList.isEmpty()) {
            for (Marker marker : myPoiMarkerList) {
                marker.remove();
            }
            myPoiMarkerList.clear();
        }
        showLoadingCircle(false);
    }

    @Override
    public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {
        switch (key) {
            case SettingManager.PREFS_MARKER_MY_POI:
                if (SettingManager.MarkerFlag.getMyPoiFlag())
                    new PutAllMyPoiMarkers().execute();
                else
                    removeAllMyPoiMarkers();
                break;
        }
    }

    private void checkIntentAndDoActions() {
        Intent intent = getIntent();

        if (intent.hasExtra(BUNDLE_MY_POI_INFO)) {
            ItemsMyPOI poiItem = (ItemsMyPOI) intent.getSerializableExtra(BUNDLE_MY_POI_INFO);
            moveCameraByPoiItem(poiItem);

            intent.removeExtra(BUNDLE_MY_POI_INFO);
        }
    }

    private void moveCameraByPoiItem(ItemsMyPOI poiItem) {
        for (Marker marker : myPoiMarkerList) {
            if (marker.getPosition().latitude == poiItem.LAT && marker.getPosition().longitude == poiItem.LNG) {
                moveCameraAndZoom(marker.getPosition(), 16);
                marker.showInfoWindow();
                onMarkerClick(marker);
                break;
            }
        }
    }

    @Override
    protected void onMarkerDirectionClick() {
        startDirection();
    }

    private void startDirection() {
        Location location = MyLocationManager.getLastLocation();
        if (location == null) {
            Utility.toastShort(getString(R.string.gps_unable_to_get_location));
            return;
        }

        PopWindowHelper.showLoadingWindow(this);

        String origin = location.getLatitude() + "," + location.getLongitude();
        String destination = selectedMarker.getPosition().latitude + "," + selectedMarker.getPosition().longitude;

        String avoidOption = getAvoidOptions(DIR_AVOID_TOLLS, DIR_AVOID_HIGHWAYS);

        WebAgent.getDirectionsData(origin, destination, DIR_MODE_DRIVING, avoidOption, new WebAgent.WebResultImplement() {
            @Override
            public void onResultSucceed(String response) {
                getPolyLineAndDrawLine(response);
                PopWindowHelper.dismissPopWindow();
            }

            @Override
            public void onResultFail(String errorMessage) {
                Utility.toastShort(errorMessage);
            }
        });
    }

    private String getAvoidOptions(String... avoidOptions) {
        StringBuilder sb = new StringBuilder();

        for (int i = 0; i < avoidOptions.length; i++) {
            if (i != 0)
                sb.append("|");
            sb.append(avoidOptions[i]);
        }
        return sb.toString();
    }

    private void getPolyLineAndDrawLine(String jsonString) {
        String polyOverview = JsonParser.getPolyLineOverview(jsonString);

        if (notNull(polyOverview)) {
            ArrayList<LatLng> linePoints = PolyHelper.decodePolyLine(polyOverview);

            PolylineOptions polyOptions = new PolylineOptions();

            for (LatLng latLng : linePoints) {
                polyOptions.add(latLng);
            }
            polyOptions.color(ContextCompat.getColor(this, R.color.md_blue_600));
            polyOptions.width(14);

            if (notNull(myPositionMarker))
                myPositionMarker.remove();

            if (notNull(polyLine))
                polyLine.remove();

            MarkerOptions marker = new MarkerOptions();
            marker.position(linePoints.get(0));

            InputStream is = getResources().openRawResource(+ R.drawable.ic_start);
            marker.icon(BitmapDescriptorFactory.fromBitmap(BitmapFactory.decodeStream(is)));

            myPositionMarker = map.addMarker(marker);
            polyLine = map.addPolyline(polyOptions);

            PopWindowHelper.dismissPopWindow();
            moveCamera(linePoints.get(0));

            closeInputStream(is);
        }
    }
}
