package com.hplasplas.task7.activitys;

import android.os.Bundle;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.hplasplas.task7.App;
import com.hplasplas.task7.R;
import com.hplasplas.task7.managers.PreferencesManager;
import com.hplasplas.task7.models.weather.current.CurrentWeather;
import com.hplasplas.task7.utils.InternetConnectionChecker;
import com.squareup.picasso.MemoryPolicy;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static com.hplasplas.task7.setting.Constants.API_KEY;
import static com.hplasplas.task7.setting.Constants.DEFAULT_CITY_ID;
import static com.hplasplas.task7.setting.Constants.ICON_DOWNLOAD_URL;
import static com.hplasplas.task7.setting.Constants.ICON_FILE_SUFFIX;
import static com.hplasplas.task7.setting.Constants.LAST_REQUEST_TIME;
import static com.hplasplas.task7.setting.Constants.MIL_PER_SEC;
import static com.hplasplas.task7.setting.Constants.MIN_REQUEST_INTERVAL;
import static com.hplasplas.task7.setting.Constants.PREF_FOR_CURRENT_CITY_ID;
import static com.hplasplas.task7.setting.Constants.PREF_FOR_CURRENT_WEATHER_JSON_DATA;
import static com.hplasplas.task7.setting.Constants.REFRESHING_TIME_STAMP_PATTERN;
import static com.hplasplas.task7.setting.Constants.SUN_TIME_STAMP_PATTERN;
import static com.hplasplas.task7.setting.Constants.UNITS_PARAMETER_VALUE;
import static com.hplasplas.task7.setting.Constants.WEATHER_TIME_STAMP_PATTERN;

public class MainActivity extends AppCompatActivity {
    
