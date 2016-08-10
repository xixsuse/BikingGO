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
import android.os.Handler;
import android.os.HandlerThread;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.content.ContextCompat;
import android.text.TextWatcher;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.Switch;
import android.widget.TextView;

import com.google.android.gms.common.GooglePlayServicesNotAvailableException;
import com.google.android.gms.common.GooglePlayServicesRepairableException;
import com.google.android.gms.common.api.PendingResult;
import com.google.android.gms.common.api.ResultCallback;
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
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;
import com.kingwaytek.cpami.bykingTablet.AppController;
import com.kingwaytek.cpami.bykingTablet.R;
import com.kingwaytek.cpami.bykingTablet.app.model.DataArray;
import com.kingwaytek.cpami.bykingTablet.app.model.items.ItemsMyPOI;
import com.kingwaytek.cpami.bykingTablet.app.model.items.ItemsPathStep;
import com.kingwaytek.cpami.bykingTablet.app.ui.poi.UiMyPoiInfoActivity;
import com.kingwaytek.cpami.bykingTablet.app.web.WebAgent;
import com.kingwaytek.cpami.bykingTablet.callbacks.OnPhotoRemovedCallBack;
import com.kingwaytek.cpami.bykingTablet.hardware.MyLocationManager;
import com.kingwaytek.cpami.bykingTablet.utilities.BitmapUtility;
import com.kingwaytek.cpami.bykingTablet.utilities.DialogHelper;
import com.kingwaytek.cpami.bykingTablet.utilities.FavoriteHelper;
import com.kingwaytek.cpami.bykingTablet.utilities.ImageSelectHelper;
import com.kingwaytek.cpami.bykingTablet.utilities.JsonParser;
import com.kingwaytek.cpami.bykingTablet.utilities.MenuHelper;
import com.kingwaytek.cpami.bykingTablet.utilities.PolyHelper;
import com.kingwaytek.cpami.bykingTablet.utilities.PopWindowHelper;
import com.kingwaytek.cpami.bykingTablet.utilities.SettingManager;
import com.kingwaytek.cpami.bykingTablet.utilities.Utility;
import com.kingwaytek.cpami.bykingTablet.utilities.adapter.PathListViewAdapter;

import java.io.InputStream;
import java.util.ArrayList;

/**
 * 地址查詢、關鍵字查詢、周邊景點 and blah blah blah~<br>
 * 都可以在這裡做！
 *
 * @author Vincent (2016/4/15).
 */
