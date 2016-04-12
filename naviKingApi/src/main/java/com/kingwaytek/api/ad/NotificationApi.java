package com.kingwaytek.api.ad;

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.support.v4.app.NotificationCompat;

public class NotificationApi {
	static final String TAG = "NotificationApi";
	public static final int NOTIFICATION_ID = 28835663;

	public static final int PUSHTYPE_REPORTED = 0;
	public static final int PUSHTYPE_OPEN_APP = 1;
	public static final int PUSHTYPE_VERSION_UPDATE = 2;
	public static final int PUSHTYPE_URL = 3;
	public static final int PUSHTYPE_TOPIC_ID = 4;
	public static final int PUSHTYPE_COUPON_ID = 5;

	public static final String REAL_INTENT = "REAL_INTENT";
	public static final String PUSH_ID = "PUSH_ID";
	public static final String LAT = "LAT";
	public static final String LON = "LON";
	public static final String MEMBER_ID = "MEMBER_ID";

	public static int mCount;

	public static void send(Context context, Class className, int pushid, double lat, double lon, String memberId, int iconResId, String ticker, String title, String content, String info) {

		Intent intent = new Intent(context, className);
		intent.setAction(Intent.ACTION_MAIN);
		intent.addCategory(Intent.CATEGORY_LAUNCHER);
		if (context != null && intent != null) {
			send(context, intent, pushid, lat, lon, memberId, iconResId, title, title, content, null);
		}
	}

	public static void send(Context context, Intent intent, int pushid, double lat, double lon, String memberId, int iconResId, String title, String content) {
		if (context != null) {
			send(context, intent, pushid, lat, lon, memberId, iconResId, title, title, content, null);
		}
	}

	public static void send(Context context, Class className, int pushid, double lat, double lon, String memberId, int iconResId, String title, String content) {
		if (context != null) {
			send(context, className, pushid, lat, lon, memberId, iconResId, title, title, content, null);
		}
	}

	public static void send(Context context, Class className, int pushid, int icon, String ticker, String title, String content, String info) {
		Intent notificationIntent = new Intent(context, className);
		notificationIntent.setAction(Intent.ACTION_MAIN);
		notificationIntent.addCategory(Intent.CATEGORY_LAUNCHER);
		send(context, notificationIntent, pushid, icon, ticker, title, content, info);
	}

	// Put the GCM message into a notification and post it.
	public static void send(Context context, Intent notificationIntent, int pushid, int icon, String ticker, String title, String content, String info) {
		AdDebugHelper.debugLog(TAG, "send()");
		long when = System.currentTimeMillis();
		NotificationManager notificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
		// 宣告圖示

		Intent clickIntent = new Intent(context, ClickReceiver.class);
		clickIntent.setAction(ClickReceiver.ACTION_NOTIFICATION_CALLER);
		clickIntent.putExtra(REAL_INTENT, notificationIntent);
		clickIntent.putExtra(PUSH_ID, pushid);

		PendingIntent contentIntent = PendingIntent.getBroadcast(context, 0, clickIntent, PendingIntent.FLAG_UPDATE_CURRENT);

        NotificationCompat.Builder notification = new NotificationCompat.Builder(context);

        notification
                .setContentIntent(contentIntent)
                .setContentTitle(title)
                .setContentText(content)
                .setSmallIcon(icon)
                .setTicker(ticker)
                .setWhen(when)
                .setAutoCancel(true);

		notificationManager.notify(getNotificationId(), notification.build());

		//Notification notification = new Notification(icon, title, when);
		// 自動提示音效
		// notification.defaults |= Notification.DEFAULT_SOUND;
		// 點選後自動移除該通知
		//notification.flags = Notification.FLAG_AUTO_CANCEL;
		//notification.setLatestEventInfo(context, title, content, contentIntent);

	}

	public static void send(Context context, Intent intent, int pushid, double lat, double lon, String memberId, int iconResId, String ticker, String title, String content, String info) {
		long when = System.currentTimeMillis();
		NotificationManager notificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
		// 要開啟的App頁面 (className)
		// Intent intent = new Intent(context, className);
		// intent.setAction(Intent.ACTION_MAIN);
		// intent.addCategory(Intent.CATEGORY_LAUNCHER);
		// 中繼要記錄clickLog
		Intent clickIntent = new Intent(context, ClickReceiver.class);
		clickIntent.setAction(ClickReceiver.ACTION_NOTIFICATION_CALLER);
		clickIntent.putExtra(REAL_INTENT, intent);
		clickIntent.putExtra(PUSH_ID, pushid);
		clickIntent.putExtra(LAT, lat);
		clickIntent.putExtra(LON, lon);
		clickIntent.putExtra(MEMBER_ID, memberId);

		PendingIntent contentIntent = PendingIntent.getBroadcast(context, 0, clickIntent, PendingIntent.FLAG_UPDATE_CURRENT);

        NotificationCompat.Builder notification = new NotificationCompat.Builder(context);

        notification
                .setContentIntent(contentIntent)
                .setContentTitle(title)
                .setContentText(content)
                .setTicker(ticker)
                .setSmallIcon(iconResId)
                .setWhen(when)
                .setAutoCancel(true);

        notificationManager.notify(getNotificationId(), notification.build());

		//Notification notification = new Notification(iconResId, title, when);
		// 自動提示音效
		// notification.defaults |= Notification.DEFAULT_SOUND;
		// 點選後自動移除該通知
		//notification.flags = Notification.FLAG_AUTO_CANCEL;
		//notification.setLatestEventInfo(context, title, content, contentIntent);
	}

	public static int getNotificationId() {
		if (mCount < 0) {
			mCount = 1;
		}
		return NOTIFICATION_ID * 10 + mCount++;
	}

}
