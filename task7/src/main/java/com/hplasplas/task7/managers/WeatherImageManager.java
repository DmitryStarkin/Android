package com.hplasplas.task7.managers;

import android.content.Context;
import android.widget.ImageView;

import com.hplasplas.task7.R;
import com.squareup.picasso.MemoryPolicy;
import com.squareup.picasso.Picasso;

import java.util.Locale;

import static com.hplasplas.task7.setting.Constants.ICON_DOWNLOAD_URL;
import static com.hplasplas.task7.setting.Constants.ICON_FILE_SUFFIX;
import static com.hplasplas.task7.setting.Constants.WEATHER_DRAWABLE_PREFIX;

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
        
        String imageUrl = ICON_DOWNLOAD_URL + iconId + ICON_FILE_SUFFIX;
        mPicasso
                .load(imageUrl)
                .memoryPolicy(MemoryPolicy.NO_STORE)
                .error(R.drawable.ic_highlight_off_red_500_24dp)
                .into(imageView);
    }
    
    public void setBackground(ImageView imageView, String weatherGroup, String weatherIcon, int weatherId) {
        
        mPicasso
                .load(calculateDrawableId(weatherGroup, weatherIcon, weatherId))
                .error(R.drawable.default_background)
                .into(imageView);
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