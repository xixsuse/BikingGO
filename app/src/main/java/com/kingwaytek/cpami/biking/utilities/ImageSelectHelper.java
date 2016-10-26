package com.kingwaytek.cpami.biking.utilities;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;

import com.kingwaytek.cpami.biking.app.model.CommonBundle;
import com.kingwaytek.cpami.biking.app.ui.BaseActivity;
import com.kingwaytek.cpami.biking.callbacks.OnPhotoRemovedCallBack;

/**
 * Intent Actions for selecting images & <br>
 * Methods of getting photo's file path from URI...
 *
 * 以上那些有點雜亂，所以都集中在這裡做！
 *
 * @author Vincent (2016/5/24)
 */
public class ImageSelectHelper implements CommonBundle {

    private static final String TAG = "ImageSelectHelper";

    private static final int SELECT_PHOTO_BY_CAMERA = 0;
    private static final int SELECT_PHOTO_BY_GALLERY = 1;
    private static final int SELECT_PHOTO_REMOVE = 2;

    /**
     * @param photoRemovedCallBack 設為 null 的話可移除 SELECT_PHOTO_REMOVE的選項！
     */
    public static View.OnClickListener getImageClick(final Activity activity, final OnPhotoRemovedCallBack photoRemovedCallBack) {
        return new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                boolean hasRemoveOption = photoRemovedCallBack != null;

                DialogHelper.showDialogPhotoMenu(activity, hasRemoveOption, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        switch (which) {
                            case SELECT_PHOTO_BY_CAMERA:
                                BaseActivity.launchCamera(activity);
                                break;

                            case SELECT_PHOTO_BY_GALLERY:
                                BaseActivity.launchGallery(activity);
                                break;

                            case SELECT_PHOTO_REMOVE:
                                if (photoRemovedCallBack != null)
                                    photoRemovedCallBack.onPhotoRemoved();
                                break;
                        }
                    }
                });
            }
        };
    }

    @SuppressWarnings("WrongConstant")
    @SuppressLint("NewApi")
    public static String getPhotoPath(Context context, int requestCode, Intent data) {
        Uri uri = data.getData();
        Log.i(TAG, "ImageContentPath: " + uri.toString());

        // TODO Parse Uri to real path if photo source is from Google Drive.
        boolean isFromGoogleDrive = uri.toString().contains("com.google.android.apps.docs.storage");

        String photoPath = "";

        switch (requestCode) {
            case REQUEST_PHOTO_FROM_GALLERY:
            case REQUEST_PHOTO_FROM_CAMERA:

                if (Build.VERSION.SDK_INT < 19 || requestCode == REQUEST_PHOTO_FROM_CAMERA || isFromGoogleDrive) {
                    String[] projection = {MediaStore.Images.Media.DATA};
                    Cursor cursor = context.getContentResolver().query(uri, projection, null, null, null);

                    if (cursor != null) {
                        cursor.moveToFirst();
                        int columnIndex = cursor.getColumnIndex(MediaStore.Images.ImageColumns.DATA);
                        photoPath = cursor.getString(columnIndex);

                        Log.i(TAG, "ImageFilePath: " + photoPath);
                        cursor.close();
                    }
                    if (cursor == null || photoPath == null) {
                        photoPath = uri.getPath();
                        Log.i(TAG, "CursorNull ImagePath: " + photoPath);
                    }
                }
                else {
                    final int takeFlags = data.getFlags() & (Intent.FLAG_GRANT_READ_URI_PERMISSION | Intent.FLAG_GRANT_WRITE_URI_PERMISSION);
                    context.getContentResolver().takePersistableUriPermission(uri, takeFlags);

                    Log.i(TAG, "UriLastPathSegment: " + uri.getLastPathSegment());
                    final String id = uri.getLastPathSegment().split(":")[1];
                    final String[] imageColumns = {MediaStore.Images.Media.DATA};
                    final String imageOrderBy = null;

                    Uri storageUri = getStorageUri();

                    Cursor imageCursor = context.getContentResolver().query(storageUri, imageColumns, MediaStore.Images.Media._ID + "="+id, null, imageOrderBy);

                    if (imageCursor != null && imageCursor.moveToFirst()) {
                        photoPath = imageCursor.getString(imageCursor.getColumnIndex(MediaStore.Images.Media.DATA));
                        Log.i(TAG, "ImageFilePath: " + photoPath);

                        imageCursor.close();
                    }
                }
                break;
        }
        return photoPath;
    }

    private static Uri getStorageUri() {
        String state = Environment.getExternalStorageState();

        if (state.equalsIgnoreCase(Environment.MEDIA_MOUNTED))
            return MediaStore.Images.Media.EXTERNAL_CONTENT_URI;
        else
            return MediaStore.Images.Media.INTERNAL_CONTENT_URI;
    }
}
