package com.kingwaytek.cpami.bykingTablet.utilities;

import android.Manifest;
import android.app.Activity;
import android.content.DialogInterface;
import android.content.pm.PackageManager;
import android.os.Build;
import android.support.v4.app.ActivityCompat;

/**
 * for the Android version above 6.0 (API level 23)，<br>
 * 需要 runTime permissions check，<br>
 * 搭配在 Base*Activity中的 onRequestPermissionsResult使用！
 *
 * @author Vincent (2016/5/27)
 */
public class PermissionCheckHelper {

    public static final int PERMISSION_REQUEST_CODE_LOCATION = 100;
    public static final int PERMISSION_REQUEST_CODE_GALLERY = 200;
    public static final int PERMISSION_REQUEST_CODE_CAMERA = 300;

    public static boolean checkLocationPermissions(final Activity activity) {
        if (ActivityCompat.checkSelfPermission(activity, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED)
        {
            if (ActivityCompat.shouldShowRequestPermissionRationale(activity, Manifest.permission.ACCESS_FINE_LOCATION)) {
                DialogHelper.showLocationPermissionRationaleDialog(activity, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        requestLocationPermission(activity, PERMISSION_REQUEST_CODE_LOCATION);
                    }
                });
            }
            else
                requestLocationPermission(activity, PERMISSION_REQUEST_CODE_LOCATION);

            return false;
        }
        else
            return true;
    }

    private static void requestLocationPermission(Activity activity, int requestCode) {
        ActivityCompat.requestPermissions(activity,
                new String[]{Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION}, requestCode);
    }

    public static boolean checkGalleryAndCameraPermissions(final Activity activity, final int requestCode) {
        if (ActivityCompat.checkSelfPermission(activity, Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED ||
                ActivityCompat.checkSelfPermission(activity, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED)
        {
            if (ActivityCompat.shouldShowRequestPermissionRationale(activity, Manifest.permission.READ_EXTERNAL_STORAGE) ||
                    ActivityCompat.shouldShowRequestPermissionRationale(activity, Manifest.permission.CAMERA)) {
                DialogHelper.showPhotoPermissionRationaleDialog(activity, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        requestPhotoPermission(activity, requestCode);
                    }
                });
            }
            else
                requestPhotoPermission(activity, requestCode);

            return false;
        }
        else
            return true;
    }

    private static void requestPhotoPermission(Activity activity, int requestCode) {
        String[] permissionArray;
        if (Build.VERSION.SDK_INT > 15)
            permissionArray = new String[]{Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.CAMERA};
        else
            permissionArray = new String[]{Manifest.permission.CAMERA};

        ActivityCompat.requestPermissions(activity, permissionArray, requestCode);
    }
}
