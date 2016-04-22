package com.kingwaytek.cpami.bykingTablet.utilities;

import android.app.Activity;
import android.util.DisplayMetrics;
import android.util.Log;

import com.kingwaytek.cpami.bykingTablet.AppController;
import com.kingwaytek.cpami.bykingTablet.R;
import com.kingwaytek.cpami.bykingTablet.app.CityObject;
import com.kingwaytek.cpami.bykingTablet.data.ICity;
import com.sonavtek.sonav.sonav;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;

public class Util {

	public static int[] precolors;
	private static String citySortCode[] = { "A", "F", "G", "C", "H", "J", "O", "K", "B", "N", "M", "P", "W" };
	private static String citySortName[] = { "台北市", "新北市", "宜蘭縣", "基隆市", "桃園縣", "新竹縣", "新竹市", "苗栗縣", "台中市", "彰化縣",
			"南投縣", "雲林縣", "金門縣" };
	public static ArrayList<CityObject> city_sort;

	public static void getSortPOICity() {
		sonav engine = sonav.getInstance();
        ICity[] cities_from_engine = engine.showlistcity();
		city_sort = new ArrayList<>();

		for (int i = 0; i < citySortName.length; i++) {
			CityObject temp = new CityObject();

			for (int j = 0; j < cities_from_engine.length; j++) {
				if (cities_from_engine[j].getName().contains(citySortName[i])) {
					temp.setCityCode(citySortCode[i]);
					temp.setCityName(citySortName[i]);
					temp.setCityID(cities_from_engine[j].getId());
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

    /**
     * 2016/04/22
     * Method moved by Vincent.
     */
    public static void initUserDatabase() {
        String DATABASE_PATH = AppController.getInstance().getAppContext().getString(R.string.SQLite_Usr_Database_Path);
        String DATABASE_NAME = AppController.getInstance().getAppContext().getString(R.string.SQLite_Usr_Database_Name);

        // 輸出路徑
        String outFileName = DATABASE_PATH + DATABASE_NAME;

        // 檢測是否已經創建
        File dir = new File(outFileName);
        if (dir.exists())
            return;

        // 檢測/創建數據庫的文件夾
        dir = new File(DATABASE_PATH);
        if (!dir.isDirectory()) {
            if (!dir.mkdirs())
                return;
        }
        // 從資源中讀取數據庫流
        InputStream input = AppController.getInstance().getAppContext().getResources().openRawResource(R.raw.biking_data);

        OutputStream output = null;

        try {
            output = new FileOutputStream(outFileName);

            // 拷貝到輸出流
            byte[] buffer = new byte[2048];
            int length;
            while ((length = input.read(buffer)) > 0) {
                output.write(buffer, 0, length);
            }
        }
        catch (IOException e) {
            e.printStackTrace();
        }
        finally {
            // 關閉輸出&輸入流
            try {
                if (output != null) {
                    output.flush();
                    output.close();
                    input.close();
                }
            }
            catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}