    private SwipeRefreshLayout swipeRefreshLayout;
    private ImageView mCurrentWeatherIcon;
    private ImageView mBackground;
    private TextView mCityName;
    private TextView mDateTime;
    private TextView mTemperature;
    private TextView mWeatherDescription;
    private TextView mPressure;
    private TextView mHumidity;
    private TextView mWindDescription;
    private TextView mCloudiness;
    private TextView mSunrise;
    private TextView mSunset;
    Call<String> currentWeatherCall;
    private int currentCityId;
    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        findViews();
        adjustViews();
        getAndPrepareLastWeatherData();
    }
    
    @Override
    protected void onResume() {
        
        super.onResume();
        refreshWeather();
    }
    
    @Override
    protected void onPause() {
        
        super.onPause();
        cancelCall(currentWeatherCall);
    }
    
    private void findViews() {
        
        swipeRefreshLayout = (SwipeRefreshLayout) findViewById(R.id.srl_container);
        mCurrentWeatherIcon = (ImageView) findViewById(R.id.weather_icon);
        mBackground = (ImageView) findViewById(R.id.background_image);
        mCityName = (TextView) findViewById(R.id.city_name);
        mDateTime = (TextView) findViewById(R.id.date_time);
        mTemperature = (TextView) findViewById(R.id.temperature);
        mPressure = (TextView) findViewById(R.id.pressure);
        mHumidity = (TextView) findViewById(R.id.humidity);
        mWindDescription = (TextView) findViewById(R.id.wind_description);
        mCloudiness = (TextView) findViewById(R.id.cloudiness);
        mSunrise = (TextView) findViewById(R.id.sunrise);
        mSunset = (TextView) findViewById(R.id.sunset);
        mWeatherDescription = (TextView) findViewById(R.id.weather_description);
    }
    
    private void adjustViews() {
        
        swipeRefreshLayout.setOnRefreshListener(this::refreshWeatherWitchMessage);
    }
    
    private void setWeatherValues(CurrentWeather currentWeather) {
        
        setWeatherIcon(mCurrentWeatherIcon, currentWeather.getWeather().get(0).getIcon());
        setBackground(mBackground, currentWeather.getWeather().get(0).getMain() ,currentWeather.getWeather().get(0).getId());
        mCityName.setText(currentWeather.getCityName());
        mDateTime.setText(getTimeString(currentWeather.getCalculationDataTime(), WEATHER_TIME_STAMP_PATTERN, MIL_PER_SEC));
        mSunrise.setText(getTimeString(currentWeather.getSys().getSunrise(), SUN_TIME_STAMP_PATTERN, MIL_PER_SEC));
        mSunset.setText(getTimeString(currentWeather.getSys().getSunset(), SUN_TIME_STAMP_PATTERN, MIL_PER_SEC));
        mTemperature.setText(getResources().getString(R.string.temperature, currentWeather.getMain().getTemp()));
        mWeatherDescription.setText(currentWeather.getWeather().get(0).getDescription());
        mPressure.setText(getResources().getString(R.string.pressure, currentWeather.getMain().getPressure()));
        mHumidity.setText(getResources().getString(R.string.humidity, currentWeather.getMain().getHumidity()));
        mCloudiness.setText(getResources().getString(R.string.cloudiness, currentWeather.getClouds().getAll()));
        mWindDescription.setText(getResources().getString(R.string.wind, currentWeather.getWind().getSpeed(), determineWindDirection(currentWeather.getWind().getDeg())));
    }
    
    private void setWeatherIcon(ImageView imageView, String iconId) {
        
        String imageUrl = ICON_DOWNLOAD_URL + iconId + ICON_FILE_SUFFIX;
        App.getPicasso()
                .load(imageUrl)
                .memoryPolicy(MemoryPolicy.NO_STORE)
                .placeholder(R.drawable.ic_system_update_alt_black_24dp)
                .error(R.drawable.ic_highlight_off_red_500_24dp)
                .into(imageView);
    }
    
    private void setBackground(ImageView imageView, String weatherGroup, int weatherId) {
        
        int weatherDrawableId = getResources().getIdentifier((Integer.toString(weatherId)), "drawable", getApplicationContext().getPackageName());
        int weatherGroupDrawableId = getResources().getIdentifier(weatherGroup, "drawable", getApplicationContext().getPackageName());
        App.getPicasso()
                .load(weatherDrawableId)
                .memoryPolicy(MemoryPolicy.NO_STORE)
                .placeholder(R.drawable.default_background)
                .error(weatherGroupDrawableId)
                .into(imageView);
    }
    
    private String determineWindDirection(double deg) {
        
        //TODO determine wind direction
        return "Nord";
    }
    
    private String getTimeString(long time, String timePattern) {
        
        return new SimpleDateFormat(timePattern, Locale.US).format(new Date(time));
    }
    
    private String getTimeString(long time, String timePattern, long correction) {
        
        return new SimpleDateFormat(timePattern, Locale.US).format(new Date(time*correction));
    }
    private void refreshWeather() {
        
        showRefreshProgress(swipeRefreshLayout);
        if (refreshIntervalIsRight() && isInternetAvailable()) {
            refreshWeatherData();
        } else {
            hideRefreshProgress(swipeRefreshLayout);
        }
    }
    
    private void refreshWeatherWitchMessage() {
        
        swipeRefreshLayout.setEnabled(false);
        if (!refreshIntervalIsRight()) {
            hideRefreshProgress(swipeRefreshLayout);
            String interval = getTimeString(MIN_REQUEST_INTERVAL - (System.currentTimeMillis() - PreferencesManager.getPreferences().getLong(LAST_REQUEST_TIME, 0)),
                    REFRESHING_TIME_STAMP_PATTERN);
            makeToast(getResources().getString(R.string.weather_refreshed, interval));
        } else if (!isInternetAvailable()) {
            hideRefreshProgress(swipeRefreshLayout);
            makeToast(getResources().getString(R.string.internet_not_available));
        } else {
            refreshWeatherData();
        }
    }
    
    private void showRefreshProgress(SwipeRefreshLayout swipeRefreshLayout) {
        
        swipeRefreshLayout.setRefreshing(true);
        swipeRefreshLayout.setEnabled(false);
    }
    
    private void hideRefreshProgress(SwipeRefreshLayout swipeRefreshLayout) {
        
        swipeRefreshLayout.setRefreshing(false);
        swipeRefreshLayout.setEnabled(true);
    }
    
    private void refreshWeatherData() {
        
        refreshWeatherData(PreferencesManager.getPreferences().getInt(PREF_FOR_CURRENT_CITY_ID, DEFAULT_CITY_ID));
    }
    
    private void refreshWeatherData(int cityId) {
        
        currentWeatherCall = App.getOpenWeatherMapApi().getCurrentWeather(cityId, UNITS_PARAMETER_VALUE, API_KEY);
        currentWeatherCall.enqueue(new Callback<String>() {
            @Override
            public void onResponse(Call<String> call, Response<String> response) {
                
                if (response.body() != null) {
                    weatherGetSuccessfully();
                    writeAndPrepareCurrentWeatherData(response.body());
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
    
    private void cancelCall(Call call) {
        
        if (call != null) {
            call.cancel();
        }
    }
    
    private void makeToast(String message) {
        
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
    }
    
    private void weatherGetError() {
        
        hideRefreshProgress(swipeRefreshLayout);
        makeToast(getResources().getString(R.string.weather_get_error));
    }
    
    private void weatherGetSuccessfully() {
        
        hideRefreshProgress(swipeRefreshLayout);
        makeToast(getResources().getString(R.string.weather_updated));
    }
    
    private void writeAndPrepareCurrentWeatherData(String jsonCurrentWeather) {
        
        PreferencesManager.getPreferences().edit()
                .putString(PREF_FOR_CURRENT_WEATHER_JSON_DATA, jsonCurrentWeather)
                .putLong(LAST_REQUEST_TIME, System.currentTimeMillis())
                .apply();
        prepareCurrentWeatherData(jsonCurrentWeather);
    }
    
    private void getAndPrepareLastWeatherData() {
        
        String jsonCurrentWeather = PreferencesManager.getPreferences().getString(PREF_FOR_CURRENT_WEATHER_JSON_DATA, null);
        if (jsonCurrentWeather != null) {
            prepareCurrentWeatherData(jsonCurrentWeather);
        }
    }
    
    private void prepareCurrentWeatherData(String jsonCurrentWeather) {
        
        setWeatherValues(App.getGson().fromJson(jsonCurrentWeather, CurrentWeather.class));
    }
    
    private boolean refreshIntervalIsRight() {
        
        long curTime = System.currentTimeMillis();
        return curTime > PreferencesManager.getPreferences().getLong(LAST_REQUEST_TIME, 0) + MIN_REQUEST_INTERVAL;
    }
    
    private boolean isInternetAvailable() {
        
        return InternetConnectionChecker.isInternetAvailable();
    }
}
