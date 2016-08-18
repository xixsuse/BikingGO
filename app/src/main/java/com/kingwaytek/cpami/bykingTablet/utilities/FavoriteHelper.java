package com.kingwaytek.cpami.bykingTablet.utilities;

import android.util.Log;

import com.kingwaytek.cpami.bykingTablet.AppController;
import com.kingwaytek.cpami.bykingTablet.R;
import com.kingwaytek.cpami.bykingTablet.app.model.items.ItemsMyPOI;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

/**
 * 用 json的格式記錄我的景點 or 行程規劃，
 * 然後用 sharedPreferences或寫檔案來儲存！
 *
 * @author Vincent (2016/5/12)
 */
public class FavoriteHelper {

    private static final String TAG = "FavoriteHelper";

    public static JSONArray JA_POI;

    public static final String POI_TITLE = "title";
    public static final String POI_ADDRESS = "address";
    public static final String POI_DESCRIPTION = "description";
    public static final String POI_LAT = "lat";
    public static final String POI_LNG = "lng";
    public static final String POI_PHOTO_PATH = "photoPath";

    public static final String PLAN_NAME = "planName";
    public static final String PLAN_ITEMS = "planItems";

    public static final String TRACK_TIME = "time";
    public static final String TRACK_NAME = "name";
    public static final String TRACK_DIFFICULTY = "difficulty";
    public static final String TRACK_DESCRIPTION = "description";
    public static final String TRACK_POLYLINE = "polyline";
    public static final String TRACK_DISTANCE = "distance";

    private static int POI_INDEX;

    public static void initPoiFavorite() {
        try {
            if (Util.isPoiFileNotExistOrEmpty()) {
                JA_POI = new JSONArray();

                Util.writePoiFile(JA_POI.toString());

                Log.i(TAG, "JA_POI isNull, FavInit!!");
            }
            else {
                JA_POI = new JSONArray(Util.readPoiFile());
                Log.i(TAG, "JA_POI isNotNull, FavInit!!");
                Log.i(TAG, "PoiInit: " + JA_POI.toString());
            }
        }
        catch (JSONException e) {
            e.printStackTrace();
        }
        finally {
            checkAndReplaceAllPhotoPathIfNotExists();
        }
    }

