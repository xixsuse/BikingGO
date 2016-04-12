package com.kingwaytek.api.model;

import java.util.ArrayList;
import java.util.Arrays;

import android.app.Activity;
import android.content.res.Resources;

import com.kingwyatek.api.R;



public class CityData{
	private static final String TAG = "CityData";

	public String id;
	public String name;
	public String lat;
	public String lon;
	public ArrayList<CityData> cityDataList;
	public CityData() {
		super();
	}

	public CityData(String _id, String _name,  String _lon,String _lat) {
		id = _id;
		name = _name;
		lon = _lon;
		lat = _lat;
	}
	
	public static ArrayList<CityData> getCityList(Activity activity) {
		ArrayList<CityData> _cityDataList = new ArrayList<CityData>();
		if(activity!=null){
			Resources resource = activity.getResources();
			ArrayList<String> cityRawList = new ArrayList<String>(Arrays.asList(resource.getStringArray(R.array.city_list)));
			
			for(int i =0;i<cityRawList.size();i++){
				String[] AfterSplit = cityRawList.get(i).split(",");
				if(AfterSplit.length==4){
					CityData cityDataTemp = new CityData(AfterSplit[0],AfterSplit[1],AfterSplit[2],AfterSplit[3]);
					_cityDataList.add(cityDataTemp);
				}
			}
		}
		return _cityDataList;
	}
	
	public static ArrayList<CityData> getTownList(Activity activity,String town_id) {
		ArrayList<CityData> _cityDataList = new ArrayList<CityData>();
		Resources resource = activity.getResources();
		int resid = activity.getResources().getIdentifier("town_list_" + town_id, "array", activity.getPackageName());
		ArrayList<String> townRawList = new ArrayList<String>(Arrays.asList(resource.getStringArray(resid)));
		for(int i =0;i<townRawList.size();i++){
			String[] AfterSplit = townRawList.get(i).split(",");
			if(AfterSplit.length==4){
				CityData cityTownDataTemp = new CityData(AfterSplit[0],AfterSplit[1],AfterSplit[2],AfterSplit[3]);
				_cityDataList.add(cityTownDataTemp);
			}
		}
		return _cityDataList;
	}
	
	public String getCityName(){
		return name ;
	}
	
	public double getLat(){		
		return getDoubleValue(lat);
	}
	
	public double getLon(){
		return getDoubleValue(lon);
	}
	
	private double getDoubleValue(String value){
		double result = 0.0f ; 
		try{
			result = Double.valueOf(value);
		}catch(NumberFormatException e){
			e.printStackTrace();
		}
		return result;
	}
}