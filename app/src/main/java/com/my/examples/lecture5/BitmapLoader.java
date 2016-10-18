/**
 * Copyrigh Mail.ru Games (c) 2015
 * Created by y.bereza.
 */
package com.my.examples.lecture5;

import android.content.AsyncTaskLoader;
import android.content.Context;
import android.graphics.Bitmap;

public class BitmapLoader extends AsyncTaskLoader<Bitmap> {
	private Bitmap mResult;
	private final int mId;
	private final boolean mScaled;
	private final int mW;
	private final int mH;

	public BitmapLoader(Context context, int id, boolean scaled, int w, int h) {
		super(context);
		mId = id;
		mScaled = scaled;
		mW = w;
		mH = h;
	}

	public int getId() {
		return mId;
	}

	@Override
	public Bitmap loadInBackground() {
		if (mScaled) {
			final String scaledFileName = "" + (mId + 1) + "_scaled.jpg";
			final String fileName = "" + (mId + 1) + ".jpg";
			Bitmap bm = ImageStorage.getInstance().loadBitmapFromCacheDir(getContext(), scaledFileName);
			if (bm == null) {
				bm = ImageStorage.getInstance().loadBitmapFromAssets(getContext(), fileName);
				if (bm != null) {
					bm = UIUtils.scaleBitmap(bm, mW, mH);
					ImageStorage.getInstance().saveBitmapToFile(getContext(), bm, scaledFileName);
					return bm;
				}
			}
			return bm;
		}
		else {
			final String fileName = "" + (mId + 1) + ".jpg";
			Bitmap bm = ImageStorage.getInstance().loadBitmapFromAssets(getContext(), fileName);
			return bm;
		}
	}

	@Override
	protected void onStartLoading() {
		if (mResult != null) {
			deliverResult(mResult);
		}
		else if (!takeContentChanged()) {
			forceLoad();
		}
	}

	@Override
	protected void onStopLoading() {
		cancelLoad();
	}

	@Override
	protected void onReset() {
		mResult = null;
		onStopLoading();
	}
}
