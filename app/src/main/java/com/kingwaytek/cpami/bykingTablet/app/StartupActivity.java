package com.kingwaytek.cpami.bykingTablet.app;

import android.app.Activity;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.view.KeyEvent;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.ScrollView;

import com.kingwaytek.cpami.bykingTablet.R;
import com.kingwaytek.cpami.bykingTablet.app.ui.UiMainMapActivity;
import com.kingwaytek.cpami.bykingTablet.utilities.FavoriteHelper;
import com.kingwaytek.cpami.bykingTablet.utilities.PermissionCheckHelper;
import com.kingwaytek.cpami.bykingTablet.utilities.SettingManager;
import com.kingwaytek.cpami.bykingTablet.utilities.Util;
import com.kingwaytek.cpami.bykingTablet.utilities.Utility;

/**
 * This is the first activity for launching. Shows splash screen and do
 * initialization .
 *
 * @author Harvey Cheng(harvey@kingwaytek.com)
 *
 *
 * 2016/04/12:
 * 已重寫，現在看起來乾淨多了 (ˊ_>ˋ)
 *
 * @author Vincent
 */
public class StartupActivity extends Activity {

    private static final long SPLASH_SCREEN_DURATION = 1000;
    private static final long PERMISSION_REQUIREMENT_TIPS = 3500;

    @Override
    public void onCreate(Bundle icicle) {
        super.onCreate(icicle);
        setWindowFeatures();
        setContentView(R.layout.activity_announce);

        showAnnouncementIfNecessary();
    }

    private void setWindowFeatures() {
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
    }

    private void checkStoragePermissionAndInit() {
        if (PermissionCheckHelper.checkFileStoragePermissions(this))
            init();
    }

    private void init() {
        Util.initUserDatabase();
        FavoriteHelper.initPoiFavorite();
        goToMain();
    }

    private void showAnnouncementIfNecessary() {
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                if (SettingManager.isAnnouncementNecessary())
                    showAnnouncement();
                else
                    checkStoragePermissionAndInit();
            }
        }, SPLASH_SCREEN_DURATION);
    }

    private void showAnnouncement() {
        View transparentView = findViewById(R.id.transparentView);
        final ScrollView announceLayout = (ScrollView) findViewById(R.id.announceLayout);

        Button confirmButton = (Button) findViewById(R.id.startup_terms_of_use_summit);
        final CheckBox checkbox = (CheckBox) findViewById(R.id.checkbox_terms_of_use_summit);

        transparentView.setVisibility(View.VISIBLE);
        announceLayout.setVisibility(View.VISIBLE);

        final float scale = this.getResources().getDisplayMetrics().density;

        checkbox.setPadding(
                checkbox.getPaddingLeft() + (int) (10.0f * scale + 0.5f),
                checkbox.getPaddingTop(),
                checkbox.getPaddingRight(),
                checkbox.getPaddingBottom()
        );

        confirmButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SettingManager.setAnnouncementNecessary(!checkbox.isChecked());
                checkStoragePermissionAndInit();
            }
        });
    }

    private void goToMain() {
        startActivity(new Intent(this, UiMainMapActivity.class));
        finish();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String permissions[], @NonNull int[] grantResults) {
        switch (requestCode) {
            case PermissionCheckHelper.PERMISSION_REQUEST_CODE_STORAGE:
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED
                        && grantResults[1] == PackageManager.PERMISSION_GRANTED)
                {
                    init();
                }
                else {
                    Utility.toastLong(getString(R.string.storage_permission_denied_force_require));

                    new Handler().postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            checkStoragePermissionAndInit();
                        }
                    }, PERMISSION_REQUIREMENT_TIPS);
                }
                break;
        }
    }

    /**
     * Prevent KeyEvent.KEYCODE_MENU event to be propagated in this activity.
     */
    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            Utility.forceCloseTask();
            return false;
        }
        else
            return keyCode == KeyEvent.KEYCODE_MENU;
    }
}