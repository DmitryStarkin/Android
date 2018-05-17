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
package com.hplasplas.weather;

import android.app.Application;

import com.crashlytics.android.Crashlytics;
import com.crashlytics.android.core.CrashlyticsCore;
import com.hplasplas.weather.components.AppComponent;
import com.hplasplas.weather.components.DaggerAppComponent;
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
import io.fabric.sdk.android.Fabric;

/**
 * Created by StarkinDG on 06.04.2017.
 */

public class App extends Application {
    
    private static AppComponent mAppComponent;
    
    public static AppComponent getAppComponent() {
        
        return mAppComponent;
    }
    
    @Override
    public void onCreate() {
        super.onCreate();
        Crashlytics crashlyticsKit = new Crashlytics.Builder()
                .core(new CrashlyticsCore.Builder().disabled(BuildConfig.DEBUG).build())
                .build();
    
        Fabric.with(this, crashlyticsKit);
        mAppComponent = buildComponent();
    }
    
    @SuppressWarnings("deprecation")
    public AppComponent buildComponent(){
        
       return DaggerAppComponent.builder()
               .appModule(new AppModule(this))
               .dbTollsModule(new DbTollsModule())
               .searchPlaceProviderModule(new SearchPlaceProviderModule())
               .downloaderModule(new DownloaderModule())
               .gSONModule(new GSONModule())
               .openWeatherMapApiModule(new OpenWeatherMapApiModule())
               .picassoModule(new PicassoModule())
               .weatherImageManagerModule(new WeatherImageManagerModule())
               .retrofitModule(new RetrofitModule())
               .dataTimeUtilsModule(new DataTimeUtilsModule())
               .messageManagerModule(new MessageManagerModule())
               .preferencesManagerModule(new PreferencesManagerModule())
               .weatherDataProviderModule(new WeatherDataProviderModule())
               .build();
    }
}
