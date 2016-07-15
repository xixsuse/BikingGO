package com.kingwaytek.cpami.bykingTablet.app.ui.track;

import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.location.Location;
import android.os.Handler;
import android.provider.Settings;
import android.support.design.widget.FloatingActionButton;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.google.android.gms.maps.model.Marker;
import com.kingwaytek.cpami.bykingTablet.AppController;
import com.kingwaytek.cpami.bykingTablet.R;
import com.kingwaytek.cpami.bykingTablet.app.service.TrackingService;
import com.kingwaytek.cpami.bykingTablet.app.ui.BaseMapActivity;
import com.kingwaytek.cpami.bykingTablet.callbacks.OnGpsLocateCallBack;
import com.kingwaytek.cpami.bykingTablet.hardware.MyLocationManager;
import com.kingwaytek.cpami.bykingTablet.utilities.DialogHelper;

/**
 * 軌跡錄製地圖
 *
 * @author Vincent (2016/7/14)
 */
public class UiTrackMapActivity extends BaseMapActivity implements OnGpsLocateCallBack {

    private LinearLayout gpsStateLayout;
    private TextView gpsStateText;
    private ProgressBar gpsStateCircle;

    private FloatingActionButton trackBtn;

    @Override
    protected void onMapReady() {
        checkGpsState();
    }

    @Override
    public void onResume() {
        super.onResume();

    }

    @Override
    protected int getMapLayout() {
        return R.layout.activity_track_map;
    }

    @Override
    protected String getActionBarTitle() {
        return getString(R.string.menu_bike_track);
    }

    @Override
    protected void findViews() {
        gpsStateLayout = (LinearLayout) findViewById(R.id.gpsStateLayout);
        gpsStateText = (TextView) findViewById(R.id.text_gpsState);
        gpsStateCircle = (ProgressBar) findViewById(R.id.gpsStateLoadingCircle);
        trackBtn = (FloatingActionButton) findViewById(R.id.floatingBtn_track);
    }

    @Override
    protected void setListener() {
        trackBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(UiTrackMapActivity.this, TrackingService.class);
                UiTrackMapActivity.this.startService(intent);
            }
        });
    }

    @Override
    public void onInfoWindowClick(Marker marker) {

    }

    @Override
    public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {

    }

    @Override
    protected void onLocateMyPosition(Location location) {

    }

    @Override
    protected void requestLocationUpdate() {
        // Do Nothing!! Until service started.
        AppController.getInstance().initGPSLocationManager(10, 2, this);
    }

    @Override
    protected void removeLocationUpdate() {
        // Do Nothing!
    }

    private void checkGpsState() {
        if (MyLocationManager.isGpsDisabled()) {
            AppController.getInstance().removeLocationManager();

            DialogHelper.showGpsRequestDialog(this, new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialogInterface, int i) {
                    Intent intent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                    startActivity(intent);
                }
            });
        }
    }

    @Override
    public void onGpsLocated() {
        gpsStateCircle.setVisibility(View.GONE);
        gpsStateText.setText(getString(R.string.track_gps_locate_done));

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                gpsStateLayout.setVisibility(View.GONE);
            }
        }, 2000);
    }

    @Override
    public void onGpsLocating() {
        gpsStateLayout.setVisibility(View.VISIBLE);
    }
}
