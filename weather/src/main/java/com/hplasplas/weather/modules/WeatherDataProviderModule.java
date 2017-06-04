package com.hplasplas.weather.modules;

import android.content.Context;

import com.google.gson.Gson;
import com.hplasplas.weather.interfaces.OpenWeatherMapApi;
import com.hplasplas.weather.managers.PreferencesManager;
import com.hplasplas.weather.managers.WeatherDataProvider;
import com.hplasplas.weather.utils.DataTimeUtils;

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
