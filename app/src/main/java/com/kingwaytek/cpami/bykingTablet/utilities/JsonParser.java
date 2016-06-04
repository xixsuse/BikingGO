package com.kingwaytek.cpami.bykingTablet.utilities;

import android.util.Log;

import com.kingwaytek.cpami.bykingTablet.app.model.DataArray;
import com.kingwaytek.cpami.bykingTablet.app.model.items.ItemsMyPOI;
import com.kingwaytek.cpami.bykingTablet.app.model.items.ItemsPlanItem;
import com.kingwaytek.cpami.bykingTablet.app.model.items.ItemsPlans;
import com.kingwaytek.cpami.bykingTablet.app.model.items.ItemsSearchResult;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.lang.ref.SoftReference;
import java.util.ArrayList;

/**
 * Parse JSON 並建立 Items，搭配 DataArray使用！
 *
 * @author Vincent (2016/4/18)
 */
public class JsonParser {

    private static final String TAG = "JsonParser";

    static JSONObject JO;
    static JSONArray JA;

    public interface JSONParseResult {
        void onParseFinished();
        void onParseFail(String errorMessage);
    }

    private static void releaseObjects() {
        JO = null;
        JA = null;
    }

    public static void parseGoogleGeocodeThenAddToList(String jsonString, JSONParseResult parseResult) {
        try {
            ArrayList<ItemsSearchResult> resultList = new ArrayList<>();

            String name;
            String adminArea = "";
            String countryName = "";
            String lat;
            String lng;

            JO = new JSONObject(jsonString);
            JA = JO.getJSONArray("results");

            JSONObject result;
            JSONObject location;

            for (int i = 0; i < JA.length(); i++) {
                result = JA.getJSONObject(i);
                location = result.getJSONObject("geometry").getJSONObject("location");

                name = result.getString("formatted_address");
                lat = location.getString("lat");
                lng = location.getString("lng");

                resultList.add(new ItemsSearchResult(name, adminArea, countryName, Double.parseDouble(lat), Double.parseDouble(lng)));
            }
            if (DataArray.list_searchResult != null)
                DataArray.list_searchResult.clear();
            DataArray.list_searchResult = new SoftReference<>(resultList);

            parseResult.onParseFinished();
        }
        catch (JSONException e) {
            e.printStackTrace();
            parseResult.onParseFail(e.getMessage());
        }
        releaseObjects();
    }

    public static ArrayList<ItemsMyPOI> parseMyPoiAndGetList() {
        try {
            ArrayList<ItemsMyPOI> myPoiList = new ArrayList<>();

            JA = new JSONArray(SettingManager.Favorite.getMyPoi());

            String title;
            String desc;
            String lat;
            String lng;
            String photoPath;

            JSONObject jo;

            for (int i = 0; i < JA.length(); i++) {
                jo = JA.getJSONObject(i);
                title = jo.getString(FavoriteHelper.POI_TITLE);
                desc = jo.getString(FavoriteHelper.POI_DESCRIPTION);
                lat = jo.getString(FavoriteHelper.POI_LAT);
                lng = jo.getString(FavoriteHelper.POI_LNG);
                photoPath = jo.getString(FavoriteHelper.POI_PHOTO_PATH);

                myPoiList.add(new ItemsMyPOI(title, desc, Double.parseDouble(lat), Double.parseDouble(lng), photoPath));
            }
            releaseObjects();
            return myPoiList;
        }
        catch(JSONException e) {
            e.printStackTrace();
            releaseObjects();
            return null;
        }
    }

    public static String getPolyLineOverview(String jsonString) {
        try {
            JO = new JSONObject(jsonString);
            JA = JO.getJSONArray("routes");
            String status = JO.getString("status");

            if (status.equals("OK")) {
                String polyLine = JA.getJSONObject(0).getJSONObject("overview_polyline").getString("points");

                if (!polyLine.isEmpty()) {
                    releaseObjects();
                    return polyLine;
                }
            }
            else if (status.equals("REQUEST_DENIED")) {
                Utility.toastLong(JO.getString("error_message"));
                Log.e(TAG, JO.getString("error_message"));
            }
        }
        catch (JSONException e) {
            e.printStackTrace();
            Log.i(TAG, "getPolyLineError!! " + e.getMessage());
        }
        releaseObjects();
        return null;
    }

    public static ArrayList<String> getMyPlanNameList() {
        try {
            if (Util.isPlanFileNotExistOrEmpty())
                return null;

            JA = new JSONArray(Util.readPlanFile());

            ArrayList<String> planNameList = new ArrayList<>();
            String planName;

            for (int i = 0; i < JA.length(); i++) {
                planName = JA.getJSONObject(i).getString(FavoriteHelper.PLAN_NAME);
                planNameList.add(planName);
            }

            releaseObjects();
            return planNameList;
        }
        catch (JSONException e) {
            e.printStackTrace();
            releaseObjects();
            return null;
        }
    }

    public static ArrayList<ItemsPlans> getPlansData() {
        try {
            if (Util.isPlanFileNotExistOrEmpty())
                return null;

            JA = new JSONArray(Util.readPlanFile());

            ArrayList<ItemsPlans> plansList = new ArrayList<>();

            JSONArray ja;
            JSONObject jo;

            String planName;
            ArrayList<ItemsPlanItem> planItems;

            String title;
            String lat;
            String lng;

            for (int i = 0; i < JA.length(); i++) {
                JO = JA.getJSONObject(i);
                planName = JO.getString(FavoriteHelper.PLAN_NAME);

                ja = JO.getJSONArray(FavoriteHelper.PLAN_ITEMS);

                planItems = new ArrayList<>();

                for (int j = 0; j < ja.length(); j++) {
                    jo = ja.getJSONObject(j);

                    title = jo.getString(FavoriteHelper.POI_TITLE);
                    lat = jo.getString(FavoriteHelper.POI_LAT);
                    lng = jo.getString(FavoriteHelper.POI_LNG);

                    planItems.add(new ItemsPlanItem(title, Double.parseDouble(lat), Double.parseDouble(lng)));
                }

                plansList.add(new ItemsPlans(planName, planItems));

                Log.i(TAG, planName + " planItems size: " + planItems.size());
            }

            releaseObjects();
            return plansList;
        }
        catch (JSONException e) {
            e.printStackTrace();
            releaseObjects();
            return null;
        }
    }
}
