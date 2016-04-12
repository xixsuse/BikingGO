package com.kingwaytek.api.utility;

import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.drawable.BitmapDrawable;
import android.util.DisplayMetrics;

public class PaintTools{
	final static int DEFAULT_FIND_TEXT_SIZE = 20 ;
	public final static int PADDING_DIV_BY_10 = 10 ;
	public final static int PADDING_DIV_BY_8 = 8 ;
	
    // 取得文字高度
	public static int getTextHeight(Paint paint,String measureText){
    	int measureSize = measureText.length() ;
    	Rect rcTextBounds = new Rect() ;
    	paint.getTextBounds(measureText, 0, measureSize, rcTextBounds);
    	return rcTextBounds.height() ;    	   
    } 
	
    public static int getAutoTextSize(String strToShow,int maxWidth,int paddintDivBy){
		Paint paint = new Paint();
    	int textSize = DEFAULT_FIND_TEXT_SIZE ;
    	int measureTextWidth = 0 ;
    	final int PADDING_SIZE = maxWidth / paddintDivBy ;
    	boolean bNotFit ;
    	do{
    		textSize += 3 ;
        	paint.setTextSize(textSize);
    		measureTextWidth = getTotalTextWidth(paint,strToShow);
    		bNotFit = (measureTextWidth + PADDING_SIZE) < maxWidth ;
    	}while(bNotFit);
    	
    	return textSize;
    }
    
	public static int getTotalTextWidth(Paint paint,String strToShow){
    	float[] widths = new float[strToShow.length()];
		int txtWidth = 0 ; 
		paint.getTextWidths(strToShow, widths);
		for(float w : widths){
			txtWidth += w ;
		}
		return txtWidth ;
    }
    
    public static int getScaleBitmapHeight(Context ctx,int imageResId){
    	
//    	Options options = new Options();
//    	options.inJustDecodeBounds = true ;
//		options.inScaled = true ;
//		options.inDensity = DisplayMetrics.DENSITY_DEFAULT;
//		options.inTargetDensity = ctx.getResources().getDisplayMetrics().densityDpi;
    	
    	int height = 0 ;
    	try{
			Bitmap bmp = BitmapFactory.decodeResource(ctx.getResources(), imageResId);
			height = (int)bmp.getHeight();
			bmp.recycle();
			bmp = null ;
    	}catch(OutOfMemoryError e){
    		e.printStackTrace();
    	}
		return height ;
    }
    
    public static int getBitmapHeightByJustDecode(Context ctx,int imageResId){
    	
//    	Options options = new Options();
//    	options.inJustDecodeBounds = true ;
//		options.inScaled = true ;
//		options.inDensity = DisplayMetrics.DENSITY_DEFAULT;
//		options.inTargetDensity = ctx.getResources().getDisplayMetrics().densityDpi;
    	
    	int height = 0 ;
    	try{
			Bitmap bmp = BitmapFactory.decodeResource(ctx.getResources(), imageResId);
			height = (int)bmp.getHeight();
			bmp.recycle();
			bmp = null ;
    	}catch(OutOfMemoryError e){
    		e.printStackTrace();
    	}
		return height ;
    }
    
	public static BitmapFactory.Options getDecodeBitmapOption(Activity activity)
	{
		BitmapFactory.Options bmOpt = new BitmapFactory.Options();		
		DisplayMetrics outDm = new DisplayMetrics(); 
		activity.getWindowManager().getDefaultDisplay().getMetrics(outDm);
		bmOpt.inDensity = outDm.densityDpi ; 
		bmOpt.inTargetDensity = bmOpt.inScreenDensity = 0;
		bmOpt.inScaled = false;
		return bmOpt;
	}
	
	@SuppressWarnings("deprecation")
	public static BitmapFactory.Options getBitmapOption()
	{
		BitmapFactory.Options bmOpt = new BitmapFactory.Options();
		
		BitmapFactory.Options opt = new BitmapFactory.Options();  
		opt.inPreferredConfig = android.graphics.Bitmap.Config.RGB_565;   
		opt.inPurgeable = true;  
		opt.inInputShareable = true;  
		return bmOpt;
	}
	
	public static void SetBitmapDrawableDensity(BitmapDrawable bmDraw)
	{		
		//bmDraw.setTargetDensity(NaviKingUtils.Screen.GetDisplayMetrics());
	}
}