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
    String[] mArgs;
    
    public CursorLoader(CursorGetter getter, String... args){
        
        
        mCursorGetter = getter;
        mArgs = args;
    }
    
    @Override
    public void run() {
        
        mCursor = mCursorGetter.getCursor(DataBaseTolls.getInstance().getDataBase(), mArgs);
        
        Message message = DataBaseTolls.getInstance().getDBHandler().obtainMessage(MESSAGE_GET_CURSOR, this);
        message.sendToTarget();
                
    }
    
    public void onPostCursorLoad(){
        
        try{
            DataBaseTolls.getInstance().onCursorLoaded(mCursor);
        }finally {
            clearReference();
        }
    }
    
    private void clearReference(){
        
        mCursorGetter = null;
        mCursor = null;
        mArgs = null;
    }
    
    public interface CursorGetter {
        
        Cursor getCursor(SQLiteDatabase dataBase, String... args);
    }
}
