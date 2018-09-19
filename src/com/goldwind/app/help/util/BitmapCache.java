package com.goldwind.app.help.util;

import android.annotation.SuppressLint;
import android.graphics.Bitmap;
import android.support.v4.util.LruCache;

public class BitmapCache {
    private LruCache<String, Bitmap> mCache;

    public BitmapCache() {
        int maxMemory = (int) Runtime.getRuntime().maxMemory();
        int maxSize = maxMemory / 8;
        mCache = new LruCache<String, Bitmap>(maxSize) {
            @SuppressLint("NewApi")
            @Override
            protected int sizeOf(String key, Bitmap bitmap) {
                return bitmap.getByteCount();
            }
        };
    }

    public Bitmap getBitmap(String url) {
        return mCache.get(url);
    }

    public void putBitmap(String url, Bitmap bitmap) {
        if (bitmap != null) {
            mCache.put(url, bitmap);
        }
    }
}
