package com.kingwaytek.cpami.bykingTablet.app;

import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnDismissListener;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.content.res.Configuration;
import android.location.Location;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.os.PowerManager;
import android.util.Log;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup.MarginLayoutParams;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.PopupWindow;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.example.actionsheet.ActionSheet;
import com.example.actionsheet.ActionSheet.ActionSheetButtonClickListener;
import com.kingwaytek.cpami.bykingTablet.R;
import com.kingwaytek.cpami.bykingTablet.app.address.CitySelection;
import com.kingwaytek.cpami.bykingTablet.app.poi.POIMethodSelection;
import com.kingwaytek.cpami.bykingTablet.app.track.TrackEngine;
import com.kingwaytek.cpami.bykingTablet.app.track.TrackEngine.TrackRecordingStatus;
import com.kingwaytek.cpami.bykingTablet.app.track.TrackRecord;
import com.kingwaytek.cpami.bykingTablet.bus.PublicTransportList;
import com.kingwaytek.cpami.bykingTablet.data.GeoPoint;
import com.kingwaytek.cpami.bykingTablet.hardware.BatteryNotifier;
import com.kingwaytek.cpami.bykingTablet.hardware.GPSListener;
import com.kingwaytek.cpami.bykingTablet.sql.Track;
import com.kingwaytek.cpami.bykingTablet.sql.TrackPoint;
import com.kingwaytek.cpami.bykingTablet.utilities.SettingManager;
import com.kingwaytek.cpami.bykingTablet.utilities.UtilDialog;
import com.kingwaytek.cpami.bykingTablet.view.ViewConstant.ActivityCaller;
import com.kingwaytek.cpami.bykingTablet.view.ViewConstant.ContextMenuOptions;
import com.kingwaytek.cpmi.maptag.MapIconDescriptionActivity;
import com.sonavtek.sonav.AbstractMapInfoHandler;
import com.sonavtek.sonav.MapView;
import com.sonavtek.sonav.PathFinder;
import com.sonavtek.sonav.ROADLISTDATA;
import com.sonavtek.sonav.sonav;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.DataOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Timer;
import java.util.TimerTask;

/**
 * This is the activity for acting with map.
 *
 * @author Harvey Cheng(harvey@kingwaytek.com)
 *
 * 2016/04/13
 * Rewritten by Vincent.
 */
public class MapActivity extends FlowNodeActivity implements OnClickListener {

    /** Event code for routing path planning done. */
    private static final int ROUTING_PLAN_END = 1;

    /** Event code for updating spend time when navigation. */
    private static final int UPDATE_NAVI_TIME = 2;

    /** Event code for updating GPS status. */
    private static final int UPDATE_GPS_STATUS = 3;

    /** Event code for navi finished. */
    private static final int NAVI_FINISHED = 4;

    /** Event code for updating timer. */
    private static final int UPDATE_CAL = 5;

    /** Request code for waiting result. */
    private static final int REQUEST_SELECT_POINT_ON_MAP = 1;

    public static final int START_POINT = 0;
    public static final int ESS1_POINT = 1;
    public static final int ESS2_POINT = 2;
    public static final int END_POINT = 3;

    public static final int START_Button = 0;
    public static final int ESSENTIAL_Button1 = 1;
    public static final int ESSENTIAL_Button2 = 2;
    public static final int END_Button = 3;

    private static int State_Flag_For_PublicTransit = 0;

    private AbstractMapInfoHandler naviHandler; // handles navigation events
    private Handler uiHandler; // UI handler for this activity
    private Handler speedHandler;
    private Handler CalHandler;
    private Timer timer; // timer for this activity
    private TimerTask naviTimeUpdator; // task for update time when navigation
    private TimerTask gpsStatusUpdator; // task for update GPS status
    private static MapView mapView;
    private int turnIconId; // for saving to be restored
    private int speedDisplayIconId; // for saving to be restored
    private ImageView turnView;
    private TextView nextRoadText;
    private TextView nowRoadText;
    private TextView moveDistance;
    private TextView leftDistance;
    private TextView spendTime;
    private ImageView gpsStatusView;
    private ImageButton changeMapViewBt;
    private static TextView WeatherText;
    private static TextView WeatherUVText;
    private static ImageView imWeather;
    private TextView TrackRecord;
    private ImageButton ibZoomIn;
    private ImageButton ibZoomOut;

    private static GeoPoint cur_GeoPoint;

    private long naviStartTime; // the time when navigation started
    private static double[] StartPointArray = { 0.0, 0.0 };
    private static double[] EndPointArray = { 0.0, 0.0 };
    private static double[] Ess1PointArray = { 0.0, 0.0 };
    private static double[] Ess2PointArray = { 0.0, 0.0 };
    public static double StartPointLonForRoadList;
    public static double StartPointLatForRoadList;
    private static TextView TextStartPoint;
    private static TextView TextEss1Point;
    private static TextView TextEss2Point;
    private static TextView TextEndPoint;
    private static String TextStartAddress = "";
    private static String TextEndAddress = "";
    private static String TextStartName = "";
    private static String TextEss1Name = "";
    private static String TextEss2Name = "";
    private static String TextEndName = "";

    private sonav engine;
    private GeoPoint geoPoint;

    private ImageView speed_digit3;
    private ImageView speed_digit2;
    private ImageView speed_digit1;
    private TextView gradient_digit;
    private TextView caltext;
    private ImageView NaviMenu;

    // // Map Option Image Buttons
    // private final ImageButton ibUserLocation;
    // private final ImageButton ibFavorite;
    // private final ImageButton ibNavigation;
    // private final ImageButton ibTrackRecord;
    // private final ImageButton ibPOI;
    // private final ImageButton ibZoomIn;
    // private final ImageButton ibZoomOut;
    // private final ImageButton ibChangeMapView;

    /*
	 * set to true if the activity is going to be destroyed because
	 * configuration changed
	 */
    private boolean isConfigChanged;
    /*
	 * @author yawhaw_ou(yawhaw@kingwaytek.com)
	 */
    public static boolean isSetStEnPointState;

    private int ButtonSrc;
    private boolean isDoEemulationNavi = true;
    private boolean StopNavigationFlag;

    public static boolean pointOnMapMode = false;

    private final int NoStartPointFlag = 1;
    private final int NoEndPointFlag = 2;
    private final int NoStartEndPointFlag = 3;
    private final String NoStartPointMsg = "請設定起點";
    private final String NoEndPointMsg = "請設定終點";
    private final String NoStartEndPointMsg = "請設定起點,終點";

    private TrackEngine tEngine;
    private Track insTrack;
    private boolean isRecordTrack = false;
    public static final int go_home = 100;
    private static boolean GO_RIGHT_NOW = false;

    private Handler weatherHandler;
    private static String[] WeatherArray;
    private final int GET_WEATHER_FINISH = 1;
    private final int WEATHER_WEB_FAIL = 2;
    private final int WEATHER_GPS_FAIL = 3;
    private final int GET_NOTHING = 4;
    private final int GET_BREAK = 5;
    private static int saveImageResource;

    private ImageButton ibZoomOutPortrait;

    private ImageButton ibZoomInPortrait;

    private ImageButton ibZoomOutlandscape;

    private ImageButton ibZoomInlandscape;

    private RelativeLayout ZoomLayoutPortrait;

    private RelativeLayout ZoomLayoutLandscape;

    private static TextView MapRecordText;

    // 取的sdk版本
    int sdkVersion = Build.VERSION.SDK_INT;

    private Handler breakHandler;

    private TextView breaktext;

    private PopupWindow pop;

    private static int PopupWindow_X = 4;
    private static int PopupWindow_landscape_Y = 70;
    private static int PopupWindow_portrait_Y = 135;
    private static int PopupWindow_landscape_Y_ForTablet = 200;
    private static int PopupWindow_portrait_Y_ForTablet = 340;

    private Handler updataTrafficConditionHandler = null;

    private String result = "";
    private StringBuffer titelstr = new StringBuffer();
    private StringBuffer titleForPop = new StringBuffer();

    private String[] Title;
    private String[] Detail;
    private String[] Lat;
    private String[] Lon;
    private String[] CityID;

    private Map<String, Double> mapForDistance;

    private RelativeLayout weatherLayout;// 平板才有

    private boolean isMapState = true;// 平板才有
    private static boolean firstIn = true;
    private boolean isnaing = false;

    private ActionSheet actionSheet;
    private ActionSheet actionSheet_navi;
    private int[][] sub_view;
    private int which_button;

    private UtilDialog progressDialog;
    private boolean isNaviFinishDialog;

    @Override
    protected void onCheckAllDone() {
        setMapViewAndEngine();
        initHandlers();
        setWidgets();
    }

    @Override
    protected void init() {
        resumeInit();
    }

    @Override
    protected String getActionBarTitle() {
        return getString(R.string.preference_navi);
    }

    @Override
    protected int getLayoutId() {
        return R.layout.map;
    }

    @Override
    protected void findViews() {
        mapView = (MapView) findViewById(R.id.mapView);
    }

    @Override
    protected void setListener() {

    }

    private void setMapViewAndEngine() {
        progressDialog = new UtilDialog(this);

        mapView.setMapActivity(MapActivity.this);
        mapView.setViewType(SettingManager.getMapViewType());

        // initialization for map
        engine = sonav.getInstance();
        engine.setvoiceself(0);
        engine.setlangvoice(SettingManager.getSoundType());
        engine.setoutwayroute(2, 5, 50);
        engine.setresizefont(1);

        int mapStyle = SettingManager.getMapStyle();

        if (mapStyle < 6)
            engine.setmapstyle(0, mapStyle, 1);
        else {
            mapStyle -= 5;
            engine.setmapstyle(1, 0, mapStyle);
        }
        engine.savenaviparameter();
    }

    private void initHandlers() {
        // create UI handler
        initialUIHandler();
        initialSpeedHandler();
        initialCalHandler();
        initialWeatherHandler();

        initialTrafficHandler();
        initialBrokeHandler();
    }

