package com.hplasplas.task3.Loaders;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.content.AsyncTaskLoader;
import android.util.Log;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

import static com.hplasplas.task3.Setting.Constants.DEBUG;

/**
 * Created by StarkinDG on 11.02.2017.
 */

public class TextLoader extends AsyncTaskLoader<String> {
    
    private final String TAG = getClass().getSimpleName();
    private String fileName;
    private Context myContext;
    private String textData;
    
    public TextLoader(Context context, Bundle args) {
        
        super(context);
        myContext = context;
        if (args != null) {
            fileName = args.getString("fileName");
        }
    }
    
    @Override
    public String loadInBackground() {
        
        if (DEBUG) {
            Log.d(TAG, "loadInBackground: ");
        }
        return loadTextFromAssets(fileName);
    }
    
    private String loadTextFromAssets(String fileName) {
        
        if (DEBUG) {
            Log.d(TAG, "loadTextFromAssets: ");
        }
        InputStream inputStream = null;
        StringBuilder builder = new StringBuilder();
        
        try {
            inputStream = myContext.getAssets().open(fileName);
            InputStreamReader isr = new InputStreamReader(inputStream);
            BufferedReader reader = new BufferedReader(isr);
            String line;
            
            while ((line = reader.readLine()) != null) {
                builder.append(line);
                builder.append('\n');
            }
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        } finally {
            if (inputStream != null) {
                try {
                    inputStream.close();
                } catch (IOException e1) {
                    e1.printStackTrace();
                }
            }
        }
        return builder.toString();
    }
    
    @Override
    public void deliverResult(String data) {
        
        if (DEBUG) {
            Log.d(TAG, "deliverResult: ");
        }
        textData = data;
        if (isStarted()) {
            super.deliverResult(data);
        }
    }
    
    @Override
    protected void onStartLoading() {
        
        if (DEBUG) {
            Log.d(TAG, "onStartLoading: ");
        }
        if (textData != null) {
            deliverResult(textData);
        } else {
            forceLoad();
        }
    }
}