package com.kingwaytek.cpami.bykingTablet.app.track;

import java.util.EnumSet;
import java.util.HashMap;
import java.util.Map;

import android.content.Context;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.util.Log;

import com.sonavtek.sonav.MapView;
import com.sonavtek.sonav.sonav;
import com.kingwaytek.cpami.bykingTablet.app.ApplicationGlobal;
import com.kingwaytek.cpami.bykingTablet.sql.Track;
import com.kingwaytek.cpami.bykingTablet.sql.TrackPoint;

public class TrackEngine implements LocationListener {

	private LocationManager locManager;
	private String locProvider;
	private Location currentLocation;

	private static TrackEngine instance;
	private sonav engine; // eeego engine
	private Context context;

	private Track insTrack;
	private TrackRecordingStatus recordingStatus;

	private static final long minTime = 3000L;
	private static final float minDistance = 10.0f;

	public enum TrackRecordingStatus {

		IMPORTED(2), IMPORTING(-2), RECORDING(-1), STOPED(1), PAUSED(0);

		private static final Map<Integer, TrackRecordingStatus> statusMap = new HashMap<Integer, TrackRecordingStatus>();
		private final int owner;

		static {
			for (TrackRecordingStatus on : EnumSet
					.allOf(TrackRecordingStatus.class)) {
				statusMap.put(on.getValue(), on);
			}
		}

		TrackRecordingStatus(int owner) {
			this.owner = owner;
		}

		public int getValue() {
			return this.owner;
		}

		public static TrackRecordingStatus get(int value) {
			return statusMap.get(value);
		}
	}

	/**
	 * The instance of TrackRecord can only has one. Others should call
	 * getInstance() method to get the singleton.
	 */
	private TrackEngine() {
		// TODO Auto-generated constructor stub
	}

	/**
	 * gets current instance of TrackRecord, create one if it does not exist.
	 * 
	 * @return instance of PathFinder
	 */
	public static TrackEngine getInstance() {
		if (instance == null) {
			synchronized (TrackEngine.class) {
				if (instance == null) {
					instance = new TrackEngine();

					instance.setEngine(sonav.getInstance());
					instance.setRecordingStatus(TrackRecordingStatus.STOPED);
					instance.context = null;
					instance.locManager = null;
					instance.locProvider = "";
					instance.insTrack = null;

					Location loc = null;
					if (ApplicationGlobal.gpsListener != null) {
						instance.currentLocation = ApplicationGlobal.gpsListener
								.getLastLocation();
					}
				}
			}
		}
		return instance;
	}

	/**
	 * @return eeego_engine
	 */
	public sonav getEngine() {
		return engine;
	}

	/**
	 * sets eeego_engine.
	 * 
	 * @param engine
	 *            to set
	 */
	public void setEngine(sonav engine) {
		this.engine = engine;
	}

	public Context getContext() {
		return context;
	}

	/**
	 * must be called before start use this engine to record track
	 * 
	 * @param context
	 */
	public void InitializeGPS(Context context) {
		if (instance.context == null) {
			instance.context = context;
		}
		if (instance.locManager != null) {
			return;
		}
		instance.locManager = (LocationManager) instance.context
				.getSystemService(Context.LOCATION_SERVICE);
		Criteria locCriteria = new Criteria();
		locCriteria.setAccuracy(Criteria.ACCURACY_FINE);
		locCriteria.setAltitudeRequired(true);
		locCriteria.setBearingRequired(false);
		locCriteria.setPowerRequirement(Criteria.POWER_LOW);

		instance.locProvider = instance.locManager.getBestProvider(locCriteria,
				true);
	}

	public Track getTrack() {
		return insTrack;
	}

	public void setTrack(Track track) {
		this.insTrack = track;
	}

	public TrackRecordingStatus getRecordingStatus() {
		return recordingStatus;
	}

	public void setRecordingStatus(TrackRecordingStatus status) {
		this.recordingStatus = status;
	}

	public Location getCurrentLocation() {
		return this.currentLocation;
	}

	public void Start() {
		Log.i("TrackRecord", "record started.");
		// ApplicationGlobal.gpsTrackRecord.setEnabled(true);

		instance.setRecordingStatus(TrackRecordingStatus.RECORDING);
		instance.locManager.requestLocationUpdates(instance.locProvider,
				minTime, minDistance, this);

		// insert track
		insTrack.Record();
		Log.i("TrackEngine", "track id=" + insTrack.getID() + "name="
				+ insTrack.getName() + ", desp=" + insTrack.getDescription());
	}

	// TODO: implementation not been done
	public void Pause() {
		Log.i("TrackRecord", "record paused.");

		instance.setRecordingStatus(TrackRecordingStatus.PAUSED);
	}

	public void Stop() {
		Log.i("TrackRecord", "record thread stoped.");
		// ApplicationGlobal.gpsTrackRecord.setEnabled(false);

		instance.setRecordingStatus(TrackRecordingStatus.STOPED);
		instance.locManager.removeUpdates(this);

		// finish and finalize track db
		insTrack.setRecordingStatus(TrackRecordingStatus.STOPED);
		// insTrack.isRecording(false);
		insTrack.setEndTime();
		insTrack.Update();
	}

	// TODO: implementation not been done
	public static void Show(Context context, int trackID) {
		Log.i("TrackRecord", "show track on map.");

		try {
			TrackEngine.getInstance().setTrack(new Track(context, trackID));
		} catch (Exception e) {
			Log.w("TrackRecord", "setTrack Failed. " + e);
			return;
		}

		// Play
		Map<Integer, TrackPoint> tPoints = instance.getTrack().getTrackPoints();

		if (tPoints == null || tPoints.size() <= 0) {
			Log.w("TrackRecord", "no track points to show. ");
			return;
		}

		instance.getEngine().newspxy(tPoints.size());
		for (TrackPoint tp : tPoints.values()) {
			instance.getEngine().addspxy(tp.getLongitude(), tp.getLatitude());
		}
		instance.getEngine().drawspxy(MapView.SHOW_MANUAL_ROUTE);
		instance.getEngine().gomap(tPoints.get(0).getLongitude(),
				tPoints.get(0).getLatitude(), 1);
	}

	@Override
	public void onLocationChanged(Location location) {
		// TODO Auto-generated method stub
		Log.i("TrackEngine", "location Changed.");
		if (location == null) {
			location = instance.locManager
					.getLastKnownLocation(instance.locProvider);
		}
		instance.currentLocation = location;

		DoRecord();
	}

	@Override
	public void onProviderDisabled(String provider) {
		// TODO Auto-generated method stub
		Log.i("TrackEngine", "location Provider Disabled.");
	}

	@Override
	public void onProviderEnabled(String provider) {
		// TODO Auto-generated method stub
		Log.i("TrackEngine", "location Provider Enabled.");
	}

	@Override
	public void onStatusChanged(String provider, int status, Bundle extras) {
		// TODO Auto-generated method stub
		Log.i("TrackEngine", "location Provider status Changed.");
	}

	private void DoRecord() {
		TrackPoint tp = new TrackPoint(context);
		tp.setID(insTrack.getID());
		tp.setLongitude(instance.currentLocation.getLongitude());
		tp.setLatitude(instance.currentLocation.getLatitude());
		tp.setAltitude(instance.currentLocation.getAltitude());
		tp.setType(1); // what is this for?
		long pinResult = tp.Pin();

		Log.i("TrackEngine",
				"pin point result=" + pinResult + ", id=" + tp.getID()
						+ ", lon=" + tp.getLongitude() + ", lat="
						+ tp.getLatitude() + ", alt=" + tp.getAltitude());
	}
}
