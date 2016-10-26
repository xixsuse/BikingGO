package com.kingwaytek.cpami.biking.app.ui;

import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.Drawable;
import android.location.Location;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.TabLayout;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.GravityCompat;
import android.support.v4.view.ViewPager;
import android.text.TextWatcher;
import android.util.Log;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
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
import com.kingwaytek.cpami.biking.AppController;
import com.kingwaytek.cpami.biking.R;
import com.kingwaytek.cpami.biking.app.model.DataArray;
import com.kingwaytek.cpami.biking.app.model.items.ItemsMyPOI;
import com.kingwaytek.cpami.biking.app.model.items.ItemsPathStep;
import com.kingwaytek.cpami.biking.app.ui.poi.UiMyPoiInfoActivity;
import com.kingwaytek.cpami.biking.app.web.WebAgent;
import com.kingwaytek.cpami.biking.callbacks.OnPhotoRemovedCallBack;
import com.kingwaytek.cpami.biking.hardware.MyLocationManager;
import com.kingwaytek.cpami.biking.utilities.BitmapUtility;
import com.kingwaytek.cpami.biking.utilities.DialogHelper;
import com.kingwaytek.cpami.biking.utilities.FavoriteHelper;
import com.kingwaytek.cpami.biking.utilities.ImageSelectHelper;
import com.kingwaytek.cpami.biking.utilities.JsonParser;
import com.kingwaytek.cpami.biking.utilities.PolyHelper;
import com.kingwaytek.cpami.biking.utilities.PopWindowHelper;
import com.kingwaytek.cpami.biking.utilities.SettingManager;
import com.kingwaytek.cpami.biking.utilities.Utility;
import com.kingwaytek.cpami.biking.utilities.adapter.DirectionModePagerAdapter;

import java.io.InputStream;
import java.text.MessageFormat;
import java.util.ArrayList;

/**
 * 地址查詢、關鍵字查詢、周邊景點 and blah blah blah~<br>
 * 都可以在這裡做！
 *
 * @author Vincent (2016/4/15).
 */
