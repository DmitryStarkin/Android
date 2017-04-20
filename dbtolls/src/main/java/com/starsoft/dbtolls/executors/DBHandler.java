package com.starsoft.dbtolls.executors;

import android.os.Handler;
import android.os.Looper;
import android.os.Message;

import com.starsoft.dbtolls.main.DataBaseTolls;
import com.starsoft.dbtolls.runables.CursorLoader;
import com.starsoft.dbtolls.runables.DataWriter;
import com.starsoft.dbtolls.runables.DbWorker;

import static com.starsoft.dbtolls.setting.Constants.MESSAGE_CLOSE_DB;
import static com.starsoft.dbtolls.setting.Constants.MESSAGE_ERROR;
import static com.starsoft.dbtolls.setting.Constants.MESSAGE_CURSOR_RECEIVED;
import static com.starsoft.dbtolls.setting.Constants.MESSAGE_DATA_RECORDED;

/**
 * Created by StarkinDG on 12.04.2017.
 */

public class DBHandler extends Handler {
    
    public DBHandler() {
        
        super(Looper.getMainLooper());
    }
    
    @Override
    public void handleMessage(Message msg) {
        
        if (msg.what == MESSAGE_CURSOR_RECEIVED) {
            ((CursorLoader) msg.obj).onPostCursorLoad();
        } else if (msg.what == MESSAGE_DATA_RECORDED) {
            ((DataWriter) msg.obj).onPostWrite();
        } else if (msg.what == MESSAGE_CLOSE_DB) {
            ((DataBaseTolls) msg.obj).closeDb();
        } else if (msg.what == MESSAGE_ERROR){
            ((DbWorker)msg.obj).onError();
        }
    }
}
