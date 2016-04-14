package com.kingwaytek.cpami.bykingTablet;

import android.app.AlarmManager;
import android.app.Application;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.Handler;
import android.util.Log;

import com.google.android.gms.analytics.ExceptionReporter;
import com.google.android.gms.analytics.Tracker;
import com.kingwaytek.cpami.bykingTablet.hardware.MyLocationManager;

/**
 * Created by vincent.chang on 2016/4/14.
 */
public class AppController extends Application {

    private static AppController appInstance;
    private MyLocationManager locationManager;

    //private RequestQueue mQueue;
    //ImageLoader imageLoader;

    @Override
    public void onCreate() {
        super.onCreate();
        appInstance = this;
        //SettingManager.initPreferences();
        //SettingManager.SystemSetting.initSettingPreference();
    }

    public static synchronized AppController getInstance() {
        return appInstance;
    }

    public Context getAppContext() {
        return getApplicationContext();
    }

    public MyLocationManager getLocationManager() {
        if (locationManager == null)
            return new MyLocationManager();
        else
            return locationManager;
    }

/*
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
*/

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

    private void restartAppImmediately() {
        PendingIntent pendingIntent = PendingIntent.getActivity(getBaseContext(), 0, new Intent(getIntent()), PendingIntent.FLAG_ONE_SHOT);
        AlarmManager am = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
        am.set(AlarmManager.RTC, System.currentTimeMillis(), pendingIntent);
        System.exit(2);
    }

    private Intent getIntent() {
        Intent intent = getBaseContext().getPackageManager().getLaunchIntentForPackage(getBaseContext().getPackageName());
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
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
