package com.kingwaytek.cpami.bykingTablet.app.ui;

import android.content.Intent;
import android.graphics.BitmapFactory;
import android.location.Location;
import android.os.Bundle;
import android.provider.Settings;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.inputmethod.EditorInfo;
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

    private static final int PLAY_SERVICES_RESOLUTION_REQUEST = 9000;

    protected MyLocationManager locationManager;
    protected GoogleMap map;

    protected AutoCompleteTextView searchText;
    private Marker searchMarker;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        getLocationManager();
        buildMap();
    }

    @Override
    protected int getLayoutId() {
        return R.layout.activity_base_map;
    }

    @Override
    protected void findViews() {
        searchText = (AutoCompleteTextView) findViewById(R.id.edit_searchText);
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (notNull(locationManager))
            locationManager.getProvidersAndUpdate(MyLocationManager.getLocationManager());
    }

    @Override
    public void onPause() {
        super.onPause();
        if (notNull(locationManager))
            locationManager.removeUpdate();
    }

    private void getLocationManager() {
        locationManager = AppController.getInstance().getLocationManager();
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
                                final Intent i = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                                startActivity(i);
                            }
                        };
                        uit.showDialog_route_plan_choice(getString(R.string.gps_is_not_enabled), null,
                                getString(R.string.confirm), getString(R.string.confirm_cancel));
                        return true;
                    }
                    else
                        return false;
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
                    searchLocation();
                    clearSearchText();
                    hideKeyboard(v, true);
                    return true;
                }
                return false;
            }
        };
    }

    private void searchLocation() {
        String input = searchText.getText().toString();

        LocationSearchHelper.searchLocation(input, new LocationSearchHelper.OnLocationFoundCallBack() {
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
        InputStream is = getResources().openRawResource(+ R.drawable.ic_end);
        marker.icon(BitmapDescriptorFactory.fromBitmap(BitmapFactory.decodeStream(is)));

        searchMarker = map.addMarker(marker);
        searchMarker.showInfoWindow();
        moveCameraAndZoom(latLng, 16);

        closeInputStream(is);
    }

    protected void clearSearchText() {
        searchText.setText("");
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        turnOnSearchKeyListener(false);
    }
}
