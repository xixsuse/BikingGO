package com.kingwaytek.cpami.bykingTablet.TrafficCondition;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageButton;
import android.widget.TextView;

import com.kingwaytek.cpami.bykingTablet.R;
import com.kingwaytek.cpami.bykingTablet.app.SMSMapContent;

import java.util.HashMap;
import java.util.Map;

public class TrafficDetail extends Activity {
	Bundle trafficDetailBundle;
	Map<String, Object> Area;
	Map<String, Object> Type;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		trafficDetailBundle = this.getIntent().getExtras();
		setContentView(R.layout.traffic_detail);

		setDataAarray();
		setAllInfo();

		ImageButton toMapButton = (ImageButton) findViewById(R.id.to_map);
		toMapButton.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				Intent intent = new Intent();
				intent.putExtra("Lon", Double.parseDouble(trafficDetailBundle
						.getString("lon")));
				intent.putExtra("Lat", Double.parseDouble(trafficDetailBundle
						.getString("lat")));
				intent.setClass(TrafficDetail.this, SMSMapContent.class);
				startActivity(intent);
				// finish();
			}
		});

	}

	private void setDataAarray() {
		Type = new HashMap<String, Object>();
		Type.put("1", "其他");
		Type.put("2", "交通事故");
		Type.put("3", "道路施工");
		Type.put("4", "交通管制");
		Type.put("5", "號誌故障");

		Area = new HashMap<String, Object>();
		Area.put("1", "台北市");
		Area.put("2", "新北市");
		Area.put("3", "基隆市");
		Area.put("4", "宜蘭縣");
		Area.put("5", "桃園縣");
		Area.put("6", "新竹市");
		Area.put("7", "新竹縣");
		Area.put("8", "苗栗縣");
	}

	private void setAllInfo() {

		String startTime = trafficDetailBundle.getString("starttime")
				.substring(0, 4)
				+ "-"
				+ trafficDetailBundle.getString("starttime").substring(4, 6)
				+ "-"
				+ trafficDetailBundle.getString("starttime").substring(6, 8)
				+ " "
				+ trafficDetailBundle.getString("starttime").substring(8, 10)
				+ ":"
				+ trafficDetailBundle.getString("starttime").substring(10);
		String endTime = trafficDetailBundle.getString("endtime").substring(0,
				4)
				+ "-"
				+ trafficDetailBundle.getString("endtime").substring(4, 6)
				+ "-"
				+ trafficDetailBundle.getString("endtime").substring(6, 8)
				+ " "
				+ trafficDetailBundle.getString("endtime").substring(8, 10)
				+ ":" + trafficDetailBundle.getString("endtime").substring(10);

		String state = null;
		if ((String) trafficDetailBundle.getString("reviewed") == "0") {
			state = "未處理";
		} else {
			state = "已處理";
		}

		((TextView) findViewById(R.id.type)).setText((String) Type
				.get(trafficDetailBundle.getString("type")));
		((TextView) findViewById(R.id.title)).setText(trafficDetailBundle
				.getString("title"));
		((TextView) findViewById(R.id.name)).setText(trafficDetailBundle
				.getString("source"));
		((TextView) findViewById(R.id.type2)).setText((String) Type
				.get(trafficDetailBundle.getString("type")));
		((TextView) findViewById(R.id.main)).setText(trafficDetailBundle
				.getString("title"));
		// ((TextView)
		// findViewById(R.id.startTime)).setText(trafficDetailBundle.getString("starttime"));
		// ((TextView)
		// findViewById(R.id.endTime)).setText(trafficDetailBundle.getString("endtime"));
		((TextView) findViewById(R.id.startTime)).setText(startTime);
		((TextView) findViewById(R.id.endTime)).setText(endTime);
		((TextView) findViewById(R.id.location)).setText((String) Area
				.get(trafficDetailBundle.getString("cityid")) + ",");
		((TextView) findViewById(R.id.townName))
				.setText((String) trafficDetailBundle.getString("town_name"));
		((TextView) findViewById(R.id.describe)).setText(trafficDetailBundle
				.getString("detial"));
		((TextView) findViewById(R.id.processState)).setText(state);
		((TextView) findViewById(R.id.processDescribe))
				.setText(trafficDetailBundle.getString("review_detail"));
	}
}
