package com.starsoft.dbtolls.runables;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Message;

import com.starsoft.dbtolls.main.DataBaseTolls;

import static com.starsoft.dbtolls.setting.Constants.MESSAGE_GET_CURSOR;

/**
 * Created by StarkinDG on 12.04.2017.
 */

public class CursorLoader implements Runnable {
    
    private CursorGetter mCursorGetter;
    private Cursor mCursor;
    private String[] mArgs;
    private int mTag;
    
    public CursorLoader(int tag, CursorGetter getter, String... args) {
        
        mTag = tag;
        mCursorGetter = getter;
        mArgs = args;
    }
    
    @Override
    public void run() {
        
        try {
            Thread.sleep(800);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        mCursor = mCursorGetter.getCursor(DataBaseTolls.getInstance().getDataBase(), mArgs);
        
        Message message = DataBaseTolls.getInstance().getDBHandler().obtainMessage(MESSAGE_GET_CURSOR, this);
        message.sendToTarget();
    }
    
    public void onPostCursorLoad() {
        
        try {
            DataBaseTolls.getInstance().onCursorLoaded(mTag, mCursor);
        } finally {
            clearReference();
        }
    }
    
    private void clearReference() {
        
        mCursorGetter = null;
        mCursor = null;
        mArgs = null;
    }
    
    @Override
    public boolean equals(Object obj) {
        
        return (obj instanceof CursorLoader) && (((CursorLoader) obj).mTag == this.mTag);
    }
    
    public interface CursorGetter {
        
        Cursor getCursor(SQLiteDatabase dataBase, String... args);
    }
}
