package com.kingwaytek.cpami.bykingTablet.utilities;

import android.content.Context;
import android.content.SharedPreferences;

import com.kingwaytek.cpami.bykingTablet.AppController;
import com.kingwaytek.cpami.bykingTablet.R;
import com.sonavtek.sonav.MapView;
import com.sonavtek.sonav.PathFinder;

/**
 * 所有 SharedPreferences的設定都在這裡做！<br>
 *
 * Context一律使用 ApplicationContext。
 *
 * @author Vincent (2016/04/14)
 */
public class SettingManager {

    /** Preference name for the directory contains data for engine. */
    private static final String PREF_DATA_DIR = "dataDir";

    /** Preference name for enable/disable showing Meteorol. view on map. */
    private static final String PREF_METEOROL_ENABLED = "meteorol.";

    /**
     * Preference name for enable/disable showing confirm dialog when accessing
     * data from Internet.
     */
    private static final String PREF_INET_CONFIRM_ENABLED = "inetConfirmEnabled";

    /**
     * Preference name for enable/disable showing confirm dialog to ask user to
     * do track record or not when starting navigation.
     */
    private static final String PREF_TRACK_CONFIRM_ENABLED = "trackConfirmEnabled";

    /** Preference name for enable/disable announcement activity when startup. */
    private static final String PREF_ANNOUNCE_ENABLED = "announceEnabled";

    /** Preference name for setting range of surround. */
    private static final String PREF_SURROUND_RANGE = "surroundRange";

    /** Preference name for enable/disable POI. */
    private static final String PREF_POI_ENABLED = "poiEnabled";

    /** Preference name for enable/disable GCN. */
    private static final String PREF_GCM_ENABLED = "gcnEnabled";

    /** Preference name for enable/disable GPS. */
    private static final String PREF_GPS_ENABLED = "gpsEnabled";

    /** Preference name for setting routing method. */
    private static final String PREF_ROUTING_METHOD = "routingMethod";

    /** Preference name for setting mode of map view. */
    private static final String PREF_MAP_VIEW_TYPE = "mapViewType";

    private static final String PREF_MAP_STYLE = "mapstyle";

    /** Preference name for setting type of speech sounds. */
    private static final String PREF_SOUND_TYPE = "soundType";

    /** Preference name for setting age of user. */
    private static final String PREF_USER_AGE = "userAge";

    /** Preference name for setting sex of user. */
    private static final String PREF_USER_SEX = "userSex";

    /** Preference name for setting height of user. */
    private static final String PREF_USER_HEIGHT = "userHeight";

    /** Preference name for setting weight of user. */
    private static final String PREF_USER_WEIGHT = "userWeight";

    /** User is male */
    private static final int MALE = 1;

    /** User is female */
    private static final int FEMALE = 2;

    private static SharedPreferences prefs;
    private static SharedPreferences.Editor editor;

    private static Context appContext() {
        return AppController.getInstance().getAppContext();
    }

    private static SharedPreferences getPreferences() {
        return appContext().getSharedPreferences(appContext().getString(R.string.preference_file), Context.MODE_PRIVATE);
    }

    public static void initPreferences() {
        prefs = getPreferences();
        editor = prefs.edit();
    }

    /**
     * Get the path contains data for engine.
     *
     * @return The path contains data for engine.
     */
    public static String getDataDirectory() {
        return prefs.getString(PREF_DATA_DIR, appContext().getString(R.string.data_dir));
    }

    /**
     * 判斷是否顯示氣象資訊.
     *
     * @return True if enabled, false if disabled or value not found.
     */
    public static boolean isMeteorologyEnabled() {
        return prefs.getBoolean(PREF_METEOROL_ENABLED, appContext().getResources().getBoolean(R.bool.meteorol));
    }

    /**
     * 設定是否顯示氣象資訊.
     *
     * @param enabled Set true for enabled or false for disabled.
     */
    public static void setMeteorologyEnabled(boolean enabled) {
        editor.putBoolean(PREF_METEOROL_ENABLED, enabled).apply();
    }

    /**
     * 判斷是否開啟連線提示.
     *
     * @return True if enabled, false if disabled or value not found.
     */
    public static boolean isInternetConfirmEnabled() {
        return prefs.getBoolean(PREF_INET_CONFIRM_ENABLED, appContext().getResources().getBoolean(R.bool.inetConfirmEnabled));
    }

