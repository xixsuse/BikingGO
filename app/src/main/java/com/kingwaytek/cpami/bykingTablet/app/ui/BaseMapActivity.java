package com.kingwaytek.cpami.bykingTablet.app.ui;

import android.content.Intent;
import android.location.Location;
import android.os.Bundle;
import android.provider.Settings;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.widget.TextView;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GoogleApiAvailability;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.kingwaytek.cpami.bykingTablet.AppController;
import com.kingwaytek.cpami.bykingTablet.R;
import com.kingwaytek.cpami.bykingTablet.hardware.MyLocationManager;
import com.kingwaytek.cpami.bykingTablet.utilities.UtilDialog;
import com.kingwaytek.cpami.bykingTablet.utilities.Utility;

/**
 * 所有要使用 GoogleMap的地方都直接繼承這裡就好了！
 *
 * @author Vincent (2016/04/14)
 */
public abstract class BaseMapActivity extends AppCompatActivity implements OnMapReadyCallback {

    protected abstract void init();
    protected abstract String getActionBarTitle();
    protected abstract void findViews();
    protected abstract void setListener();

    protected final String TAG = getClass().getSimpleName();
    private static final int PLAY_SERVICES_RESOLUTION_REQUEST = 9000;

    protected MyLocationManager locationManager;
    protected GoogleMap map;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setTheme(R.style.AppTheme);
        setContentView(R.layout.activity_base_map);

        findViews();
        getLocationManager();
        setActionBar();

        buildMap();
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

    private void setActionBar() {
        ActionBar actionbar = getSupportActionBar();

        if (notNull(actionbar)) {
            actionbar.setDisplayShowTitleEnabled(false);
            actionbar.setHomeButtonEnabled(false);
            actionbar.setDisplayOptions(ActionBar.DISPLAY_SHOW_CUSTOM);

            actionbar.setCustomView(R.layout.include_foolish_action_bar);
            Toolbar toolbar = (Toolbar) actionbar.getCustomView().getParent();
            toolbar.setContentInsetsAbsolute(0, 0);

            TextView title = (TextView) actionbar.getCustomView().findViewById(R.id.actionBar_title);
            title.setText(getActionBarTitle());
        }
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
        }
        catch (SecurityException e) {
            e.printStackTrace();
        }
    }

    private void moveCameraToDefaultLocation() {
        Location location = MyLocationManager.getLastLocation();
        if (notNull(location))
            map.moveCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(location.getLatitude(), location.getLongitude()), 13));
        else
            Utility.toastShort(getString(R.string.gps_unable_to_get_location));
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

    public static boolean notNull(Object anyObject) {
        return anyObject != null;
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }
}
