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
import android.graphics.Bitmap;
import android.widget.ImageView;

import com.hplasplas.weather.R;
import com.squareup.picasso.MemoryPolicy;
import com.squareup.picasso.Picasso;

import java.io.IOException;
import java.util.Locale;

import static com.hplasplas.weather.setting.Constants.ICON_DOWNLOAD_URL;
import static com.hplasplas.weather.setting.Constants.ICON_FILE_SUFFIX;
import static com.hplasplas.weather.setting.Constants.WEATHER_DRAWABLE_PREFIX;

/**
 * Created by StarkinDG on 25.04.2017.
 */

public class WeatherImageManager {
    
    private Context mAppContext;
    private Picasso mPicasso;
    
    public WeatherImageManager(Context appContext, Picasso picasso) {
        
        mAppContext = appContext;
        mPicasso = picasso;
    }
    
    public void setWeatherIcon(ImageView imageView, String iconId) {
        
        mPicasso
                .load(buildIconURL(iconId))
                .memoryPolicy(MemoryPolicy.NO_STORE)
                .error(R.drawable.ic_highlight_off_red_500_24dp)
                .into(imageView);
    }
    
    public Bitmap getWeatherIcon(String iconId) throws IOException {
    
        return mPicasso
                .load(buildIconURL(iconId))
                .memoryPolicy(MemoryPolicy.NO_STORE)
                .error(R.drawable.ic_highlight_off_red_500_24dp)
                .get();
    }
    
    private String buildIconURL(String iconId){
        
        return ICON_DOWNLOAD_URL + iconId + ICON_FILE_SUFFIX;
    }
    
    public void setBackground(ImageView imageView, String weatherGroup, String weatherIcon, int weatherId) {
    
    
        imageView.setImageResource(calculateDrawableId(weatherGroup, weatherIcon, weatherId));
    }
    
    
    private int calculateDrawableId(String weatherGroup, String weatherIcon, int weatherId) {
        
        if(weatherGroup == null || weatherIcon == null){
            return R.drawable.default_background;
        }
        String dayNight = weatherIcon.substring(weatherIcon.length() - 1);
        int drawableId = mAppContext.getResources().getIdentifier(WEATHER_DRAWABLE_PREFIX + (Integer.toString(weatherId)) + dayNight,
                "drawable", mAppContext.getPackageName());
        if(drawableId == 0){
            drawableId = mAppContext.getResources().getIdentifier(WEATHER_DRAWABLE_PREFIX + (Integer.toString(weatherId)),
                    "drawable", mAppContext.getPackageName());
        }
        if (drawableId == 0) {
            drawableId = mAppContext.getResources().getIdentifier(weatherGroup.toLowerCase(Locale.US),
                    "drawable", mAppContext.getPackageName());;
        }
        if (drawableId == 0) {
            drawableId = R.drawable.default_background;
        }
        return drawableId;
    }
}