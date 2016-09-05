package com.kingwaytek.cpami.bykingTablet.app.ui.planning;

import android.text.Editable;
import android.text.TextWatcher;
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;
import android.widget.ListView;

import com.kingwaytek.cpami.bykingTablet.R;
import com.kingwaytek.cpami.bykingTablet.app.model.DataArray;
import com.kingwaytek.cpami.bykingTablet.app.ui.BaseActivity;
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

    @Override
    protected void init() {
        getListTypeAndGetData();
        setEditText();
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
    }

    private void getListTypeAndGetData() {
        listType = getIntent().getIntExtra(BUNDLE_SHARED_LIST_TYPE, SHARED_LIST_TYPE_PLAN);
        String postTypeValue = listType == SHARED_LIST_TYPE_PLAN ? POST_VALUE_TYPE_PLAN : POST_VALUE_TYPE_TRACK;

        DialogHelper.showLoadingDialog(this);

        WebAgent.getListFromBikingService(postTypeValue, new WebAgent.WebResultImplement() {
            @Override
            public void onResultSucceed(String response) {
                sharedListView.setAdapter(new SharedListAdapter(UiSharedListActivity.this, DataArray.getSharedList(response)));
                DialogHelper.dismissDialog();
            }

            @Override
            public void onResultFail(String errorMessage) {
                Utility.toastShort(errorMessage);
                DialogHelper.dismissDialog();
            }
        });
    }

    private void setEditText() {
        edit_filter.setSingleLine();
        edit_filter.setImeOptions(EditorInfo.IME_ACTION_DONE);
    }

    @Override
    public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

    @Override
    public void onTextChanged(CharSequence s, int start, int before, int count) {

    }

    @Override
    public void afterTextChanged(Editable s) {}

    @Override
    public void onDestroy() {
        super.onDestroy();
        edit_filter.removeTextChangedListener(this);
    }
}
