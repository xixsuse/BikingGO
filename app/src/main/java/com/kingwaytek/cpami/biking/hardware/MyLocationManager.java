package com.kingwaytek.cpami.biking.hardware;

import android.content.Context;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.util.Log;

import com.google.android.gms.maps.model.LatLng;
import com.kingwaytek.cpami.biking.AppController;
import com.kingwaytek.cpami.biking.callbacks.OnGpsLocateCallBack;
import com.kingwaytek.cpami.biking.utilities.SettingManager;
import com.kingwaytek.cpami.biking.utilities.TrackingFileUtil;
import com.kingwaytek.cpami.biking.utilities.Utility;

import java.lang.ref.WeakReference;
import java.util.List;

/**
 * Manage location update, mainly for UiMainMapActivity and UiTrackMapActivity.
 *
 * @author Vincent (vincent.chang@kingwaytek.com)
 */
public class MyLocationManager implements LocationListener {

    private static final String TAG = "MyLocationManager";

    private static final LatLng DEFAULT_LOCATION = new LatLng(25.026794, 121.522413);

    private static final long UPDATE_POSITION_TIME = 5000;
    private static final float UPDATE_POSITION_METERS = 1;

    private static long GPS_UPDATE_TIME;
    private static float GPS_UPDATE_DISTANCE;

    private static WeakReference<LocationManager> locManager;

    private static boolean isProviderFromGps;

    private OnGpsLocateCallBack gpsLocateCallBack;
    private boolean detectGpsLocateState;
    private boolean isTrackingMode;

    private boolean startTracking;
    private boolean isGpsLocated;
    private Location lastLocation;
    private static final float TRACKING_MINI_DISTANCE = 8;

    private static Context appContext() {
        return AppController.getInstance().getAppContext();
    }

    public MyLocationManager() {
        getProvidersAndUpdate();
    }

    public MyLocationManager(long updateTimeDuration, float updateDistanceDuration, OnGpsLocateCallBack gpsLocateCallBack) {
        GPS_UPDATE_TIME = updateTimeDuration;
        GPS_UPDATE_DISTANCE = updateDistanceDuration;
        this.gpsLocateCallBack = gpsLocateCallBack;
        isTrackingMode = true;

        setGPSUpdateRequest();
    }

    private static LocationManager getLocationManager() {
        if (locManager == null || locManager.get() == null)
            locManager = new WeakReference<>((LocationManager) appContext().getSystemService(Context.LOCATION_SERVICE));
        return locManager.get();
    }

    public void getProvidersAndUpdate() {
        LocationManager locationManager = getLocationManager();
        List<String> providers = locationManager.getAllProviders();

        for (String provider : providers) {
            addUpdateRequest(locationManager, provider);
            Log.i(TAG, "Provider added: " + provider);
        }
    }

    private void addUpdateRequest(LocationManager locationManager, String provider) {
        try {
            locationManager.requestLocationUpdates(provider, UPDATE_POSITION_TIME, UPDATE_POSITION_METERS, this);
        }
        catch (SecurityException e) {
            e.printStackTrace();
        }
    }

