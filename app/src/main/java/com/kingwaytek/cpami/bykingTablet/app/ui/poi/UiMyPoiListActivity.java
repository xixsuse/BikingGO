package com.kingwaytek.cpami.bykingTablet.app.ui.poi;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;

import com.google.android.gms.maps.model.LatLng;
import com.kingwaytek.cpami.bykingTablet.R;
import com.kingwaytek.cpami.bykingTablet.app.model.DataArray;
import com.kingwaytek.cpami.bykingTablet.app.model.items.ItemsMyPOI;
import com.kingwaytek.cpami.bykingTablet.app.ui.BaseActivity;
import com.kingwaytek.cpami.bykingTablet.app.ui.UiMainMapActivity;
import com.kingwaytek.cpami.bykingTablet.utilities.MenuHelper;
import com.kingwaytek.cpami.bykingTablet.utilities.SettingManager;
import com.kingwaytek.cpami.bykingTablet.utilities.Utility;
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
                Intent intent;
                Bundle bundle = new Bundle();

                switch (ENTRY_TYPE) {
                    case ENTRY_TYPE_DEFAULT:
                        intent = new Intent(UiMyPoiListActivity.this, UiMainMapActivity.class);

                        intent.putExtra(BUNDLE_MY_POI_INFO, poiItem);

                        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                        startActivity(intent);

                        SettingManager.MapLayer.setMyPoiFlag(true);
                        break;

                    case ENTRY_TYPE_LOCATION_SELECT:
                        intent = new Intent();

                        bundle.putString(BUNDLE_LOCATION_TITLE, poiItem.TITLE);
                        bundle.putParcelable(BUNDLE_LOCATION_LATLNG, new LatLng(poiItem.LAT, poiItem.LNG));

                        intent.putExtras(bundle);

                        setResult(RESULT_OK, intent);
                        finish();
                        break;
                }
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
                Utility.toastLong(getString(R.string.poi_add_a_new_one_instruction));
                goTo(UiMainMapActivity.class, true);
                break;
        }

        return true;
    }
}
