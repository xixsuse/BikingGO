package com.kingwaytek.cpami.bykingTablet.utilities;

import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
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

    public static void showLoadingWindow(Context context) {
        inflater = LayoutInflater.from(context);
        View view = inflater.inflate(R.layout.popup_loading_window, null);

        double popWidth = Utility.getScreenWidth() / 2;
        double popHeight = Utility.getScreenHeight() / 4;

        popWindow = new PopupWindow(view, (int) popWidth, (int) popHeight);

        setPopWindowCancelable(false);

        //double xPos = Utility.getScreenWidth() / 2 - popWidth / 2;
        //double yPos = Utility.getScreenHeight() / 2 - popWidth;

        if (BaseActivity.getWindowView() != null)
            popWindow.showAtLocation(BaseActivity.getWindowView(), Gravity.CENTER, 0, 0);
    }

    public static View getPoiEditWindowView(Context context, View anchorView) {
        inflater = LayoutInflater.from(context);
        View view = inflater.inflate(R.layout.popup_poi_edit_window, null);

        double popWidth = Utility.getScreenWidth() / 1.1;
        double popHeight = Utility.getScreenHeight() / 1.5;

        popWindow = new PopupWindow(view, (int) popWidth, (int) popHeight);

        setPopWindowCancelable(false);

        double xPos = Utility.getScreenWidth() / 2 - popWidth / 2;
        int yPos = appContext().getResources().getDimensionPixelSize(R.dimen.padding_size_xl);

        popWindow.showAsDropDown(anchorView, (int) xPos, yPos);

        return view;
    }

    public static View getMarkerSwitchWindowView(View anchorView) {
        inflater = LayoutInflater.from(appContext());
        View view = inflater.inflate(R.layout.popup_marker_switch_window, null);

        double popWidth = Utility.getScreenWidth() / 1.4;
        double popHeight = Utility.getScreenHeight() / 1.5;

        popWindow = new PopupWindow(view, (int) popWidth, (int) popHeight);

        setPopWindowCancelable(true);

        double xPos = Utility.getScreenWidth() / 2 - popWidth / 2;
        int yPos = appContext().getResources().getDimensionPixelSize(R.dimen.padding_size_xl);

        popWindow.showAsDropDown(anchorView, (int) xPos, yPos);

        return view;
    }

    public static View getFullScreenPoiEditView(View anchorView) {
        inflater = LayoutInflater.from(appContext());
        View view = inflater.inflate(R.layout.popup_poi_edit_window, null);

        double popWidth = Utility.getScreenWidth();
        double popHeight = Utility.getScreenHeight() - Utility.getActionbarHeight();

        popWindow = new PopupWindow(view, (int) popWidth, (int) popHeight);

        setPopWindowCancelable(true);

        popWindow.showAtLocation(anchorView, Gravity.BOTTOM, 0, 0);

        return view;
    }

    public static View getPathListPopWindowView(View anchorView, Context context) {
        inflater = LayoutInflater.from(context);
        View view = inflater.inflate(R.layout.popup_path_list, null);

        double popWidth = Utility.getScreenWidth() / 2.2;
        double popHeight = Utility.getScreenHeight() - Utility.getActionbarHeight();

        //FrameLayout headerLayout = (FrameLayout) view.findViewById(R.id.pathListHeaderLayout);
        //setHeaderLayoutHeight(headerLayout, (int) (popHeight * 0.25));

        popWindow = new PopupWindow(view, (int) popWidth, (int) popHeight, true);

        popWindow.setTouchable(true);
        popWindow.setFocusable(false);
        popWindow.setOutsideTouchable(false);

        popWindow.showAtLocation(anchorView, Gravity.BOTTOM|Gravity.START, 0, 0);
        //popWindow.update(0, 0, (int) popWidth, (int) popHeight, true);

        return view;
    }

    private static void setHeaderLayoutHeight(FrameLayout layout, int height) {
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, height);
        layout.setLayoutParams(params);
    }

    private static void setPopWindowCancelable(boolean isCancelable) {
        if (isCancelable) {
            popWindow.setFocusable(true);
            popWindow.setOutsideTouchable(true);
            popWindow.setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        }
        else {
            popWindow.setTouchable(true);
            popWindow.setFocusable(true);
            popWindow.setOutsideTouchable(false);
        }
    }

    public static boolean isPopWindowShowing() {
        return popWindow != null && popWindow.isShowing();
    }

    public static void dismissPopWindow() {
        if (popWindow != null)
            popWindow.dismiss();
    }
}
