package com.kingwaytek.cpami.bykingTablet.utilities;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;

import com.kingwaytek.cpami.bykingTablet.R;

/**
 * 呼叫 Dialog的方法集中在這裡，<br>
 * 以 static的方法 & DialogBuilder來做！
 *
 * @author Vincent (2016/5/19)
 */
public class DialogHelper {

    private static AlertDialog.Builder dialogBuilder;

    public static void showDeleteConfirmDialog(Context context, String name, DialogInterface.OnClickListener confirmClick) {
        dialogBuilder = new AlertDialog.Builder(context);
        dialogBuilder.setTitle(context.getString(R.string.poi_confirm_to_delete, name));
        dialogBuilder.setCancelable(true);
        dialogBuilder.setPositiveButton(context.getString(R.string.yes), confirmClick);
        dialogBuilder.setNegativeButton(context.getString(R.string.no), null);
        dialogBuilder.create().show();
    }
}
