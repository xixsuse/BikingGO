package com.kingwaytek.cpami.bykingTablet.app.ui.planning;

import android.widget.EditText;
import android.widget.ImageButton;

import com.kingwaytek.api.widget.dslv.DragSortListView;
import com.kingwaytek.cpami.bykingTablet.R;
import com.kingwaytek.cpami.bykingTablet.app.ui.BaseActivity;

/**
 * Created by vincent.chang on 2016/6/1.
 */
public class UiMyPlanEditActivity extends BaseActivity {

    private EditText edit_planTitle;
    private ImageButton planAddBtn;
    private DragSortListView dragSortListView;

    @Override
    protected void init() {

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
        dragSortListView = (DragSortListView) findViewById(R.id.drag_handle);
    }

    @Override
    protected void setListener() {

    }
}
