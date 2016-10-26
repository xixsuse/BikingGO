package com.kingwaytek.cpami.biking.utilities;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.drawable.Drawable;
import android.media.ExifInterface;
import android.net.Uri;
import android.os.Handler;
import android.util.Log;

import com.kingwaytek.cpami.biking.AppController;
import com.kingwaytek.cpami.biking.R;

import java.io.File;
import java.io.IOException;

/**
 * get inSampleSize decoded bitmap & image rotation determine的各種方法...<br>
 * 不小心越寫越多，所以就都集中放到這裡！
 *
 * @author Vincent (2016/5/27)
 */
public class BitmapUtility {

    private static final String TAG = "BitmapUtility";

    public interface OnBitmapDecodedCallback {
        void onDecodeCompleted(Bitmap bitmap);
    }

    public static void getDecodedBitmapInFullWidth(final String imgPath, final int imageViewHeight, final Handler uiHandler,
                                                   final OnBitmapDecodedCallback decodeCallback)
    {
        if (Utility.isFileNotExists(imgPath))
            return;

        new Thread() {
            @Override
            public void run() {
                int rotation = getPhotoOrientation(imgPath);
                Log.i("DecodeBitmap", "rotation: " + rotation);

                int reqWidth = Utility.getScreenWidth();
                Log.i("DecodeBitmapInFullWidth", "screenWidth: " + reqWidth + " ImageViewHeight: " + imageViewHeight);

                final BitmapFactory.Options options = new BitmapFactory.Options();
                options.inJustDecodeBounds = true;

                BitmapFactory.decodeFile(imgPath, options);

                int imgWidth = options.outWidth;
                int imgHeight = options.outHeight;
                String imgType = options.outMimeType;
                Log.i("ImageInfo", imgType + " " + imgWidth + " x " + imgHeight);

                if (imgHeight > imageViewHeight) {
                    double multiple = (double) imgHeight / (double) imageViewHeight;
                    reqWidth = (int) ((double) imgWidth / multiple);
                    Log.i("DecodeBitmapInFullWidth", "multiple: " + multiple + " reqWidth: " + reqWidth);
                }
                else if (imgWidth < reqWidth)
                    reqWidth = imgWidth;

                options.inSampleSize = getInSampleSize(options, reqWidth, imageViewHeight);

                options.inJustDecodeBounds = false;
                Bitmap imageInSampleSize = BitmapFactory.decodeFile(imgPath, options);

                Log.i("DecodedImage", "SampleSize: " + options.inSampleSize);

                Bitmap photoBitmap;

                if (imageInSampleSize == null)
                    photoBitmap = BitmapFactory.decodeResource(AppController.getInstance().getResources(), R.drawable.ic_empty_image);
                else {
                    photoBitmap = createScaleBitmap(imageInSampleSize, reqWidth, imageViewHeight);
                    if (isRotateNeeded(rotation))
                        photoBitmap = getRotatedPhoto(photoBitmap, rotation, reqWidth, imageViewHeight);
                }

                final Bitmap finalPhotoBitmap = photoBitmap;

                uiHandler.post(new Runnable() {
                    @Override
                    public void run() {
                        decodeCallback.onDecodeCompleted(finalPhotoBitmap);
                    }
                });
            }
        }.start();
    }

    /**
     * 2016/09/09 Updated:
     * LruCache used, that has applied in Activities.
     */
    public static Bitmap  getDecodedBitmap(String imgPath, int reqWidth, int reqHeight) {
        Log.i("DecodeBitmap", "reqWidth: " + reqWidth + " reqHeight: " + reqHeight);

        if (Utility.isFileNotExists(imgPath))
            return null;

        int rotation = getPhotoOrientation(imgPath);
        Log.i("DecodeBitmap", "rotation: " + rotation);

        final BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds = true;			// inJustDecodeBounds = true 時，就可以直接抓出圖片屬性，而不用整張都載入

        BitmapFactory.decodeFile(imgPath, options);

        int imgWidth = options.outWidth;		//獲得來源 Image 的寬度 長度 & Type
        int imgHeight = options.outHeight;
        String imgType = options.outMimeType;	//這一段只是為了把資訊Log出來，其實可以不用寫~
        Log.i("ImageInfo", imgType + " " + imgWidth + " x " + imgHeight);
/*
        if (isRotateNeeded(rotation)) {
            options.outWidth = imgHeight;
            options.outHeight = imgWidth;
        }
*/
        options.inSampleSize = getInSampleSize(options, reqWidth, reqHeight);	//藉由 getInSampleSize，指定 inSampleSize 的數值

        options.inJustDecodeBounds = false;	//屬性抓完了，就可以把 inJustDecodeBounds 給關掉了~
        Bitmap imageInSampleSize = BitmapFactory.decodeFile(imgPath, options);	//這時後 options 中的數值是已經被重新指定過了喔！

        Log.i("DecodedImage", "SampleSize: " + options.inSampleSize);

        Bitmap photoBitmap;

        if (imageInSampleSize == null)
            photoBitmap = BitmapFactory.decodeResource(AppController.getInstance().getResources(), R.drawable.ic_empty_image);
        else {
            photoBitmap = createScaleBitmap(imageInSampleSize, reqWidth, reqHeight);
            if (isRotateNeeded(rotation))
                photoBitmap = getRotatedPhoto(photoBitmap, rotation, reqWidth, reqHeight);
        }

        return photoBitmap;
    }

