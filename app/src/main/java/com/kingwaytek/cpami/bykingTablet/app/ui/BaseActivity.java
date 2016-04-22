package com.kingwaytek.cpami.bykingTablet.app.ui;

import android.content.Context;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.TextView;

import com.kingwaytek.cpami.bykingTablet.R;
import com.kingwaytek.cpami.bykingTablet.app.web.WebAgent;

import java.io.IOException;
import java.io.InputStream;

/**
 * 這裡有 Actionbar and 各種基本會呼叫到的方法，<br>
 * 每個 Activity都應該繼承這裡！
 *
 * @author Vincent (2016/04/18)
 */
public abstract class BaseActivity extends AppCompatActivity {

    protected final String TAG = getClass().getSimpleName();

    protected abstract void init();
    protected abstract String getActionBarTitle();
    protected abstract int getLayoutId();
    protected abstract void findViews();
    protected abstract void setListener();

    private ActionBar actionbar;
    private static View actionbarView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setTheme(R.style.AppTheme);

        setContentView(getLayoutId());

        setActionBar();

        findViews();
        setListener();
        init();
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    private void setActionBar() {
        actionbar = getSupportActionBar();

        if (notNull(actionbar)) {
            actionbar.setDisplayShowTitleEnabled(false);
            actionbar.setHomeButtonEnabled(false);
            actionbar.setDisplayOptions(ActionBar.DISPLAY_SHOW_CUSTOM);

            actionbar.setCustomView(R.layout.include_foolish_action_bar);
            Toolbar toolbar = (Toolbar) actionbar.getCustomView().getParent();
            toolbar.setContentInsetsAbsolute(0, 0);

            TextView title = (TextView) actionbar.getCustomView().findViewById(R.id.actionBar_title);
            title.setText(getActionBarTitle());

            actionbarView = actionbar.getCustomView();
            showActionbar(true);
        }
    }

    public static View getActionbarView() {
        if (notNull(actionbarView))
            return actionbarView;
        return null;
    }

    public void showActionbar(boolean isShow) {
        if (isShow)
            actionbar.show();
        else
            actionbar.hide();
    }

    public static boolean notNull(Object anyObject) {
        return anyObject != null;
    }

    public void hideKeyboard(View view, boolean isHide) {
        InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        if (isHide)
            imm.hideSoftInputFromWindow(view.getApplicationWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
        else
            imm.toggleSoftInput(0, InputMethodManager.HIDE_NOT_ALWAYS);
    }

    protected void closeInputStream(InputStream is) {
        try {
            is.close();
        }
        catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        WebAgent.stopRetryThread();
    }
}
