package com.kingwaytek.cpami.bykingTablet.app.service;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.util.Log;

/**
 * Created by vincent.chang on 2016/7/15.
 */
public class TrackingService extends Service {

    private static final String TAG = "TrackingService";

    @Override
    public void onCreate() {
        super.onCreate();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                while (true) {
                    try {
                        Thread.sleep(2000);
                        Log.i(TAG, "ServiceRunning~");
                    }
                    catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            }
        }).start();


        return super.onStartCommand(intent, flags, startId);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }
}
