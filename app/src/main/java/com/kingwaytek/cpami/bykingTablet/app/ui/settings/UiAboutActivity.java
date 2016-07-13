package com.kingwaytek.cpami.bykingTablet.app.ui.settings;

import android.widget.TextView;

import com.kingwaytek.cpami.bykingTablet.AppController;
import com.kingwaytek.cpami.bykingTablet.BuildConfig;
import com.kingwaytek.cpami.bykingTablet.R;
import com.kingwaytek.cpami.bykingTablet.app.ui.BaseActivity;

/**
 * 關於頁面
 *
 * @author Vincent (2016/7/12)
 */
public class UiAboutActivity extends BaseActivity {

    private TextView text_appVersion;
    private TextView text_mapVersion;

    @Override
    protected void init() {
        setVersions();
    }

    @Override
    protected String getActionBarTitle() {
        return getString(R.string.preference_about);
    }

    @Override
    protected int getLayoutId() {
        return R.layout.activity_about;
    }

    @Override
    protected void findViews() {
        text_appVersion = (TextView) findViewById(R.id.about_appVersion);
        text_mapVersion = (TextView) findViewById(R.id.about_mapVersion);
    }

    @Override
    protected void setListener() {

    }

    private void setVersions() {
        text_appVersion.setText(BuildConfig.VERSION_NAME);
        text_mapVersion.setText(AppController.getInstance().getDataVersion());
    }
}
