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

package com.hplasplas.weather.activitys;

import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.support.v4.view.MenuItemCompat;
import android.support.v4.widget.CursorAdapter;
import android.support.v4.widget.SimpleCursorAdapter;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SearchView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.hplasplas.weather.App;
import com.hplasplas.weather.BuildConfig;
import com.hplasplas.weather.R;
import com.hplasplas.weather.adapters.ForecastAdapter;
import com.hplasplas.weather.managers.MessageManager;
import com.hplasplas.weather.managers.WeatherDataProvider;
import com.hplasplas.weather.managers.WeatherImageManager;
import com.hplasplas.weather.models.weather.current.CurrentWeather;
import com.hplasplas.weather.models.weather.forecast.ThreeHourForecast;
import com.hplasplas.weather.utils.DataTimeUtils;
import com.starsoft.dbtolls.main.DataBaseTolls;

import java.util.List;

import javax.inject.Inject;

import static com.hplasplas.weather.setting.Constants.CITY_QUERY_BEGIN_SEARCH_PREFIX;
import static com.hplasplas.weather.setting.Constants.CITY_QUERY_BEGIN_SEARCH_SUFFIX;
import static com.hplasplas.weather.setting.Constants.CITY_QUERY_FULL_SEARCH_PREFIX;
import static com.hplasplas.weather.setting.Constants.CITY_QUERY_FULL_SEARCH_SUFFIX;
import static com.hplasplas.weather.setting.Constants.COLUMNS_CITY_ID;
import static com.hplasplas.weather.setting.Constants.COLUMNS_CITY_NAME;
import static com.hplasplas.weather.setting.Constants.DEBUG;
import static com.hplasplas.weather.setting.Constants.MIL_PER_SEC;
import static com.hplasplas.weather.setting.Constants.REFRESH_INDICATOR_END_OFFSET;
import static com.hplasplas.weather.setting.Constants.REFRESH_INDICATOR_START_OFFSET;
import static com.hplasplas.weather.setting.Constants.SNACK_BAR_MESSAGE_DURATION;
import static com.hplasplas.weather.setting.Constants.SUGGESTION_QUERY_TAG;
import static com.hplasplas.weather.setting.Constants.SUN_TIME_STAMP_PATTERN;
import static com.hplasplas.weather.setting.Constants.UPDATE_ALL_WIDGETS;
import static com.hplasplas.weather.setting.Constants.WEATHER_TIME_STAMP_PATTERN;
import static com.hplasplas.weather.setting.Constants.WIND_DIRECTION_DIVIDER;
import static com.hplasplas.weather.setting.Constants.WIND_DIRECTION_PREFIX;

public class MainActivity extends AppCompatActivity implements DataBaseTolls.onCursorReadyListener {
    
    private final String TAG = getClass().getSimpleName();
    
    @Inject
    public WeatherImageManager mImageManager;
    @Inject
    public DataTimeUtils mDataTimeUtils;
    @Inject
    public WeatherDataProvider mWeatherDataProvider;
    @Inject
    public DataBaseTolls mDataBaseTolls;
    @Inject
    public MessageManager mMessageManager;
    private Toolbar mToolbar;
    private SearchView mSearchView;
    private MenuItem mSearchMenuItem;
    private SwipeRefreshLayout mSwipeRefreshLayout;
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
    private ProgressBar mCityFindBar;
    private RecyclerView mRecyclerView;
    private boolean mClearText;
    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        
        super.onCreate(savedInstanceState);
        App.getAppComponent().inject(this);
        mDataBaseTolls.setOnCursorReadyListener(this);
        
