package com.kingwaytek.cpami.biking.utilities;

import android.util.Log;

import com.kingwaytek.cpami.biking.AppController;
import com.kingwaytek.cpami.biking.R;
import com.kingwaytek.cpami.biking.app.model.items.ItemsMyPOI;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.lang.ref.SoftReference;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Locale;

/**
 * 用 json的格式記錄我的景點 or 行程規劃，
 * 然後用 sharedPreferences或寫檔案來儲存！
 *
 * @author Vincent (2016/5/12)
 */
public class FavoriteHelper {

    private static final String TAG = "FavoriteHelper";

    public static SoftReference<JSONArray> JA_POI;

    public static final String POI_TITLE = "title";
    public static final String POI_ADDRESS = "address";
    public static final String POI_DESCRIPTION = "description";
    public static final String POI_LAT = "lat";
    public static final String POI_LNG = "lng";
    public static final String POI_PHOTO_PATH = "photoPath";

    public static final String PLAN_NAME = "planName";
    public static final String PLAN_DATE = "planDate";
    public static final String PLAN_ITEMS = "planItems";

    static final String TRACK_DATE = "time";
    static final String TRACK_NAME = "name";
    static final String TRACK_DIFFICULTY = "difficulty";
    static final String TRACK_DESCRIPTION = "description";
    static final String TRACK_POLYLINE = "polyline";
    static final String TRACK_DISTANCE = "distance";
    static final String TRACK_SPEED = "speed";
    static final String TRACK_DURATION = "duration";

    private static int POI_INDEX;

    public static void initPoiFavorite(boolean checkPhotoPath) {
        try {
            if (CommonFileUtil.isPoiFileNotExistOrEmpty()) {
                JA_POI = new SoftReference<>(new JSONArray());

                CommonFileUtil.writePoiFile(JA_POI.get().toString());

                Log.i(TAG, "JA_POI isNull, FavInit!!");
            }
            else {
                JA_POI =  new SoftReference<>(new JSONArray(CommonFileUtil.readPoiFile()));
                Log.i(TAG, "JA_POI isNotNull, FavInit!!");
                Log.i(TAG, "PoiInit: " + JA_POI.toString());
            }
        }
        catch (JSONException e) {
            e.printStackTrace();
        }
        finally {
            if (checkPhotoPath)
                checkAndReplaceAllPhotoPathIfNotExists();
        }
    }

    private static void checkPoiArrayIsStillAlive() {
        if (JA_POI == null || JA_POI.get() == null)
            initPoiFavorite(false);
    }

