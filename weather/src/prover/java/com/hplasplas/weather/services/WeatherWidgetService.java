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
package com.hplasplas.weather.services;

import android.app.IntentService;
import android.app.PendingIntent;
import android.app.Service;
import android.appwidget.AppWidgetManager;
import android.content.ComponentName;
import android.content.Intent;
import android.support.annotation.Nullable;
import android.widget.RemoteViews;

import com.hplasplas.weather.App;
import com.hplasplas.weather.R;
import com.hplasplas.weather.activitys.MainActivity;
import com.hplasplas.weather.managers.WeatherDataProvider;
import com.hplasplas.weather.managers.WeatherImageManager;
import com.hplasplas.weather.models.weather.current.CurrentWeather;
import com.hplasplas.weather.receivers.WeatherWidgetProvider;
import com.hplasplas.weather.utils.DataTimeUtils;

import java.io.IOException;

import javax.inject.Inject;

import static com.hplasplas.weather.setting.Constants.MIL_PER_SEC;
import static com.hplasplas.weather.setting.Constants.WIDGET_TIME_STAMP_PATTERN;

/**
 * Created by StarkinDG on 14.05.2017.
 */

public class WeatherWidgetService extends IntentService {
    
    @Inject
    public WeatherImageManager mImageManager;
    @Inject
    public DataTimeUtils mDataTimeUtils;
    @Inject
    public WeatherDataProvider mWeatherDataProvider;
    
    public WeatherWidgetService() {
        
        super("WeatherWidgetService");
        App.getAppComponent().inject(this);
    }
    
    @Override
    public int onStartCommand(@Nullable Intent intent, int flags, int startId) {
        
        super.onStartCommand(intent, flags, startId);
        return Service.START_NOT_STICKY;
    }
    
    @Override
    protected void onHandleIntent(@Nullable Intent intent) {
        
        CurrentWeather currentWeather;
        currentWeather = mWeatherDataProvider.getSavedWeatherData();
        updateWidgets(currentWeather);
        currentWeather = mWeatherDataProvider.getCurrentWeather();
        updateWidgets(currentWeather);
    }
    
    private void updateWidgets(CurrentWeather currentWeather) {
        
        RemoteViews remoteView = new RemoteViews(this.getPackageName(), R.layout.wan_cell_widget);
        Intent startActivityIntent = new Intent(this.getApplicationContext(), MainActivity.class);
        startActivityIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        PendingIntent pendingIntent = PendingIntent.getActivity(this.getApplicationContext(), 0, startActivityIntent, 0);
        remoteView.setOnClickPendingIntent(R.id.wan_cell_widget_layout, pendingIntent);
        if (currentWeather != null) {
            remoteView.setTextViewText(R.id.widget_city, currentWeather.getCityName());
            remoteView.setTextViewText(R.id.widget_temperature, getResources().getString(R.string.temperature, currentWeather.getMain().getTemp()));
            remoteView.setTextViewText(R.id.widget_updated, mDataTimeUtils.getTimeString(currentWeather.getCalculationDataTime(),
                    WIDGET_TIME_STAMP_PATTERN, MIL_PER_SEC));
            try {
                remoteView.setImageViewBitmap(R.id.widget_weather_icon, mImageManager.getWeatherIcon(currentWeather.getWeather().get(0).getIcon()));
            } catch (IOException e) {
                e.printStackTrace();
            }
            AppWidgetManager appWidgetManager = AppWidgetManager.getInstance(this);
            appWidgetManager.updateAppWidget(new ComponentName(this, WeatherWidgetProvider.class), remoteView);
        }
    }
}
