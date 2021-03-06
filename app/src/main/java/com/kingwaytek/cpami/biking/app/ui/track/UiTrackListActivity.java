package com.kingwaytek.cpami.biking.app.ui.track;

import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.kingwaytek.cpami.biking.R;
import com.kingwaytek.cpami.biking.app.model.DataArray;
import com.kingwaytek.cpami.biking.app.model.items.ItemsTrackRecord;
import com.kingwaytek.cpami.biking.app.service.TrackingService;
import com.kingwaytek.cpami.biking.app.ui.BaseActivity;
import com.kingwaytek.cpami.biking.app.ui.planning.UiSharedListActivity;
import com.kingwaytek.cpami.biking.app.web.WebAgent;
import com.kingwaytek.cpami.biking.utilities.DialogHelper;
import com.kingwaytek.cpami.biking.utilities.FavoriteHelper;
import com.kingwaytek.cpami.biking.utilities.JsonParser;
import com.kingwaytek.cpami.biking.utilities.MenuHelper;
import com.kingwaytek.cpami.biking.utilities.PermissionCheckHelper;
import com.kingwaytek.cpami.biking.utilities.TrackingFileUtil;
import com.kingwaytek.cpami.biking.utilities.Utility;
import com.kingwaytek.cpami.biking.utilities.adapter.TrackListAdapter;

import java.util.ArrayList;
import java.util.Collections;

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
        //FavoriteHelper.hardModifyTrackInfo();
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
                if (position != parent.getCount() - 1 && !trackListAdapter.isUploadMode()) {
                    Intent intent = new Intent(UiTrackListActivity.this, UiTrackMapActivity.class);
                    intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    intent.putExtra(BUNDLE_ENTRY_TYPE, ENTRY_TYPE_TRACK_VIEWING);
                    // Reverse the order of position, because the list has already reversed.
                    position = parent.getCount() - (position + 2);
                    intent.putExtra(BUNDLE_TRACK_INDEX, position);

                    startActivity(intent);
                }
            }
        });

        trackListView.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
                // 最後一行是 footer view，它不能有點擊事件！
                if (position != parent.getCount() - 1 && !trackListAdapter.isUploadMode()) {
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
                Collections.reverse(trackList); // Reverse the items order of trackList.

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
        View view = DialogHelper.getListMenuDialogView(this, false);
        final TextView trackBrowse = (TextView) view.findViewById(R.id.trackMenu_browse);
        final LinearLayout trackUpload = (LinearLayout) view.findViewById(R.id.trackMenu_upload);
        final LinearLayout trackDelete = (LinearLayout) view.findViewById(R.id.trackMenu_delete);

        trackBrowse.setTag(trackBrowse.getId());
        trackUpload.setTag(trackUpload.getId());
        trackDelete.setTag(trackDelete.getId());

        View.OnClickListener menuClick = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                switch ((int)v.getTag()) {
                    case R.id.trackMenu_browse:
                        goToSharedList();
                        break;

                    case R.id.trackMenu_upload:
                        setUploadMode(true);
                        break;

                    case R.id.trackMenu_delete:
                        if (notNull(trackListAdapter)) {
                            trackListAdapter.showCheckBox(true);
                            trackListAdapter.notifyDataSetChanged();
                        }
                        MenuHelper.setMenuOptionsByMenuAction(menu, ACTION_DELETE);
                        break;
                }
                DialogHelper.dismissDialog();
                trackBrowse.setOnClickListener(null);
                trackUpload.setOnClickListener(null);
                trackDelete.setOnClickListener(null);
            }
        };

        trackBrowse.setOnClickListener(menuClick);

        if (trackListAdapter == null || trackListAdapter.isEmpty()) {
            trackUpload.setVisibility(View.GONE);
            trackDelete.setVisibility(View.GONE);
        }
        else {
            trackUpload.setOnClickListener(menuClick);
            trackDelete.setOnClickListener(menuClick);
        }
    }

    private void goToSharedList() {
        Intent intent = new Intent(this, UiSharedListActivity.class);

        intent.putExtra(BUNDLE_ENTRY_TYPE, ENTRY_TYPE_VIEW_SHARED_PLAN);
        intent.putExtra(BUNDLE_SHARED_LIST_TYPE, SHARED_LIST_TYPE_TRACK);

        startActivity(intent);
    }

    private void setUploadMode(boolean isUploadMode) {
        trackListAdapter.setUploadMode(isUploadMode);
        floatingBtn_addTrack.setVisibility(isUploadMode ? View.GONE : View.VISIBLE);

        if (isUploadMode) {
            menu.clear();
            Utility.toastLong(getString(R.string.track_chose_a_track_to_upload));
        }
        else
            MenuHelper.setMenuOptionsByMenuAction(menu, ACTION_MORE);
    }

    public void uploadTrack(final String trackName, int trackIndex) {
        if (notNull(trackListAdapter)) {
            final int index = trackListAdapter.getCount() - (trackIndex + 1);

            DialogHelper.showUploadConfirmDialog(this, trackName, new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    dialog.dismiss();

                    final String trackContent = DataArray.getTrackObjectString(index);

                    if (notNull(trackContent)) {
                        DialogHelper.showLoadingDialog(UiTrackListActivity.this);

                        WebAgent.uploadDataToBikingService(POST_VALUE_TYPE_TRACK, trackName, trackContent, new WebAgent.WebResultImplement() {
                            @Override
                            public void onResultSucceed(String response) {
                                Utility.toastShort(getString(R.string.upload_done));
                                DialogHelper.dismissDialog();
                            }

                            @Override
                            public void onResultFail(String errorMessage) {
                                Utility.toastLong(errorMessage);
                                DialogHelper.dismissDialog();
                            }
                        });
                    }
                }
            });
        }
    }

    private void deleteSelectedTrack() {
        final ArrayList<Integer> indexList = trackListAdapter.getCheckedList();
        Log.i(TAG, indexList.toString());
        reverseList(indexList);
        Log.i(TAG, indexList.toString());

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

    private void reverseList(ArrayList<Integer> indexList) {
        if (notNull(trackListAdapter)) {
            for (int i = 0; i < indexList.size(); i++) {
                indexList.set(i, trackListAdapter.getCount() - (indexList.get(i) + 1));
            }
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
                else if (notNull(trackListAdapter) && trackListAdapter.isUploadMode())
                    setUploadMode(false);
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
        else if (notNull(trackListAdapter) && trackListAdapter.isUploadMode())
            setUploadMode(false);
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
