package com.kingwaytek.cpami.bykingTablet.utilities;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.PopupWindow;

import com.kingwaytek.cpami.bykingTablet.AppController;
import com.kingwaytek.cpami.bykingTablet.R;
import com.kingwaytek.cpami.bykingTablet.app.ui.BaseActivity;

/**
 * 使用 PopupWindow的方法統一在這裡做！
 *
 * @author Vincent (2016/4/18)
 */
public class PopWindowHelper {

    private static PopupWindow popWindow;

    private static Context appContext() {
        return AppController.getInstance().getAppContext();
    }

    public static View getLoadingPopView() {
        LayoutInflater inflater = LayoutInflater.from(appContext());
        View view = inflater.inflate(R.layout.popup_loading_window, null);

        double popWidth = Utility.getScreenWidth() / 2;
        double popHeight = Utility.getScreenHeight() / 4;

        popWindow = new PopupWindow(view, (int)popWidth, (int)popHeight);

        popWindow.setFocusable(true);
        popWindow.setOutsideTouchable(false);

        double xPos = Utility.getScreenWidth() / 2 - popWidth / 2;
        double yPos = Utility.getScreenHeight() / 2 - popWidth;

        if (BaseActivity.getActionbarView() != null)
            popWindow.showAsDropDown(BaseActivity.getActionbarView(), (int) xPos, (int) yPos);

        return view;
    }

    public static void dismissPopWindow() {
        if (popWindow != null)
            popWindow.dismiss();
    }
}
