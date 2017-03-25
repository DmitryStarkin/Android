package com.hplasplas.task6.util;

import android.os.Handler;
import android.os.Looper;
import android.os.Message;

import com.hplasplas.task6.loaders.BitmapInThreadLoader;

/**
 * Created by StarkinDG on 24.03.2017.
 */

public class MainHandler extends Handler {
    
    private static MainHandler sHandler = null;
    
    private MainHandler() {
        
        super(Looper.getMainLooper());
    }
    
    public static synchronized MainHandler getHandler() {
        
        if (sHandler == null) {
            sHandler = new MainHandler();
        }
        return sHandler;
    }
    
    @Override
    public void handleMessage(Message msg) {
        
        ((BitmapInThreadLoader) msg.obj).onPostBitmapLoad();
    }
}

