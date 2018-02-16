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

import com.google.gson.Gson;
import com.hplasplas.weather.R;
import com.hplasplas.weather.interfaces.OpenWeatherMapApi;
import com.hplasplas.weather.models.weather.current.CurrentWeather;
import com.hplasplas.weather.models.weather.forecast.FifeDaysForecast;
import com.hplasplas.weather.utils.DataTimeUtils;
import com.hplasplas.weather.utils.InternetConnectionChecker;

import java.io.IOException;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static com.hplasplas.weather.setting.Constants.API_KEY;
import static com.hplasplas.weather.setting.Constants.MIN_REQUEST_INTERVAL;
import static com.hplasplas.weather.setting.Constants.REFRESHING_TIME_STAMP_PATTERN;
import static com.hplasplas.weather.setting.Constants.UNITS_PARAMETER_VALUE;

/**
 * Created by StarkinDG on 15.05.2017.
 */

public class WeatherDataProvider {
    
    private Context mAppContext;
    private Gson mGson;
    private PreferencesManager mPreferencesManager;
    private OpenWeatherMapApi mOpenWeatherMapApi;
    private DataTimeUtils mDataTimeUtils;
    private Call<String> mCurrentWeatherCall;
    private Call<String> mForecastWeatherCall;
    private CurrentWeatherReadyListener mCurrentWeatherReadyListener;
    private ForecastReadyListener mForecastReadyListener;
    private ErrorListener mErrorListener;
    private String mMainThreadName;
    
    public WeatherDataProvider(Context context, OpenWeatherMapApi openWeatherMapApi, PreferencesManager preferencesManager,
                               Gson gson, DataTimeUtils dataTimeUtils) {
        
        mDataTimeUtils = dataTimeUtils;
        mAppContext = context;
        mGson = gson;
        mOpenWeatherMapApi = openWeatherMapApi;
        mPreferencesManager = preferencesManager;
    }
    
    public void tryEnqueueWeatherAndForecastData() {
        
        getSavedWeatherData();
        getSavedForecastData();
        if (isInternetAvailable() && refreshIntervalIsRight()) {
            enqueueWeatherAndForecastData(mPreferencesManager.readCityId());
        }
    }
    
    public void tryEnqueueWeatherAndForecastData(int cityId) {
        
        if (isInternetAvailable()) {
            enqueueWeatherAndForecastData(cityId);
        } else {
            sendError(mAppContext.getResources().getString(R.string.internet_not_available));
        }
    }
    
    public void tryEnqueueWeatherWitchMessage() {
        
        if (!refreshIntervalIsRight()) {
            String interval = mDataTimeUtils.getTimeString(MIN_REQUEST_INTERVAL - (System.currentTimeMillis() - mPreferencesManager.readLastRequestTime()),
                    REFRESHING_TIME_STAMP_PATTERN);
            sendError(mAppContext.getResources().getString(R.string.weather_refreshed, interval));
        } else if (!isInternetAvailable()) {
            
            sendError(mAppContext.getResources().getString(R.string.internet_not_available));
        } else {
            tryEnqueueWeatherAndForecastData();
        }
    }
    
    public void enqueueWeatherAndForecastData(int cityId) {
        
        mCurrentWeatherCall = mOpenWeatherMapApi.getCurrentWeather(cityId, UNITS_PARAMETER_VALUE, API_KEY);
        mForecastWeatherCall = mOpenWeatherMapApi.getFifeDaysWeather(cityId, UNITS_PARAMETER_VALUE, API_KEY);
        mCurrentWeatherCall.enqueue(new Callback<String>() {
            @Override
            public void onResponse(Call<String> call, Response<String> response) {
                
                if (response.body() != null) {
                    String currentWeatherResponse = response.body();
                    mForecastWeatherCall.enqueue(new Callback<String>() {
                        @Override
                        public void onResponse(Call<String> call, Response<String> response) {
                            
                            if (response.body() != null) {
                                writeAndPrepareCurrentWeatherData(currentWeatherResponse);
                                writeAndPrepareForecastData(response.body());
                            } else {
                                weatherGetError();
                            }
                        }
                        
                        @Override
                        public void onFailure(Call<String> call, Throwable t) {
                            
                            weatherGetError();
                        }
                    });
                } else {
                    weatherGetError();
                }
            }
            
            @Override
            public void onFailure(Call<String> call, Throwable t) {
                
                weatherGetError();
            }
        });
    }
    
    public void enqueueCurrentWeatherData(int cityId) {
        
    }
    
    public void enqueueForecastData(int cityId) {
        
    }
    
    public CurrentWeather getCurrentWeather() {
        
        return getCurrentWeather(mPreferencesManager.readCityId());
    }
    
    public FifeDaysForecast getForecast() {
        
        return getForecast(mPreferencesManager.readCityId());
    }
    
    public CurrentWeather getCurrentWeather(int cityId) {
        
        if (isWeatherUpdateAvailable()) {
            try {
                Response<String> response = mOpenWeatherMapApi.getCurrentWeather(cityId, UNITS_PARAMETER_VALUE, API_KEY).execute();
                if (response.body() != null) {
                    return writeAndPrepareCurrentWeatherData(response.body());
                } else {
                    return null;
                }
            } catch (IOException e) {
                e.printStackTrace();
                return null;
            }
        } else {
            return null;
        }
    }
    
