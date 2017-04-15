package com.hplasplas.task7.utils;

import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;

import com.hplasplas.task7.App;
import com.starsoft.dbtolls.main.DataBaseFactory;
import com.starsoft.dbtolls.main.DataBaseTolls;

import java.io.IOException;
import java.io.InputStream;

import static com.hplasplas.task7.setting.Constants.DB_FILE_NAME;

/**
 * Created by StarkinDG on 14.04.2017.
 */

public class CityDbFactory implements DataBaseFactory {
    
    @Override
    public void createDataBase(SQLiteDatabase db, DataBaseTolls dataBaseTolls, DataBaseTolls.DbReplacer replacer) {
        
        try {
            InputStream inputStream = App.getAppContext().getAssets().open(DB_FILE_NAME);
            replacer.replaceFrom(inputStream);
        } catch (IOException e) {
            e.printStackTrace();
            throw new SQLiteException("Fail to initial db", e);
        }
    }
    
    @Override
    public boolean beforeOpenDataBase(SQLiteDatabase db, DataBaseTolls dataBaseTolls) {
        
        return false;
    }
    
    @Override
    public void UpgradeDataBase(SQLiteDatabase db, int oldVersion, int newVersion, DataBaseTolls dataBaseTolls, DataBaseTolls.DbReplacer replacer) {
        
    }
}
