/**
 * Copyright © 2017 Dmitry Starkin Contacts: t0506803080@gmail.com. All rights reserved
 *
 */
package com.hplasplas.weather.components;

import com.hplasplas.weather.activitys.MainActivity;
import com.hplasplas.weather.adapters.ForecastAdapter;
import com.hplasplas.weather.modules.AppModule;
import com.hplasplas.weather.modules.DataBaseFactoryModule;
import com.hplasplas.weather.modules.DataTimeUtilsModule;
import com.hplasplas.weather.modules.DbTollsModule;
import com.hplasplas.weather.modules.DownloaderModule;
import com.hplasplas.weather.modules.GSONModule;
import com.hplasplas.weather.modules.MessageManagerModule;
import com.hplasplas.weather.modules.OpenWeatherMapApiModule;
import com.hplasplas.weather.modules.PicassoModule;
import com.hplasplas.weather.modules.PreferencesManagerModule;
import com.hplasplas.weather.modules.RetrofitModule;
import com.hplasplas.weather.modules.WeatherDataProviderModule;
import com.hplasplas.weather.modules.WeatherImageManagerModule;
import com.hplasplas.weather.services.WeatherWidgetService;

import javax.inject.Singleton;

import dagger.Component;

/**
 * Created by StarkinDG on 14.05.2017.
 */

@Singleton
@Component(modules = {AppModule.class, DataBaseFactoryModule.class, DbTollsModule.class,
        DownloaderModule.class, GSONModule.class, OpenWeatherMapApiModule.class, PicassoModule.class,
        RetrofitModule.class, WeatherImageManagerModule.class, DataTimeUtilsModule.class,
        MessageManagerModule.class, PreferencesManagerModule.class, WeatherDataProviderModule.class})

public interface AppComponent {
    
    void inject(MainActivity mainActivity);
    void inject(ForecastAdapter forecastAdapter);
    void inject(WeatherWidgetService weatherWidgetService);
}