    public static boolean isPoiExisted(double lat, double lng) {
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
            String address = JA_POI.getJSONObject(POI_INDEX).getString(POI_ADDRESS);
            String desc = JA_POI.getJSONObject(POI_INDEX).getString(POI_DESCRIPTION);
            double lat = JA_POI.getJSONObject(POI_INDEX).getDouble(POI_LAT);
            double lng = JA_POI.getJSONObject(POI_INDEX).getDouble(POI_LNG);
            String photoPath = JA_POI.getJSONObject(POI_INDEX).getString(POI_PHOTO_PATH);

            return new ItemsMyPOI(title, address, desc, lat, lng, photoPath);
        }
        catch (JSONException e) {
            e.printStackTrace();
            return null;
        }
    }

    public static void addMyPoi(String title, String address, String desc, double lat, double lng, String photoPath) {
        try {
            JSONObject jo = new JSONObject();

            jo.put(POI_TITLE, title);
            jo.put(POI_ADDRESS, address);
            jo.put(POI_DESCRIPTION, desc);
            jo.put(POI_LAT, lat);
            jo.put(POI_LNG, lng);
            jo.put(POI_PHOTO_PATH, photoPath);

            JA_POI.put(jo);

            Util.writePoiFile(JA_POI.toString());
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
                Util.writePoiFile(JA_POI.toString());
                Log.i(TAG, "PoiRemoved: " + JA_POI.toString());
            }
            else
                Log.i(TAG, "This POI doesn't exist!");
        }
        catch (JSONException e) {
            e.printStackTrace();
        }
    }

    public static void updateMyPoi(String title, String address, String desc, String photoPath) {
        try {
            JA_POI.getJSONObject(POI_INDEX).put(POI_TITLE, title);
            JA_POI.getJSONObject(POI_INDEX).put(POI_ADDRESS, address);
            JA_POI.getJSONObject(POI_INDEX).put(POI_DESCRIPTION, desc);
            JA_POI.getJSONObject(POI_INDEX).put(POI_PHOTO_PATH, photoPath);

            Util.writePoiFile(JA_POI.toString());

            Log.i(TAG, "PoiUpdated: " + JA_POI.toString());
        }
        catch (JSONException e) {
            e.printStackTrace();
        }
    }

    private static void checkAndReplaceAllPhotoPathIfNotExists() {
        try {
            JSONObject jo;
            String photoPath;

            boolean needRewrite = false;

            for (int i = 0; i < JA_POI.length(); i++) {
                jo = JA_POI.getJSONObject(i);
                photoPath = jo.getString(POI_PHOTO_PATH);

                if (Utility.isFileNotExists(photoPath)) {
                    jo.put(POI_PHOTO_PATH, "");
                    needRewrite = true;
                    Log.i(TAG, "PhotoPath Replaced: " + photoPath);
                }
            }

            if (needRewrite)
                Util.writePoiFile(JA_POI.toString());
        }
        catch (JSONException e) {
            e.printStackTrace();
        }
    }

    /**
     * @return index where the PlanItem just added.
     */
    public static int addPlan(JSONObject singlePlanJO) {
        JSONArray ja_plan;

        try {
            if (Util.isPlanFileNotExistOrEmpty())
                ja_plan = new JSONArray();
            else
                ja_plan = new JSONArray(Util.readPlanFile());

            ja_plan.put(singlePlanJO);
            Util.writePlanFile(ja_plan.toString());

            Utility.toastShort(AppController.getInstance().getString(R.string.plan_save_completed));
            Log.i(TAG, "addPlan: " + ja_plan.toString());

            return (ja_plan.length() - 1);
        }
        catch (JSONException e) {
            e.printStackTrace();
            return -1;
        }
    }

    public static void updatePlan(int index, String planName, JSONArray planItems) {
        try {
            if (!Util.isPlanFileNotExistOrEmpty()) {
                JSONArray ja_plans = new JSONArray(Util.readPlanFile());

                ja_plans.getJSONObject(index).put(PLAN_NAME, planName);
                ja_plans.getJSONObject(index).put(PLAN_ITEMS, planItems);

                Util.writePlanFile(ja_plans.toString());

                Utility.toastShort(AppController.getInstance().getString(R.string.plan_update_completed));
                Log.i(TAG, "updatePlan: " + ja_plans.toString());
            }
        }
        catch (JSONException e) {
            e.printStackTrace();
        }
    }

    public static void removePlan(int index) {
        try {
            if (!Util.isPlanFileNotExistOrEmpty()) {
                JSONArray ja_plans = new JSONArray(Util.readPlanFile());

                int len = ja_plans.length();

                ArrayList<JSONObject> tempJA = new ArrayList<>();

                for (int i = 0; i < len; i++) {
                    if (i != index)
                        tempJA.add(ja_plans.getJSONObject(i));
                }

                ja_plans = new JSONArray();

                for (JSONObject jo : tempJA) {
                    ja_plans.put(jo);
                }

                Util.writePlanFile(ja_plans.toString());

                Utility.toastShort(AppController.getInstance().getString(R.string.plan_remove_completed));
                Log.i(TAG, "removePlan: " + ja_plans.toString());
            }
        }
        catch (JSONException e) {
            e.printStackTrace();
        }
    }

    public static void removeMultiPlan(ArrayList<Integer> checkedList) {
        try {
            if (!Util.isPlanFileNotExistOrEmpty()) {
                JSONArray ja_plans = new JSONArray(Util.readPlanFile());

                int len = ja_plans.length();

                ArrayList<JSONObject> tempJA = new ArrayList<>();

                boolean isNotRemovable;

                for (int i = 0; i < len; i++) {
                    isNotRemovable = true;

                    for (Integer index : checkedList) {
                        if (i == index) {
                            isNotRemovable = false;
                            break;
                        }
                    }
                    if (isNotRemovable)
                        tempJA.add(ja_plans.getJSONObject(i));
                }

                ja_plans = new JSONArray();

                for (JSONObject jo : tempJA) {
                    ja_plans.put(jo);
                }

                Util.writePlanFile(ja_plans.toString());

                Utility.toastShort(AppController.getInstance().getString(R.string.plan_remove_completed));
                Log.i(TAG, "removePlan: " + ja_plans.toString());
            }
        }
        catch (JSONException e) {
            e.printStackTrace();
        }
    }

    public static void addTrack(String time, String name, int difficulty, String description, String polyline, String distance) {
        try {
            String ja_string = TrackingFileUtil.readTrackFile();
            JSONArray ja_track;

            if (ja_string == null)
                ja_track = new JSONArray();
            else
                ja_track = new JSONArray(ja_string);

            JSONObject jo = new JSONObject();
            jo.put(TRACK_TIME, time);
            jo.put(TRACK_NAME, name);
            jo.put(TRACK_DIFFICULTY, difficulty);
            jo.put(TRACK_DESCRIPTION, description);
            jo.put(TRACK_POLYLINE, polyline);
            jo.put(TRACK_DISTANCE, distance);

            ja_track.put(jo);

            TrackingFileUtil.writeTrackFile(ja_track.toString());

            Log.i(TAG, "addTrackFile: " + ja_track.toString());
        }
        catch (JSONException e) {
            e.printStackTrace();
        }
    }

    public static void removeMultiTrack(ArrayList<Integer> checkedList) {
        String ja_string = TrackingFileUtil.readTrackFile();

        if (ja_string != null)
        {
            try {
                JSONArray ja = new JSONArray(ja_string);

                ArrayList<JSONObject> tempJA = new ArrayList<>();
                boolean isNotRemovable;

                for (int i = 0; i < ja.length(); i++) {
                    isNotRemovable = true;

                    for (Integer index : checkedList) {
                        if (i == index) {
                            isNotRemovable = false;
                            break;
                        }
                    }
                    if (isNotRemovable)
                        tempJA.add(ja.getJSONObject(i));
                }

                ja = new JSONArray();

                for (JSONObject jo : tempJA) {
                    ja.put(jo);
                }

                TrackingFileUtil.writeTrackFile(ja.toString());
                Utility.toastShort(AppController.getInstance().getString(R.string.track_delete_completed));

                Log.i(TAG, "removeTrack: " + ja.toString());
            }
            catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }

    public static void updateTrackInfo(int index, String name, int difficulty, String description) {
        String ja_string = TrackingFileUtil.readTrackFile();

        if (ja_string != null) {
            try {
                JSONArray ja = new JSONArray(ja_string);

                ja.getJSONObject(index).put(TRACK_NAME, name);
                ja.getJSONObject(index).put(TRACK_DIFFICULTY, difficulty);
                ja.getJSONObject(index).put(TRACK_DESCRIPTION, description);

                TrackingFileUtil.writeTrackFile(ja.toString());

                Log.i(TAG, "TrackFileUpdated: " + ja.toString());
            }
            catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }
}