package com.my.examples.lecture5;


import android.content.Context;

public class ImageStorage {
    private static volatile ImageStorage sSelf;
    private boolean mEmpty = true;

    private ImageStorage() {

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

    /**
     * Beware! function may work slow. Do not call from the UI thread
     * @param context
     */
    public void loadFilesFromAssets(Context context) {

    }

    public boolean isEmpty() {
        return mEmpty;
    }
}
