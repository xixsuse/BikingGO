package com.kingwaytek.api.cache;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Bitmap.CompressFormat;

import com.kingwaytek.api.utility.Encoder;
import com.kingwaytek.api.utility.FileApi;
import com.kingwaytek.api.utility.FileApi.BitmapApi;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Calendar;
import java.util.Date;
import java.util.concurrent.TimeUnit;

public class DiskLruCache {

	private boolean bDebug = true;
	private final static String TAG = "DiskLruCache";
	private static final int DISK_MAX_SIZE_DEFAULT = 64 * 1024 * 1024; // SD 64MB
	private static final int[] DISK_REMOVE_DAYS_PAST = { 10, 7, 3, 1, 0 }; // 判斷時間新舊來刪除cache
	private static int dayPointer = 0;

	public static CompressFormat mCompressFormat = CompressFormat.JPEG;
	public final static int mCompressQuality = 70; // 建議保留為 90 ,70會很醜
	private static File mCacheDir;
	private static int mMaxDiskCacheSize = DISK_MAX_SIZE_DEFAULT ;
	
	final static String FILE_CACHEDIR_NAME = "cacheDir";

	private DiskLruCache(File cacheDir) {
		mCacheDir = cacheDir;
	}
	
	public static void setMaxCacheSize(int maxDiskCache){
		mMaxDiskCacheSize = maxDiskCache ;
	}

	public static void cleanAllData(Context context,File cacheDir) {
		if (context != null) {
			openCache(context,cacheDir);
			if (mCacheDir != null) {
				for (File fileInDir : mCacheDir.listFiles()) {
					fileInDir.delete();
				}
			}
		}
	}

	// FIXME size limit
	public static DiskLruCache openCache(Context context,File cacheDir) {		
		if (context == null){
			return null;
		}
		
		if( cacheDir == null){
			return null;
		}
				
		try {				
			if (cacheDir.isDirectory() && cacheDir.canWrite()) {
				return new DiskLruCache(cacheDir);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		return null;
	}

	public Bitmap get(Context context, String imageUrl) {
		return (findFile(imageUrl) == true) ? getBitmap(context, imageUrl) : null;
	}

	public boolean findFile(String imageUrl) {
		try{
			boolean bFind = imageUrl != null && imageUrl.length() > 0 && mCacheDir != null ;
			if (bFind) {
				boolean exist = new File(mCacheDir,getEncodeFileName(imageUrl)).exists();
				return exist;
			}
		}catch(Exception e){
			e.printStackTrace();
		}
		return false ; 
	}
	
	private String getEncodeFileName(String url){
		return Encoder.toMd5String(url);
	}

	private Bitmap getBitmap(Context context, String imageUrl) {
		File file = new File(mCacheDir,getEncodeFileName(imageUrl));		
		if (file.exists() && file.length() != 0){
			return BitmapApi.getBitmapFromStorage(file.getAbsolutePath());			
		} else {
			return null;
		}
	}

	public void put(String imageUrl, Bitmap bmp) {
		
		clearCacheIfOutOfMaxSize();

		try {
			String filename = getEncodeFileName(imageUrl);
			File recodeFile = new File(mCacheDir.getAbsolutePath() + File.separator + filename);
			FileOutputStream fos = new FileOutputStream(recodeFile);
			if(imageUrl.endsWith("png")) {
				mCompressFormat = CompressFormat.PNG;
			} else {
				mCompressFormat = CompressFormat.JPEG;
			}
			bmp.compress(mCompressFormat, mCompressQuality, fos);
			fos.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	private void clearCacheIfOutOfMaxSize(){
		boolean needClearCache = mCacheDir.getUsableSpace() > mMaxDiskCacheSize && FileApi.Size.getDirSize(mCacheDir) > mMaxDiskCacheSize ;
		if (needClearCache) {
			while (cleanOldData() == false);
		}
	}

	private boolean cleanOldData() {
		boolean isClean = false;
		Calendar calendar = Calendar.getInstance();
		long nowSeconds = calendar.getTimeInMillis();
		for (File fileInDir : mCacheDir.listFiles()) {
			Date lastModDate = new Date(fileInDir.lastModified());
			long minusSeconds = nowSeconds - lastModDate.getTime();
			long days = TimeUnit.MILLISECONDS.toDays(minusSeconds);
			if (days >= DISK_REMOVE_DAYS_PAST[dayPointer]) {
				fileInDir.delete();
				isClean = true;
			}
		}

		if (dayPointer + 1 < DISK_REMOVE_DAYS_PAST.length) {
			dayPointer++;
		} else {
			isClean = true;
		}
		return isClean;
	}
}