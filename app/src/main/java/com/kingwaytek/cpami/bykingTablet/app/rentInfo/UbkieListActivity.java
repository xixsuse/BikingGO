package com.kingwaytek.cpami.bykingTablet.app.rentInfo;

import java.util.ArrayList;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;

import com.kingwaytek.cpami.bykingTablet.R;
import com.kingwaytek.cpami.bykingTablet.data.GeoPoint;
import com.sonavtek.sonav.MapView;
import com.sonavtek.sonav.sonav;

public class UbkieListActivity extends Activity {
	private ArrayList<UbikeObject> Ubike_list;
	private ArrayList<DistenceObject> distence_list;
	private UbikeListAdapter adapter;
	private ListView listView;
	private Runnable runnable_update;
	private boolean onDestroy;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.ubikestation);
		listView = (ListView) findViewById(R.id.ubike_station_list);

		Ubike_list = UbikeActivity.Ubike_list;
		distence_list = UbikeActivity.distence_list;
		UbikeActivity.ubikePoint = -1;
		adapter = new UbikeListAdapter(this, R.layout.ubikelist_cell, Ubike_list, distence_list);
		listView.setAdapter(adapter);
		listView.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> arg0, View arg1, int position, long arg3) {

				UbikeActivity.ubikePoint = position;
				finish();
			}
		});

		// runnable_update = new Runnable() {
		//
		// @Override
		// public void run() {
		// if (!onDestroy) {
		// Log.i("UbkieListActivity", "runnable_update:start");
		// adapter.notifyDataSetChanged();
		// Handler handler = new Handler();
		// handler.postDelayed(this, 10000);
		// } else {
		// Log.i("UbkieListActivity", "runnable_update:stop");
		// }
		// }
		// };
		// runnable_update.run();
	}

	// @Override
	// protected void onDestroy() {
	// // TODO Auto-generated method stub
	// onDestroy = true;
	// super.onDestroy();
	// }
}
