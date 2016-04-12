package com.kingwaytek.cpami.bykingTablet.app;

import java.util.ArrayList;

import android.app.Activity;
import android.content.Context;
import android.util.DisplayMetrics;
import android.util.Log;

import com.kingwaytek.cpami.bykingTablet.data.ICity;
import com.sonavtek.sonav.sonav;

public class Util {

	private static sonav engine;
	public static int[] precolors;
	private static String citySortCode[] = { "A", "F", "G", "C", "H", "J", "O", "K", "B", "N", "M", "P", "W" };
	private static String citySortName[] = { "台北市", "新北市", "宜蘭縣", "基隆市", "桃園縣", "新竹縣", "新竹市", "苗栗縣", "台中市", "彰化縣",
			"南投縣", "雲林縣", "金門縣" };
	public static ArrayList<CityObject> city_sort;
	private static ICity[] cities_from_engin;

	public static void getSortPOICity(Context context) {
		engine = sonav.getInstance();
		cities_from_engin = engine.showlistcity();
		city_sort = new ArrayList<CityObject>();

		for (int i = 0; i < citySortName.length; i++) {
			CityObject temp = new CityObject();

			for (int j = 0; j < cities_from_engin.length; j++) {
				if (cities_from_engin[j].getName().contains(citySortName[i])) {
					temp.setCityCode(citySortCode[i]);
					temp.setCityName(citySortName[i]);
					temp.setCityID(cities_from_engin[j].getId());
					city_sort.add(temp);
					break;
				}
			}
		}
	}

	public static int[] initPrecolors(Activity activity) {

		if (precolors == null) {
			DisplayMetrics dm = new DisplayMetrics();
			activity.getWindowManager().getDefaultDisplay().getMetrics(dm);

			int mapWidth = 0 == dm.widthPixels ? dm.widthPixels : dm.widthPixels + 1;
			int mapHeight = 0 == dm.heightPixels ? dm.heightPixels : dm.heightPixels + 1;
			Log.i("Map Size@MapView init", "Width:" + mapWidth + " Height:" + mapHeight);

			Util.precolors = new int[mapWidth  * mapHeight ];
		}
		return Util.precolors;
	}
}
