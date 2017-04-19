package com.starsoft.dbtolls.runables;

import android.os.Message;

import com.starsoft.dbtolls.main.DataBaseTolls;

/**
 * Created by StarkinDG on 19.04.2017.
 */

public abstract class DbWorker implements Runnable {
    
     int mTag;
     Throwable mThrowable;
    
    public void onError() {
        
        try {
            DataBaseTolls.getInstance().onError(mTag, mThrowable);
        } finally {
            clearReference();
        }
    }
    
    void sendHandlerMessage(int hMessage){
        
        Message message = DataBaseTolls.getInstance().getDBHandler().obtainMessage(hMessage, this);
        message.sendToTarget();
    }
    
    @Override
    public boolean equals(Object obj) {
        
        return (obj instanceof DbWorker) && (((DbWorker) obj).mTag == this.mTag);
    }
    
    abstract void clearReference();
}