    /**
     * 設定是否開啟連線提示.
     *
     * @param enabled Set true for enabled or false for disabled.
     */
    public static void setInternetConfirmEnabled(boolean enabled) {
        editor.putBoolean(PREF_INET_CONFIRM_ENABLED, enabled).apply();
    }

    /**
     * 判斷是否於導航開始前詢問使用者是否進行軌跡錄製.
     *
     * @return True if enabled, false if disabled or value not found.
     */
    public static boolean isTrackConfirmEnabled() {
        return prefs.getBoolean(PREF_TRACK_CONFIRM_ENABLED, appContext().getResources().getBoolean(R.bool.trackConfirmEnabled));
    }

    /**
     * 設定是否於導航開始前詢問使用者是否進行軌跡錄製.
     *
     * @param enabled Set true for enabled or false for disabled.
     */
    public static void setTrackConfirmEnabled(boolean enabled) {
        editor.putBoolean(PREF_TRACK_CONFIRM_ENABLED, enabled).apply();
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
     * 取得POI查詢的距離範圍.
     *
     * @return The distance of range in meter.
     */
    public static int getSurroundRange() {
        return prefs.getInt(PREF_SURROUND_RANGE, appContext().getResources().getInteger(R.integer.surroundRange));
    }

    /**
     * 設定POI查詢的距離範圍.
     *
     * @param distance Set distance in meter.
     */
    public static void setSurroundRange(int distance) {
        editor.putInt(PREF_SURROUND_RANGE, distance).apply();
    }

    /**
     * 判斷是否開啟POI.
     *
     * @return True if enabled, false if disabled or value not found.
     */
    public static boolean isPOIEnabled() {
        return prefs.getBoolean(PREF_POI_ENABLED, appContext().getResources().getBoolean(R.bool.poiEnabled));
    }

    /**
     * 設定是否開啟POI.
     *
     * @param enabled Set true for enabled or false for disabled.
     */
    public static void setPOIEnabled(boolean enabled) {
        editor.putBoolean(PREF_POI_ENABLED, enabled).apply();
    }

    /**
     * 判斷是否開啟GCN.
     *
     * @return True if enabled, false if disabled or value not found.
     */
    public static boolean isGCMEnabled() {
        return prefs.getBoolean(PREF_GCM_ENABLED, appContext().getResources().getBoolean(R.bool.gcmEnabled));
    }

    /**
     * 設定是否開啟GCN.
     *
     * @param enabled Set true for enabled or false for disabled.
     */
    public static void setGCMEnabled(boolean enabled) {
        editor.putBoolean(PREF_GCM_ENABLED, enabled).apply();
    }

    /**
     * 取得路徑規畫方式.
     *
     * @return The method for routing plan. Default is
     *         {@link PathFinder#BICYCLE}.
     */
    public static int getRoutingMethod() {
        return prefs.getInt(PREF_ROUTING_METHOD, appContext().getResources().getInteger(R.integer.routingMethod));
    }

    /**
     * 設定路徑規畫方式.
     *
     * @param method The method to set.
     */
    public static void setRoutingMethod(int method) {
        editor.putInt(PREF_ROUTING_METHOD, method).apply();
    }

    /**
     * 取得地圖顯示方式.
     *
     * @return The view type of map. Default is {@link MapView#VIEW_2D}.
     */
    public static int getMapViewType() {
        return prefs.getInt(PREF_MAP_STYLE, appContext().getResources().getInteger(R.integer.mapViewType));
    }

    /**
     * 設定地圖顯示方式.
     *
     * @param type The view type to set.
     */
    public static void setMapViewType(int type) {
        editor.putInt(PREF_MAP_VIEW_TYPE, type).apply();
    }

    /**
     * 取得地圖配色.
     *
     * @return The view type of map. Default is {@link MapView#VIEW_2D}.
     */
    public static int getMapStyle() {
        return prefs.getInt(PREF_MAP_STYLE, appContext().getResources().getInteger(R.integer.mapStyle));
    }

    /**
     * 設定地圖配色.
     *
     * @param type The view type to set.
     */
    public static void setMapStyle(int type) {
        editor.putInt(PREF_MAP_STYLE, type).apply();
    }
    /**
     * 取得語音類型.
     *
     * @return The type of sound for playing. Default is CHINESE.
     */
    public static int getSoundType() {
        return prefs.getInt(PREF_SOUND_TYPE, appContext().getResources().getInteger(R.integer.soundType));
    }

    /**
     * 設定語音類型.
     *
     * @param type The type to set.
     */
    public static void setSoundType(int type) {
        editor.putInt(PREF_SOUND_TYPE, type).apply();
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
}
