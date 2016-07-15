package com.kingwaytek.cpami.bykingTablet.app.ui.track;

import android.content.Intent;
import android.content.pm.PackageManager;
import android.support.annotation.NonNull;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ListView;

import com.kingwaytek.cpami.bykingTablet.R;
import com.kingwaytek.cpami.bykingTablet.app.ui.BaseActivity;
import com.kingwaytek.cpami.bykingTablet.utilities.MenuHelper;
import com.kingwaytek.cpami.bykingTablet.utilities.PermissionCheckHelper;
import com.kingwaytek.cpami.bykingTablet.utilities.Util;
import com.kingwaytek.cpami.bykingTablet.utilities.Utility;

/**
 * 軌跡錄製清單
 *
 * @author Vincent (2016/7/14)
 */
public class UiTrackListActivity extends BaseActivity {

    private ListView trackListView;

    @Override
    protected void init() {
        checkPermissionAndCreateFolder();
    }

    @Override
    protected String getActionBarTitle() {
        return getString(R.string.menu_bike_track);
    }

    @Override
    protected int getLayoutId() {
        return R.layout.activity_track_list;
    }

    @Override
    protected void findViews() {
        trackListView = (ListView) findViewById(R.id.trackListView);
    }

    @Override
    protected void setListener() {

    }

    private void checkPermissionAndCreateFolder() {
        if (PermissionCheckHelper.checkFileStoragePermissions(this, PermissionCheckHelper.PERMISSION_REQUEST_CODE_STORAGE)) {
            Util.createTrackFolder();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuHelper.setMenuOptionsByMenuAction(menu, ACTION_ADD);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        super.onOptionsItemSelected(item);
        switch (item.getItemId()) {
            case ACTION_ADD:
                Intent intent = new Intent(UiTrackListActivity.this, UiTrackMapActivity.class);
                intent.putExtra(BUNDLE_ENTRY_TYPE, ENTRY_TYPE_TRACKING);
                startActivity(intent);
                break;
        }

        return true;
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String permissions[], @NonNull int[] grantResults) {
        switch (requestCode) {
            case PermissionCheckHelper.PERMISSION_REQUEST_CODE_STORAGE:
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED
                        && grantResults[1] == PackageManager.PERMISSION_GRANTED)
                {
                    Util.createTrackFolder();
                }
                else {
                    Utility.toastShort(getString(R.string.storage_permission_denied));
                    finish();
                }
                break;
        }
    }
}
