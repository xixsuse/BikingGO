package com.kingwaytek.cpami.biking.app.ui.poi_book;

import android.content.Intent;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ListView;

import com.kingwaytek.cpami.biking.R;
import com.kingwaytek.cpami.biking.app.model.items.ItemsCitiesAndPOI;
import com.kingwaytek.cpami.biking.app.ui.BaseActivity;
import com.kingwaytek.cpami.biking.app.web.WebAgent;
import com.kingwaytek.cpami.biking.utilities.DialogHelper;
import com.kingwaytek.cpami.biking.utilities.JsonParser;
import com.kingwaytek.cpami.biking.utilities.Utility;
import com.kingwaytek.cpami.biking.utilities.adapter.CitiesAndPoiListAdapter;

import java.util.ArrayList;

/**
 * <h1>景點書：City & POI List</h1>
 *
 * 城市清單與城市內景點列表在同一個 ListView中做切換！
 *
 * @author Vincent (2016/9/6)
 */
public class UiPoiCityListActivity extends BaseActivity implements TextWatcher {

    private EditText edit_filter;
    private ListView cityListView;
    private CitiesAndPoiListAdapter cityAdapter;

    private ArrayList<ItemsCitiesAndPOI> cityList;

    private boolean isFilterable;

    @Override
    protected void init() {
        setEditText();
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
        edit_filter = (EditText) findViewById(R.id.edit_listFilter);
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

    private void setEditText() {
        edit_filter.setSingleLine();
        edit_filter.setImeOptions(EditorInfo.IME_ACTION_DONE);
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
                goToPoiDetail(response);
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
        isFilterable = !isViewingCities;

        ArrayList<ItemsCitiesAndPOI> citiesAndPOIs;

        if (isViewingCities) {
            edit_filter.setVisibility(View.GONE);
            edit_filter.removeTextChangedListener(this);
            edit_filter.setText("");

            if (cityList == null) {
                citiesAndPOIs = JsonParser.parseAndGetCityList(jsonString);
                cityList = citiesAndPOIs;
            }
            else
                citiesAndPOIs = cityList;
        }
        else {
            edit_filter.setVisibility(View.VISIBLE);
            edit_filter.addTextChangedListener(this);
            citiesAndPOIs = JsonParser.parseAndGetCityPoiList(jsonString);
        }

        if (cityAdapter == null) {
            cityAdapter = new CitiesAndPoiListAdapter(this, isViewingCities, citiesAndPOIs);
            cityListView.setAdapter(cityAdapter);
        }
        else
            cityAdapter.resetList(isViewingCities, citiesAndPOIs);
    }

    private void goToPoiDetail(String jsonString) {
        Intent intent = new Intent(this, UiPoiDetailActivity.class);
        intent.putExtra(BUNDLE_POI_DETAIL, jsonString);
        startActivity(intent);
    }

    @Override
    public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

    @Override
    public void onTextChanged(CharSequence s, int start, int before, int count) {}

    @Override
    public void afterTextChanged(Editable s) {
        if (isFilterable) {
            if (s.toString().isEmpty())
                cityAdapter.filterData("");
            else
                cityAdapter.filterData(s.toString());

            cityListView.smoothScrollToPosition(0);
        }
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
