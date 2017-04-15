package com.hplasplas.task7.modules;

import android.content.Context;

import com.starsoft.dbtolls.main.DataBaseFactory;
import com.starsoft.dbtolls.main.DataBaseTolls;
import com.starsoft.dbtolls.main.DbTollsBuilder;

import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;

import static com.hplasplas.task7.setting.Constants.DB_FILE_NAME;
import static com.hplasplas.task7.setting.Constants.DB_VERSION;
import static com.hplasplas.task7.setting.Constants.NUMBER_THREAD_FOR_QUERY;

/**
 * Created by StarkinDG on 15.04.2017.
 */

@Module
public class DbTollsModule {
    
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
