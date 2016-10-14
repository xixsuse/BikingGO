package com.kingwaytek.cpami.bykingTablet.utilities;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Build;
import android.text.Html;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.kingwaytek.cpami.bykingTablet.AppController;
import com.kingwaytek.cpami.bykingTablet.R;

import java.io.File;

/**
 * 使用 PopupWindow的方法統一在這裡做！
 *
 * @author Vincent (2016/4/18)
 */
public class PopWindowHelper {

    private static LayoutInflater inflater;
    private static PopupWindow popWindow;
    private static PopupWindow secondPopWindow;

    private static Context appContext() {
        return AppController.getInstance().getAppContext();
    }

    public static void showLoadingWindow(Context context) {
        dismissPopWindow();

        inflater = LayoutInflater.from(context);
        View view = inflater.inflate(R.layout.popup_loading_window, null);

        double popWidth = Utility.getScreenWidth() / 2;
        double popHeight = Utility.getScreenHeight() / 4;

        popWindow = new PopupWindow(view, (int) popWidth, (int) popHeight);

        setPopWindowCancelable(false);

        //double xPos = Utility.getScreenWidth() / 2 - popWidth / 2;
        //double yPos = Utility.getScreenHeight() / 2 - popWidth;

    }

    public static View getPoiEditWindowView(Context context, View anchorView) {
        dismissPopWindow();

        inflater = LayoutInflater.from(context);
        View view = inflater.inflate(R.layout.popup_poi_edit_window, null);

        double popWidth = Utility.getScreenWidth();
        double popHeight = Utility.getScreenHeight() - Utility.getActionbarHeight();

        popWindow = new PopupWindow(view, (int) popWidth, (int) popHeight);

        popWindow.setTouchable(true);
        popWindow.setFocusable(true);
        popWindow.setOutsideTouchable(false);

        //double xPos = Utility.getScreenWidth() / 2 - popWidth / 2;
        //int yPos = appContext().getResources().getDimensionPixelSize(R.dimen.padding_size_xl);

        popWindow.showAtLocation(anchorView, Gravity.BOTTOM, 0, 0);

        return view;
    }

    public static View getMarkerSwitchWindowView(View anchorView, boolean lineOnly) {
        dismissPopWindow();

        inflater = LayoutInflater.from(appContext());
        View view = inflater.inflate(lineOnly ? R.layout.popup_layer_switch_window_line_only : R.layout.popup_layer_switch_window, null);

        double popWidth = Utility.getScreenWidth();
        double popHeight = Utility.getScreenHeight() - Utility.getActionbarHeight();

        popWindow = new PopupWindow(view, (int) popWidth, (int) popHeight);

        setPopWindowCancelable(true);

        popWindow.showAtLocation(anchorView, Gravity.BOTTOM, 0, 0);

        return view;
    }

    public static View getFullScreenPoiEditView(View anchorView) {
        dismissPopWindow();

        inflater = LayoutInflater.from(appContext());
        View view = inflater.inflate(R.layout.popup_poi_edit_window_full_screen, null);

        double popWidth = Utility.getScreenWidth();
        double popHeight = Utility.getScreenHeight() - Utility.getActionbarHeight();

        popWindow = new PopupWindow(view, (int) popWidth, (int) popHeight);

        setPopWindowCancelable(true);

        popWindow.showAtLocation(anchorView, Gravity.BOTTOM, 0, 0);

        return view;
    }

    public static View getPathListPopWindowView(View anchorView, Context context) {
        dismissPopWindow();

        inflater = LayoutInflater.from(context);
        View view = inflater.inflate(R.layout.popup_path_list, null);

        int planTitleLayoutHeight = appContext().getResources().getDimensionPixelSize(R.dimen.actionbar_height);
        int actionbarHeight = Utility.getActionbarHeight();

        double popWidth = Utility.getScreenWidth();
        double popHeight = Utility.getScreenHeight() - (actionbarHeight + planTitleLayoutHeight);

        RelativeLayout headerLayout = (RelativeLayout) view.findViewById(R.id.pathListHeaderLayout);
        setHeaderLayoutHeight(headerLayout, (int) (popHeight * 0.18));

        popWindow = new PopupWindow(view, (int) popWidth, (int) popHeight, true);

        setPopWindowUnCancelableAndOutsideTouchable(popWindow);

        popWindow.showAtLocation(anchorView, Gravity.TOP|Gravity.START, 0, actionbarHeight);

        return view;
    }

