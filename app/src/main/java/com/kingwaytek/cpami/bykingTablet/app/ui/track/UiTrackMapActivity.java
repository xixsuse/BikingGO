package com.kingwaytek.cpami.bykingTablet.app.ui.track;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.location.Location;
import android.os.Handler;
import android.provider.Settings;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.content.ContextCompat;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.PolylineOptions;
import com.kingwaytek.cpami.bykingTablet.AppController;
import com.kingwaytek.cpami.bykingTablet.R;
import com.kingwaytek.cpami.bykingTablet.app.service.TrackingService;
import com.kingwaytek.cpami.bykingTablet.app.ui.BaseMapActivity;
import com.kingwaytek.cpami.bykingTablet.hardware.MyLocationManager;
import com.kingwaytek.cpami.bykingTablet.utilities.DialogHelper;
import com.kingwaytek.cpami.bykingTablet.utilities.TrackingFileUtil;

/**
 * 軌跡錄製地圖
 *
 * @author Vincent (2016/7/14)
 */
public class UiTrackMapActivity extends BaseMapActivity {

    private LinearLayout gpsStateLayout;
    private TextView gpsStateText;
    private ProgressBar gpsStateCircle;

    private FloatingActionButton trackBtn;

    private BroadcastReceiver receiver;

    private Intent trackingServiceIntent;
    private LatLng preLatLng;

    private Context appContext() {
        return AppController.getInstance().getAppContext();
    }

    @Override
    protected void onMapReady() {

    }

    @Override
    public void onResume() {
        super.onResume();
        trackingServiceIntent = new Intent(this, TrackingService.class);
        gettingReceive();
        checkGpsAndServiceState();
    }

    @Override
    public void onPause() {
        super.onPause();
        stopReceiving();
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
                Intent intent = new Intent(TRACKING_BROADCAST_FOR_SERVICE);

                if (TrackingService.IS_TRACKING_REQUESTED) {
                    intent.putExtra(TRACKING_REQUEST_STARTING, false);
                    trackBtn.setImageResource(android.R.drawable.ic_media_play);
                    TrackingFileUtil.closeWriter();

                    showTrackingText(false);
                }
                else {
                    intent.putExtra(TRACKING_REQUEST_STARTING, true);
                    trackBtn.setImageResource(android.R.drawable.ic_media_pause);
                    TrackingFileUtil.cleanTrackingFile();
                }
                LocalBroadcastManager.getInstance(appContext()).sendBroadcast(intent);
            }
        });
    }

    @Override
    public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {

    }

    @Override
    protected void onLocateMyPosition(Location location) {

    }

    @Override
    protected void requestLocationUpdate() {
        // Do Nothing!
        Log.i(TAG, "requestLocationUpdate (Do Nothing)");
    }

    @Override
    protected void removeLocationUpdate() {
        // Do Nothing!
        Log.i(TAG, "removeLocationUpdate (Do Nothing)");
    }

    private void checkGpsAndServiceState() {
        if (MyLocationManager.isGpsDisabled()) {
            AppController.getInstance().removeLocationManager();

            if (isTrackingServiceRunning())
                stopService(trackingServiceIntent);

            DialogHelper.showGpsRequestDialog(this, new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialogInterface, int i) {
                    Intent intent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                    startActivity(intent);
                }
            });
        }
        else
            checkServiceAndSetButton();
    }

    private void checkServiceAndSetButton() {
        if (TrackingService.IS_TRACKING_REQUESTED)
            trackBtn.setImageResource(android.R.drawable.ic_media_pause);
        else
            trackBtn.setImageResource(android.R.drawable.ic_media_play);

        if (!isTrackingServiceRunning())
            startService(trackingServiceIntent);
    }

    private void gettingReceive() {
        if (receiver == null) {
            receiver = new BroadcastReceiver() {
                @Override
                public void onReceive(Context context, Intent intent) {
                    getIntentAndDoActions(intent);
                }
            };
        }
        LocalBroadcastManager.getInstance(appContext()).registerReceiver(receiver, new IntentFilter(TRACKING_BROADCAST_FOR_ACTIVITY));
    }

    private void stopReceiving() {
        if (notNull(receiver))
            LocalBroadcastManager.getInstance(appContext()).unregisterReceiver(receiver);
    }

    private void getIntentAndDoActions(Intent intent) {
        if (intent.hasExtra(TRACKING_IS_GPS_LOCATED)) {
            if (intent.getBooleanExtra(TRACKING_IS_GPS_LOCATED, false))
                onGpsLocated();
            else
                onGpsLocating();
        }

        if (intent.hasExtra(TRACKING_IS_DOING_RIGHT_NOW)) {
            showTrackingText(true);
            drawingPolyline((LatLng) intent.getParcelableExtra(TRACKING_IS_DOING_RIGHT_NOW));
        }
    }

    public void onGpsLocated() {
        gpsStateCircle.setVisibility(View.GONE);
        gpsStateText.setText(getString(R.string.track_gps_locate_done));

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                gpsStateLayout.setVisibility(View.GONE);
            }
        }, 3000);
    }

    public void onGpsLocating() {
        gpsStateLayout.setVisibility(View.VISIBLE);
        gpsStateText.setText(getString(R.string.track_gps_locating));
        gpsStateCircle.setVisibility(View.VISIBLE);
        showTrackingText(false);
    }

    private void drawingPolyline(LatLng newLatLng) {
        moveCamera(newLatLng);

        if (preLatLng == null)
            preLatLng = newLatLng;
        else {
            map.addPolyline(new PolylineOptions()
                    .add(preLatLng, newLatLng)
                    .color(ContextCompat.getColor(appContext(), R.color.md_blue_A700))
                    .width(20));

            preLatLng = newLatLng;
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (TrackingService.IS_TRACKING_REQUESTED)
            finish();
        else {
            stopService(trackingServiceIntent);
            showTrackingText(false);
            finish();
            Log.i(TAG, "StopService");
        }
        Log.i(TAG, "IsServiceRunning: " + isTrackingServiceRunning());
    }
}
