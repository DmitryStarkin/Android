package com.hplasplas.task6.Loaders;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.support.v4.content.AsyncTaskLoader;
import android.util.Log;

import java.io.IOException;
import java.io.InputStream;

import static com.hplasplas.task6.Setting.Constants.DEBUG;
import static com.hplasplas.task6.Setting.Constants.FILE_NAME_TO_LOAD;
import static com.hplasplas.task6.Setting.Constants.NO_PICTURE;

/**
 * Created by StarkinDG on 11.02.2017.
 */

public class BitmapLoader extends AsyncTaskLoader<Bitmap> {
    
    private final String TAG = getClass().getSimpleName();
    private String fileName;
    private Bitmap picture;
    
    public BitmapLoader(Context context, Bundle args) {
        
        super(context);
        if (args != null) {
            fileName = args.getString(FILE_NAME_TO_LOAD);
        }
    }
    
    @Override
    public Bitmap loadInBackground() {
        
        if (DEBUG) {
            Log.d(TAG, "loadInBackground: ");
        }
        return LoadPicture(fileName);
    }
    
   
   
   Bitmap LoadPicture(String fileName){
       Bitmap newBitmap = null;
       try {
           newBitmap = BitmapFactory.decodeFile(fileName);
       }catch (OutOfMemoryError e){
    
       }
       
    return newBitmap;
   }
   
    private Bitmap loadPictureFromAssets(String fileName) {
        
        if (DEBUG) {
            Log.d(TAG, "loadTextFromAssets: ");
        }
        Bitmap newBitmap = null;
        InputStream inputStream = null;
        StringBuilder builder = new StringBuilder();
        String data;
        try {
            inputStream = getContext().getAssets().open(NO_PICTURE);
            newBitmap = BitmapFactory.decodeStream(inputStream);
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