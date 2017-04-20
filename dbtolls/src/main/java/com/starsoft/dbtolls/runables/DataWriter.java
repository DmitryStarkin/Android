package com.starsoft.dbtolls.runables;

import android.database.sqlite.SQLiteDatabase;

import com.starsoft.dbtolls.main.DataBaseTolls;

import static com.starsoft.dbtolls.setting.Constants.MESSAGE_ERROR;
import static com.starsoft.dbtolls.setting.Constants.MESSAGE_DATA_RECORDED;

/**
 * Created by StarkinDG on 12.04.2017.
 */

public class DataWriter<T> extends DbWorker {
    
    private DBWriter<T> mDBWriter;
    private boolean result;
    private T mArgs;
    
    
    public DataWriter(int tag, DBWriter<T> writer, T args) {
        
        mDBWriter = writer;
        mTag = tag;
        mArgs = args;
    }
    
    @Override
    public void run() {
        
        try {
            mDBWriter.writeData(DataBaseTolls.getInstance().getDataBase(), mArgs);
            result = true;
            sendHandlerMessage(MESSAGE_DATA_RECORDED);
        } catch (Exception e) {
            e.printStackTrace();
            mThrowable = e;
            result = false;
            sendHandlerMessage(MESSAGE_ERROR);
            sendHandlerMessage(MESSAGE_DATA_RECORDED);
        }
    }
    
    public void onPostWrite() {
        
        try {
            DataBaseTolls.getInstance().onDataWrite(mTag, result);
        } finally {
            clearReference();
        }
    }
    
    @Override
    void clearReference() {
        
        mDBWriter = null;
    }
    
    public interface DBWriter<T> {
        
        void writeData(SQLiteDatabase dataBase, T args) throws Exception;
    }
}