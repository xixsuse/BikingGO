package com.kingwaytek.cpami.bykingTabletPad;

import com.kingwaytek.cpami.bykingTablet.app.Infomation.DetailActivity;
import com.kingwaytek.cpami.bykingTablet.app.Infomation.InfomationActivity;
import com.kingwaytek.cpami.bykingTablet.app.Infomation.InfomationObject;

import android.app.AlertDialog.Builder;
import android.app.Dialog;
import android.app.Service;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.content.Intent;
import android.os.Bundle;
import android.os.IBinder;
import android.view.WindowManager;

public class GCMDialogService extends Service {

	private InfomationObject object;

	@Override
	public IBinder onBind(Intent arg0) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public int onStartCommand(Intent intent, int flags, int startId) {
		// TODO Auto-generated method stub
		// TODO GCM Null 問題
		try {
			if (intent != null) {
				final String contentTitle = (intent
						.getStringExtra("contentTitle") == null) ? "" : intent
						.getStringExtra("contentTitle");
				final String startTime = (intent.getStringExtra("startTime") == null) ? ""
						: intent.getStringExtra("startTime");
				final String endTime = (intent.getStringExtra("endTime") == null) ? ""
						: intent.getStringExtra("endTime");
				final String address = (intent.getStringExtra("address") == null) ? ""
						: intent.getStringExtra("address");
				final String message = (intent.getStringExtra("message") == null) ? ""
						: intent.getStringExtra("message");

				final Intent intent2 = intent;
				Builder builder = new Builder(getApplicationContext());

				// builder.setTitle(message.substring(0, 10));
				builder.setMessage(contentTitle);
				builder.setNegativeButton("前往", new OnClickListener() {

					@Override
					public void onClick(DialogInterface dialog, int which) {
						// TODO Auto-generated method stub
						object = new InfomationObject();
						object.setName(contentTitle);
						object.setStart(startTime);
						object.setEnd(endTime);
						object.setAdd(address);
						object.setDescription(message);
						Intent notificationIntent = new Intent(
								getApplicationContext(), DetailActivity.class);
						notificationIntent.putExtra("Info", object);
						notificationIntent.putExtra("GCM", "GCM");
						notificationIntent
								.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK
										| Intent.FLAG_ACTIVITY_SINGLE_TOP
										| Intent.FLAG_ACTIVITY_NO_HISTORY);
						startActivity(notificationIntent);

						stopService(intent2);
					}
				});
				Dialog dialog = builder.create();
				dialog.getWindow().setType(
						WindowManager.LayoutParams.TYPE_SYSTEM_ALERT);
				dialog.show();
			}
		} catch (NullPointerException e) {
			e.printStackTrace();
		}
		return super.onStartCommand(intent, flags, startId);
	}
}
