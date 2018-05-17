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
package com.hplasplas.weather.modules;

import android.content.Context;

import com.hplasplas.weather.utils.CityDbFactory;
import com.starsoft.dbtolls.main.DataBaseFactory;
import com.starsoft.dbtolls.main.DataBaseTolls;
import com.starsoft.dbtolls.main.DbTollsBuilder;

import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;

/**
 * Created by StarkinDG on 15.04.2017.
 */

@Module
public class DbTollsModule {
    
    private static final String DB_FILE_NAME = "city.db";
    private static final int DB_VERSION = 1;
    private static final int NUMBER_THREAD_FOR_QUERY = 1;
    
    @Provides
    @Singleton
    public DataBaseFactory provideDataBaseFactory(Context context){
        
        return  new CityDbFactory(context, DB_FILE_NAME);
    }
    
    @Provides
    @Singleton
    public DataBaseTolls provideDataBaseTolls(Context context, DataBaseFactory dataBaseFactory){
        
        return  new DbTollsBuilder().setName(DB_FILE_NAME)
                .setVersion(DB_VERSION)
                .setNumberThreadsForProcessing(NUMBER_THREAD_FOR_QUERY)
                .setDataBaseFactory(dataBaseFactory)
                .buildWith(context);
    }
}
