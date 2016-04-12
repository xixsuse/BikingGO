package com.kingwaytek.api.model;

import android.app.Activity;
import android.content.res.Resources;

import java.util.ArrayList;
import java.util.Arrays;



public class TownData{
	private static final String TAG = "TownData";

	public String id;
	public String name;
	public String lat;
	public String lon;

	public ArrayList<TownData> townDataList;
	public TownData() {
		super();
	}

	public TownData(String _id, String _name,  String _lon,String _lat) {
		id = _id;
		name = _name;
		lon = _lon;
		lat = _lat;
	}
	
	/**
	 * 取得鄉鎮清單
	 * @param activity
	 * @param town_id
	 * 輸入縣市ID 
	 * EX A
	 * @return
	 */
	public static ArrayList<TownData> getTownDataList(Activity activity,String town_id) {
		ArrayList<TownData> _townDataList = new ArrayList<TownData>();
		if(town_id.length()==1){
			Resources resource = activity.getResources();
			int resid = activity.getResources().getIdentifier("town_list_" + town_id, "array", activity.getPackageName());
			ArrayList<String> townRawList = new ArrayList<String>(Arrays.asList(resource.getStringArray(resid)));
			for(int i =0;i<townRawList.size();i++){
				String[] AfterSplit = townRawList.get(i).split(",");
				if(AfterSplit.length==4){
					TownData townDataTemp = new TownData(AfterSplit[0],AfterSplit[1],AfterSplit[2],AfterSplit[3]);
					_townDataList.add(townDataTemp);
				}
			}
		}
		return _townDataList;
	}
	
	

}