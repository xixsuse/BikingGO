package com.kingwaytek.cpami.bykingTablet.utilities;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Build;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.TimePicker;

import com.kingwaytek.cpami.bykingTablet.R;
import com.kingwaytek.cpami.bykingTablet.app.model.CommonBundle;
import com.kingwaytek.cpami.bykingTablet.utilities.adapter.DialogItemsAdapter;

import java.io.File;

/**
 * 呼叫 Dialog的方法集中在這裡，<br>
 * 以 static的方法 & DialogBuilder來做！
 *
 * @author Vincent (2016/5/19)
 */
public class DialogHelper {

    private static AlertDialog.Builder dialogBuilder;
    private static Dialog dialog;

    public interface OnTimeSetCallBack {
        void onTimeSet(String year, String month, String day, String hours, String minutes);
    }

    public static void showLoadingDialog(Context context) {
        dialogBuilder = new AlertDialog.Builder(context);

        View view = LayoutInflater.from(context).inflate(R.layout.popup_loading_window, null);
        dialogBuilder.setView(view);
        dialogBuilder.setCancelable(false);

        dialog = dialogBuilder.create();
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        dialog.getWindow().setDimAmount(0.0f);

        dialog.show();
    }

    public static void dismissDialog() {
        if (dialog != null && dialog.isShowing())
            dialog.dismiss();
    }

    public static void showDeleteConfirmDialog(Context context, String name, DialogInterface.OnClickListener confirmClick) {
        dialogBuilder = new AlertDialog.Builder(context);
        dialogBuilder.setTitle(context.getString(R.string.poi_confirm_to_delete, name));
        dialogBuilder.setCancelable(true);
        dialogBuilder.setPositiveButton(context.getString(R.string.yes), confirmClick);
        dialogBuilder.setNegativeButton(context.getString(R.string.no), null);
        dialogBuilder.create().show();
    }

    public static void showDeleteConfirmDialog(Context context, DialogInterface.OnClickListener confirmClick) {
        dialogBuilder = new AlertDialog.Builder(context);
        dialogBuilder.setTitle(context.getString(R.string.confirm_to_delete_selected_items));
        dialogBuilder.setCancelable(true);
        dialogBuilder.setPositiveButton(context.getString(R.string.yes), confirmClick);
        dialogBuilder.setNegativeButton(context.getString(R.string.no), null);
        dialogBuilder.create().show();
    }

    public static void showImageViewDialog(final Context context, final String title, final String photoPath) {
        if (photoPath.isEmpty())
            return;

        dialogBuilder = new AlertDialog.Builder(context);

        int width = Utility.getScreenWidth();
        int height = Utility.getScreenHeight() - Utility.getActionbarHeight();

        View view = LayoutInflater.from(context).inflate(R.layout.inflate_dialog_image_view, null);

        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(width, height);
        view.setLayoutParams(params);

        dialogBuilder.setView(view);

        final AlertDialog dialog = dialogBuilder.create();

        Window dialogWindow = dialog.getWindow();
        WindowManager.LayoutParams windowParams = dialogWindow.getAttributes();
        windowParams.alpha = 0.9f;
        windowParams.width = width;
        windowParams.height = height;
        windowParams.gravity = Gravity.CENTER;

        dialog.setCanceledOnTouchOutside(true);
        dialog.show();

        TextView poiTitle = (TextView) view.findViewById(R.id.dialogPoiTitle);
        ImageButton closeBtn = (ImageButton) view.findViewById(R.id.dialogCloseBtn);

        poiTitle.setText(title);

        final ImageView image = (ImageView) view.findViewById(R.id.dialogImageView);

        final Bitmap bitmap = BitmapFactory.decodeFile(photoPath);
        image.setImageBitmap(bitmap);

        image.setOnClickListener(new View.OnClickListener() {
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
                dialog.dismiss();
            }
        });

