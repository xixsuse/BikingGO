package com.kingwaytek.cpami.bykingTablet.app.ui.planning;

import android.content.DialogInterface;
import android.content.Intent;
import android.location.Location;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;
import android.widget.ImageButton;

import com.google.android.gms.maps.model.LatLng;
import com.kingwaytek.api.widget.dslv.DragSortListView;
import com.kingwaytek.cpami.bykingTablet.R;
import com.kingwaytek.cpami.bykingTablet.app.model.DataArray;
import com.kingwaytek.cpami.bykingTablet.app.model.items.ItemsPlanItem;
import com.kingwaytek.cpami.bykingTablet.app.model.items.ItemsPlans;
import com.kingwaytek.cpami.bykingTablet.app.ui.BaseActivity;
import com.kingwaytek.cpami.bykingTablet.app.ui.UiMainMapActivity;
import com.kingwaytek.cpami.bykingTablet.app.ui.poi.UiMyPoiListActivity;
import com.kingwaytek.cpami.bykingTablet.hardware.MyLocationManager;
import com.kingwaytek.cpami.bykingTablet.utilities.DialogHelper;
import com.kingwaytek.cpami.bykingTablet.utilities.FavoriteHelper;
import com.kingwaytek.cpami.bykingTablet.utilities.MenuHelper;
import com.kingwaytek.cpami.bykingTablet.utilities.Utility;
import com.kingwaytek.cpami.bykingTablet.utilities.adapter.PlanEditListAdapter;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

/**
 * 行程規劃編輯頁面，
 * 主要使用 DragAndSorListView & write JSON to SD file來儲存！
 *
 * @author Vincent (2016/6/1)
 */
public class UiMyPlanEditActivity extends BaseActivity {

    private EditText edit_planTitle;
    private ImageButton planAddBtn;

    private DragSortListView dragSortListView;
    private PlanEditListAdapter editListAdapter;

    private static final int MAX_POINTS_COUNT = 5;
    private static final int INDEX_ADD_A_NEW_ONE = -1;
    private int INDEX_WHICH_PLAN_ITEM;

    private static final int PLAN_EDIT_INDEX_A_NEW_ONE = -1;
    private int PLAN_EDIT_INDEX;

    @Override
    protected void init() {
        setPlanIfExists();
    }

    @Override
    public void onResume() {
        super.onResume();
        hidePlanAddButtonIfReachMax();
    }

    @Override
    protected String getActionBarTitle() {
        return getString(R.string.menu_planning);
    }

    @Override
    protected int getLayoutId() {
        return R.layout.activity_my_plan_edit;
    }

    @Override
    protected void findViews() {
        edit_planTitle = (EditText) findViewById(R.id.edit_planTitle);
        planAddBtn = (ImageButton) findViewById(R.id.planAddButton);
        dragSortListView = (DragSortListView) findViewById(R.id.dragListView);
    }

