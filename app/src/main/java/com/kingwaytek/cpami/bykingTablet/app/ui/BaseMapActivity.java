package com.kingwaytek.cpami.bykingTablet.app.ui;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.BitmapFactory;
import android.location.Location;
import android.os.Bundle;
import android.provider.Settings;
import android.support.design.widget.NavigationView;
import android.support.v4.app.ActivityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.EditText;
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
import com.kingwaytek.cpami.bykingTablet.app.model.ItemsSearchResult;
import com.kingwaytek.cpami.bykingTablet.hardware.MyLocationManager;
import com.kingwaytek.cpami.bykingTablet.utilities.LocationSearchHelper;
import com.kingwaytek.cpami.bykingTablet.utilities.UtilDialog;
import com.kingwaytek.cpami.bykingTablet.utilities.Utility;

import java.io.InputStream;
import java.util.ArrayList;

/**
 * 所有要使用 GoogleMap的地方都直接繼承這裡就好了！
 *
 * @author Vincent (2016/04/14)
 */
public abstract class BaseMapActivity extends BaseActivity implements OnMapReadyCallback,
        GoogleMap.InfoWindowAdapter, GoogleMap.OnInfoWindowClickListener, GoogleMap.OnMarkerClickListener {

    protected abstract void onLocateMyPosition(Location location);

    private static final int PLAY_SERVICES_RESOLUTION_REQUEST = 9000;

    private static final int LOCATION_UPDATE_REQUEST_CODE = 100;
    private boolean permissionChecked;

    protected MyLocationManager locationManager;
    protected GoogleMap map;

    protected AutoCompleteTextView searchText;
    private Marker searchMarker;

    private DrawerLayout drawer;
    private NavigationView drawerView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        checkLocationPermissions(LOCATION_UPDATE_REQUEST_CODE);
        buildMap();

        initDrawer();
    }

    @Override
    protected int getLayoutId() {
        return R.layout.activity_base_map;
    }

    @Override
    protected void findViews() {
        searchText = (AutoCompleteTextView) findViewById(R.id.edit_searchText);
        drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawerView = (NavigationView) findViewById(R.id.navigation_view);
    }

    @Override
    protected void onResume() {
        super.onResume();
        requestLocationUpdate();
    }

    @Override
    public void onPause() {
        super.onPause();
        removeLocationUpdate();
    }

    private void requestLocationUpdate() {
        if (permissionChecked) {
            if (locationManager == null)
                locationManager = AppController.getInstance().getLocationManager();
            else
                locationManager.getProvidersAndUpdate(MyLocationManager.getLocationManager());
        }
    }

    private void removeLocationUpdate() {
        if (permissionChecked && notNull(locationManager))
            locationManager.removeUpdate();
    }

    private void checkLocationPermissions(final int requestCode) {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED)
        {
            if (ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.ACCESS_FINE_LOCATION)) {
                UtilDialog dialog = new UtilDialog(this) {
                  @Override
                  public void click_btn_1() {
                      super.click_btn_1();
                      requestLocationPermission(requestCode);
                  }
                };
                dialog.showDialog_route_plan_choice(
                        getString(R.string.location_permission_rationale_title),
                        getString(R.string.location_permission_rationale_content),
                        getString(R.string.confirm),
                        null);
            }
            else
                requestLocationPermission(requestCode);
        }
        else
            permissionChecked = true;
    }

    private void requestLocationPermission(int requestCode) {
        ActivityCompat.requestPermissions(this,
                new String[]{Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION}, requestCode);
    }

    private void buildMap() {
        if (checkPlayServices()) {
            SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.googleMapFragment);
            mapFragment.getMapAsync(this);
        }
    }

    @Override
    public void onMapReady(GoogleMap map) {
        this.map = map;
        initMapState();
        turnOnSearchKeyListener(true);
        setListener();
        init();
    }

    private void initMapState() {
        try {
            map.setMyLocationEnabled(true);
            map.setBuildingsEnabled(true);
            map.getUiSettings().setZoomControlsEnabled(true);
            map.getUiSettings().setMapToolbarEnabled(true);
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

            map.setInfoWindowAdapter(this);
            map.setOnInfoWindowClickListener(this);
        }
        catch (SecurityException e) {
            e.printStackTrace();
        }
    }

    @Override
    public View getInfoWindow(Marker marker) {
        View view = LayoutInflater.from(this).inflate(R.layout.inflate_marker_search_result_window, null);
        TextView title = (TextView) view.findViewById(R.id.marker_title);
        title.setText(marker.getTitle());

        return view;
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
                //finish();
            }
            return false;
        }
        return true;
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
        LocationSearchHelper.searchLocation(query, new LocationSearchHelper.OnLocationFoundCallBack() {
            @Override
            public void onLocationFound(ArrayList<ItemsSearchResult> searchResults, ArrayList<String> nameList, boolean isSearchByGeocoder) {
                if (notNull(searchResults.get(0)))
                    putSearchMarkerOnMap(nameList.get(0), new LatLng(searchResults.get(0).LAT, searchResults.get(0).LNG));
            }
            @Override
            public void onNothingFound() {
                Utility.toastLong(getString(R.string.location_search_found_nothing));
            }
        });
    }

    private void putSearchMarkerOnMap(String title, LatLng latLng) {
        if (notNull(searchMarker))
            searchMarker.remove();

        MarkerOptions marker = new MarkerOptions();
        marker.position(latLng);
        marker.title(title);
        InputStream is = getResources().openRawResource(+ R.drawable.ic_start);
        marker.icon(BitmapDescriptorFactory.fromBitmap(BitmapFactory.decodeStream(is)));

        searchMarker = map.addMarker(marker);
        searchMarker.showInfoWindow();
        moveCameraAndZoom(latLng, 16);

        closeInputStream(is);
    }

    protected void clearSearchText() {
        searchText.setText("");
    }

    protected ArrayAdapter<String> getSimpleAdapter(ArrayList<String> nameList) {
        return new ArrayAdapter<>(this, R.layout.listview_simple_layout_with_right_arrow, nameList);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults) {
        if (requestCode == LOCATION_UPDATE_REQUEST_CODE) {
            if (grantResults.length > 0
                    && grantResults[0] == PackageManager.PERMISSION_GRANTED
                    && grantResults[1] == PackageManager.PERMISSION_GRANTED)
            {
                permissionChecked = true;
                requestLocationUpdate();
            }
            else {
                Utility.toastShort(getString(R.string.location_permission_denied));
                permissionChecked = false;
            }
        }
    }

    private void initDrawer() {
        setDrawerWidth();

        ActionBarDrawerToggle drawerToggle = new ActionBarDrawerToggle(this, drawer, R.string.drawer_open, R.string.drawer_close) {
            @Override
            public void onDrawerOpened(View drawerView) {
                super.onDrawerOpened(drawerView);
            }

            @Override
            public void onDrawerClosed(View drawerView) {
                super.onDrawerClosed(drawerView);
            }
        };
        drawer.addDrawerListener(drawerToggle);
        drawerToggle.syncState();

        drawerView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(MenuItem item) {
                item.setChecked(true);
                Utility.toastShort(item.getTitle().toString());
                drawer.closeDrawers();

                return true;
            }
        });
    }

    private void setDrawerWidth() {
        int width = (int) (Utility.getScreenWidth() / 1.8);

        DrawerLayout.LayoutParams params = (DrawerLayout.LayoutParams) drawerView.getLayoutParams();
        params.width = width;

        drawerView.setLayoutParams(params);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        turnOnSearchKeyListener(false);
    }
}
