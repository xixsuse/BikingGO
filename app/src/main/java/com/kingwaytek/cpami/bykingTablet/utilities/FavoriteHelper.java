package com.kingwaytek.cpami.bykingTablet.utilities;

import android.util.Log;

import com.kingwaytek.cpami.bykingTablet.app.model.ItemsMyPOI;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

/**
 * 用 json的格式記錄我的最愛 or 我的景點，
 * 然後用 sharedPreferences來儲存！
 *
 * @author Vincent (2016/5/12)
 */
public class FavoriteHelper {

    private static final String TAG = "FavoriteHelper";

    private static JSONArray JA_POI;

    public static final String POI_TITLE = "title";
    public static final String POI_DESCRIPTION = "description";
    public static final String POI_LAT = "lat";
    public static final String POI_LNG = "lng";
    public static final String POI_PHOTO_PATH = "photoPath";

    private static int POI_INDEX;

    public static void initFavorite() {
        SettingManager.Favorite.initFavoritePreference();

        try {
            if (SettingManager.Favorite.getMyPoi() == null) {
                JA_POI = new JSONArray();

                SettingManager.Favorite.setMyPoi(JA_POI.toString());

                Log.i(TAG, "JA_POI isNull, FavInit!!");
            }
            else {
                JA_POI = new JSONArray(SettingManager.Favorite.getMyPoi());
                Log.i(TAG, "JA_POI isNotNull, FavInit!!");
                Log.i(TAG, "PoiInit: " + JA_POI.toString());
            }
        }
        catch (JSONException e) {
            e.printStackTrace();
        }
    }

    public static void checkIsFavInit() {
        if (SettingManager.Favorite.getMyPoi() == null || JA_POI == null || JA_POI.length() == 0)
            initFavorite();
    }

    public static boolean isPoiExisted(double lat, double lng) {
        checkIsFavInit();

        String LAT = String.valueOf(lat);
        String LNG = String.valueOf(lng);

        try {
            JSONObject jo;

            for (int i = 0; i < JA_POI.length(); i++) {
                jo = JA_POI.getJSONObject(i);
                if (jo.getString(POI_LAT).equals(LAT) && jo.getString(POI_LNG).equals(LNG)) {
                    POI_INDEX = i;
                    return true;
                }
            }
            return false;
        }
        catch (JSONException e) {
            e.printStackTrace();
            return false;
        }
    }

    public static ItemsMyPOI getMyPoiItem() {
        try {
            String title = JA_POI.getJSONObject(POI_INDEX).getString(POI_TITLE);
            String desc = JA_POI.getJSONObject(POI_INDEX).getString(POI_DESCRIPTION);
            double lat = JA_POI.getJSONObject(POI_INDEX).getDouble(POI_LAT);
            double lng = JA_POI.getJSONObject(POI_INDEX).getDouble(POI_LNG);
            String photoPath = JA_POI.getJSONObject(POI_INDEX).getString(POI_PHOTO_PATH);

            return new ItemsMyPOI(title, desc, lat, lng, photoPath);
        }
        catch (JSONException e) {
            e.printStackTrace();
            return null;
        }
    }

    public static void addMyPoi(String title, String desc, double lat, double lng, String photoPath) {
        checkIsFavInit();

        try {
            JSONObject jo = new JSONObject();

            jo.put(POI_TITLE, title);
            jo.put(POI_DESCRIPTION, desc);
            jo.put(POI_LAT, lat);
            jo.put(POI_LNG, lng);
            jo.put(POI_PHOTO_PATH, photoPath);

            JA_POI.put(jo);

            SettingManager.Favorite.setMyPoi(JA_POI.toString());
            Log.i(TAG, "PoiAdded: " + JA_POI.toString());
        }
        catch (JSONException e) {
            e.printStackTrace();
        }
    }

    public static void removeMyPoi(double lat, double lng) {
        try {
            if (isPoiExisted(lat, lng)) {
                int len = JA_POI.length();

                ArrayList<JSONObject> tempJA = new ArrayList<>(len);

                for (int i = 0; i < len; i++) {
                    if (i != POI_INDEX)
                        tempJA.add(JA_POI.getJSONObject(i));
                }

                JA_POI = new JSONArray();

                for (JSONObject jo : tempJA) {
                    if (jo != null)
                        JA_POI.put(jo);
                }
                SettingManager.Favorite.setMyPoi(JA_POI.toString());
                Log.i(TAG, "PoiRemoved: " + JA_POI.toString());
            }
            else
                Log.i(TAG, "This POI doesn't exist!");
        }
        catch (JSONException e) {
            e.printStackTrace();
        }
    }

    public static void updateMyPoi(String title, String desc, String photoPath) {
        try {
            JA_POI.getJSONObject(POI_INDEX).put(POI_TITLE, title);
            JA_POI.getJSONObject(POI_INDEX).put(POI_DESCRIPTION, desc);
            JA_POI.getJSONObject(POI_INDEX).put(POI_PHOTO_PATH, photoPath);

            SettingManager.Favorite.setMyPoi(JA_POI.toString());

            Log.i(TAG, "PoiUpdated: " + JA_POI.toString());
        }
        catch (JSONException e) {
            e.printStackTrace();
        }
    }

    public static void checkAndReplaceAllPhotoPathIfNotExists() {
        checkIsFavInit();

        try {
            JSONObject jo;
            String photoPath;

            for (int i = 0; i < JA_POI.length(); i++) {
                jo = JA_POI.getJSONObject(i);
                photoPath = jo.getString(POI_PHOTO_PATH);

                if (Utility.isFileNotExists(photoPath)) {
                    jo.put(POI_PHOTO_PATH, "");
                    Log.i(TAG, "PhotoPath Replaced: " + photoPath);
                }
            }
            SettingManager.Favorite.setMyPoi(JA_POI.toString());
        }
        catch (JSONException e) {
            e.printStackTrace();
        }
    }
}