    private void setWidgets() {
        // init map option buttons
        setMapButtonListener();

        // register battery notifier
        BatteryNotifier.Register(this);

        View view = this.getLayoutInflater().inflate(R.layout.traffic_conditon_popupwindow, null);
        pop = new PopupWindow(view, 136, 25);

        breaktext = (TextView) view.findViewById(R.id.text_break_content);
        breaktext.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                UtilDialog uit = new UtilDialog(MapActivity.this) {
                    @Override
                    public void click_btn_1() {
                        super.click_btn_1();
                    }
                };
                uit.showDialog_route_plan_choice(titelstr.toString(), null, "確定", null);
            }
        });
    }

    private void resumeInit() {
        if (!hasNotInit) {
            initialNavigationEventHandler(engine);

            // checkWeatherState();
            mapView.gainMapEventOwnership();

            if (mapView.getControlMode() == MapView.STATE_MAP) {
                if (TrackEngine.getInstance().getRecordingStatus().equals(TrackRecordingStatus.RECORDING)) {
                    Log.i("MapActivity.java", "RECORDING");
                    MapRecordText.setVisibility(View.VISIBLE);
                }
                else
                    MapRecordText.setVisibility(View.INVISIBLE);
            }
        }
    }


    public void onOldCreate(Bundle icicle) {
        Log.i("MapActivity.java", "onCreate()");

        super.onCreate(icicle);

        setContentView(R.layout.map);

        progressDialog = new UtilDialog(this);
        PowerManager pm = (PowerManager) getSystemService(Context.POWER_SERVICE);
        PowerManager.WakeLock wl = pm.newWakeLock(PowerManager.SCREEN_BRIGHT_WAKE_LOCK | PowerManager.ON_AFTER_RELEASE, "BWL");

        mapView = (MapView) findViewById(R.id.mapView);

        mapView.setMapActivity(MapActivity.this);
        // initialization for map
        engine = sonav.getInstance();
        mapView.setViewType(SettingManager.getMapViewType());

        engine.setvoiceself(0);

        engine.setlangvoice(SettingManager.getSoundType());

        engine.setoutwayroute(2, 5, 50);
        // engine.seticonsize(30, 30);
        // engine.setcolorrgb(255, 255, 255);
        // engine.setalpha(0);
        // engine.setbkcolorrgb(255, 255, 255);
        engine.setresizefont(1);

        // getMapStyle();
        int mapStyle = SettingManager.getMapStyle();

        if (mapStyle < 6)
            engine.setmapstyle(0, mapStyle, 1);
        else {
            mapStyle -= 5;
            engine.setmapstyle(1, 0, mapStyle);
        }

        engine.savenaviparameter();
        // create handler for events of navigation
        initialNavigationEventHandler(engine);

        // create UI handler
        initialUIHandler();
        initialSpeedHandler();
        initialCalHandler();
        initialWeatherHandler();

        // create timer for this activity
        timer = new Timer();

        // startService(new Intent(this, TrackRecordService.class));

        // init map option buttons
        setMapButtonListener();

        // register battery notifier
        BatteryNotifier.Register(this);

        geoPoint = new GeoPoint();

        View view = this.getLayoutInflater().inflate(R.layout.traffic_conditon_popupwindow, null);
        pop = new PopupWindow(view, 136, 25);

        breaktext = (TextView) view.findViewById(R.id.text_break_content);
        breaktext.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                UtilDialog uit = new UtilDialog(MapActivity.this) {
                    @Override
                    public void click_btn_1() {
                        super.click_btn_1();
                    }
                };
                uit.showDialog_route_plan_choice(titelstr.toString(), null, "確定", null);
            }
        });

        initialTrafficHandler();
        initialBrokeHandler();
    }

    @Override
    protected void onResume() {
        resumeInit();
        super.onResume();
    }


    @Override
    protected void onDestroy() {
        //timer.cancel();

        // stop navigation or emulator if the activity is going to be destroyed and won't be resumed.
        if (!isConfigChanged) {
            int mapMode = mapView.getControlMode();

            // stop navigation or emulator
            if (mapMode == MapView.STATE_NAVI || mapMode == MapView.STATE_NAVI_PAUSE) {
                mapView.setControlMode(MapView.STATE_MAP);
            }
            else if (mapMode == MapView.STATE_EMU || mapMode == MapView.STATE_EMU_PAUSE) {
                mapView.closeNaviEmulator();
            }

            // clear routing path
            mapView.setRoutingPathVisible(false);
        }

        // unregister battery notifier
        BatteryNotifier.UnRegister(this);

        super.onDestroy();
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        Log.i("MapActivity.java", "onKeyDown");

        if (isSetStEnPointState || pointOnMapMode) {
            return false;
        }
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            if (isnaing) {
                // 正在導航
                UtilDialog uit = new UtilDialog(this) {
                    @Override
                    public void click_btn_1() {
                        if (isRecordTrack) {
                            StopTrackRecord();
                            isRecordTrack = false;
                        }
                        stopNavigation();
                        switchView(R.layout.map_options, R.id.navi_option_view, R.id.option_view);
                        engine.clearshortpath();
                        engine.clearallroutepoint();
                        engine.savenaviparameter();

                        /*** 清除marker ***/
                        engine.setflagpoint(0, -1, -1);
                        engine.setflagpoint(1, -1, -1);
                        engine.setflagpoint(2, -1, -1);
                        engine.setflagpoint(3, -1, -1);
                        engine.setflagpoint(5, -1, -1);
                        /***************/

                        setMapButtonListener();
                        isSetStEnPointState = false;
                        isnaing = false;

                        super.click_btn_1();
                    }
                };
                uit.showDialog_route_plan_choice(getString(R.string.confirm_stop_navi), null, "確定", "取消");
            }
            else {
                //goToForResult(MainActivity.class, false, ActivityCaller.MAIN.getValue());
                finish();
            }
        }
        return false;
    }

    @Override
    protected void onRestoreInstanceState(Bundle savedState) {
        super.onRestoreInstanceState(savedState);
        Log.i("MapActivity.java", "onRestoreInstanceState");

        mapView.setViewType(savedState.getInt("viewType"));

        isSetStEnPointState = savedState.getBoolean("SetStEnPointState");
        if (WeatherArray != null) {
            WeatherText.setText(savedState.getString("WeatherText"));
            WeatherUVText.setText(savedState.getString("WeatherUVText"));
            imWeather.setImageResource(savedState.getInt("imWeather"));
        }

        if (savedState.getInt("mapCtrlMode") == MapView.STATE_MAP && !isSetStEnPointState) {
            // mapView.setCenter((GeoPoint)
            // savedState.getParcelable("mapCenter"));

            // move to previous center point and change zoom level
            int prevZoomLevel = savedState.getInt("zoomLevel");
            int zoomLevel = mapView.getZoomLevel();

            if (zoomLevel < prevZoomLevel) {
                while (mapView.getZoomLevel() < prevZoomLevel) {
                    mapView.zoomOut();
                }
            } else {
                while (mapView.getZoomLevel() > prevZoomLevel) {
                    mapView.zoomIn();
                }
            }
        }
        else if (savedState.getInt("mapCtrlMode") == MapView.STATE_NAVI || savedState.getInt("mapCtrlMode") == MapView.STATE_EMU) {
            naviStartTime = savedState.getLong("naviStartTime");

            startNavigation();

            turnIconId = savedState.getInt("turnIconId");
            turnView.setImageResource(turnIconId);
            nextRoadText.setText(savedState.getString("nextRoadText"));
            nowRoadText.setText(savedState.getString("nowRoadText"));
            moveDistance.setText(savedState.getString("moveDistance"));
            leftDistance.setText(savedState.getString("leftDistance"));
            spendTime.setText(savedState.getString("spendTime"));

            if (mapView.getViewType() == MapView.VIEW_2D_FIX_DIRECTION) {
                changeMapViewBt.setImageResource(R.drawable.nav_map_mode_b);
            }
        }
        else {
            StartPointArray = savedState.getDoubleArray("StartPoint");
            EndPointArray = savedState.getDoubleArray("EndPoint");
            Ess1PointArray = savedState.getDoubleArray("Ess1tPoint");
            Ess2PointArray = savedState.getDoubleArray("Ess2tPoint");
            // switchOptionView2(R.layout.setting_start_end_point);
            switchView(R.layout.setting_start_end_point, R.id.option_view, R.id.setPoint_view);
            isSetStEnPointState = true;
            setRoutePointButtonListner();
        }
        // mapView.setCenter((GeoPoint) savedState.getParcelable("mapCenter"));

    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        Log.i("MapActivity.java", "onSaveInstanceState");
        isConfigChanged = true;
        //timer.cancel(); // cancel all tasks

        outState.putInt("viewType", mapView.getViewType());
        outState.putInt("mapCtrlMode", mapView.getControlMode());

        if (WeatherArray != null && WeatherArray.length >= 9) {
            outState.putString("WeatherText", WeatherArray[3] + "-" + WeatherArray[4] + "℃");
            outState.putString("WeatherUVText", "UV " + WeatherArray[9]);
            outState.putInt("imWeather", saveImageResource);
        }

        if (mapView.getControlMode() == MapView.STATE_MAP && !isSetStEnPointState) {
            outState.putParcelable("mapCenter", mapView.getCenter());
            outState.putInt("zoomLevel", mapView.getZoomLevel());
        }
        else if (mapView.getControlMode() == MapView.STATE_NAVI || mapView.getControlMode() == MapView.STATE_EMU) {
            outState.putLong("naviStartTime", naviStartTime);
            outState.putInt("turnIconId", turnIconId);
            // cuber
            outState.putString("nextRoadText", (String) nextRoadText.getText());

            outState.putString("nowRoadText", (String) nowRoadText.getText());
            outState.putString("moveDistance", (String) moveDistance.getText());
            outState.putString("leftDistance", (String) leftDistance.getText());
            outState.putString("spendTime", (String) spendTime.getText());

            sonav.getInstance().setMapInfoHandler(null);
        }
        else {
            outState.putBoolean("SetStEnPointState", isSetStEnPointState);
            outState.putDoubleArray("StartPoint", StartPointArray);
            outState.putDoubleArray("EndtPoint", EndPointArray);
            outState.putDoubleArray("Ess1tPoint", Ess1PointArray);
            outState.putDoubleArray("Ess2tPoint", Ess2PointArray);
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        engine.getMapEventHandler().setMapView(mapView);

        Log.i("MapActivity.java", "requestCode==go_home");

        if (resultCode == RESULT_OK) {
            ActivityResultProcessor(data);
        }
        else if (resultCode == -10) {

            finish();
            System.runFinalizersOnExit(true);
            System.exit(1);
            android.os.Process.killProcess(android.os.Process.myPid());
        }
    }

    private void ActivityResultProcessor(final Intent data) {
        engine.gomap(cur_GeoPoint.getLongitude(), cur_GeoPoint.getLatitude(), 0);
        engine.repaintmap();
        final ContextMenuOptions action;
        if (data == null) {
            return;
        } else
            action = (ContextMenuOptions) data.getSerializableExtra("Action");

        Log.i("MapActivity.java", "action ==" + String.valueOf(action != null));

        if (action == null)
            return;

        switch (action) {
            case NAVIGATION:
                if (getGoImmediately()) {
                    Location loc = null;
                    if (ApplicationGlobal.gpsListener != null) {
                        loc = ApplicationGlobal.gpsListener.getLastLocation();
                    }
                    if (loc != null) {
                        GeoPoint mapXYGeoPoint = new GeoPoint(loc.getLongitude(), loc.getLatitude());
                        String str = "Lon:" + loc.getLongitude() + "\nLat:" + loc.getLatitude();

                        StartPointArray[0] = loc.getLongitude();
                        StartPointArray[1] = loc.getLatitude();

                        TextStartName = "目前位置";
                    }
                    else {
                        UtilDialog uit = new UtilDialog(MapActivity.this) {
                            @Override
                            public void click_btn_1() {
                                super.click_btn_1();
                            }
                        };
                        uit.showDialog_route_plan_choice(
                                getString(R.string.gps_unable_to_get_location), null,
                                getString(R.string.dialog_ok_button_text), null);
                    }
                }

                switchView(R.layout.map, R.id.option_view, R.id.setPoint_view);
                switchView(R.layout.setting_start_end_point, R.id.option_view, R.id.setPoint_view);

                // engine.gomap(cur_g.getLongitude(), cur_g.getLatitude(), 0);
                // engine.repaintmap();

                isSetStEnPointState = true;
                setRoutePointButtonListner();
                break;
            default:
                break;
        }

    }

    @Override
    public int getMenuResource() {
        int mode = mapView.getControlMode();

        return mode == MapView.STATE_NAVI || mode == MapView.STATE_NAVI_PAUSE || mode == MapView.STATE_EMU ?
                R.layout.map_navi_menu : R.layout.main_menu;
    }

    private void setMapButtonListener() {
        ImageButton ibUserLocation = (ImageButton) findViewById(R.id.to_user_location);
        ImageButton ibFavorite = (ImageButton) findViewById(R.id.to_favorite);
        ImageButton ibNavigation = (ImageButton) findViewById(R.id.to_navi);
        ImageButton ibTrackRecord = (ImageButton) findViewById(R.id.to_track_record);
        ImageButton ibPOI = (ImageButton) findViewById(R.id.to_poi);
        ibZoomIn = (ImageButton) findViewById(R.id.zoom_in);
        ibZoomOut = (ImageButton) findViewById(R.id.zoom_out);
        imWeather = (ImageView) findViewById(R.id.weather);
        WeatherText = (TextView) findViewById(R.id.weather_text);
        WeatherUVText = (TextView) findViewById(R.id.weather_uv_text);
        MapRecordText = (TextView) findViewById(R.id.track_recording);
        weatherLayout = (RelativeLayout) findViewById(R.id.weather_layout);

        // 地圖 tag 說明
        Button btn_iconDescription = (Button) this.findViewById(R.id.top_right_btn);
        btn_iconDescription.setOnClickListener(this);

        // 如果使用者是直接橫版開啟單車時要移動icon位置
        if (this.getResources().getConfiguration().orientation == Configuration.ORIENTATION_LANDSCAPE) {
            MarginLayoutParams marginParams = new MarginLayoutParams(weatherLayout.getLayoutParams());
            marginParams.setMargins(20, 1, 0, 0);
            RelativeLayout.LayoutParams layoutParams = new RelativeLayout.LayoutParams(marginParams);
            weatherLayout.setLayoutParams(layoutParams);
        }

        if (WeatherArray != null && WeatherArray.length >= 4) {
            WeatherText.setText(WeatherArray[3] + "-" + WeatherArray[4] + "℃");
            WeatherUVText.setText("UV " + WeatherArray[9]);
            imWeather.setImageResource(saveImageResource);
        }
        if (TrackEngine.getInstance().getRecordingStatus()
                .equals(TrackRecordingStatus.RECORDING)) {
            Log.i("MapActivity.java", "RECORDING");
            MapRecordText.setVisibility(View.VISIBLE);
        } else {
            MapRecordText.setVisibility(View.INVISIBLE);
        }

        // 我的位置
        ibUserLocation.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                myLocation();
            }
        });

        ibFavorite.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                goToForResult(MyFavorite.class, false, ActivityCaller.FAVORITE.getValue());
            }
        });

        ibNavigation.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                // TODO Auto-generated method stub
                // startNavigation();
                isSetStEnPointState = true;
                switchView(R.layout.setting_start_end_point, R.id.option_view, R.id.setPoint_view);

                setRoutePointButtonListner();
            }
        });

        ibTrackRecord.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                goTo(TrackRecord.class, false);
            }
        });

        ibPOI.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                // TODO Auto-generated method stub
                goToForResult(POIMethodSelection.class, false, ActivityCaller.POI.getValue());
            }
        });

        ibZoomIn.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                mapView.zoomIn();
            }
        });

        ibZoomOut.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                mapView.zoomOut();
            }
        });

        imWeather.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                if (SettingManager.isInternetConfirmEnabled()) {
                    UtilDialog uit = new UtilDialog(MapActivity.this) {
                        @Override
                        public void click_btn_1() {
                            progressDialog.progressDialog("請稍候片刻", "正在取的氣象資訊");
                            checkWeatherState();
                            super.click_btn_1();
                        }

                        @Override
                        public void click_btn_2() {
                            super.click_btn_2();
                        }
                    };
                    uit.showDialog_route_plan_choice(getString(R.string.dialog_web_message), null, "確定", "取消");
                }
                else {
                    // WeatherDialog = ProgressDialog.show(MapActivity.this,
                    // "請稍候片刻", "正在取的氣象資訊", true);
                    progressDialog.progressDialog("請稍候片刻", "正在取的氣象資訊");
                    checkWeatherState();
                }
            }
        });
    }

    public void setActionSheet_navi() {
        sub_view = new int[4][2];
        sub_view[0][0] = R.id.actionsheet_navi01;
        sub_view[1][0] = R.id.actionsheet_navi02;
        sub_view[2][0] = R.id.actionsheet_navi03;
        sub_view[3][0] = R.id.actionsheet_navi04;
        actionSheet_navi = (ActionSheet) findViewById(R.id.actionSheet_navi);
        actionSheet_navi.setContext(MapActivity.this);
        actionSheet_navi.setActionSheetLayout(R.layout.action_sheet_navi, sub_view);

        actionSheet_navi.setOnActionSheetButtonClickListener(new ActionSheetButtonClickListener() {
                    @Override
                    public void onButtonClick(ActionSheet actionsheet,
                                              int index, int id) {
                        Intent roadListIntent;
                        switch (index) {
                            case 0:// 路徑清單
                                roadListIntent = new Intent(MapActivity.this,
                                        RoadList.class);
                                startActivity(roadListIntent);
                                break;
                            case 1:// 沿途景點
                                roadListIntent = new Intent(MapActivity.this,
                                        AroundScenic.class);
                                startActivity(roadListIntent);
                                break;
                            case 2:// 停止導航

                                UtilDialog uit = new UtilDialog(MapActivity.this) {
                                    @Override
                                    public void click_btn_1() {
                                        if (isRecordTrack) {
                                            StopTrackRecord();
                                            isRecordTrack = false;
                                        }
                                        stopNavigation();
                                        switchView(R.layout.map_options, R.id.navi_option_view, R.id.option_view);

                                        engine.clearshortpath();
                                        engine.clearallroutepoint();
                                        engine.savenaviparameter();

                                        /*** 清除marker ***/
                                        engine.setflagpoint(0, -1, -1);
                                        engine.setflagpoint(1, -1, -1);
                                        engine.setflagpoint(2, -1, -1);
                                        engine.setflagpoint(3, -1, -1);
                                        engine.setflagpoint(5, -1, -1);
                                        /***************/

                                        setMapButtonListener();
                                        isSetStEnPointState = false;
                                        isnaing = false;
                                        super.click_btn_1();
                                    }
                                };
                                uit.showDialog_route_plan_choice(getString(R.string.confirm_stop_navi), null, "確定", "取消");
                                break;

                            case 3:
                                break;

                            default:
                                break;
                        }
                    }
                });
    }

    public void setActionSheet() {
        sub_view = new int[9][2];
        sub_view[0][0] = R.id.actionsheet_start_end_01;
        sub_view[1][0] = R.id.actionsheet_start_end_02;
        sub_view[2][0] = R.id.actionsheet_start_end_03;
        sub_view[3][0] = R.id.actionsheet_start_end_04;
        sub_view[4][0] = R.id.actionsheet_start_end_05;
        sub_view[5][0] = R.id.actionsheet_start_end_06;
        sub_view[6][0] = R.id.actionsheet_start_end_07;
        sub_view[7][0] = R.id.actionsheet_start_end_08;
        sub_view[8][0] = R.id.actionsheet_start_end_09;

        actionSheet = (ActionSheet) findViewById(R.id.actionSheet_map);
        actionSheet.setContext(MapActivity.this);
        actionSheet.setActionSheetLayout(R.layout.action_sheet_start_end, sub_view);

        actionSheet.setOnActionSheetButtonClickListener(new ActionSheetButtonClickListener() {
                    @Override
                    public void onButtonClick(ActionSheet actionsheet, int index, int id) {
                        switch (index) {
                            case 0:
                                break;
                            case 1:// 目前位置
                                selectSetPositionWay(index, which_button);
                                // isDoEemulationNavi = false;
                                break;
                            case 2:// 地圖上的點
                                // isDoEemulationNavi = true;
                                selectSetPositionWay(index, which_button);
                                break;
                            case 3:// 地址查詢
                                selectSetPositionWay(index, which_button);
                                // isDoEemulationNavi = true;
                                break;
                            case 4:// 景點查詢
                                selectSetPositionWay(index, which_button);
                                // isDoEemulationNavi = true;
                                break;
                            case 5:// 我的最愛
                                selectSetPositionWay(index, which_button);
                                // isDoEemulationNavi = true;
                                break;
                            case 6:// 查詢紀錄
                                selectSetPositionWay(index, which_button);
                                // isDoEemulationNavi = true;
                                break;
                            case 7:// 刪除紀錄
                                selectSetPositionWay(index, which_button);
                                // isDoEemulationNavi = true;
                                break;
                            case 8:
                                break;
                            default:
                                break;
                        }
                    }
                });
    }

    private void setRoutePointButtonListner() {
        TextStartPoint = (TextView) findViewById(R.id.start_point_textView);
        TextEss1Point = (TextView) findViewById(R.id.essential_point1_textview);
        TextEss2Point = (TextView) findViewById(R.id.essential_point2_textview);
        TextEndPoint = (TextView) findViewById(R.id.end_point_textview);
        Button StartButtton = (Button) findViewById(R.id.start_point_button);
        Button esspoint1 = (Button) findViewById(R.id.essential_point1_button);
        Button esspoint2 = (Button) findViewById(R.id.essential_point2_button);
        Button EndButtton = (Button) findViewById(R.id.end_point_botton);
        Button StartNaviButtton = (Button) findViewById(R.id.start_navi_botton);
        Button CancelButton = (Button) findViewById(R.id.cancel_Button);
        setActionSheet();
        if (TextStartName != "")
            TextStartPoint.setText(TextStartName);
        if (TextEss1Name != "")
            TextEss1Point.setText(TextEss1Name);
        if (TextEss2Name != "")
            TextEss2Point.setText(TextEss2Name);
        if (TextEndName != "")
            TextEndPoint.setText(TextEndName);
        StartButtton.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                // TODO Auto-generated method stub
                // Log.i("MapActivity.java","startButtton onClick");
                // setLocationDialog(START_Button);

                actionSheet.show();
                which_button = START_Button;
            }
        });

        esspoint1.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                // TODO Auto-generated method stub
                // setLocationDialog(ESSENTIAL_Button1);
                actionSheet.show();
                which_button = ESSENTIAL_Button1;
            }
        });

        esspoint2.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                // TODO Auto-generated method stub
                // setLocationDialog(ESSENTIAL_Button2);
                actionSheet.show();
                which_button = ESSENTIAL_Button2;
            }
        });

        EndButtton.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                // TODO Auto-generated method stub
                // setLocationDialog(END_Button);
                actionSheet.show();
                which_button = END_Button;
            }
        });

        StartNaviButtton.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {

                if (TextStartPoint.getText().toString().contains("目前位置"))
                    isDoEemulationNavi = false;
                else
                    isDoEemulationNavi = true;

                String strMsg = "";
                if (checkPointSetting() != 0) {// 檢查起點終點是否都有設定
                    switch (checkPointSetting()) {
                        case NoStartPointFlag:
                            strMsg = NoStartPointMsg;
                            break;
                        case NoEndPointFlag:
                            strMsg = NoEndPointMsg;
                            break;
                        case NoStartEndPointFlag:
                            strMsg = NoStartEndPointMsg;
                            break;

                        default:
                            break;
                    }
                    UtilDialog uit = new UtilDialog(MapActivity.this) {
                        @Override
                        public void click_btn_1() {

                            super.click_btn_1();
                        }

                        @Override
                        public void click_btn_2() {

                            super.click_btn_2();
                        }
                    };
                    uit.showDialog_route_plan_choice(strMsg, null, "確定", null);
                }
                else {
                    if (isPointAvailable() != null) {// 檢查設定的點是否有無效的值,如果都是有效值,回傳null
                        String Msg = isPointAvailable();

                        UtilDialog uit = new UtilDialog(MapActivity.this);
                        uit.showDialog_route_plan_choice(Msg, null, "確定", null);
                    } else {// 開始導航

                        isnaing = true;

                        UtilDialog uit = new UtilDialog(MapActivity.this) {
                            @Override
                            public void click_btn_1() {
                                SettingManager.setRoutingMethod(1);
                                startNavi();
                                super.click_btn_1();
                            }

                            @Override
                            public void click_btn_2() {
                                SettingManager.setRoutingMethod(2);
                                startNavi();
                                super.click_btn_2();
                            }
                        };
                        uit.showDialog_route_plan_choice("路徑規劃選擇", null, "最佳(專用道路優先)", "最短路徑");
                    }
                }
                return;
            }
        });

        CancelButton.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                RelativeLayout layout = (RelativeLayout) findViewById(R.id.mapLayout);
                switchView(R.layout.map_options, R.id.setPoint_view, R.id.option_view);
                isSetStEnPointState = false;
                setMapButtonListener();

                // mapView.setMapActivity(MapActivity.this);
                Log.i("MapActivity.java", "mapactivity!=null=" + String.valueOf(MapActivity.this != null));
            }
        });

    }

    private void selectSetPositionWay(int which, int whichButtonSrc) {
        String[] getWitchButton = { "startButton", "ess1Button", "ess2Button", "endButton" };

        switch (which) {
            case 3:
                Intent AddressIntent = new Intent(MapActivity.this, CitySelection.class);
                AddressIntent.putExtra("setpoint", getWitchButton[whichButtonSrc]);
                startActivityForResult(AddressIntent, ActivityCaller.ADDRESS.getValue());
                break;
            case 4:
                Intent POIIntent = new Intent(MapActivity.this, POIMethodSelection.class);
                POIIntent.putExtra("setpoint", getWitchButton[whichButtonSrc]);
                startActivityForResult(POIIntent, ActivityCaller.POI.getValue());
                break;
            case 6:
                Intent POIViewIntent = new Intent(MapActivity.this, MyHistory.class);
                POIViewIntent.putExtra("setpoint", getWitchButton[whichButtonSrc]);
                startActivityForResult(POIViewIntent, ActivityCaller.HISTORY.getValue());
                break;
            case 5:
                Intent FavoriteViewIntent = new Intent(MapActivity.this, MyFavorite.class);
                FavoriteViewIntent.putExtra("setpoint", getWitchButton[whichButtonSrc]);
                startActivityForResult(FavoriteViewIntent, ActivityCaller.FAVORITE.getValue());
                break;

            case 1:// 目前的位置
                Log.i("MapActivity.java", "目前的位置");

                Location loc = null;

                if (ApplicationGlobal.gpsListener != null) {
                    loc = ApplicationGlobal.gpsListener.getLastLocation();
                }
                Log.i("MapActivity.java", "loc!=null=" + String.valueOf(loc != null));

                if (loc != null) {
                    GeoPoint mapXYGeoPoint = new GeoPoint(loc.getLongitude(), loc.getLatitude());

                    String str = "目前位置";
                    switch (whichButtonSrc) {
                        case 0:
                            setPosition(str, mapXYGeoPoint, START_POINT);
                            break;
                        case 1:
                            setPosition(str, mapXYGeoPoint, ESS1_POINT);
                            break;
                        case 2:
                            setPosition(str, mapXYGeoPoint, ESS2_POINT);
                            break;
                        case 3:
                            setPosition(str, mapXYGeoPoint, END_POINT);
                            break;
                        default:
                            break;
                    }
                    UtilDialog uit = new UtilDialog(MapActivity.this);
                    uit.showDialog_route_plan_choice(getString(R.string.gps_position_finish), null, "確定", null);
                }
                else {
                    UtilDialog uit = new UtilDialog(MapActivity.this);
                    uit.showDialog_route_plan_choice(getString(R.string.gps_unable_to_get_location), null, "確定", null);
                    return;
                }

                break;

            case 2:// 地圖上的點
                pointOnMapMode = true;
                pointOnMap(whichButtonSrc);
                break;

            case 7:// 取消設定
                switch (whichButtonSrc) {
                    case START_POINT:
                        TextStartName = "請選擇起點";
                        TextStartPoint.setText(TextStartName);
                        StartPointArray[0] = 0.0;
                        StartPointArray[1] = 0.0;
                        break;
                    case ESS1_POINT:
                        TextEss1Name = "經過點1";
                        TextEss1Point.setText(TextEss1Name);
                        Ess1PointArray[0] = 0.0;
                        Ess1PointArray[1] = 0.0;
                        break;
                    case ESS2_POINT:
                        TextEss2Name = "經過點2";
                        TextEss2Point.setText(TextEss2Name);
                        Ess2PointArray[0] = 0.0;
                        Ess2PointArray[1] = 0.0;
                        break;
                    case END_POINT:
                        TextEndName = "請選擇終點";
                        TextEndPoint.setText(TextEndName);
                        EndPointArray[0] = 0.0;
                        EndPointArray[1] = 0.0;
                        break;
                    default:
                        break;
                }
                break;
        }
    }

    private void DrawTrack(final int trackID) {
        if (trackID <= -1) {
            return;
        }

        Track drawTrack = null;
        try {
            drawTrack = new Track(this, trackID);
        } catch (Exception e) {
            Log.e("MapActivity", e.toString());
            return;
        }

        sonav engine = sonav.getInstance();
        engine.newspxy(drawTrack.getTrackPoints().size());
        TrackPoint[] tpoints = new TrackPoint[drawTrack.getTrackPoints().size()];
        drawTrack.getTrackPoints().values().toArray(tpoints);

        for (TrackPoint tp : tpoints) {
            engine.addspxy(tp.getLongitude(), tp.getLatitude());
        }

        engine.drawspxy(MapView.SHOW_MANUAL_ROUTE);
        engine.gomap(tpoints[0].getLongitude(), tpoints[0].getLatitude(), 0);
    }

    private void myLocation() {
        UtilDialog uit = new UtilDialog(MapActivity.this) {
            @Override
            public void click_btn_1() {
                // WeatherDialog = ProgressDialog.show(MapActivity.this,
                // "請稍候片刻", "正在取的氣象資訊", true);

                jumpToMyLocation();
                progressDialog.progressDialog("請稍候片刻", "正在取的氣象資訊");
                checkWeatherState();

                super.click_btn_1();
            }
        };
        uit.showDialog_route_plan_choice("取得我的位置時,是否同時更新氣象資訊?", null, "是", "否");

    }

    private int jumpToMyLocation() {
        Location loc = null;
        if (ApplicationGlobal.gpsListener != null) {
            loc = ApplicationGlobal.gpsListener.getLastLocation();
        }
        if (loc != null) {
            engine.gomap(loc.getLongitude(), loc.getLatitude(), 0);
            engine.setflagpoint(MapView.USER_LOCATION_POINT,
                    loc.getLongitude(), loc.getLatitude());
            return 1;
        }
        return -1;
    }

    private void onApplicationExit() {
        TrackEngine te = TrackEngine.getInstance();
        Log.i("MapActivity.java", "te=" + String.valueOf(te != null));

        if (!te.getRecordingStatus().equals(TrackRecordingStatus.STOPED)) {
            te.Stop();
        }
    }

    /**
     * Start routing path planning.
     */
    private void doRoutingPlan() {
        new Thread() {
            @Override
            public void run() {
                try {
                    // engine.addnavivoice("誰說下雨天要來騎腳踏車的");
                    PathFinder pathFinder = PathFinder.getInstance();
                    if (SettingManager.getRoutingMethod() == 1) {
                        pathFinder.setMethod(PathFinder.BICYCLE);
                    }
                    else if (SettingManager.getRoutingMethod() == 2) {
                        pathFinder.setMethod(PathFinder.WALKING);
                    }

                    pathFinder.setOrigin(new GeoPoint(StartPointArray[0], StartPointArray[1]));

                    if (Ess1PointArray[0] != 0.0)
                        pathFinder.setViaOne(new GeoPoint(Ess1PointArray[0], Ess1PointArray[1]));

                    if (Ess2PointArray[0] != 0.0)
                        pathFinder.setViaTwo(new GeoPoint(Ess2PointArray[0], Ess2PointArray[1]));

                    pathFinder.setDestin(new GeoPoint(EndPointArray[0], EndPointArray[1]));
					/*
					 * pathFinder.setOrigin(new GeoPoint(121.534538,
					 * 25.038016)); pathFinder.setViaOne(new
					 * GeoPoint(121.549001, 25.031250));
					 * pathFinder.setViaTwo(new GeoPoint(121.53870, 25.007606));
					 * pathFinder.setDestin(new GeoPoint(121.556382,
					 * 24.981452)); pathFinder.setOrigin(new
					 * GeoPoint(121.533206, 25.023832));
					 */
                    // pathFinder.setDestin(new GeoPoint(121.570183,
                    // 25.023615));//松仁路
                    // pathFinder.setDestin(new GeoPoint(121.5053611111111,
                    // 24.983028055555554));//興南路

                    pathFinder.FindPath();
                    clearPoint();
                    while (pathFinder.getStatus() != PathFinder.PATH_FINDING_DONE) {
                        Log.d(getClass().toString(), "pathFinder.getStatus()="
                                + pathFinder.getStatus());
                        Thread.sleep(500);
                    }
                    // RoutingPlanDialog.dismiss();
                    progressDialog.dismiss();
                    // setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_SENSOR);
                    String str = "";
                    ROADLISTDATA[] list = pathFinder.getPathResult();

                    if (list == null)
                        uiHandler.sendMessage(uiHandler.obtainMessage(ROUTING_PLAN_END, null));
                    else {
                        for (ROADLISTDATA data : list) {
                            Log.d(getClass().toString(), "route road=" + data);
                            str += data + "\n";
                        }
                        uiHandler.sendMessage(uiHandler.obtainMessage(ROUTING_PLAN_END, str));
                    }
                } catch (Exception e) {
                    Log.e(getClass().toString(), e.getMessage(), e);

                    uiHandler.sendMessage(uiHandler.obtainMessage(ROUTING_PLAN_END, null));
                }
            }
        }.start();
    }

    /**
     * Initialize the navigation event handler for this activity.
     */
    private void initialNavigationEventHandler(sonav engine) {
        naviHandler = new AbstractMapInfoHandler() {

            @Override
            public void onCurrentAddressChanged(int clazz, String address) {
            }

            @Override
            public void onCurrentRoadChanged(String roadName, String sign) {
                if (roadName != null && nowRoadText != null) {
                    nowRoadText.setText(roadName);
                }
            }

            @Override
            public void onDistanceChanged(int meter) {
                if (leftDistance != null) {
                    leftDistance.setText(String.valueOf(meter));
                }
            }

            @Override
            public void onDistanceToNextRoadChanged(int meter) {
                if (moveDistance != null) {
                    moveDistance.setText(String.valueOf(meter));
                }
            }

            @Override
            public void onDistrictChanged(String cityName, String townName) {
            }

            @Override
            public void onNextRoadChanged(String roadName, String sign) {
                if (roadName != null && nextRoadText != null) {
                    nextRoadText.setText(roadName);
                }
            }

            @Override
            public void onNextTurnChanged(int turn) {
                if (turnView != null) {
                    turnIconId = getResources().getIdentifier("turn_" + turn, "drawable", getPackageName());

                    turnView.setImageResource(turnIconId);
                }
            }

            @Override
            public void onTimeLeftChanged(int minute) {
            }

            @Override
            public void onRoutingPathChanged() {
            }

            @Override
            public void onRoadLayer() {
                // engine.addnavivoice("您已經騎上自行車道了");
            }

            // cuber
            @Override
            public void displayGradient(double gradient) {
                if (gradient_digit == null) {
                } else {
                    gradient_digit.setText(String.valueOf(gradient / 1000));
                }
            }

            public void naviFinished() {
                new Thread() {
                    public void run() {
                        try {
                            Thread.sleep(5000);
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                        uiHandler.sendMessage(uiHandler.obtainMessage(NAVI_FINISHED, null));
                    }
                }.start();
            }
        };

        naviHandler.setEngine(engine);
        engine.setMapInfoHandler(naviHandler);
    }

    /**
     * Initialize the UI handler for this activity.
     */
    private void initialUIHandler() {
        uiHandler = new Handler() {
            int ROUTING_PLAN_STATE = -1;

            @Override
            public void handleMessage(Message msg) {
                Log.i("MapActivity.java", "UIHandler");
                if (msg.what == ROUTING_PLAN_END) {
                    ROUTING_PLAN_STATE = ROUTING_PLAN_END;

                    // String Carbon[] =
                    // getCarbonEmissionReductions(engine.getspdistime()[0]);
                    PathFinder pathFinder = PathFinder.getInstance();
                    ROADLISTDATA[] list = pathFinder.getPathResult();

                    if (list == null) {
                        UtilDialog uit = new UtilDialog(MapActivity.this) {
                            @Override
                            public void click_btn_1() {
                                if (isRecordTrack) {
                                    StopTrackRecord();
                                    isRecordTrack = false;
                                }
                                stopNavigation();
                                switchView(R.layout.map_options,
                                        R.id.navi_option_view, R.id.option_view);

                                setMapButtonListener();
                                isSetStEnPointState = false;
                                isnaing = false;
                                super.click_btn_1();
                            }
                        };
                        uit.showDialog_route_plan_choice("沒有找到適合路徑", null, "確定", null);

                    } else {

                        int length[] = new int[list.length];
                        int LengthCount = 0;
                        for (int i = 0; i < list.length; i++) {
                            length[i] = (int) list[i].getLength();
                            LengthCount += length[i];
                        }
                        String Carbon[] = getCarbonEmissionReductions(LengthCount);

                        UtilDialog uit = new UtilDialog(MapActivity.this) {
                            @Override
                            public void click_btn_1() {

                                if (isDoEemulationNavi) {
                                    mapView.setNaviEmulatorSpeed(1);
                                    mapView.playNaviEmulator(false);
                                    mapView.setControlMode(MapView.STATE_EMU);
                                }
                                else {
                                    // set mode of MapView
                                    mapView.setControlMode(MapView.STATE_NAVI);
                                }
                                super.click_btn_1();
                            }
                        };
                        uit.showDialog_route_plan_choice("減碳量!!!", "此旅程減少的碳排放\n取代汽車碳量:\n" + "  " + Carbon[0]
                                        + "(Kg)\n" + "取代機車碳量:\n" + "  " + Carbon[1] + "(Kg)", "確定", null);

                        if (naviStartTime == 0) {
                            // naviStartTime = System.currentTimeMillis();
                        }
                    }
                }
                else if (msg.what == UPDATE_NAVI_TIME) {
                    if (ROUTING_PLAN_STATE != ROUTING_PLAN_END)
                        spendTime.setText("00:00:00");
                    else
                        spendTime.setText((String) msg.obj);
                }
                else if (msg.what == NAVI_FINISHED) {
                    if (!isNaviFinishDialog) {
                        UtilDialog uit = new UtilDialog(MapActivity.this) {
                            @Override
                            public void click_btn_1() {
                                stopNavigation();
                                switchView(R.layout.map_options, R.id.navi_option_view, R.id.option_view);
                                setMapButtonListener();
                                engine.clearshortpath();
                                engine.clearallroutepoint();
                                engine.savenaviparameter();

                                /*** 清除marker ***/
                                engine.setflagpoint(0, -1, -1);
                                engine.setflagpoint(1, -1, -1);
                                engine.setflagpoint(2, -1, -1);
                                engine.setflagpoint(3, -1, -1);
                                engine.setflagpoint(5, -1, -1);
                                /***************/

                                isSetStEnPointState = false;
                                ROUTING_PLAN_STATE = -1;

                                if (isRecordTrack) {
                                    StopTrackRecord();
                                    isRecordTrack = false;
                                }
                                super.click_btn_1();
                            }
                        };

                        uit.showDialog_route_plan_choice("已到達目的地", null, "確定", null);
                        uit.dialog.setOnDismissListener(new OnDismissListener() {
                            @Override
                            public void onDismiss(DialogInterface dialog) {
                                isnaing = false;
                                isNaviFinishDialog = false;
                            }
                        });
                    }
                }
            }
        };
    }

    private void initialSpeedHandler() {
        speedHandler = new Handler() {
            @Override
            public void handleMessage(Message msg) {
                if (msg.what == UPDATE_GPS_STATUS) {
                    gpsStatusView.setImageResource(GPSListener.isGpsServiceAvailable() ? R.drawable.nav_gps_strong : R.drawable.nav_gps_none);

                    Location loc = null;
                    if (ApplicationGlobal.gpsListener != null) {
                        loc = ApplicationGlobal.gpsListener.getLastLocation();
                    }
                    if (loc != null) {
                        int speed = (int) (ApplicationGlobal.gpsListener
                                .getLastLocation().getSpeed() * 3.6);

                        String speedString = String.valueOf(speed);

                        if (speed <= 9) {
                            displaySpeedDigital3(speed);
                            displaySpeedDigital2(0);
                            displaySpeedDigital1(0);
                        }
                        else if (speed >= 10 && speed <= 99) {
                            displaySpeedDigital3(Integer.parseInt(speedString.substring(1, 2)));
                            displaySpeedDigital2(Integer.parseInt(speedString.substring(0, 1)));
                            displaySpeedDigital1(0);
                        }
                        else if (speed > 99) {
                            displaySpeedDigital3(Integer.parseInt(speedString.substring(2, 3)));
                            displaySpeedDigital2(Integer.parseInt(speedString.substring(1, 2)));
                            displaySpeedDigital1(Integer.parseInt(speedString.substring(0, 1)));
                        }
                    }
                }
            }
        };
    }

    private void initialCalHandler() {
        CalHandler = new Handler() {
            @Override
            public void handleMessage(Message msg) {
                if (msg.what == UPDATE_CAL) {
                    Log.i("MapActivity.java", "UPDATE_CAL");
                    caltext.setText((String) msg.obj);
                }
            }
        };
    }

    private void initialWeatherHandler() {
        weatherHandler = new Handler() {
            @Override
            public void handleMessage(Message msg) {
                if (msg.what == GET_WEATHER_FINISH) {
                    Log.i("MapActivity.java", "Get_Weather_Finish");

                    WeatherText.setText(WeatherArray[3] + "-" + WeatherArray[4] + "℃");

                    if (WeatherArray != null && WeatherArray.length >= 9)
                        WeatherUVText.setText("UV " + WeatherArray[9]);

                    imWeather.setImageResource(Integer.parseInt((String) msg.obj));
                    saveImageResource = Integer.parseInt((String) msg.obj);
                }
                else if (msg.what == WEATHER_GPS_FAIL) {
                    UtilDialog uit = new UtilDialog(MapActivity.this);
                    uit.showDialog_route_plan_choice(getString(R.string.gps_unable_to_get_location), null, "確定", null);
                }
                else if (msg.what == WEATHER_WEB_FAIL) {
                    UtilDialog uit = new UtilDialog(MapActivity.this);
                    uit.showDialog_route_plan_choice(getString(R.string.dialog_web_message2), null, "確定", null);
                }
            }
        };
    }

    private void initialTrafficHandler() {
        updataTrafficConditionHandler = new Handler() {
            @Override
            public void handleMessage(Message msg) {
                if (msg.what == GET_WEATHER_FINISH) {
                    // Toast.makeText(MapActivity.this, "下載成功", 9000).show();
                    checkBreakState();
                }
                else if (msg.what == WEATHER_GPS_FAIL) {
                    // AlertDialogUtil.showMsgWithConfirm(Update.this,
                    // getString(R.string.dialog_gps_message),
                    // getString(R.string.dialog_ok_button_text));
                }
                else if (msg.what == WEATHER_WEB_FAIL) {
                    // Toast.makeText(MapActivity.this, "更新路況失敗", 9000).show();
                }
            }
        };
    }

    private void switchView(int resId, int RemovedResId, int AddResId) {

        RelativeLayout layout = (RelativeLayout) findViewById(R.id.mapLayout);

        layout.removeView(layout.findViewById(RemovedResId));
        Log.i("MapActivity.java", "isSetStEnPointState=" + isSetStEnPointState);
        // if (isSetStEnPointState == true) {
        // //
        // layout.findViewById(R.id.mapView_zoom_layout).setVisibility(layout.findViewById(R.layout.zoom_in_out).GONE);
        // layout.removeView(layout.findViewById(R.id.mapView_zoom_layout));
        // }
        // else {
        // layout.removeView(layout.findViewById(R.id.mapView_zoom_layout));}
        LayoutInflater inflater = (LayoutInflater) getSystemService(LAYOUT_INFLATER_SERVICE);
        if (resId != R.layout.map) {
            View optionView = inflater.inflate(resId, null);

            optionView.setId(AddResId);
            layout.addView(optionView);
        }

    }

    /**
     * Switch to navigation mode.
     */
    private void startNavigation() {
        isMapState = false;
        RelativeLayout layout = (RelativeLayout) findViewById(R.id.mapLayout);
        layout.removeView(layout.findViewById(R.id.weather_layout));
        switchView(R.layout.map_navi_options, R.id.setPoint_view, R.id.navi_option_view);

        setActionSheet_navi();

        // start navigation if not yet started
        if (naviStartTime == 0) {
            setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
            // RoutingPlanDialog = ProgressDialog.show(this, "正在執行路徑規畫",
            // "請稍候片刻...");

            progressDialog.progressDialog("正在執行路徑規畫", "請稍候片刻...");

            doRoutingPlan();
            StopNavigationFlag = false;
        }
        if (SettingManager.getMapViewType() == 1) {
            mapView.setViewType(MapView.VIEW_2D);
        }
        else if (SettingManager.getMapViewType() == 2) {
            mapView.setViewType(MapView.VIEW_2D_FIX_DIRECTION);
        }
        else if (SettingManager.getMapViewType() == 2) {
            mapView.setViewType(MapView.VIEW_3D);
        }
        else {
            mapView.setViewType(MapView.VIEW_2D);
        }

        turnView = (ImageView) findViewById(R.id.turn_image);
        nextRoadText = (TextView) findViewById(R.id.next_road);
        nowRoadText = (TextView) findViewById(R.id.now_road_name);
        moveDistance = (TextView) findViewById(R.id.move_distance);
        leftDistance = (TextView) findViewById(R.id.left_distance);
        spendTime = (TextView) findViewById(R.id.spend_time);
        gpsStatusView = (ImageButton) findViewById(R.id.gps_image);
        changeMapViewBt = (ImageButton) findViewById(R.id.change_map_view);
        speed_digit3 = (ImageView) findViewById(R.id.speed_digit3);
        speed_digit2 = (ImageView) findViewById(R.id.speed_digit2);
        speed_digit1 = (ImageView) findViewById(R.id.speed_digit1);
        gradient_digit = (TextView) findViewById(R.id.gradient_digit);
        caltext = (TextView) findViewById(R.id.cal_text);
        NaviMenu = (ImageView) findViewById(R.id.navi_menu);
        weatherLayout = (RelativeLayout) findViewById(R.id.weather_navi_layout);
        WeatherText = (TextView) findViewById(R.id.weather_text);
        WeatherUVText = (TextView) findViewById(R.id.weather_uv_text);
        imWeather = (ImageView) findViewById(R.id.weather);
        TrackRecord = (TextView) findViewById(R.id.track_recording);
        ZoomLayoutPortrait = (RelativeLayout) findViewById(R.id.zoom_layout_navi_portrait);
        ZoomLayoutLandscape = (RelativeLayout) findViewById(R.id.zoom_layout_navi_landscape);
        ibZoomOutPortrait = (ImageButton) findViewById(R.id.zoom_out_portrait);
        ibZoomInPortrait = (ImageButton) findViewById(R.id.zoom_in_portrait);
        ibZoomOutlandscape = (ImageButton) findViewById(R.id.zoom_out_landscape);
        ibZoomInlandscape = (ImageButton) findViewById(R.id.zoom_in_landscape);
        ZoomLayoutPortrait.setVisibility(ImageView.VISIBLE);
        ibZoomOutPortrait.setVisibility(ImageView.VISIBLE);
        ibZoomInPortrait.setVisibility(ImageView.VISIBLE);

        if (isDoEemulationNavi) {
            ZoomLayoutPortrait.setVisibility(ImageView.GONE);
            ZoomLayoutPortrait.setVisibility(ImageView.GONE);
            ibZoomOutPortrait.setVisibility(ImageView.GONE);
            ibZoomInPortrait.setVisibility(ImageView.GONE);
        }
        if (isRecordTrack) {
            TrackRecord.setVisibility(View.VISIBLE);
        }
        // 檢查是否已經有氣像資訊,如果有就顯示
        if (WeatherArray != null) {
            Log.i("MapActivity.java", "WeatherArray!=null");
            WeatherText.setText(WeatherArray[3] + "-" + WeatherArray[4] + "℃");
            WeatherUVText.setText("UV " + WeatherArray[9]);
            imWeather.setImageResource(saveImageResource);
        }

        imWeather.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                if (SettingManager.isInternetConfirmEnabled()) {
                    UtilDialog uit = new UtilDialog(MapActivity.this) {
                        @Override
                        public void click_btn_1() {
                            // WeatherDialog =
                            // ProgressDialog.show(MapActivity.this, "請稍候片刻",
                            // "正在取得氣象資訊", true);
                            progressDialog.progressDialog("請稍候片刻", "正在取的氣象資訊");
                            checkWeatherState();
                            super.click_btn_1();
                        }
                    };
                    uit.showDialog_route_plan_choice(getString(R.string.dialog_web_message), null, "確定", "取消");
                }
                else {
                    // WeatherDialog = ProgressDialog.show(MapActivity.this,
                    // "請稍候片刻", "正在取的氣象資訊", true);
                    progressDialog.progressDialog("請稍候片刻", "正在取的氣象資訊");
                    checkWeatherState();
                }
            }
        });

        ibZoomInPortrait.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                mapView.zoomIn();
            }
        });

        ibZoomOutPortrait.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                mapView.zoomOut();
            }
        });

        ibZoomInlandscape.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                mapView.zoomIn();
            }
        });

        ibZoomOutlandscape.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                mapView.zoomOut();
            }
        });

        changeMapViewBt.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                // TODO Auto-generated method stub
                int type = mapView.getViewType();

                if (mapView.getViewType() == MapView.VIEW_3D) {
                    mapView.setViewType(MapView.VIEW_2D);
                    changeMapViewBt.setImageResource(R.drawable.nav_map_mode_a);
                }
                else if (mapView.getViewType() == MapView.VIEW_2D_FIX_DIRECTION) {
                    mapView.setViewType(MapView.VIEW_3D);
                    changeMapViewBt.setImageResource(R.drawable.nav_map_mode_c);
                }
                else {
                    mapView.setViewType(MapView.VIEW_2D_FIX_DIRECTION);
                    changeMapViewBt.setImageResource(R.drawable.nav_map_mode_b);

                }
            }
        });

        NaviMenu.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                actionSheet_navi.show();
            }
        });

        nextRoadText.setText("");
        nowRoadText.setText("");

        new Thread() {
            public void run() {
                int timeCounter = 0;
                try {
                    while (!(StopNavigationFlag)) {
                        timeCounter++;
                        if (timeCounter % 60 == 0) {
                            // checkbreakState();
                            updateTrafficCondition();
                        }

                        uiHandler.sendMessage(uiHandler.obtainMessage(UPDATE_NAVI_TIME, toTimer(timeCounter)));
                        naviStartTime = timeCounter;
                        // 真實導航時才有速度and卡路里
                        if (!isDoEemulationNavi) {
                            // 速度
                            speedHandler.sendMessage(uiHandler.obtainMessage(UPDATE_GPS_STATUS, null));
                            // 卡路里
                            CalHandler.sendMessage(uiHandler.obtainMessage(UPDATE_CAL, String.valueOf(cal(timeCounter))));
                        }
                        Thread.sleep(1000);
                    }
                } catch (Exception e) {
                    // TODO: handle exception
                    e.printStackTrace();
                }
            }
        }.start();

        if (naviTimeUpdator != null) {
            naviTimeUpdator.cancel();
            naviTimeUpdator = null;
        }
    }

    /**
     * Switch to normal map mode.
     */
    private void stopNavigation() {
        if (!isDoEemulationNavi)
            isDoEemulationNavi = true;

        naviStartTime = 0;

        // set mode of MapView
        mapView.setControlMode(MapView.STATE_MAP);
        engine.clearshortpath();
        engine.clearallroutepoint();
        engine.savenaviparameter();

        StopNavigationFlag = true;
        isMapState = true;
        pop.dismiss();
    }

    /**
     * switch title_bar Visible, 0 is visible| 1 is invisible|2 is gone
     */
    private void setTitleBarVisibility(int visibility) {
        try {
            int titleContainerId = (Integer) Class.forName("com.android.internal.R$id").getField("title_container").get(null);
            findViewById(titleContainerId).setVisibility(visibility);

        } catch (Exception ex) {
            Log.d("switchTitleBar", ex.getMessage());
        }
    }

    private void displaySpeedDigital3(int speed) {
        speedDisplayIconId = getResources().getIdentifier("nav_no_0" + speed, "drawable", getPackageName());
        speed_digit3.setImageResource(speedDisplayIconId);
    }

    private void displaySpeedDigital2(int speed) {
        speedDisplayIconId = getResources().getIdentifier("nav_no_0" + speed, "drawable", getPackageName());

        speed_digit2.setImageResource(speedDisplayIconId);
    }

    private void displaySpeedDigital1(int speed) {
        speedDisplayIconId = getResources().getIdentifier("nav_no_0" + speed, "drawable", getPackageName());

        speed_digit1.setImageResource(speedDisplayIconId);
    }

    public static void setPosition(String name, GeoPoint position, int flag) {
        setPosition(name, position, flag, "");
        Log.i("MapActivity_setPosition", "setto:" + flag);
    }

    public static void setPosition(String name, GeoPoint position, int flag, String address) {

        cur_GeoPoint = position;

        State_Flag_For_PublicTransit = flag;
        if (START_POINT == flag) {
            StartPointArray[0] = position.getLongitude();
            StartPointArray[1] = position.getLatitude();
            StartPointLonForRoadList = StartPointArray[0];
            StartPointLatForRoadList = StartPointArray[1];
            TextStartName = name;
            TextStartAddress = address;

            if (isSetStEnPointState)
                TextStartPoint.setText(name);
            Log.i("MapActivity.java", "TextStartName==" + TextStartName);
        }
        else if (ESS1_POINT == flag) {
            Ess1PointArray[0] = position.getLongitude();
            Ess1PointArray[1] = position.getLatitude();
            TextEss1Name = name;

            if (isSetStEnPointState)
                TextEss1Point.setText(name);
        }
        else if (ESS2_POINT == flag) {
            Ess2PointArray[0] = position.getLongitude();
            Ess2PointArray[1] = position.getLatitude();
            TextEss2Name = name;

            if (isSetStEnPointState)
                TextEss2Point.setText(name);
        }
        else if (END_POINT == flag) {
            Log.i("MapActivity.java", "EndName=" + name);
            EndPointArray[0] = position.getLongitude();
            EndPointArray[1] = position.getLatitude();
            TextEndName = name;
            TextEndAddress = address;

            if (isSetStEnPointState)
                TextEndPoint.setText(name);
        }
        Log.i("MapActivity_setPosition", "setto:" + flag);
    }

    public static double[] getStartArray() {
        return StartPointArray;
    }

    public static double[] getEndArray() {
        return EndPointArray;
    }

    public static String getName(int flag) {
        String Name = "";
        if (flag == START_POINT)
            Name = TextStartName;
        else if (flag == END_POINT)
            Name = TextEndName;

        return Name;
    }

    public static String getAddress(int flag) {
        String address = "";
        if (flag == START_POINT)
            address = TextStartAddress;
        else if (flag == END_POINT)
            address = TextEndAddress;

        return address;
    }

    public static double getStartPoinForRoadList() {
        return StartPointLatForRoadList;
    }

    public static int CheckFlagForPublicTransit() {
        return State_Flag_For_PublicTransit;
    }

    private void checkWeatherState() {
        new Thread() {
            @Override
            public void run() {
                try {
                    String result = "";
                    double Lon, Lat;
                    Location loc = null;

                    if (ApplicationGlobal.gpsListener != null) {
                        loc = ApplicationGlobal.gpsListener.getLastLocation();
                    }
                    if (loc != null) {
                        Lon = loc.getLongitude();
                        Lat = loc.getLatitude();
                    } else {
                        if (progressDialog != null) {
                            // WeatherDialog.dismiss();
                            progressDialog.dismiss();
                            weatherHandler.sendMessage(weatherHandler.obtainMessage(WEATHER_GPS_FAIL, "無法取得目前位置"));
                        }
                        return;
                    }
                    // Internet Connect
                    Date date = new Date();
                    String MD5Code = CreatMD5Code.getMD5((String.valueOf(((date.getMonth() + 1)
                            + date.getHours()) * (1102 + date.getDate())) + "Kingway").getBytes());

                    // String WeatherURL =
                    // "http://59.120.150.54:8081/BikeGo/WeatherReport?"+

                    String WeatherURL = "http://biking.cpami.gov.tw/Service/WeatherReport?"
                            + "lon="
                            + String.valueOf(Lon).substring(0, 8)
                            + "&"
                            + "lat="
                            + String.valueOf(Lat).substring(0, 8)
                            + "&code="
                            + MD5Code;

                    HttpClient cliente = new DefaultHttpClient();
                    HttpResponse response;
                    HttpGet httpget = new HttpGet(WeatherURL);
                    response = cliente.execute(httpget);
                    HttpEntity entity = response.getEntity();
                    if (entity != null) {
                        InputStream instream = entity.getContent();
                        result = PublicTransportList.convertStreamToString(instream);
                        instream.close();
                    }
                    // Log.i("MapActivity.java","weather result="+result);
                    if (result.equalsIgnoreCase("null")) {
                        Log.i("MapActivity.java", "weather is null");
                    }
                    // String[] WeatherArray = result.split("\\|");
                    WeatherArray = result.split("\\|");
                    // WeatherText.setText(WeatherArray[3]+"-"+WeatherArray[4]+"℃");
                    Log.i("MapActivity.java", "strArray[1]=" + WeatherArray[1]);

                    int WeatherIconId = getResources().getIdentifier(
                            "ic_weather_" + WeatherArray[1], "drawable",
                            getPackageName());
                    weatherHandler.sendMessage(weatherHandler.obtainMessage(
                            GET_WEATHER_FINISH, String.valueOf(WeatherIconId)));
                    // imWeather.setImageResource(WeatherIconId);

                    // Toast.makeText(MapActivity.this,WeatherArray[0]+WeatherArray[1]+WeatherArray[2]+WeatherArray[3]+"-"+WeatherArray[4]
                    // ,9000).show();
                    if (progressDialog != null) {
                        // WeatherDialog.dismiss();
                        progressDialog.dismiss();
                    }
                    // Thread.sleep(60*60*1000);
                    // }
                }
                catch (Exception e) {
                    // TODO: handle exception
                    e.printStackTrace();
                    if (progressDialog != null) {
                        // WeatherDialog.dismiss();
                        progressDialog.dismiss();
                    }
                }
            }
        }.start();
    }

    public void wakeUpSetPointView(double[] position) {
        switchView(R.layout.setting_start_end_point, R.id.option_view, R.id.setPoint_view);
        isSetStEnPointState = true;
        setRoutePointButtonListner();
        GeoPoint mapXYGeoPoint = new GeoPoint(position[0], position[1]);
        String str = "Lon:" + position[0] + "\nLat:" + position[1];

        Log.i("MapActivity.java", "position MapXY=" + str);

        switch (getWhichButton()) {
            case 0:
                setPosition(str, mapXYGeoPoint, START_POINT);
                break;
            case 1:
                setPosition(str, mapXYGeoPoint, ESS1_POINT);
                break;
            case 2:
                setPosition(str, mapXYGeoPoint, ESS2_POINT);
                break;
            case 3:
                setPosition(str, mapXYGeoPoint, END_POINT);
                break;
            default:
                break;
        }

    }

    public void pointOnMap(int whichButtonSrc) {
        // if (this.getResources().getConfiguration().orientation ==
        // Configuration.ORIENTATION_LANDSCAPE) {
        // Log.i("MapActivity.java", "landscape");
        // //切到橫版
        // switchView(R.layout.map_v, R.id.setPoint_view, R.id.option_view);
        // }else if (this.getResources().getConfiguration().orientation ==
        // Configuration.ORIENTATION_PORTRAIT) {
        // Log.i("MapActivity.java", "portrait");
        // //切到直版
        switchView(R.layout.map_options, R.id.setPoint_view, R.id.option_view);
        // }
        RelativeLayout map_menu_bg = (RelativeLayout) findViewById(R.id.map_menu_bg);
        ImageButton ibUserLocation = (ImageButton) findViewById(R.id.to_user_location);
        ImageButton ibFavorite = (ImageButton) findViewById(R.id.to_favorite);
        ImageButton ibNavigation = (ImageButton) findViewById(R.id.to_navi);
        ImageButton ibTrackRecord = (ImageButton) findViewById(R.id.to_track_record);
        ImageButton ibPOI = (ImageButton) findViewById(R.id.to_poi);
        ImageView imWeatherBg = (ImageView) findViewById(R.id.weather_bg);
        ImageView imWeather = (ImageView) findViewById(R.id.weather);
        TextView textWeather = (TextView) findViewById(R.id.weather_text);
        TextView textUVWeather = (TextView) findViewById(R.id.weather_uv_text);
        ibZoomIn = (ImageButton) findViewById(R.id.zoom_in);
        ibZoomOut = (ImageButton) findViewById(R.id.zoom_out);

        map_menu_bg.setVisibility(View.INVISIBLE);
        ibUserLocation.setVisibility(View.INVISIBLE);
        ibFavorite.setVisibility(View.INVISIBLE);
        ibNavigation.setVisibility(View.INVISIBLE);
        ibTrackRecord.setVisibility(View.INVISIBLE);
        ibPOI.setVisibility(View.INVISIBLE);
        imWeatherBg.setVisibility(View.INVISIBLE);
        imWeather.setVisibility(View.INVISIBLE);
        textWeather.setVisibility(View.INVISIBLE);
        textUVWeather.setVisibility(View.INVISIBLE);

        ibZoomIn.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                Log.i("MapActivity.java", "ibZoomIn in pointOnMap");
                mapView.zoomIn();
            }
        });

        ibZoomOut.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                mapView.zoomOut();
            }
        });
        isSetStEnPointState = false;
        setWhichButton(whichButtonSrc);
    }

    public void setWhichButton(int WhichButtonSrc) {
        ButtonSrc = WhichButtonSrc;
    }

    public int getWhichButton() {
        return ButtonSrc;
    }

    private int checkPointSetting() {
        int flag = 0;
        if (StartPointArray[0] == 0.0 && StartPointArray[1] == 0.0) {
            if (EndPointArray[0] == 0.0 && EndPointArray[1] == 0.0) {
                flag = NoStartEndPointFlag;
            } else {
                flag = NoStartPointFlag;
            }
        } else if (EndPointArray[0] == 0.0 && EndPointArray[1] == 0.0) {
            flag = NoEndPointFlag;
        }
        return flag;
    }

    private String isPointAvailable() {
        String str = null;
        if (engine.showcitytownname(StartPointArray[0], StartPointArray[1]) == null) {
            str = "起點位置無效,請重新設定。";
        } else if (Ess1PointArray[0] != 0.0 && Ess1PointArray[1] != 0.0) {
            if (engine.showcitytownname(Ess1PointArray[0], Ess1PointArray[1]) == null)
                str = "經過點1位置無效,請重新設定。";
        } else if (Ess2PointArray[0] != 0.0 && Ess2PointArray[1] != 0.0) {
            if (engine.showcitytownname(Ess2PointArray[0], Ess2PointArray[1]) == null)
                str = "經過點2位置無效,請重新設定。";
        } else if (engine.showcitytownname(EndPointArray[0], EndPointArray[1]) == null) {
            str = "終點點位置無效,請重新設定。";
        }
        return str;
    }

    private void clearPoint() {
        TextStartName = "請選擇起點";
        // TextStartPoint.setText(TextStartName);
        StartPointArray[0] = 0.0;
        StartPointArray[1] = 0.0;

        TextEss1Name = "經過點1";
        // TextEss1Point.setText(TextEss1Name);
        Ess1PointArray[0] = 0.0;
        Ess1PointArray[1] = 0.0;

        TextEss2Name = "經過點2";
        // TextEss2Point.setText(TextEss2Name);
        Ess2PointArray[0] = 0.0;
        Ess2PointArray[1] = 0.0;

        TextEndName = "請選擇終點";
        // TextEndPoint.setText(TextEndName);
        EndPointArray[0] = 0.0;
        EndPointArray[1] = 0.0;
    }

    // 將秒數換成時分秒的時間格式
    private String toTimer(int s) {
        int hh, mm, ss;
        hh = s / (60 * 60);
        mm = (s - hh * 60 * 60) / 60;
        ss = s - hh * 60 * 60 - mm * 60;

        return ((hh < 10) ? "0" + hh : hh) + ":" + ((mm < 10) ? "0" + mm : mm)
                + ":" + ((ss < 10) ? "0" + ss : ss);
    }

    // 計算卡路里
    public int cal(int time) {
        double basic = 0;

        int sex = SettingManager.getUserSex();
        int age = SettingManager.getUserAge();
        int height = SettingManager.getUserHeight();
        int weight = SettingManager.getUserWeight();

        double manBasic = ((13.75 * weight) + (5 * height) - (6.76 * age) + 66) / 24;// 男:
        double womanBasic = ((9.56 * weight) + (1.85 * height) - (4.68 * age) + 65) / 24;// 女:

        if (sex == 1)
            basic = manBasic;
        else if (sex == 2)
            basic = womanBasic;

        int cal = (int) ((3 * basic) * (time / 3600));
        return cal;
    }

    private void StartTrackRecord() {
        tEngine = TrackEngine.getInstance();
        tEngine.InitializeGPS(MapActivity.this);
        insTrack = new Track(MapActivity.this);
        insTrack.setName("導航軌跡");
        insTrack.setDifficulty(1);
        insTrack.setDescription("困難度:" + "1顆星。");
        insTrack.setCreateTime();
        tEngine.setTrack(insTrack);
        tEngine.Start();
    }

    private void StopTrackRecord() {
        tEngine = TrackEngine.getInstance();
        if (!tEngine.getRecordingStatus().equals(TrackRecordingStatus.STOPED))
            tEngine.Stop();
        if (TrackPoint.getTrackPoints(this, insTrack.getID()).getCount() < 2) {
            Track.Erase(this, insTrack.getID());

            UtilDialog uit = new UtilDialog(MapActivity.this);
            uit.showDialog_route_plan_choice("軌跡", "此軌跡為空值，無法存檔!!", "確定", null);
        }
    }

    public static void setGoImmediately(boolean isgo) {
        GO_RIGHT_NOW = isgo;
    }

    public static boolean getGoImmediately() {
        return GO_RIGHT_NOW;
    }

    private void updateTrafficCondition() {
        new Thread() {
            @Override
            public void run() {
                File file = new File("/sdcard/BikingData/myloc");

                FileOutputStream fos = null;
                try {
                    fos = new FileOutputStream(file);
                } catch (FileNotFoundException e1) {
                    e1.printStackTrace();
                }
                DataOutputStream dos = new DataOutputStream(fos);
                // Internet Connect
                try {
                    Date date = new Date();
                    String MD5Code = CreatMD5Code.getMD5((String.valueOf(((date
                            .getMonth() + 1) + date.getHours())
                            * (1207 + date.getDate())) + "Kingway").getBytes());
                    // String TrafficAlertUploadURL =
                    // "http://192.168.1.186:8080/BikeGo/TrafficAlertList?AlertStartTime=201111111334&AlertEndTime=201111111334&Code="+MD5Code;
                    String TrafficAlertUploadURL = "http://biking.cpami.gov.tw/Service/TrafficAlertList?AlertStartTime=201111111334&AlertEndTime=201111111334&Code="
                            + MD5Code;
                    HttpClient cliente = new DefaultHttpClient();
                    HttpResponse response;
                    HttpPost httpPost = new HttpPost(TrafficAlertUploadURL);
                    response = cliente.execute(httpPost);
                    HttpEntity entity = response.getEntity();

                    if (entity != null) {
                        InputStream instream = entity.getContent();
                        result = PublicTransportList
                                .convertStreamToString(instream);
                        instream.close();
                    }
                    Log.i("MapActivity.java", "TrafficAlertUpload result="
                            + result);

                    if (result.equalsIgnoreCase("null")) {
                        Log.i("MapActivity.java",
                                "TrafficAlertUpload result is null");
                    }
                    // WaitDialog.dismiss();
                    updataTrafficConditionHandler
                            .sendMessage(updataTrafficConditionHandler
                                    .obtainMessage(GET_WEATHER_FINISH,
                                            String.valueOf(0)));
                } catch (Exception e) {
                    e.printStackTrace();
                    Log.i("MapActivity.java", "traffic fail");
                    // WaitDialog.dismiss();
                    updataTrafficConditionHandler
                            .sendMessage(updataTrafficConditionHandler
                                    .obtainMessage(WEATHER_WEB_FAIL, "路況更新失敗"));
                    return;
                }

                int number = getNumber(result);
                Title = new String[number];
                Detail = new String[number];
                CityID = new String[number];
                Lat = new String[number];
                Lon = new String[number];

                JSONArray jaRoute = null;
                try {
                    jaRoute = new JSONArray(result);
                } catch (Exception e) {
                    e.printStackTrace();
                }

                for (int i = 0; i < number; i++) {
                    try {
                        String ObjSource = jaRoute.get(i).toString();
                        JSONObject jo = new JSONObject(ObjSource);
                        Title[i] = jo.getString("title");
                        Detail[i] = jo.getString("detail");
                        CityID[i] = jo.getString("cityid");
                        Lat[i] = jo.getString("lat");
                        Lon[i] = jo.getString("lon");
                        try {
                            dos.writeBytes("," + Lon[i] + "," + Lat[i]
                                    + ",1,093345678,,adress," + "\n");
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
                engine.reloadmyloc();
                try {
                    dos.close();
                    fos.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }.start();
    }

    private void checkBreakState() {

        mapForDistance = new HashMap<>();
        final Map<String, String> detail = new HashMap<>();
        new Thread() {
            @Override
            public void run() {
                Double[] result1;

                // check break point
                try {
                    Double currentlon = 0.0;
                    Double currentlat = 0.0;
                    Location loc = null;
                    if (ApplicationGlobal.gpsListener != null) {
                        loc = ApplicationGlobal.gpsListener.getLastLocation();
                    }
                    if (loc != null) {
                        currentlat = new Double(loc.getLatitude());
                        currentlon = new Double(loc.getLongitude());
                    }
                    // double pointX=25.026973,pointY=121.524153;

                    // int offset=100000000;
                    int offset = 50000000;
                    Double[] currentXY = CoordinateUtil.LonlatToTwd97(
                            Double.parseDouble(currentlon.toString().substring(0, 9)),
                            Double.parseDouble(currentlat.toString().substring(0, 9)));
                    double twd97X = currentXY[0];
                    double twd97Y = currentXY[1];
                    double breakPointX;
                    double breakPointY;

                    // 下面for迴圈檢查網路下載的路況JSON中每件路況的經緯度是否有在範圍內
                    for (int i = 0; i < getNumber(result); i++) {

                        result1 = CoordinateUtil.LonlatToTwd97(
                                Double.parseDouble(Lat[i]),
                                Double.parseDouble(Lon[i]));

                        breakPointX = result1[0];
                        breakPointY = result1[1];

                        if ((breakPointX < (twd97X + offset)) && (twd97X > (twd97X - offset))) {
                            if ((breakPointY < (twd97Y + offset)) && (breakPointY > (twd97Y - offset))) {
                                // 在範圍內的的路況放到 一個HashMap<String, Double>
                                Double D = calculateDistance(twd97X, twd97Y, breakPointX, breakPointY);
                                String str = Title[i];
                                String detailInfo = Detail[i];
                                mapForDistance.put(str, D);
                                detail.put(str, detailInfo);
                            }
                        }
                    }
                    // sortPoint function把在範圍內的路況依照離使用者最近的距離由小到大做排序
                    List<Map.Entry<String, Double>> list = sortPoint(mapForDistance);
                    titelstr.setLength(0);

                    if (list.size() <= 3) {
                        int j = 1;
                        for (Entry<String, Double> b : list) {
                            titleForPop.append((j) + "."
                                    + b.getKey().toString() + ";");
                            titelstr.append((j)
                                    + "."
                                    + b.getKey().toString()
                                    + "\n"
                                    + detail.get(b.getKey().toString() + "\n\n"));
                            j++;
                        }
                    }
                    else {
                        for (int i = 0; i < 3; i++) {
                            titleForPop.append((i + 1) + "." + list.get(i).getKey().toString() + " ");
                            titelstr.append((i + 1)
                                    + "."
                                    + list.get(i).getKey().toString()
                                    + "\n"
                                    + detail.get(list.get(i).getKey().toString()) + "\n\n");
                        }
                    }
                    if (mapForDistance.size() == 0)
                        breakHandler.sendMessage(breakHandler.obtainMessage(GET_NOTHING, ""));
                    else
                        breakHandler.sendMessage(breakHandler.obtainMessage(GET_BREAK, titleForPop.toString()));
                }
                catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }.start();
    }

    /**
     * sort point by distance.
     *
     * @return sorted list
     */
    private List<Entry<String, Double>> sortPoint(Map<String, Double> map) {
        List<Map.Entry<String, Double>> list_data = new ArrayList<>(map.entrySet());

        Collections.sort(list_data,
                new Comparator<Map.Entry<String, Double>>() {
                    public int compare(Map.Entry<String, Double> o1, Map.Entry<String, Double> o2) {
                        return (int) ((o2.getValue() - o1.getValue()) * 1000.0);
                    }
                });
        return list_data;
    }

    /**
     * calculate Distance for between two point.
     *
     * @return distance
     */
    private double calculateDistance(double x1, double y1, double x2, double y2) {
        double temp_A, temp_B;
        double Distance; // 用來儲存算出來的斜邊距離
        temp_A = x1 > x2 ? (x1 - x2) : (x2 - x1); // 横向距離 (取正數，因為邊長不能是負數)
        temp_B = y1 > y2 ? (y1 - y2) : (y2 - y1); // 縱向距離 (取正數，因為邊長不能是負數)
        Distance = java.lang.Math.sqrt(temp_A * temp_A + temp_B * temp_B); // 計算斜邊距離
        return Distance;
    }

    private int getNumber(String source) {
        int rtn = 0;
        try {
            JSONArray jaRoute = new JSONArray(source);
            rtn = jaRoute.length();
        }
        catch (JSONException je) {
            je.printStackTrace();
        }
        return rtn;
    }

    private void initialBrokeHandler() {
        breakHandler = new Handler() {
            @Override
            public void handleMessage(Message msg) {
                if (msg.what == GET_BREAK) {
                    if (MapActivity.this.getResources().getConfiguration().orientation == Configuration.ORIENTATION_LANDSCAPE) {
                        if (sdkVersion < 11) {
                            pop.showAtLocation(
                                    findViewById(R.id.navi_option_view),
                                    Gravity.BOTTOM | Gravity.LEFT,
                                    PopupWindow_X, PopupWindow_landscape_Y);
                        }
                        else {
                            pop.showAtLocation(
                                    findViewById(R.id.navi_option_view),
                                    Gravity.BOTTOM | Gravity.LEFT,
                                    PopupWindow_X,
                                    PopupWindow_landscape_Y_ForTablet);
                        }
                        breaktext.setText((String) msg.obj);
                    }
                    else if (MapActivity.this.getResources().getConfiguration().orientation == Configuration.ORIENTATION_PORTRAIT) {
                        if (sdkVersion < 11) {
                            pop.showAtLocation(
                                    findViewById(R.id.navi_option_view),
                                    Gravity.BOTTOM | Gravity.LEFT,
                                    PopupWindow_X, PopupWindow_portrait_Y);
                        }
                        else {
                            pop.showAtLocation(
                                    findViewById(R.id.navi_option_view),
                                    Gravity.BOTTOM | Gravity.LEFT,
                                    PopupWindow_X,
                                    PopupWindow_portrait_Y_ForTablet);
                        }
                        breaktext.setText((String) msg.obj);
                    }
                }
                else if (msg.what == GET_NOTHING) { // 沒有再偵測到路況就關掉路況PopWindow
                    if (pop.isShowing())
                        pop.dismiss();
                }
            }
        };
    }

    // 取的取代氣機車得減碳量
    private String[] getCarbonEmissionReductions(double distance) {
        Log.i("MapActivity.java", "distance=" + distance);
        String str[] = new String[2];
        double CarCarbon = ((distance / 1000) / 9.1) * 2.26;
        double bicycleCarbon = ((distance / 1000) / 36) * 2.26;

        if (CarCarbon == 0.0) {
            str[0] = "0.0";
            str[1] = "0.0";
        }
        else {
            str[0] = String.valueOf(CarCarbon).toString().split("\\.")[0]
                    + "."
                    + String.valueOf(CarCarbon).toString().split("\\.")[1]
                    .substring(1, 3);
            str[1] = String.valueOf(bicycleCarbon).toString().split("\\.")[0]
                    + "."
                    + String.valueOf(bicycleCarbon).toString().split("\\.")[1]
                    .substring(1, 3);
        }
        return str;
    }

    private void startNavi() {
        if (!isDoEemulationNavi && (SettingManager.isTrackConfirmEnabled() ||
                TrackEngine.getInstance().getRecordingStatus().equals(TrackRecordingStatus.RECORDING))) {

            UtilDialog uit = new UtilDialog(MapActivity.this) {
                @Override
                public void click_btn_1() {
                    isRecordTrack = true;
                    startNavigation();
                    StartTrackRecord();
                    super.click_btn_1();
                }

                @Override
                public void click_btn_2() {
                    isRecordTrack = false;
                    startNavigation();
                    super.click_btn_2();
                }
            };
            uit.showDialog_route_plan_choice("導航時是否錄製軌跡?", null, "錄製", "不錄製");
        }
        else
            startNavigation();

        isSetStEnPointState = false;
    }

    /* 地圖 icon 說明 */
    @Override
    public void onClick(View arg0) {
        Intent intent = new Intent(this, MapIconDescriptionActivity.class);
        this.startActivity(intent);
        this.overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
    }
}