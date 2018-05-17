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
package com.hplasplas.weather.components;

import com.hplasplas.weather.activitys.MainActivity;
import com.hplasplas.weather.activitys.SearchPlaceActivity;
import com.hplasplas.weather.adapters.ForecastAdapter;
import com.hplasplas.weather.modules.AppModule;
import com.hplasplas.weather.modules.DataTimeUtilsModule;
import com.hplasplas.weather.modules.DbTollsModule;
import com.hplasplas.weather.modules.DownloaderModule;
import com.hplasplas.weather.modules.GSONModule;
import com.hplasplas.weather.modules.MessageManagerModule;
import com.hplasplas.weather.modules.OpenWeatherMapApiModule;
import com.hplasplas.weather.modules.PicassoModule;
import com.hplasplas.weather.modules.PreferencesManagerModule;
import com.hplasplas.weather.modules.RetrofitModule;
import com.hplasplas.weather.modules.SearchPlaceProviderModule;
import com.hplasplas.weather.modules.WeatherDataProviderModule;
import com.hplasplas.weather.modules.WeatherImageManagerModule;
import com.hplasplas.weather.services.WeatherWidgetService;

import javax.inject.Singleton;

import dagger.Component;

/**
 * Created by StarkinDG on 14.05.2017.
 */

@Singleton
@Component(modules = {AppModule.class, DbTollsModule.class, SearchPlaceProviderModule.class,
        DownloaderModule.class, GSONModule.class, OpenWeatherMapApiModule.class, PicassoModule.class,
        RetrofitModule.class, WeatherImageManagerModule.class, DataTimeUtilsModule.class,
        MessageManagerModule.class, PreferencesManagerModule.class, WeatherDataProviderModule.class})

public interface AppComponent {
    
    void inject(MainActivity mainActivity);
    void inject(SearchPlaceActivity searchPlaceActivity);
    void inject(ForecastAdapter forecastAdapter);
    void inject(WeatherWidgetService weatherWidgetService);
}