public class UiMainMapActivity extends BaseGoogleApiActivity implements TextWatcher, GoogleMap.OnMapLongClickListener,
        OnPhotoRemovedCallBack, MapLayerHandler.OnLayerChangedCallback {

    private boolean isFirstTimeRun = true;  //每次startActivity過來這個值都會被重設，除非設為static

    private Menu menu;

    private Marker myNewMarker;
    private Marker lastAroundPoiMarker;
    private Marker selectedMarker;

    private Marker myPositionMarker;
    private Polyline polyLine;

    private ArrayList<Marker> myPoiMarkerList;

    private ImageView poiImageView;
    private String photoPath;

    private MapLayerHandler layerHandler;
    private static final String NAME_OF_HANDLER_THREAD = "LayerHandlerThread";
    private static HandlerThread handlerThread;

    private LinearLayout pathInfoLayout;
    private ListView pathListView;
    private Polyline highLightPoly;

    private TextView polylineName;
    private TextView polylineLocation;
    private TextView polylineDescription;

    @Override
    protected void onApiReady() {
        //showRightButtons(true);
        checkIntentAndDoActions();
        registerPreferenceChangedListener();
        Log.i(TAG, "onApiReady!!!");
    }

    @Override
    protected String getActionBarTitle() {
        return getString(R.string.app_name);
    }

    @Override
    protected void findViews() {
        super.findViews();
        pathInfoLayout = (LinearLayout) findViewById(R.id.pathInfoLayout);
        pathListView = (ListView) findViewById(R.id.pathListView);

        polylineInfoLayout = (RelativeLayout) findViewById(R.id.polylineInfoLayout);
        polylineName = (TextView) findViewById(R.id.text_polylineName);
        polylineLocation = (TextView) findViewById(R.id.text_polylineLocation);
        polylineDescription = (TextView) findViewById(R.id.text_polylineDescription);
    }

    @Override
    protected void setListener() {

    }

    @Override
    public void onStop() {
        super.onStop();
        unRegisterPreferenceChangedListener();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        map.setOnMapLongClickListener(null);
        closeAllLayerFlag();
    }

    private void checkIntentAndDoActions() {
        if (isFirstTimeRun)
        {
            switch (ENTRY_TYPE) {
                case ENTRY_TYPE_DEFAULT:
                    map.setOnMapLongClickListener(this);
                    map.setOnMarkerClickListener(this);

                    if (SettingManager.MapLayer.getMyPoiFlag())
                        new PutAllMyPoiMarkers().execute();

                    break;

                case ENTRY_TYPE_LOCATION_SELECT:
                    map.setOnMapLongClickListener(this);

                    if (SettingManager.MapLayer.getMyPoiFlag())
                        new PutAllMyPoiMarkers().execute();

                    break;
            }
            isFirstTimeRun = false;
        }
    }

    @Override
    public boolean onMarkerClick(Marker marker) {
        if (ENTRY_TYPE == ENTRY_TYPE_DEFAULT) {
            selectedMarker = marker;

            if (FavoriteHelper.isPoiExisted(marker.getPosition().latitude, marker.getPosition().longitude))
                showMarkerButtonLayout(true, true);
            else
                showMarkerButtonLayout(true, false);
        }
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
                            editMyPoi(marker.getPosition(), null, null);
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
                            editMyPoi(marker.getPosition(), marker.getTitle(), marker.getSnippet());
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
    public boolean onCreateOptionsMenu(Menu menu) {
        super.onCreateOptionsMenu(menu);
        this.menu = menu;

        return true;
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

            case ACTION_LIST:
                showPathInfo();
                break;
        }

        return true;
    }

    private void showSwitchPopView() {
        View view = PopWindowHelper.getMarkerSwitchWindowView(searchTextLayout);

        final Switch switch_myPoi = (Switch) view.findViewById(R.id.switch_my_poi);
        final Switch switch_layerCycling = (Switch) view.findViewById(R.id.switch_layer_cycling_1);
        final Switch switch_layerTopTen = (Switch) view.findViewById(R.id.switch_layer_top_ten);
        final Switch switch_layerRecommended = (Switch) view.findViewById(R.id.switch_layer_recommended);
        final Switch switch_layerAllOfTaiwan = (Switch) view.findViewById(R.id.switch_layer_all_of_taiwan);
        final Switch switch_layerRentStation = (Switch) view.findViewById(R.id.switch_layer_rent_station);

        switch_myPoi.setChecked(SettingManager.MapLayer.getMyPoiFlag());
        switch_layerCycling.setChecked(SettingManager.MapLayer.getCyclingLayer());
        switch_layerTopTen.setChecked(SettingManager.MapLayer.getTopTenLayer());
        switch_layerRecommended.setChecked(SettingManager.MapLayer.getRecommendedLayer());
        switch_layerAllOfTaiwan.setChecked(SettingManager.MapLayer.getAllOfTaiwanLayer());
        switch_layerRentStation.setChecked(SettingManager.MapLayer.getRentStationLayer());

        switch_myPoi.setTag(switch_myPoi.getId());
        switch_layerCycling.setTag(switch_layerCycling.getId());
        switch_layerTopTen.setTag(switch_layerTopTen.getId());
        switch_layerRecommended.setTag(switch_layerRecommended.getId());
        switch_layerAllOfTaiwan.setTag(switch_layerAllOfTaiwan.getId());
        switch_layerRentStation.setTag(switch_layerRentStation.getId());

        CompoundButton.OnCheckedChangeListener checkedChangeListener = new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                switch ((int)buttonView.getTag()) {
                    case R.id.switch_my_poi:
                        switch_myPoi.setChecked(isChecked);
                        SettingManager.MapLayer.setMyPoiFlag(isChecked);
                        break;

                    case R.id.switch_layer_cycling_1:
                        if (isChecked) {
                            switch_layerAllOfTaiwan.setChecked(false);
                            switch_layerRentStation.setChecked(false);
                        }
                        switch_layerCycling.setChecked(isChecked);
                        SettingManager.MapLayer.setCyclingLayer(isChecked);
                        break;

                    case R.id.switch_layer_top_ten:
                        if (isChecked) {
                            switch_layerAllOfTaiwan.setChecked(false);
                            switch_layerRentStation.setChecked(false);
                        }
                        switch_layerTopTen.setChecked(isChecked);
                        SettingManager.MapLayer.setTopTenLayer(isChecked);
                        break;

                    case R.id.switch_layer_recommended:
                        if (isChecked) {
                            switch_layerAllOfTaiwan.setChecked(false);
                            switch_layerRentStation.setChecked(false);
                        }
                        switch_layerRecommended.setChecked(isChecked);
                        SettingManager.MapLayer.setRecommendedLayer(isChecked);
                        break;

                    case R.id.switch_layer_all_of_taiwan:
                        switch_layerCycling.setChecked(false);
                        switch_layerTopTen.setChecked(false);
                        switch_layerRecommended.setChecked(false);
                        switch_layerRentStation.setChecked(false);
                        switch_layerAllOfTaiwan.setChecked(isChecked);
                        SettingManager.MapLayer.setAllOfTaiwanLayer(isChecked);
                        break;

                    case R.id.switch_layer_rent_station:
                        switch_layerCycling.setChecked(false);
                        switch_layerTopTen.setChecked(false);
                        switch_layerRecommended.setChecked(false);
                        switch_layerAllOfTaiwan.setChecked(false);
                        switch_layerRentStation.setChecked(isChecked);
                        SettingManager.MapLayer.setRentStationLayer(isChecked);
                        break;
                }
            }
        };

        switch_myPoi.setOnCheckedChangeListener(checkedChangeListener);
        switch_layerCycling.setOnCheckedChangeListener(checkedChangeListener);
        switch_layerTopTen.setOnCheckedChangeListener(checkedChangeListener);
        switch_layerRecommended.setOnCheckedChangeListener(checkedChangeListener);
        switch_layerAllOfTaiwan.setOnCheckedChangeListener(checkedChangeListener);
        switch_layerRentStation.setOnCheckedChangeListener(checkedChangeListener);
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

        onMarkerClick(lastAroundPoiMarker);

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
    public void onMapLongClick(LatLng latLng) {
        putNewMarker(latLng);
    }

    private void putNewMarker(LatLng latLng) {
        MarkerOptions marker = new MarkerOptions();
        marker.position(latLng);
        marker.title(String.valueOf(latLng.latitude + "\n" + latLng.longitude));

        if (ENTRY_TYPE == ENTRY_TYPE_LOCATION_SELECT)
            marker.snippet(getString(R.string.poi_select_this_point));
        else
            marker.snippet(getString(R.string.poi_edit_this_point));

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

    private void editMyPoi(final LatLng latLng, @Nullable String title, @Nullable String address) {
        View view = PopWindowHelper.getPoiEditWindowView(this, mapRootLayout);

        final EditText poiTitle = (EditText) view.findViewById(R.id.edit_poiTitle);
        final EditText poiLocation = (EditText) view.findViewById(R.id.edit_poiLocation);
        final EditText poiContent = (EditText) view.findViewById(R.id.edit_poiContent);
        poiImageView = (ImageView) view.findViewById(R.id.image_poiPhoto);
        Button poiBtnSave = (Button) view.findViewById(R.id.btn_poiSave);
        Button poiBtnCancel = (Button) view.findViewById(R.id.btn_poiCancel);

        if (notNull(title)) {
            poiTitle.setText(title);
            poiTitle.setSelection(title.length());
        }

        if (address == null || address.isEmpty())
            address = String.valueOf(latLng.latitude + ",\n" + latLng.longitude); // If address is null, use LatLng instead.

        poiLocation.setText(address);

        photoPath = "";
        final boolean isPoiExisted = FavoriteHelper.isPoiExisted(latLng.latitude, latLng.longitude);

        poiBtnSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String title = poiTitle.getText().toString();
                String address = poiLocation.getText().toString();
                String content = poiContent.getText().toString();

                if (!title.isEmpty()) {
                    if (isPoiExisted) {
                        FavoriteHelper.updateMyPoi(title, address, content, photoPath);
                        Utility.toastShort(getString(R.string.poi_update_done));
                        PopWindowHelper.dismissPopWindow();

                        reloadMarker(title, content, false);
                    }
                    else {
                        FavoriteHelper.addMyPoi(title, address, content, latLng.latitude, latLng.longitude, photoPath);
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
                poiLocation.setText(poiItem.ADDRESS);
                poiLocation.setSelection(poiItem.ADDRESS.length());
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

            /**
             * 如果正要 Reload的 Marker是搜尋結果打上來的(ic_search_result)，<br>
             * 那就 assign searchMarker to null，<br>
             * 避免下次再做搜尋時，已經 Reload的 Marker會被移除！
             */
            String key = String.valueOf(selectedMarker.getPosition().latitude) + String.valueOf(selectedMarker.getPosition().longitude);
            if (notNull(markerTypeMap) && markerTypeMap.containsKey(key)) {
                if (markerTypeMap.get(key) == R.drawable.ic_search_result)
                    searchMarker = null;
            }

            setMarkerTypeMap(selectedMarker.getPosition().latitude, selectedMarker.getPosition().longitude, R.drawable.ic_my_poi);

            selectedMarker.showInfoWindow();
            onMarkerClick(selectedMarker);

            if (addToList)
                myPoiMarkerList.add(selectedMarker);

            SettingManager.MapLayer.setMyPoiFlag(true);

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
        String title = null;
        /** 判斷 Marker title是經緯度還是一般的名子，
         *  先假設如果 title多於 15個數字的話，就是經緯度！ */
        if (notNull(selectedMarker.getTitle())) {
            if (Utility.getNumericCount(selectedMarker.getTitle()) < 15)
                title = selectedMarker.getTitle();
        }
        editMyPoi(selectedMarker.getPosition(), title, null);
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
            moveCameraAndShowInfoWindowIfIntentHasExtras();
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
                if (SettingManager.MapLayer.getMyPoiFlag())
                    new PutAllMyPoiMarkers().execute();
                else
                    removeAllMyPoiMarkers();
                break;

            default:
                setLayersByPrefKey(key);
                break;
        }
    }

    private void moveCameraAndShowInfoWindowIfIntentHasExtras() {
        Intent intent = getIntent();

        if (intent.hasExtra(BUNDLE_MY_POI_INFO)) {
            ItemsMyPOI poiItem = (ItemsMyPOI) intent.getSerializableExtra(BUNDLE_MY_POI_INFO);

            for (Marker marker : myPoiMarkerList) {
                if (marker.getPosition().latitude == poiItem.LAT && marker.getPosition().longitude == poiItem.LNG) {
                    moveCameraAndZoom(marker.getPosition(), 16);
                    marker.showInfoWindow();
                    onMarkerClick(marker);
                    break;
                }
            }

            intent.removeExtra(BUNDLE_MY_POI_INFO);
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

        DialogHelper.showLoadingDialog(this);

        String origin = location.getLatitude() + "," + location.getLongitude();
        String destination = selectedMarker.getPosition().latitude + "," + selectedMarker.getPosition().longitude;

        String avoidOption = getAvoidOptions(DIR_AVOID_TOLLS, DIR_AVOID_HIGHWAYS);

        WebAgent.getDirectionsData(origin, destination, DIR_MODE_WALKING, avoidOption, new WebAgent.WebResultImplement() {
            @Override
            public void onResultSucceed(String response) {
                getPolyLineAndDrawLine(response);
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
            polyOptions.color(ContextCompat.getColor(AppController.getInstance().getAppContext(), R.color.md_blue_600));
            polyOptions.width(15);

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

            setPathListView(jsonString);
            MenuHelper.setMenuOptionsByMenuAction(menu, ACTION_LIST, ACTION_SWITCH, ACTION_AROUND);
        }
    }

    private void setPathListView(String jsonString) {
        pathListView.setAdapter(new PathListViewAdapter(this, JsonParser.parseAnGetDirectionItems(jsonString)));
    }

    private void showPathInfo() {
        if (pathInfoLayout.getVisibility() == View.GONE) {
            pathInfoLayout.setVisibility(View.VISIBLE);
            pathListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                    showMarkerButtonLayout(false, false);
                    selectedMarker.hideInfoWindow();
                    ItemsPathStep pathStepItem = (ItemsPathStep) parent.getItemAtPosition(position);
                    moveCameraAndDrawHighlight(pathStepItem);
                }
            });
        }
        else {
            pathInfoLayout.setVisibility(View.GONE);
            pathListView.setOnItemClickListener(null);
        }
    }

    private void moveCameraAndDrawHighlight(ItemsPathStep pathStepItem) {
        moveCameraAndZoom(new LatLng(pathStepItem.START_LAT, pathStepItem.START_LNG), 17);

        ArrayList<LatLng> latLngList = PolyHelper.decodePolyLine(pathStepItem.POLY_LINE);

        PolylineOptions polyLine = new PolylineOptions();

        for (LatLng latLng : latLngList) {
            polyLine.add(latLng);
        }
        polyLine.color(ContextCompat.getColor(AppController.getInstance().getAppContext(), R.color.md_deep_purple_A400));
        polyLine.width(18);
        polyLine.zIndex(1000);

        if (highLightPoly != null)
            highLightPoly.remove();

        highLightPoly = map.addPolyline(polyLine);
    }

    private void setLayersByPrefKey(String key) {
        if (handlerThread == null) {
            handlerThread = new HandlerThread(NAME_OF_HANDLER_THREAD);
            handlerThread.start();
        }

        if (layerHandler == null)
            layerHandler = new MapLayerHandler(handlerThread.getLooper(), new Handler(), this);

        switch (key) {
            case SettingManager.PREFS_LAYER_CYCLING_1:
                if (SettingManager.MapLayer.getCyclingLayer()) {
                    layerHandler.addLayer(map, MapLayerHandler.LAYER_CYCLING);
                    showLoadingCircle(true);
                }
                else
                    layerHandler.removeLayer(MapLayerHandler.LAYER_CYCLING);
                break;

            case SettingManager.PREFS_LAYER_TOP_TEN:
                if (SettingManager.MapLayer.getTopTenLayer()) {
                    layerHandler.addLayer(map, MapLayerHandler.LAYER_TOP_TEN);
                    showLoadingCircle(true);
                }
                else
                    layerHandler.removeLayer(MapLayerHandler.LAYER_TOP_TEN);

                break;

            case SettingManager.PREFS_LAYER_RECOMMENDED:
                if (SettingManager.MapLayer.getRecommendedLayer()) {
                    layerHandler.addLayer(map, MapLayerHandler.LAYER_RECOMMENDED);
                    showLoadingCircle(true);
                }
                else
                    layerHandler.removeLayer(MapLayerHandler.LAYER_RECOMMENDED);
                break;

            case SettingManager.PREFS_LAYER_ALL_OF_TAIWAN:
                if (SettingManager.MapLayer.getAllOfTaiwanLayer()) {
                    layerHandler.addLayer(map, MapLayerHandler.LAYER_ALL_OF_TAIWAN);
                    showLoadingCircle(true);
                }
                else
                    layerHandler.removeLayer(MapLayerHandler.LAYER_ALL_OF_TAIWAN);
                break;

            case SettingManager.PREFS_LAYER_RENT_STATION:
                if (SettingManager.MapLayer.getRentStationLayer()) {
                    layerHandler.addLayer(map, MapLayerHandler.LAYER_RENT_STATION);
                    showLoadingCircle(true);
                }
                else
                    layerHandler.removeLayer(MapLayerHandler.LAYER_RENT_STATION);
                break;
        }
    }

    @Override
    public void onPolylinePrepared(final int layerCode, final PolylineOptions polyLine) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                switch (layerCode) {
                    case MapLayerHandler.LAYER_CYCLING:
                        layerHandler.polyLineCyclingList.add(map.addPolyline(polyLine));
                        break;

                    case MapLayerHandler.LAYER_TOP_TEN:
                        layerHandler.polyLineTopTenList.add(map.addPolyline(polyLine));
                        break;

                    case MapLayerHandler.LAYER_RECOMMENDED:
                        layerHandler.polyLineRecommendList.add(map.addPolyline(polyLine));
                        break;

                    case MapLayerHandler.LAYER_ALL_OF_TAIWAN:
                        layerHandler.polyLineTaiwanList.add(map.addPolyline(polyLine));
                        break;
                }

                map.setOnPolylineClickListener(new GoogleMap.OnPolylineClickListener() {
                    @Override
                    public void onPolylineClick(Polyline polyline) {
                        int zIndex;
                        Log.i(TAG, "zIndex: " + polyline.getZIndex() + " LayerCode: " + getLayerCodeByZIndex((int) polyline.getZIndex()));
                        switch (getLayerCodeByZIndex((int) polyline.getZIndex())) {
                            case MapLayerHandler.LAYER_CYCLING:
                                zIndex = (int) (polyline.getZIndex() - MapLayerHandler.LAYER_CYCLING);
                                showPolylineInfo(R.raw.layer_cycling_route_line, zIndex);
                                break;

                            case MapLayerHandler.LAYER_TOP_TEN:
                                zIndex = (int) (polyline.getZIndex() - MapLayerHandler.LAYER_TOP_TEN);
                                showPolylineInfo(R.raw.layer_top10, zIndex);
                                break;

                            case MapLayerHandler.LAYER_RECOMMENDED:
                                zIndex = (int) (polyline.getZIndex() - MapLayerHandler.LAYER_RECOMMENDED);
                                showPolylineInfo(R.raw.layer_recommend, zIndex);
                                break;

                            case MapLayerHandler.LAYER_ALL_OF_TAIWAN:
                                zIndex = (int) (polyline.getZIndex() - MapLayerHandler.LAYER_ALL_OF_TAIWAN);
                                showPolylineInfo(R.raw.layer_biking_route_taiwan, zIndex);
                                break;
                        }

                    }
                });
            }
        });
    }

    private int getLayerCodeByZIndex(int zIndex) {
        if (zIndex - MapLayerHandler.LAYER_CYCLING < 100 && zIndex - MapLayerHandler.LAYER_CYCLING >= 0)
            return MapLayerHandler.LAYER_CYCLING;

        else if (zIndex - MapLayerHandler.LAYER_TOP_TEN < 100 && zIndex - MapLayerHandler.LAYER_TOP_TEN >= 0)
            return MapLayerHandler.LAYER_TOP_TEN;

        else if (zIndex - MapLayerHandler.LAYER_RECOMMENDED < 100 && zIndex - MapLayerHandler.LAYER_RECOMMENDED >= 0)
            return MapLayerHandler.LAYER_RECOMMENDED;

        else if (zIndex - MapLayerHandler.LAYER_ALL_OF_TAIWAN >= 0)
            return MapLayerHandler.LAYER_ALL_OF_TAIWAN;

        else
            return 0;
    }

    private void showPolylineInfo(int geoJsonData, final int zIndex) {
        showLoadingCircle(true);

        layerHandler.getLayerProperties(geoJsonData, zIndex);
    }

    @Override
    public void onPolylineClick(String name, String location, String description) {
        polylineInfoLayout.setVisibility(View.VISIBLE);

        polylineName.setText(name);

        if (!location.isEmpty()) {
            polylineLocation.setVisibility(View.VISIBLE);
            polylineLocation.setText(location);
        }
        else
            polylineLocation.setVisibility(View.GONE);

        if (!description.isEmpty()) {
            polylineDescription.setVisibility(View.VISIBLE);
            polylineDescription.setText(description);
        }
        else
            polylineDescription.setVisibility(View.GONE);
    }

    @Override
    public void onLayerAdded(int layerCode) {
        showLoadingCircle(false);
    }

    @Override
    public void onLayersAllGone() {
        if (notNull(handlerThread)) {
            handlerThread.quit();
            handlerThread.interrupt();
            handlerThread = null;
        }
        if (notNull(layerHandler))
            layerHandler = null;

        map.setOnPolylineClickListener(null);
        polylineInfoLayout.setVisibility(View.GONE);
    }

    private void closeAllLayerFlag() {
        SettingManager.MapLayer.setCyclingLayer(false);
        SettingManager.MapLayer.setTopTenLayer(false);
        SettingManager.MapLayer.setRecommendedLayer(false);
        SettingManager.MapLayer.setAllOfTaiwanLayer(false);
        SettingManager.MapLayer.setRentStationLayer(false);
    }
}