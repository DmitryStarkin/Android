package com.hplasplas.task3.Loaders;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.content.AsyncTaskLoader;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

/**
 * Created by StarkinDG on 11.02.2017.
 */

public class TextLoader extends AsyncTaskLoader<String> {

    public final String TAG = getClass().getSimpleName();
    private String fileName;
    private Context myContext;
    
    public TextLoader(Context context, Bundle args) {

        super(context);
        myContext = context;
        if (args != null) {
            fileName = args.getString("fileName");
        }
    }

    @Override
    public String loadInBackground() {

        return loadTextFromAssets(fileName);
    }

    private String loadTextFromAssets(String fileName) {

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
            if (inputStream != null) {
                try {
                    inputStream.close();
                } catch (IOException e1) {
                    e1.printStackTrace();
                }
            }
            return null;
        }

        return builder.toString();
    }

    @Override
    protected void onStartLoading() {

        super.onStartLoading();
        forceLoad();
    }
}
