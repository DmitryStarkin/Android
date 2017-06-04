package com.hplasplas.weather;

import android.app.Application;

import com.hplasplas.weather.components.AppComponent;
import com.hplasplas.weather.components.DaggerAppComponent;
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
        mAppComponent = buildComponent();
    }
    
    @SuppressWarnings("deprecation")
    public AppComponent buildComponent(){
        
       return DaggerAppComponent.builder()
               .appModule(new AppModule(this))
               .dataBaseFactoryModule(new DataBaseFactoryModule())
               .dbTollsModule(new DbTollsModule())
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
