package com.kingwaytek.cpami.bykingTablet.app.ui;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.graphics.BitmapFactory;
import android.location.Location;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
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
import com.google.android.gms.location.places.ui.PlacePicker;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.BitmapDescriptor;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.kingwaytek.cpami.bykingTablet.R;
import com.kingwaytek.cpami.bykingTablet.app.model.DataArray;
import com.kingwaytek.cpami.bykingTablet.app.model.ItemsMyPOI;
import com.kingwaytek.cpami.bykingTablet.utilities.FavoriteHelper;
import com.kingwaytek.cpami.bykingTablet.utilities.PopWindowHelper;
import com.kingwaytek.cpami.bykingTablet.utilities.SettingManager;
import com.kingwaytek.cpami.bykingTablet.utilities.Utility;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;

/**
 * 地址查詢、關鍵字查詢、周邊景點 and blah blah blah~<br>
 * 都可以在這裡做！
 *
 * @author Vincent (2016/4/15).
 */
public class UiPoiSearchMapActivity extends BaseGoogleApiActivity implements TextWatcher, GoogleMap.OnMapLongClickListener {

    private static final int REQUEST_PLACE_PICKER = 1;
    private static final int REQUEST_POI_PHOTO = 10;
    private static final int REQUEST_POI_PHOTO_M = 11;

    private boolean isFirstTimeLaunch = true;

    private Marker lastMarker;
    private HashMap<LatLng, Integer> markerTypeMap;
    private ArrayList<Marker> myPoiMarkerList;

    private ImageView poiImageView;
    private String photoPath;

    @Override
    protected void onApiReady() {
        showRightButtons(true);
        putMarkersAndSetListener();
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

        if (isFirstTimeLaunch) {
            registerPreferenceChangedListener();
            markerTypeMap = new HashMap<>();

            if (SettingManager.MarkerFlag.getMyPoiFlag())
                new PutAllMyPoiMarkers().execute();

            isFirstTimeLaunch = false;
        }
    }

    private void setMarkerTypeMap(double lat, double lng, int iconResId) {

    }

