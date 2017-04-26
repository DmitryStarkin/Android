package com.hplasplas.task7;

import android.app.Application;

import com.hplasplas.task7.components.AppComponent;
import com.hplasplas.task7.components.DaggerAppComponent;
import com.hplasplas.task7.modules.AppModule;
import com.hplasplas.task7.modules.DataBaseFactoryModule;
import com.hplasplas.task7.modules.DataTimeUtilsModule;
import com.hplasplas.task7.modules.DbTollsModule;
import com.hplasplas.task7.modules.DownloaderModule;
import com.hplasplas.task7.modules.GSONModule;
import com.hplasplas.task7.modules.MessageManagerModule;
import com.hplasplas.task7.modules.OpenWeatherMapApiModule;
import com.hplasplas.task7.modules.PicassoModule;
import com.hplasplas.task7.modules.PreferencesManagerModule;
import com.hplasplas.task7.modules.RetrofitModule;
import com.hplasplas.task7.modules.WeatherImageManagerModule;

/**
 * Created by StarkinDG on 06.04.2017.
 */

public class App extends Application {
    
    private static AppComponent sAppComponent;
    
    public static AppComponent getAppComponent() {
        
        return sAppComponent;
    }
    
    @Override
    public void onCreate() {
        super.onCreate();
        sAppComponent = buildComponent();
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
               .build();
    }
}
