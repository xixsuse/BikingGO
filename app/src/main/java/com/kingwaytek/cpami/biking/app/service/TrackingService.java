package com.kingwaytek.cpami.biking.app.service;

import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;

import com.google.android.gms.maps.model.LatLng;
import com.kingwaytek.cpami.biking.AppController;
import com.kingwaytek.cpami.biking.R;
import com.kingwaytek.cpami.biking.app.model.CommonBundle;
import com.kingwaytek.cpami.biking.callbacks.OnGpsLocateCallBack;
import com.kingwaytek.cpami.biking.hardware.MyLocationManager;
import com.kingwaytek.cpami.biking.utilities.Utility;

/**
 * Created by vincent.chang on 2016/7/15.
 */
public class TrackingService extends Service implements OnGpsLocateCallBack, CommonBundle {

    private static final String TAG = "TrackingService";

    private static final long GPS_TRACKING_TIME = 1000L;
    private static final float GPS_TRACKING_DISTANCE = 2.0f;

    private Thread thread;

    private BroadcastReceiver receiver;

    public static boolean IS_TRACKING_REQUESTED;

    private Intent trackingIntent;

    private Context appContext() {
        return AppController.getInstance().getAppContext();
    }

    private MyLocationManager trackManager() {
        return AppController.getInstance().getTrackManager();
    }

    @Override
    public void onCreate() {
        super.onCreate();
        AppController.getInstance().initTrackManager(GPS_TRACKING_TIME, GPS_TRACKING_DISTANCE, this);
        gettingReceive();
        Log.i(TAG, "Service onCreate!!!");
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        thread = new Thread(new Runnable() {
            @Override
            public void run() {
                while (!thread.isInterrupted()) {
                    try {
                        Thread.sleep(10000);
                        Log.i(TAG, "ServiceRunning~");
                    }
                    catch (InterruptedException e) {
                        e.printStackTrace();
                        break;
                    }
                }
            }
        });
        thread.start();

        return super.onStartCommand(intent, flags, startId);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        thread.interrupt();
        stopSelf();
        trackManager().removeUpdate();
        stopReceiving();
        IS_TRACKING_REQUESTED = false;
        AppController.getInstance().releaseTrackManager();

        Log.i(TAG, "Service onDestroy!!!");
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onGpsLocated() {
        Intent intent = new Intent(TRACKING_BROADCAST_FOR_ACTIVITY);
        intent.putExtra(TRACKING_IS_GPS_LOCATED, true);

        LocalBroadcastManager.getInstance(appContext()).sendBroadcast(intent);
    }

    @Override
    public void onGpsLocating() {
        Intent intent = new Intent(TRACKING_BROADCAST_FOR_ACTIVITY);
        intent.putExtra(TRACKING_IS_GPS_LOCATED, false);

        LocalBroadcastManager.getInstance(appContext()).sendBroadcast(intent);
    }

    @Override
    public void onLocationWritten(LatLng latLng) {
        if (trackingIntent == null)
            trackingIntent = new Intent(TRACKING_BROADCAST_FOR_ACTIVITY);

        trackingIntent.putExtra(TRACKING_IS_DOING_RIGHT_NOW, latLng);

        LocalBroadcastManager.getInstance(appContext()).sendBroadcast(trackingIntent);
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
        LocalBroadcastManager.getInstance(appContext()).registerReceiver(receiver, new IntentFilter(TRACKING_BROADCAST_FOR_SERVICE));
    }

    private void stopReceiving() {
        if (receiver != null)
            LocalBroadcastManager.getInstance(appContext()).unregisterReceiver(receiver);
    }

    private void getIntentAndDoActions(Intent intent) {
        if (intent.hasExtra(TRACKING_REQUEST_STARTING)) {
            IS_TRACKING_REQUESTED = intent.getBooleanExtra(TRACKING_REQUEST_STARTING, false);

            trackManager().setStartTracking(IS_TRACKING_REQUESTED);

            if (IS_TRACKING_REQUESTED) {
                if (trackManager().isGpsLocated())
                    trackManager().tracking(MyLocationManager.getLastLocation());
                else {
                    Utility.showToastOnNewThread(getString(R.string.track_waiting_for_gps_located));
                    trackManager().onLocationChanged(MyLocationManager.getLastLocation());
                }
            }
        }
    }
}