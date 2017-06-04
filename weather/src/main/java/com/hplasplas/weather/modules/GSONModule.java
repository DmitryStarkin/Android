package com.hplasplas.weather.modules;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;

/**
 * Created by StarkinDG on 15.04.2017.
 */

@Module
public class GSONModule {
    
    @Provides
    @Singleton
    public Gson provideRetrofit(){
        
        return  new GsonBuilder().create();
    }
}
