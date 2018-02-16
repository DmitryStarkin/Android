/*
 * Copyright © 2018. Dmitry Starkin Contacts: t0506803080@gmail.com
 *
 * Licensed under the Apache License, Version 2.0 (the «License»);
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * //www.apache.org/licenses/LICENSE-2.0
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an «AS IS» BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

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
