package com.starsoft.dbtolls.main;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * Created by StarkinDG on 15.04.2017.
 */

class DBHelper extends SQLiteOpenHelper {
    
    private DataBaseFactory mDataBaseFactory;
    
    public DBHelper(Context context, String name, SQLiteDatabase.CursorFactory factory, int version, DataBaseFactory dBFactory) {
        
        super(context, name, factory, version);
        mDataBaseFactory = dBFactory;
    }
    
    @Override
    public void onCreate(SQLiteDatabase db) {
        
        mDataBaseFactory.createDataBase(db, DataBaseTolls.getInstance(), DataBaseTolls.getInstance().new DbReplacer());
    }
    
    @Override
    public void onOpen(SQLiteDatabase db) {
        
        if (!mDataBaseFactory.beforeOpenDataBase(db, DataBaseTolls.getInstance())) {
            //TODO default open command
        }
    }
    
    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        
        mDataBaseFactory.UpgradeDataBase(db, oldVersion, newVersion, DataBaseTolls.getInstance(), DataBaseTolls.getInstance().new DbReplacer());
    }
}
