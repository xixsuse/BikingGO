package com.kingwaytek.cpami.bykingTablet.app.ui.settings;

import android.widget.TextView;

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
    }

    @Override
    protected void setListener() {

    }

    private void setVersions() {
        text_appVersion.setText(BuildConfig.VERSION_NAME);
    }
}
