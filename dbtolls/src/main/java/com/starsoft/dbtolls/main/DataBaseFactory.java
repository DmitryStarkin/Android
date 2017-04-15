package com.starsoft.dbtolls.main;

/**
 * Created by StarkinDG on 12.04.2017.
 */

import android.database.sqlite.SQLiteDatabase;

public interface DataBaseFactory {
    
    void createDataBase(SQLiteDatabase db, DataBaseTolls dataBaseTolls, DataBaseTolls.DbReplacer replacer);
    
    boolean beforeOpenDataBase(SQLiteDatabase db, DataBaseTolls dataBaseTolls);
    
    void UpgradeDataBase(SQLiteDatabase db, int oldVersion, int newVersion, DataBaseTolls dataBaseTolls);
}
