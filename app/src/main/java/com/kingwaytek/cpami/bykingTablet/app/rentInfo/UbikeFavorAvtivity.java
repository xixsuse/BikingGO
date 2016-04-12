package com.kingwaytek.cpami.bykingTablet.app.rentInfo;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;

import com.kingwaytek.cpami.bykingTablet.R;

import android.app.Activity;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.AdapterView.OnItemClickListener;

public class UbikeFavorAvtivity extends Activity {
	private ArrayList<UbikeObject> Ubike_list;
	private ArrayList<DistenceObject> distence_list;
	private ArrayList<Integer> UbikeFavorList;
	private UbikeFavorAdapter adapter;
	private ListView listView;
	
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.ubikestation);
		listView = (ListView) findViewById(R.id.ubike_station_list);
		
		Ubike_list = UbikeActivity.Ubike_list;
		distence_list = UbikeActivity.distence_list;
		UbikeFavorList = UbikeActivity.UbikeFavorList;
		adapter = new UbikeFavorAdapter(this, R.layout.ubikelist_cell, Ubike_list, distence_list, UbikeFavorList);
		listView.setAdapter(adapter);
		
	}
}
