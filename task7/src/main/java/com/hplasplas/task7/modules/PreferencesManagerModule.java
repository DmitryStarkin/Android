package com.hplasplas.task7.modules;

import android.content.Context;

import com.hplasplas.task7.managers.PreferencesManager;

import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;

/**
 * Created by StarkinDG on 26.04.2017.
 */

@Module
public class PreferencesManagerModule {
    
    @Provides
    @Singleton
    public PreferencesManager providePreferencesManager(Context context){
        
        return  new PreferencesManager(context);
    }
}
