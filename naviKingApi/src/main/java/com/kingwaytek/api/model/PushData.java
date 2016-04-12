package com.kingwaytek.api.model;

import android.os.Bundle;

import com.kingwaytek.api.utility.UtilityApi;

/**
 * PushData
 * 
 * @author CalvinHuang
 * 
 */
public class PushData {
	private static final String TAG = "PushData";

	private AlertData alertData;
	private String badge;
	private String sound;
	private LKPushData lKPushData;

	public PushData() {
		super();
	}

	public PushData(AlertData _alertData, String _badge, String _sound, LKPushData _lKPushData) {
		alertData = _alertData;
		badge = _badge;
		sound = _sound;
		lKPushData = _lKPushData;
	}

	public AlertData getAlertData() {
		return alertData;
	}

	public String getBadge() {
		return badge;
	}

	public LKPushData getLKPushData() {
		return lKPushData;
	}

	public String getSound() {
		return sound;
	}

	public static final String BUNDLE_KEY_ALERT = "alert";
	public static final String BUNDLE_KEY_BADGE = "badge";
	public static final String BUNDLE_KEY_SOUND = "sound";
	public static final String BUNDLE_KEY_DATA = "data";
	public static PushData parsingData(Bundle _bundle) {
		String badgeTemp = _bundle.getString(BUNDLE_KEY_BADGE);
		String soundTemp = _bundle.getString(BUNDLE_KEY_SOUND);
		AlertData alertDataTemp = new AlertData();
		if (UtilityApi.checkStringNotEmpty(_bundle.getString(BUNDLE_KEY_ALERT))) {
			alertDataTemp = AlertData.parsingData(_bundle.getString(BUNDLE_KEY_ALERT));
		}
		LKPushData lKPushDataTemp = new LKPushData();
		if (UtilityApi.checkStringNotEmpty(_bundle.getString(BUNDLE_KEY_DATA))) {
			lKPushDataTemp = LKPushData.parsingData(_bundle.getString(BUNDLE_KEY_DATA));
		}
		PushData pushData = new PushData(alertDataTemp, badgeTemp, soundTemp, lKPushDataTemp);
		return pushData;
	}

}