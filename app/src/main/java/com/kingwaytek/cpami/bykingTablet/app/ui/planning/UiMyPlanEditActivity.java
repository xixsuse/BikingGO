package com.kingwaytek.cpami.bykingTablet.app.ui.planning;

import android.content.DialogInterface;
import android.content.Intent;
import android.location.Location;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;

import com.google.android.gms.maps.model.LatLng;
import com.kingwaytek.api.widget.dslv.DragSortListView;
import com.kingwaytek.cpami.bykingTablet.R;
import com.kingwaytek.cpami.bykingTablet.app.model.items.ItemsPlanItem;
import com.kingwaytek.cpami.bykingTablet.app.ui.BaseActivity;
import com.kingwaytek.cpami.bykingTablet.app.ui.UiMainMapActivity;
import com.kingwaytek.cpami.bykingTablet.callbacks.OnLocationSelectedCallBack;
import com.kingwaytek.cpami.bykingTablet.hardware.MyLocationManager;
import com.kingwaytek.cpami.bykingTablet.utilities.DialogHelper;
import com.kingwaytek.cpami.bykingTablet.utilities.adapter.PlanEditListAdapter;

import java.util.ArrayList;

/**
 * Created by vincent.chang on 2016/6/1.
 */
public class UiMyPlanEditActivity extends BaseActivity implements OnLocationSelectedCallBack {

    private EditText edit_planTitle;
    private ImageButton planAddBtn;

    private DragSortListView dragSortListView;
    private PlanEditListAdapter editListAdapter;

    private ArrayList<ItemsPlanItem> planItemList;

    @Override
    protected void init() {

    }

    @Override
    public void onResume() {
        super.onResume();
        hidePlanAddButtonIfHasFive();
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
                addPlan();
            }
        });

        dragSortListView.setDropListener(getDropListener());
        dragSortListView.setRemoveListener(getRemoveListener());
    }

    private void addPlan() {
        DialogHelper.showLocationSelectMenu(this, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                switch (which) {
                    case 0:
                        Location location = MyLocationManager.getLastLocation();
                        addItemToListView(getString(R.string.location_current), new LatLng(location.getLatitude(), location.getLongitude()));
                        break;

                    case 1:
                        Intent intent = new Intent(UiMyPlanEditActivity.this, UiMainMapActivity.class);

                        Bundle bundle = new Bundle();
                        bundle.putSerializable(BUNDLE_LOCATION_SELECT, UiMyPlanEditActivity.this);

                        intent.putExtra(BUNDLE_ENTRY_TYPE, ENTRY_TYPE_LOCATION_SELECT);
                        intent.putExtras(bundle);

                        startActivity(intent);
                        break;

                    case 2:

                        break;
                }
            }
        });
    }

    private void addItemToListView(String title, LatLng latLng) {
        if (editListAdapter == null) {
            planItemList = new ArrayList<>();
            planItemList.add(new ItemsPlanItem(title, latLng, 1));

            editListAdapter = new PlanEditListAdapter(this, planItemList);
            dragSortListView.setAdapter(editListAdapter);
        }
        else {
            int order = editListAdapter.getCount() + 1;
            editListAdapter.addPlanItem(new ItemsPlanItem(title, latLng, order));
        }
        hidePlanAddButtonIfHasFive();
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
                hidePlanAddButtonIfHasFive();
            }
        };
    }

    public void hidePlanAddButtonIfHasFive() {
        if (notNull(editListAdapter)) {
            if (editListAdapter.getCount() >= 5)
                planAddBtn.setVisibility(View.GONE);
            else
                planAddBtn.setVisibility(View.VISIBLE);
        }
    }

    @Override
    public void onLocationSelected(String title, LatLng latLng) {

    }
}