        dialog.setOnDismissListener(new DialogInterface.OnDismissListener() {
            @Override
            public void onDismiss(DialogInterface dialog) {
                image.setOnClickListener(null);
                bitmap.recycle();
            }
        });
    }

    public static void showDialogPhotoMenu(Context context, boolean hasRemoveOption, DialogInterface.OnClickListener onClickListener) {
        String[] items;
        if (hasRemoveOption)
            items = context.getResources().getStringArray(R.array.dialog_photo_menu_has_remove);
        else
            items = context.getResources().getStringArray(R.array.dialog_photo_menu);

        dialogBuilder = new AlertDialog.Builder(context);

        dialogBuilder.setAdapter(new DialogItemsAdapter(context, items, CommonBundle.SELECT_TYPE_PHOTO), onClickListener);

        //dialogBuilder.setItems(items, onClickListener);
        dialogBuilder.setCancelable(true);

        dialog = dialogBuilder.create();
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        dialog.show();
    }

    public static void showLocationSelectMenu(Context context, DialogInterface.OnClickListener onClickListener) {
        String[] items = context.getResources().getStringArray(R.array.location_select_array);

        dialogBuilder = new AlertDialog.Builder(context);

        dialogBuilder.setAdapter(new DialogItemsAdapter(context, items, CommonBundle.SELECT_TYPE_POSITION), onClickListener);
        dialogBuilder.setCancelable(true);

        dialog = dialogBuilder.create();
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        dialog.show();
    }

    public static void showLocationPermissionRationaleDialog(Context context, DialogInterface.OnClickListener positiveClick) {
        dialogBuilder = new AlertDialog.Builder(context);
        dialogBuilder.setTitle(context.getString(R.string.location_permission_rationale_title));
        dialogBuilder.setMessage(context.getString(R.string.location_permission_rationale_content));
        dialogBuilder.setCancelable(false);
        dialogBuilder.setPositiveButton(context.getString(R.string.confirm), positiveClick);
        dialogBuilder.create().show();
    }

    public static void showPhotoPermissionRationaleDialog(Context context, DialogInterface.OnClickListener positiveClick) {
        dialogBuilder = new AlertDialog.Builder(context);
        dialogBuilder.setTitle(context.getString(R.string.camera_permission_rationale_title));
        dialogBuilder.setMessage(context.getString(R.string.camera_permission_rationale_content));
        dialogBuilder.setCancelable(false);
        dialogBuilder.setPositiveButton(context.getString(R.string.confirm), positiveClick);
        dialogBuilder.create().show();
    }

    public static void showStoragePermissionRationaleDialog(Context context, DialogInterface.OnClickListener positiveClick) {
        dialogBuilder = new AlertDialog.Builder(context);
        dialogBuilder.setTitle(context.getString(R.string.storage_permission_rationale_title));
        dialogBuilder.setMessage(context.getString(R.string.storage_permission_rationale_content));
        dialogBuilder.setCancelable(false);
        dialogBuilder.setPositiveButton(context.getString(R.string.confirm), positiveClick);
        dialogBuilder.create().show();
    }

    public static void showPickersDialog(Context context, boolean plusTenMinutes, final OnTimeSetCallBack timeSetCallBack) {
        dialogBuilder = new AlertDialog.Builder(context);
        View view = LayoutInflater.from(context).inflate(R.layout.inflate_date_and_time_pickers, null);

        final DatePicker datePicker = (DatePicker) view.findViewById(R.id.picker_date);
        final TimePicker timePicker = (TimePicker) view.findViewById(R.id.picker_time);
        final Button btn_confirm = (Button) view.findViewById(R.id.btn_confirm);
        final Button btn_cancel = (Button) view.findViewById(R.id.btn_cancel);

        long aMonth = (1000L * 60 * 60 * 24) * 30;
        long aMonthLater = System.currentTimeMillis() + aMonth;
        long aMonthAgo = System.currentTimeMillis() - aMonth;

        datePicker.setMaxDate(aMonthLater);
        datePicker.setMinDate(aMonthAgo);

        if (plusTenMinutes) {
            int totalMinutes;
            if (Build.VERSION.SDK_INT >= 23)
                totalMinutes = (timePicker.getHour() * 60) + timePicker.getMinute();
            else
                totalMinutes = (timePicker.getCurrentHour() * 60) + timePicker.getCurrentMinute();

            totalMinutes += 10;

            int hours = totalMinutes / 60;
            int minutes = totalMinutes % 60;

            if (Build.VERSION.SDK_INT >= 23) {
                timePicker.setHour(hours);
                timePicker.setMinute(minutes);
            }
            else {
                timePicker.setCurrentHour(hours);
                timePicker.setCurrentMinute(minutes);
            }
        }

        btn_cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (dialog != null)
                    dialog.dismiss();

                btn_cancel.setOnClickListener(null);
                btn_confirm.setOnClickListener(null);
            }
        });

        btn_confirm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String year = String.valueOf(datePicker.getYear());
                String month = String.valueOf(datePicker.getMonth());
                String day = String.valueOf(datePicker.getDayOfMonth());
                String hours;
                String minutes;

                if (Build.VERSION.SDK_INT >= 23) {
                    hours = String.valueOf(timePicker.getHour());
                    minutes = String.valueOf(timePicker.getMinute());
                }
                else {
                    hours = String.valueOf(timePicker.getCurrentHour());
                    minutes = String.valueOf(timePicker.getCurrentMinute());
                }

                timeSetCallBack.onTimeSet(year, month, day, hours, minutes);

                dialog.dismiss();
                btn_cancel.setOnClickListener(null);
                btn_confirm.setOnClickListener(null);
            }
        });

        dialogBuilder.setView(view);

        dialog = dialogBuilder.create();
        dialog.show();
    }
}
