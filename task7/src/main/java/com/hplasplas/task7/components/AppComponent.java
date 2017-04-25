package com.hplasplas.task7.components;

import com.hplasplas.task7.activitys.MainActivity;
import com.hplasplas.task7.adapters.ForecastAdapter;
import com.hplasplas.task7.modules.AppModule;
import com.hplasplas.task7.modules.DataBaseFactoryModule;
import com.hplasplas.task7.modules.DataTimeUtilsModule;
import com.hplasplas.task7.modules.DbTollsModule;
import com.hplasplas.task7.modules.DownloaderModule;
import com.hplasplas.task7.modules.GSONModule;
import com.hplasplas.task7.modules.MessageManagerModule;
import com.hplasplas.task7.modules.OpenWeatherMapApiModule;
import com.hplasplas.task7.modules.PicassoModule;
import com.hplasplas.task7.modules.RetrofitModule;
import com.hplasplas.task7.modules.WeatherImageManagerModule;

import javax.inject.Singleton;

import dagger.Component;

/**
 * Created by StarkinDG on 15.04.2017.
 */

@Singleton
@Component(modules = {AppModule.class, DataBaseFactoryModule.class, DbTollsModule.class,
        DownloaderModule.class, GSONModule.class, OpenWeatherMapApiModule.class, PicassoModule.class,
        RetrofitModule.class, WeatherImageManagerModule.class, DataTimeUtilsModule.class, MessageManagerModule.class})

public interface AppComponent {
    
    void inject(MainActivity mainActivity);
    void inject(ForecastAdapter forecastAdapter);
}
