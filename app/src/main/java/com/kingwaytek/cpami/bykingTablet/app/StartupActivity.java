package com.kingwaytek.cpami.bykingTablet.app;

import android.content.Context;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.provider.Settings;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.KeyEvent;
import android.view.Window;
import android.view.WindowManager;

import com.kingwaytek.cpami.bykingTablet.R;
import com.kingwaytek.cpami.bykingTablet.app.Infomation.CommunicationBaseActivity;
import com.kingwaytek.cpami.bykingTablet.hardware.GPSListener;
import com.sonavtek.sonav.sonav;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

/**
 * This is the first activity for launching. Shows splash screen and do
 * initialization .
 *
 * @author Harvey Cheng(harvey@kingwaytek.com)
 *
 *
 * Modified by Vincent on 2016/04/12.
 */
public class StartupActivity extends CommunicationBaseActivity {

    private static final int START_INIT = 1;

    private static final int INIT_SUCCESS = 2;

    private static final int INIT_FAILED = 3;
    private static final String DIR_DATA = "BikingData";
    private sonav engine;

    private static Handler handler; // dealing with initialization events

    private static Thread initializer; // Thread which doing initialization

    private LocationManager manager;

    public static String DataVersion;
    public String pv6 = "010601,010701,070401,071008,071415,080401";
    public String pv4 = "";
    public String pv2 = "01,02,03,04,05,06,07,08,09,10,11,12,13,14";

    @Override
    public void onCreate(Bundle icicle) {
        super.onCreate(icicle);

        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        // set views
        setContentView(R.layout.startup);

        DisplayMetrics dm = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(dm);

        int intScreenX = dm.widthPixels;
        int intScreenY = dm.heightPixels;

        userDatabaseInit(this);
        Log.i("StartActivity", "intScreenX = " + intScreenX + "  intScreenY = " + intScreenY);

        // enable GPS listener
        if (ApplicationGlobal.gpsListener == null)
            ApplicationGlobal.gpsListener = new GPSListener(this, 5000, 1);

        // create instance of engine
        engine = sonav.getInstance();

        Intent startActivityIntent = StartupActivity.this.getIntent();
        Log.i("StartActivity.java", "startActivityIntent.getIntExtra(SMS,0)=" + startActivityIntent.getIntExtra("SMS", 0));

        if (handler == null) {
            handler = new Handler() {

                @Override
                public void handleMessage(Message msg) {
                    super.handleMessage(msg);

                    switch (msg.what) {
                        case START_INIT:

                            initializer = new Thread() {

                                @Override
                                public void run() {
                                    initialize();

                                    while (engine.getState() != sonav.STATE_READY) {
                                        try {
                                            Thread.sleep(500);
                                        } catch (InterruptedException ex) {
                                            Log.e(getClass().toString(), ex.getMessage(), ex);
                                        }
                                    }
                                    try {
                                        Thread.sleep(2000);
                                    } catch (InterruptedException e) {
                                        // TODO Auto-generated catch block
                                        e.printStackTrace();
                                    }
                                    handler.sendMessage(handler.obtainMessage(INIT_SUCCESS));
                                }
                            };

                            initializer.start();

                            break;

                        case INIT_SUCCESS:
                            engine.setpoiauto(0);
                            engine.setpoivisible(pv6, pv4, pv2, 0);
                            engine.setpoivisible(pv6, pv4, pv2, 1);
                            engine.setpoivisible(pv6, pv4, pv2, 2);
                            engine.setpoivisible(pv6, pv4, pv2, 3);
                            engine.setpoivisible(pv6, pv4, pv2, 4);
                            engine.setpoivisible(pv6, pv4, pv2, 5);

                            /*** 清除marker ***/
                            // engine.setflagpoint(0, -1, -1);
                            engine.setflagpoint(1, -1, -1);
                            engine.setflagpoint(2, -1, -1);
                            engine.setflagpoint(3, -1, -1);
                            engine.setflagpoint(5, -1, -1);
                            /***************/

                            Bundle params = getIntent().getExtras();
                            Intent intent = new Intent(StartupActivity.this, AnnounceActivity.class);

                            if (params != null) {
                                intent.putExtra("SMSToMapActivity", 1);
                                intent.putExtras(params);
                            }
                            startActivity(intent);
                            finish();

                            break;

                        case INIT_FAILED:
                            AlertDialogUtil.showMessage(StartupActivity.this, getString(R.string.msg_err), R.drawable.dialog_error);
                            break;

                        default:

                            break;
                    }
                }
            };
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        deleteBikingDataDir();

        // check GPS
        manager = (LocationManager) this.getSystemService(Context.LOCATION_SERVICE);

        // Check BikingData File
        //File dir = new File("/sdcard/BikingData/");
        File dir = new File(Environment.getExternalStorageDirectory().getPath() + "/" + DIR_DATA + "/");

        if (!dir.exists()) {
            UtilDialog uit = new UtilDialog(StartupActivity.this) {
                @Override
                public void click_btn_1() {
                    final Intent i = new Intent(StartupActivity.this, MapDownloadActivity.class);
                    super.click_btn_1();
                    startActivity(i);
                    finish();
                }

                @Override
                public void click_btn_2() {
                    super.click_btn_2();
                    android.os.Process.killProcess(android.os.Process.myPid());
                }
            };
            uit.showDialog_route_plan_choice("尚未安裝圖資，是否前往下載?", null, "確定", null);
            // Check GPS
        }
        else {
            if (manager.isProviderEnabled(LocationManager.GPS_PROVIDER) || manager.isProviderEnabled(LocationManager.NETWORK_PROVIDER)) {
                ApplicationGlobal.gpsListener.setEnabled(true);
                handler.sendMessage(handler.obtainMessage(START_INIT));
            }
            else {
//				if (isFirstIn) {
                UtilDialog uit = new UtilDialog(StartupActivity.this) {
                    @Override
                    public void click_btn_1() {
                        final Intent i = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                        super.click_btn_1();
                        startActivity(i);
                    }

                    @Override
                    public void click_btn_2() {
                        super.click_btn_2();
                        handler.sendMessage(handler.obtainMessage(START_INIT));
                    }
                };
                uit.showDialog_route_plan_choice("您尚未開啟系統GPS功能,請先開啟。", null, "確定", "取消");
            }
        }
    }

    @Override
    public void onWindowFocusChanged(boolean hasFocus) {
        super.onWindowFocusChanged(hasFocus);

        // anim.start();
    }

    /**
     * Prevent KeyEvent.KEYCODE_MENU event to be propagated in this activity.
     */
    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        return keyCode == KeyEvent.KEYCODE_MENU;
    }

