package com.kingwaytek.cpami.bykingTablet.app;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;

import android.graphics.Bitmap;
import android.graphics.Bitmap.CompressFormat;
import android.graphics.BitmapFactory;
import android.util.Log;

/**
 * Image Conversion Utility
 * 
 * @author Andy Chiao (andy.chiao@kingwaytek.com)
 */
public class ImageUtil {

	public static byte[] JpegToBlob(String imgFile)
			throws FileNotFoundException {
		if (imgFile == null || imgFile.equals("")) {
			return null;
		}

		File imagefile = new File(imgFile);
		FileInputStream fis = new FileInputStream(imagefile);
		Bitmap bmp = BitmapFactory.decodeStream(fis);

		return BitmapToBlob(bmp);
	}

	public static byte[] BitmapToBlob(Bitmap bmpSource) {
		if (bmpSource == null)
			return null;

		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		bmpSource.compress(Bitmap.CompressFormat.JPEG, 100, baos);
		byte[] data = baos.toByteArray();

		return data;
	}

	public static void BlobToJpeg(byte[] srcStream, String imgFile)
			throws IOException {
		if (srcStream == null || srcStream.length == 0)
			return;

		Bitmap bitmapimage = BlobToBitmap(srcStream);
		String filepath = imgFile;
		File imagefile = new File(filepath);
		FileOutputStream fos = new FileOutputStream(imagefile);
		bitmapimage.compress(CompressFormat.JPEG, 100, fos);
		fos.flush();
		fos.close();
	}

	public static Bitmap BlobToBitmap(byte[] srcStream) {
		if (srcStream == null || srcStream.length == 0)
			return null;

		return BitmapFactory.decodeByteArray(srcStream, 0, srcStream.length);
	}

	// ///給定path字串與縮放等級,回傳bitmap,scale月大回傳size越小
	public static Bitmap PathToBitmap(String filePath, int scale) {

		if (filePath == null || filePath.trim() == "")
			return null;

		InputStream is = null;

		Bitmap bp = null;
		
		BitmapFactory.Options opts = new BitmapFactory.Options();
		opts.inJustDecodeBounds = false;
		opts.inTempStorage = new byte[16 * 1024];
		opts.inSampleSize = scale;

		try {

			is = new FileInputStream(filePath);

			bp = BitmapFactory.decodeStream(is, null, opts);

			is.close();

		} catch (FileNotFoundException e) {
			Log.d("PathToBitmap", e.getMessage());
		} catch (IOException e) {
			e.printStackTrace();
		} catch (OutOfMemoryError e) {
			e.printStackTrace();
		}

		return bp;
	}

}
