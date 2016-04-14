package com.kingwaytek.cpami.bykingTablet.app.rentInfo;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;

import com.kingwaytek.cpami.bykingTablet.R;
import com.kingwaytek.cpami.bykingTablet.app.MapDownloadActivity;
import com.kingwaytek.cpami.bykingTablet.utilities.UtilDialog;
import com.kingwaytek.cpami.bykingTablet.view.ViewConstant.ActivityCaller;
import com.kingwaytek.cpami.bykingTablet.view.ViewConstant.ContextMenuOptions;

import java.io.IOException;

public class RentInfoActivity extends Activity {

	private Button stationButton;
	private Button ubikeButton;
	private Button parkButton;
	private int area;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.rent_info);

		stationButton = (Button) findViewById(R.id.rent_btn_station);
		ubikeButton = (Button) findViewById(R.id.rent_btn_ubike);
		parkButton = (Button) findViewById(R.id.rent_btn_park);

		try {
			this.area = MapDownloadActivity.getMapArea();
		} catch (IOException e) {
			e.printStackTrace();
		}

		if (area != 0 && area != 1) {
			ubikeButton.setVisibility(View.GONE);
		}

		stationButton.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				Intent intent = new Intent();
				intent.setClass(RentInfoActivity.this,
						RentStationActivity.class);
				startActivityForResult(intent, ActivityCaller.RENT.getValue());
			}
		});
		parkButton.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				Intent intent = new Intent();
				intent.setClass(RentInfoActivity.this, ParkActivity.class);
				startActivityForResult(intent, ActivityCaller.RENT.getValue());
			}
		});
		ubikeButton.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {

				UtilDialog uit = new UtilDialog(RentInfoActivity.this) {
					@Override
					public void click_btn_1() {
						Intent intent = new Intent();
						intent.setClass(RentInfoActivity.this, UbikeActivity.class);
						// startActivityForResult(itenSpoi,
						// ActivityCaller.SPOI.getValue());
						startActivity(intent);
						super.click_btn_1();
					}

					@Override
					public void click_btn_2() {
						super.click_btn_2();
					}
				};
				uit.showDialog_route_plan_choice(getString(R.string.dialog_web_message), null, "確定", "取消");
			}
		});
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		if (resultCode == RESULT_OK) {
			Intent itenCaller = new Intent();
			itenCaller.putExtra("Action", ContextMenuOptions.NAVIGATION);
			// itenCaller.putExtra("Name", data.getStringExtra("POI_Name"));
			// itenCaller.putExtra("Location", data
			// .getParcelableExtra("POI_Location"));
			// itenCaller.putExtra("Others", data
			// .getStringArrayExtra("POI_Others"));
			setResult(RESULT_OK, itenCaller);
			finish();
		} else if (resultCode == RESULT_FIRST_USER) {
			setResult(RESULT_FIRST_USER);
			finish();
		}
	}
}
