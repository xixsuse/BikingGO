package com.kingwaytek.cpami.bykingTablet.app.address;

import android.app.ListActivity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.CheckBox;
import android.widget.ListView;
import android.widget.TextView;

import com.kingwaytek.cpami.bykingTablet.R;
import com.kingwaytek.cpami.bykingTablet.utilities.Util;
import com.kingwaytek.cpami.bykingTablet.data.GeoPoint;
import com.kingwaytek.cpami.bykingTablet.data.ITown;
import com.kingwaytek.cpami.bykingTablet.view.ListViewAdapter;
import com.kingwaytek.cpami.bykingTablet.view.ViewConstant.ActivityCaller;
import com.kingwaytek.cpami.bykingTablet.view.ViewConstant.ContextMenuOptions;
import com.sonavtek.sonav.sonav;

import java.util.ArrayList;

/**
 * Activity for Select a City in Address Search
 *
 * @author Andy Chiao (andy.chiao@kingwaytek.com)
 *
 */
public class CitySelection extends ListActivity {

    private Intent itenCaller;
    private ListViewAdapter listAdapter;

    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle icicle) {
        super.onCreate(icicle);

        itenCaller = getIntent();
        setContentView(R.layout.selection_listview_layout);
        TextView titleBar = (TextView) findViewById(R.id.titlebar_text);
        titleBar.setText(R.string.address_search_city_prompt);
        TextView tvTitle = (TextView) findViewById(R.id.selection_listview_title);
        tvTitle.setText(R.string.address_search_city_prompt);

        if (Util.city_sort == null) {
            Util.getSortPOICity(this);
        }
        ShowList();
    }

    @Override
    public void setTitle(CharSequence title) {
        ((TextView) findViewById(R.id.title_text)).setText(title);
        ((TextView) findViewById(R.id.title_text2)).setText("");
    }

    /**
     * onListItemClick Handler inheritance of ListActivity
     */
    @Override
    protected void onListItemClick(ListView l, View v, int position, long id) {
        Log.i("CitySelection", "position = " + position + ", id = " + id);

        switch (listAdapter.getListMode()) {
            case SINGLE:
                listItem_Click(position);
                break;
            case MULTIPLE:
                CheckBox ckbSelect = (CheckBox) v.findViewById(R.id.selection_listview_item_checkbox);
                ckbSelect.toggle();
                listAdapter.getCheckBoxData().put(position, ckbSelect.isChecked());
            default:
                break;
        }

        super.onListItemClick(l, v, position, id);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (resultCode == RESULT_OK && requestCode == ActivityCaller.ADDRESS.getValue())
        {
            ContextMenuOptions option = (ContextMenuOptions) data.getSerializableExtra("Action");

            String addressPart = data.getStringExtra("addressResult");
            double[] addressXY = data.getDoubleArrayExtra("addressLocation");

            itenCaller.putExtra("Action", option);
            itenCaller.putExtra("Name", addressPart);
            itenCaller.putExtra("addressResult", addressPart);
            itenCaller.putExtra("addressLocation", addressXY);
            itenCaller.putExtra("Location", new GeoPoint(addressXY[1], addressXY[2]));

            setResult(RESULT_OK, itenCaller);
            finish();
        }
        else if (resultCode == RESULT_FIRST_USER) {
            setResult(RESULT_FIRST_USER);
            finish();
        }
    }

    protected void listItem_Click(int position) {
        int cityID = Util.city_sort.get(position).getCityID();
        String cityName = Util.city_sort.get(position).getCityName();

        sonav engine = sonav.getInstance();

        ITown[] towns = engine.showlisttown(cityID);
        Intent itenContent;

        if (cityName.contains("台北市") || cityName.contains("基隆市") || cityName.contains("新竹市")) {

            itenContent = new Intent(this, RoadInput.class); // roadInput
            itenContent.putExtra("cityID", cityID);
            itenContent.putExtra("cityName", cityName);
        }
        else {
            if (towns.length == 0) {
                itenContent = new Intent(this, RoadInput.class); // roadInput
                itenContent.putExtra("townID", cityID);
                itenContent.putExtra("townName", cityName);
            }
            else {
                itenContent = new Intent(this, TownSelection.class);
                itenContent.putExtra("cityID", cityID);
                itenContent.putExtra("cityName", cityName);
            }

        }
        itenContent.putExtra("addressSelection", cityName);
        itenContent.putExtra("setpoint", itenCaller.getStringExtra("setpoint"));
        startActivityForResult(itenContent, ActivityCaller.ADDRESS.getValue());
    }

    private void ShowList() {
        ArrayList<String> cur_city_name_sort = new ArrayList<>();

        for (int i = 0; i < Util.city_sort.size(); i++) {
            String temp;
            temp = Util.city_sort.get(i).getCityName();
            cur_city_name_sort.add(temp);
        }

        listAdapter = new ListViewAdapter(
                this,
                R.layout.selection_listview_item_address,
                R.id.selection_listview_item_text,
                cur_city_name_sort.toArray(new String[cur_city_name_sort.size()]));
        setListAdapter(listAdapter);
    }
}