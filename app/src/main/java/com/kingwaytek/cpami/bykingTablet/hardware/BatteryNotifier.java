package com.kingwaytek.cpami.bykingTablet.hardware;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.util.Log;
import android.widget.Toast;

import com.kingwaytek.cpami.bykingTablet.R;
import com.kingwaytek.cpami.bykingTablet.app.track.TrackEngine;
import com.kingwaytek.cpami.bykingTablet.app.track.TrackEngine.TrackRecordingStatus;

public class BatteryNotifier extends BroadcastReceiver {

	private static BatteryNotifier instance;
	private boolean isRegisted;
	private int batteryLevel;

	public static void Register(Context context) {
		if (instance == null) {
			synchronized (BatteryNotifier.class) {
				if (instance == null) {
					instance = new BatteryNotifier();
					instance.isRegisted = false;
				}
			}
		}
		context.registerReceiver(instance, new IntentFilter(
				Intent.ACTION_BATTERY_CHANGED));
		instance.isRegisted = true;
		Log.i("BatteryNotifier", "Notifier Registed.");
	}

	public static void UnRegister(Context context) {
		if (instance == null || !instance.isRegisted) {
			return;
		}
		context.unregisterReceiver(instance);
		instance.isRegisted = false;
		Log.i("BatteryNotifier", "Notifier unRegisted.");
	}

	/**
	 * Constructor
	 * 
	 * @param context
	 *            context that this notifier resides on.
	 */
	private BatteryNotifier() {
		// TODO Auto-generated constructor stub

	}

	public static BatteryNotifier getInstance() throws NullPointerException {
		return instance;
	}

	public boolean isNotifierRegisted() {
		return isRegisted;
	}

	public int getBatteryLevel() {
		return batteryLevel;
	}

	@Override
	public void onReceive(Context context, Intent intent) {
		// TODO Auto-generated method stub
		Log.i("BatteryNotifier", "Received:" + context.getClass().getName());

		// broadcast when battery changed
		if (intent.getAction().equals(Intent.ACTION_BATTERY_CHANGED)) {
			instance.batteryLevel = intent.getIntExtra("level", 10);
			Log.i("BatteryNotifier", "Battery Change Received:"
					+ instance.batteryLevel);

			onBatteryChanged(context);
		}
	}

	private void onBatteryChanged(final Context context) {
		if (batteryLevel <= 15 && batteryLevel >= 10) {
			TrackEngine tengine = TrackEngine.getInstance();
			if (!tengine.getRecordingStatus().equals(
					TrackRecordingStatus.STOPED)) {
				Toast
						.makeText(
								context,
								context
										.getString(R.string.track_record_battery_low_warnning_text),
								Toast.LENGTH_LONG).show();
			}
		}
		if (batteryLevel < 10) {

			// handles when a track is recording.
			TrackEngine tengine = TrackEngine.getInstance();
			if (!tengine.getRecordingStatus().equals(
					TrackRecordingStatus.STOPED)) {
				tengine.Stop();
				Toast
						.makeText(
								context,
								context
										.getString(R.string.track_record_battery_low_auto_stop_text),
								Toast.LENGTH_LONG).show();
			}
		}
	}
}
