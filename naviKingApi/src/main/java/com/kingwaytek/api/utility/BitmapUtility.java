package com.kingwaytek.api.utility;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.ColorMatrix;
import android.graphics.ColorMatrixColorFilter;
import android.graphics.Paint;

public class BitmapUtility {
	public static Bitmap setIconToGray(Context context, int img_res) {

		Bitmap originImg = BitmapFactory.decodeResource(context.getResources(),
				img_res);
		Bitmap grayImg = Bitmap.createBitmap(originImg.getWidth(),
				originImg.getHeight(), Bitmap.Config.ARGB_8888);
		Canvas canvas = new Canvas(grayImg);
		Paint paint = new Paint();
		ColorMatrix colorMatrix = new ColorMatrix();
		colorMatrix.setSaturation(0);
		ColorMatrixColorFilter colorMatrixFilter = new ColorMatrixColorFilter(
				colorMatrix);
		paint.setColorFilter(colorMatrixFilter);
		canvas.drawBitmap(originImg, 0, 0, paint);
		return grayImg;
	}
}
