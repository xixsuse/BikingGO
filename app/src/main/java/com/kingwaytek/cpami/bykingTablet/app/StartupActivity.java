package com.kingwaytek.cpami.bykingTablet.app;

import android.app.Activity;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.view.KeyEvent;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.LinearLayout;

import com.kingwaytek.cpami.bykingTablet.R;
import com.kingwaytek.cpami.bykingTablet.utilities.SettingManager;
import com.kingwaytek.cpami.bykingTablet.utilities.Utility;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

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

    private static final long SPLASH_SCREEN_DURATION = 1500;

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

    private void showAnnouncementIfNecessary() {
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                if (SettingManager.isAnnouncementNecessary())
                    showAnnouncement();
                else
                    goToMain();
            }
        }, SPLASH_SCREEN_DURATION);
    }

    private void showAnnouncement() {
        View transparentView = findViewById(R.id.transparentView);
        LinearLayout announceLayout = (LinearLayout) findViewById(R.id.announceLayout);

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
                goToMain();
            }
        });
    }

    private void goToMain() {
        startActivity(new Intent(this, MainActivity.class));
        finish();
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

    /**
     * 每次啟動時把myloc檔裡的內容清空
     */
    private void clear_myloc() {
        final String DIR_DATA = "/BikingData/myloc";

        File file = new File(Environment.getExternalStorageDirectory().getPath() + DIR_DATA);
        FileOutputStream fos = null;
        try {
            fos = new FileOutputStream(file, false);
        }
        catch (FileNotFoundException e) {
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
}