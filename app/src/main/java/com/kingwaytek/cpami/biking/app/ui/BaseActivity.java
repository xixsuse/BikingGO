package com.kingwaytek.cpami.biking.app.ui;

import android.app.Activity;
import android.app.ActivityManager;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.design.widget.NavigationView;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.util.LruCache;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.kingwaytek.cpami.biking.AppController;
import com.kingwaytek.cpami.biking.R;
import com.kingwaytek.cpami.biking.app.model.ActionbarMenu;
import com.kingwaytek.cpami.biking.app.model.CommonBundle;
import com.kingwaytek.cpami.biking.app.service.TrackingService;
import com.kingwaytek.cpami.biking.app.ui.events.UiEventListActivity;
import com.kingwaytek.cpami.biking.app.ui.planning.UiMyPlanListActivity;
import com.kingwaytek.cpami.biking.app.ui.poi.UiMyPoiListActivity;
import com.kingwaytek.cpami.biking.app.ui.poi_book.UiPoiCityListActivity;
import com.kingwaytek.cpami.biking.app.ui.report.UiReportActivity;
import com.kingwaytek.cpami.biking.app.ui.settings.UiAboutActivity;
import com.kingwaytek.cpami.biking.app.ui.track.UiTrackListActivity;
import com.kingwaytek.cpami.biking.app.web.WebAgent;
import com.kingwaytek.cpami.biking.utilities.BitmapUtility;
import com.kingwaytek.cpami.biking.utilities.MenuHelper;
import com.kingwaytek.cpami.biking.utilities.NotifyHelper;
import com.kingwaytek.cpami.biking.utilities.PermissionCheckHelper;
import com.kingwaytek.cpami.biking.utilities.PopWindowHelper;
import com.kingwaytek.cpami.biking.utilities.Utility;

import java.io.IOException;
import java.io.InputStream;

/**
 * 這裡有 Actionbar and 各種基本會呼叫到的方法，<br>
 * 每個 Activity都應該繼承這裡！
 *
 * @author Vincent (2016/04/18)
 */
public abstract class BaseActivity extends AppCompatActivity implements ActionbarMenu, CommonBundle {

    protected final String TAG = getClass().getSimpleName();

    protected abstract void init();
    protected abstract String getActionBarTitle();
    protected abstract int getLayoutId();
    protected abstract void findViews();
    protected abstract void setListener();

    protected int ENTRY_TYPE;

    private ActionBar actionbar;

    protected DrawerLayout drawer;
    protected ActionBarDrawerToggle drawerToggle;
    protected NavigationView drawerView;

    private ProgressBar loadingCircle;
    private TextView trackingText;

    private final int maxMemory = (int) (Runtime.getRuntime().maxMemory() / 1024);
    private final int bitmapCacheSize = maxMemory / 8;
    protected LruCache<String, Bitmap> bitmapCache;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setTheme(R.style.AppTheme);
        //setWindowFeatures();
        setContentView(getLayoutId());

        initLruCache();

        getEntryType();

        setActionBar();
        findActionbarWidgets();

        findViews();
        setListener();

