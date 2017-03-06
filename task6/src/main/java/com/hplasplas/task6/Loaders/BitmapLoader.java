package com.hplasplas.task6.Loaders;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.support.v4.content.AsyncTaskLoader;
import android.util.Log;

import java.io.IOException;
import java.io.InputStream;

import static com.hplasplas.task6.Setting.Constants.ARG_FILE_NAME_TO_LOAD;
import static com.hplasplas.task6.Setting.Constants.ARG_REQUESTED_PICTURE_ASPECT_RATIO;
import static com.hplasplas.task6.Setting.Constants.ARG_REQUESTED_PICTURE_HEIGHT;
import static com.hplasplas.task6.Setting.Constants.ARG_REQUESTED_PICTURE_WIDTH;
import static com.hplasplas.task6.Setting.Constants.DEBUG;
import static com.hplasplas.task6.Setting.Constants.NO_PICTURE;

/**
 * Created by StarkinDG on 11.02.2017.
 */

public class BitmapLoader extends AsyncTaskLoader<Bitmap> {
    
    private final String TAG = getClass().getSimpleName();
    private int requestedHeight;
    private int requestedWidth;
    private double aspectRatio;
    private String fileName;
    private Bitmap picture;
    private BitmapFactory.Options currentBitmapOptions;
    
    public BitmapLoader(Context context, Bundle args) {
        
        super(context);
        if (args != null) {
            fileName = args.getString(ARG_FILE_NAME_TO_LOAD);
            requestedHeight = args.getInt(ARG_REQUESTED_PICTURE_HEIGHT);
            requestedWidth = args.getInt(ARG_REQUESTED_PICTURE_WIDTH);
            aspectRatio = args.getDouble(ARG_REQUESTED_PICTURE_ASPECT_RATIO);
        }
    }
    
    @Override
    public Bitmap loadInBackground() {
        
        if (DEBUG) {
            Log.d(TAG, "loadInBackground: ");
        }
        if (requestedHeight != 0 && requestedWidth != 0) {
            currentBitmapOptions = readBitmapOptionsFromFile(fileName);
            currentBitmapOptions.inSampleSize = calculateInSampleSize(currentBitmapOptions, requestedWidth, requestedHeight);
        } else {
            currentBitmapOptions = new BitmapFactory.Options();
        }
        
        return LoadPicture(fileName);
    }
    
    private int calculateInSampleSize(BitmapFactory.Options options, int reqWidth, int reqHeight) {
        
        final int height = options.outHeight;
        final int width = options.outWidth;
        int inSampleSize = 1;
        
        if (height > reqHeight || width > reqWidth) {
            inSampleSize = 2;
            while ((height / inSampleSize) > reqHeight && (width / inSampleSize) > reqWidth) {
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
            newBitmap = loadPictureFromAssets(NO_PICTURE);
        }
        if (aspectRatio >= 1) {
            newBitmap = cropToAspectRatio(newBitmap);
        }
        return newBitmap;
    }
    
    private Bitmap cropToAspectRatio(Bitmap newBitmap) {
        //TODO
        return newBitmap;
    }
    
    private Bitmap LoadPictureFromFile(String fileName) {
        
        Bitmap newBitmap;
        try {
            newBitmap = BitmapFactory.decodeFile(fileName, currentBitmapOptions);
        } catch (OutOfMemoryError e) {
            newBitmap = BitmapFactory.decodeFile(fileName, reduceSize(currentBitmapOptions));
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
            newBitmap = BitmapFactory.decodeStream(inputStream, null, currentBitmapOptions);
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
    
    private BitmapFactory.Options reduceSize(BitmapFactory.Options options) {
        
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
        picture = data;
        if (isStarted()) {
            super.deliverResult(data);
        }
    }
    
    @Override
    protected void onStartLoading() {
        
        if (DEBUG) {
            Log.d(TAG, "onStartLoading: ");
        }
        if (picture != null) {
            deliverResult(picture);
        } else {
            forceLoad();
        }
    }
}