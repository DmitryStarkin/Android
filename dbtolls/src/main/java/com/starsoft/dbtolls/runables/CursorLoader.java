package com.starsoft.dbtolls.runables;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.starsoft.dbtolls.main.DataBaseTolls;

import static com.starsoft.dbtolls.setting.Constants.MESSAGE_ERROR;
import static com.starsoft.dbtolls.setting.Constants.MESSAGE_GET_CURSOR;

/**
 * Created by StarkinDG on 12.04.2017.
 */

public class CursorLoader extends DbWorker {
    
    private CursorGetter mCursorGetter;
    private Cursor mCursor;
    private String[] mArgs;
    
    public CursorLoader(int tag, CursorGetter getter, String... args) {
        
        mTag = tag;
        mCursorGetter = getter;
        mArgs = args;
    }
    
    @Override
    public void run() {
        
        try {
            Thread.sleep(800);
            mCursor = mCursorGetter.getCursor(DataBaseTolls.getInstance().getDataBase(), mArgs);
            sendHandlerMessage(MESSAGE_GET_CURSOR);
        } catch (Exception e){
            e.printStackTrace();
            mThrowable = e;
            if(mCursor != null){
                mCursor.close();
            }
            sendHandlerMessage(MESSAGE_ERROR);
        }
    }
    
    public void onPostCursorLoad() {
        
        try {
            DataBaseTolls.getInstance().onCursorLoaded(mTag, mCursor);
        } finally {
            clearReference();
        }
    }
    
    @Override
    void clearReference() {
        
        mCursorGetter = null;
        mCursor = null;
        mArgs = null;
    }
    
    public interface CursorGetter {
        
        Cursor getCursor(SQLiteDatabase dataBase, String... args) throws Exception;
    }
}
