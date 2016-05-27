package com.kingwaytek.cpami.bykingTablet.app.ui.poi;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;

import com.kingwaytek.cpami.bykingTablet.R;
import com.kingwaytek.cpami.bykingTablet.app.model.DataArray;
import com.kingwaytek.cpami.bykingTablet.app.model.ItemsMyPOI;
import com.kingwaytek.cpami.bykingTablet.app.ui.BaseActivity;
import com.kingwaytek.cpami.bykingTablet.app.ui.UiMainMapActivity;
import com.kingwaytek.cpami.bykingTablet.utilities.SettingManager;
import com.kingwaytek.cpami.bykingTablet.utilities.adapter.MyPoiListAdapter;

/**
 * 我的景點列表
 *
 * @author Vincent (2016/5/20)
 */
public class UiMyPoiListActivity extends BaseActivity {

    private ListView poiListView;
    private MyPoiListAdapter poiListAdapter;

    @Override
    protected void init() {

    }

    @Override
    public void onResume() {
        super.onResume();
        setPoiList();
    }

    @Override
    protected String getActionBarTitle() {
        return getString(R.string.menu_my_poi);
    }

    @Override
    protected int getLayoutId() {
        return R.layout.activity_my_poi_list;
    }

    @Override
    protected void findViews() {
        poiListView = (ListView) findViewById(R.id.myPoiListView);
    }

    @Override
    protected void setListener() {
        poiListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                ItemsMyPOI poiItem = (ItemsMyPOI) parent.getItemAtPosition(position);

                Intent intent = new Intent(UiMyPoiListActivity.this, UiMainMapActivity.class);
                Bundle bundle = new Bundle();

                bundle.putSerializable(BUNDLE_MY_POI_INFO, poiItem);
                intent.putExtras(bundle);

                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(intent);

                SettingManager.MarkerFlag.setMyPoiFlag(true);
            }
        });
    }

    private void setPoiList() {
        if (poiListAdapter == null) {
            poiListAdapter = new MyPoiListAdapter(this, DataArray.getMyPOI());
            poiListView.setAdapter(poiListAdapter);
        }
        else
            poiListAdapter.refreshList(DataArray.getMyPOI());
    }
}
