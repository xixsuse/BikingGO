package com.kingwaytek.cpami.bykingTablet.app.ui.poi_book;

import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;

import com.kingwaytek.cpami.bykingTablet.R;
import com.kingwaytek.cpami.bykingTablet.app.model.items.ItemsCitiesAndPOI;
import com.kingwaytek.cpami.bykingTablet.app.ui.BaseActivity;
import com.kingwaytek.cpami.bykingTablet.app.web.WebAgent;
import com.kingwaytek.cpami.bykingTablet.utilities.DialogHelper;
import com.kingwaytek.cpami.bykingTablet.utilities.JsonParser;
import com.kingwaytek.cpami.bykingTablet.utilities.Utility;
import com.kingwaytek.cpami.bykingTablet.utilities.adapter.CitiesAndPoiListAdapter;

import java.util.ArrayList;

/**
 * <h1>景點書：City & POI List</h1>
 *
 * 城市清單與城市內景點列表在同一個 ListView中做切換！
 *
 * @author Vincent (2016/9/6)
 */
public class UiPoiCityListActivity extends BaseActivity {

    private ListView cityListView;
    private CitiesAndPoiListAdapter cityAdapter;

    private ArrayList<ItemsCitiesAndPOI> cityList;

    @Override
    protected void init() {
        getCityList();
    }

    @Override
    protected String getActionBarTitle() {
        return getString(R.string.menu_poi_book);
    }

    @Override
    protected int getLayoutId() {
        return R.layout.activity_city_list;
    }

    @Override
    protected void findViews() {
        cityListView = (ListView) findViewById(R.id.cities_listView);
    }

    @Override
    protected void setListener() {
        cityListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                if (notNull(cityAdapter) && cityAdapter.isViewingCities()) {
                    String cityName = (String) parent.getItemAtPosition(position);
                    getCityPoiList(cityName);
                }
                else
                    getPoiDetail((int) id);
            }
        });
    }

    private void getCityList() {
        DialogHelper.showLoadingDialog(this);

        WebAgent.getCityListFromBikingService(new WebAgent.WebResultImplement() {
            @Override
            public void onResultSucceed(String response) {
                setCityListView(response, true);
                DialogHelper.dismissDialog();
            }

            @Override
            public void onResultFail(String errorMessage) {
                Utility.toastShort(errorMessage);
                DialogHelper.dismissDialog();
            }
        });
    }

    private void getCityPoiList(String cityName) {
        DialogHelper.showLoadingDialog(this);

        WebAgent.getCityPoiFromBikingService(cityName, new WebAgent.WebResultImplement() {
            @Override
            public void onResultSucceed(String response) {
                setCityListView(response, false);
                DialogHelper.dismissDialog();
            }

            @Override
            public void onResultFail(String errorMessage) {
                Utility.toastShort(errorMessage);
                DialogHelper.dismissDialog();
            }
        });
    }

    private void getPoiDetail(int id) {
        DialogHelper.showLoadingDialog(this);

        WebAgent.getPoiDetailFromBikingService(id, new WebAgent.WebResultImplement() {
            @Override
            public void onResultSucceed(String response) {
                // TODO POI DETAIL!!!
                DialogHelper.dismissDialog();
            }

            @Override
            public void onResultFail(String errorMessage) {
                Utility.toastShort(errorMessage);
                DialogHelper.dismissDialog();
            }
        });
    }

    private void setCityListView(String jsonString, boolean isViewingCities) {
        ArrayList<ItemsCitiesAndPOI> citiesAndPOIs;

        if (isViewingCities) {
            if (cityList == null) {
                citiesAndPOIs = JsonParser.parseAndGetCityList(jsonString);
                cityList = citiesAndPOIs;
            }
            else
                citiesAndPOIs = cityList;
        }
        else
            citiesAndPOIs = JsonParser.parseAndGetCityPoiList(jsonString);

        if (cityAdapter == null) {
            cityAdapter = new CitiesAndPoiListAdapter(this, isViewingCities, citiesAndPOIs);
            cityListView.setAdapter(cityAdapter);
        }
        else
            cityAdapter.resetList(isViewingCities, citiesAndPOIs);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            if (notNull(cityAdapter) && !cityAdapter.isViewingCities()) {
                setCityListView(null, true);
            }
            else
                super.onOptionsItemSelected(item);
        }
        return true;
    }

    @Override
    public void onBackPressed() {
        if (notNull(cityAdapter) && !cityAdapter.isViewingCities()) {
            setCityListView(null, true);
        }
        else
            super.onBackPressed();
    }
}
