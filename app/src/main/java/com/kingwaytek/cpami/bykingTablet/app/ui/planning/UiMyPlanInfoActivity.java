package com.kingwaytek.cpami.bykingTablet.app.ui.planning;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;

import com.kingwaytek.cpami.bykingTablet.R;
import com.kingwaytek.cpami.bykingTablet.app.model.ApiUrls;
import com.kingwaytek.cpami.bykingTablet.app.model.DataArray;
import com.kingwaytek.cpami.bykingTablet.app.model.items.ItemsPlanItem;
import com.kingwaytek.cpami.bykingTablet.app.model.items.ItemsPlans;
import com.kingwaytek.cpami.bykingTablet.app.ui.BaseActivity;
import com.kingwaytek.cpami.bykingTablet.app.web.WebAgent;
import com.kingwaytek.cpami.bykingTablet.utilities.DialogHelper;
import com.kingwaytek.cpami.bykingTablet.utilities.FavoriteHelper;
import com.kingwaytek.cpami.bykingTablet.utilities.MenuHelper;
import com.kingwaytek.cpami.bykingTablet.utilities.Utility;
import com.kingwaytek.cpami.bykingTablet.utilities.adapter.PlanInfoListAdapter;

import java.text.MessageFormat;
import java.util.ArrayList;

/**
 * 行程規劃資訊頁...但沒什麼資訊可言，<br>
 * 這裡主要是可以接到 {@link UiMyPlanEditActivity} & Google Directions.
 *
 * @author Vincent (2016/6/4)
 */
public class UiMyPlanInfoActivity extends BaseActivity {

    private TextView planName;
    private ListView planListView;
    private Button btn_startPlan;

    private PlanInfoListAdapter infoListAdapter;

    private int PLAN_EDIT_INDEX;
    private String name;

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
                startDirection();
            }
        });
    }

    private void getIndexAndItems() {
        Intent intent = getIntent();

        if (intent.hasExtra(BUNDLE_PLAN_EDIT_INDEX)) {
            PLAN_EDIT_INDEX = intent.getIntExtra(BUNDLE_PLAN_EDIT_INDEX, 0);

            ItemsPlans planAndItems = DataArray.getPlansData().get(PLAN_EDIT_INDEX);

            if (notNull(planAndItems)) {
                name = planAndItems.NAME;
                setPlanContent(planAndItems);
            }
        }
    }

    private void setPlanContent(ItemsPlans planAndItems) {
        planName.setText(planAndItems.NAME);

        if (infoListAdapter == null) {
            infoListAdapter = new PlanInfoListAdapter(this, planAndItems.PLAN_ITEMS);
            planListView.setAdapter(infoListAdapter);
        }
        else
            infoListAdapter.refreshList(planAndItems.PLAN_ITEMS);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuHelper.setMenuOptionsByMenuAction(menu, ACTION_DELETE, ACTION_UPLOAD, ACTION_EDIT);
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

            case ACTION_UPLOAD:

                break;

            case ACTION_DELETE:
                DialogHelper.showDeleteConfirmDialog(this, name, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        FavoriteHelper.removePlan(PLAN_EDIT_INDEX);
                        Utility.toastShort(getString(R.string.plan_remove_completed));
                        finish();
                    }
                });

                break;
        }

        return true;
    }

    private boolean canDirection() {
        return infoListAdapter.getCount() > 1;
    }

    private boolean isContainWaypoints() {
        return infoListAdapter.getCount() > 2;
    }

    private ArrayList<String> getLatLngArray() {
        ArrayList<String> latLngArray = new ArrayList<>();
        ItemsPlanItem planItem;

        for (int i = 0; i < infoListAdapter.getCount(); i++) {
            planItem = (ItemsPlanItem) infoListAdapter.getItem(i);
            latLngArray.add(String.valueOf(planItem.LAT + "," + planItem.LNG));
        }
        return latLngArray;
    }

    private String getWaypointsString() {
        ArrayList<String> latLngArray = getLatLngArray();
        latLngArray.remove(0);
        latLngArray.remove(latLngArray.size() - 1);

        StringBuilder sb = new StringBuilder();

        for (int i = 0; i < latLngArray.size(); i++) {
            if (i != 0)
                sb.append("|");
            sb.append(latLngArray.get(i));
        }
        Log.i(TAG, "WaypointsString: " + sb.toString());
        return sb.toString();
    }

    private String getAvoidOptions(String... avoidOptions) {
        StringBuilder sb = new StringBuilder();

        for (int i = 0; i < avoidOptions.length; i++) {
            if (i != 0)
                sb.append("|");
            sb.append(avoidOptions[i]);
        }
        return sb.toString();
    }

    private String getCombinedApiUrl(boolean useOptimize) {
        ArrayList<String> latLngArray = getLatLngArray();
        Log.i(TAG, "latLngArray: " + latLngArray.toString());

        String apiUrl;

        if (isContainWaypoints()) {
            String waypointsString = getWaypointsString();

            apiUrl = MessageFormat.format(ApiUrls.API_GOOGLE_DIRECTION_MULTI_POINT,
                    latLngArray.get(0),
                    latLngArray.get(latLngArray.size() - 1),
                    useOptimize,
                    waypointsString,
                    DIR_MODE_WALKING, getAvoidOptions(DIR_AVOID_TOLLS, DIR_AVOID_HIGHWAYS), Utility.getLocaleLanguage(),
                    getString(R.string.GoogleDirectionKey));
        }
        else {
            apiUrl = MessageFormat.format(ApiUrls.API_GOOGLE_DIRECTION,
                    latLngArray.get(0),
                    latLngArray.get(latLngArray.size() - 1),
                    DIR_MODE_WALKING, getAvoidOptions(DIR_AVOID_TOLLS, DIR_AVOID_HIGHWAYS), Utility.getLocaleLanguage(),
                    getString(R.string.GoogleDirectionKey));
        }

        return apiUrl;
    }

    private void startDirection() {
        if (canDirection()) {
            DialogHelper.showLoadingDialog(this);

            final String apiUrl = getCombinedApiUrl(false);

            WebAgent.getMultiDirectionsData(apiUrl, new WebAgent.WebResultImplement() {
                @Override
                public void onResultSucceed(String response) {
                    Intent intent = new Intent(UiMyPlanInfoActivity.this, UiPlanDirectionMapActivity.class);
                    intent.putExtra(BUNDLE_ENTRY_TYPE, ENTRY_TYPE_DIRECTIONS);

                    Bundle bundle = new Bundle();
                    bundle.putString(BUNDLE_PLAN_DIRECTION_JSON, response);
                    bundle.putInt(BUNDLE_PLAN_EDIT_INDEX, PLAN_EDIT_INDEX);

                    intent.putExtras(bundle);
                    startActivity(intent);
                }

                @Override
                public void onResultFail(String errorMessage) {
                    Utility.toastLong(errorMessage);
                    Log.e(TAG, errorMessage);
                }
            });
        }
        else
            Utility.toastLong(getString(R.string.plan_require_more_then_two));
    }
}
