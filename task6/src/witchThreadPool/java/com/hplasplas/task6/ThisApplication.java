package com.hplasplas.task6;

import android.app.Application;
import android.content.Context;

/**
 * Created by StarkinDG on 26.03.2017.
 */

public class ThisApplication extends Application {
    
    private static ThisApplication instance;
    
    public static synchronized ThisApplication getInstance() {
        return instance;
    }
    
    public static synchronized Context getMainContext() {
        return instance.getApplicationContext();
    }
    
    @Override
    public void onCreate() {
        instance = this;
        super.onCreate();
    }
}
