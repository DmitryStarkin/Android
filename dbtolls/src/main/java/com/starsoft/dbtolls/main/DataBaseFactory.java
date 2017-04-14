package com.starsoft.dbtolls.main;

/**
 * Created by StarkinDG on 12.04.2017.
 */

import android.database.sqlite.SQLiteDatabase;

public interface DataBaseFactory {
    
    
    void createDataBase(SQLiteDatabase db, DataBaseTolls myDataBaseTolls);
    boolean beforeOpenDataBase(SQLiteDatabase db, DataBaseTolls myDataBaseTolls);
    void UpgradeDataBase(SQLiteDatabase db, int oldVersion, int newVersion, DataBaseTolls myDataBaseTolls);
    
    
}
