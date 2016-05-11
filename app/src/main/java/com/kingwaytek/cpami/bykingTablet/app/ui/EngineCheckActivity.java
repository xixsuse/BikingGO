package com.kingwaytek.cpami.bykingTablet.app.ui;

import android.content.Intent;
import android.os.Environment;
import android.provider.Settings;
import android.util.Log;

import com.kingwaytek.cpami.bykingTablet.R;
import com.kingwaytek.cpami.bykingTablet.app.AlertDialogUtil;
import com.kingwaytek.cpami.bykingTablet.app.MapDownloadActivity;
import com.kingwaytek.cpami.bykingTablet.callbacks.OnEngineReadyCallBack;
import com.kingwaytek.cpami.bykingTablet.hardware.MyLocationManager;
import com.kingwaytek.cpami.bykingTablet.utilities.SettingManager;
import com.kingwaytek.cpami.bykingTablet.utilities.UtilDialog;
import com.sonavtek.sonav.sonav;

import java.io.File;

/**
 * 任何有使用到 sonav engine的 Activity都要繼承這裡！
 *
 * @author Vincent (2016/4/19).
 */
public abstract class EngineCheckActivity extends BaseActivity implements OnEngineReadyCallBack {

    protected abstract void onCheckAllDone();

    private static final String DIR_DATA = "BikingData";

    private boolean isInit;
    private sonav engine;

    @Override
    protected int getLayoutId() {
        return R.layout.startup;
    }

    @Override
    protected void init() {
        Log.i(TAG, "engineCheck Init!");
        showActionbar(false);
    }

    @Override
    protected void onResume() {
        super.onResume();
        checkMapData();
    }

    private void checkMapData() {
        if (!isInit) {
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
        if (!isInit) {
            try {
                engine = sonav.getInstance();
                engine.setIconSize(1);
                engine.setresizefont(2);// xhdpis
                engine.init(getApplicationContext(), SettingManager.getDataDirectory(), this);
            }
            catch (Throwable t) {
                Log.e(getClass().toString(), t.getMessage(), t);
                engine.callOnEngineInitFailed(this);
            }
        }
        else
            doneOfCheck();
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

        doneOfCheck();
        isInit = true;
    }

    @Override
    public void onEngineInitFailed() {
        AlertDialogUtil.showMessage(this, getString(R.string.msg_err), R.drawable.dialog_error);
        finish();
    }

    private void doneOfCheck() {
        setContentView(getLayoutId());
        findViews();
        setListener();
        showActionbar(true);
        onCheckAllDone();
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
