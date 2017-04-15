package com.starsoft.dbtolls.runables;

import android.database.sqlite.SQLiteDatabase;
import android.os.Message;

import com.starsoft.dbtolls.main.DataBaseTolls;

import static com.starsoft.dbtolls.setting.Constants.MESSAGE_WRITE_DATA;

/**
 * Created by StarkinDG on 12.04.2017.
 */

public class DataWriter<T> implements Runnable {
    
    private DBWriter<T> mDBWriter;
    private boolean result;
    private T args;
    
    public DataWriter(DBWriter writer, T args) {
        
    }
    
    @Override
    public void run() {
        
        result = mDBWriter.writeData(DataBaseTolls.getInstance().getDataBase(), args);
        Message message = DataBaseTolls.getInstance().getDBHandler().obtainMessage(MESSAGE_WRITE_DATA, this);
        message.sendToTarget();
    }
    
    public void onPostWrite() {
        
        try {
            DataBaseTolls.getInstance().onDataWrite(result);
        } finally {
            clearReference();
        }
    }
    
    private void clearReference() {
        
        mDBWriter = null;
    }
    
    public interface DBWriter<T> {
        
        boolean writeData(SQLiteDatabase dataBase, T args);
    }
}