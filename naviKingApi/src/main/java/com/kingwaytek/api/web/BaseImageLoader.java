package com.kingwaytek.api.web;

import java.io.File;
import java.lang.ref.WeakReference;
import com.kingwaytek.api.cache.DiskLruCache;
import com.kingwaytek.api.cache.MemoryLruCache;
import com.kingwaytek.api.utility.FileApi;
import android.content.Context;
import android.graphics.Bitmap;
import android.os.AsyncTask;
import android.widget.ImageView;

public abstract class BaseImageLoader extends AsyncTask<String, Void, Bitmap> {
	
	protected abstract String getCachePath(Context context);
	protected abstract Bitmap processBitmapInBackground(Bitmap bitmap);
	public static final int MAX_CACHE_SIZE_IN_BYTES = 1024 * 1024 * 80; // 100Mb

	static DiskLruCache mDiskLruCache;
	final boolean AVOID_MUTIL_THREAD = true;
	
	private final WeakReference<ImageView> mWeakRefImageView;
	protected Context mContext;
	protected String mUrl;
	protected int mImageProcessType ;


	public BaseImageLoader(Context context, ImageView imageView, String photoUrl) {
		imageView.setTag(photoUrl);
		this.mWeakRefImageView = new WeakReference<ImageView>(imageView);		
		this.mContext = context;
		this.mUrl = photoUrl;
		//this.mImageProcessType = imageProcessType ;
		initDiskLru(context);
	}

	void initDiskLru(Context ctx) {
		if (mDiskLruCache == null) {
			File cacheDir = new File(getCachePath(mContext));
			DiskLruCache.setMaxCacheSize(MAX_CACHE_SIZE_IN_BYTES);
			mDiskLruCache = DiskLruCache.openCache(ctx, cacheDir);
		}
	}

	@Override
	protected void onPreExecute() {
		super.onPreExecute();
		showImageWithoutCreateTaskIfExist();		
	}

	@Override
	protected Bitmap doInBackground(String... urls) {
		Bitmap processBitmap = null; 
		try {
			Bitmap bitmap = getBitmap(mUrl);
			processBitmap = processBitmapInBackground(bitmap);
		} catch (Exception e) {
			e.printStackTrace();
		} catch (Error e) {
			e.printStackTrace();
		}
		return processBitmap;
	}
	
	private void showImageWithoutCreateTaskIfExist(){
		if (AVOID_MUTIL_THREAD) {
			try {
				Bitmap bitmap = getBitmapFromLocal(mUrl);
				if (bitmap != null) {
					Bitmap processBitmap = processBitmapInBackground(bitmap);
					showImage(processBitmap);
					this.cancel(true);
				}
			} catch (Exception e) {
				e.printStackTrace();
			} catch (Error e) {
				e.printStackTrace();
			}
		}
	}

	Bitmap getBitmapFromLocal(String url) {
		return getBitmap(url, false);
	}

	Bitmap getBitmap(String url) {
		return getBitmap(url, true);
	}

	Bitmap getBitmap(String url, boolean bFromWeb) {
		Bitmap bitmap = null;

		// Memory
		bitmap = MemoryLruCache.get(url);
		boolean isMemoryExist = bitmap != null;
		if (isMemoryExist) {
			return bitmap;
		}

		// Disk
		boolean isFileExist = mDiskLruCache.findFile(mUrl);
		if (isFileExist) {
			bitmap = mDiskLruCache.get(mContext, mUrl);
			if(bitmap != null){
				return bitmap;
			}
		}

		// Web
		if (bFromWeb) {
			bitmap = FileApi.BitmapApi.getBitmapFromUrl(mUrl);
			MemoryLruCache.put(mUrl, bitmap);
			mDiskLruCache.put(mUrl, bitmap);
		}
		return bitmap;
	}

	@Override
	protected void onPostExecute(Bitmap bitmap) {		
		if (isCancelled()) {
            bitmap = null;
        }
		showImage(bitmap);
	}

	private void showImage(Bitmap result) {		
		if(mWeakRefImageView == null){
			return ;
		}
		
		ImageView imageView = mWeakRefImageView.get();
		if(imageView == null){
			return ;
		}
		
		try {
			String photoUrl = (String) imageView.getTag();
			boolean bUrlEqual = photoUrl.equals(mUrl);
			if (result != null && imageView != null && bUrlEqual) {
				imageView.setImageBitmap(result);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}