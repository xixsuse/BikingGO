package com.kingwaytek.cpami.bykingTablet.app.ui.planning;

import android.view.Menu;
import android.view.MenuItem;
import android.widget.ListView;

import com.kingwaytek.cpami.bykingTablet.R;
import com.kingwaytek.cpami.bykingTablet.app.model.DataArray;
import com.kingwaytek.cpami.bykingTablet.app.ui.BaseActivity;
import com.kingwaytek.cpami.bykingTablet.utilities.MenuHelper;
import com.kingwaytek.cpami.bykingTablet.utilities.adapter.PlanListAdapter;

import java.util.ArrayList;

/**
 * Created by vincent.chang on 2016/6/1.
 */
public class UiMyPlanListActivity extends BaseActivity {

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
                goTo(UiMyPlanEditActivity.class, true);
                break;
        }

        return true;
    }

    private void setPlanList() {
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
