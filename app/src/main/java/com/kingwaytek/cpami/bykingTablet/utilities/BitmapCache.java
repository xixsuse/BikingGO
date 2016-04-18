package com.kingwaytek.cpami.bykingTablet.utilities;

import android.graphics.Bitmap;
import android.util.LruCache;

import com.android.volley.toolbox.ImageLoader;

/**
 * Created by vincent.chang on 2016/1/22.
 */
public class BitmapCache implements ImageLoader.ImageCache {

    private LruCache<String, Bitmap> mCache;
    final int maxMemory = (int) (Runtime.getRuntime().maxMemory() / 1024);
    final int bitmapCacheSize = maxMemory / 8;

    public BitmapCache() {
        //final int maxSize = 10 * 1024 * 1024;
        mCache = new LruCache<String, Bitmap>(bitmapCacheSize) {
            @Override
            protected int sizeOf(String key, Bitmap value) {
                return value.getRowBytes() * value.getHeight();
            }
        };
    }

    @Override
    public Bitmap getBitmap(String url) {
        return mCache.get(url);
    }

    @Override
    public void putBitmap(String url, Bitmap bitmap) {
        mCache.put(url, bitmap);
    }
}
