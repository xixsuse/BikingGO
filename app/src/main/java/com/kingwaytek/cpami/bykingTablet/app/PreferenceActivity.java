package com.kingwaytek.cpami.bykingTablet.app;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.GridView;

import com.kingwaytek.cpami.bykingTablet.R;
import com.kingwaytek.cpami.bykingTablet.app.VersionUpdate.Update;
import com.sonavtek.sonav.MapView;
import com.sonavtek.sonav.PathFinder;

/**
 * Activity for reading/editing preferences.
 *
 * @author Harvey Cheng(harvey@kingwaytek.com)
 */
public class PreferenceActivity extends Activity implements OnItemClickListener {
    // extends FlowNodeActivity implements OnItemClickListener {

    /** Preference name for the directory contains data for engine. */
    public static final String PREF_DATA_DIR = "dataDir";

    /** Preference name for enable/disable showing Meteorol. view on map. */
    public static final String PREF_METEOROL_ENABLED = "meteorol.";

    /**
     * Preference name for enable/disable showing confirm dialog when accessing
     * data from Internet.
     */
    public static final String PREF_INET_CONFIRM_ENABLED = "inetConfirmEnabled";

    /**
     * Preference name for enable/disable showing confirm dialog to ask user to
     * do track record or not when starting navigation.
     */
    public static final String PREF_TRACK_CONFIRM_ENABLED = "trackConfirmEnabled";

    /** Preference name for enable/disable announcement activity when startup. */
    public static final String PREF_ANNOUNCE_ENABLED = "announceEnabled";

    /** Preference name for setting range of surround. */
    public static final String PREF_SURROUND_RANGE = "surroundRange";

    /** Preference name for enable/disable POI. */
    public static final String PREF_POI_ENABLED = "poiEnabled";

    /** Preference name for enable/disable GCN. */
    public static final String PREF_GCM_ENABLED = "gcnEnabled";

    /** Preference name for enable/disable GPS. */
    public static final String PREF_GPS_ENABLED = "gpsEnabled";

    /** Preference name for setting routing method. */
    public static final String PREF_ROUTING_METHOD = "routingMethod";

    /** Preference name for setting mode of map view. */
    public static final String PREF_MAP_VIEW_TYPE = "mapViewType";

    public static final String PREF_MAP_STYLE = "mapstyle";

    /** Preference name for setting type of speech sounds. */
    public static final String PREF_SOUND_TYPE = "soundType";

    /** Preference name for setting age of user. */
    public static final String PREF_USER_AGE = "userAge";

    /** Preference name for setting sex of user. */
    public static final String PREF_USER_SEX = "userSex";

    /** Preference name for setting height of user. */
    public static final String PREF_USER_HEIGHT = "userHeight";

    /** Preference name for setting weight of user. */
    public static final String PREF_USER_WEIGHT = "userWeight";

    /** User is male */
    public static final int MALE = 1;

    /** User is female */
    public static final int FEMALE = 2;

    private Class<?>[] destinationList = new Class<?>[] {
            OperationSetting.class, NaviSetting.class, HealthManager.class,
            Update.class, About.class, MapDownloadActivity.class };

    /**
     * 2016/04/14
     *
     * Modified by Vincent.
     */
    @Override
    public void onCreate(Bundle icicle) {
        super.onCreate(icicle);

        setContentView(R.layout.preferencelist);

        GridView gridView = (GridView) this.findViewById(R.id.gridView1);

        PreferenceAdapter adapter = new PreferenceAdapter(this);

        gridView.setAdapter(adapter);

        gridView.setOnItemClickListener(this);
    }

    @Override
    public void onItemClick(AdapterView<?> arg0, View arg1, int arg2, long arg3) {

        Intent intent = new Intent();

        Class<?> destinationActivity = destinationList[arg2];

        intent.setClass(this, destinationActivity);

        this.startActivityForResult(intent, 600);
    }

    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if(resultCode == -10){
            Intent intent = new Intent();
            intent.putExtra("FINISH", 1);
            setResult(-10, intent);
            finish();
        }

