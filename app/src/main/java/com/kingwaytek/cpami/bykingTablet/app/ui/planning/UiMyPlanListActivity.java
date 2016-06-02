package com.kingwaytek.cpami.bykingTablet.app.ui.planning;

import android.view.Menu;
import android.view.MenuItem;

import com.kingwaytek.cpami.bykingTablet.R;
import com.kingwaytek.cpami.bykingTablet.app.ui.BaseActivity;
import com.kingwaytek.cpami.bykingTablet.utilities.MenuHelper;

/**
 * Created by vincent.chang on 2016/6/1.
 */
public class UiMyPlanListActivity extends BaseActivity {



    @Override
    protected void init() {

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
}
