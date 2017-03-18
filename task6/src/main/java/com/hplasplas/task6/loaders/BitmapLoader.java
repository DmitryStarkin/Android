package com.hplasplas.task6.loaders;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.os.Bundle;
import android.support.v4.content.AsyncTaskLoader;
import android.util.Log;

import java.io.IOException;
import java.io.InputStream;

import static com.hplasplas.task6.setting.Constants.CROP_TO_ASPECT_RATIO;
import static com.hplasplas.task6.setting.Constants.DEBUG;
import static com.hplasplas.task6.setting.Constants.FILE_NAME_TO_LOAD;
import static com.hplasplas.task6.setting.Constants.NO_PICTURE_FILE_NAME;
import static com.hplasplas.task6.setting.Constants.REQUESTED_PICTURE_HEIGHT;
import static com.hplasplas.task6.setting.Constants.REQUESTED_PICTURE_WIDTH;

/**
 * Created by StarkinDG on 11.02.2017.
 */

public class BitmapLoader extends AsyncTaskLoader<Bitmap> {
    
    private final String TAG = getClass().getSimpleName();
    private int mRequestedHeight;
    private int mRequestedWidth;
    private boolean mCropToAspectRatio;
    private String mFileName;
    private Bitmap mPicture;
    private BitmapFactory.Options mCurrentBitmapOptions;
    
    public BitmapLoader(Context context, Bundle args) {
        
        super(context);
        if (args != null) {
            mFileName = args.getString(FILE_NAME_TO_LOAD);
            mRequestedHeight = args.getInt(REQUESTED_PICTURE_HEIGHT);
            mRequestedWidth = args.getInt(REQUESTED_PICTURE_WIDTH);
            mCropToAspectRatio = args.getBoolean(CROP_TO_ASPECT_RATIO, false);
        }
    }
    
    @Override
    public Bitmap loadInBackground() {
        
        if (DEBUG) {
            Log.d(TAG, "loadInBackground: ");
        }
        if (mRequestedHeight != 0 & mRequestedWidth != 0) {
            mCurrentBitmapOptions = readBitmapOptionsFromFile(mFileName);
            mCurrentBitmapOptions.inSampleSize = calculateInSampleSize(mCurrentBitmapOptions, mRequestedWidth, mRequestedHeight);
        } else {
            mCurrentBitmapOptions = new BitmapFactory.Options();
        }
        return LoadPicture(mFileName);
    }
    
    /**
     * calculate inSampleSize
     * until all sides of picture are less then the requested size
     * taking a biggest size as the height
     */
    private int calculateInSampleSize(BitmapFactory.Options options, int reqWidth, int reqHeight) {
        
        final int width = options.outHeight > options.outWidth ? options.outWidth : options.outHeight;
        final int height = options.outHeight > options.outWidth ? options.outHeight : options.outWidth;
        
        int inSampleSize = 1;
        
        if (height > reqHeight || width > reqWidth) {
            inSampleSize = 2;
            while ((height / inSampleSize) > reqHeight || (width / inSampleSize) > reqWidth) {
                inSampleSize *= 2;
            }
        }
        return inSampleSize;
    }
    
    private BitmapFactory.Options readBitmapOptionsFromFile(String filename) {
        
        BitmapFactory.Options targetOptions = new BitmapFactory.Options();
        targetOptions.inJustDecodeBounds = true;
        BitmapFactory.decodeFile(filename, targetOptions);
        targetOptions.inJustDecodeBounds = false;
        return targetOptions;
    }
    
    private Bitmap LoadPicture(String fileName) {
        
        Bitmap newBitmap;
        newBitmap = LoadPictureFromFile(fileName);
        if (newBitmap == null) {
            newBitmap = loadPictureFromAssets(NO_PICTURE_FILE_NAME);
        }
        if (mCropToAspectRatio) {
            newBitmap = cropToAspectRatio(newBitmap);
        }
        
        if (newBitmap.getHeight() < newBitmap.getWidth()) {
            newBitmap = rotate(newBitmap, 90);
        }
        return newBitmap;
    }
    
    private Bitmap cropToAspectRatio(Bitmap newBitmap) {
        
        //TODO
        return newBitmap;
    }
    
    private Bitmap rotate(Bitmap bitmap, float degrees) {
        
        Bitmap newBitmap;
        Matrix matrix = new Matrix();
        matrix.postRotate(degrees);
        try {
            newBitmap = Bitmap.createBitmap(bitmap, 0, 0, bitmap.getWidth(), bitmap.getHeight(), matrix, true);
            bitmap.recycle();
        } catch (OutOfMemoryError e) {
            newBitmap = bitmap;
        }
        return newBitmap;
    }
    
    private Bitmap LoadPictureFromFile(String fileName) {
        
        Bitmap newBitmap;
        try {
            newBitmap = BitmapFactory.decodeFile(fileName, mCurrentBitmapOptions);
        } catch (OutOfMemoryError e) {
            newBitmap = BitmapFactory.decodeFile(fileName, reduceSizeTwice(mCurrentBitmapOptions));
        }
        return newBitmap;
    }
    
    private Bitmap loadPictureFromAssets(String fileName) {
        
        if (DEBUG) {
            Log.d(TAG, "loadTextFromAssets: ");
        }
        Bitmap newBitmap = null;
        InputStream inputStream = null;
        try {
            inputStream = getContext().getAssets().open(fileName);
            newBitmap = BitmapFactory.decodeStream(inputStream, null, mCurrentBitmapOptions);
        } catch (IOException e) {
            e.printStackTrace();
            newBitmap = null;
        } finally {
            if (inputStream != null) {
                try {
                    inputStream.close();
                } catch (IOException e1) {
                    e1.printStackTrace();
                }
            }
        }
        return newBitmap;
    }
    
    private BitmapFactory.Options reduceSizeTwice(BitmapFactory.Options options) {
        
        int inSampleSize = options.inSampleSize;
        if (inSampleSize < 2) {
            inSampleSize = 2;
        } else {
            inSampleSize *= 2;
        }
        options.inSampleSize = inSampleSize;
        return options;
    }
    
    @Override
    public void deliverResult(Bitmap data) {
        
        if (DEBUG) {
            Log.d(TAG, "deliverResult: ");
        }
        mPicture = data;
        if (isStarted()) {
            super.deliverResult(data);
        }
    }
    
    @Override
    protected void onStartLoading() {
        
        if (DEBUG) {
            Log.d(TAG, "onStartLoading: ");
        }
        if (mPicture != null) {
            deliverResult(mPicture);
        } else {
            forceLoad();
        }
    }
}