    private void initialize() {
        try {
            // engine.setresizefont(3.5);//xxhdpi
            engine.setIconSize(1);
            engine.setresizefont(2);// xhdpis
            engine.init(this, PreferenceActivity.getDataDirectory(this));
        }
        catch (Throwable t) {
            Log.e(getClass().toString(), t.getMessage(), t);
            handler.sendMessage(handler.obtainMessage(INIT_FAILED));
        }

        DataVersion = getResources().getString(R.string.DataVersion);
    }

    private boolean isInit = false;

    synchronized private void userDatabaseInit(Context context) {
        String DATABASE_PATH = context.getString(R.string.SQLite_Usr_Database_Path);
        String DATABASE_NAME = context.getString(R.string.SQLite_Usr_Database_Name);
        if (isInit)
            return;
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

        InputStream input;
        OutputStream output = null;

        // 從資源中讀取數據庫流
        input = context.getResources().openRawResource(R.raw.biking_data);

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

        isInit = true;
    }

    /**
     * 每次啟動時把myloc檔裡的內容清空
     */
    private void clear_myloc() {

        File file = new File(Environment.getExternalStorageDirectory().getPath() + "/" + DIR_DATA + "/myloc");
        FileOutputStream fos = null;
        try {
            fos = new FileOutputStream(file, false);

        } catch (FileNotFoundException e) {

            e.printStackTrace();
        }

        try {
            if (fos != null)
                fos.close();
        }
        catch (IOException e) {
            e.printStackTrace();
        }

    }

    private boolean checkIsGPSOpen() {
        if (manager.isProviderEnabled(LocationManager.GPS_PROVIDER) || manager.isProviderEnabled(LocationManager.NETWORK_PROVIDER)) {
            ApplicationGlobal.gpsListener.setEnabled(true);
            return true;
        }
        return false;
    }

    private boolean deleteBikingDataDir() {

        File dir = Environment.getExternalStoragePublicDirectory(DIR_DATA + "_1");

        if (!dir.exists()) {
            return dir.exists();
        }

        deleteDirectory(dir);
        return dir.exists();
    }

    private void deleteDirectory(File dir) {
        for (File file : dir.listFiles()) {
            if (file.isDirectory()) {
                deleteDirectory(file);
            }
            else {
                System.out.println("file delete:" + file.getName());
                file.delete();
            }
        }
        System.out.println("dir delete:" + dir.getName());
        dir.delete();
    }
}