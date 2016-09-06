package com.kingwaytek.cpami.bykingTablet.app.ui.planning;

import android.content.Intent;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ListView;

import com.kingwaytek.cpami.bykingTablet.R;
import com.kingwaytek.cpami.bykingTablet.app.model.DataArray;
import com.kingwaytek.cpami.bykingTablet.app.ui.BaseActivity;
import com.kingwaytek.cpami.bykingTablet.app.ui.track.UiTrackMapActivity;
import com.kingwaytek.cpami.bykingTablet.app.web.WebAgent;
import com.kingwaytek.cpami.bykingTablet.utilities.DialogHelper;
import com.kingwaytek.cpami.bykingTablet.utilities.Utility;
import com.kingwaytek.cpami.bykingTablet.utilities.adapter.SharedListAdapter;

/**
 * 車友分享行程 or 軌跡清單
 *
 * @author Vincent (2016/9/2)
 */
public class UiSharedListActivity extends BaseActivity implements TextWatcher {

    private int listType;

    private EditText edit_filter;

    private ListView sharedListView;
    private SharedListAdapter sharedAdapter;

    private boolean isFilterable;

    @Override
    protected void init() {
        getBundleAndSetEditText();
    }

    @Override
    public void onResume() {
        super.onResume();
        getListTypeAndGetData();
    }

    @Override
    protected String getActionBarTitle() {
        return getString(R.string.page_shared_list);
    }

    @Override
    protected int getLayoutId() {
        return R.layout.activity_shared_list;
    }

    @Override
    protected void findViews() {
        edit_filter = (EditText) findViewById(R.id.edit_listFilter);
        sharedListView = (ListView) findViewById(R.id.sharedListView);
    }

    @Override
    protected void setListener() {
        edit_filter.addTextChangedListener(this);

        sharedListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                getSharedContentAndGoNext((int)id);
            }
        });
    }

    private void getBundleAndSetEditText() {
        listType = getIntent().getIntExtra(BUNDLE_SHARED_LIST_TYPE, SHARED_LIST_TYPE_PLAN);
        edit_filter.setSingleLine();
        edit_filter.setImeOptions(EditorInfo.IME_ACTION_DONE);
    }

    private void getListTypeAndGetData() {
        final boolean isPlanType = listType == SHARED_LIST_TYPE_PLAN;
        String postTypeValue = isPlanType ? POST_VALUE_TYPE_PLAN : POST_VALUE_TYPE_TRACK;

        DialogHelper.showLoadingDialog(this);

        WebAgent.getListFromBikingService(postTypeValue, new WebAgent.WebResultImplement() {
            @Override
            public void onResultSucceed(String response) {
                if (sharedAdapter == null) {
                    sharedAdapter = new SharedListAdapter(UiSharedListActivity.this, isPlanType, DataArray.getSharedList(response));
                    sharedListView.setAdapter(sharedAdapter);
                }
                else {
                    sharedAdapter.refreshData(DataArray.getSharedList(response));
                    afterTextChanged(edit_filter.getText());
                }

                DialogHelper.dismissDialog();
                isFilterable = true;
            }

            @Override
            public void onResultFail(String errorMessage) {
                Utility.toastShort(errorMessage);
                DialogHelper.dismissDialog();
            }
        });
    }

    @Override
    public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

    @Override
    public void onTextChanged(CharSequence s, int start, int before, int count) {}

    @Override
    public void afterTextChanged(Editable s) {
        if (isFilterable) {
            if (s.toString().isEmpty()) {
                sharedListView.smoothScrollToPosition(0);
                sharedAdapter.filterData("");
            }
            else {
                sharedListView.smoothScrollToPosition(0);
                sharedAdapter.filterData(s.toString());
            }
        }
    }

    private void getSharedContentAndGoNext(final int id) {
        DialogHelper.showLoadingDialog(this);
        WebAgent.downloadDataFromBikingService(id, new WebAgent.WebResultImplement() {
            @Override
            public void onResultSucceed(String response) {
                goToPlanOrTrack(response, id);
                DialogHelper.dismissDialog();
            }

            @Override
            public void onResultFail(String errorMessage) {
                Utility.toastShort(errorMessage);
                DialogHelper.dismissDialog();
            }
        });
    }

    private void goToPlanOrTrack(String jsonString, int id) {
        Intent intent = new Intent();

        switch (listType) {
            case SHARED_LIST_TYPE_PLAN:
                intent.setClass(this, UiMyPlanInfoActivity.class);
                intent.putExtra(BUNDLE_ENTRY_TYPE, ENTRY_TYPE_VIEW_SHARED_PLAN);
                break;

            case SHARED_LIST_TYPE_TRACK:
                intent.setClass(this, UiTrackMapActivity.class);
                intent.putExtra(BUNDLE_ENTRY_TYPE, ENTRY_TYPE_VIEW_SHARED_TRACK);
                break;
        }

        intent.putExtra(BUNDLE_SHARED_ITEM_ID, id);
        intent.putExtra(BUNDLE_SHARED_ITEM, jsonString);
        startActivity(intent);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        edit_filter.removeTextChangedListener(this);
    }
}
