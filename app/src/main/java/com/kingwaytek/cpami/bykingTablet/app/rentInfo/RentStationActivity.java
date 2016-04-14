package com.kingwaytek.cpami.bykingTablet.app.rentInfo;

import android.app.Activity;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;

import com.kingwaytek.cpami.bykingTablet.R;
import com.kingwaytek.cpami.bykingTablet.utilities.Util;
import com.kingwaytek.cpami.bykingTablet.app.poi.POIListView;
import com.kingwaytek.cpami.bykingTablet.sql.SQLiteBot;
import com.kingwaytek.cpami.bykingTablet.sql.SqliteConstant.TableName;
import com.kingwaytek.cpami.bykingTablet.view.ViewConstant.ActivityCaller;
import com.kingwaytek.cpami.bykingTablet.view.ViewConstant.SearchMode;

public class RentStationActivity extends Activity {

	private ListView listView;
	private RentStationAdapter adapter;
	private SQLiteBot sqliteDatabase;
	private Cursor cursor;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		setContentView(R.layout.rent_station);
		if (Util.city_sort == null) {
			Util.getSortPOICity(this);
		}
		listView = (ListView) findViewById(R.id.rentStation_listView);
		adapter = new RentStationAdapter(R.layout.rent_station_cell, this, Util.city_sort);
		listView.setAdapter(adapter);
		sqliteDatabase = new SQLiteBot(this.getString(R.string.SQLite_App_Database_Name),
				this.getString(R.string.SQLite_App_Database_Path), TableName.POI);


		listView.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> arg0, View arg1, int position, long arg3) {
				// TODO Auto-generated method stub
				
				
				
				Intent intent = new Intent();
				intent.putExtra("RentStation", "Y");
				intent.putExtra("POI_Search", SearchMode.BY_SURROUNDING);
				intent.putExtra("POIList_Caller", ActivityCaller.POI);
				intent.setClass(RentStationActivity.this, POIListView.class);
				intent.putExtra("RentCity", arg1.getTag().toString());
				startActivityForResult(intent, ActivityCaller.RENT.getValue());
			}
		});
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		// super.onActivityResult(requestCode, resultCode, intent);
		if (resultCode == RESULT_OK) {
			Intent itenCaller = new Intent();
			itenCaller.putExtra("Action", data.getSerializableExtra("POI_Action"));
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
