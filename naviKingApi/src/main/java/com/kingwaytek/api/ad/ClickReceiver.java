package com.kingwaytek.api.ad;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

/**
 * 點推播後會先到這記錄ClickPushID,再開啟對應的頁面
 * 
 * @author CalvinHuang
 * 
 */
public final class ClickReceiver extends BroadcastReceiver {

	static final String TAG = "ClickReceiver";
	public static final String ACTION_NOTIFICATION_CALLER = "NOTIFICATION_CALLER";

	public void onReceive(Context context, Intent intent) {
		if (context != null && intent != null && intent.getAction() != null && intent.getAction().equals(ACTION_NOTIFICATION_CALLER)) {
			Bundle bunde = intent.getExtras();
			if (bunde == null) {
				return;
			}
			AdDebugHelper.debugLog(TAG, "ClickNotification");
			Intent realIntent = intent.getParcelableExtra(NotificationApi.REAL_INTENT);
			realIntent.setAction(Intent.ACTION_MAIN);
			realIntent.addCategory(Intent.CATEGORY_LAUNCHER);
			realIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
			int pushid = intent.getIntExtra(NotificationApi.PUSH_ID, 0);
			double lat = intent.getDoubleExtra(NotificationApi.LAT, 0.0);
			double lon = intent.getDoubleExtra(NotificationApi.LON, 0.0);
			String memberId = intent.getStringExtra(NotificationApi.MEMBER_ID);
			if (pushid > 0) {
				AdDebugHelper.debugLog(TAG, "pushid:" + pushid);
				AdDebugHelper.debugLog(TAG, "lat:" + lat);
				AdDebugHelper.debugLog(TAG, "lon:" + lon);
				AdDebugHelper.debugLog(TAG, "memberId:" + memberId);
				GcmManager.sendPushClickLogTask(context, pushid, lat, lon, memberId);
			}
			context.startActivity(realIntent);
		}
	}
}
