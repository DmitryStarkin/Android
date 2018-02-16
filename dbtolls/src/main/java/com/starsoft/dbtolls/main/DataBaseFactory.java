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

/**
 * Created by StarkinDG on 12.04.2017.
 */

import android.database.sqlite.SQLiteDatabase;

public interface DataBaseFactory {
    
    void createDataBase(SQLiteDatabase db, DataBaseTolls dataBaseTolls, DataBaseTolls.DbReplacer replacer);
    
    boolean beforeOpenDataBase(SQLiteDatabase db, DataBaseTolls dataBaseTolls);
    
    void UpgradeDataBase(SQLiteDatabase db, int oldVersion, int newVersion, DataBaseTolls dataBaseTolls, DataBaseTolls.DbReplacer replacer);
}
