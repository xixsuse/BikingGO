package com.kingwaytek.cpami.bykingTablet.app.ui;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Environment;
import android.provider.Settings;
import android.util.Log;

import com.kingwaytek.cpami.bykingTablet.R;
import com.kingwaytek.cpami.bykingTablet.app.AlertDialogUtil;
import com.kingwaytek.cpami.bykingTablet.app.AnnounceActivity;
import com.kingwaytek.cpami.bykingTablet.app.MapDownloadActivity;
import com.kingwaytek.cpami.bykingTablet.app.PreferenceActivity;
import com.kingwaytek.cpami.bykingTablet.callbacks.OnEngineReadyCallBack;
import com.kingwaytek.cpami.bykingTablet.hardware.MyLocationManager;
import com.kingwaytek.cpami.bykingTablet.utilities.UtilDialog;
import com.sonavtek.sonav.sonav;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

/**
 * Created by vincent.chang on 2016/4/19.
 */
public abstract class EngineCheckActivity extends Activity implements OnEngineReadyCallBack {

    protected abstract void onCheckAllDone();

    private static final String DIR_DATA = "BikingData";

    private static boolean isInit;
    private sonav engine;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    protected void onResume() {
        super.onResume();

    }

    synchronized private void userDatabaseInit() {
        String DATABASE_PATH = getString(R.string.SQLite_Usr_Database_Path);
        String DATABASE_NAME = getString(R.string.SQLite_Usr_Database_Name);

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
        // 從資源中讀取數據庫流
        InputStream input = getResources().openRawResource(R.raw.biking_data);

        OutputStream output = null;

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
    }

    private void checkMapData() {
        deleteBikingDataDir();

        // Check BikingData File
        File dir = new File(Environment.getExternalStorageDirectory().getPath() + "/" + DIR_DATA + "/");

        if (!dir.exists()) {
            UtilDialog uit = new UtilDialog(this) {
                @Override
                public void click_btn_1() {
                    super.click_btn_1();
                    final Intent i = new Intent(EngineCheckActivity.this, MapDownloadActivity.class);
                    startActivity(i);
                    finish();
                }

                @Override
                public void click_btn_2() {
                    super.click_btn_2();
                    finish();
                }
            };
            uit.showDialog_route_plan_choice(getString(R.string.data_not_install_yet), null, getString(R.string.confirm), null);
        }
        else
            checkGps();
    }

    private void checkGps() {
        if (MyLocationManager.isGpsDisabled()) {
            UtilDialog uit = new UtilDialog(this) {
                @Override
                public void click_btn_1() {
                    super.click_btn_1();
                    Intent intent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                    startActivity(intent);
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
        else
            engineInitialize();
    }

    private void engineInitialize() {
        try {
            engine = sonav.getInstance();
            engine.setIconSize(1);
            engine.setresizefont(2);// xhdpis
            engine.init(getApplicationContext(), PreferenceActivity.getDataDirectory(this), this);
        }
        catch (Throwable t) {
            Log.e(getClass().toString(), t.getMessage(), t);
            engine.callOnEngineInitFailed();
        }
    }

    @Override
    public void onEngineInitializing() {

    }

    @Override
    public void onEngineReady() {
        final String pv6 = "010601,010701,070401,071008,071415,080401";
        final String pv4 = "";
        final String pv2 = "01,02,03,04,05,06,07,08,09,10,11,12,13,14";

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
        Intent intent = new Intent(this, AnnounceActivity.class);

        if (params != null) {
            intent.putExtra("SMSToMapActivity", 1);
            intent.putExtras(params);
        }
        startActivity(intent);
        finish();
    }

    @Override
    public void onEngineInitFailed() {
        AlertDialogUtil.showMessage(this, getString(R.string.msg_err), R.drawable.dialog_error);
    }

    private void deleteBikingDataDir() {

        File dir = Environment.getExternalStoragePublicDirectory(DIR_DATA + "_1");

        if (dir.exists())
            deleteDirectory(dir);
    }

    private void deleteDirectory(File dir) {
        for (File file : dir.listFiles()) {
            if (file.isDirectory()) {
                deleteDirectory(file);
            }
            else {
                Log.i("File deleted:", file.getName());
                file.delete();
            }
        }
        Log.i("Dir deleted:", dir.getName());
        dir.delete();
    }


    @Override
    protected void onDestroy() {
        super.onDestroy();

    }
}
