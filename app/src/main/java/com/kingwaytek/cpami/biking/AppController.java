package com.kingwaytek.cpami.biking;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.Handler;
import android.support.multidex.MultiDex;
import android.support.multidex.MultiDexApplication;
import android.util.Log;

import com.android.volley.RequestQueue;
import com.android.volley.toolbox.ImageLoader;
import com.android.volley.toolbox.Volley;
import com.facebook.FacebookSdk;
import com.facebook.appevents.AppEventsLogger;
import com.google.android.gms.analytics.ExceptionReporter;
import com.google.android.gms.analytics.Tracker;
import com.kingwaytek.cpami.biking.callbacks.OnGpsLocateCallBack;
import com.kingwaytek.cpami.biking.hardware.MyLocationManager;
import com.kingwaytek.cpami.biking.utilities.BitmapCache;
import com.kingwaytek.cpami.biking.utilities.SettingManager;

/**
 * The global controller for application level.
 *
 * @author Vincent (2016/4/14)
 */
public class AppController extends MultiDexApplication {

    private static AppController appInstance;
    private MyLocationManager locationManager;
    private MyLocationManager trackManager;

    private RequestQueue mQueue;
    private ImageLoader imageLoader;

    @Override
    protected void attachBaseContext(Context base) {
        super.attachBaseContext(base);
        MultiDex.install(this);
    }

    @Override
    public void onCreate() {
        super.onCreate();
        appInstance = this;
        SettingManager.initPreferences();
        initFacebookSDK();
    }

    public static synchronized AppController getInstance() {
        return appInstance;
    }

    public Context getAppContext() {
        return getApplicationContext();
    }

    private void initFacebookSDK() {
        FacebookSdk.sdkInitialize(getApplicationContext());
        AppEventsLogger.activateApp(this);
    }

    public void initLocationManager() {
        if (locationManager == null)
            locationManager = new MyLocationManager();
        else
            locationManager.getProvidersAndUpdate();
    }

    public void removeLocationManager() {
        if (locationManager != null)
            locationManager.removeUpdate();
    }

    public MyLocationManager getLocationManager() {
        if (locationManager != null)
            return locationManager;
        else
            return null;
    }

    public void initTrackManager(long updateTime, float updateDistance, OnGpsLocateCallBack gpsCallBack) {
        trackManager = new MyLocationManager(updateTime, updateDistance, gpsCallBack);
    }

    public MyLocationManager getTrackManager() {
        if (trackManager != null)
            return trackManager;
        return null;
    }

    public void releaseTrackManager() {
        trackManager = null;
    }

    public String getDataVersion() {
        return getString(R.string.DataVersion);
    }

    public RequestQueue getRequestQueue() {
        if (mQueue == null)
            mQueue = Volley.newRequestQueue(getApplicationContext());
        return mQueue;
    }

    public ImageLoader getImageLoader() {
        if (imageLoader == null)
            imageLoader = new ImageLoader(getRequestQueue(), new BitmapCache());
        return imageLoader;
    }

    private Thread.UncaughtExceptionHandler getExceptionHandler(Tracker tracker) {
        MyExceptionHandler myExceptionHandler = new MyExceptionHandler(Thread.getDefaultUncaughtExceptionHandler());
        return new ExceptionReporter(tracker, myExceptionHandler, this);
    }

    public void restartAppWithDelayedTime(long delayTime) {
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                PendingIntent pendingIntent = PendingIntent.getActivity(getBaseContext(), 0, new Intent(getIntent()), PendingIntent.FLAG_ONE_SHOT);
                AlarmManager am = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
                am.set(AlarmManager.RTC, System.currentTimeMillis(), pendingIntent);
                System.exit(2);
            }
        }, delayTime);
    }

    public void restartAppImmediately() {
        PendingIntent pendingIntent = PendingIntent.getActivity(getBaseContext(), 0, new Intent(getIntent()), PendingIntent.FLAG_ONE_SHOT);
        AlarmManager am = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
        am.set(AlarmManager.RTC, System.currentTimeMillis(), pendingIntent);
        System.exit(2);
    }

    private Intent getIntent() {
        Intent intent = getBaseContext().getPackageManager().getLaunchIntentForPackage(getBaseContext().getPackageName());
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        return intent;
    }

    private class MyExceptionHandler implements Thread.UncaughtExceptionHandler {
        private Thread.UncaughtExceptionHandler oldHandler;

        MyExceptionHandler(Thread.UncaughtExceptionHandler oldHandler) {
            this.oldHandler = oldHandler;
        }

        @Override
        public void uncaughtException(Thread thread, Throwable throwable) {
            Log.e("EXCEPTION", "Caught exception: " + throwable.getClass().getName() + ": " + throwable.getMessage());
            //restartAppImmediately();
            oldHandler.uncaughtException(thread, throwable);
        }
    }
}
