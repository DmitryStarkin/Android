package com.hplasplas.weather.utils;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;

import com.starsoft.dbtolls.main.DataBaseFactory;
import com.starsoft.dbtolls.main.DataBaseTolls;

import java.io.IOException;
import java.io.InputStream;

import static com.hplasplas.weather.setting.Constants.DB_FILE_NAME;

/**
 * Created by StarkinDG on 14.04.2017.
 */

public class CityDbFactory implements DataBaseFactory {
    
    private Context appContext;
    
    public CityDbFactory(Context context){
        appContext = context;
    }
    
    @Override
    public void createDataBase(SQLiteDatabase db, DataBaseTolls dataBaseTolls, DataBaseTolls.DbReplacer replacer) {
        
        try {
            InputStream inputStream = appContext.getAssets().open(DB_FILE_NAME);
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
