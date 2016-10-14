package com.kingwaytek.cpami.bykingTablet.app.ui;

import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.BitmapFactory;
import android.location.Location;
import android.os.Bundle;
import android.os.Handler;
import android.os.HandlerThread;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GoogleApiAvailability;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;
import com.kingwaytek.cpami.bykingTablet.AppController;
import com.kingwaytek.cpami.bykingTablet.R;
import com.kingwaytek.cpami.bykingTablet.app.model.DataArray;
import com.kingwaytek.cpami.bykingTablet.app.model.items.ItemsSearchResult;
import com.kingwaytek.cpami.bykingTablet.app.model.items.ItemsYouBike;
import com.kingwaytek.cpami.bykingTablet.hardware.MyLocationManager;
import com.kingwaytek.cpami.bykingTablet.utilities.DebugHelper;
import com.kingwaytek.cpami.bykingTablet.utilities.LocationSearchHelper;
import com.kingwaytek.cpami.bykingTablet.utilities.PermissionCheckHelper;
import com.kingwaytek.cpami.bykingTablet.utilities.SettingManager;
import com.kingwaytek.cpami.bykingTablet.utilities.UtilDialog;
import com.kingwaytek.cpami.bykingTablet.utilities.Utility;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;

/**
 * 所有要使用 GoogleMap的地方都直接繼承這裡就好了！
 *
 * @author Vincent (2016/04/14)
 */
