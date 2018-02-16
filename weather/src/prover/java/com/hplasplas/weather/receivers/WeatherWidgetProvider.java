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
package com.hplasplas.weather.receivers;

import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;

import com.hplasplas.weather.services.WeatherWidgetService;

import static com.hplasplas.weather.setting.Constants.UPDATE_ALL_WIDGETS;

/**
 * Created by StarkinDG on 14.05.2017.
 */

public class WeatherWidgetProvider extends AppWidgetProvider {
    
    @Override
    public void onReceive(Context context, Intent intent) {
        
        super.onReceive(context, intent);
        if(intent.getAction().equals(UPDATE_ALL_WIDGETS) || intent.getAction().equals(Intent.ACTION_BOOT_COMPLETED)){
            AppWidgetManager appWidgetManager = AppWidgetManager.getInstance(context.getApplicationContext());
            int[] appWidgetIds = appWidgetManager.getAppWidgetIds(new ComponentName(context.getApplicationContext(), WeatherWidgetProvider.class));
            startService(context, appWidgetIds);
        }
    }
        
    @Override
    public void onUpdate(Context context, AppWidgetManager appWidgetManager, int[] appWidgetIds) {
        
        super.onUpdate(context, appWidgetManager, appWidgetIds);
        startService(context, appWidgetIds);
    }
    
    private void startService(Context context, int[] appWidgetIds) {
        
        if(appWidgetIds.length != 0){
        Intent serviceIntent = new Intent(context, WeatherWidgetService.class);
        context.startService(serviceIntent);
        }
    }
}
