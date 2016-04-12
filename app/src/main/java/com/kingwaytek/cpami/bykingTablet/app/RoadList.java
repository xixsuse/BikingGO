package com.kingwaytek.cpami.bykingTablet.app;

import java.util.ArrayList;
import java.util.HashMap;

import com.sonavtek.sonav.PathFinder;
import com.sonavtek.sonav.ROADLISTDATA;
import com.sonavtek.sonav.sonav;
import com.kingwaytek.cpami.bykingTablet.R;
import com.kingwaytek.cpami.bykingTablet.view.RoadlistAdapter;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.text.InputFilter.LengthFilter;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

public class RoadList extends Activity {
	private String[] name;
	private int[] turn;
	private String[] strlength;
	private int[] intlength;
	private double[] Longitude;
	private double[] Latitude;
	// private double StartPointArray;
	private String[] citytownname;
	private sonav engine;
	// private Map<Integer, Object[]> map;
	private ListView roadListView;
	private RoadlistAdapter listitemAdapter;
	// private ListViewAdapter roadListAdapter;
	private TextView StratRoadNameText;
	private TextView TotalLenghText;
	private TextView TotalTimeText;
	private TextView CityNameText;
	private ArrayList<HashMap<String, Object>> listitem;
	private int LengthCount;
	private Button gohome;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		// requestWindowFeature(Window.FEATURE_CUSTOM_TITLE);
		setContentView(R.layout.roadlist);
		// getWindow().setFeatureInt(Window.FEATURE_CUSTOM_TITLE,
		// R.layout.title_bar);
		// setTitle(getString(R.string.byking_function_RoadList_title));
		// ((TextView) findViewById(R.id.title_text2)).setText("");

