package com.kingwaytek.cpami.bykingTablet.app.ui.planning;

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
import com.kingwaytek.cpami.bykingTablet.app.model.DataArray;
import com.kingwaytek.cpami.bykingTablet.app.model.items.ItemsPlanPreview;
import com.kingwaytek.cpami.bykingTablet.app.ui.BaseActivity;
import com.kingwaytek.cpami.bykingTablet.app.web.WebAgent;
import com.kingwaytek.cpami.bykingTablet.utilities.DialogHelper;
import com.kingwaytek.cpami.bykingTablet.utilities.FavoriteHelper;
import com.kingwaytek.cpami.bykingTablet.utilities.MenuHelper;
import com.kingwaytek.cpami.bykingTablet.utilities.PermissionCheckHelper;
import com.kingwaytek.cpami.bykingTablet.utilities.Utility;
import com.kingwaytek.cpami.bykingTablet.utilities.adapter.PlanListAdapter;

import java.util.ArrayList;

/**
 * 行程規劃清單頁面
 *
 * @author Vincent (2016/6/1)
 */
public class UiMyPlanListActivity extends BaseActivity {

    private Menu menu;

    private ListView planListView;
    private PlanListAdapter planAdapter;
    private FloatingActionButton floatingBtn_addPlan;

    @Override
    protected void init() {

    }

    @Override
    public void onResume() {
        super.onResume();
        setPlanList();
    }

    @Override
    protected String getActionBarTitle() {
        return getString(R.string.menu_planning);
    }

    @Override
    protected int getLayoutId() {
        return R.layout.activity_my_plan_list;
    }

    @Override
    protected void findViews() {
        planListView = (ListView) findViewById(R.id.myPlanListView);
        floatingBtn_addPlan = (FloatingActionButton) findViewById(R.id.floatingBtn_addPlan);
    }

