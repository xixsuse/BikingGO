package com.kingwaytek.cpami.bykingTablet.utilities;

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.support.v4.app.NotificationCompat;

import com.kingwaytek.cpami.bykingTablet.AppController;
import com.kingwaytek.cpami.bykingTablet.R;
import com.kingwaytek.cpami.bykingTablet.app.model.CommonBundle;
import com.kingwaytek.cpami.bykingTablet.app.ui.track.UiTrackMapActivity;

/**
 * Created by vincent.chang on 2016/7/29.
 */
public class NotifyHelper {

    private static final int NOTIFY_ID_TRACK_SERVICE = 100;

    private static Context appContext() {
        return AppController.getInstance().getAppContext();
    }

    public static void showServiceNotification() {
        Intent intent = new Intent(appContext(), UiTrackMapActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
        intent.addFlags(Intent.FLAG_ACTIVITY_RESET_TASK_IF_NEEDED);
        intent.putExtra(CommonBundle.BUNDLE_ENTRY_TYPE, CommonBundle.ENTRY_TYPE_TRACKING);
        PendingIntent pendingIntent = PendingIntent.getActivity(appContext(), 1, intent, PendingIntent.FLAG_ONE_SHOT);

        NotificationCompat.Builder builder = new NotificationCompat.Builder(appContext());
        builder.setSmallIcon(R.drawable.menu_icon_bike_tracking)
                .setContentTitle(appContext().getString(R.string.app_name))
                .setContentText(appContext().getString(R.string.track_in_background))
                .setTicker(appContext().getString(R.string.track_in_background))
                .setWhen(System.currentTimeMillis())
                .setShowWhen(true)
                .setContentIntent(pendingIntent)
                .setSound(null)
                .setVibrate(null);

        NotificationManager NM = (NotificationManager) appContext().getSystemService(Context.NOTIFICATION_SERVICE);
        NM.notify(NOTIFY_ID_TRACK_SERVICE, builder.build());
    }

    public static void clearServiceNotification() {
        NotificationManager NM = (NotificationManager) appContext().getSystemService(Context.NOTIFICATION_SERVICE);
        NM.cancel(NOTIFY_ID_TRACK_SERVICE);
    }
}