        if (resultCode == RESULT_OK) {

        } else if (resultCode == RESULT_FIRST_USER) {
            setResult(RESULT_FIRST_USER);
            finish();
        }
    }

    /**
     * Get instance of {@link SharedPreferences} of the context.
     *
     * @param ctx The context.
     * @return instance of {@link SharedPreferences}.
     */
    public static SharedPreferences getSharedPreferences(Context ctx) {
        return ctx.getSharedPreferences(ctx.getString(R.string.preference_file), MODE_PRIVATE);
    }

    /**
     * Get instance of SharedPreferences.Editor of the context.
     *
     * @param ctx
     *            The context.
     * @return instance of SharedPreferences.Editor .
     */
    public static SharedPreferences.Editor getSharedPreferencesEditor(Context ctx) {
        return ctx.getSharedPreferences(ctx.getString(R.string.preference_file), MODE_PRIVATE).edit();
    }

    /**
     * 以屬性名稱查找對應的值.
     *
     * @param ctx
     *            The context.
     * @param name
     *            Name of attribute.
     * @param defaultVal
     *            The default value if value not found.
     * @return The value of the attribute.
     */
    public static String getString(Context ctx, String name, String defaultVal) {
        return getSharedPreferences(ctx).getString(name, defaultVal);
    }

    /**
     * 依屬性名稱設定值.
     *
     * @param ctx
     *            The context.
     * @param name
     *            Name of attribute.
     * @param value
     *            The value to set.
     */
    public static void setString(Context ctx, String name, String value) {
        SharedPreferences.Editor editor = getSharedPreferencesEditor(ctx);
        editor = editor.putString(name, value);
        editor.commit();
    }

    /**
     * Get the path contains data for engine.
     *
     * @param ctx
     *            The context.
     * @return The path contains data for engine.
     */
    public static String getDataDirectory(Context ctx) {
        return getSharedPreferences(ctx).getString(PREF_DATA_DIR, ctx.getResources().getString(R.string.data_dir));
    }

    /**
     * Set the path contains data for engine.
     *
     * @param ctx
     *            The context.
     * @param path
     *            The path contains data for engine.
     */
    public static void setDataDirectory(Context ctx, String path) {
        setString(ctx, PREF_DATA_DIR, path);
    }

    /**
     * 判斷是否顯示氣象資訊.
     *
     * @param ctx
     *            The context.
     * @return True if enabled, false if disabled or value not found.
     */
    public static String isMeteorolEnabled(Context ctx) {
        return getSharedPreferences(ctx).getString(PREF_METEOROL_ENABLED, ctx.getResources().getString(R.string.meteorol));
    }

    /**
     * 設定是否顯示氣象資訊.
     *
     * @param ctx
     *            The context.
     * @param enabled
     *            Set true for enabled or false for disabled.
     */
    public static void setMeteorolEnabled(Context ctx, boolean enabled) {
        setString(ctx, PREF_METEOROL_ENABLED, String.valueOf(enabled));
    }

    /**
     * 判斷是否開啟連線提示.
     *
     * @param ctx
     *            The context.
     * @return True if enabled, false if disabled or value not found.
     */
    public static String isInternetConfirmEnabled(Context ctx) {
        return getSharedPreferences(ctx).getString(PREF_INET_CONFIRM_ENABLED,
                ctx.getResources().getString(R.string.inetConfirmEnabled));
    }

    /**
     * 設定是否開啟連線提示.
     *
     * @param ctx
     *            The context.
     * @param enabled
     *            Set true for enabled or false for disabled.
     */
    public static void setInternetConfirmEnabled(Context ctx, boolean enabled) {
        setString(ctx, PREF_INET_CONFIRM_ENABLED, String.valueOf(enabled));
    }

    /**
     * 判斷是否於導航開始前詢問使用者是否進行軌跡錄製.
     *
     * @param ctx
     *            The context.
     * @return True if enabled, false if disabled or value not found.
     */
    public static String isTrackConfirmEnabled(Context ctx) {
        return getSharedPreferences(ctx).getString(PREF_TRACK_CONFIRM_ENABLED, ctx.getResources().getString(R.string.trackConfirmEnabled));
    }

    /**
     * 設定是否於導航開始前詢問使用者是否進行軌跡錄製.
     *
     * @param ctx
     *            The context.
     * @param enabled
     *            Set true for enabled or false for disabled.
     */
    public static void setTrackConfirmEnabled(Context ctx, boolean enabled) {
        setString(ctx, PREF_TRACK_CONFIRM_ENABLED, String.valueOf(enabled));
    }

    /**
     * 判斷是否開啟使用聲明提示.
     *
     * @param ctx
     *            The context.
     * @return True if enabled, false if disabled or value not found.
     */
    public static String isAnnouncementEnabled(Context ctx) {
        return getSharedPreferences(ctx).getString(PREF_ANNOUNCE_ENABLED, ctx.getResources().getString(R.string.announceEnabled));
    }

    /**
     * 設定是否開啟使用聲明提示.
     *
     * @param ctx
     *            The context.
     * @param enabled
     *            Set true for enabled or false for disabled.
     */
    public static void setAnnouncementEnabled(Context ctx, boolean enabled) {
        setString(ctx, PREF_ANNOUNCE_ENABLED, String.valueOf(enabled));
    }

    /**
     * 取得POI查詢的距離範圍.
     *
     * @param ctx
     *            The context.u
     * @return The distance of range in meter.
     */
    public static String getSurroundRange(Context ctx) {
        return getSharedPreferences(ctx).getString(PREF_SURROUND_RANGE,
                ctx.getResources().getString(R.string.surroundRange));
    }

    /**
     * 設定POI查詢的距離範圍.
     *
     * @param ctx
     *            The context.
     * @param distance
     *            Set distance in meter.
     */
    public static void setSurroundRange(Context ctx, int distance) {
        setString(ctx, PREF_SURROUND_RANGE, String.valueOf(distance));
    }

    /**
     * 判斷是否開啟POI.
     *
     * @param ctx
     *            The context.
     * @return True if enabled, false if disabled or value not found.
     */
    public static String isPOIEnabled(Context ctx) {
        return getSharedPreferences(ctx).getString(PREF_POI_ENABLED,
                ctx.getResources().getString(R.string.poiEnabled));
    }

    /**
     * 設定是否開啟POI.
     *
     * @param ctx
     *            The context.
     * @param enabled
     *            Set true for enabled or false for disabled.
     */
    public static void setPOIEnabled(Context ctx, boolean enabled) {
        setString(ctx, PREF_POI_ENABLED, String.valueOf(enabled));
    }

    /**
     * 判斷是否開啟GCN.
     *
     * @param ctx
     *            The context.
     * @return True if enabled, false if disabled or value not found.
     */
    public static String isGCMEnabled(Context ctx) {
        return getSharedPreferences(ctx).getString(PREF_GCM_ENABLED,
                ctx.getResources().getString(R.string.gcmEnabled));
    }

    /**
     * 設定是否開啟GCN.
     *
     * @param ctx
     *            The context.
     * @param enabled
     *            Set true for enabled or false for disabled.
     */
    public static void setGCMEnabled(Context ctx, boolean enabled) {
        setString(ctx, PREF_GCM_ENABLED, String.valueOf(enabled));
    }

    /**
     * 判斷是否開啟GPS.
     *
     * @param ctx
     *            The context.
     * @return True if enabled, false if disabled or value not found.
     */
    public static boolean isGpsEnabled(Context ctx) {
        return getSharedPreferences(ctx).getBoolean(PREF_GPS_ENABLED,
                ctx.getResources().getBoolean(R.bool.gpsEnabled));
    }

    /**
     * 設定是否開啟GPS.
     *
     * @param ctx
     *            The context.
     * @param enabled
     *            Set true for enabled or false for disabled.
     */
    public static void setGpsEnabled(Context ctx, boolean enabled) {
        setString(ctx, PREF_GPS_ENABLED, String.valueOf(enabled));
    }

    /**
     * 取得路徑規畫方式.
     *
     * @param ctx
     *            The context.
     * @return The method for routing plan. Default is
     *         {@link PathFinder#BICYCLE}.
     */
    public static String getRoutingMethod(Context ctx) {
        return getSharedPreferences(ctx).getString(PREF_ROUTING_METHOD,
                ctx.getResources().getString(R.string.routingMethod));
    }

    /**
     * 設定路徑規畫方式.
     *
     * @param ctx
     *            The context.
     * @param method
     *            The method to set.
     */
    public static void setRoutingMethod(Context ctx, int method) {
        setString(ctx, PREF_ROUTING_METHOD, String.valueOf(method));
    }

    /**
     * 取得地圖顯示方式.
     *
     * @param ctx
     *            The context.
     * @return The view type of map. Default is {@link MapView#VIEW_2D}.
     */
    public static String getMapViewType(Context ctx) {
        return getSharedPreferences(ctx).getString(PREF_MAP_STYLE,
                ctx.getResources().getString(R.string.mapStyle));
    }

    /**
     * 設定地圖顯示方式.
     *
     * @param ctx
     *            The context.
     * @param type
     *            The view type to set.
     */
    public static void setMapViewType(Context ctx, int type) {
        setString(ctx, PREF_MAP_VIEW_TYPE, String.valueOf(type));
    }

    /**
     * 取得地圖配色.
     *
     * @param ctx
     *            The context.
     * @return The view type of map. Default is {@link MapView#VIEW_2D}.
     */
    public static String getMapStyle(Context ctx) {
        return getSharedPreferences(ctx).getString(PREF_MAP_STYLE,
                ctx.getResources().getString(R.string.mapViewType));
    }

    /**
     * 設定地圖配色.
     *
     * @param ctx
     *            The context.
     * @param type
     *            The view type to set.
     */
    public static void setMapStyle(Context ctx, int type) {
        setString(ctx, PREF_MAP_STYLE, String.valueOf(type));
    }
    /**
     * 取得語音類型.
     *
     * @param ctx
     *            The context.
     * @return The type of sound for playing. Default is
     *         {@link eeego#SOUND_CHINESE}.
     */
    public static String getSoundType(Context ctx) {
        return getSharedPreferences(ctx).getString(PREF_SOUND_TYPE,
                ctx.getResources().getString(R.string.soundType));
    }

    /**
     * 設定語音類型.
     *
     * @param ctx
     *            The context.
     * @param type
     *            The type to set.
     */
    public static void setSoundType(Context ctx, int type) {
        setString(ctx, PREF_SOUND_TYPE, String.valueOf(type));
    }

    /**
     * 取得使用者年齡.
     *
     * @param ctx
     *            The context.
     * @return The age of user. Default is 0 for unknown.
     */
    public static String getUserAge(Context ctx) {
        return getSharedPreferences(ctx).getString(PREF_USER_AGE,
                ctx.getResources().getString(R.string.userAge));
    }

    /**
     * 設定使用者年齡.
     *
     * @param ctx
     *            The context.
     * @param age
     *            The age to set.
     */
    public static void setUserAge(Context ctx, int age) {
        setString(ctx, PREF_USER_AGE, String.valueOf(age));
    }

    /**
     * 取得使用者性別.
     *
     * @param ctx
     *            The context.
     * @return The sex of user. Default is 0 for unknown.
     */
    public static String getUserSex(Context ctx) {
        return getSharedPreferences(ctx).getString(PREF_USER_SEX,
                ctx.getResources().getString(R.string.userSex));
    }

    /**
     * 設定使用者性別.
     *
     * @param ctx
     *            The context.
     * @param sex
     *            The sex to set.
     */
    public static void setUserSex(Context ctx, int sex) {
        setString(ctx, PREF_USER_SEX, String.valueOf(sex));
    }

    /**
     * 取得使用者身高.
     *
     * @param ctx
     *            The context.
     * @return The height of user. Default is 0 for unknown.
     */
    public static String getUserHeight(Context ctx) {
        return getSharedPreferences(ctx).getString(PREF_USER_HEIGHT,
                ctx.getResources().getString(R.string.userHeight));
    }

    /**
     * 設定使用者身高.
     *
     * @param ctx
     *            The context.
     * @param height
     *            The height to set.
     */
    public static void setUserHeight(Context ctx, int height) {
        setString(ctx, PREF_USER_HEIGHT, String.valueOf(height));
    }

    /**
     * 取得使用者體重.
     *
     * @param ctx
     *            The context.
     * @return The weight of user. Default is 0 for unknown.
     */
    public static String getUserWeight(Context ctx) {
        return getSharedPreferences(ctx).getString(PREF_USER_WEIGHT,
                ctx.getResources().getString(R.string.userWeight));
    }

    /**
     * 設定使用者體重.
     *
     * @param ctx
     *            The context.
     * @param weight
     *            The weight to set.
     */
    public static void setUserWeight(Context ctx, int weight) {
        setString(ctx, PREF_USER_WEIGHT, String.valueOf(weight));
    }

}