package com.kingwaytek.cpami.biking.app.ui.planning;

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

import com.kingwaytek.cpami.biking.R;
import com.kingwaytek.cpami.biking.app.model.ApiUrls;
import com.kingwaytek.cpami.biking.app.model.DataArray;
import com.kingwaytek.cpami.biking.app.model.items.ItemsPlanItem;
import com.kingwaytek.cpami.biking.app.model.items.ItemsPlans;
import com.kingwaytek.cpami.biking.app.ui.BaseActivity;
import com.kingwaytek.cpami.biking.app.web.WebAgent;
import com.kingwaytek.cpami.biking.utilities.DialogHelper;
import com.kingwaytek.cpami.biking.utilities.FavoriteHelper;
import com.kingwaytek.cpami.biking.utilities.JsonParser;
import com.kingwaytek.cpami.biking.utilities.MenuHelper;
import com.kingwaytek.cpami.biking.utilities.Utility;
import com.kingwaytek.cpami.biking.utilities.adapter.PlanInfoListAdapter;

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

    /**
     * 2016/11/14 Updated by Vincent:<p>
     *
     * 如果頁面是從 {@link UiMyPlanEditActivity} savePlan回來的話，<br>
     * 就要假設 PlanName已被修改過，因此就要用 Intent帶 PlanName回來更新 extra，<br>
     * 這樣在執行 getPlanIndexByName(planName)時才能找到對的 index。
     */
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == RESULT_OK && requestCode == REQUEST_PlAN_UPDATE) {
            getIntent().putExtra(BUNDLE_PLAN_NAME, data.getStringExtra(BUNDLE_PLAN_NAME));
        }
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

    /**
     * 2016/10/20 Updated by Vincent:<p>
     *
     * PLAN_EDIT_INDEX 取得的方式改成：<br>
     * 從上一個 Activity Intent 傳 planName來，<br>
     * 然後再用 FavoriteHelper.getPlanIndexByName(planName)來取得回傳的 index.
     */
    private void getIndexAndItems() {
        if (ENTRY_TYPE != ENTRY_TYPE_VIEW_SHARED_PLAN) {
            Intent intent = getIntent();

            if (intent.hasExtra(BUNDLE_PLAN_NAME)) {
                PLAN_EDIT_INDEX = FavoriteHelper.getPlanIndexByName(intent.getStringExtra(BUNDLE_PLAN_NAME));

                Log.i(TAG, "getIndexAndItems() PLAN_EDIT_INDEX: " + PLAN_EDIT_INDEX);

                ItemsPlans planAndItems = DataArray.getPlansData().get(PLAN_EDIT_INDEX);

                if (notNull(planAndItems)) {
                    name = planAndItems.NAME;
                    setPlanContent(planAndItems);
                }
            }
        }
        else
            getItemsFromBundle();
    }

    private void getItemsFromBundle() {
        if (getIntent().hasExtra(BUNDLE_SHARED_ITEM)) {
            ItemsPlans planAndItems = JsonParser.parseAndGetSharedPlan(getIntent().getStringExtra(BUNDLE_SHARED_ITEM));

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
        if (ENTRY_TYPE != ENTRY_TYPE_VIEW_SHARED_PLAN)
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
                startActivityForResult(intent, REQUEST_PlAN_UPDATE);
                break;

            case ACTION_UPLOAD:
                uploadPlan();
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

    private void uploadPlan() {
        if (canDirection()) {
            final String name = planName.getText().toString();

            DialogHelper.showUploadConfirmDialog(this, name, new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    dialog.dismiss();

                    final String planContent = DataArray.getPlanObjectString(PLAN_EDIT_INDEX);

                    if (notNull(planContent)) {
                        DialogHelper.showLoadingDialog(UiMyPlanInfoActivity.this);

                        WebAgent.uploadDataToBikingService(POST_VALUE_TYPE_PLAN, name, planContent, new WebAgent.WebResultImplement() {
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
        else
            Utility.toastLong(getString(R.string.plan_require_more_then_two_for_upload));
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
                    Bundle bundle = new Bundle();

                    bundle.putString(BUNDLE_PLAN_DIRECTION_JSON, response);

                    if (ENTRY_TYPE != ENTRY_TYPE_VIEW_SHARED_PLAN) {
                        bundle.putInt(BUNDLE_ENTRY_TYPE, ENTRY_TYPE_DIRECTIONS);
                        bundle.putInt(BUNDLE_PLAN_EDIT_INDEX, PLAN_EDIT_INDEX);
                    }
                    else {
                        bundle.putInt(BUNDLE_ENTRY_TYPE, ENTRY_TYPE_VIEW_SHARED_PLAN);
                        bundle.putInt(BUNDLE_SHARED_ITEM_ID, getIntent().getIntExtra(BUNDLE_SHARED_ITEM_ID, 0));
                        bundle.putString(BUNDLE_SHARED_ITEM, getIntent().getStringExtra(BUNDLE_SHARED_ITEM));
                    }

                    intent.putExtras(bundle);
                    startActivity(intent);

                    if (ENTRY_TYPE == ENTRY_TYPE_VIEW_SHARED_PLAN)
                        UiMyPlanInfoActivity.this.finish();
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
