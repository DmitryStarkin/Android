package com.hplasplas.task6.loaders;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;

import com.starsoft.bmutil.BitmapTools;

import java.io.IOException;
import java.io.InputStream;

import static com.hplasplas.task6.setting.Constants.DEBUG;
import static com.hplasplas.task6.setting.Constants.FILE_NAME_TO_LOAD;
import static com.hplasplas.task6.setting.Constants.LIST_INDEX;
import static com.hplasplas.task6.setting.Constants.MUST_IMPLEMENT_INTERFACE_MESSAGE;
import static com.hplasplas.task6.setting.Constants.NO_PICTURE_FILE_NAME;
import static com.hplasplas.task6.setting.Constants.REQUESTED_PICTURE_HEIGHT;
import static com.hplasplas.task6.setting.Constants.REQUESTED_PICTURE_WIDTH;

/**
 * Created by StarkinDG on 15.03.2017.
 */

public class BitmapInThreadLoader implements Runnable {
    
    private final String TAG = getClass().getSimpleName();
    
    int index;
    int requestedHeight;
    int requestedWidth;
    String fileName;
    AppCompatActivity myActivity;
    
    public BitmapInThreadLoader(AppCompatActivity activity, Bundle args) {
        
        myActivity = activity;
        fileName = args.getString(FILE_NAME_TO_LOAD);
        requestedHeight = args.getInt(REQUESTED_PICTURE_HEIGHT);
        requestedWidth = args.getInt(REQUESTED_PICTURE_WIDTH);
        index = args.getInt(LIST_INDEX);
    }
    
    @Override
    public void run() {
        
        try {
            BitmapTools bitmapTools = new BitmapTools();
            Bitmap newBitmap;
            BitmapFactory.Options currentBitmapOptions;
            if (requestedHeight != 0 & requestedWidth != 0) {
                currentBitmapOptions = bitmapTools.readBitmapOptionsFromFile(fileName);
                currentBitmapOptions.inSampleSize = bitmapTools.calculateInSampleSize(currentBitmapOptions, requestedWidth, requestedHeight);
            } else {
                currentBitmapOptions = new BitmapFactory.Options();
            }
            newBitmap = bitmapTools.LoadPictureFromFile(fileName, currentBitmapOptions);
            
            if (newBitmap == null) {
                newBitmap = loadPictureFromAssets(NO_PICTURE_FILE_NAME, currentBitmapOptions, bitmapTools);
            }
            
            if (newBitmap.getHeight() < newBitmap.getWidth()) {
                newBitmap = bitmapTools.rotate(newBitmap, 90);
            }
            
            try {
                BitmapLoaderListener listener = (BitmapLoaderListener) myActivity;
                listener.onBitmapLoadFinished(index, fileName, newBitmap);
            } catch (ClassCastException e) {
                throw new ClassCastException(myActivity.toString() + MUST_IMPLEMENT_INTERFACE_MESSAGE);
            }
        } finally {
            clearTread();
        }
    }
    
    private Bitmap loadPictureFromAssets(String fileName, BitmapFactory.Options currentBitmapOptions, BitmapTools bitmapTools) {
        
        if (DEBUG) {
            Log.d(TAG, "loadTextFromAssets: ");
        }
        Bitmap newBitmap = null;
        InputStream inputStream = null;
        try {
            inputStream = myActivity.getAssets().open(fileName);
            newBitmap = bitmapTools.loadPictureFromInputStream(fileName, inputStream, currentBitmapOptions);
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
    
    private void clearTread() {
        
        myActivity = null;
        fileName = null;
    }
    
    public interface BitmapLoaderListener {
        
        public void onBitmapLoadFinished(int index, String fileName, Bitmap bitmap);
    }
}

