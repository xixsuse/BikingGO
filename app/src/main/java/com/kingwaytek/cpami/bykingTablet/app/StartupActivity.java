package com.kingwaytek.cpami.bykingTablet.app;

import android.content.Context;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.Environment;
import android.provider.Settings;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.KeyEvent;
import android.view.Window;
import android.view.WindowManager;

import com.kingwaytek.cpami.bykingTablet.R;
import com.kingwaytek.cpami.bykingTablet.app.Infomation.CommunicationBaseActivity;
import com.kingwaytek.cpami.bykingTablet.callbacks.OnEngineReadyCallBack;
import com.kingwaytek.cpami.bykingTablet.hardware.GPSListener;
import com.kingwaytek.cpami.bykingTablet.utilities.UtilDialog;
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
public class StartupActivity extends CommunicationBaseActivity implements OnEngineReadyCallBack {

    private static final String DIR_DATA = "BikingData";
    private sonav engine;

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
    }

    @Override
    protected void onResume() {
        super.onResume();
        deleteBikingDataDir();

        // check GPS
        manager = (LocationManager) this.getSystemService(Context.LOCATION_SERVICE);

        // Check BikingData File
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
            uit.showDialog_route_plan_choice(getString(R.string.data_not_install_yet), null, getString(R.string.confirm), null);
            // Check GPS
        }
        else {
            if (manager.isProviderEnabled(LocationManager.GPS_PROVIDER) || manager.isProviderEnabled(LocationManager.NETWORK_PROVIDER)) {
                ApplicationGlobal.gpsListener.setEnabled(true);
                engineInitialize();
            }
            else {
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
                        engineInitialize();
                    }
                };
                uit.showDialog_route_plan_choice(getString(R.string.gps_is_not_enabled), null,
                        getString(R.string.confirm), getString(R.string.confirm_cancel));
            }
        }
    }

    @Override
    public void onEngineInitializing() {

    }

    @Override
    public void onEngineReady() {
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
    }

    @Override
    public void onEngineInitFailed() {
        AlertDialogUtil.showMessage(StartupActivity.this, getString(R.string.msg_err), R.drawable.dialog_error);
    }

    /**
     * Prevent KeyEvent.KEYCODE_MENU event to be propagated in this activity.
     */
    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        return keyCode == KeyEvent.KEYCODE_MENU;
    }

    private void engineInitialize() {
        try {
            // engine.setresizefont(3.5);//xxhdpi
            engine.setIconSize(1);
            engine.setresizefont(2);// xhdpis
            engine.init(getApplicationContext(), PreferenceActivity.getDataDirectory(this), this);
        }
        catch (Throwable t) {
            Log.e(getClass().toString(), t.getMessage(), t);
            engine.callOnEngineInitFailed();
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