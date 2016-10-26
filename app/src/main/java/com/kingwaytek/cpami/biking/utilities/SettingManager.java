package com.kingwaytek.cpami.biking.utilities;

import android.content.Context;
import android.content.SharedPreferences;

import com.kingwaytek.cpami.biking.AppController;
import com.kingwaytek.cpami.biking.R;

/**
 * 所有 SharedPreferences的設定都在這裡做！<br>
 *
 * Context一律使用 ApplicationContext。
 *
 * @author Vincent (2016/04/14)
 */
public class SettingManager {

    private static final String TAG = "SettingManager";

    /** Preference name for enable/disable announcement activity when startup. */
    private static final String PREF_ANNOUNCE_ENABLED = "announceEnabled";

    /** Preference name for setting age of user. */
    private static final String PREF_USER_AGE = "userAge";

    /** Preference name for setting sex of user. */
    private static final String PREF_USER_SEX = "userSex";

    /** Preference name for setting height of user. */
    private static final String PREF_USER_HEIGHT = "userHeight";

    /** Preference name for setting weight of user. */
    private static final String PREF_USER_WEIGHT = "userWeight";

    private static final String PREF_APP_FIRST_LAUNCH = "AppFirstLaunch";

    /** MainMap Map Layer */
    public static final String PREFS_MARKER_MY_POI = "MyPoiMarker";
    public static final String PREFS_LAYER_CYCLING_1 = "LayerCycling1";
    public static final String PREFS_LAYER_TOP_TEN = "LayerTopTen";
    public static final String PREFS_LAYER_RECOMMENDED = "LayerRecommended";
    public static final String PREFS_LAYER_ALL_OF_TAIWAN = "LayerAllOfTaiwan";
    public static final String PREFS_LAYER_RENT_STATION = "LayerRentStation";
    public static final String PREFS_LAYER_YOU_BIKE = "LayerYouBike";

    public static SharedPreferences prefs;
    private static SharedPreferences.Editor editor;

    private static Context appContext() {
        return AppController.getInstance().getAppContext();
    }

    private static SharedPreferences getPreferences(String name) {
        return appContext().getSharedPreferences(name, Context.MODE_PRIVATE);
    }

    public static void initPreferences() {
        prefs = getPreferences(appContext().getString(R.string.preference_file));
        editor = prefs.edit();
    }

    /**
     * 判斷是否開啟使用聲明提示.
     *
     * @return True if enabled, false if disabled or value not found.
     */
    public static boolean isAnnouncementNecessary() {
        return prefs.getBoolean(PREF_ANNOUNCE_ENABLED, appContext().getResources().getBoolean(R.bool.isAnnounceNecessary));
    }

    /**
     * 設定是否開啟使用聲明提示.
     *
     * @param enabled Set true for enabled or false for disabled.
     */
    public static void setAnnouncementNecessary(boolean enabled) {
        editor.putBoolean(PREF_ANNOUNCE_ENABLED, enabled).apply();
    }

    /**
     * 取得使用者年齡.
     *
     * @return The age of user. Default is 0 for unknown.
     */
    public static int getUserAge() {
        return prefs.getInt(PREF_USER_AGE, appContext().getResources().getInteger(R.integer.userAge));
    }

    /**
     * 設定使用者年齡.
     *
     * @param age The age to set.
     */
    public static void setUserAge(int age) {
        editor.putInt(PREF_USER_AGE, age).apply();
    }

    /**
     * 取得使用者性別.
     *
     * @return The sex of user. Default is 0 for unknown.
     */
    public static int getUserSex() {
        return prefs.getInt(PREF_USER_SEX, appContext().getResources().getInteger(R.integer.userSex));
    }

    /**
     * 設定使用者性別.
     *
     * @param sex The sex to set.
     */
    public static void setUserSex(int sex) {
        editor.putInt(PREF_USER_SEX, sex).apply();
    }

    /**
     * 取得使用者身高.
     *
     * @return The height of user. Default is 0 for unknown.
     */
    public static int getUserHeight() {
        return prefs.getInt(PREF_USER_HEIGHT, appContext().getResources().getInteger(R.integer.userHeight));
    }

    /**
     * 設定使用者身高.
     *
     * @param height The height to set.
     */
    public static void setUserHeight(int height) {
        editor.putInt(PREF_USER_HEIGHT, height).apply();
    }

    /**
     * 取得使用者體重.
     *
     * @return The weight of user. Default is 0 for unknown.
     */
    public static int getUserWeight() {
        return prefs.getInt(PREF_USER_WEIGHT, appContext().getResources().getInteger(R.integer.userWeight));
    }

    /**
     * 設定使用者體重.
     *
     * @param weight The weight to set.
     */
    public static void setUserWeight(int weight) {
        editor.putInt(PREF_USER_WEIGHT, weight).apply();
    }

    public static void setAppFirstLaunch(boolean launched) {
        editor.putBoolean(PREF_APP_FIRST_LAUNCH, launched);
    }

