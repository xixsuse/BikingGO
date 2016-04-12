package com.kingwaytek.cpami.bykingTablet.hardware;

import android.content.Context;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.location.LocationProvider;
import android.os.Bundle;
import android.util.Log;

import com.sonavtek.sonav.GPSDATA;
import com.sonavtek.sonav.MapView;
import com.sonavtek.sonav.sonav;

/**
 * 使用者定位監聽器
 * 
 * @author Harvey Cheng(harvey@kingwaytek.com)
 */
public class GPSListener implements LocationListener {

	private static LocationManager manager;
	private static String provider;
	private static boolean gpsServiceAvailable;
	private boolean enabled;
	private int minTime;
	private int minDistance;
	private Location lastLocation = null;
	public static double lon;
	public static double lat;

	/**
	 * Create new instance of GPSListener.
	 * 
	 * @param ctx
	 *            The instance of Context.
	 * @param minTime
	 *            The minimum time interval for notifications, in milliseconds.
	 *            This field is only used as a hint to conserve power, and
	 *            actual time between location updates may be greater or lesser
	 *            than this value.
	 * @param minDistance
	 *            The minimum distance interval for notifications, in meters
	 */
	public GPSListener(Context ctx, int minTime, int minDistance) {
		Log.i("GPSListener.java", "Enter GPSListener");
		this.minTime = minTime;
		this.minDistance = minDistance;

		// get the instance of LocationManager and provider if not exist
		if (manager == null) {
			synchronized (GPSListener.class) {
				if (manager == null) {
					manager = (LocationManager) ctx
							.getSystemService(Context.LOCATION_SERVICE);

					provider = manager.getBestProvider(new Criteria(), true);
				}
			}
		}
	}

	public void onLocationChanged(Location loc) {
		boolean identified = false;

		if (loc != null) {
			Log.d(getClass().toString(), "onLocationChanged=" + loc
					+ ", extra=" + loc.getExtras());

			identified = true;
			lon = loc.getLongitude();
			lat = loc.getLatitude();
			Log.i("GPSListener.java", "loc.getLongitude=" + lon + " , "
					+ "loc.getLatitude()=" + lat);
		} else {
			Log.w(getClass().toString(), "getLastKnownLocation");

			loc = manager.getLastKnownLocation(provider);
		}

		if (loc != null) {
			sonav engine = sonav.getInstance();

			// record the last known location
			lastLocation = loc;

			// send to engine
			engine.setgpsvalue(toEngineData(loc, identified));

			// 設定使用者的位置旗標
			engine.setflagpoint(MapView.USER_LOCATION_POINT,
					loc.getLongitude(), loc.getLatitude());
		}

	}

	public void onProviderDisabled(String provider) {
		Log.d(getClass().toString(), "onProviderDisabled: " + provider);
	}

	public void onProviderEnabled(String provider) {
		Log.d(getClass().toString(), "onProviderEnabled: " + provider);
	}

	public void onStatusChanged(String provider, int status, Bundle extras) {
		Log.d(getClass().toString(), "onStatusChanged: provider=" + provider
				+ ", status=" + status + ", extras=" + extras);

		gpsServiceAvailable = status == LocationProvider.AVAILABLE;
	}

	/**
	 * Check if this listener is enabled or disabled.
	 * 
	 * @return True if enabled or false instead.
	 */
	public boolean isEnabled() {
		return enabled;
	}

	/**
	 * Enable/disable this listener.
	 * 
	 * @param enabled
	 *            Set true if to enable or false to disable.
	 */
	public synchronized void setEnabled(boolean enabled) {
		this.enabled = enabled;

		if (enabled && provider != null) {
			manager.requestLocationUpdates(provider, minTime, minDistance, this);
		} else {
			manager.removeUpdates(this);
		}
	}

	public static LocationManager getManager() {
		return manager;
	}

	public static String getProvider() {
		return provider;
	}

	public static void setProvider(String provider) {
		GPSListener.provider = provider;
	}

	public int getMinTime() {
		return minTime;
	}

	public void setMinTime(int minTime) {
		this.minTime = minTime;
	}

	public int getMinDistance() {
		return minDistance;
	}

	public void setMinDistance(int minDistance) {
		this.minDistance = minDistance;
	}

	/**
	 * Get the last known location.
	 * 
	 * @return The last known location.
	 */
	public Location getLastLocation() {
		return lastLocation;
	}

	/**
	 * Check if the GPS service is available or not.
	 * 
	 * @return True if available or false instead.
	 */
	public static boolean isGpsServiceAvailable() {
		return gpsServiceAvailable;
	}

	/**
	 * Transform the location to the data for engine.
	 * 
	 * @param loc
	 *            The location.
	 * @param identified
	 *            The location is identified or not.
	 * @return The data for engine.
	 */
	public GPSDATA toEngineData(Location loc, boolean identified) {
		GPSDATA gpsData = new GPSDATA();

		gpsData.setSatelliteNumber(identified ? 3 : 2);
		gpsData.setLongitude(loc.getLongitude());
		gpsData.setLatitude(loc.getLatitude());
		gpsData.setSpeed(loc.getSpeed() == 0 ? 1 : loc.getSpeed() * 3.6);
		gpsData.setAltitude(loc.getAltitude());
		gpsData.setBearing((int) loc.getBearing());
		gpsData.setTime(loc.getTime());

		return gpsData;
	}
}