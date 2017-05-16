package com.hplasplas.task7.managers;

import android.content.Context;

import com.google.gson.Gson;
import com.hplasplas.task7.R;
import com.hplasplas.task7.interfaces.OpenWeatherMapApi;
import com.hplasplas.task7.models.weather.current.CurrentWeather;
import com.hplasplas.task7.models.weather.forecast.FifeDaysForecast;
import com.hplasplas.task7.utils.DataTimeUtils;
import com.hplasplas.task7.utils.InternetConnectionChecker;

import java.io.IOException;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static com.hplasplas.task7.setting.Constants.API_KEY;
import static com.hplasplas.task7.setting.Constants.MIN_REQUEST_INTERVAL;
import static com.hplasplas.task7.setting.Constants.REFRESHING_TIME_STAMP_PATTERN;
import static com.hplasplas.task7.setting.Constants.UNITS_PARAMETER_VALUE;

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
    
    public WeatherDataProvider(Context context, OpenWeatherMapApi openWeatherMapApi, PreferencesManager preferencesManager,
                               Gson gson, DataTimeUtils dataTimeUtils) {
        
        mDataTimeUtils = dataTimeUtils;
        mAppContext = context;
        mGson = gson;
        mOpenWeatherMapApi = openWeatherMapApi;
        mPreferencesManager = preferencesManager;
    }
    
    public void tryEnqueueWeatherAndForecastData() {
        
        if (isInternetAvailable() && refreshIntervalIsRight()) {
        enqueueWeatherAndForecastData(mPreferencesManager.readCityId());
        } else {
            getSavedWeatherData();
            getSavedForecastData();
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
            sendError( mAppContext.getResources().getString(R.string.weather_refreshed, interval));
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
                    String currentWeatherResponse =  response.body();
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
    
    public CurrentWeather getCurrentWeather(){
        
       return  getCurrentWeather(mPreferencesManager.readCityId());
    }
    
    public FifeDaysForecast getForecast(){
        
        return  getForecast(mPreferencesManager.readCityId());
    }
    
    public CurrentWeather getCurrentWeather(int cityId){
        if(isWeatherUpdateAvailable()) {
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
    
    public FifeDaysForecast getForecast(int cityId){
        
        if(isWeatherUpdateAvailable()) {
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
    
    private synchronized CurrentWeather writeAndPrepareCurrentWeatherData(String jsonCurrentWeather){
        mPreferencesManager.writeCurrentWeatherData(jsonCurrentWeather);
        mPreferencesManager.writeCurrentTime();
        return prepareCurrentWeatherData(jsonCurrentWeather);
    }
    
    private synchronized FifeDaysForecast writeAndPrepareForecastData(String jsonForecast){
        mPreferencesManager.writeForecastWeatherData(jsonForecast);
        mPreferencesManager.writeCurrentTime();
        return prepareForecastWeatherData(jsonForecast);
    }
    
    private void weatherGetError() {
        
            sendError( mAppContext.getResources().getString(R.string.weather_get_error));
    }
    
    private void sendError(String errMessage){
        
        if (mErrorListener != null ) {
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
        if (mCurrentWeatherReadyListener != null) {
             
            mCurrentWeatherReadyListener.onCurrentWeatherReady(currentWeather, isNew);
        }
        return currentWeather;
    }
    
    private synchronized FifeDaysForecast prepareForecastWeatherData(String jsonForecastWeather) {
        
        FifeDaysForecast forecast = mGson.fromJson(jsonForecastWeather, FifeDaysForecast.class);
        if (mForecastReadyListener != null) {
            mForecastReadyListener.onForecastReady(forecast);
        }
        return forecast;
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
    
    public void registerCurrentWeatherReadyListener(CurrentWeatherReadyListener listener) {
        
        mCurrentWeatherReadyListener = listener;
        
    }
    
    public void registerForecastReadyListener(ForecastReadyListener listener) {
        
        mForecastReadyListener = listener;
    }
    
    public void registerErrorListener(ErrorListener listener) {
        
        mErrorListener = listener;
    }
    
    public void unRegisterErrorListener() {
        
        mErrorListener = null;
    }
    
    public void unRegisterCurrentWeatherReadyListener() {
        
        mCurrentWeatherReadyListener = null;
    }
    
    public void unRegisterForecastReadyListener() {
    
        mForecastReadyListener = null;
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