		// gohome = (Button)findViewById(R.id.go_home);
		// gohome.setOnClickListener(new OnClickListener() {
		//
		// @Override
		// public void onClick(View v) {
		// setResult(RESULT_CANCELED);
		// finish();
		// return;
		//
		// }
		// });
		engine = sonav.getInstance();
		int mapstyle = Integer.valueOf(PreferenceActivity.getMapStyle(this));
		if (mapstyle < 6) {
			engine.setmapstyle(0, mapstyle, 1);
		} else {
			mapstyle -= 5;
			engine.setmapstyle(1, 0, mapstyle);
		}
		engine.savenaviparameter();
		LengthCount = 0;
		// 判斷是否路徑規畫完畢
		PathFinder pathFinder = PathFinder.getInstance();
		if (pathFinder.getStatus() != pathFinder.PATH_FINDING_DONE) {
			Log.i("RoadList.java", "pathFinder.getStatus()!= DONE");

			UtilDialog uit = new UtilDialog(RoadList.this) {
				@Override
				public void click_btn_1() {
					super.click_btn_1();
					finish();
					
				}
			};
			uit.showDialog_route_plan_choice("尚未做路徑規畫", null, "確定", "最短路徑");
		} else if (engine.getshortpathok() == 0) {
			Log.i("RoadList.java", "pathFinder.getStatus()!= DONE");

			UtilDialog uit = new UtilDialog(RoadList.this) {
				@Override
				public void click_btn_1() {
					super.click_btn_1();
					finish();
					
				}
			};
			uit.showDialog_route_plan_choice("路徑規劃失敗!", null, "確定", null);

		} else {

			roadListView = (ListView) findViewById(R.id.RoadlistItem);
			TotalLenghText = (TextView) findViewById(R.id.param1a);
			TotalTimeText = (TextView) findViewById(R.id.param1b);
			StratRoadNameText = (TextView) findViewById(R.id.param2b);
			CityNameText = (TextView) findViewById(R.id.param2c);

			// StartPointArray = MapActivity.StartPointLonForRoadList;
			String StartRoadName = engine.xytoroadname1(MapActivity.StartPointLonForRoadList,
					MapActivity.StartPointLatForRoadList);
			StratRoadNameText.setText(StartRoadName);
			String citytownname = engine.showcitytownname(MapActivity.StartPointLonForRoadList,
					MapActivity.StartPointLonForRoadList);
			CityNameText.setText(citytownname);
			// Log.i("RoadList.java","StartPointArray[0]="+MapActivity.StartPointLonForRoadList+" "+"StartPointArray[1]="+MapActivity.StartPointLatForRoadList);
			// Log.i("RoadList.java","StartRoadName="+StartRoadName+" "+"citytownname="+citytownname);
			// Toast.makeText(this, StartRoadName+"\n" +citytownname,
			// 5000).show();
			setRoadListData();
		}
	}

	@Override
	public void setTitle(CharSequence title) {
		((TextView) findViewById(R.id.title_text)).setText(title);
		((TextView) findViewById(R.id.title_text2)).setText("");
	}

	public void setRoadListData() {
		PathFinder pathFinder = PathFinder.getInstance();
		// Log.i("RoadList.java","pathFinder.getPathResult()"+String.valueOf(pathFinder.getPathResult()!=null));
		ROADLISTDATA[] list = pathFinder.getPathResult();
		name = new String[list.length];
		turn = new int[list.length];
		strlength = new String[list.length];
		intlength = new int[list.length];
		Longitude = new double[list.length];
		Latitude = new double[list.length];
		citytownname = new String[list.length];
		listitem = new ArrayList<HashMap<String, Object>>();

		for (int i = 0; i < list.length; i++) {

			// Log.i("RoadList.java","list[i].getName()"+list[i].getName());
			name[i] = list[i].getName();
			// Log.i("RoadList.java","list ="+name[i]);
			intlength[i] = (int) list[i].getLength();
			LengthCount += intlength[i];
			strlength[i] = String.valueOf(intlength[i]);
			Longitude[i] = list[i].getLongitude();
			Latitude[i] = list[i].getLatitude();
			citytownname[i] = engine.showcitytownname(Longitude[i], Latitude[i]);
			turn[i] = getResources().getIdentifier("turn_" + list[i].getTurn(), "drawable", getPackageName());
		}
		// LenghtCountText.setText("總距離:"+String.valueOf(LengthCount/1000)+"公里");
		int[] result = engine.getspdistime();
		double totalDistance = 0;
		int totalTime = 0;
		if (result != null) {
			totalDistance = result[0];
			totalTime = result[1] * 60 + result[2];
		}
		Log.i("RoadList.java", "totalDistance/1000" + (totalDistance / 1000));
		TotalLenghText.setText("總距離:" + String.valueOf(totalDistance / 1000).substring(0, 3) + "公里");
		TotalTimeText.setText("估計時間:" + String.valueOf(totalTime) + "分鐘");
		CityNameText.setText(citytownname[0]);

		for (int i = 0; i < list.length; i++) {
			HashMap<String, Object> map = new HashMap<String, Object>();
			map.put("name", name[i]);
			map.put("strlength", strlength[i]);
			map.put("citytownname", citytownname[i]);
			listitem.add(map);
		}
		// map = new HashMap<Integer, Object[]>() ;
		// map.put(R.id.roadlist_text1, name );
		// map.put(R.id.roadlist_distance, length);
		listitemAdapter = new RoadlistAdapter(this, listitem, R.layout.roadlistitem, new String[] { "name",
				"strlength", "citytownname" }, new int[] { R.id.roadlist_text1, R.id.roadlist_distance, R.id.area });
		listitemAdapter.putNameArray(name);
		listitemAdapter.putLengthArray(strlength);
		listitemAdapter.putTurnArray(turn);
		listitemAdapter.putCityTowNameArray(citytownname);
		// roadListAdapter = new ListViewAdapter(this, R.layout.roadlistitem,
		// map);
		// roadListAdapter.AddIconData(R.id.turn_image, turn);
		// roadListView.setAdapter(roadListAdapter);
		roadListView.setAdapter(listitemAdapter);

	}

}
