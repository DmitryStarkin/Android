package com.hplasplas.task6;

import android.app.Application;

/**
 * Created by StarkinDG on 26.03.2017.
 */

public class ThisApplication extends Application {
    private static ThisApplication instance;
    
    public static ThisApplication getInstance() {
        return instance;
    }
    
    @Override
    public void onCreate() {
        instance = this;
        super.onCreate();
    }
}
