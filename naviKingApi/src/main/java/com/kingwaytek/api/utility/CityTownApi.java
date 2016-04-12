package com.kingwaytek.api.utility;

import java.util.ArrayList;
import java.util.Arrays;

import android.app.Activity;
import android.content.res.Resources;

import com.kingwaytek.api.model.CityData;
import com.kingwaytek.api.model.TownData;
import com.kingwyatek.api.R;



public class CityTownApi{
	
	
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
	
	public static CityData getCityData(Activity activity,String cityId) {
		
		if(activity!=null){
			Resources resource = activity.getResources();
			ArrayList<String> cityRawList = new ArrayList<String>(Arrays.asList(resource.getStringArray(R.array.city_list)));
			for(int i =0;i<cityRawList.size();i++){
				String[] AfterSplit = cityRawList.get(i).split(",");
				if(AfterSplit.length==4){
					CityData cityDataTemp = new CityData(AfterSplit[0],AfterSplit[1],AfterSplit[2],AfterSplit[3]);
					if(cityDataTemp.id.equals(cityId)){
						return cityDataTemp;
					}
			
				}
			}
		}
		return null;
	}
	
	public static ArrayList<TownData> getTownList(Activity activity,
			String town_id) {
		ArrayList<TownData> _cityDataList = new ArrayList<TownData>();
		if (activity != null && town_id != null) {
			Resources resource = activity.getResources();
			int resid = activity.getResources().getIdentifier( "town_list_" + town_id, "array", activity.getPackageName());
			ArrayList<String> townRawList = new ArrayList<String>( Arrays.asList(resource.getStringArray(resid)));
			for (int i = 0; i < townRawList.size(); i++) {
				String[] AfterSplit = townRawList.get(i).split(",");
				if (AfterSplit.length == 4) {
					TownData cityTownDataTemp = new TownData(AfterSplit[0], AfterSplit[1], AfterSplit[2], AfterSplit[3]);
					_cityDataList.add(cityTownDataTemp);
				}
			}
		}
		return _cityDataList;
	}

}