    public FifeDaysForecast getForecast(int cityId) {
        
        if (isWeatherUpdateAvailable()) {
            try {
                Response<String> response = mOpenWeatherMapApi.getFifeDaysWeather(cityId, UNITS_PARAMETER_VALUE, API_KEY).execute();
                if (response.body() != null) {
                    return writeAndPrepareForecastData(response.body());
                } else {
                    return null;
                }
            } catch (IOException e) {
                e.printStackTrace();
                return null;
            }
        } else {
            return null;
        }
    }
    
    public boolean isWeatherUpdateAvailable() {
        
        return isInternetAvailable() && refreshIntervalIsRight();
    }
    
    private synchronized CurrentWeather writeAndPrepareCurrentWeatherData(String jsonCurrentWeather) {
        
        mPreferencesManager.writeCurrentWeatherData(jsonCurrentWeather);
        mPreferencesManager.writeCurrentTime();
        return prepareCurrentWeatherData(jsonCurrentWeather);
    }
    
    private synchronized FifeDaysForecast writeAndPrepareForecastData(String jsonForecast) {
        
        mPreferencesManager.writeForecastWeatherData(jsonForecast);
        mPreferencesManager.writeCurrentTime();
        return prepareForecastWeatherData(jsonForecast);
    }
    
    private void weatherGetError() {
        
        sendError(mAppContext.getResources().getString(R.string.weather_get_error));
    }
    
    private void sendError(String errMessage) {
        
        if (mErrorListener != null) {
            mErrorListener.onError(errMessage);
        }
    }
    
    public synchronized CurrentWeather getSavedWeatherData() {
        
        String jsonCurrentWeather = mPreferencesManager.readCurrentWeatherData();
        if (jsonCurrentWeather != null) {
            return prepareCurrentWeatherData(jsonCurrentWeather, false);
        } else {
            return null;
        }
    }
    
    public synchronized FifeDaysForecast getSavedForecastData() {
        
        String jsonForecast = mPreferencesManager.readForecastWeatherData();
        if (jsonForecast != null) {
            return prepareForecastWeatherData(jsonForecast);
        } else {
            return null;
        }
    }
    
    private synchronized CurrentWeather prepareCurrentWeatherData(String jsonCurrentWeather) {
        
        return prepareCurrentWeatherData(jsonCurrentWeather, true);
    }
    
    private synchronized CurrentWeather prepareCurrentWeatherData(String jsonCurrentWeather, boolean isNew) {
        
        CurrentWeather currentWeather = mGson.fromJson(jsonCurrentWeather, CurrentWeather.class);
        mPreferencesManager.writeCityId(currentWeather.getCityId());
        if (mCurrentWeatherReadyListener != null && isMainThread()) {
            mCurrentWeatherReadyListener.onCurrentWeatherReady(currentWeather, isNew);
        }
        return currentWeather;
    }
    
    private synchronized FifeDaysForecast prepareForecastWeatherData(String jsonForecastWeather) {
        
        FifeDaysForecast forecast = mGson.fromJson(jsonForecastWeather, FifeDaysForecast.class);
        if (mForecastReadyListener != null && isMainThread()) {
            mForecastReadyListener.onForecastReady(forecast);
        }
        return forecast;
    }
    
    private boolean isMainThread() {
    
        return mMainThreadName != null && Thread.currentThread().getName().equals(mMainThreadName);
    }
    
    private boolean refreshIntervalIsRight() {
        
        long curTime = System.currentTimeMillis();
        return curTime > mPreferencesManager.readLastRequestTime() + MIN_REQUEST_INTERVAL;
    }
    
    private boolean isInternetAvailable() {
        
        return InternetConnectionChecker.isInternetAvailable(mAppContext);
    }
    
    public void cancelCalls() {
        
        cancelCall(mCurrentWeatherCall);
        cancelCall(mForecastWeatherCall);
    }
    
    private void cancelCall(Call call) {
        
        if (call != null) {
            call.cancel();
        }
    }
    
    public void registerCurrentWeatherReadyListener(CurrentWeatherReadyListener listener, String treadName) {
        
        mCurrentWeatherReadyListener = listener;
        mMainThreadName = treadName;
    }
    
    public void registerForecastReadyListener(ForecastReadyListener listener, String treadName) {
        
        mForecastReadyListener = listener;
        mMainThreadName = treadName;
    }
    
    public void registerErrorListener(ErrorListener listener) {
        
        mErrorListener = listener;
    }
    
    public void unRegisterErrorListener() {
        
        mErrorListener = null;
    }
    
    public void unRegisterCurrentWeatherReadyListener() {
        
        mCurrentWeatherReadyListener = null;
        mMainThreadName = null;
    }
    
    public void unRegisterForecastReadyListener() {
        
        mForecastReadyListener = null;
        mMainThreadName = null;
    }
    
    public void unRegisterListeners() {
        
        unRegisterCurrentWeatherReadyListener();
        unRegisterForecastReadyListener();
        unRegisterErrorListener();
    }
    
    public interface CurrentWeatherReadyListener {
        
        void onCurrentWeatherReady(CurrentWeather currentWeather, boolean isNew);
    }
    
    public interface ForecastReadyListener {
        
        void onForecastReady(FifeDaysForecast forecast);
    }
    
    public interface ErrorListener {
        
        void onError(String errMessage);
    }
}
