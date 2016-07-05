package com.kingwaytek.cpami.bykingTablet.app.model;

import com.kingwaytek.cpami.bykingTablet.R;

/**
 * 這裡記錄 Actionbar Menu對應的 ID
 *
 * @author Vincent (2016/5/20)
 */
public interface ActionbarMenu {

    int ACTION_SWITCH = R.id.actionbar_switch;
    int ACTION_AROUND = R.id.actionbar_around;
    int ACTION_ADD = 1 << 1;
    int ACTION_SAVE = 1 << 2;
    int ACTION_EDIT = 1 << 3;
    int ACTION_LIST = 1 << 4;
    int ACTION_DELETE = 1 << 5;
    int ACTION_SHARE = 1 << 6;
}
