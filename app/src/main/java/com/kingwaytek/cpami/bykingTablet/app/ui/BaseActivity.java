package com.kingwaytek.cpami.bykingTablet.app.ui;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.NavigationView;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.ImageButton;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.kingwaytek.cpami.bykingTablet.R;
import com.kingwaytek.cpami.bykingTablet.app.MainActivity;
import com.kingwaytek.cpami.bykingTablet.app.model.ActionbarMenu;
import com.kingwaytek.cpami.bykingTablet.app.ui.poi.UiMyPoiListActivity;
import com.kingwaytek.cpami.bykingTablet.app.web.WebAgent;
import com.kingwaytek.cpami.bykingTablet.utilities.Utility;

import java.io.IOException;
import java.io.InputStream;

/**
 * 這裡有 Actionbar and 各種基本會呼叫到的方法，<br>
 * 每個 Activity都應該繼承這裡！
 *
 * @author Vincent (2016/04/18)
 */
public abstract class BaseActivity extends AppCompatActivity implements ActionbarMenu {

    protected final String TAG = getClass().getSimpleName();

    protected abstract void init();
    protected abstract String getActionBarTitle();
    protected abstract int getLayoutId();
    protected abstract void findViews();
    protected abstract void setListener();

    private ActionBar actionbar;
    private static View actionbarView;

    protected DrawerLayout drawer;
    protected ActionBarDrawerToggle drawerToggle;
    protected NavigationView drawerView;

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
            actionbar.setDisplayHomeAsUpEnabled(true);  // 會有內建的 back arrow button，可搭配 Drawer使用!
            //actionbar.setHomeButtonEnabled(true);     // 會出現自訂的 back button，無法搭配 Drawer使用
            //actionbar.setDisplayShowTitleEnabled(true);
            actionbar.setDisplayOptions(ActionBar.DISPLAY_SHOW_CUSTOM | ActionBar.DISPLAY_HOME_AS_UP);

            //setActionbarIcon();

            actionbar.setCustomView(R.layout.include_foolish_action_bar);
            Toolbar toolbar = (Toolbar) actionbar.getCustomView().getParent();
            toolbar.setContentInsetsAbsolute(0, 0);

            TextView title = (TextView) actionbar.getCustomView().findViewById(R.id.actionBar_title);
            title.setText(getActionBarTitle());

            actionbarView = actionbar.getCustomView();
            showActionbar(true);
        }
    }

    private void setActionbarIcon() {
        if (getLayoutId() == R.layout.activity_base_map)
            actionbar.setIcon(R.drawable.selector_toolbar_list);
        else
            actionbar.setIcon(R.drawable.selector_toolbar_back_arrow);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        if (getLayoutId() == R.layout.activity_base_map) {
            MenuInflater menuInflater = getMenuInflater();
            menuInflater.inflate(R.menu.options_menu, menu);
        }
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                if (getLayoutId() == R.layout.activity_base_map) {
                    if (drawer.isDrawerOpen(GravityCompat.START))
                        drawer.closeDrawers();
                    else
                        drawer.openDrawer(GravityCompat.START);
                }
                else
                    finish();

                break;
        }
        return true;
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
        //actionbar_menuBtn = (ImageButton) actionbarView.findViewById(R.id.actionBar_menuButton);
        //actionbar_aroundBtn = (ImageButton) actionbarView.findViewById(R.id.actionBar_aroundButton);
        //actionbar_switchBtn = (ImageButton) actionbarView.findViewById(R.id.actionBar_switchButton);
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

    protected void initDrawer() {
        drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawerView = (NavigationView) findViewById(R.id.navigation_view);

        setDrawerWidth();

        drawerToggle = new ActionBarDrawerToggle(this, drawer, R.string.drawer_open, R.string.drawer_close) {
            @Override
            public void onDrawerOpened(View drawerView) {
                super.onDrawerOpened(drawerView);
                actionbar.setIcon(R.drawable.selector_toolbar_back_arrow);
            }

            @Override
            public void onDrawerClosed(View drawerView) {
                super.onDrawerClosed(drawerView);
                actionbar.setIcon(R.drawable.selector_toolbar_list);
            }
        };
        drawer.addDrawerListener(drawerToggle);
        drawerToggle.syncState();

        drawerView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(MenuItem item) {
                item.setChecked(true);
                onMenuItemClick(item);
                drawer.closeDrawers();

                return true;
            }
        });
    }

    protected void setDrawerWidth() {
        int width = (int) (Utility.getScreenWidth() / 1.8);

        DrawerLayout.LayoutParams params = (DrawerLayout.LayoutParams) drawerView.getLayoutParams();
        params.width = width;

        drawerView.setLayoutParams(params);
    }

    protected void unCheckAllMenuItem() {
        for (int i = 0; i < drawerView.getMenu().size(); i++) {
            drawerView.getMenu().getItem(i).setChecked(false);
        }
    }

    private void onMenuItemClick(MenuItem menuItem) {
        switch (menuItem.getItemId()) {
            case R.id.menu_home:
                goTo(MainActivity.class, false);
                break;

            case R.id.menu_bike_track:

                break;

            case R.id.menu_my_poi:
                goTo(UiMyPoiListActivity.class, false);
                break;

            case R.id.menu_poi_book:

                break;

            case R.id.menu_report:

                break;

            case R.id.menu_favorite:

                break;

            case R.id.menu_settings:

                break;
        }
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
