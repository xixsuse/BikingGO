package com.kingwaytek.cpami.bykingTablet.app.ui.planning;

import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.support.annotation.NonNull;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;

import com.kingwaytek.cpami.bykingTablet.R;
import com.kingwaytek.cpami.bykingTablet.app.model.DataArray;
import com.kingwaytek.cpami.bykingTablet.app.ui.BaseActivity;
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
    }

    @Override
    protected void setListener() {
        planListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent intent = new Intent(UiMyPlanListActivity.this, UiMyPlanInfoActivity.class);
                intent.putExtra(BUNDLE_PLAN_EDIT_INDEX, position);
                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(intent);
            }
        });

        planListView.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, final int position, long id) {
                planAdapter.showCheckBox(true);
                planAdapter.setBoxChecked(position);
                setMenuOption(ACTION_DELETE);

                return true;
            }
        });
    }

    private void setMenuOption(int action) {
        MenuHelper.setMenuOptionsByMenuAction(menu, action);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        this.menu = menu;
        MenuHelper.setMenuOptionsByMenuAction(menu, ACTION_ADD);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                if (notNull(planAdapter) && planAdapter.isCheckBoxShowing())
                    unCheckBoxAndResumeMenu();
                else
                    super.onOptionsItemSelected(item);
                break;

            case ACTION_ADD:
                goTo(UiMyPlanEditActivity.class, true);
                break;

            case ACTION_DELETE:
                deleteSelectedItems();
                break;
        }

        return true;
    }

    private void setPlanList() {
        if (PermissionCheckHelper.checkFileStoragePermissions(this, PermissionCheckHelper.PERMISSION_REQUEST_CODE_STORAGE)) {
            ArrayList<String> planNameList = DataArray.getPlanNameList();

            if (notNull(planNameList)) {
                if (planAdapter == null) {
                    planAdapter = new PlanListAdapter(this, planNameList);
                    planListView.setAdapter(planAdapter);
                }
                else
                    planAdapter.refreshList(planNameList);
            }
        }
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
        setMenuOption(ACTION_ADD);
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
        else
            super.onBackPressed();
    }
}