/**
 * Copyright © 2017 Dmitry Starkin Contacts: t0506803080@gmail.com. All rights reserved
 *
 */
package com.hplasplas.weather.modules;

import android.content.Context;

import com.hplasplas.weather.utils.CityDbFactory;
import com.starsoft.dbtolls.main.DataBaseFactory;

import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;

/**
 * Created by StarkinDG on 15.04.2017.
 */

@Module
public class DataBaseFactoryModule {
    
    @Provides
    @Singleton
    public DataBaseFactory provideDataBaseFactory(Context context){
        
        return  new CityDbFactory(context);
    }
}
