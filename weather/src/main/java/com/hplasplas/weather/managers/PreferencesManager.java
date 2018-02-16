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
