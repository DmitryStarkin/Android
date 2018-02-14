/**
 * Copyright © 2017 Dmitry Starkin Contacts: t0506803080@gmail.com. All rights reserved
 *
 */
package com.hplasplas.weather.modules;

import android.content.Context;

import com.hplasplas.weather.managers.WeatherImageManager;
import com.squareup.picasso.Picasso;

import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;

/**
 * Created by StarkinDG on 25.04.2017.
 */

@Module
public class WeatherImageManagerModule {
    
    @Provides
    @Singleton
    public WeatherImageManager provideWeatherImageManager(Context context, Picasso picasso){
        
        return  new WeatherImageManager(context, picasso);
    }
}