    private void setGPSUpdateRequest() {
        detectGpsLocateState = true;
        try {
            if (isProviderFromGps) {
                getLocationManager().requestLocationUpdates(LocationManager.GPS_PROVIDER, GPS_UPDATE_TIME, GPS_UPDATE_DISTANCE, this);
                getLocationManager().requestLocationUpdates(LocationManager.NETWORK_PROVIDER, GPS_UPDATE_TIME, GPS_UPDATE_DISTANCE, this);
                Log.i(TAG, "requestTime: " + GPS_UPDATE_TIME + " requestDistance: " + GPS_UPDATE_DISTANCE);
            }
            else {
                getLocationManager().requestLocationUpdates(LocationManager.GPS_PROVIDER, 1000, 0, this);
                getLocationManager().requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 1000, 0, this);
            }
        }
        catch (SecurityException e) {
            e.printStackTrace();
        }
    }

    /**
     * 如果沒定到任何位置，或手機上的"位置"沒打開，或 SettingManager中的 GpsEnable == false，
     * 都將直接使用 DEFAULT_LOCATION
     */
    public static Location getLastLocation() {
        Location location = null;
        try {
            LocationManager locationManager = getLocationManager();

            String bestProvider = locationManager.getBestProvider(getCriteria(), true);
            Log.i(TAG, "BestProvider: " + bestProvider);

            if (bestProvider != null) {
                location = locationManager.getLastKnownLocation(bestProvider);

                if (location != null) {
                    Log.i(TAG, "BestProvider: " + location.getProvider() + " Lat: " + location.getLatitude() + " Lng: " + location.getLongitude());
                    Log.i(TAG, "Altitude: " + location.getAltitude());
                }
            }

            if (location == null) {
                List<String> providers = locationManager.getProviders(getCriteria(), true);
                Log.i(TAG, "FirstProvider: " + providers);

                if (providers != null) {
                    for (String provider : providers) {
                        location = locationManager.getLastKnownLocation(provider);

                        if (location != null) {
                            Log.i(TAG, "FirstProvider: " + location.getProvider() + " Lat: " + location.getLatitude() + " Lng: " + location.getLongitude());
                            Log.i(TAG, "Altitude: " + location.getAltitude());
                            break;
                        }
                    }
                }
            }

            if (location == null) {
                location = locationManager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);
                Log.i(TAG, "Location Is Null! Using Network");
            }

            if (location == null) {
                location = locationManager.getLastKnownLocation(LocationManager.PASSIVE_PROVIDER);
                Log.i(TAG, "Location Is Null! Using Passive");
            }

            if (location == null) {
                location = new Location(LocationManager.PASSIVE_PROVIDER);
                location.setLatitude(DEFAULT_LOCATION.latitude);
                location.setLongitude(DEFAULT_LOCATION.longitude);
                Log.i(TAG, "Location Is Null! Using Default location");
            }
        }
        catch (SecurityException e) {
            e.printStackTrace();
        }

        return location;
    }

    private static Criteria getCriteria() {
        Criteria criteria = new Criteria();

        if (isProviderFromGps)
            criteria.setAccuracy(Criteria.ACCURACY_FINE);
        else
            criteria.setAccuracy(Criteria.ACCURACY_COARSE);

        criteria.setPowerRequirement(Criteria.POWER_LOW);
        criteria.setCostAllowed(true);
        criteria.setAltitudeRequired(false);
        criteria.setBearingRequired(false);
        criteria.setSpeedRequired(false);
        return criteria;
    }

    public void removeUpdate() {
        try {
            getLocationManager().removeUpdates(this);
            setGpsIsLocated(false);
            detectGpsLocateState = false;
        }
        catch (SecurityException e) {
            e.printStackTrace();
        }
    }

    public static boolean isGpsDisabled() {
        return !getLocationManager().isProviderEnabled(LocationManager.GPS_PROVIDER);
    }

    public static boolean isLocationEnabled() {
        return getLocationManager().isProviderEnabled(LocationManager.GPS_PROVIDER) ||
                getLocationManager().isProviderEnabled(LocationManager.NETWORK_PROVIDER);
    }

    @Override
    public void onLocationChanged(Location location) {
        Log.i(TAG, "onLocationChanged!!!");

        if (location.getProvider().equals(LocationManager.GPS_PROVIDER)) {
            isProviderFromGps = true;
            Log.i(TAG, "ProviderFromGPS!!!");
        }
        else {
            isProviderFromGps = false;
            detectGpsLocateState = true;
            Log.i(TAG, "ProviderIs: " + location.getProvider());
        }

        //Log.i(TAG, "GpsCallBackIsNotNull: " + (gpsLocateCallBack != null) + " detectGpsLocateState: " + detectGpsLocateState);

        if (gpsLocateCallBack != null && detectGpsLocateState) {
            if (isProviderFromGps) {
                setGPSUpdateRequest();
                gpsLocateCallBack.onGpsLocated();
                detectGpsLocateState = false;

                setGpsIsLocated(true);
                Log.i(TAG, "GPS CallBack: Located!");
            }
            else {
                gpsLocateCallBack.onGpsLocating();
                setGpsIsLocated(false);
                Log.i(TAG, "GPS CallBack: Locating...");
            }
        }

        if (isTrackingMode)
            tracking(location);
    }

    @Override
    public void onStatusChanged(String provider, int status, Bundle extras) {

    }

    @Override
    public void onProviderEnabled(String provider) {
        Utility.showToastOnNewThread("ProviderEnabled: " + provider);
    }

    @Override
    public void onProviderDisabled(String provider) {
        Utility.showToastOnNewThread("ProviderDisabled: " + provider);
    }


    private void setGpsIsLocated(boolean isLocated) {
        this.isGpsLocated = isLocated;
    }

    public boolean isGpsLocated() {
        return isGpsLocated;
    }

    public void setStartTracking(boolean startTracking) {
        this.startTracking = startTracking;
    }

    public void tracking(Location location) {
        if (isGpsLocated && startTracking) {
            if (lastLocation == null) {
                lastLocation = location;
            }
            else if (location.distanceTo(lastLocation) >= TRACKING_MINI_DISTANCE) {
                TrackingFileUtil.writeLocationTrackingFile(location.getLatitude(), location.getLongitude());
                gpsLocateCallBack.onLocationWritten(new LatLng(location.getLatitude(), location.getLongitude()));

                lastLocation = location;
                //Utility.showToastOnNewThread("Location written!");
                Log.i(TAG, "Location Written!!!");
            }

            if (SettingManager.TrackingTimeAndLayer.getStartTime() == 0)
                SettingManager.TrackingTimeAndLayer.setStartTime(System.currentTimeMillis());
        }
    }

    public boolean isTrackingRightNow() {
        return isGpsLocated && startTracking;
    }
}