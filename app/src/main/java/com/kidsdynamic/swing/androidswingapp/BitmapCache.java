package com.kidsdynamic.swing.androidswingapp;

import android.graphics.Bitmap;
import android.support.v4.util.LruCache;

/**
 * Created by 03543 on 2017/1/11.
 */

public class BitmapCache {

    private LruCache<String, Bitmap> mCache;

    public BitmapCache() {
        final int maxMemory = (int) (Runtime.getRuntime().maxMemory() / 1024);

        // Use 1/8th of the available memory for this memory cache.
        final int cacheSize = maxMemory / 8;

        mCache = new LruCache<String, Bitmap>(cacheSize) {
            @Override
            protected int sizeOf(String key, Bitmap bitmap) {
                // The cache size will be measured in kilobytes rather than number of items.
                return bitmap.getByteCount() / 1024;
            }
        };
    }

    public void put(String key, Bitmap bitmap) {
        while (get(key) != null)
            mCache.remove(key);
        mCache.put(key, bitmap);
    }

    public Bitmap get(String key) {
        return mCache.get(key);
    }

    public void remove(String key) {
        mCache.remove(key);
    }

    public void clear() {
        mCache.evictAll();
    }
}
