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
