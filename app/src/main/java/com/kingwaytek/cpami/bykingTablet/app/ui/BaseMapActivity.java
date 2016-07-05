package com.kingwaytek.cpami.bykingTablet.app.ui;

import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.BitmapFactory;
import android.location.Location;
import android.os.Bundle;
import android.provider.Settings;
import android.support.annotation.NonNull;
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
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.kingwaytek.cpami.bykingTablet.AppController;
import com.kingwaytek.cpami.bykingTablet.R;
import com.kingwaytek.cpami.bykingTablet.app.model.items.ItemsSearchResult;
import com.kingwaytek.cpami.bykingTablet.hardware.MyLocationManager;
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
        SharedPreferences.OnSharedPreferenceChangeListener {

    protected abstract void onMapReady();
    protected abstract void onLocateMyPosition(Location location);

    private static final int PLAY_SERVICES_RESOLUTION_REQUEST = 9000;

    protected GoogleMap map;

    //private MyLocationManager locationManager;
    private boolean locationPermissionChecked;
    private boolean isMapBuilt;

    protected RelativeLayout mapRootLayout;
    protected FrameLayout searchTextLayout;
    protected LinearLayout markerBtnLayout;

    protected AutoCompleteTextView searchText;
    protected Marker searchMarker;

    protected HashMap<String, Integer> markerTypeMap;

    protected ImageButton markerBtn_edit;
    protected ImageButton markerBtn_direction;
    protected ImageButton markerBtn_navigation;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        checkLocationPermissions();

        if (locationPermissionChecked)
            buildMap();

        initDrawer();
    }

    @Override
    protected int getLayoutId() {
        return R.layout.activity_base_map;
    }

    @Override
    protected void findViews() {
        mapRootLayout = (RelativeLayout) findViewById(R.id.mapRootLayout);
        searchTextLayout = (FrameLayout) findViewById(R.id.searchTextLayout);
        searchText = (AutoCompleteTextView) findViewById(R.id.edit_searchText);
        markerBtnLayout = (LinearLayout) findViewById(R.id.markerBtnLayout);
        markerBtn_edit = (ImageButton) findViewById(R.id.markerBtn_edit);
        markerBtn_direction = (ImageButton) findViewById(R.id.markerBtn_routePath);
        markerBtn_navigation = (ImageButton) findViewById(R.id.markerBtn_navigation);
    }

    @Override
    protected void onResume() {
        super.onResume();
        requestLocationUpdate();
        unCheckAllMenuItem();
    }

    @Override
    public void onPause() {
        super.onPause();
        removeLocationUpdate();
    }

    private void requestLocationUpdate() {
        if (locationPermissionChecked)
            AppController.getInstance().initLocationManager();
    }

    private void removeLocationUpdate() {
        if (locationPermissionChecked)
            AppController.getInstance().removeLocationManager();
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

            map.setOnMapClickListener(new GoogleMap.OnMapClickListener() {
                @Override
                public void onMapClick(LatLng latLng) {
                    showMarkerButtonLayout(false, false);
                }
            });

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

        InputStream is = getResources().openRawResource(+ R.drawable.ic_search_result);
        marker.icon(BitmapDescriptorFactory.fromBitmap(BitmapFactory.decodeStream(is)));

        setMarkerTypeMap(latLng.latitude, latLng.longitude, R.drawable.ic_search_result);

        searchMarker = map.addMarker(marker);
        searchMarker.showInfoWindow();

        onMarkerClick(searchMarker);

        moveCameraAndZoom(latLng, 16);

        closeInputStream(is);
    }

    protected void clearSearchText() {
        searchText.setText("");
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
            markerBtnLayout.setVisibility(View.GONE);
            markerBtn_edit.setOnClickListener(null);
            markerBtn_navigation.setOnClickListener(null);
            markerBtn_direction.setOnClickListener(null);
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

    @Override
    protected void onDestroy() {
        super.onDestroy();
        turnOnSearchKeyListener(false);
        if (notNull(drawer))
            drawer.removeDrawerListener(drawerToggle);
    }
}
