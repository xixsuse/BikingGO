package com.kingwaytek.cpami.bykingTablet.app.ui.planning;

import android.content.Intent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;

import com.kingwaytek.cpami.bykingTablet.R;
import com.kingwaytek.cpami.bykingTablet.app.model.DataArray;
import com.kingwaytek.cpami.bykingTablet.app.model.items.ItemsPlans;
import com.kingwaytek.cpami.bykingTablet.app.ui.BaseActivity;
import com.kingwaytek.cpami.bykingTablet.utilities.MenuHelper;
import com.kingwaytek.cpami.bykingTablet.utilities.adapter.PlanInfoListAdapter;

/**
 * Created by vincent.chang on 2016/6/4.
 */
public class UiMyPlanInfoActivity extends BaseActivity {

    private TextView planName;
    private ListView planListView;
    private Button btn_startPlan;

    private int PLAN_EDIT_INDEX;

    @Override
    protected void init() {

    }

    @Override
    public void onResume() {
        super.onResume();
        getIndexAndItems();
    }

    @Override
    protected String getActionBarTitle() {
        return getString(R.string.menu_planning);
    }

    @Override
    protected int getLayoutId() {
        return R.layout.activity_my_plan_info;
    }

    @Override
    protected void findViews() {
        planName = (TextView) findViewById(R.id.text_planName);
        planListView = (ListView) findViewById(R.id.planInfoListView);
        btn_startPlan = (Button) findViewById(R.id.button_startPlanning);
    }

    @Override
    protected void setListener() {
        btn_startPlan.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });
    }

    private void getIndexAndItems() {
        Intent intent = getIntent();

        if (intent.hasExtra(BUNDLE_PLAN_EDIT_INDEX)) {
            PLAN_EDIT_INDEX = intent.getIntExtra(BUNDLE_PLAN_EDIT_INDEX, 0);

            ItemsPlans planAndItems = DataArray.getPlansData().get(PLAN_EDIT_INDEX);

            if (notNull(planAndItems))
                setPlanContent(planAndItems);
        }
    }

    private void setPlanContent(ItemsPlans planAndItems) {
        planName.setText(planAndItems.NAME);
        planListView.setAdapter(new PlanInfoListAdapter(this, planAndItems.PLAN_ITEMS));
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuHelper.setMenuOptionsByMenuAction(menu, ACTION_EDIT);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        super.onOptionsItemSelected(item);

        switch (item.getItemId()) {
            case ACTION_EDIT:
                Intent intent = new Intent(UiMyPlanInfoActivity.this, UiMyPlanEditActivity.class);
                intent.putExtra(BUNDLE_PLAN_EDIT_INDEX, PLAN_EDIT_INDEX);
                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(intent);
                break;
        }

        return true;
    }


}
