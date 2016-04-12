package com.kingwaytek.api.cache;

import android.graphics.Bitmap;
import android.support.v4.util.LruCache;

public class MemoryLruCache {

	public static LruCache<String, Bitmap> mImageCache;
	public static int cacheSize = -1;

	private static void init() {
		if (cacheSize == -1) {
			cacheSize = getCacheSize();
		}
		if (mImageCache == null) {
			mImageCache = initLruCache(cacheSize);
		}
	}

	public static void put(String key, Bitmap value) {		
		if(value != null){
			return ;
		}
		
		init();
		try {
			mImageCache.put(key, value);
		} catch (Exception e) {
			e.printStackTrace();
		} catch (Error e) {
			e.printStackTrace();
		}
	}

	public static Bitmap get(String key) {
		init();
		synchronized (mImageCache){
			try {
				return mImageCache.get(key);
			} catch (Exception e) {
				e.printStackTrace();
			} catch (Error e) {
				e.printStackTrace();
			}
		}
		return null;
	}

	// The max memory can be used. This can prevent from OOM
	private static int getCacheSize() {
		int maxMemory = (int) (Runtime.getRuntime().maxMemory() / 1024);
		int cacheSize = maxMemory / 4;
		return cacheSize;
	}

	private static LruCache<String, Bitmap> initLruCache(int _cacheSize) {
		LruCache<String, Bitmap> imageCache = new LruCache<String, Bitmap>(_cacheSize) {
			@Override
			protected int sizeOf(String key, Bitmap bitmap) {
				return bitmap.getByteCount() / 1024;
			}
		};
		return imageCache;
	}
}