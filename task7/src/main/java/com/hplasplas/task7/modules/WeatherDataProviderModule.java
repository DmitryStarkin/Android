package com.hplasplas.task7.modules;

import android.content.Context;

import com.google.gson.Gson;
import com.hplasplas.task7.interfaces.OpenWeatherMapApi;
import com.hplasplas.task7.managers.PreferencesManager;
import com.hplasplas.task7.managers.WeatherDataProvider;
import com.hplasplas.task7.utils.DataTimeUtils;

import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;

/**
 * Created by StarkinDG on 15.05.2017.
 */

@Module
public class WeatherDataProviderModule {
    
    @Provides
    @Singleton
    public WeatherDataProvider provideWeatherDataProvider(Context context, OpenWeatherMapApi openWeatherMapApi,
                                                          PreferencesManager preferencesManager, Gson gson, DataTimeUtils dataTimeUtils){
        
        return  new WeatherDataProvider(context, openWeatherMapApi, preferencesManager, gson, dataTimeUtils);
    }
}
