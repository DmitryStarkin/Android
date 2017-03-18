package com.hplasplas.task3.loaders;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.content.AsyncTaskLoader;
import android.util.Log;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

import static com.hplasplas.task3.setting.Constants.DEBUG;

/**
 * Created by StarkinDG on 11.02.2017.
 */

public class TextLoader extends AsyncTaskLoader<String> {
    
    private final String TAG = getClass().getSimpleName();
    private String mFileName;
    private String mTextData;
    
    public TextLoader(Context context, Bundle args) {
        
        super(context);
        if (args != null) {
            mFileName = args.getString("mFileName");
        }
    }
    
    @Override
    public String loadInBackground() {
        
        if (DEBUG) {
            Log.d(TAG, "loadInBackground: ");
        }
        return loadTextFromAssets(mFileName);
    }
    
    private String loadTextFromAssets(String fileName) {
        
        if (DEBUG) {
            Log.d(TAG, "loadTextFromAssets: ");
        }
        InputStream inputStream = null;
        StringBuilder builder = new StringBuilder();
        String data;
        try {
            inputStream = getContext().getAssets().open(fileName);
            InputStreamReader isr = new InputStreamReader(inputStream);
            BufferedReader reader = new BufferedReader(isr);
            
            while ((data = reader.readLine()) != null) {
                builder.append(data);
                builder.append('\n');
            }
            data = builder.toString();
        } catch (IOException e) {
            e.printStackTrace();
            data = null;
        } finally {
            if (inputStream != null) {
                try {
                    inputStream.close();
                } catch (IOException e1) {
                    e1.printStackTrace();
                }
            }
        }
        return data;
    }
    
    @Override
    public void deliverResult(String data) {
        
        if (DEBUG) {
            Log.d(TAG, "deliverResult: ");
        }
        mTextData = data;
        if (isStarted()) {
            super.deliverResult(data);
        }
    }
    
    @Override
    protected void onStartLoading() {
        
        if (DEBUG) {
            Log.d(TAG, "onStartLoading: ");
        }
        if (mTextData != null) {
            deliverResult(mTextData);
        } else {
            forceLoad();
        }
    }
}