    public static boolean getAppFirstLaunch() {
        return prefs.getBoolean(PREF_APP_FIRST_LAUNCH, true);
    }

    public static class MapLayer {

        public static void setMyPoiFlag(boolean isChecked) {
            editor.putBoolean(PREFS_MARKER_MY_POI, isChecked).apply();
        }

        public static boolean getMyPoiFlag() {
            return prefs.getBoolean(PREFS_MARKER_MY_POI, true);
        }

        public static void setCyclingLayer(boolean isChecked) {
            editor.putBoolean(PREFS_LAYER_CYCLING_1, isChecked).apply();
        }

        public static boolean getCyclingLayer() {
            return prefs.getBoolean(PREFS_LAYER_CYCLING_1, false);
        }

        public static void setTopTenLayer(boolean isChecked) {
            editor.putBoolean(PREFS_LAYER_TOP_TEN, isChecked).apply();
        }

        public static boolean getTopTenLayer() {
            return prefs.getBoolean(PREFS_LAYER_TOP_TEN, false);
        }

        public static void setRecommendedLayer(boolean isChecked) {
            editor.putBoolean(PREFS_LAYER_RECOMMENDED, isChecked).apply();
        }

        public static boolean getRecommendedLayer() {
            return prefs.getBoolean(PREFS_LAYER_RECOMMENDED, false);
        }

        public static void setAllOfTaiwanLayer(boolean isChecked) {
            editor.putBoolean(PREFS_LAYER_ALL_OF_TAIWAN, isChecked).apply();
        }

        public static boolean getAllOfTaiwanLayer() {
            return prefs.getBoolean(PREFS_LAYER_ALL_OF_TAIWAN, false);
        }

        public static void setRentStationLayer(boolean isChecked) {
            editor.putBoolean(PREFS_LAYER_RENT_STATION, isChecked).apply();
        }

        public static boolean getRentStationLayer() {
            return prefs.getBoolean(PREFS_LAYER_RENT_STATION, false);
        }

        public static void setYouBikeLayer(boolean isChecked) {
            editor.putBoolean(PREFS_LAYER_YOU_BIKE, isChecked).apply();
        }

        public static boolean getYouBikeLayer() {
            return prefs.getBoolean(PREFS_LAYER_YOU_BIKE, false);
        }
    }

    public static class TrackingTimeAndLayer {

        private static final String PREFS_START_TIME_MILLIS = "StartTimeMillis";
        private static final String PREFS_END_TIME_MILLIS = "EndTimeMillis";
        public static final String PREFS_TRACK_MAP_LAYER_CYCLING_1 = "TrackMapLayerCycling1";
        public static final String PREFS_TRACK_MAP_LAYER_TOP_TEN = "TrackMapLayerTopTen";
        public static final String PREFS_TRACK_MAP_LAYER_RECOMMENDED = "TrackMapLayerRecommended";
        public static final String PREFS_TRACK_MAP_LAYER_ALL_OF_TAIWAN = "TrackMapLayerAllOfTaiwan";

        public static void setStartTime(long currentTime) {
            editor.putLong(PREFS_START_TIME_MILLIS, currentTime).apply();
        }

        public static long getStartTime() {
            return prefs.getLong(PREFS_START_TIME_MILLIS, 0);
        }

        public static void clearStartTime() {
            setStartTime(0);
        }

        public static void setEndTime(long endTime) {
            editor.putLong(PREFS_END_TIME_MILLIS, endTime).apply();
        }

        public static long getEndTime() {
            return prefs.getLong(PREFS_END_TIME_MILLIS, 0);
        }

        public static void setCyclingLayer(boolean isChecked) {
            editor.putBoolean(PREFS_TRACK_MAP_LAYER_CYCLING_1, isChecked).apply();
        }

        public static boolean getCyclingLayer() {
            return prefs.getBoolean(PREFS_TRACK_MAP_LAYER_CYCLING_1, false);
        }

        public static void setTopTenLayer(boolean isChecked) {
            editor.putBoolean(PREFS_TRACK_MAP_LAYER_TOP_TEN, isChecked).apply();
        }

        public static boolean getTopTenLayer() {
            return prefs.getBoolean(PREFS_TRACK_MAP_LAYER_TOP_TEN, false);
        }

        public static void setRecommendedLayer(boolean isChecked) {
            editor.putBoolean(PREFS_TRACK_MAP_LAYER_RECOMMENDED, isChecked).apply();
        }

        public static boolean getRecommendedLayer() {
            return prefs.getBoolean(PREFS_TRACK_MAP_LAYER_RECOMMENDED, false);
        }

        public static void setAllOfTaiwanLayer(boolean isChecked) {
            editor.putBoolean(PREFS_TRACK_MAP_LAYER_ALL_OF_TAIWAN, isChecked).apply();
        }

        public static boolean getAllOfTaiwanLayer() {
            return prefs.getBoolean(PREFS_TRACK_MAP_LAYER_ALL_OF_TAIWAN, false);
        }
    }
}