        init();
    }

    private void setWindowFeatures() {
        if (Build.VERSION.SDK_INT > Build.VERSION_CODES.KITKAT) {
            Window window = getWindow();
            window.setFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_NAVIGATION,
                    WindowManager.LayoutParams.FLAG_TRANSLUCENT_NAVIGATION);
        }
    }

    private void initLruCache() {
        bitmapCache = new LruCache<String, Bitmap>(bitmapCacheSize) {
            @Override
            protected int sizeOf(String key, Bitmap bitmap) {
                // The cache size will be measured in kilobytes rather than number of items.
                return bitmap.getByteCount() / 1024;
            }
        };
    }

    public void addBitmapToMemoryCache(String key, Bitmap bitmap) {
        if (getBitmapFromMemCache(key) == null)
            bitmapCache.put(key, bitmap);
    }

    public Bitmap getBitmapFromMemCache(String key) {
        return bitmapCache.get(key);
    }

    public void checkBitmapCache(String bitmapKey) {
        if (getBitmapFromMemCache(bitmapKey) == null) {
            int iconId;
            switch (bitmapKey) {
                case BITMAP_KEY_SUPPLY_STATION:
                    iconId = R.drawable.ic_marker_supply_station;
                    break;

                case BITMAP_KEY_BIKE_RENT_STATION:
                    iconId = R.drawable.ic_marker_bike_rent_station;
                    break;

                case BITMAP_KEY_YOU_BIKE_NORMAL:
                    iconId = R.drawable.ic_marker_you_bike_normal;
                    break;

                case BITMAP_KEY_YOU_BIKE_FULL:
                    iconId = R.drawable.ic_marker_you_bike_full;
                    break;

                case BITMAP_KEY_YOU_BIKE_EMPTY:
                    iconId = R.drawable.ic_marker_you_bike_empty;
                    break;

                case BITMAP_KEY_YOU_BIKE_OUT_OF_SERVICE:
                    iconId = R.drawable.ic_marker_you_bike_unavailable;
                    break;

                default:
                    iconId = R.drawable.ic_marker_start;
                    break;
            }

            Drawable drawable = ContextCompat.getDrawable(getApplicationContext(), iconId);
            addBitmapToMemoryCache(bitmapKey, BitmapUtility.convertDrawableToBitmap(
                    drawable, getResources().getDimensionPixelSize(R.dimen.icon_marker_common_size)));

            Log.i(TAG, "BitmapAddedToLruCache: " + bitmapKey);
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        if (isTrackingServiceRunning()) {
            NotifyHelper.showServiceNotification();
            Log.i(TAG, "ServiceRunning, show notification!");
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        showTrackingText();
        NotifyHelper.clearServiceNotification();
        Log.i(TAG, "onResume!!!");
    }

    private void getEntryType() {
        ENTRY_TYPE = getIntent().getIntExtra(BUNDLE_ENTRY_TYPE, ENTRY_TYPE_DEFAULT);
        Log.i(TAG, "ENTRY_TYPE: " + ENTRY_TYPE);
    }

    private void setActionBar() {
        actionbar = getSupportActionBar();

        if (notNull(actionbar)) {
            int actionbarOptions = ActionBar.DISPLAY_SHOW_CUSTOM | ActionBar.DISPLAY_HOME_AS_UP;

            actionbar.setDisplayHomeAsUpEnabled(true);  // 會有內建的 back arrow button，可搭配 Drawer使用!
            //actionbar.setHomeButtonEnabled(true);     // 會出現自訂的 back button，無法搭配 Drawer使用
            //actionbar.setDisplayShowTitleEnabled(true);
            actionbar.setDisplayOptions(actionbarOptions);

            //setActionbarIcon();

            actionbar.setCustomView(R.layout.include_foolish_action_bar);
            Toolbar toolbar = (Toolbar) actionbar.getCustomView().getParent();
            toolbar.setContentInsetsAbsolute(0, 0);

            TextView title = (TextView) actionbar.getCustomView().findViewById(R.id.actionBar_title);
            title.setText(getActionBarTitle());

            showActionbar(true);
        }
    }

    private void setActionbarIcon() {
        if (getLayoutId() == R.layout.activity_main_map)
            actionbar.setIcon(R.drawable.selector_toolbar_list);
        else
            actionbar.setIcon(R.drawable.selector_toolbar_back_arrow);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        switch (ENTRY_TYPE) {
            case ENTRY_TYPE_DEFAULT:
                if (getLayoutId() == R.layout.activity_main_map) {
                    MenuInflater menuInflater = getMenuInflater();
                    menuInflater.inflate(R.menu.options_menu, menu);
                }
                break;

            case ENTRY_TYPE_LOCATION_SELECT:
                MenuHelper.setMenuOptionsByMenuAction(menu, ACTION_AROUND);
                break;

            case ENTRY_TYPE_DIRECTIONS:
                MenuHelper.setMenuOptionsByMenuAction(menu, ACTION_LIST);
                break;
        }

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                if (getLayoutId() == R.layout.activity_main_map && ENTRY_TYPE == ENTRY_TYPE_DEFAULT) {
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

    public void showActionbar(boolean isShow) {
        if (isShow)
            actionbar.show();
        else
            actionbar.hide();
    }

    private void findActionbarWidgets() {
        loadingCircle = (ProgressBar) actionbar.getCustomView().findViewById(R.id.loadingCircle);
        trackingText = (TextView) actionbar.getCustomView().findViewById(R.id.text_tracking);
    }

    protected void showLoadingCircle(boolean isShow) {
        if (isShow)
            loadingCircle.setVisibility(View.VISIBLE);
        else
            loadingCircle.setVisibility(View.INVISIBLE);
    }

    private void showTrackingText() {
        if (notNull(AppController.getInstance().getTrackManager()) && AppController.getInstance().getTrackManager().isTrackingRightNow())
            trackingText.setVisibility(View.VISIBLE);
        else
            trackingText.setVisibility(View.GONE);
    }

    protected void showTrackingText(boolean isShow) {
        if (isShow)
            trackingText.setVisibility(View.VISIBLE);
        else
            trackingText.setVisibility(View.GONE);
    }

    protected void initDrawer() {
        drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawerView = (NavigationView) findViewById(R.id.navigation_view);

        setDrawerWidth();

        drawerToggle = new ActionBarDrawerToggle(this, drawer, R.string.drawer_open, R.string.drawer_close) {
            @Override
            public void onDrawerOpened(View drawerView) {
                super.onDrawerOpened(drawerView);
            }

            @Override
            public void onDrawerClosed(View drawerView) {
                super.onDrawerClosed(drawerView);
            }
        };

        if (ENTRY_TYPE == ENTRY_TYPE_DEFAULT) {
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
        // 如果 ENTRY_TYPE不是DEFAULT的話，就不能有menu出現！
        else {
            drawer.setDrawerLockMode(DrawerLayout.LOCK_MODE_LOCKED_CLOSED);
            drawerToggle.setDrawerIndicatorEnabled(false);
        }
    }

    protected void setDrawerWidth() {
        int width = (int) (Utility.getScreenWidth() / 1.4);

        DrawerLayout.LayoutParams params = (DrawerLayout.LayoutParams) drawerView.getLayoutParams();
        params.width = width;

        drawerView.setLayoutParams(params);
    }

    protected void unCheckAllMenuItem() {
        if (notNull(drawerView)) {
            for (int i = 0; i < drawerView.getMenu().size(); i++) {
                drawerView.getMenu().getItem(i).setChecked(false);
            }
            drawerView.getMenu().getItem(0).setChecked(true);
        }
    }

    private void onMenuItemClick(MenuItem menuItem) {
        switch (menuItem.getItemId()) {
            /*
            case R.id.menu_home:
                goTo(MainActivity.class, false);
                break;
            */
            case R.id.menu_my_poi:
                Intent intent = new Intent(this, UiMyPoiListActivity.class);
                startActivityForResult(intent, REQUEST_RELOAD_ALL_MARKER);
                break;

            case R.id.menu_planning:
                goTo(UiMyPlanListActivity.class, false);
                break;

            case R.id.menu_bike_track:
                goTo(UiTrackListActivity.class, false);
                break;

            case R.id.menu_poi_book:
                goTo(UiPoiCityListActivity.class, false);
                break;

            case R.id.menu_events:
                goTo(UiEventListActivity.class, false);
                break;

            case R.id.menu_report:
                goTo(UiReportActivity.class, false);
                break;

            case R.id.menu_about:
                goTo(UiAboutActivity.class, false);
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

    /** For Google directions */
    public String getAvoidOptions(String... avoidOptions) {
        StringBuilder sb = new StringBuilder();

        sb.append("&avoid=");

        for (int i = 0; i < avoidOptions.length; i++) {
            if (i != 0)
                sb.append("|");
            sb.append(avoidOptions[i]);
        }
        return sb.toString();
    }

    public Handler getUiHandler() {
        return new Handler();
    }

    public static void launchCamera(Activity activity) {
        if (PermissionCheckHelper.checkGalleryAndCameraPermissions(activity, PermissionCheckHelper.PERMISSION_REQUEST_CODE_CAMERA)) {
            Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
            activity.startActivityForResult(intent, REQUEST_PHOTO_FROM_CAMERA);
        }
    }

    public static void launchGallery(Activity activity) {
        if (PermissionCheckHelper.checkGalleryAndCameraPermissions(activity, PermissionCheckHelper.PERMISSION_REQUEST_CODE_GALLERY)) {
            Intent intent;

            if (Build.VERSION.SDK_INT < 19) {
                intent = new Intent();
                intent.setType("image/*");
                intent.setAction(Intent.ACTION_GET_CONTENT);
            }
            else {
                intent = new Intent(Intent.ACTION_OPEN_DOCUMENT);
                intent.addCategory(Intent.CATEGORY_OPENABLE);
                intent.setType("image/*");
            }
            activity.startActivityForResult(intent, REQUEST_PHOTO_FROM_GALLERY);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String permissions[], @NonNull int[] grantResults) {
        switch (requestCode) {
            case PermissionCheckHelper.PERMISSION_REQUEST_CODE_GALLERY:
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED
                        && grantResults[1] == PackageManager.PERMISSION_GRANTED)
                {
                    launchGallery(this);
                }
                else
                    Utility.toastShort(getString(R.string.camera_permission_denied));
                break;

            case PermissionCheckHelper.PERMISSION_REQUEST_CODE_CAMERA:
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED
                        && grantResults[1] == PackageManager.PERMISSION_GRANTED)
                {
                    launchCamera(this);
                }
                else
                    Utility.toastShort(getString(R.string.camera_permission_denied));
                break;
        }
    }

    protected boolean isTrackingServiceRunning() {
        ActivityManager am = (ActivityManager) getSystemService(Context.ACTIVITY_SERVICE);

        for (ActivityManager.RunningServiceInfo runningService : am.getRunningServices(Integer.MAX_VALUE)) {
            if (TrackingService.class.getName().equals(runningService.service.getClassName())) {
                Log.i(TAG, "ServiceRunning! Return true.");
                return true;
            }
        }
        return false;
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        Log.i(TAG, "onDestroy!!!");
        WebAgent.stopRetryThread();
        PopWindowHelper.dismissPopWindow();
    }
}
