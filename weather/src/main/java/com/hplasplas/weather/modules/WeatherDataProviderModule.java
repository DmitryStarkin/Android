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
