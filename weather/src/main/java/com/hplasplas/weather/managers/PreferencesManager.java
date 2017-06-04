package com.hplasplas.weather.managers;

import android.content.Context;
import android.content.SharedPreferences;

import static android.content.Context.MODE_PRIVATE;
import static com.hplasplas.weather.setting.Constants.DEFAULT_CITY_ID;
import static com.hplasplas.weather.setting.Constants.LAST_REQUEST_TIME;
import static com.hplasplas.weather.setting.Constants.PREFERENCES_FILE;
import static com.hplasplas.weather.setting.Constants.PREF_FOR_CURRENT_CITY_ID;
import static com.hplasplas.weather.setting.Constants.PREF_FOR_CURRENT_WEATHER_JSON_DATA;
import static com.hplasplas.weather.setting.Constants.PREF_FOR_FIFE_DAYS_FORECAST_JSON_DATA;

/**
 * Created by StarkinDG on 06.04.2017.
 */

public class PreferencesManager {
    
    private Context appContext;
    
    public  PreferencesManager(Context context){
        
        appContext = context;
    }
    
    private SharedPreferences getPreferences(Context context) {
        
        return context.getSharedPreferences(PREFERENCES_FILE, MODE_PRIVATE);
    }
    
    public void writeCityId(int id){
        getPreferences(appContext).edit()
                .putInt(PREF_FOR_CURRENT_CITY_ID, id)
                .apply();
    }
    
    public int readCityId(){
        
        return getPreferences(appContext).getInt(PREF_FOR_CURRENT_CITY_ID, DEFAULT_CITY_ID);
    }
    
    public void writeCurrentWeatherData(String data){
        
        getPreferences(appContext).edit()
                .putString(PREF_FOR_CURRENT_WEATHER_JSON_DATA, data)
                .apply();
    }
    
    public void writeForecastWeatherData(String data){
        
        getPreferences(appContext).edit()
                .putString(PREF_FOR_FIFE_DAYS_FORECAST_JSON_DATA, data)
                .apply();
    }
    
    public String readCurrentWeatherData(){
        
        return getPreferences(appContext).getString(PREF_FOR_CURRENT_WEATHER_JSON_DATA, null);
    }
    
    public String readForecastWeatherData(){
        
        return getPreferences(appContext).getString(PREF_FOR_FIFE_DAYS_FORECAST_JSON_DATA, null);
    }
    
    public void writeCurrentTime(){
    
        getPreferences(appContext).edit()
                .putLong(LAST_REQUEST_TIME, System.currentTimeMillis())
                .apply();
    }
    
    public long readLastRequestTime(){
        
        return  getPreferences(appContext).getLong(LAST_REQUEST_TIME, 0);
    }
}
