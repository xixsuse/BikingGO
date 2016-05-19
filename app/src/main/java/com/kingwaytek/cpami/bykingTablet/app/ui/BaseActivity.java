package com.kingwaytek.cpami.bykingTablet.app.ui;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.ImageButton;
import android.widget.ProgressBar;
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

    private ProgressBar loadingCircle;

    protected ImageButton actionbar_menuBtn;
    private ImageButton actionbar_aroundBtn;
    private ImageButton actionbar_switchBtn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setTheme(R.style.AppTheme);

        setContentView(getLayoutId());

        setActionBar();
        findActionbarWidgetViewAndSetListener();

        findViews();
        setListener();
        init();
    }

    @Override
    protected void onResume() {
        super.onResume();
        Log.i(TAG, "onResume!!!");
    }

    private void setActionBar() {
        actionbar = getSupportActionBar();

        if (notNull(actionbar)) {
            actionbar.setDisplayShowTitleEnabled(false);
            actionbar.setDisplayHomeAsUpEnabled(true);
            actionbar.setHomeButtonEnabled(true);
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

    public void showRightButtons(boolean isShow) {
        if (isShow) {
            actionbar_aroundBtn.setVisibility(View.VISIBLE);
            actionbar_switchBtn.setVisibility(View.VISIBLE);
        }
        else {
            actionbar_aroundBtn.setVisibility(View.GONE);
            actionbar_switchBtn.setVisibility(View.GONE);
        }
    }

    private void findActionbarWidgetViewAndSetListener() {
        loadingCircle = (ProgressBar) actionbarView.findViewById(R.id.loadingCircle);
        actionbar_menuBtn = (ImageButton) actionbarView.findViewById(R.id.actionBar_menuButton);
        actionbar_aroundBtn = (ImageButton) actionbarView.findViewById(R.id.actionBar_aroundButton);
        actionbar_switchBtn = (ImageButton) actionbarView.findViewById(R.id.actionBar_switchButton);

        actionbar_menuBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onMenuButtonClick();
            }
        });

        actionbar_aroundBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onAroundButtonClick();
            }
        });

        actionbar_switchBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onSwitchButtonClick();
            }
        });
    }

    protected void onMenuButtonClick() {

    }

    protected void onAroundButtonClick() {

    }

    protected void onSwitchButtonClick() {

    }

    protected void setMenuButtonIcon(int iconRes) {
        actionbar_menuBtn.setImageDrawable(ContextCompat.getDrawable(this, iconRes));
    }

    protected void showLoadingCircle(boolean isShow) {
        if (isShow)
            loadingCircle.setVisibility(View.VISIBLE);
        else
            loadingCircle.setVisibility(View.GONE);
    }

    public void goTo(Class<?> clazz, boolean clearTop) {
        Intent intent = new Intent(this, clazz);

        if (clearTop)
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);

        startActivity(intent);
    }

    public static boolean notNull(Object anyObject) {
        return anyObject != null;
    }

    public void hideKeyboard(boolean isHide) {
        View rootView = getWindow().getDecorView().getRootView();

        InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        if (isHide)
            imm.hideSoftInputFromWindow(rootView.getApplicationWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
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
