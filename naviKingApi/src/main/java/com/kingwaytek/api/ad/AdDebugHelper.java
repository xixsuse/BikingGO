package com.kingwaytek.api.ad;

import android.content.Context;
import android.util.Log;
import android.widget.Toast;

public class AdDebugHelper {

	static String TAG = "AdDebugHelper";

	private static boolean bDeveloperMode = false;

	public final static boolean IS_USE_WIFI_TEST_SERVICE = false; // 內網

	public static boolean checkOpen() {
		return bDeveloperMode;
	}

	public static void debugLog(boolean isDebug, String TAG, String message) {
		if (isDebug) {
			debugLog(TAG, message);
		}
	}

	public static void debugLog(String TAG, String message) {
		if (TAG == null || message == null) {
			return;
		}

		if (checkOpen()) {
			Log.d(TAG, message);
		}
	}

	public static void debugToast(Context context, String message) {
		if (context == null || message == null) {
			return;
		}
		if (checkOpen()) {
			Toast.makeText(context, message, Toast.LENGTH_LONG).show();
		}
	}
}