    private static void setHeaderLayoutHeight(RelativeLayout layout, int height) {
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, height);
        layout.setLayoutParams(params);
    }

    /**
     * 呼叫此方法前，必定會先乎叫 getPathListPopWindowView(..)，這兩個方法在同一個 Context內，<br>
     * And inflater was already assigned for this Context, So, don't need to passing Context to this method!
     */
    public static void showPathStepPopWindow(View anchorView, String instruction, final String distance, String goOnPath) {
        dismissPopWindow();

        View view = inflater.inflate(R.layout.popup_path_step, null);

        TextView text_instruction = (TextView) view.findViewById(R.id.text_pathInstruction);
        TextView text_distance = (TextView) view.findViewById(R.id.text_distance);
        TextView text_goOnPath = (TextView) view.findViewById(R.id.text_goOnPath);
        ImageButton closeBtn = (ImageButton) view.findViewById(R.id.pathWindowCloseBtn);

        closeBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dismissPopWindow();
            }
        });

        text_instruction.setText(Html.fromHtml(instruction));
        text_distance.setText(distance);
        text_goOnPath.setText(Html.fromHtml(goOnPath));

        double popWidth = Utility.getScreenWidth() / 1.4;
        double popHeight = (Utility.getScreenHeight() - Utility.getActionbarHeight()) * 0.2;

        secondPopWindow = new PopupWindow(view, (int) popWidth, (int) popHeight);

        setPopWindowUnCancelableAndOutsideTouchable(secondPopWindow);

        int xPos = (int) ((Utility.getScreenWidth() / 2) - (popWidth / 2));

        secondPopWindow.showAsDropDown(anchorView, xPos, 0);
    }

    public static View getSharedRatingWindow(Context context, View anchorView, boolean isPlan) {
        inflater = LayoutInflater.from(context);
        View view = inflater.inflate(R.layout.popup_shared_rating_window, null);

        TextView text_ratingFor = (TextView) view.findViewById(R.id.text_ratingForThis);
        text_ratingFor.setText(isPlan ? appContext().getString(R.string.rating_for_plan) : appContext().getString(R.string.rating_for_track));

        int popWidth = Utility.getScreenWidth();
        int popHeight = Utility.getScreenHeight() - Utility.getActionbarHeight();

        popWindow = new PopupWindow(view, popWidth, popHeight);

        setPopWindowCancelable(true);

        popWindow.showAtLocation(anchorView, Gravity.BOTTOM, 0, 0);

        return view;
    }

    public static void showImageViewWindow(View anchorView, final Context context, final String title, final String photoPath) {
        inflater = LayoutInflater.from(context);

        View view = inflater.inflate(R.layout.popup_image_view, null);

        int popWidth = Utility.getScreenWidth();
        int popHeight = Utility.getScreenHeight() - Utility.getActionbarHeight();

        popWindow = new PopupWindow(view, popWidth, popHeight);

        TextView poiTitle = (TextView) view.findViewById(R.id.dialogPoiTitle);
        final ImageButton closeBtn = (ImageButton) view.findViewById(R.id.dialogCloseBtn);
        final ImageView imageView = (ImageView) view.findViewById(R.id.dialogImageView);

        poiTitle.setText(title);

        Uri imageUri = Uri.fromFile(new File(photoPath));
        Glide.with(context)
                .load(imageUri)
                .placeholder(R.drawable.ic_empty_image)
                .fitCenter()
                .into(imageView);

        /*
        int imageHeight = popHeight - (
                context.getResources().getDimensionPixelSize(R.dimen.padding_size_l) +
                        context.getResources().getDimensionPixelSize(R.dimen.padding_size_m) +
                        context.getResources().getDimensionPixelSize(R.dimen.icon_common_size));

        final Bitmap bitmap = BitmapUtility.getDecodedBitmapInFullWidth(photoPath, imageHeight);
        imageView.setImageBitmap(bitmap);
        */

        imageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent();
                intent.setAction(Intent.ACTION_VIEW);
                intent.setDataAndType(Uri.fromFile(new File(photoPath)), "image/*");
                context.startActivity(intent);
            }
        });

        closeBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                closeBtn.setOnClickListener(null);
                //if (bitmap != null)
                //    bitmap.recycle();
                dismissPopWindow();
            }
        });

        setPopWindowCancelable(true);
        popWindow.showAtLocation(anchorView, Gravity.BOTTOM, 0, 0);
    }

    private static void setPopWindowCancelable(boolean isCancelable) {
        if (isCancelable) {
            popWindow.setFocusable(true);
            popWindow.setOutsideTouchable(true);
            popWindow.setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        }
        else if (Build.VERSION.SDK_INT < 23) {
            popWindow.setTouchable(true);
            popWindow.setFocusable(true);
            popWindow.setOutsideTouchable(false);
        }
        else
            setPopWindowUnCancelableAndOutsideTouchable(popWindow);
    }

    private static void setPopWindowUnCancelableAndOutsideTouchable(PopupWindow popupWindow) {
        popupWindow.setTouchable(true);
        popupWindow.setFocusable(false);
        popupWindow.setOutsideTouchable(false);
    }

    public static boolean isPopWindowShowing() {
        return popWindow != null && popWindow.isShowing();
    }

    public static void dismissPopWindow() {
        if (isPopWindowShowing())
            popWindow.dismiss();

        if (secondPopWindow != null)
            secondPopWindow.dismiss();
    }
}
