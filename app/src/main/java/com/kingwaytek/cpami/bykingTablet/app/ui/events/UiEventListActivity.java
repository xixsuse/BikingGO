package com.kingwaytek.cpami.bykingTablet.app.ui.events;

import android.widget.ListView;

import com.kingwaytek.cpami.bykingTablet.R;
import com.kingwaytek.cpami.bykingTablet.app.model.DataArray;
import com.kingwaytek.cpami.bykingTablet.app.model.items.ItemsEvents;
import com.kingwaytek.cpami.bykingTablet.app.ui.BaseActivity;
import com.kingwaytek.cpami.bykingTablet.utilities.DialogHelper;
import com.kingwaytek.cpami.bykingTablet.utilities.adapter.EventListAdapter;

import java.util.ArrayList;

/**
 * Created by vincent.chang on 2016/7/7.
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
}
