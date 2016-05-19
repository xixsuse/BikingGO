package com.kingwaytek.cpami.bykingTablet.utilities;

import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.view.Gravity;
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

    private static LayoutInflater inflater;
    private static PopupWindow popWindow;

    private static Context appContext() {
        return AppController.getInstance().getAppContext();
    }

    public static void showLoadingWindow() {
        inflater = LayoutInflater.from(appContext());
        View view = inflater.inflate(R.layout.popup_loading_window, null);

        double popWidth = Utility.getScreenWidth() / 2;
        double popHeight = Utility.getScreenHeight() / 4;

        popWindow = new PopupWindow(view, (int) popWidth, (int) popHeight);

        popWindow.setFocusable(true);
        popWindow.setOutsideTouchable(false);

        double xPos = Utility.getScreenWidth() / 2 - popWidth / 2;
        double yPos = Utility.getScreenHeight() / 2 - popWidth;

        if (BaseActivity.getActionbarView() != null)
            popWindow.showAsDropDown(BaseActivity.getActionbarView(), (int) xPos, (int) yPos);
    }

    public static View getPoiEditWindowView(Context context, View anchorView) {
        inflater = LayoutInflater.from(context);
        View view = inflater.inflate(R.layout.popup_poi_edit_window, null);

        double popWidth = Utility.getScreenWidth() / 1.1;
        double popHeight = Utility.getScreenHeight() / 1.2;

        popWindow = new PopupWindow(view, (int) popWidth, (int) popHeight);

        popWindow.setFocusable(true);
        popWindow.setOutsideTouchable(false);

        //double xPos = Utility.getScreenWidth() / 2 - popWidth / 2;
        int yPos = Utility.getActionbarHeight();

        popWindow.showAtLocation(anchorView,Gravity.CENTER_HORIZONTAL, 0, yPos);

        return view;
    }

    public static View getMarkerSwitchWindowView(View anchorView) {
        inflater = LayoutInflater.from(appContext());
        View view = inflater.inflate(R.layout.popup_marker_switch_window, null);

        double popWidth = Utility.getScreenWidth() / 1.4;
        double popHeight = Utility.getScreenHeight() / 1.5;

        popWindow = new PopupWindow(view, (int) popWidth, (int) popHeight);

        popWindow.setFocusable(true);
        popWindow.setOutsideTouchable(true);
        popWindow.setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));

        int yPos = appContext().getResources().getDimensionPixelSize(R.dimen.padding_size_xxl) +
                appContext().getResources().getDimensionPixelSize(R.dimen.padding_size_xl);

        popWindow.showAtLocation(anchorView, Gravity.CENTER, 0, yPos);

        return view;
    }

    public static void dismissPopWindow() {
        if (popWindow != null)
            popWindow.dismiss();
    }
}