public class UiMainMapActivity extends BaseGoogleApiActivity implements TextWatcher, GoogleMap.OnMapLongClickListener,
        OnPhotoRemovedCallBack, View.OnTouchListener, TabLayout.OnTabSelectedListener {

    private boolean isFirstTimeRun = true;  //每次startActivity過來這個值都會被重設，除非設為static

    private Marker myNewMarker;
    private Marker lastAroundPoiMarker;
    private Marker poiBookMarker;

    private Marker myPositionMarker;
    private Polyline polyLine;
    private String polylineOverview_walk;
    private String polylineOverview_transit;

    private ArrayList<Marker> myPoiMarkerList;

    private ImageView poiImageView;
    private String photoPath;

    private DirectionModePagerAdapter pagerAdapter;
    private LinearLayout pathInfoLayout;
    private TabLayout modeTab;
    private ViewPager pathListPager;
    private Polyline highLightPoly;

    private ImageView footerImage;
    private static final int FOOTER_TAG_BACKGROUND_LIGHT = 0;
    private static final int FOOTER_TAG_BACKGROUND_DARK = 1;

    private final int screenWidth = Utility.getScreenWidth();
    private final int screenHeight = Utility.getScreenHeight();
    private int pathInfoLayoutMaxHeight;

    private static long exitTime = 0;

    @Override
    protected void onApiReady() {
        Log.i(TAG, "onApiReady!!!");
        checkIntentAndDoActions();
        registerPreferenceChangedListener();
    }

    @Override
    protected String getActionBarTitle() {
        return getString(R.string.app_name);
    }

    @Override
    protected void findViews() {
        super.findViews();
        pathInfoLayout = (LinearLayout) findViewById(R.id.pathInfoLayout);
        modeTab = (TabLayout) findViewById(R.id.directionModeTabLayout);
        pathListPager = (ViewPager) findViewById(R.id.pathListPager);

        footerImage = (ImageView) findViewById(R.id.footerImage);
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
        if (ENTRY_TYPE == ENTRY_TYPE_DEFAULT)
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

            if (getIntent().hasExtra(BUNDLE_DIRECTION_FROM_POI_BOOK)) {
                putMarkerFromPoiBook();

                String[] jsonStringAndFromTo = getIntent().getStringArrayExtra(BUNDLE_DIRECTION_FROM_POI_BOOK);
                getPolylineAndDrawLine(jsonStringAndFromTo[0], jsonStringAndFromTo[1]);
            }

            if (SettingManager.getAppFirstLaunch()) {
                getUiHandler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        drawer.openDrawer(GravityCompat.START);
                        SettingManager.setAppFirstLaunch(false);
                    }
                }, 1500);
            }

            isFirstTimeRun = false;
        }
    }

    @Override
    public boolean onMarkerClick(Marker marker) {
        if (ENTRY_TYPE == ENTRY_TYPE_DEFAULT) {
            selectedMarker = marker;

            String key = String.valueOf(marker.getPosition().latitude) + String.valueOf(marker.getPosition().longitude);

            if (markerTypeMap.containsKey(key) && (
                    markerTypeMap.get(key) == R.drawable.ic_marker_you_bike_normal || markerTypeMap.get(key) == R.drawable.ic_pin_place)) {
                showMarkerButtonLayout(false, false);
            }
            else {
                if (FavoriteHelper.isPoiExisted(marker.getPosition().latitude, marker.getPosition().longitude))
                    showMarkerButtonLayout(true, true);
                else
                    showMarkerButtonLayout(true, false);
            }
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
                    case R.drawable.ic_marker_my_poi:
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
                        case R.drawable.ic_marker_end:
                            //editMyPoi(marker.getPosition(), null, null);
                            break;

                        case R.drawable.ic_marker_my_poi:
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

                        case R.drawable.ic_marker_search_result:
                        case R.drawable.ic_marker_around:
                            //editMyPoi(marker.getPosition(), marker.getTitle(), marker.getSnippet());
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

            Bitmap bitmap = getBitmapFromMemCache(photoPath);

            if (bitmap == null) {
                bitmap = BitmapUtility.getDecodedBitmap(photoPath, imgSize, imgSize);
                addBitmapToMemoryCache(photoPath, bitmap);
                markerPoiPhoto.setImageBitmap(bitmap);
            }
            else
                markerPoiPhoto.setImageBitmap(bitmap);
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
        View view = PopWindowHelper.getMarkerSwitchWindowView(mapRootLayout, false);

        final Switch switch_myPoi = (Switch) view.findViewById(R.id.switch_my_poi);
        final Switch switch_layerCycling = (Switch) view.findViewById(R.id.switch_layer_cycling_1);
        final Switch switch_layerTopTen = (Switch) view.findViewById(R.id.switch_layer_top_ten);
        final Switch switch_layerRecommended = (Switch) view.findViewById(R.id.switch_layer_recommended);
        final Switch switch_layerAllOfTaiwan = (Switch) view.findViewById(R.id.switch_layer_all_of_taiwan);
        final Switch switch_layerRentStation = (Switch) view.findViewById(R.id.switch_layer_rent_station);
        final Switch switch_layerYouBike = (Switch) view.findViewById(R.id.switch_layer_you_bike);

        final ImageButton closeBtn = (ImageButton) view.findViewById(R.id.switchWindowCloseBtn);

        switch_myPoi.setChecked(SettingManager.MapLayer.getMyPoiFlag());
        switch_layerCycling.setChecked(SettingManager.MapLayer.getCyclingLayer());
        switch_layerTopTen.setChecked(SettingManager.MapLayer.getTopTenLayer());
        switch_layerRecommended.setChecked(SettingManager.MapLayer.getRecommendedLayer());
        switch_layerAllOfTaiwan.setChecked(SettingManager.MapLayer.getAllOfTaiwanLayer());
        switch_layerRentStation.setChecked(SettingManager.MapLayer.getRentStationLayer());
        switch_layerYouBike.setChecked(SettingManager.MapLayer.getYouBikeLayer());

        switch_myPoi.setTag(switch_myPoi.getId());
        switch_layerCycling.setTag(switch_layerCycling.getId());
        switch_layerTopTen.setTag(switch_layerTopTen.getId());
        switch_layerRecommended.setTag(switch_layerRecommended.getId());
        switch_layerAllOfTaiwan.setTag(switch_layerAllOfTaiwan.getId());
        switch_layerRentStation.setTag(switch_layerRentStation.getId());
        switch_layerYouBike.setTag(switch_layerYouBike.getId());

        CompoundButton.OnCheckedChangeListener checkedChangeListener = new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                boolean checked = isLayerChanging() != isChecked;

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
                        switch_layerCycling.setChecked(checked);
                        SettingManager.MapLayer.setCyclingLayer(checked);
                        break;

                    case R.id.switch_layer_top_ten:
                        if (isChecked) {
                            switch_layerAllOfTaiwan.setChecked(false);
                            switch_layerRentStation.setChecked(false);
                        }
                        switch_layerTopTen.setChecked(checked);
                        SettingManager.MapLayer.setTopTenLayer(checked);
                        break;

                    case R.id.switch_layer_recommended:
                        if (isChecked) {
                            switch_layerAllOfTaiwan.setChecked(false);
                            switch_layerRentStation.setChecked(false);
                        }
                        switch_layerRecommended.setChecked(checked);
                        SettingManager.MapLayer.setRecommendedLayer(checked);
                        break;

                    case R.id.switch_layer_all_of_taiwan:
                        if (checked) {
                            switch_layerCycling.setChecked(false);
                            switch_layerTopTen.setChecked(false);
                            switch_layerRecommended.setChecked(false);
                            switch_layerRentStation.setChecked(false);
                            switch_layerYouBike.setChecked(false);
                        }
                        switch_layerAllOfTaiwan.setChecked(checked);
                        SettingManager.MapLayer.setAllOfTaiwanLayer(checked);
                        break;

                    case R.id.switch_layer_rent_station:
                        if (checked) {
                            switch_layerCycling.setChecked(false);
                            switch_layerTopTen.setChecked(false);
                            switch_layerRecommended.setChecked(false);
                            switch_layerAllOfTaiwan.setChecked(false);
                            switch_layerYouBike.setChecked(false);
                        }
                        switch_layerRentStation.setChecked(checked);
                        SettingManager.MapLayer.setRentStationLayer(checked);
                        break;

                    case R.id.switch_layer_you_bike:
                        if (checked) {
                            switch_layerAllOfTaiwan.setChecked(false);
                            switch_layerRentStation.setChecked(false);
                        }
                        switch_layerYouBike.setChecked(checked);
                        SettingManager.MapLayer.setYouBikeLayer(checked);
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
        switch_layerYouBike.setOnCheckedChangeListener(checkedChangeListener);

        closeBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                PopWindowHelper.dismissPopWindow();

                switch_myPoi.setOnCheckedChangeListener(null);
                switch_layerCycling.setOnCheckedChangeListener(null);
                switch_layerTopTen.setOnCheckedChangeListener(null);
                switch_layerRecommended.setOnCheckedChangeListener(null);
                switch_layerAllOfTaiwan.setOnCheckedChangeListener(null);
                switch_layerRentStation.setOnCheckedChangeListener(null);
                switch_layerYouBike.setOnCheckedChangeListener(null);
                closeBtn.setOnClickListener(null);
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

                case REQUEST_RELOAD_ALL_MARKER:
                    new PutAllMyPoiMarkers().execute();
                    break;
            }
        }
    }

    private void putMarkerAndMoveCameraByPlace(Place place) {
        Utility.toastShort(place.getName().toString());
        moveCameraAndZoom(place.getLatLng(), 18);

        if (notNull(lastAroundPoiMarker))
            lastAroundPoiMarker.remove();

        MarkerOptions marker = new MarkerOptions();

        marker.position(place.getLatLng());
        marker.title(place.getName().toString());
        marker.snippet(place.getAddress().toString());

        Bitmap bitmap = getBitmapFromMemCache(BITMAP_KEY_AROUND_POI);
        if (bitmap == null) {
            Drawable drawable = ContextCompat.getDrawable(getApplicationContext(), R.drawable.ic_marker_around);
            bitmap = BitmapUtility.convertDrawableToBitmap(drawable, getResources().getDimensionPixelSize(R.dimen.icon_marker_common_size));
            addBitmapToMemoryCache(BITMAP_KEY_AROUND_POI, bitmap);
        }
        //InputStream is = getResources().openRawResource(+R.drawable.ic_around_poi);
        //marker.icon(BitmapDescriptorFactory.fromBitmap(BitmapFactory.decodeStream(is)));
        marker.icon(BitmapDescriptorFactory.fromBitmap(bitmap));

        setMarkerTypeMap(place.getLatLng().latitude, place.getLatLng().longitude, R.drawable.ic_marker_around);

        lastAroundPoiMarker = map.addMarker(marker);

        onMarkerClick(lastAroundPoiMarker);

        if (notNull(lastAroundPoiMarker))
            lastAroundPoiMarker.showInfoWindow();

        //closeInputStream(is);
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

        Bitmap markerBitmap = getBitmapFromMemCache(BITMAP_KEY_END_POINT);

        if (markerBitmap == null) {
            InputStream is = getResources().openRawResource(+R.drawable.ic_marker_end);
            markerBitmap = BitmapFactory.decodeStream(is);
            addBitmapToMemoryCache(BITMAP_KEY_END_POINT, markerBitmap);
            closeInputStream(is);
        }
        marker.icon(BitmapDescriptorFactory.fromBitmap(markerBitmap));

        setMarkerTypeMap(latLng.latitude, latLng.longitude, R.drawable.ic_marker_end);

        if (notNull(myNewMarker))
            myNewMarker.remove();

        myNewMarker = map.addMarker(marker);
        myNewMarker.showInfoWindow();

        if (map.getCameraPosition().zoom < 16)
            moveCameraAndZoom(latLng, 16);
        else
            moveCamera(latLng);

        onMarkerClick(myNewMarker);
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
                Utility.toastShort(getString(R.string.poi_remove_done));

                showMarkerButtonLayout(false, false);
                break;
            }
        }
    }

    private void reloadMarker(String title, String snippet, boolean addToList) {
        if (notNull(selectedMarker)) {
            selectedMarker.setTitle(title);
            selectedMarker.setSnippet(snippet);

            Bitmap bitmap = getBitmapFromMemCache(BITMAP_KEY_MY_POI);

            if (bitmap == null) {
                InputStream is = this.getResources().openRawResource(+R.drawable.ic_marker_my_poi);
                bitmap = BitmapFactory.decodeStream(is);
                addBitmapToMemoryCache(BITMAP_KEY_MY_POI, bitmap);
                closeInputStream(is);
            }
            selectedMarker.setIcon(BitmapDescriptorFactory.fromBitmap(bitmap));

            /**
             * 如果正要 Reload的 Marker是搜尋結果打上來的(ic_search_result)，<br>
             * 那就 assign searchMarker to null，<br>
             * 避免下次再做搜尋時，已經 Reload的 Marker會被移除！
             */
            String key = String.valueOf(selectedMarker.getPosition().latitude) + String.valueOf(selectedMarker.getPosition().longitude);
            if (notNull(markerTypeMap) && markerTypeMap.containsKey(key)) {
                if (markerTypeMap.get(key) == R.drawable.ic_marker_search_result)
                    searchMarker = null;
            }

            setMarkerTypeMap(selectedMarker.getPosition().latitude, selectedMarker.getPosition().longitude, R.drawable.ic_marker_my_poi);

            selectedMarker.showInfoWindow();
            onMarkerClick(selectedMarker);

            if (addToList)
                myPoiMarkerList.add(selectedMarker);

            SettingManager.MapLayer.setMyPoiFlag(true);

            Log.i(TAG, "PoiListSize: " + myPoiMarkerList.size());

            /**
             * 如果 Reloaded marker是來自新增的 marker (myNewMarker) 的話，<br>
             * 那麼此時 myNewMarker & selectedMarker都會是等於 myPoiMarker，<br>
             * 那就要 assign myNewMarker to null，<br>
             * 避免下次 putNewMarker時，現在新增的 myPoiMarker也會被移除！
             */
            if (notNull(myNewMarker) &&
                    myNewMarker.getPosition().latitude == selectedMarker.getPosition().latitude &&
                    myNewMarker.getPosition().longitude == selectedMarker.getPosition().longitude)
            {
                myNewMarker = null;
            }
        }
    }

    private void getPhotoPathAndSetImageView(int requestCode, Intent data) {
        photoPath = ImageSelectHelper.getPhotoPath(this, requestCode, data);
        setPoiImageView(photoPath);
        setImageClickListener();
    }

    private void setPoiImageView(String photoPath) {
        int reqSize = getResources().getDimensionPixelSize(R.dimen.poi_photo_edit_view);

        Bitmap bitmap = getBitmapFromMemCache(photoPath);

        if (bitmap == null) {
            bitmap = BitmapUtility.getDecodedBitmap(photoPath, reqSize, reqSize);
            addBitmapToMemoryCache(photoPath, bitmap);
            poiImageView.setImageBitmap(bitmap);
        }
        else
            poiImageView.setImageBitmap(bitmap);
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
        String address = null;

        /** 判斷 Marker title是經緯度還是一般的名子，
         *  先假設如果 title多於 15個數字的話，就是經緯度！ */
        if (notNull(selectedMarker.getTitle())) {
            if (Utility.getNumericCount(selectedMarker.getTitle()) < 15)
                title = selectedMarker.getTitle();
        }

        if (notNull(selectedMarker.getSnippet())) {
            if (!selectedMarker.getSnippet().equals(getString(R.string.poi_edit_this_point)) &&
                    !selectedMarker.getSnippet().equals(getString(R.string.poi_select_this_point)))
                address = selectedMarker.getSnippet();
        }

        editMyPoi(selectedMarker.getPosition(), title, address);
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

            if (notNull(myPoiList) && !myPoiList.isEmpty()) {
                Bitmap bitmap = getBitmapFromMemCache(BITMAP_KEY_MY_POI);

                if (bitmap == null) {
                    InputStream is = getResources().openRawResource(+R.drawable.ic_marker_my_poi);
                    bitmap = BitmapFactory.decodeStream(is);
                    closeInputStream(is);
                    addBitmapToMemoryCache(BITMAP_KEY_MY_POI, bitmap);
                }
                BitmapDescriptor iconBitmap = BitmapDescriptorFactory.fromBitmap(bitmap);

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

                    setMarkerTypeMap(poiItem.LAT, poiItem.LNG, R.drawable.ic_marker_my_poi);
                }
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

        final String currentOrigin = location.getLatitude() + "," + location.getLongitude();
        final String currentDestination = selectedMarker.getPosition().latitude + "," + selectedMarker.getPosition().longitude;

        String avoidOption = getAvoidOptions(DIR_AVOID_TOLLS, DIR_AVOID_HIGHWAYS);

        WebAgent.getDirectionsData(currentOrigin, currentDestination, DIR_MODE_WALKING, avoidOption, new WebAgent.WebResultImplement() {
            @Override
            public void onResultSucceed(String response) {
                getPolylineAndDrawLine(response, currentOrigin + "&" + currentDestination);
            }

            @Override
            public void onResultFail(String errorMessage) {
                Utility.toastShort(errorMessage);
            }
        });
    }

    private void getPolylineAndDrawLine(String jsonString, String fromTo) {
        String polyOverview = JsonParser.getPolyLineOverview(jsonString);

        if (notNull(polyOverview)) {
            PopWindowHelper.dismissPopWindow();

            if (notNull(myNewMarker))
                myNewMarker.hideInfoWindow();
            if (notNull(selectedMarker))
                selectedMarker.hideInfoWindow();

            showMarkerButtonLayout(false, false);

            footerImage.setOnTouchListener(this);
            footerImage.setImageResource(R.drawable.ic_drag_sort);
            footerImage.setTag(FOOTER_TAG_BACKGROUND_LIGHT);
            footerImage.setBackgroundResource(R.drawable.background_footer_gradient_light);
            footerImage.setScaleType(ImageView.ScaleType.FIT_CENTER);

            openPathInfoLayout(jsonString, fromTo);

            setPolylineOverviewAndDraw(polyOverview, true);
        }
    }

    public void setPolylineOverviewAndDraw(String polylineOverview, boolean isWalking) {
        if (isWalking)
            polylineOverview_walk = polylineOverview;
        else
            polylineOverview_transit = polylineOverview;

        if (modeTab.getTabCount() > 0)
            drawPolyline(modeTab.getSelectedTabPosition() == 0);
    }

    private void drawPolyline(boolean isWalking) {
        PolylineOptions polyOptions = new PolylineOptions();
        ArrayList<LatLng> linePoints = new ArrayList<>();

        if (isWalking && notNull(polylineOverview_walk)) {
            linePoints = PolyHelper.decodePolyLine(polylineOverview_walk);

            for (LatLng latLng : linePoints) {
                polyOptions.add(latLng);
            }
            polyOptions.color(ContextCompat.getColor(AppController.getInstance().getAppContext(), R.color.md_grey_700));
            polyOptions.width(15);
        }
        else if (!isWalking && notNull(polylineOverview_transit)) {
            linePoints = PolyHelper.decodePolyLine(polylineOverview_transit);

            for (LatLng latLng : linePoints) {
                polyOptions.add(latLng);
            }
            polyOptions.color(ContextCompat.getColor(AppController.getInstance().getAppContext(), R.color.md_purple_A200));
            polyOptions.width(18);
        }

        if (notNull(highLightPoly))
            highLightPoly.remove();

        if (notNull(polyLine))
            polyLine.remove();

        polyLine = map.addPolyline(polyOptions);

        if (notNull(myPositionMarker))
            myPositionMarker.remove();

        if (!linePoints.isEmpty()) {
            MarkerOptions marker = new MarkerOptions();

            marker.position(linePoints.get(0));

            Bitmap markerBitmap = getBitmapFromMemCache(BITMAP_KEY_START_POINT);

            if (markerBitmap == null) {
                InputStream is = getResources().openRawResource(+ R.drawable.ic_marker_start);
                markerBitmap = BitmapFactory.decodeStream(is);
                addBitmapToMemoryCache(BITMAP_KEY_START_POINT, markerBitmap);
                closeInputStream(is);
            }
            marker.icon(BitmapDescriptorFactory.fromBitmap(markerBitmap));

            myPositionMarker = map.addMarker(marker);

            if (getIntent().hasExtra(BUNDLE_DIRECTION_FROM_POI_BOOK))
                zoomToFitsBetweenTwoMarkers(linePoints.get(0));
            else
                moveCamera(linePoints.get(0));
        }
    }

    private void openPathInfoLayout(String jsonString, String fromTo) {
        showPathInfoLayout(true);

        if (pagerAdapter == null) {
            pagerAdapter = new DirectionModePagerAdapter(getSupportFragmentManager(), jsonString, fromTo);
            pathListPager.setAdapter(pagerAdapter);
        }
        else {
            pagerAdapter.getDirectionFragmentInstance(0).updateData(jsonString);
            pagerAdapter.getDirectionFragmentInstance(1).updateData(fromTo);
        }

        if (modeTab.getTabCount() == 0) {
            modeTab.setupWithViewPager(pathListPager);
            modeTab.setTabMode(TabLayout.MODE_FIXED);
            modeTab.setTabGravity(TabLayout.GRAVITY_FILL);

            modeTab.getTabAt(0).setIcon(R.drawable.ic_directions_walk);
            modeTab.getTabAt(1).setIcon(R.drawable.ic_directions_transit);

            modeTab.setOnTabSelectedListener(this);
        }

        final int height;

        if (getIntent().hasExtra(BUNDLE_DIRECTION_FROM_POI_BOOK)) {
            height = (int) (screenHeight * 0.5);
            pathListPager.setCurrentItem(1);
            setSearchTextTransparent(true);
        }
        else {
            height = (int) (screenHeight * 0.4);
            setSearchTextTransparent(false);
        }

        final LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, height);
        pathInfoLayout.setLayoutParams(params);

        setPathInfoLayoutMaxHeight();
    }

    private void showPathInfoLayout(boolean isShow) {
        pathInfoLayout.setVisibility(isShow ? View.VISIBLE : View.GONE);
        if (!isShow)
            setSearchTextTransparent(false);
    }

    @Override
    public void onTabSelected(TabLayout.Tab tab) {
        Log.i(TAG, "TabSelected!!! " + tab.getPosition());
        drawPolyline(tab.getPosition() == 0);
        pathListPager.setCurrentItem(tab.getPosition(), true);
    }

    @Override
    public void onTabUnselected(TabLayout.Tab tab) {}

    @Override
    public void onTabReselected(TabLayout.Tab tab) {}


    public void onPathStepClick(ItemsPathStep pathStepItem) {
        showMarkerButtonLayout(false, false);
        selectedMarker.hideInfoWindow();
        moveCameraAndDrawHighlight(new LatLng(pathStepItem.START_LAT, pathStepItem.START_LNG), pathStepItem.POLY_LINE, 0);

        int layoutMinHeight = (int) (screenHeight * 0.7);
        int layoutDefaultHeight = (int) (screenHeight * 0.4);

        if (pathInfoLayout.getLayoutParams().height > layoutMinHeight) {
            LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, layoutDefaultHeight);
            pathInfoLayout.setLayoutParams(params);
            setFooterBackground(true);
        }
    }

    public void onTransitStepClick(LatLng location, String polyline, int colorRes) {
        showMarkerButtonLayout(false, false);
        selectedMarker.hideInfoWindow();
        moveCameraAndDrawHighlight(location, polyline, colorRes);

        int layoutMinHeight = (int) (screenHeight * 0.8);
        int layoutDefaultHeight = (int) (screenHeight * 0.5);

        if (pathInfoLayout.getLayoutParams().height > layoutMinHeight) {
            LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, layoutDefaultHeight);
            pathInfoLayout.setLayoutParams(params);
            setFooterBackground(true);
        }
    }

    /**
     * @param colorRes Set to 0 to using default color.
     */
    private void moveCameraAndDrawHighlight(LatLng location, String polyline, int colorRes) {
        ArrayList<LatLng> latLngList = PolyHelper.decodePolyLine(polyline);

        PolylineOptions polyLine = new PolylineOptions();

        for (LatLng latLng : latLngList) {
            polyLine.add(latLng);
        }

        int res = R.color.md_blue_A700;

        if (colorRes == 0)
            polyLine.width(18);
        else {
            polyLine.width(22);
            res = colorRes;
        }

        polyLine.color(ContextCompat.getColor(AppController.getInstance().getAppContext(), res));
        polyLine.zIndex(1000);

        if (notNull(highLightPoly))
            highLightPoly.remove();

        highLightPoly = map.addPolyline(polyLine);

        if (colorRes == 0)
            moveCameraAndZoom(location, 18);
        else
            moveCamera(location);
    }

    /**
     * <h1>2016/10/12 Updated:<p>
     *
     * All the map layers and polyline click functions were moved to {@link BaseMapActivity}.
     * @see #setLayersByPrefKey(String)
     */

    private void closeAllLayerFlag() {
        SettingManager.MapLayer.setCyclingLayer(false);
        SettingManager.MapLayer.setTopTenLayer(false);
        SettingManager.MapLayer.setRecommendedLayer(false);
        SettingManager.MapLayer.setAllOfTaiwanLayer(false);
        SettingManager.MapLayer.setRentStationLayer(false);
        SettingManager.MapLayer.setYouBikeLayer(false);
    }

    private void setPathInfoLayoutMaxHeight() {
        if (pathInfoLayoutMaxHeight == 0) {
            pathInfoLayoutMaxHeight = (screenHeight - (
                    Utility.getActionbarHeight() + getApplicationContext().getResources().getDimensionPixelSize(R.dimen.font_text_size_xxl)
            ));
        }
    }

    @Override
    public boolean onTouch(View v, MotionEvent event) {
        final float X = event.getRawX();
        final float Y = event.getRawY();
        //Log.i(TAG, "onTouch - RawX: " + X + " RawY: " + Y);

        float xDown;
        float yDown;

        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:

                break;

            case MotionEvent.ACTION_MOVE:
                xDown = screenWidth - X;
                yDown = screenHeight - Y;
                Log.i(TAG, "ACTION_MOVE - xDown: " + xDown + " yDown: " + yDown);

                if (yDown > (screenHeight / 10)) {
                    showPathInfoLayout(true);
                    setPathInfoLayoutHeight((int)yDown);
                }
                else
                    showPathInfoLayout(false);

                setSearchTextTransparent(yDown > (screenHeight / 2.5));

                break;

            case MotionEvent.ACTION_UP:

                break;
        }

        return true;
    }

    private void setPathInfoLayoutHeight(int height) {
        if (isNotReachedMaxHeight() || height < pathInfoLayoutMaxHeight) {
            final LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, height);
            pathInfoLayout.setLayoutParams(params);
            setFooterBackground(true);
        }
        else
            setFooterBackground(false);
    }

    private boolean isNotReachedMaxHeight() {
        if (pathInfoLayout.getLayoutParams().height > (pathInfoLayoutMaxHeight * 0.9)) {
            final LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, pathInfoLayoutMaxHeight);
            pathInfoLayout.setLayoutParams(params);
            setFooterBackground(false);
        }
        return pathInfoLayout.getLayoutParams().height < pathInfoLayoutMaxHeight;
    }

    private void setFooterBackground(boolean lightBackground) {
        if (notNull(footerImage.getTag())) {
            if (lightBackground && (int)footerImage.getTag() != FOOTER_TAG_BACKGROUND_LIGHT) {
                footerImage.setBackgroundResource(R.drawable.background_footer_gradient_light);
                footerImage.setTag(FOOTER_TAG_BACKGROUND_LIGHT);
            }
            else if (!lightBackground) {
                footerImage.setBackgroundResource(R.drawable.background_footer_gradient_dark);
                footerImage.setTag(FOOTER_TAG_BACKGROUND_DARK);
            }
        }
    }

    private void putMarkerFromPoiBook() {
        String title = getIntent().getStringExtra(BUNDLE_PUT_MARKER_TITLE);
        String snippet = getIntent().getStringExtra(BUNDLE_PUT_MARKER_SNIPPET);
        double[] coordinates = getIntent().getDoubleArrayExtra(BUNDLE_PUT_MARKER_COORDINATES);

        setMarkerTypeMap(coordinates[0], coordinates[1], R.drawable.ic_pin_place);

        MarkerOptions marker = new MarkerOptions();
        marker.title(title);
        marker.snippet(snippet);
        marker.position(new LatLng(coordinates[0], coordinates[1]));

        Bitmap bitmap = getBitmapFromMemCache(BITMAP_KEY_PIN_PLACE);
        if (bitmap == null) {
            Drawable drawable = ContextCompat.getDrawable(getApplicationContext(), R.drawable.ic_pin_place);
            bitmap = BitmapUtility.convertDrawableToBitmap(drawable, getResources().getDimensionPixelSize(R.dimen.icon_marker_common_size));
            addBitmapToMemoryCache(BITMAP_KEY_PIN_PLACE, bitmap);
        }
        marker.icon(BitmapDescriptorFactory.fromBitmap(bitmap));

        if (notNull(poiBookMarker))
            poiBookMarker.remove();

        poiBookMarker = map.addMarker(marker);
        onMarkerClick(poiBookMarker);
    }

    private void zoomToFitsBetweenTwoMarkers(LatLng origin) {
        double[] coordinates = getIntent().getDoubleArrayExtra(BUNDLE_PUT_MARKER_COORDINATES);
        LatLng destination = new LatLng(coordinates[0], coordinates[1]);

        moveCameraAndZoomToFits(origin, destination);

        getIntent().removeExtra(BUNDLE_DIRECTION_FROM_POI_BOOK);
    }

    @Override
    protected void onMarkerNavigationClick() {
        if (isOfficialGoogleMapsInstalled()) {
            String location = String.valueOf(selectedMarker.getPosition().latitude) + "," + String.valueOf(selectedMarker.getPosition().longitude);

            Uri mapUri = Uri.parse(MessageFormat.format(GOOGLE_MAPS_URI, location, NAVIGATION_MODE_WALK));
            Intent mapIntent = new Intent(Intent.ACTION_VIEW, mapUri);

            mapIntent.setPackage(GOOGLE_MAPS_PACKAGE);

            if (mapIntent.resolveActivity(getPackageManager()) != null)
                startActivity(mapIntent);
        }
        else
            Utility.toastShort(getString(R.string.google_maps_is_not_installed));
    }

    private boolean isOfficialGoogleMapsInstalled() {
        try {
            getPackageManager().getPackageInfo(GOOGLE_MAPS_PACKAGE, PackageManager.GET_ACTIVITIES);
            return true;
        }
        catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
            return false;
        }
    }

    @Override
    public void onBackPressed() {
        if (pathInfoLayout.getVisibility() == View.VISIBLE) {
            showPathInfoLayout(false);
            setFooterBackground(true);
        }
        else if (ENTRY_TYPE == ENTRY_TYPE_DEFAULT)
            finishByDoubleClickBack();
        else
            super.onBackPressed();
    }

    private void finishByDoubleClickBack() {
        if (System.currentTimeMillis() - exitTime > 2000) {
            Utility.toastShort(getString(R.string.confirm_to_exit));
            exitTime = System.currentTimeMillis();
        }
        else if (isTrackingServiceRunning()) {
            super.onBackPressed();
        }
        else {
            closeAllLayerFlag();
            WebAgent.stopRetryThread();
            PopWindowHelper.dismissPopWindow();
            Utility.forceCloseTask();
        }
    }
}