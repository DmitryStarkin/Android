package com.hplasplas.task7.setting;

import com.hplasplas.task7.BuildConfig;

/**
 * Created by StarkinDG on 06.04.2017.
 */

public final class Constants {
    
    public static final boolean DEBUG = BuildConfig.DEBUG;
    
    public static final long MIL_PER_SEC = 1000;
    
    //wind directions determine
    public static final double WIND_DIRECTION_DIVIDER = 22.5;
    public static final String WIND_DIRECTION_PREFIX = "d_";
    
    //time stamps
    public static final String WEATHER_TIME_STAMP_PATTERN = "E, d MMM y, HH:mm";
    public static final String SUN_TIME_STAMP_PATTERN = "HH:mm";
    public static final String REFRESHING_TIME_STAMP_PATTERN = "mm 'min' ss 'sec'";
    
    //request values
    public static final long MIN_REQUEST_INTERVAL = 600000;
    
    //preferences values
    public static final String PREFERENCES_FILE = "myPref";
    public static final String PREF_FOR_CURRENT_CITY_ID = "currentCity";
    public static final String PREF_FOR_CURRENT_WEATHER_JSON_DATA = "currentWeather";
    public static final String PREF_FOR_FIFE_DAYS_WEATHER_JSON_DATA = "fifeDaysWeather";
    public static final String LAST_REQUEST_TIME = "lastRequestTime";
    
    //default request values
    public static final int DEFAULT_CITY_ID = 705812;
    
    //dialog values
    public static final String MESSAGE_DIALOG_TAG = "messageDialog";
    
    //Api setting
    public static final String API_KEY = "66f1e1075104ffa9d6e1a03796632b9e";
    public static final String BASE_URL = "http://api.openweathermap.org/data/2.5/";
    public static final String CURRENT_WEATHER_URL = "weather";
    public static final String FIFE_DAY_WEATHER_URL = "forecast";
    public static final String CITY_ID_PARAMETER = "id";
    public static final String API_KEY_PARAMETER = "APPID";
    public static final String UNITS_PARAMETER = "units";
    public static final String UNITS_PARAMETER_VALUE = "metric";
    public static final String ICON_DOWNLOAD_URL = "http://openweathermap.org/img/w/";
    public static final String ICON_FILE_SUFFIX = ".png";
    
    //DB seting
    public static final String DB_FILE_NAME = "city.db";
    public static final String DB_SQL_NAME = "city.sql";
    public static final int DB_VERSION = 1;
    public static final String[] COLUMNS_CITY_NAME = {"name"};
    public static final String COLUMNS_CITY_ID = "id";
    
    //db Query
    public static final String CITY_QUERYBEGIN_SEARCH_PREFIX = "SELECT _id, id, name FROM city_data WHERE name LIKE \'";
    public static final String CITY_QUERY_BEGIN_SEARCH_SUFFIX = "%\' LIMIT 10";
    public static final String CITY_QUERY_FULL_SEARCH_PREFIX = "SELECT _id, id, name FROM city_data WHERE name = \'";
    public static final String CITY_QUERY_FULL_SEARCH_SUFFIX = "\'";
    
    //other
    public static final String WEATHER_DRAWABLE_PREFIX = "w_";
    public static final int REFRESH_INDICATOR_OFFSET = 10;
}

