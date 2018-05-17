/*
 * Copyright © 2018. Dmitry Starkin Contacts: t0506803080@gmail.com
 *
 * This file is part of weather
 *
 *     weather is free software: you can redistribute it and/or modify
 *     it under the terms of the GNU General Public License as published by
 *     the Free Software Foundation, either version 3 of the License, or
 *     (at your option) any later version.
 *
 *    weather is distributed in the hope that it will be useful,
 *     but WITHOUT ANY WARRANTY; without even the implied warranty of
 *     MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *     GNU General Public License for more details.
 *
 *     You should have received a copy of the GNU General Public License
 *     along with weather  If not, see <http://www.gnu.org/licenses/>.
 */
package com.hplasplas.weather.utils;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;

import com.starsoft.dbtolls.main.DataBaseFactory;
import com.starsoft.dbtolls.main.DataBaseTolls;

import java.io.IOException;
import java.io.InputStream;

/**
 * Created by StarkinDG on 14.04.2017.
 */

public class CityDbFactory implements DataBaseFactory {
    
    private Context appContext;
    private String mDbFileName;
    
    public CityDbFactory(Context context, String dbFileName){
        appContext = context;
        mDbFileName = dbFileName;
    }
    
    @Override
    public void createDataBase(SQLiteDatabase db, DataBaseTolls dataBaseTolls, DataBaseTolls.DbReplacer replacer) {
        
        try {
            InputStream inputStream = appContext.getAssets().open(mDbFileName);
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