public abstract class BaseMapActivity extends BaseActivity implements OnMapReadyCallback,
        GoogleMap.InfoWindowAdapter, GoogleMap.OnInfoWindowClickListener, GoogleMap.OnMarkerClickListener,
        SharedPreferences.OnSharedPreferenceChangeListener, MapLayerHandler.OnLayerChangedCallback {

    protected abstract void onMapReady();
    protected abstract int getMapLayout();
    protected abstract void onLocateMyPosition(Location location);

    private static final int PLAY_SERVICES_RESOLUTION_REQUEST = 9000;

    protected GoogleMap map;

    //private MyLocationManager locationManager;
    private boolean locationPermissionChecked;
    private boolean isMapBuilt;

    protected RelativeLayout mapRootLayout;
    protected FrameLayout searchTextLayout;
    protected LinearLayout markerBtnLayout;

    protected Marker selectedMarker;

    protected AutoCompleteTextView searchText;
    protected Marker searchMarker;

    protected HashMap<String, Integer> markerTypeMap;

    protected ImageButton markerBtn_edit;
    protected ImageButton markerBtn_direction;
    protected ImageButton markerBtn_navigation;
    protected ImageButton uBikeRefreshBtn;

    private TextView polylineName;
    private TextView polylineLocation;
    private TextView polylineDescription;

    protected RelativeLayout polylineInfoLayout;

    protected MapLayerHandler layerHandler;
    private static final String NAME_OF_HANDLER_THREAD = "LayerHandlerThread";
    private HandlerThread handlerThread;

    private ArrayList<ItemsYouBike> tempYouBikeList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        checkLocationPermissions();

        if (locationPermissionChecked)
            buildMap();

        if (ENTRY_TYPE == ENTRY_TYPE_DEFAULT)
            initDrawer();
    }

    @Override
    protected void init() {

    }

    @Override
    protected int getLayoutId() {
        return getMapLayout();
    }

    @Override
    protected void findViews() {
        if (ENTRY_TYPE == ENTRY_TYPE_DEFAULT || ENTRY_TYPE == ENTRY_TYPE_TRACKING) {
            if (getMapLayout() == R.layout.activity_main_map)
                uBikeRefreshBtn = (ImageButton) findViewById(R.id.uBikeRefreshBtn);

            polylineInfoLayout = (RelativeLayout) findViewById(R.id.polylineInfoLayout);
            polylineName = (TextView) findViewById(R.id.text_polylineName);
            polylineLocation = (TextView) findViewById(R.id.text_polylineLocation);
            polylineDescription = (TextView) findViewById(R.id.text_polylineDescription);
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        requestLocationUpdate();
        //unCheckAllMenuItem();
    }

    @Override
    public void onPause() {
        super.onPause();
        removeLocationUpdate();
    }

    protected void requestLocationUpdate() {
        if (locationPermissionChecked) {
            if (!isTrackingServiceRunning())
                AppController.getInstance().initLocationManager();
        }
    }

    protected void removeLocationUpdate() {
        if (locationPermissionChecked) {
            if (!isTrackingServiceRunning())
                AppController.getInstance().removeLocationManager();
        }
    }

    private void checkLocationPermissions() {
        locationPermissionChecked = PermissionCheckHelper.checkLocationPermissions(this);
    }

    private void buildMap() {
        if (checkPlayServices()) {
            SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.googleMapFragment);
            mapFragment.getMapAsync(this);
            isMapBuilt = true;
            Log.i(TAG, "buildMap!!!!");
        }
    }

    @Override
    public void onMapReady(GoogleMap map) {
        this.map = map;
        initMapState();
        turnOnSearchKeyListener(true);
        setListener();
        onMapReady();
    }

    private void initMapState() {
        try {
            map.setMyLocationEnabled(true);
            map.setBuildingsEnabled(true);
            map.getUiSettings().setZoomControlsEnabled(true);
            map.getUiSettings().setMapToolbarEnabled(false);
            moveCameraToDefaultLocation();

            map.setOnMyLocationButtonClickListener(new GoogleMap.OnMyLocationButtonClickListener() {
                @Override
                public boolean onMyLocationButtonClick() {
                    if (MyLocationManager.isGpsDisabled()) {
                        UtilDialog uit = new UtilDialog(BaseMapActivity.this) {
                            @Override
                            public void click_btn_1() {
                                super.click_btn_1();
                                Intent intent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                                startActivity(intent);
                            }
                        };
                        uit.showDialog_route_plan_choice(getString(R.string.gps_is_not_enabled), null,
                                getString(R.string.confirm), getString(R.string.confirm_cancel));
                        return true;
                    }
                    else {
                        onLocateMyPosition(MyLocationManager.getLastLocation());
                        return false;
                    }
                }
            });

            if (ENTRY_TYPE == ENTRY_TYPE_DEFAULT || ENTRY_TYPE == ENTRY_TYPE_TRACKING) {
                map.setOnMapClickListener(new GoogleMap.OnMapClickListener() {
                    @Override
                    public void onMapClick(LatLng latLng) {
                        showMarkerButtonLayout(false, false);
                        polylineInfoLayout.setVisibility(View.GONE);
                    }
                });
            }

            map.setInfoWindowAdapter(this);
            map.setOnInfoWindowClickListener(this);
        }
        catch (SecurityException e) {
            e.printStackTrace();
        }
    }

    /**
     * 放入 key: lat + lng & value: Marker icon resource ID
     *
     * markerTypeMap用來在 onInfoWindowClick時判斷要觸發什麼事件
     */
    protected void setMarkerTypeMap(double lat, double lng, int iconResId) {
        if (markerTypeMap == null)
            markerTypeMap = new HashMap<>();

        String key = String.valueOf(lat) + String.valueOf(lng);

        markerTypeMap.put(key, iconResId);
    }

    @Override
    public View getInfoWindow(Marker marker) {
        if (notNull(marker.getTitle())) {
            View view = LayoutInflater.from(this).inflate(R.layout.inflate_marker_search_result_window, null);
            TextView title = (TextView) view.findViewById(R.id.marker_title);

            title.setText(marker.getTitle());

            return view;
        }
        else
            return null;
    }

    @Override
    public View getInfoContents(Marker marker) {
        return null;
    }

    @Override
    public void onInfoWindowClick(Marker marker) {

    }

    @Override
    public boolean onMarkerClick(Marker marker) {
        return false;
    }

    private void moveCameraToDefaultLocation() {
        Location location = MyLocationManager.getLastLocation();
        if (notNull(location))
            map.moveCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(location.getLatitude(), location.getLongitude()), 13));
        else
            Utility.toastShort(getString(R.string.gps_unable_to_get_location));
    }

    public void moveCamera(LatLng latLng) {
        map.animateCamera(CameraUpdateFactory.newLatLng(latLng));
    }

    public void moveCameraAndZoom(LatLng latLng, int zoomLevel) {
        map.animateCamera(CameraUpdateFactory.newLatLngZoom(latLng, zoomLevel));
    }

    public void moveCameraAndZoomToFits(LatLng origin, LatLng destination) {
        LatLngBounds.Builder boundsBuilder = new LatLngBounds.Builder();

        boundsBuilder.include(origin);
        boundsBuilder.include(destination);

        LatLngBounds bounds = boundsBuilder.build();

        CameraUpdate cu = CameraUpdateFactory.newLatLngBounds(bounds, 100);
        //map.animateCamera(cu);

        map.animateCamera(cu, new GoogleMap.CancelableCallback() {
            @Override
            public void onFinish() {
                //CameraUpdate zoomOut = CameraUpdateFactory.zoomBy(-3.0f);
                //map.animateCamera(zoomOut);
                Log.i(TAG, "MapZoomBounds!!!!!");
            }

            @Override
            public void onCancel() {}
        });
    }

    private boolean checkPlayServices() {
        GoogleApiAvailability apiAvailability = GoogleApiAvailability.getInstance();
        int resultCode = apiAvailability.isGooglePlayServicesAvailable(this);

        if (resultCode != ConnectionResult.SUCCESS)
        {
            if (apiAvailability.isUserResolvableError(resultCode))
                apiAvailability.getErrorDialog(this, resultCode, PLAY_SERVICES_RESOLUTION_REQUEST).show();
            else {
                Utility.toastShort("This device is not supported PlayServices.");
                Log.i(TAG, "This device is not supported.");
                finish();
            }
            return false;
        }
        return true;
    }

    protected void registerPreferenceChangedListener() {
        SettingManager.prefs.registerOnSharedPreferenceChangeListener(this);
    }

    protected void unRegisterPreferenceChangedListener() {
        SettingManager.prefs.unregisterOnSharedPreferenceChangeListener(this);
    }

    private void turnOnSearchKeyListener(boolean isOn) {
        if (ENTRY_TYPE != ENTRY_TYPE_TRACKING && ENTRY_TYPE != ENTRY_TYPE_TRACK_VIEWING && ENTRY_TYPE != ENTRY_TYPE_VIEW_SHARED_TRACK) {
            if (isOn) {
                searchText.setOnKeyListener(getOnKeyListener());
                searchText.setText("");
                searchText.setSingleLine();
                searchText.setImeOptions(EditorInfo.IME_ACTION_SEARCH);
            }
            else {
                searchText.setOnKeyListener(null);
                searchText.setImeOptions(EditorInfo.IME_ACTION_DONE);
            }
        }
    }

    private EditText.OnKeyListener getOnKeyListener() {
        return new EditText.OnKeyListener() {
            @Override
            public boolean onKey(View v, int keyCode, KeyEvent event) {
                if (keyCode == KeyEvent.KEYCODE_ENTER && event.getAction() == KeyEvent.ACTION_UP) {
                    searchLocation(searchText.getText().toString());
                    clearSearchText();
                    hideKeyboard(true);
                    return true;
                }
                return false;
            }
        };
    }

    protected void searchLocation(String query) {
        LocationSearchHelper.searchLocation(this, query, new LocationSearchHelper.OnLocationFoundCallBack() {
            @Override
            public void onLocationFound(ArrayList<ItemsSearchResult> searchResults, ArrayList<String> nameList, boolean isSearchByGeocoder) {
                if (notNull(searchResults.get(0)))
                    putSearchMarkerOnMap(nameList.get(0), searchResults.get(0).ADDRESS, new LatLng(searchResults.get(0).LAT, searchResults.get(0).LNG));
            }
            @Override
            public void onNothingFound() {
                Utility.toastLong(getString(R.string.location_search_found_nothing));
            }
        });
    }

    private void putSearchMarkerOnMap(String title, String snippet, LatLng latLng) {
        if (notNull(searchMarker))
            searchMarker.remove();

        MarkerOptions marker = new MarkerOptions();
        marker.position(latLng);
        marker.title(title);
        marker.snippet(snippet);

        InputStream is = getResources().openRawResource(+ R.drawable.ic_marker_search_result);
        marker.icon(BitmapDescriptorFactory.fromBitmap(BitmapFactory.decodeStream(is)));

        setMarkerTypeMap(latLng.latitude, latLng.longitude, R.drawable.ic_marker_search_result);

        searchMarker = map.addMarker(marker);
        searchMarker.showInfoWindow();

        onMarkerClick(searchMarker);
        selectedMarker = searchMarker;

        moveCameraAndZoom(latLng, 16);

        closeInputStream(is);
    }

    protected void clearSearchText() {
        searchText.setText("");
    }

    protected void setSearchTextTransparent(boolean isTransparent) {
        if (isTransparent) {
            searchText.setBackgroundResource(R.drawable.background_search_text_transparent);
            searchText.setHintTextColor(ContextCompat.getColor(getApplicationContext(), R.color.md_grey_100));
        }
        else {
            searchText.setBackgroundResource(R.drawable.background_search_text);
            searchText.setHintTextColor(ContextCompat.getColor(getApplicationContext(), R.color.md_grey_400));
        }
    }

    protected void showMarkerButtonLayout(boolean isShow, boolean showEditBtn) {
        if (isShow) {
            markerBtnLayout.setVisibility(View.VISIBLE);

            if (showEditBtn)
                markerBtn_edit.setImageResource(R.drawable.selector_button_edit);
            else
                markerBtn_edit.setImageResource(R.drawable.selector_button_add_poi);

            markerBtn_edit.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    onMarkerEditClick();
                }
            });

            markerBtn_navigation.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    onMarkerNavigationClick();
                }
            });

            markerBtn_direction.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    onMarkerDirectionClick();
                }
            });
        }
        else {
            if (notNull(markerBtnLayout)) {
                markerBtnLayout.setVisibility(View.GONE);
                markerBtn_edit.setOnClickListener(null);
                markerBtn_navigation.setOnClickListener(null);
                markerBtn_direction.setOnClickListener(null);
            }
        }
    }

    /**
     * 3顆在地圖上出現的 MarkerButton，
     * 以 Override的方法使用！
     */
    protected void onMarkerEditClick() {

    }

    protected void onMarkerNavigationClick() {

    }

    protected void onMarkerDirectionClick() {

    }

    protected ArrayAdapter<String> getSimpleAdapter(ArrayList<String> nameList) {
        return new ArrayAdapter<>(this, R.layout.listview_simple_layout_with_right_arrow, nameList);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String permissions[], @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        switch (requestCode) {
            case PermissionCheckHelper.PERMISSION_REQUEST_CODE_LOCATION:
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED
                        && grantResults[1] == PackageManager.PERMISSION_GRANTED)
                {
                    locationPermissionChecked = true;
                    requestLocationUpdate();
                }
                else {
                    Utility.toastShort(getString(R.string.location_permission_denied));
                    locationPermissionChecked = false;
                }
                if (!isMapBuilt)
                    buildMap();
                break;
        }
    }

    /************************ Polyline layer control area ************************/

    /**
     * Updated by Vincent on 2016/10/12:<p>
     *
     * Load polyline data of GeoJson files which in the project resources.<br>
     * Add and remove polyline and markers by using HandlerThread and {@link MapLayerHandler}
     *
     * @param key The key of SharedPreferences.
     */
    protected void setLayersByPrefKey(final String key) {
        if (handlerThread == null) {
            handlerThread = new HandlerThread(NAME_OF_HANDLER_THREAD);
            handlerThread.start();
        }

        if (layerHandler == null)
            layerHandler = new MapLayerHandler(handlerThread.getLooper(), new Handler(), this);

        boolean isLayerAdded;

        switch (key) {
            case SettingManager.PREFS_LAYER_CYCLING_1:
            case SettingManager.TrackingTimeAndLayer.PREFS_TRACK_MAP_LAYER_CYCLING_1:

                if (getMapLayout() == R.layout.activity_main_map)
                    isLayerAdded = SettingManager.MapLayer.getCyclingLayer();
                else
                    isLayerAdded = SettingManager.TrackingTimeAndLayer.getCyclingLayer();

                if (isLayerAdded) {
                    checkBitmapCache(BITMAP_KEY_SUPPLY_STATION);
                    layerHandler.addLayer(map, getBitmapFromMemCache(BITMAP_KEY_SUPPLY_STATION), MapLayerHandler.LAYER_CYCLING);
                    showLoadingCircle(true);
                }
                else
                    layerHandler.removeLayer(MapLayerHandler.LAYER_CYCLING);

                break;

            case SettingManager.PREFS_LAYER_TOP_TEN:
            case SettingManager.TrackingTimeAndLayer.PREFS_TRACK_MAP_LAYER_TOP_TEN:

                if (getMapLayout() == R.layout.activity_main_map)
                    isLayerAdded = SettingManager.MapLayer.getTopTenLayer();
                else
                    isLayerAdded = SettingManager.TrackingTimeAndLayer.getTopTenLayer();

                if (isLayerAdded) {
                    layerHandler.addLayer(map, null, MapLayerHandler.LAYER_TOP_TEN);
                    showLoadingCircle(true);
                }
                else
                    layerHandler.removeLayer(MapLayerHandler.LAYER_TOP_TEN);

                break;

            case SettingManager.PREFS_LAYER_RECOMMENDED:
            case SettingManager.TrackingTimeAndLayer.PREFS_TRACK_MAP_LAYER_RECOMMENDED:

                if (getMapLayout() == R.layout.activity_main_map)
                    isLayerAdded = SettingManager.MapLayer.getRecommendedLayer();
                else
                    isLayerAdded = SettingManager.TrackingTimeAndLayer.getRecommendedLayer();

                if (isLayerAdded) {
                    layerHandler.addLayer(map, null, MapLayerHandler.LAYER_RECOMMENDED);
                    showLoadingCircle(true);
                }
                else
                    layerHandler.removeLayer(MapLayerHandler.LAYER_RECOMMENDED);

                break;

            case SettingManager.PREFS_LAYER_ALL_OF_TAIWAN:
            case SettingManager.TrackingTimeAndLayer.PREFS_TRACK_MAP_LAYER_ALL_OF_TAIWAN:

                if (getMapLayout() == R.layout.activity_main_map)
                    isLayerAdded = SettingManager.MapLayer.getAllOfTaiwanLayer();
                else
                    isLayerAdded = SettingManager.TrackingTimeAndLayer.getAllOfTaiwanLayer();

                if (isLayerAdded) {
                    layerHandler.addLayer(map, null, MapLayerHandler.LAYER_ALL_OF_TAIWAN);
                    showLoadingCircle(true);
                }
                else
                    layerHandler.removeLayer(MapLayerHandler.LAYER_ALL_OF_TAIWAN);

                break;

            case SettingManager.PREFS_LAYER_RENT_STATION:
                if (SettingManager.MapLayer.getRentStationLayer()) {
                    checkBitmapCache(BITMAP_KEY_BIKE_RENT_STATION);
                    layerHandler.addLayer(map, getBitmapFromMemCache(BITMAP_KEY_BIKE_RENT_STATION), MapLayerHandler.LAYER_RENT_STATION);
                    showLoadingCircle(true);
                }
                else
                    layerHandler.removeLayer(MapLayerHandler.LAYER_RENT_STATION);
                break;

            case SettingManager.PREFS_LAYER_YOU_BIKE:
            case MARKERS_YOU_BIKE_REFRESH:
                if (SettingManager.MapLayer.getYouBikeLayer()) {
                    showLoadingCircle(true);

                    setYouBikeRefreshButtonStatus(false);
                    layerHandler.setIsLayerChanging(true);

                    if (DebugHelper.GET_YOU_BIKE_FROM_OPEN_DATA)
                    {
                        DataArray.getYouBikeData(new DataArray.OnYouBikeDataGetCallback() {
                            @SuppressWarnings("unchecked")
                            @Override
                            public void onTaipeiYouBikeGet(ArrayList<ItemsYouBike> uBikeItems) {
                                if (tempYouBikeList == null || tempYouBikeList.isEmpty())
                                    tempYouBikeList = uBikeItems;
                                else {
                                    tempYouBikeList.addAll(0, uBikeItems);
                                    if (key.equals(MARKERS_YOU_BIKE_REFRESH))
                                        layerHandler.refreshAllYouBikeMarkers(tempYouBikeList);
                                    else
                                        layerHandler.new YouBikeMarkerAddTask(BaseMapActivity.this, map).execute(tempYouBikeList);
                                }
                            }

                            @SuppressWarnings("unchecked")
                            @Override
                            public void onNewTaipeiYouBikeGet(ArrayList<ItemsYouBike> uBikeItems) {
                                if (tempYouBikeList == null || tempYouBikeList.isEmpty())
                                    tempYouBikeList = uBikeItems;
                                else {
                                    tempYouBikeList.addAll(uBikeItems);
                                    if (key.equals(MARKERS_YOU_BIKE_REFRESH))
                                        layerHandler.refreshAllYouBikeMarkers(tempYouBikeList);
                                    else
                                        layerHandler.new YouBikeMarkerAddTask(BaseMapActivity.this, map).execute(tempYouBikeList);
                                }
                            }

                            @Override
                            public void onDataGetFailed() {
                                runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        showLoadingCircle(false);
                                        Utility.toastShort(getString(R.string.network_connection_error_please_retry));
                                        if (notNull(tempYouBikeList)) {
                                            tempYouBikeList.clear();
                                            tempYouBikeList = null;
                                        }
                                        if (notNull(layerHandler) && layerHandler.isYouBikeMarkerAdded())
                                            setYouBikeRefreshButtonStatus(true);

                                        layerHandler.setIsLayerChanging(false);
                                    }
                                });
                            }
                        });
                    }
                    else {
                        DataArray.getAllYouBikeData(new DataArray.OnAllYouBikeDataGetCallback() {
                            @SuppressWarnings("unchecked")
                            @Override
                            public void onAllYouBikeGet(ArrayList<ItemsYouBike> uBikeItems) {
                                if (key.equals(MARKERS_YOU_BIKE_REFRESH))
                                    layerHandler.refreshAllYouBikeMarkers(uBikeItems);
                                else
                                    layerHandler.new YouBikeMarkerAddTask(BaseMapActivity.this, map).execute(uBikeItems);
                            }

                            @Override
                            public void onDataGetFailed() {
                                showLoadingCircle(false);
                                Utility.toastShort(getString(R.string.network_connection_error_please_retry));

                                if (notNull(layerHandler) && layerHandler.isYouBikeMarkerAdded())
                                    setYouBikeRefreshButtonStatus(true);

                                layerHandler.setIsLayerChanging(false);
                            }
                        });
                    }
                }
                else {
                    setYouBikeRefreshButtonStatus(false);
                    layerHandler.removeLayer(MapLayerHandler.LAYER_YOU_BIKE);
                }
                break;
        }
    }

    protected boolean isLayerChanging() {
        return notNull(layerHandler) && layerHandler.isLayerChanging();
    }

    @Override
    public void onPolylinePrepared(final int layerCode, final PolylineOptions polyLine) {
        if (layerHandler == null)
            return;

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
                        showMarkerButtonLayout(false, false);
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

        if (layerCode == MapLayerHandler.LAYER_YOU_BIKE) {
            Utility.toastShort(getString(R.string.you_bike_all_data_get));
            if (notNull(tempYouBikeList)) {
                tempYouBikeList.clear();
                tempYouBikeList = null;
            }
            setYouBikeRefreshButtonStatus(true);
        }
    }

    @Override
    public void onLayersAllGone() {
        if (notNull(handlerThread)) {
            handlerThread.quit();
            handlerThread.interrupt();
            handlerThread = null;
        }
        if (notNull(layerHandler) && !isLayerChanging())
            layerHandler = null;

        map.setOnPolylineClickListener(null);
        polylineInfoLayout.setVisibility(View.GONE);

        Log.i(TAG, "onLayersAllGone!!!!! isLayerChanging: " + isLayerChanging());
    }

    private void setYouBikeRefreshButtonStatus(boolean enabled) {
        if (enabled) {
            uBikeRefreshBtn.setVisibility(View.VISIBLE);
            uBikeRefreshBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    setLayersByPrefKey(MARKERS_YOU_BIKE_REFRESH);
                }
            });
        }
        else {
            uBikeRefreshBtn.setVisibility(View.GONE);
            uBikeRefreshBtn.setOnClickListener(null);
        }
    }

    /************************ Polyline layer control area ************************/

    @Override
    protected void onDestroy() {
        super.onDestroy();
        turnOnSearchKeyListener(false);
        if (notNull(drawer))
            drawer.removeDrawerListener(drawerToggle);

        if (notNull(layerHandler))
            onLayersAllGone();
    }
}
