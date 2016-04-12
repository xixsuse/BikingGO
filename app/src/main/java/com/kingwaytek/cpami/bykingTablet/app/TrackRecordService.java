package com.kingwaytek.cpami.bykingTablet.app;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.support.v4.app.NotificationCompat;
import android.util.Log;

import com.kingwaytek.cpami.bykingTablet.R;

/**
 * This class provides functionality of track record.
 * 
 * If you want the instance keep running after application killed (not to create
 * new instance), you should use bindService() method instead of startService()
 * method.
 */
public class TrackRecordService extends Service {

    private NotificationManager notifyMgr;
    private Notification notification;

    @Override
    public IBinder onBind(Intent intent) {
	Log.d(getClass().toString(), "onBind: intent=" + intent);
	return null;
    }

    @Override
    public void onCreate() {
	super.onCreate();

	notifyMgr = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);

	Log.d(getClass().toString(), "onCreate");
    }

    @Override
    public void onDestroy() {
	super.onDestroy();

	Log.d(getClass().toString(), "onDestroy");
    }

    @Override
    public void onRebind(Intent intent) {
	super.onRebind(intent);

	Log.d(getClass().toString(), "onRebind: " + intent);
    }

    @Override
    public void onStart(Intent intent, int startId) {
	super.onStart(intent, startId);

	Log.d(getClass().toString(), "onStart: intent=" + intent + ", id=" + startId);
    }

    static int id = 0;

//    @Override
//    public int onStartCommand(Intent intent, int flags, int startId) {
//	Log.d(getClass().toString(), 
//		"onStartCommand: intent=" + intent + ", flags=" + flags + ", id=" + startId);
//
//	startTrackRecord();
//
//	return START_NOT_STICKY;
//    }

    private void startTrackRecord() {
	notification = showNotification(R.drawable.icon, "更新完成", "更新完成", "update completed");
    }

    @Override
    public boolean onUnbind(Intent intent) {
	Log.d(getClass().toString(), "onUnbind: intent=" + intent);
	
	return super.onUnbind(intent);
    }

    /**
     * Shows notification on status bar.
     * 
     * @param noti
     *            update exist instance or set null to create new one
     * @param iconId
     *            resource ID of icon.
     * @param title
     *            The title that goes in the expanded entry.
     * @param content
     *            The text that goes in the expanded entry.
     * @param tickerText
     *            The text that flows by in the status bar when the notification
     *            first activates.
     */
    private Notification showNotification(int iconId, String title, String content, String tickerText) {
        PendingIntent contentIntent = PendingIntent.getActivity(this, 0, new Intent(), 0);

        NotificationCompat.Builder noti = new NotificationCompat.Builder(getApplicationContext());
        noti.setContentIntent(contentIntent)
                .setContentText(title)
                .setContentText(content)
                .setSmallIcon(iconId)
                .setTicker(tickerText)
                .setOngoing(true);

        //nofi.setLatestEventInfo(this, title, content, contentIntent);
        //nofi.flags |= Notification.FLAG_ONGOING_EVENT;
        notifyMgr.notify(0, noti.build());

        return noti.build();
    }
}