    @Override
    protected void setListener() {
        planAddBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                selectPlanItem(INDEX_ADD_A_NEW_ONE);
            }
        });

        dragSortListView.setDropListener(getDropListener());
        dragSortListView.setRemoveListener(getRemoveListener());
    }

    private void setPlanIfExists() {
        PLAN_EDIT_INDEX = getIntent().getIntExtra(BUNDLE_PLAN_EDIT_INDEX, PLAN_EDIT_INDEX_A_NEW_ONE);

        if (PLAN_EDIT_INDEX != PLAN_EDIT_INDEX_A_NEW_ONE) {
            getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN);

            ItemsPlans planAndItems = DataArray.getPlansData().get(PLAN_EDIT_INDEX);

            if (notNull(planAndItems))
                setPlanContents(planAndItems);
        }
        else {
            edit_planTitle.requestFocus();
            edit_planTitle.setImeOptions(EditorInfo.IME_ACTION_NONE);
        }
    }

    private void setPlanContents(ItemsPlans planAndItems) {
        edit_planTitle.setText(planAndItems.NAME);
        edit_planTitle.setSelection(planAndItems.NAME.length());

        for (ItemsPlanItem planItem : planAndItems.PLAN_ITEMS) {
            addItemToListView(planItem.TITLE, planItem.LAT, planItem.LNG);
        }
    }

    public void selectPlanItem(final int index) {
        INDEX_WHICH_PLAN_ITEM = index;

        DialogHelper.showLocationSelectMenu(this, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                Intent intent;

                switch (which) {
                    case 0:
                        Location location = MyLocationManager.getLastLocation();
                        if (index == INDEX_ADD_A_NEW_ONE)
                            addItemToListView(getString(R.string.location_current), location.getLatitude(), location.getLongitude());
                        else
                            setItemToPosition(getString(R.string.location_current), location.getLatitude(), location.getLongitude());
                        break;

                    case 1:
                        intent = new Intent(UiMyPlanEditActivity.this, UiMainMapActivity.class);
                        intent.putExtra(BUNDLE_ENTRY_TYPE, ENTRY_TYPE_LOCATION_SELECT);

                        startActivityForResult(intent, REQUEST_SELECT_LOCATION);
                        break;

                    case 2:
                        intent = new Intent(UiMyPlanEditActivity.this, UiMyPoiListActivity.class);
                        intent.putExtra(BUNDLE_ENTRY_TYPE, ENTRY_TYPE_LOCATION_SELECT);

                        startActivityForResult(intent, REQUEST_SELECT_LOCATION);
                        break;
                }
            }
        });
    }

    private void addItemToListView(String title, double lat, double lng) {
        if (editListAdapter == null) {
            ArrayList<ItemsPlanItem> planItemList = new ArrayList<>();
            planItemList.add(new ItemsPlanItem(title, lat, lng));

            editListAdapter = new PlanEditListAdapter(this, planItemList);
            dragSortListView.setAdapter(editListAdapter);
        }
        else {
            editListAdapter.addPlanItem(new ItemsPlanItem(title, lat, lng));
        }
        hidePlanAddButtonIfReachMax();
    }

    private void setItemToPosition(String title, double lat, double lng) {
        editListAdapter.setPlanItem(INDEX_WHICH_PLAN_ITEM, new ItemsPlanItem(title, lat, lng));
        hidePlanAddButtonIfReachMax();
    }

    private DragSortListView.DropListener getDropListener() {
        return new DragSortListView.DropListener() {
            @Override
            public void drop(int from, int to) {
                if (from != to) {
                    ItemsPlanItem planItem = (ItemsPlanItem) editListAdapter.getItem(from);
                    editListAdapter.removePlanItem(from);
                    editListAdapter.insertPlanItem(to, planItem);
                }
            }
        };
    }

    private DragSortListView.RemoveListener getRemoveListener() {
        return new DragSortListView.RemoveListener() {
            @Override
            public void remove(int which) {
                editListAdapter.removePlanItem(which);
                hidePlanAddButtonIfReachMax();
            }
        };
    }

    public void hidePlanAddButtonIfReachMax() {
        if (notNull(editListAdapter)) {
            if (editListAdapter.getCount() >= MAX_POINTS_COUNT)
                planAddBtn.setVisibility(View.GONE);
            else
                planAddBtn.setVisibility(View.VISIBLE);
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == RESULT_OK) {
            switch (requestCode) {
                case REQUEST_SELECT_LOCATION:
                    Bundle bundle = data.getExtras();
                    if (notNull(bundle)) {
                        String title = bundle.getString(BUNDLE_LOCATION_TITLE);
                        LatLng latLng = bundle.getParcelable(BUNDLE_LOCATION_LATLNG);

                        if (notNull(latLng)) {
                            if (INDEX_WHICH_PLAN_ITEM == INDEX_ADD_A_NEW_ONE)
                                addItemToListView(title, latLng.latitude, latLng.longitude);
                            else
                                setItemToPosition(title, latLng.latitude, latLng.longitude);
                        }
                    }
                    break;
            }
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuHelper.setMenuOptionsByMenuAction(menu, ACTION_SAVE);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        super.onOptionsItemSelected(item);

        switch (item.getItemId()) {
            case ACTION_SAVE:
                savePlan();
                break;
        }

        return true;
    }

    private String getPlanTitle() {
        return edit_planTitle.getText().toString();
    }

    private void savePlan() {
        if (getPlanTitle().isEmpty()) {
            Utility.toastShort(getString(R.string.plan_require_title));
            edit_planTitle.requestFocus();
            hideKeyboard(false);
        }
        else {
            try {
                JSONArray ja = new JSONArray();
                ItemsPlanItem planItem;

                if (notNull(editListAdapter)) {
                    for (int i = 0; i < editListAdapter.getCount(); i++) {
                        planItem = (ItemsPlanItem) editListAdapter.getItem(i);

                        JSONObject jo = new JSONObject();
                        jo.put(FavoriteHelper.POI_TITLE, planItem.TITLE);
                        jo.put(FavoriteHelper.POI_LAT, planItem.LAT);
                        jo.put(FavoriteHelper.POI_LNG, planItem.LNG);

                        ja.put(jo);
                    }
                }

                if (PLAN_EDIT_INDEX == PLAN_EDIT_INDEX_A_NEW_ONE) {
                    JSONObject singlePlanJO = new JSONObject();
                    singlePlanJO.put(FavoriteHelper.PLAN_NAME, getPlanTitle());
                    singlePlanJO.put(FavoriteHelper.PLAN_ITEMS, ja);

                    int planEditIndex = FavoriteHelper.addPlan(singlePlanJO);
                    goToPlanInfo(planEditIndex);
                }
                else {
                    FavoriteHelper.updatePlan(PLAN_EDIT_INDEX, getPlanTitle(), ja);
                    finish();
                }
            }
            catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }

    private void goToPlanInfo(int planEditIndex) {
        Intent intent = new Intent(UiMyPlanEditActivity.this, UiMyPlanInfoActivity.class);
        intent.putExtra(BUNDLE_PLAN_EDIT_INDEX, planEditIndex);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(intent);
        finish();
    }
}
