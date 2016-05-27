package com.kingwaytek.cpami.bykingTablet.hardware;

import android.content.Context;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.util.Log;

import com.google.android.gms.maps.model.LatLng;
import com.kingwaytek.cpami.bykingTablet.AppController;
import com.sonavtek.sonav.GPSDATA;

import java.lang.ref.WeakReference;
import java.util.List;

/**
 * GPSListener is SUCKS!!!
 * This one is BETTER!
 *
 * @author Vincent (vincent.chang@kingwaytek.com)
 */
public class MyLocationManager implements LocationListener {

    private static final String TAG = "MyLocationManager";

    private static final LatLng DEFAULT_LOCATION = new LatLng(24.993413, 121.301028);

    private static final long UPDATE_POSITION_TIME = 5000;
    private static final int UPDATE_POSITION_METERS = 1;

    private static WeakReference<LocationManager> locManager;

    private static boolean isProviderFromGps;

    private static Context appContext() {
        return AppController.getInstance().getAppContext();
    }

    public MyLocationManager() {
        LocationManager locationManager = getLocationManager();
        getProvidersAndUpdate(locationManager);
    }

    public static LocationManager getLocationManager() {
        if (locManager == null || locManager.get() == null)
            locManager = new WeakReference<>((LocationManager) appContext().getSystemService(Context.LOCATION_SERVICE));
        return locManager.get();
    }

    public void getProvidersAndUpdate(LocationManager locationManager) {
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

                if (location != null)
                    Log.i(TAG, "BestProvider: " + location.getProvider() + " Lat: " + location.getLatitude() + " Lng: " + location.getLongitude());
            }

            if (location == null) {
                List<String> providers = locationManager.getProviders(getCriteria(), true);
                Log.i(TAG, "FirstProvider: " + providers);

                if (providers != null) {
                    for (String provider : providers) {
                        location = locationManager.getLastKnownLocation(provider);

                        if (location != null) {
                            Log.i(TAG, "FirstProvider: " + location.getProvider() + " Lat: " + location.getLatitude() + " Lng: " + location.getLongitude());
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

        //sonav.getInstance().setgpsvalue(getEngineData(location));
        //sonav.getInstance().setflagpoint(MapView.USER_LOCATION_POINT, location.getLongitude(), location.getLatitude());

        if (location.getProvider().equals(LocationManager.GPS_PROVIDER)) {
            isProviderFromGps = true;
            Log.i(TAG, "ProviderFromGPS!!!");
        }
        else {
            isProviderFromGps = false;
            Log.i(TAG, "ProviderIs: " + location.getProvider());
        }
    }

    @Override
    public void onStatusChanged(String provider, int status, Bundle extras) {

    }

    @Override
    public void onProviderEnabled(String provider) {

    }

    @Override
    public void onProviderDisabled(String provider) {

    }

    /**
     * Transform the location to the data for engine.
     *
     * @param loc The location.
     * @return The data for engine.
     */
    private GPSDATA getEngineData(Location loc) {
        GPSDATA gpsData = new GPSDATA();

        gpsData.setSatelliteNumber(3);
        gpsData.setLongitude(loc.getLongitude());
        gpsData.setLatitude(loc.getLatitude());
        gpsData.setSpeed(loc.getSpeed() == 0 ? 1 : loc.getSpeed() * 3.6);
        gpsData.setAltitude(loc.getAltitude());
        gpsData.setBearing((int) loc.getBearing());
        gpsData.setTime(loc.getTime());

        return gpsData;
    }
}
