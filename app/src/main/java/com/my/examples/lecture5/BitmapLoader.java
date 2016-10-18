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
			mResult = ImageStorage.getInstance().loadBitmapFromCacheDir(getContext(), scaledFileName);
			if (mResult == null) {
				mResult = ImageStorage.getInstance().loadBitmapFromAssets(getContext(), fileName);
				if (mResult != null) {
					mResult = UIUtils.scaleBitmap(mResult, mW, mH);
					ImageStorage.getInstance().saveBitmapToFile(getContext(), mResult, scaledFileName);
					return mResult;
				}
			}
			return mResult;
		}
		else {
			final String fileName = "" + (mId + 1) + ".jpg";
			mResult = ImageStorage.getInstance().loadBitmapFromAssets(getContext(), fileName);
			return mResult;
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
