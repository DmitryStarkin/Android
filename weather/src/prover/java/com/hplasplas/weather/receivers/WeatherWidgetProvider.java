/**
 * Copyright © 2017 Dmitry Starkin Contacts: t0506803080@gmail.com. All rights reserved
 *
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