    @Override
    public View getInfoWindow(Marker marker) {
        if (notNull(marker.getSnippet())) {
            View view = getLayoutInflater().inflate(R.layout.inflate_marker_poi_info_window, null);

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
        if (markerTypeMap.containsKey(marker.getPosition())) {
            switch (markerTypeMap.get(marker.getPosition())) {
                case R.drawable.ic_my_poi:
                    editMyPoi(marker.getPosition());
                    break;
            }
        }
    }

    @Override
    protected void onSwitchButtonClick() {
        View view = PopWindowHelper.getMarkerSwitchWindowView(mapLayout);

        Switch switch_myPoi = (Switch) view.findViewById(R.id.switch_my_poi);

        switch_myPoi.setChecked(SettingManager.MarkerFlag.getMyPoiFlag());

        switch_myPoi.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                SettingManager.MarkerFlag.setMyPoiFlag(isChecked);
            }
        });
    }

    @Override
    protected void onAroundButtonClick() {
        try {
            PlacePicker.IntentBuilder pickerBuilder = new PlacePicker.IntentBuilder();
            startActivityForResult(pickerBuilder.build(this), REQUEST_PLACE_PICKER);
        }
        catch (GooglePlayServicesNotAvailableException | GooglePlayServicesRepairableException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == RESULT_OK) {
            switch (requestCode) {
                case REQUEST_PLACE_PICKER:
                    Place place = PlacePicker.getPlace(this, data);
                    putMarkerAndMoveCamera(place);
                    break;

                case REQUEST_POI_PHOTO:
                case REQUEST_POI_PHOTO_M:
                    getPhotoPathAndSetImageView(requestCode, data);
                    break;
            }
        }
    }

    private void putMarkerAndMoveCamera(Place place) {
        Utility.toastShort(place.getName().toString());
        moveCameraAndZoom(place.getLatLng(), 17);

        if (notNull(lastMarker))
            lastMarker.remove();

        MarkerOptions marker = new MarkerOptions();

        marker.position(place.getLatLng());
        marker.title(place.getName().toString());
        marker.snippet(place.getAddress().toString());

        InputStream is = getResources().openRawResource(+R.drawable.ic_around_poi);
        marker.icon(BitmapDescriptorFactory.fromBitmap(BitmapFactory.decodeStream(is)));

        markerTypeMap.put(place.getLatLng(), R.drawable.ic_around_poi);

        lastMarker = map.addMarker(marker);

        if (notNull(lastMarker))
            lastMarker.showInfoWindow();

        closeInputStream(is);
    }

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
        putMarker(latLng);
    }

    private void putMarker(LatLng latLng) {
        MarkerOptions marker = new MarkerOptions();
        marker.position(latLng);
        marker.title("選擇點位：");
        marker.snippet(String.valueOf(latLng.latitude + "\n" + latLng.longitude));

        InputStream is = getResources().openRawResource(+R.drawable.ic_my_poi);
        marker.icon(BitmapDescriptorFactory.fromBitmap(BitmapFactory.decodeStream(is)));

        markerTypeMap.put(latLng, R.drawable.ic_my_poi);

        map.addMarker(marker).showInfoWindow();
        moveCameraAndZoom(latLng, 16);

        closeInputStream(is);
    }

    private void editMyPoi(final LatLng latLng) {
        View view = PopWindowHelper.getPoiEditWindowView(this, mapLayout);

        final EditText poiTitle = (EditText) view.findViewById(R.id.edit_poiTitle);
        final EditText poiContent = (EditText) view.findViewById(R.id.edit_poiContent);
        poiImageView = (ImageView) view.findViewById(R.id.image_poiPhoto);
        TextView poiLatLng = (TextView) view.findViewById(R.id.text_poiLatLng);
        Button poiBtnSave = (Button) view.findViewById(R.id.btn_poiSave);
        Button poiBtnCancel = (Button) view.findViewById(R.id.btn_poiCancel);

        String poiLocation = String.valueOf("\n" + latLng.latitude + ",\n" + latLng.longitude);
        poiLatLng.setText(getString(R.string.poi_lat_lng, poiLocation));

        poiImageView.setOnClickListener(getClickListener());

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
                    }
                    else {
                        FavoriteHelper.addMyPoi(title, content, latLng.latitude, latLng.longitude, photoPath);
                        Utility.toastShort(getString(R.string.poi_save_done));
                        PopWindowHelper.dismissPopWindow();
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
            ItemsMyPOI poiItem = FavoriteHelper.getMyPoiItem();
            if (notNull(poiItem)) {
                poiTitle.setText(poiItem.TITLE);
                poiContent.setText(poiItem.DESCRIPTION);

                Log.i(TAG, "PhotoPathFromFavorite: " + poiItem.PHOTO_PATH);

                if (!poiItem.PHOTO_PATH.isEmpty())
                    setPoiImageView(poiItem.PHOTO_PATH);
            }
        }
    }

    private View.OnClickListener getClickListener() {
        return new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (Build.VERSION.SDK_INT < 19) {
                    Intent intent = new Intent();
                    intent.setType("image/*");
                    intent.setAction(Intent.ACTION_GET_CONTENT);
                    startActivityForResult(intent, REQUEST_POI_PHOTO);
                }
                else {
                    Intent intent = new Intent(Intent.ACTION_OPEN_DOCUMENT);
                    intent.addCategory(Intent.CATEGORY_OPENABLE);
                    intent.setType("image/*");
                    startActivityForResult(intent, REQUEST_POI_PHOTO_M);
                }
            }
        };
    }

    @SuppressWarnings("WrongConstant")
    @SuppressLint("NewApi")
    private void getPhotoPathAndSetImageView(int requestCode, Intent data) {
        Uri uri = data.getData();

        photoPath = uri.toString();
        Log.i(TAG, "ImageContentPath: " + photoPath);

        switch (requestCode) {
            case REQUEST_POI_PHOTO:
                Cursor cursor = getContentResolver().query(uri, null, null, null, null);

                if (notNull(cursor)) {
                    cursor.moveToFirst();
                    int columnIndex = cursor.getColumnIndex(MediaStore.Images.ImageColumns.DATA);
                    photoPath = cursor.getString(columnIndex);

                    Log.i(TAG, "ImageFilePath: " + photoPath);
                    cursor.close();
                }
                else {
                    photoPath = uri.getPath();
                    Log.i(TAG, "CursorNull ImagePath: " + photoPath);
                }

                setPoiImageView(photoPath);

                break;

            case REQUEST_POI_PHOTO_M:
                final int takeFlags = data.getFlags() & (Intent.FLAG_GRANT_READ_URI_PERMISSION | Intent.FLAG_GRANT_WRITE_URI_PERMISSION);
                getContentResolver().takePersistableUriPermission(uri, takeFlags);

                Log.i(TAG, "UriLastPathSegment: " + uri.getLastPathSegment());
                final String id = uri.getLastPathSegment().split(":")[1];
                final String[] imageColumns = {MediaStore.Images.Media.DATA};
                final String imageOrderBy = null;

                Uri storageUri = getStorageUri();

                Cursor imageCursor = getContentResolver().query(storageUri, imageColumns, MediaStore.Images.Media._ID + "="+id, null, imageOrderBy);

                if (notNull(imageCursor) && imageCursor.moveToFirst()) {
                    photoPath = imageCursor.getString(imageCursor.getColumnIndex(MediaStore.Images.Media.DATA));
                    Log.i(TAG, "ImageFilePath: " + photoPath);

                    setPoiImageView(photoPath);

                    imageCursor.close();
                }

                break;
        }
    }

    private Uri getStorageUri() {
        String state = Environment.getExternalStorageState();

        if (state.equalsIgnoreCase(Environment.MEDIA_MOUNTED))
            return MediaStore.Images.Media.EXTERNAL_CONTENT_URI;
        else
            return MediaStore.Images.Media.INTERNAL_CONTENT_URI;
    }

    private void setPoiImageView(String photoPath) {
        int reqSize = getResources().getDimensionPixelSize(R.dimen.poi_photo_edit_view);
        poiImageView.setImageBitmap(Utility.getDecodedBitmap(photoPath, reqSize, reqSize));
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
                }
            }
            else
                Utility.showToastOnNewThread(getString(R.string.poi_empty));

            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            showLoadingCircle(false);
        }
    }

    private void removeAllMyPoiMarkers() {
        showLoadingCircle(true);

        if (notNull(myPoiMarkerList) && !myPoiMarkerList.isEmpty()) {
            for (Marker marker : myPoiMarkerList) {
                marker.remove();
            }
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
}
