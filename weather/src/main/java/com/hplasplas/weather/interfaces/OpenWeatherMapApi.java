package com.hplasplas.weather.interfaces;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Query;

import static com.hplasplas.weather.setting.Constants.API_KEY_PARAMETER;
import static com.hplasplas.weather.setting.Constants.CITY_ID_PARAMETER;
import static com.hplasplas.weather.setting.Constants.CURRENT_WEATHER_URL;
import static com.hplasplas.weather.setting.Constants.FIFE_DAY_WEATHER_URL;
import static com.hplasplas.weather.setting.Constants.UNITS_PARAMETER;

/**
 * Created by StarkinDG on 06.04.2017.
 */

public interface OpenWeatherMapApi {
    
    @GET(CURRENT_WEATHER_URL)
    Call<String> getCurrentWeather(@Query(CITY_ID_PARAMETER) int cityId, @Query(UNITS_PARAMETER) String units, @Query(API_KEY_PARAMETER) String apiKey);
    
    @GET(FIFE_DAY_WEATHER_URL)
    Call<String> getFifeDaysWeather(@Query(CITY_ID_PARAMETER) int cityId, @Query(UNITS_PARAMETER) String units, @Query(API_KEY_PARAMETER) String apiKey);
}
