package com.hplasplas.weather.adapters;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.hplasplas.weather.App;
import com.hplasplas.weather.R;
import com.hplasplas.weather.managers.WeatherImageManager;
import com.hplasplas.weather.models.weather.forecast.ThreeHourForecast;
import com.hplasplas.weather.utils.DataTimeUtils;

import java.util.List;

import javax.inject.Inject;

import static com.hplasplas.weather.setting.Constants.FORECAST_DATE_STAMP_PATTERN;
import static com.hplasplas.weather.setting.Constants.FORECAST_TIME_STAMP_PATTERN;
import static com.hplasplas.weather.setting.Constants.MIL_PER_SEC;

/**
 * Created by StarkinDG on 25.04.2017.
 */

public class ForecastAdapter extends RecyclerView.Adapter<ForecastAdapter.ViewHolder> {
    
    @Inject
    public WeatherImageManager mImageManager;
    @Inject
    public DataTimeUtils mDataTimeUtils;
    @Inject
    public Context mAppContext;
    
    private List<ThreeHourForecast> mThreeHourForecast;
    
    
    public ForecastAdapter(List<ThreeHourForecast> threeHourForecast) {
        
        App.getAppComponent().inject(this);
        mThreeHourForecast = threeHourForecast;
        
    }
    
    @Override
    public ForecastAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.recyclerview_item, parent, false);
        return new ViewHolder(view);
    }
    
    @Override
    public void onBindViewHolder(ForecastAdapter.ViewHolder holder, int position) {
    
        if (position != RecyclerView.NO_POSITION && mThreeHourForecast != null && !mThreeHourForecast.get(position).getWeather().isEmpty()) {
            mImageManager.setWeatherIcon(holder.mForecastIcon, mThreeHourForecast.get(position).getWeather().get(0).getIcon());
            holder.mForecastDate.setText(mDataTimeUtils.getTimeString(mThreeHourForecast.get(position).getDt(), FORECAST_DATE_STAMP_PATTERN, MIL_PER_SEC));
            holder.mForecastTime.setText(mDataTimeUtils.getTimeString(mThreeHourForecast.get(position).getDt(), FORECAST_TIME_STAMP_PATTERN, MIL_PER_SEC));
            holder.mForecastTemp.setText(mAppContext.getResources().getString(R.string.temperature, mThreeHourForecast.get(position).getMain().getTemp()));
        }
    }
    
    @Override
    public int getItemCount() {
        
        return mThreeHourForecast.size();
    }
    
    public void setThreeHourForecast(List<ThreeHourForecast> threeHourForecast) {
        
        this.mThreeHourForecast = threeHourForecast;
    }
    
    class ViewHolder extends RecyclerView.ViewHolder {
        
        private TextView mForecastDate;
        private TextView mForecastTime;
        private TextView mForecastTemp;
        private ImageView mForecastIcon;
       
        
        ViewHolder(View itemView) {
            
            super(itemView);
            mForecastDate = (TextView) itemView.findViewById(R.id.forecast_date);
            mForecastTime = (TextView) itemView.findViewById(R.id.forecast_time);
            mForecastTemp = (TextView) itemView.findViewById(R.id.forecast_temperature);
            mForecastIcon = (ImageView) itemView.findViewById(R.id.forecast_icon);
        }
    }
}