        setContentView(R.layout.activity_main);
        findViews();
        adjustViews();
        adjustRecyclerView();
    }
    
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        
        getMenuInflater().inflate(R.menu.main_menu, menu);
        mSearchMenuItem = menu.findItem(R.id.action_search);
        mSearchView = (SearchView) mSearchMenuItem.getActionView();
        mSearchView.setQueryHint(getResources().getString(R.string.search_hint));
        MenuItemCompat.setOnActionExpandListener(mSearchMenuItem, new MenuItemCompat.OnActionExpandListener() {
            @Override
            public boolean onMenuItemActionExpand(MenuItem item) {
                
                createSuggestionAdapter();
                return true;
            }
            
            @Override
            public boolean onMenuItemActionCollapse(MenuItem item) {
                
                if (DEBUG) {
                    Log.d(TAG, "onMenuItemActionCollapse: ");
                }
                mDataBaseTolls.clearAllTasks();
                mSearchView.setQueryHint(getResources().getString(R.string.search_hint));
                closeCursor(mSearchView);
                mCityFindBar.setVisibility(View.INVISIBLE);
                return true;
            }
        });
        
        mSearchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                
                refreshCityList(CITY_QUERY_FULL_SEARCH_PREFIX + query + CITY_QUERY_FULL_SEARCH_SUFFIX, true);
                return true;
            }
            
            @Override
            public boolean onQueryTextChange(String newText) {
                
                if (DEBUG) {
                    Log.d(TAG, "onQueryTextChange: ");
                }
                if (newText.length() > 1) {
                    
                    refreshCityList(CITY_QUERY_BEGIN_SEARCH_PREFIX + newText + CITY_QUERY_BEGIN_SEARCH_SUFFIX, false);
                } else {
                    closeCursor(mSearchView);
                }
                return true;
            }
        });
        
        mSearchView.setOnSuggestionListener(new SearchView.OnSuggestionListener() {
            
            @Override
            public boolean onSuggestionSelect(int position) {
                
                return false;
            }
            
            @Override
            public boolean onSuggestionClick(int position) {
                
                refreshWeatherWithCursor(position);
                mSearchMenuItem.collapseActionView();
                return true;
            }
        });
        return true;
    }
    
    @Override
    protected void onResume() {
        
        super.onResume();
        mWeatherDataProvider.registerErrorListener(this::showErrorMessage);
        mWeatherDataProvider.registerCurrentWeatherReadyListener(this::showCurrentWeather, Thread.currentThread().getName());
        mWeatherDataProvider.registerForecastReadyListener(forecast -> setAdapter(mRecyclerView, forecast.getThreeHourForecast()),
                Thread.currentThread().getName());
        tryRefreshWeather();
    }
    
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        
        int id = item.getItemId();
        if (id == R.id.action_about) {
            mMessageManager.makeDialogMessage(this, getString(R.string.about_message_title), R.drawable.ic_info_outline_white_24dp, R.layout.about_message_body);
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
    
    @Override
    protected void onPause() {
        
        super.onPause();
        mDataBaseTolls.clearAllTasks();
        mWeatherDataProvider.cancelCalls();
        mWeatherDataProvider.unRegisterListeners();
        closeCursor(mSearchView);
        updateWidgets();
    }
    
    private void updateWidgets(){
        
        if(BuildConfig.FLAVOR.equals("prover")){
            Intent intent = new Intent(UPDATE_ALL_WIDGETS);
            sendBroadcast(intent);
        }
    }
    
    private void showErrorMessage(String errMessage){
        
        hideRefreshProgress(mSwipeRefreshLayout);
        if(errMessage != null){
        mMessageManager.makeSnackbarMessage(mBackground, errMessage, SNACK_BAR_MESSAGE_DURATION);
        }
    }
    
    private void showCurrentWeather(CurrentWeather currentWeather, boolean isNew) {
    
        if (DEBUG) {
            Log.d(TAG, "showCurrentWeather: ");
        }
        hideRefreshProgress(mSwipeRefreshLayout);
        if (isNew){
            mMessageManager.makeSnackbarMessage(mBackground, getResources().getString(R.string.weather_updated), SNACK_BAR_MESSAGE_DURATION);
    }
        setWeatherValues(currentWeather);
    }
    
    private void closeCursor(SearchView searchView) {
        
        if (searchView != null) {
            CursorAdapter adapter = searchView.getSuggestionsAdapter();
            if (adapter != null) {
                Cursor cursor = adapter.swapCursor(null);
                if (cursor != null && !cursor.isClosed()) {
                    cursor.close();
                }
            }
        }
    }
    
    private void findViews() {
    
        mToolbar = (Toolbar) findViewById(R.id.toolbar_actionbar);
        mCityFindBar = (ProgressBar) findViewById(R.id.city_find_bar);
        mSwipeRefreshLayout = (SwipeRefreshLayout) findViewById(R.id.srl_container);
        mCurrentWeatherIcon = (ImageView) findViewById(R.id.weather_icon);
        mBackground = (ImageView) findViewById(R.id.background_image);
        mCityName = (TextView) findViewById(R.id.city_name);
        mDateTime = (TextView) findViewById(R.id.date_time);
        mTemperature = (TextView) findViewById(R.id.forecast_temperature);
        mPressure = (TextView) findViewById(R.id.pressure);
        mHumidity = (TextView) findViewById(R.id.humidity);
        mWindDescription = (TextView) findViewById(R.id.wind_description);
        mCloudiness = (TextView) findViewById(R.id.cloudiness);
        mSunrise = (TextView) findViewById(R.id.sunrise);
        mSunset = (TextView) findViewById(R.id.sunset);
        mWeatherDescription = (TextView) findViewById(R.id.weather_description);
        mRecyclerView = (RecyclerView) findViewById(R.id.forecast_list);
    }
    
    private void adjustViews() {
        
        setSupportActionBar(mToolbar);
        mCityFindBar.setVisibility(View.INVISIBLE);
        mSwipeRefreshLayout.setOnRefreshListener(this::refreshWeatherWitchMessage);
        mSwipeRefreshLayout.setProgressViewOffset(true, REFRESH_INDICATOR_START_OFFSET,
                REFRESH_INDICATOR_END_OFFSET);
    }
    
    private void adjustRecyclerView() {
        
        mRecyclerView.setHasFixedSize(true);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false));
    }
    
    private void createSuggestionAdapter() {
        
        if (mSearchView.getSuggestionsAdapter() == null) {
            int[] to = {R.id.city_item, R.id.country};
            SimpleCursorAdapter adapter = new SimpleCursorAdapter(MainActivity.this, R.layout.city_search_item,
                    null, COLUMNS_CITY_NAME, to, 0);
            mSearchView.setSuggestionsAdapter(adapter);
        }
    }
    
    private void refreshCityList(String query, boolean clearText) {
        
        mClearText = clearText;
        mDataBaseTolls.getDataUsingSQLCommand(SUGGESTION_QUERY_TAG, query);
        mCityFindBar.setVisibility(View.VISIBLE);
    }
    
    @Override
    public void onCursorReady(Cursor cursor) {
        
        if (DEBUG) {
            Log.d(TAG, "onCursorReady: ");
        }
        if (mSearchMenuItem == null || !mSearchMenuItem.isActionViewExpanded()) {
            cursor.close();
        } else {
            Cursor oldCursor = mSearchView.getSuggestionsAdapter().swapCursor(cursor);
            if (oldCursor != null && !oldCursor.isClosed()) {
                oldCursor.close();
            }
            if (mClearText && (cursor == null || !cursor.moveToFirst())) {
                mSearchView.setQuery(null, false);
                mSearchView.setQueryHint(getResources().getString(R.string.no_result));
            }
            mCityFindBar.setVisibility(View.INVISIBLE);
        }
        
    }
    
    private void setWeatherValues(CurrentWeather currentWeather) {
    
        mImageManager.setBackground(mBackground, currentWeather.getWeather().get(0).getMain(),
                currentWeather.getWeather().get(0).getIcon(), currentWeather.getWeather().get(0).getId());
        mImageManager.setWeatherIcon(mCurrentWeatherIcon, currentWeather.getWeather().get(0).getIcon());
        mCityName.setText(currentWeather.getCityName());
        mDateTime.setText(mDataTimeUtils.getTimeString(currentWeather.getCalculationDataTime(), WEATHER_TIME_STAMP_PATTERN, MIL_PER_SEC));
        mSunrise.setText(mDataTimeUtils.getTimeString(currentWeather.getSys().getSunrise(), SUN_TIME_STAMP_PATTERN, MIL_PER_SEC));
        mSunset.setText(mDataTimeUtils.getTimeString(currentWeather.getSys().getSunset(), SUN_TIME_STAMP_PATTERN, MIL_PER_SEC));
        mTemperature.setText(getResources().getString(R.string.temperature, currentWeather.getMain().getTemp()));
        mWeatherDescription.setText(currentWeather.getWeather().get(0).getDescription());
        mPressure.setText(getResources().getString(R.string.pressure, currentWeather.getMain().getPressure()));
        mHumidity.setText(getResources().getString(R.string.humidity, currentWeather.getMain().getHumidity()));
        mCloudiness.setText(getResources().getString(R.string.cloudiness, currentWeather.getClouds().getAll()));
        mWindDescription.setText(getResources().getString(R.string.wind, currentWeather.getWind().getSpeed(),
                determineWindDirection(currentWeather.getWind().getDeg())));
    }
    
    private String determineWindDirection(double deg) {
        
        int direction = getResources().getIdentifier(WIND_DIRECTION_PREFIX + Integer.toString((int) Math.round(deg / WIND_DIRECTION_DIVIDER)),
                "string", getApplicationContext().getPackageName());
        return getResources().getString(direction);
    }
    
    private void tryRefreshWeather() {
        
        showRefreshProgress(mSwipeRefreshLayout);
        mWeatherDataProvider.tryEnqueueWeatherAndForecastData();
           
    }
    
    private void refreshWeatherWithCursor(int CursorPosition) {
        
        Cursor cursor = mSearchView.getSuggestionsAdapter().getCursor();
        if (cursor != null) {
            cursor.moveToPosition(CursorPosition);
            tryRefreshWeather(cursor.getInt(cursor.getColumnIndex(COLUMNS_CITY_ID)));
        }
    }
    
    private void tryRefreshWeather(int cityId) {
        
        showRefreshProgress(mSwipeRefreshLayout);
        mWeatherDataProvider.tryEnqueueWeatherAndForecastData(cityId);
    }
    
    private void refreshWeatherWitchMessage() {
    
        mWeatherDataProvider.tryEnqueueWeatherWitchMessage();
    }
    
    private void showRefreshProgress(SwipeRefreshLayout swipeRefreshLayout) {
        
        swipeRefreshLayout.setRefreshing(true);
    }
    
    private void hideRefreshProgress(SwipeRefreshLayout swipeRefreshLayout) {
        
        swipeRefreshLayout.setRefreshing(false);
    }
    
    private ForecastAdapter setAdapter(RecyclerView recyclerView, List<ThreeHourForecast> itemList) {
    
        if (DEBUG) {
            Log.d(TAG, "setAdapter: ");
        }
        ForecastAdapter adapter = new ForecastAdapter(itemList);
        if (recyclerView.getAdapter() == null) {
            recyclerView.setAdapter(adapter);
        } else {
            recyclerView.swapAdapter(adapter, true);
        }
        return adapter;
    }
    
}
