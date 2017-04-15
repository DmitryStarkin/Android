package com.hplasplas.task7.modules;

import com.hplasplas.task7.interfaces.OpenWeatherMapApi;

import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;
import retrofit2.Retrofit;

/**
 * Created by StarkinDG on 15.04.2017.
 */

@Module
public class OpenWeatherMapApiModule {
    
    @Provides
    @Singleton
    public OpenWeatherMapApi provideOpenWeatherMapApi(Retrofit retrofit){
        return  retrofit.create(OpenWeatherMapApi.class);
    }
}
