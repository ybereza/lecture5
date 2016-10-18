package com.my.examples.lecture5;


import android.content.Context;
import android.content.res.AssetManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.support.v4.util.LruCache;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

public class ImageStorage {
    private static volatile ImageStorage sSelf;
    private boolean mEmpty = true;

    private LruCache<Integer, Bitmap> mMemoryCache;

    private ImageStorage() {
        final int maxMemory = (int) (Runtime.getRuntime().maxMemory() / 1024);
        final int cacheSize = maxMemory / 8;
        mMemoryCache = new LruCache<Integer, Bitmap>(cacheSize) {
            @Override
            protected int sizeOf(Integer key, Bitmap bitmap) {
                return bitmap.getRowBytes() * bitmap.getHeight() / 1024;
            }
        };
    }

    public static ImageStorage getInstance() {
        if (sSelf == null) {
            synchronized (ImageStorage.class) {
                if (sSelf == null) {
                    sSelf = new ImageStorage();
                }
                return sSelf;
            }
        }
        return sSelf;
    }

    public Bitmap getBitmapFromCache(Integer id) {
        return mMemoryCache.get(id);
    }

    public void putBitmapIntoCache(Integer id, Bitmap bitmap) {
        mMemoryCache.put(id, bitmap);
    }

    public Bitmap loadBitmapFromCacheDir(Context context, String filename) {
        File cacheDir = context.getCacheDir();
        File file = new File(cacheDir, filename);
        if (file.exists()) {
            try {
                InputStream is = new FileInputStream(file);
                BitmapFactory.Options opt = new BitmapFactory.Options();
                return BitmapFactory.decodeStream(is, null, opt);
            }
            catch (FileNotFoundException e) {
                e.printStackTrace();
            }
        }
        return null;
    }

    /**
     * Beware! function may work slow. Do not call from the UI thread
     * @param context
     */
    public Bitmap loadBitmapFromAssets(Context context, String filename) {
        AssetManager assetManager = context.getAssets();
        try {
            InputStream is = assetManager.open("images/"+filename);
            BitmapFactory.Options opt = new BitmapFactory.Options();
            return BitmapFactory.decodeStream(is, null, opt);
        }
        catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    public void saveBitmapToFile(Context context, Bitmap bitmap, String filename) {
        try {
            File cacheDir = context.getCacheDir();
            File file = new File(cacheDir, filename);
            OutputStream os = new FileOutputStream(file);
            bitmap.compress(Bitmap.CompressFormat.PNG, 100, os);
            os.flush();
            os.close();
        }
        catch (IOException e) {
            e.printStackTrace();
        }
    }

    public boolean isEmpty() {
        return mEmpty;
    }
}
