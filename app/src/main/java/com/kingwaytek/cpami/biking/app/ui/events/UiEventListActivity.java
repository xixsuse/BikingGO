package com.kingwaytek.cpami.biking.app.ui.events;

import android.content.Intent;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;

import com.kingwaytek.cpami.biking.R;
import com.kingwaytek.cpami.biking.app.model.DataArray;
import com.kingwaytek.cpami.biking.app.model.items.ItemsEvents;
import com.kingwaytek.cpami.biking.app.ui.BaseActivity;
import com.kingwaytek.cpami.biking.utilities.DialogHelper;
import com.kingwaytek.cpami.biking.utilities.adapter.EventListAdapter;

import java.util.ArrayList;

/**
 * 活動訊息列表
 *
 * @author Vincent (2016/7/7)
 */
public class UiEventListActivity extends BaseActivity {

    private ListView eventListView;

    @Override
    protected void init() {
        getEventsData();
    }

    @Override
    protected String getActionBarTitle() {
        return getString(R.string.menu_events);
    }

    @Override
    protected int getLayoutId() {
        return R.layout.activity_event_list;
    }

    @Override
    protected void findViews() {
        eventListView = (ListView) findViewById(R.id.events_listView);
    }

    @Override
    protected void setListener() {
        eventListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                goToDetail(position);
            }
        });
    }

    private void getEventsData() {
        DialogHelper.showLoadingDialog(this);

        DataArray.checkAndGetEventsData(new DataArray.OnDataGetCallBack() {
            @Override
            public void onDataGet() {
                ArrayList<String> nameList = new ArrayList<>();

                for (ItemsEvents eventItem : DataArray.list_events.get()) {
                    nameList.add(eventItem.NAME);
                }
                setEventListView(nameList);

                DialogHelper.dismissDialog();
            }
        });
    }

    private void setEventListView(ArrayList<String> nameList) {
        eventListView.setAdapter(new EventListAdapter(this, nameList));
    }

    private void goToDetail(final int position) {
        DialogHelper.showLoadingDialog(this);

        DataArray.checkAndGetEventsData(new DataArray.OnDataGetCallBack() {
            @Override
            public void onDataGet() {
                Intent intent = new Intent(UiEventListActivity.this, UiEventDetailActivity.class);
                intent.putExtra(BUNDLE_EVENT_DETAIL, DataArray.list_events.get().get(position));
                startActivity(intent);

                DialogHelper.dismissDialog();
            }
        });
    }
}
