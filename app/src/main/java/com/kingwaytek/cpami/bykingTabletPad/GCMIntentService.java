/*
 * Copyright 2012 Google Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.kingwaytek.cpami.bykingTabletPad;

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.support.v4.app.NotificationCompat;
import android.util.Log;

import com.google.android.gcm.GCMBaseIntentService;
import com.google.android.gcm.GCMRegistrar;
import com.kingwaytek.cpami.bykingTablet.R;
import com.kingwaytek.cpami.bykingTablet.app.Infomation.InfomationActivity;

import static com.kingwaytek.cpami.bykingTabletPad.CommonUtilities.SENDER_ID;
import static com.kingwaytek.cpami.bykingTabletPad.CommonUtilities.displayMessage;

/**
 * IntentService responsible for handling GCM messages.
 */
public class GCMIntentService extends GCMBaseIntentService {

	@SuppressWarnings("hiding")
	private static final String TAG = "GCMIntentService";

	public GCMIntentService() {
		super(SENDER_ID);
	}

	@Override
	protected void onRegistered(Context context, String registrationId) {

		Log.i(TAG, "Device registered: regId = " + registrationId);
		displayMessage(context, getString(R.string.gcm_registered));
		ServerUtilities.register(context, registrationId);
	}

	@Override
	protected void onUnregistered(Context context, String registrationId) {
		Log.i(TAG, "Device unregistered");
		displayMessage(context, getString(R.string.gcm_unregistered));
		if (GCMRegistrar.isRegisteredOnServer(context)) {
			ServerUtilities.unregister(context, registrationId);
		} else {
			// This callback results from the call to unregister made on
			// ServerUtilities when the registration to the server failed.
			Log.i(TAG, "Ignoring unregister callback");
		}
	}

	// @Override
	// protected void onMessage(Context context, Intent intent) {
	// Log.i(TAG, "Received message");
	// String message = getString(R.string.gcm_message);
	// displayMessage(context, message);
	// // notifies user
	// generateNotification(context, message);
	// }
	@Override
	protected void onMessage(Context context, Intent intent) {
		Log.i(TAG, "Received message");
		// String message = getString(R.string.gcm_message);
		String contentTitle = intent.getStringExtra("contentTitle");
		String startTime = intent.getStringExtra("startTime");
		String endTime = intent.getStringExtra("endTime");
		String address = intent.getStringExtra("address");
		String message = intent.getStringExtra("message");

		Log.i(TAG, "contentTitle=" + contentTitle);
		Log.i(TAG, "startTime=" + startTime);
		Log.i(TAG, "endTime=" + endTime);
		Log.i(TAG, "address=" + address);
		Log.i(TAG, "message=" + message);

		displayMessage(context, message);
		// notifies user
		generateNotification(context, message);

		Intent intentDialog = new Intent();
		intentDialog.putExtra("contentTitle", contentTitle);
		intentDialog.putExtra("startTime", startTime);
		intentDialog.putExtra("endTime", endTime);
		intentDialog.putExtra("address", address);
		intentDialog.putExtra("message", message);
		intentDialog.setAction("GCMDialog");

		startService(intentDialog);
	}

	@Override
	protected void onDeletedMessages(Context context, int total) {
		Log.i(TAG, "Received deleted messages notification");
		String message = getString(R.string.gcm_deleted, total);
		displayMessage(context, message);
		// notifies user
		generateNotification(context, message);
	}

	@Override
	public void onError(Context context, String errorId) {
		Log.i(TAG, "Received error: " + errorId);
		displayMessage(context, getString(R.string.gcm_error, errorId));
	}

	@Override
	protected boolean onRecoverableError(Context context, String errorId) {
		// log message
		Log.i(TAG, "Received recoverable error: " + errorId);
		displayMessage(context, getString(R.string.gcm_recoverable_error, errorId));
		return super.onRecoverableError(context, errorId);
	}

	/**
	 * Issues a notification to inform the user that server has sent a message.
	 */
	private static void generateNotification(Context context, String message) {
        int icon = R.drawable.ic_launcher;
        long when = System.currentTimeMillis();
        String title = context.getString(R.string.app_name);

        NotificationManager notificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);

        Intent notificationIntent = new Intent(context, InfomationActivity.class);
        // set intent so it does not start a new activity
        notificationIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);

        PendingIntent intent = PendingIntent.getActivity(context, 0, notificationIntent, 0);

        NotificationCompat.Builder notification = new NotificationCompat.Builder(context);

        notification.setContentIntent(intent)
                .setContentTitle(title)
                .setContentText(message)
                .setTicker(message)
                .setSmallIcon(icon)
                .setWhen(when)
                .setAutoCancel(true);

        //notification.setLatestEventInfo(context, title, message, intent);
        //notification.flags |= Notification.FLAG_AUTO_CANCEL;
        notificationManager.notify(0, notification.build());
	}

}