    @Override
    protected void setListener() {
        planListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                if (position != parent.getCount() -1 && !planAdapter.isUploadMode()) {
                    Intent intent = new Intent(UiMyPlanListActivity.this, UiMyPlanInfoActivity.class);
                    intent.putExtra(BUNDLE_PLAN_NAME, planAdapter.getName(position));
                    intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    startActivity(intent);
                }
            }
        });

        planListView.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, final int position, long id) {
                if (position != parent.getCount() - 1 && !planAdapter.isUploadMode()) {
                    planAdapter.showCheckBox(true);
                    planAdapter.setBoxChecked(position);
                    setMenuOption(ACTION_DELETE);
                    return true;
                }
                return false;
            }
        });

        floatingBtn_addPlan.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                goTo(UiMyPlanEditActivity.class, true);
            }
        });
    }

    private void setMenuOption(int action) {
        MenuHelper.setMenuOptionsByMenuAction(menu, action);
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
                if (notNull(planAdapter) && planAdapter.isCheckBoxShowing())
                    unCheckBoxAndResumeMenu();
                else if (notNull(planAdapter) && planAdapter.isUploadMode())
                    setUploadMode(false);
                else
                    super.onOptionsItemSelected(item);
                break;

            case ACTION_MORE:
                showPlanMenuDialog();
                break;

            case ACTION_DELETE:
                deleteSelectedItems();
                break;
        }

        return true;
    }

    private void setPlanList() {
        if (PermissionCheckHelper.checkFileStoragePermissions(this)) {
            ArrayList<ItemsPlanPreview> planPreviewList = DataArray.getPlanPreviewItems();

            if (notNull(planPreviewList)) {
                if (planAdapter == null) {
                    planAdapter = new PlanListAdapter(this, planPreviewList);
                    planListView.setAdapter(planAdapter);

                    // Add an empty footer view, to prevent the final row of ListView get blocked by FloatingButton.
                    View view = LayoutInflater.from(this).inflate(R.layout.inflate_empty_footer_view, null);
                    planListView.addFooterView(view);
                }
                else
                    planAdapter.refreshList(planPreviewList);
            }
        }
    }

    private void showPlanMenuDialog() {
        View view = DialogHelper.getListMenuDialogView(this, true);
        final TextView planBrowse = (TextView) view.findViewById(R.id.planMenu_browse);
        final LinearLayout planUpload = (LinearLayout) view.findViewById(R.id.planMenu_upload);
        final LinearLayout planDelete = (LinearLayout) view.findViewById(R.id.planMenu_delete);

        planBrowse.setTag(planBrowse.getId());
        planUpload.setTag(planUpload.getId());
        planDelete.setTag(planDelete.getId());

        View.OnClickListener menuClick = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                switch ((int)v.getTag()) {
                    case R.id.planMenu_browse:
                        goToSharedList();
                        DialogHelper.dismissDialog();
                        break;

                    case R.id.planMenu_upload:
                        setUploadMode(true);
                        DialogHelper.dismissDialog();
                        break;

                    case R.id.planMenu_delete:
                        if (notNull(planAdapter)) {
                            planAdapter.showCheckBox(true);
                            planAdapter.notifyDataSetChanged();
                        }
                        setMenuOption(ACTION_DELETE);

                        DialogHelper.dismissDialog();
                        break;
                }
                planBrowse.setOnClickListener(null);
                planUpload.setOnClickListener(null);
                planDelete.setOnClickListener(null);
            }
        };

        planBrowse.setOnClickListener(menuClick);

        if (planAdapter == null || planAdapter.isEmpty()) {
            planUpload.setVisibility(View.GONE);
            planDelete.setVisibility(View.GONE);
        }
        else {
            planUpload.setOnClickListener(menuClick);
            planDelete.setOnClickListener(menuClick);
        }
    }

    private void goToSharedList() {
        Intent intent = new Intent(this, UiSharedListActivity.class);

        intent.putExtra(BUNDLE_ENTRY_TYPE, ENTRY_TYPE_VIEW_SHARED_PLAN);
        intent.putExtra(BUNDLE_SHARED_LIST_TYPE, SHARED_LIST_TYPE_PLAN);

        startActivity(intent);
    }

    private void setUploadMode(boolean isUploadMode) {
        planAdapter.setUploadMode(isUploadMode);
        floatingBtn_addPlan.setVisibility(isUploadMode ? View.GONE : View.VISIBLE);

        if (isUploadMode) {
            menu.clear();
            Utility.toastLong(getString(R.string.plan_chose_a_plan_to_upload));
        }
        else
            setMenuOption(ACTION_MORE);
    }

    public void uploadPlan(final String planName, final int planIndex) {
        DialogHelper.showUploadConfirmDialog(this, planName, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();

                final String planContent = DataArray.getPlanObjectString(planIndex);

                if (notNull(planContent)) {
                    DialogHelper.showLoadingDialog(UiMyPlanListActivity.this);

                    WebAgent.uploadDataToBikingService(POST_VALUE_TYPE_PLAN, planName, planContent, new WebAgent.WebResultImplement() {
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

    private void deleteSelectedItems() {
        final ArrayList<Integer> checkedList = planAdapter.getCheckedList();

        if (!checkedList.isEmpty()) {
            DialogHelper.showDeleteConfirmDialog(UiMyPlanListActivity.this, new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    FavoriteHelper.removeMultiPlan(checkedList);
                    setPlanList();
                    unCheckBoxAndResumeMenu();
                }
            });
        }
    }

    private void unCheckBoxAndResumeMenu() {
        planAdapter.unCheckAllBox();
        setMenuOption(ACTION_MORE);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String permissions[], @NonNull int[] grantResults) {
        switch (requestCode) {
            case PermissionCheckHelper.PERMISSION_REQUEST_CODE_STORAGE:
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED
                        && grantResults[1] == PackageManager.PERMISSION_GRANTED)
                {
                    setPlanList();
                }
                else {
                    Utility.toastShort(getString(R.string.storage_permission_denied));
                    finish();
                }
                break;
        }
    }

    @Override
    public void onBackPressed() {
        if (notNull(planAdapter) && planAdapter.isCheckBoxShowing())
            unCheckBoxAndResumeMenu();
        else if (notNull(planAdapter) && planAdapter.isUploadMode())
            setUploadMode(false);
        else
            super.onBackPressed();
    }
}