    public static boolean isPoiExisted(double lat, double lng) {
        checkPoiArrayIsStillAlive();

        String LAT = String.valueOf(lat);
        String LNG = String.valueOf(lng);

        try {
            JSONArray ja_poi = JA_POI.get();
            JSONObject jo;

            for (int i = 0; i < ja_poi.length(); i++) {
                jo = ja_poi.getJSONObject(i);
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
        checkPoiArrayIsStillAlive();
        try {
            JSONArray ja_poi = JA_POI.get();

            String title = ja_poi.getJSONObject(POI_INDEX).getString(POI_TITLE);
            String address = ja_poi.getJSONObject(POI_INDEX).getString(POI_ADDRESS);
            String desc = ja_poi.getJSONObject(POI_INDEX).getString(POI_DESCRIPTION);
            double lat = ja_poi.getJSONObject(POI_INDEX).getDouble(POI_LAT);
            double lng = ja_poi.getJSONObject(POI_INDEX).getDouble(POI_LNG);
            String photoPath = ja_poi.getJSONObject(POI_INDEX).getString(POI_PHOTO_PATH);

            return new ItemsMyPOI(title, address, desc, lat, lng, photoPath);
        }
        catch (JSONException e) {
            e.printStackTrace();
            return null;
        }
    }

    public static void addMyPoi(String title, String address, String desc, double lat, double lng, String photoPath) {
        checkPoiArrayIsStillAlive();
        try {
            JSONObject jo = new JSONObject();

            jo.put(POI_TITLE, title);
            jo.put(POI_ADDRESS, address);
            jo.put(POI_DESCRIPTION, desc);
            jo.put(POI_LAT, lat);
            jo.put(POI_LNG, lng);
            jo.put(POI_PHOTO_PATH, photoPath);

            JA_POI.get().put(jo);

            CommonFileUtil.writePoiFile(JA_POI.get().toString());
            Log.i(TAG, "PoiAdded: " + JA_POI.get().toString());
        }
        catch (JSONException e) {
            e.printStackTrace();
        }
    }

    public static void removeMyPoi(double lat, double lng) {
        checkPoiArrayIsStillAlive();
        try {
            if (isPoiExisted(lat, lng)) {
                JSONArray ja_poi = JA_POI.get();

                int len = ja_poi.length();

                ArrayList<JSONObject> tempJA = new ArrayList<>(len);

                for (int i = 0; i < len; i++) {
                    if (i != POI_INDEX)
                        tempJA.add(ja_poi.getJSONObject(i));
                }

                ja_poi = new JSONArray();

                for (JSONObject jo : tempJA) {
                    if (jo != null)
                        ja_poi.put(jo);
                }
                CommonFileUtil.writePoiFile(ja_poi.toString());
                Log.i(TAG, "PoiRemoved: " + ja_poi.toString());
            }
            else
                Log.i(TAG, "This POI doesn't exist!");
        }
        catch (JSONException e) {
            e.printStackTrace();
        }
    }

    public static void removeMultiPoi(ArrayList<Integer> checkedList) {
        checkPoiArrayIsStillAlive();
        try {
            JSONArray ja_poi = JA_POI.get();

            int len = ja_poi.length();

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
                    tempJA.add(ja_poi.getJSONObject(i));
            }

            ja_poi = new JSONArray();

            for (JSONObject jo : tempJA) {
                ja_poi.put(jo);
            }

            CommonFileUtil.writePoiFile(ja_poi.toString());

            Utility.toastShort(AppController.getInstance().getString(R.string.poi_remove_done));
            Log.i(TAG, "removePoi: " + ja_poi.toString());
        }
        catch (JSONException e) {
            e.printStackTrace();
        }
    }

    public static void updateMyPoi(String title, String address, String desc, String photoPath) {
        checkPoiArrayIsStillAlive();
        try {
            JSONArray ja_poi = JA_POI.get();

            ja_poi.getJSONObject(POI_INDEX).put(POI_TITLE, title);
            ja_poi.getJSONObject(POI_INDEX).put(POI_ADDRESS, address);
            ja_poi.getJSONObject(POI_INDEX).put(POI_DESCRIPTION, desc);
            ja_poi.getJSONObject(POI_INDEX).put(POI_PHOTO_PATH, photoPath);

            CommonFileUtil.writePoiFile(ja_poi.toString());

            Log.i(TAG, "PoiUpdated: " + ja_poi.toString());
        }
        catch (JSONException e) {
            e.printStackTrace();
        }
    }

    private static void checkAndReplaceAllPhotoPathIfNotExists() {
        checkPoiArrayIsStillAlive();
        try {
            JSONArray ja_poi = JA_POI.get();

            JSONObject jo;
            String photoPath;

            boolean needRewrite = false;

            for (int i = 0; i < ja_poi.length(); i++) {
                jo = ja_poi.getJSONObject(i);
                photoPath = jo.getString(POI_PHOTO_PATH);

                if (!photoPath.isEmpty() && Utility.isFileNotExists(photoPath)) {
                    jo.put(POI_PHOTO_PATH, "");
                    needRewrite = true;
                    Log.i(TAG, "PhotoPath Replaced: " + photoPath);
                }
            }

            if (needRewrite)
                CommonFileUtil.writePoiFile(ja_poi.toString());
        }
        catch (JSONException e) {
            e.printStackTrace();
        }
    }

    /**
     * 2016/10/20 Updated by Vincent:<p>
     * 不再回傳 index了！
     */
    public static void addPlan(JSONObject singlePlanJO) {
        JSONArray ja_plan = new JSONArray();
        JSONArray ja_tempPlan;

        try {
            if (CommonFileUtil.isPlanFileNotExistOrEmpty())
                ja_plan.put(singlePlanJO);
            else {
                ja_tempPlan = new JSONArray(CommonFileUtil.readPlanFile());
                ja_plan.put(singlePlanJO);

                for (int i = 0; i < ja_tempPlan.length(); i++) {
                    ja_plan.put(ja_tempPlan.getJSONObject(i));
                }
            }
            CommonFileUtil.writePlanFile(ja_plan.toString());

            Utility.toastShort(AppController.getInstance().getString(R.string.plan_save_completed));
            Log.i(TAG, "addPlan: " + ja_plan.toString());
        }
        catch (JSONException e) {
            e.printStackTrace();
        }
    }

    public static void updatePlan(int index, String planName, String planDate, JSONArray planItems) {
        try {
            if (!CommonFileUtil.isPlanFileNotExistOrEmpty()) {
                JSONArray ja_plans = new JSONArray(CommonFileUtil.readPlanFile());

                ja_plans.getJSONObject(index).put(PLAN_NAME, planName);
                ja_plans.getJSONObject(index).put(PLAN_DATE, planDate);
                ja_plans.getJSONObject(index).put(PLAN_ITEMS, planItems);

                String jaPlanString = getSortedPlanString(ja_plans);

                if (jaPlanString != null) {
                    CommonFileUtil.writePlanFile(jaPlanString);

                    Utility.toastShort(AppController.getInstance().getString(R.string.plan_update_completed));
                    Log.i(TAG, "updatePlan: " + jaPlanString);
                }
                else
                    Utility.toastShort(AppController.getInstance().getString(R.string.plan_update_failed));
            }
        }
        catch (JSONException e) {
            e.printStackTrace();
        }
    }

    // Bubble Sort!
    private static String getSortedPlanString(JSONArray ja_plan) {
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy年MM月dd日 HH:mm", Locale.TAIWAN);
        ArrayList<JSONObject> joArray = new ArrayList<>();

        try {
            for (int i = 0; i < ja_plan.length(); i++) {
                joArray.add(ja_plan.getJSONObject(i));
            }

            for (int i = 0; i < joArray.size(); i++) {
                for (int j = i; j < joArray.size() - 1; j++) {
                    if (isSmallerThenNext(dateFormat, joArray, i, j + 1)) {
                        JSONObject jo_temp = joArray.get(i);
                        joArray.set(i, joArray.get(j + 1));
                        joArray.set(j + 1, jo_temp);
                    }
                }
            }

            ja_plan = new JSONArray();
            for (JSONObject jo : joArray) {
                ja_plan.put(jo);
            }

            return ja_plan.toString();
        }
        catch (JSONException e) {
            e.printStackTrace();
            return null;
        }
    }

    private static boolean isSmallerThenNext(SimpleDateFormat dateFormat, ArrayList<JSONObject> joArray, int origin, int next) {
        try {
            return dateFormat.parse(joArray.get(origin).getString(PLAN_DATE)).getTime() <
                    dateFormat.parse(joArray.get(next).getString(PLAN_DATE)).getTime();
        }
        catch (ParseException | JSONException e) {
            e.printStackTrace();
            return false;
        }
    }

    public static int getPlanIndexByName(String planName) {
        try {
            if (!CommonFileUtil.isPlanFileNotExistOrEmpty()) {
                JSONArray ja_plans = new JSONArray(CommonFileUtil.readPlanFile());
                JSONObject jo;

                for (int i = 0; i < ja_plans.length(); i++) {
                    jo = ja_plans.getJSONObject(i);
                    if (jo.getString(PLAN_NAME).equals(planName))
                        return i;
                }
                return -1;
            }
            return -1;
        }
        catch (JSONException e) {
            e.printStackTrace();
            return -1;
        }
    }

    public static boolean isPlanNotDuplicated(String planName) {
        try {
            if (!CommonFileUtil.isPlanFileNotExistOrEmpty()) {
                JSONArray ja_plans = new JSONArray(CommonFileUtil.readPlanFile());
                JSONObject jo;

                for (int i = 0; i < ja_plans.length(); i++) {
                    jo = ja_plans.getJSONObject(i);
                    if (jo.getString(PLAN_NAME).equals(planName))
                        return false;
                }
                return true;
            }
            return true;
        }
        catch (JSONException e) {
            e.printStackTrace();
            return true;
        }
    }

    public static void removePlan(int index) {
        try {
            if (!CommonFileUtil.isPlanFileNotExistOrEmpty()) {
                JSONArray ja_plans = new JSONArray(CommonFileUtil.readPlanFile());

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

                CommonFileUtil.writePlanFile(ja_plans.toString());

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
            if (!CommonFileUtil.isPlanFileNotExistOrEmpty()) {
                JSONArray ja_plans = new JSONArray(CommonFileUtil.readPlanFile());

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

                CommonFileUtil.writePlanFile(ja_plans.toString());

                Utility.toastShort(AppController.getInstance().getString(R.string.plan_remove_completed));
                Log.i(TAG, "removePlan: " + ja_plans.toString());
            }
        }
        catch (JSONException e) {
            e.printStackTrace();
        }
    }

    public static void addTrack(String time, String name, int difficulty, String description, String polyline, String distance, String speed, String duration) {
        try {
            String ja_string = TrackingFileUtil.readTrackFile();
            JSONArray ja_track;

            if (ja_string == null)
                ja_track = new JSONArray();
            else
                ja_track = new JSONArray(ja_string);

            JSONObject jo = new JSONObject();
            jo.put(TRACK_DATE, time);
            jo.put(TRACK_NAME, name);
            jo.put(TRACK_DIFFICULTY, difficulty);
            jo.put(TRACK_DESCRIPTION, description);
            jo.put(TRACK_POLYLINE, polyline);
            jo.put(TRACK_DISTANCE, distance);
            jo.put(TRACK_SPEED, speed);
            jo.put(TRACK_DURATION, duration);

            ja_track.put(jo);

            TrackingFileUtil.writeTrackFile(ja_track.toString());

            Log.i(TAG, "addTrackFile: " + ja_track.toString());
        }
        catch (JSONException e) {
            e.printStackTrace();
        }
    }

    public static void removeTrack(int index) {
        String ja_string = TrackingFileUtil.readTrackFile();

        if (ja_string != null) {
            try {
                JSONArray ja_track = new JSONArray(ja_string);

                int len = ja_track.length();

                ArrayList<JSONObject> tempJA = new ArrayList<>();

                for (int i = 0; i < len; i++) {
                    if (i != index)
                        tempJA.add(ja_track.getJSONObject(i));
                }

                ja_track = new JSONArray();

                for (JSONObject jo : tempJA) {
                    ja_track.put(jo);
                }

                TrackingFileUtil.writeTrackFile(ja_track.toString());

                Utility.toastShort(AppController.getInstance().getString(R.string.track_delete_completed));
                Log.i(TAG, "removeTrack: " + ja_track.toString());
            }
            catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }

    public static void removeMultiTrack(ArrayList<Integer> checkedList) {
        String ja_string = TrackingFileUtil.readTrackFile();

        if (ja_string != null)
        {
            try {
                JSONArray ja_track = new JSONArray(ja_string);

                ArrayList<JSONObject> tempJA = new ArrayList<>();
                boolean isNotRemovable;

                for (int i = 0; i < ja_track.length(); i++) {
                    isNotRemovable = true;

                    for (Integer index : checkedList) {
                        if (i == index) {
                            isNotRemovable = false;
                            break;
                        }
                    }
                    if (isNotRemovable)
                        tempJA.add(ja_track.getJSONObject(i));
                }

                ja_track = new JSONArray();

                for (JSONObject jo : tempJA) {
                    ja_track.put(jo);
                }

                TrackingFileUtil.writeTrackFile(ja_track.toString());
                Utility.toastShort(AppController.getInstance().getString(R.string.track_delete_completed));

                Log.i(TAG, "removeTrack: " + ja_track.toString());
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
                JSONArray ja_track = new JSONArray(ja_string);

                ja_track.getJSONObject(index).put(TRACK_NAME, name);
                ja_track.getJSONObject(index).put(TRACK_DIFFICULTY, difficulty);
                ja_track.getJSONObject(index).put(TRACK_DESCRIPTION, description);

                TrackingFileUtil.writeTrackFile(ja_track.toString());

                Log.i(TAG, "TrackFileUpdated: " + ja_track.toString());
            }
            catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }

    public static void hardModifyTrackInfo() {
        String ja_string = TrackingFileUtil.readTrackFile();

        if (ja_string != null) {
            try {
                JSONArray ja_track = new JSONArray(ja_string);

                for (int i = 0; i < ja_track.length(); i++) {
                    ja_track.getJSONObject(i).put(TRACK_SPEED, "");
                    ja_track.getJSONObject(i).put(TRACK_DURATION, "");
                }

                TrackingFileUtil.writeTrackFile(ja_track.toString());

                Log.i(TAG, "TrackFileUpdated: " + ja_track.toString());
            }
            catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }
}