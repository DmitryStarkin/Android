package com.starsoft.dbtolls.executors;

import android.os.Handler;
import android.os.Looper;
import android.os.Message;

import com.starsoft.dbtolls.main.DataBaseTolls;
import com.starsoft.dbtolls.runables.CursorLoader;
import com.starsoft.dbtolls.runables.DataWriter;

import static com.starsoft.dbtolls.setting.Constants.MESSAGE_CLOSE_DB;
import static com.starsoft.dbtolls.setting.Constants.MESSAGE_GET_CURSOR;
import static com.starsoft.dbtolls.setting.Constants.MESSAGE_WRITE_DATA;

/**
 * Created by StarkinDG on 12.04.2017.
 */

public class DBHandler extends Handler {
    
    public DBHandler() {
        
        super(Looper.getMainLooper());
    }
    
    @Override
    public void handleMessage(Message msg) {
        
        if (msg.what == MESSAGE_GET_CURSOR) {
            ((CursorLoader) msg.obj).onPostCursorLoad();
        } else if (msg.what == MESSAGE_WRITE_DATA) {
            ((DataWriter) msg.obj).onPostWrite();
        } else if (msg.what == MESSAGE_CLOSE_DB) {
            ((DataBaseTolls) msg.obj).closeDb();
        }
    }
}
