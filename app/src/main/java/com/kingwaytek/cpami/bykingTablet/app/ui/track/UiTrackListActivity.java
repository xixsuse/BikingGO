package com.kingwaytek.cpami.bykingTablet.app.ui.track;

import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.kingwaytek.cpami.bykingTablet.R;
import com.kingwaytek.cpami.bykingTablet.app.model.items.ItemsTrackRecord;
import com.kingwaytek.cpami.bykingTablet.app.service.TrackingService;
import com.kingwaytek.cpami.bykingTablet.app.ui.BaseActivity;
import com.kingwaytek.cpami.bykingTablet.utilities.DialogHelper;
import com.kingwaytek.cpami.bykingTablet.utilities.FavoriteHelper;
import com.kingwaytek.cpami.bykingTablet.utilities.JsonParser;
import com.kingwaytek.cpami.bykingTablet.utilities.MenuHelper;
import com.kingwaytek.cpami.bykingTablet.utilities.PermissionCheckHelper;
import com.kingwaytek.cpami.bykingTablet.utilities.TrackingFileUtil;
import com.kingwaytek.cpami.bykingTablet.utilities.Utility;
import com.kingwaytek.cpami.bykingTablet.utilities.adapter.TrackListAdapter;

import java.util.ArrayList;

/**
 * 軌跡錄製清單
 *
 * @author Vincent (2016/7/14)
 */
public class UiTrackListActivity extends BaseActivity {

    private Menu menu;

    private boolean hasGoodPermission;
    private ListView trackListView;
    private TrackListAdapter trackListAdapter;
    private FloatingActionButton floatingBtn_addTrack;

    @Override
    protected void init() {

    }

    @Override
    public void onResume() {
        super.onResume();
        checkStoragePermission();
        createFolderAndSetListView();
        checkTrackingStatusAndSetButton();
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
        floatingBtn_addTrack = (FloatingActionButton) findViewById(R.id.floatingBtn_addTrack);
    }

    @Override
    protected void setListener() {
        floatingBtn_addTrack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(UiTrackListActivity.this, UiTrackMapActivity.class);
                intent.putExtra(BUNDLE_ENTRY_TYPE, ENTRY_TYPE_TRACKING);
                startActivity(intent);
            }
        });

        trackListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                // 最後一行是 footer view，它不能有點擊事件！
                if (position != parent.getCount() - 1) {
                    Intent intent = new Intent(UiTrackListActivity.this, UiTrackMapActivity.class);
                    intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    intent.putExtra(BUNDLE_ENTRY_TYPE, ENTRY_TYPE_TRACK_VIEWING);
                    intent.putExtra(BUNDLE_TRACK_INDEX, position);

                    startActivity(intent);
                }
            }
        });

        trackListView.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
                // 最後一行是 footer view，它不能有點擊事件！
                if (position != parent.getCount() - 1) {
                    trackListAdapter.showCheckBox(true);
                    trackListAdapter.setBoxChecked(position);
                    MenuHelper.setMenuOptionsByMenuAction(menu, ACTION_DELETE);
                    return true;
                }
                return false;
            }
        });
    }

    private void checkStoragePermission() {
        hasGoodPermission = PermissionCheckHelper.checkFileStoragePermissions(this);
    }

    private void createFolderAndSetListView() {
        if (hasGoodPermission) {
            TrackingFileUtil.createTrackFolder();

            ArrayList<ItemsTrackRecord> trackList = JsonParser.getTrackList();

            if (notNull(trackList)) {
                if (trackListAdapter == null) {
                    trackListAdapter = new TrackListAdapter(this, trackList);
                    trackListView.setAdapter(trackListAdapter);

                    // Add an empty footer view, to prevent the final row of ListView get blocked by FloatingButton.
                    View view = LayoutInflater.from(this).inflate(R.layout.inflate_empty_footer_view, null);
                    trackListView.addFooterView(view);
                }
                else
                    trackListAdapter.refreshList(trackList);
            }
        }
    }

    private void checkTrackingStatusAndSetButton() {
        if (TrackingService.IS_TRACKING_REQUESTED)
            floatingBtn_addTrack.setImageResource(R.drawable.ic_button_return);
        else
            floatingBtn_addTrack.setImageResource(R.drawable.ic_button_add);
    }

    private void showTrackMenuDialog() {
        View view = DialogHelper.getTrackMenuDialogView(this);
        TextView trackDownload = (TextView) view.findViewById(R.id.trackMenu_download);
        LinearLayout trackUpload = (LinearLayout) view.findViewById(R.id.trackMenu_upload);
        LinearLayout trackDelete = (LinearLayout) view.findViewById(R.id.trackMenu_delete);

        trackDownload.setOnClickListener(getTrackMenuClick(trackDownload.getId()));

        if (trackListAdapter == null || trackListAdapter.isEmpty()) {
            trackUpload.setVisibility(View.GONE);
            trackDelete.setVisibility(View.GONE);
        }
        else {
            trackUpload.setOnClickListener(getTrackMenuClick(trackUpload.getId()));
            trackDelete.setOnClickListener(getTrackMenuClick(trackDelete.getId()));
        }
    }

    private View.OnClickListener getTrackMenuClick(final int id) {
        return new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                switch (id) {
                    case R.id.trackMenu_download:
                        DialogHelper.dismissDialog();
                        break;

                    case R.id.trackMenu_upload:
                        DialogHelper.dismissDialog();
                        break;

                    case R.id.trackMenu_delete:
                        if (notNull(trackListAdapter)) {
                            trackListAdapter.showCheckBox(true);
                            trackListAdapter.notifyDataSetChanged();
                        }
                        MenuHelper.setMenuOptionsByMenuAction(menu, ACTION_DELETE);

                        DialogHelper.dismissDialog();
                        break;
                }
            }
        };
    }

    private void deleteSelectedTrack() {
        final ArrayList<Integer> indexList = trackListAdapter.getCheckedList();

        if (!indexList.isEmpty()) {
            DialogHelper.showDeleteConfirmDialog(this, new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    FavoriteHelper.removeMultiTrack(indexList);
                    createFolderAndSetListView();
                    unCheckBoxAndResumeMenu();
                }
            });
        }
    }

    private void unCheckBoxAndResumeMenu() {
        trackListAdapter.unCheckAllBox();
        MenuHelper.setMenuOptionsByMenuAction(menu, ACTION_MORE);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        this.menu = menu;
        MenuHelper.setMenuOptionsByMenuAction(menu, ACTION_MORE);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                if (notNull(trackListAdapter) && trackListAdapter.isCheckBoxShowing())
                    unCheckBoxAndResumeMenu();
                else
                    super.onOptionsItemSelected(item);
                break;

            case ACTION_MORE:
                showTrackMenuDialog();
                break;

            case ACTION_DELETE:
                deleteSelectedTrack();
                break;
        }
        return true;
    }

    @Override
    public void onBackPressed() {
        if (notNull(trackListAdapter) && trackListAdapter.isCheckBoxShowing())
            unCheckBoxAndResumeMenu();
        else
            super.onBackPressed();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String permissions[], @NonNull int[] grantResults) {
        switch (requestCode) {
            case PermissionCheckHelper.PERMISSION_REQUEST_CODE_STORAGE:
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED
                        && grantResults[1] == PackageManager.PERMISSION_GRANTED)
                {
                    hasGoodPermission = true;
                    createFolderAndSetListView();
                }
                else {
                    hasGoodPermission = false;
                    Utility.toastShort(getString(R.string.storage_permission_denied));
                    finish();
                }
                break;
        }
    }
}
