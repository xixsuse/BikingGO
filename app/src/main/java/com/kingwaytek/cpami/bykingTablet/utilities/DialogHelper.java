package com.kingwaytek.cpami.bykingTablet.utilities;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageButton;
import android.widget.ImageView;

import com.kingwaytek.cpami.bykingTablet.R;

import java.io.File;

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

    public static void showImageViewDialog(final Context context, final String photoPath) {
        if (photoPath.isEmpty())
            return;

        dialogBuilder = new AlertDialog.Builder(context);

        int width = Utility.getScreenWidth();
        int height = Utility.getScreenHeight() - Utility.getActionbarHeight();

        View view = LayoutInflater.from(context).inflate(R.layout.inflate_dialog_image_view, null);

        //LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(width, LinearLayout.LayoutParams.WRAP_CONTENT);
        //view.setLayoutParams(params);

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

        ImageButton closeBtn = (ImageButton) view.findViewById(R.id.dialogCloseBtn);
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
}
