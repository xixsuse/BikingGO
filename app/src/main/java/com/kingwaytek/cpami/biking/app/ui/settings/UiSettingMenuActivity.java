package com.kingwaytek.cpami.biking.app.ui.settings;

import android.view.View;
import android.widget.LinearLayout;

import com.kingwaytek.cpami.biking.R;
import com.kingwaytek.cpami.biking.app.ui.BaseActivity;

/**
 * 設定主頁面
 *
 * @author Vincent (2016/7/12)
 */
public class UiSettingMenuActivity extends BaseActivity {

    private LinearLayout menu_naviLayout;
    private LinearLayout menu_healthLayout;
    private LinearLayout menu_mapDownloadLayout;
    private LinearLayout menu_aboutLayout;

    @Override
    protected void init() {

    }

    @Override
    protected String getActionBarTitle() {
        return getString(R.string.menu_settings);
    }

    @Override
    protected int getLayoutId() {
        return R.layout.activity_setting_menu;
    }

    @Override
    protected void findViews() {
        menu_naviLayout = (LinearLayout) findViewById(R.id.setting_menu_navi_layout);
        menu_healthLayout = (LinearLayout) findViewById(R.id.setting_menu_health_layout);
        menu_mapDownloadLayout = (LinearLayout) findViewById(R.id.setting_menu_map_download_layout);
        menu_aboutLayout = (LinearLayout) findViewById(R.id.setting_menu_about_layout);
    }

    @Override
    protected void setListener() {
        menu_naviLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //goTo(NaviSetting.class, false);
            }
        });

        menu_healthLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                goTo(UiHealthActivity.class, false);
            }
        });

        menu_mapDownloadLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //goTo(MapDownloadActivity.class, false);
            }
        });

        menu_aboutLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                goTo(UiAboutActivity.class, false);
            }
        });
    }
}