    private static int getInSampleSize(BitmapFactory.Options options, int reqWidth, int reqHeight) {
        final int width = options.outWidth;			//來源 Image 的寬度&高度~
        final int height = options.outHeight;
        int inSampleSize = 1;

        if (width > reqWidth || height > reqHeight)	//如果來源的長寬 大於 Require 的話...
        {
            final int halfWidth = width / 2;		//就除一半阿~
            final int halfHeight = height / 2;

            while ((halfWidth / inSampleSize) > reqWidth && (halfHeight / inSampleSize) > reqHeight) {
                inSampleSize *= 2;
            }
        }
        return inSampleSize;
    }

    private static Bitmap createScaleBitmap(Bitmap image, int dstWidth, int dstHeight) {
        Bitmap scaledImg = Bitmap.createScaledBitmap(image, dstWidth, dstHeight, false);
        if (image != scaledImg) {
            image.recycle();
            Log.i("DecodedImage", "ScaledWidth: " + scaledImg.getWidth() + " ScaledHeight: " + scaledImg.getHeight());
            return scaledImg;
        }
        else {
            scaledImg.recycle();
            Log.i("DecodedImage", "Width: " + scaledImg.getWidth() + " Height: " + scaledImg.getHeight());
            return image;
        }
    }

    private static int getPhotoOrientation(String photoPath) {
        int rotate = 0;

        File photoFile = new File(photoPath);

        AppController.getInstance().getAppContext().getContentResolver().notifyChange(Uri.fromFile(photoFile), null);

        try {
            ExifInterface exif = new ExifInterface(photoFile.getAbsolutePath());

            int orientation = exif.getAttributeInt(ExifInterface.TAG_ORIENTATION, ExifInterface.ORIENTATION_NORMAL);

            switch (orientation) {
                case ExifInterface.ORIENTATION_ROTATE_270:
                    rotate = 270;
                    break;
                case ExifInterface.ORIENTATION_ROTATE_180:
                    rotate = 180;
                    break;
                case ExifInterface.ORIENTATION_ROTATE_90:
                    rotate = 90;
                    break;
            }

            Log.i("RotateImage", "Exif orientation: " + orientation);
            Log.i("RotateImage", "Rotate value: " + rotate);
        }
        catch (IOException e) {
            e.printStackTrace();
        }
        return rotate;
    }

    private static boolean isRotateNeeded(int rotation) {
        return rotation == 90 || rotation == 180 || rotation == 270;
    }

    private static Bitmap getRotatedPhoto(Bitmap srcBitmap, int rotate, int reqWidth, int reqHeight) {
        Matrix matrix = new Matrix();
        matrix.postRotate(rotate);

        Bitmap photoBitmap = Bitmap.createBitmap(srcBitmap, 0, 0, reqWidth, reqHeight, matrix, true);
        srcBitmap.recycle();

        return photoBitmap;
    }

    public static Bitmap convertDrawableToBitmap(Drawable drawable, int iconSize) {
        Log.i(TAG, "iconSize: " + iconSize);

        Bitmap bitmap = Bitmap.createBitmap(iconSize, iconSize, Bitmap.Config.ARGB_8888);
        Log.i(TAG, "drawableIntrinsicWidth: " + drawable.getIntrinsicWidth() + " drawableIntrinsicHeight: " + drawable.getIntrinsicHeight());

        Canvas canvas = new Canvas(bitmap);
        drawable.setBounds(0, 0, iconSize, iconSize);
        Log.i(TAG, "canvasWidth: " + canvas.getWidth() + " canvasHeight: " + canvas.getHeight());

        drawable.draw(canvas);

        return bitmap;
    }
}
