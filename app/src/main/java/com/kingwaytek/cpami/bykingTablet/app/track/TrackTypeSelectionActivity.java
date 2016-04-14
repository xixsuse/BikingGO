package com.kingwaytek.cpami.bykingTablet.app.track;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageButton;

import com.kingwaytek.cpami.bykingTablet.R;
import com.kingwaytek.cpami.bykingTablet.app.IsAllowedConnection;
import com.kingwaytek.cpami.bykingTablet.utilities.UtilDialog;

public class TrackTypeSelectionActivity extends Activity implements OnClickListener {

	private ImageButton btn_friend;

	private ImageButton btn_road;

	private ImageButton btn_plan;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		this.setContentView(R.layout.activity_track_type_selection);

		btn_friend = (ImageButton) this.findViewById(R.id.button3);
		btn_friend.setTag(1);

		btn_road = (ImageButton) this.findViewById(R.id.button4);
		btn_road.setTag(0);

		btn_plan = (ImageButton) this.findViewById(R.id.button5);
		btn_plan.setTag(2);

		btn_friend.setOnClickListener(this);
		btn_road.setOnClickListener(this);
		btn_plan.setOnClickListener(this);
	}

	@Override
	public void onClick(View arg0) {
		int tag = (Integer) arg0.getTag();

		this.goToNextActivityWithCatagory(String.valueOf(tag));
	}

	private void goToNextActivityWithCatagory(final String type) {

		if (IsAllowedConnection.checkConnectionPermission(this)) {

			UtilDialog uit = new UtilDialog(TrackTypeSelectionActivity.this) {
				@Override
				public void click_btn_1() {
					Intent intent = new Intent(TrackTypeSelectionActivity.this, TrackDownLoad.class);

					intent.putExtra("type", type);

					TrackTypeSelectionActivity.this.startActivity(intent);
					super.click_btn_1();
				}

				@Override
				public void click_btn_2() {

					super.click_btn_2();
				}
			};
			uit.showDialog_route_plan_choice(this.getResources().getString(R.string.dialog_web_message), null, "確定",
					"取消");

//			new AlertDialog.Builder(this).setMessage(this.getResources().getString(R.string.dialog_web_message))
//					.setNegativeButton("取消", null).setPositiveButton("確定", new DialogInterface.OnClickListener() {
//
//						@Override
//						public void onClick(DialogInterface dialog, int which) {
//							Intent intent = new Intent(TrackTypeSelectionActivity.this, TrackDownLoad.class);
//
//							intent.putExtra("type", type);
//
//							TrackTypeSelectionActivity.this.startActivity(intent);
//						}
//					}).show();
		}
	}
}
