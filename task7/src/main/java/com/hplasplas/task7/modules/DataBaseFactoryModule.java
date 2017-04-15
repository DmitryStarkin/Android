package com.hplasplas.task7.modules;

import android.content.Context;

import com.hplasplas.task7.utils.CityDbFactory;
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
    public DataBaseFactory provideDownloader(Context context){
        
        return  new CityDbFactory(context);
    }
}
