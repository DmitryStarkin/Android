package com.hplasplas.task6.util;

import android.os.Handler;
import android.os.Looper;
import android.os.Message;

import com.hplasplas.task6.loaders.BitmapInThreadLoader;
import com.hplasplas.task6.managers.CollapsedElementsManager;

import static com.hplasplas.task6.setting.Constants.MESSAGE_BITMAP_LOAD;
import static com.hplasplas.task6.setting.Constants.MESSAGE_PANEL_MUST_HIDE;

/**
 * Created by StarkinDG on 24.03.2017.
 */

public class MainHandler extends Handler {
    
    private static MainHandler instance = null;
    
    private MainHandler() {
        
        super(Looper.getMainLooper());
    }
    
    public static synchronized MainHandler getInstance() {
        
        if (instance == null) {
            instance = new MainHandler();
        }
        return instance;
    }
    
    @Override
    public void handleMessage(Message msg) {
        
        if (msg.what == MESSAGE_BITMAP_LOAD) {
            ((BitmapInThreadLoader) msg.obj).onPostBitmapLoad();
        } else if (msg.what == MESSAGE_PANEL_MUST_HIDE) {
            ((CollapsedElementsManager) msg.obj).hideBottomPanel();
        }
    }
}

