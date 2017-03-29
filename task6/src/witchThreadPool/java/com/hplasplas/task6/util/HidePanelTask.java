package com.hplasplas.task6.util;

import android.os.Message;

import java.lang.ref.WeakReference;
import java.util.TimerTask;

import static com.hplasplas.task6.setting.Constants.MESSAGE_PANEL_MUST_HIDE;

/**
 * Created by StarkinDG on 29.03.2017.
 */

public class HidePanelTask extends TimerTask {
    
    private WeakReference<HideBottomPanelListener> mListener;
    
    public HidePanelTask(HideBottomPanelListener listener) {
        
        mListener = new WeakReference<>(listener);
    }
    
    @Override
    public void run() {
        
        if (mListener.get() != null) {
            Message message = MainHandler.getHandler().obtainMessage(MESSAGE_PANEL_MUST_HIDE, this);
            message.sendToTarget();
        }
    }
    
    void hidePanel() {
        
        if (mListener.get() != null) {
            mListener.get().hidePanel();
        }
    }
    
    public interface HideBottomPanelListener {
        
        void hidePanel();
    }
}
