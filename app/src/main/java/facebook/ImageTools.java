package facebook;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;

import android.content.Context;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.provider.MediaStore;
import android.util.Log;
import android.widget.ImageView;

public class ImageTools {

	private static final String TAG = "ImageTools";
	private static final String NULL_EXCEPTION = "Null Exception";

	/** Get Bitmap by resource id **/
	public static Bitmap getBitmap(Context context, int resourceId, int scale) {

		Bitmap bitmap = null;

		InputStream inputStream = context.getResources().openRawResource(resourceId);

		bitmap = BitmapFactory.decodeStream(inputStream, null, getBitmapOptions(scale));

		return bitmap;
	}

	/** Get Bitmap from file **/
	public static Bitmap getBitmap(String filePath, int scale) {

		Bitmap bitmap = null;

		File file = new File(filePath);

		if (!file.exists()) {
			return null;
		}

		bitmap = getBitmapFromFile(file, scale);

		return bitmap;
	}

	public static Bitmap getBitmap(File file, int scale) {

		Bitmap bitmap = null;

		if (!file.exists()) {
			return null;
		}

		bitmap = getBitmapFromFile(file, scale);

		return bitmap;
	}

	private static Bitmap getBitmapFromFile(File file, int scale) {

		Bitmap bitmap = null;

		try {

			bitmap = BitmapFactory.decodeStream(new FileInputStream(file), null, getBitmapOptions(scale));

		} catch (FileNotFoundException e) {
			Log.e(TAG, "Exception happened in getBitmapFromFile(): " + printException(e));
		}

		return bitmap;
	}

	/** Get photo path from URI **/
	public static String getPhotoPath(Context context, Uri uri) {

		String path = null;

		Cursor cursor = context.getContentResolver().query(uri, null, null, null, null);

		if (cursor != null) {

			if (cursor.moveToFirst()) {
				path = cursor.getString(cursor.getColumnIndex(MediaStore.Images.ImageColumns.DATA));
			}
		}

		return path;
	}

	/** Setting BitmapOptions **/
	public static BitmapFactory.Options getBitmapOptions(int scale) {
		BitmapFactory.Options options = new BitmapFactory.Options();
		options.inPurgeable = true;
		options.inInputShareable = true;
		options.inSampleSize = scale;
		return options;
	}

	/** Recycle Bitmap **/
	public static void recycleBitmap(ImageView image) {

		BitmapDrawable drawable = (BitmapDrawable) image.getDrawable();

		Bitmap bitmap = null;

		if (drawable != null) {
			bitmap = drawable.getBitmap();
		}

		if (bitmap != null && !bitmap.isRecycled()) {
			bitmap.recycle();
		}

		image.setImageBitmap(null);
	}

	/** Get RGB form Bitmap **/
	public static int[][] getBitmapRGBArray(Bitmap bitmap) {

		int[][] rgbArray = null;

		if (bitmap == null) {
			return null;
		}

		rgbArray = new int[bitmap.getWidth()][bitmap.getHeight()];

		for (int i = 0; i < bitmap.getWidth(); i++) {

			for (int j = 0; j < bitmap.getHeight(); j++) {

				rgbArray[i][j] = bitmap.getPixel(i, j);
			}
		}

		return rgbArray;
	}

	/** Print Exception **/
	private static String printException(Exception e) {

		String exceptionMessage = NULL_EXCEPTION;

		if (e != null) {
			exceptionMessage = e.getMessage().toString();
		}

		return exceptionMessage;
	}

}
