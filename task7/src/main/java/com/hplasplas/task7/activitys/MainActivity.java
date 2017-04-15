package com.hplasplas.task7.activitys;

import android.database.Cursor;
import android.os.Bundle;
import android.support.v4.view.MenuItemCompat;
import android.support.v4.widget.CursorAdapter;
import android.support.v4.widget.SimpleCursorAdapter;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.SearchView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.hplasplas.task7.App;
import com.hplasplas.task7.R;
import com.hplasplas.task7.dialogs.MessageDialog;
import com.hplasplas.task7.interfaces.OpenWeatherMapApi;
import com.hplasplas.task7.managers.PreferencesManager;
import com.hplasplas.task7.models.weather.current.CurrentWeather;
import com.hplasplas.task7.utils.InternetConnectionChecker;
import com.squareup.picasso.MemoryPolicy;
import com.squareup.picasso.Picasso;
import com.starsoft.dbtolls.main.DataBaseTolls;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

import javax.inject.Inject;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static com.hplasplas.task7.setting.Constants.*;

public class MainActivity extends AppCompatActivity implements DataBaseTolls.onCursorReadyListener {
    
    private final String TAG = getClass().getSimpleName();
    
    @Inject
    public Picasso mPicasso;
    @Inject
    public OpenWeatherMapApi mOpenWeatherMapApi;
    @Inject
    public Gson mGson;
    @Inject
    public DataBaseTolls mDataBaseTolls;
    
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
    private Call<String> mCurrentWeatherCall;
    private boolean mClearText;
    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        
        super.onCreate(savedInstanceState);
        App.getAppComponent().inject(this);
        mDataBaseTolls.setOnCursorReadyListener(this);
        setContentView(R.layout.activity_main);
        findViews();
        adjustViews();
        getAndPrepareLastWeatherData();
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
                DataBaseTolls.getInstance().clearAllTasks();
                mSearchView.setQueryHint(getResources().getString(R.string.search_hint));
                closeCursor(mSearchView);
                hideRefreshProgress(mSwipeRefreshLayout);
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
                    
                    refreshCityList(CITY_QUERYBEGIN_SEARCH_PREFIX + newText + CITY_QUERY_BEGIN_SEARCH_SUFFIX, false);
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
        refreshWeather();
    }
    
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        
        int id = item.getItemId();
        if (id == R.id.action_about) {
            MessageDialog.newInstance(getString(R.string.about_message)).show(getSupportFragmentManager(), MESSAGE_DIALOG_TAG);
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
    
    @Override
    protected void onPause() {
        
        super.onPause();
        mDataBaseTolls.clearAllTasks();
        cancelCall(mCurrentWeatherCall);
        closeCursor(mSearchView);
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
    
    private void cancelCall(Call call) {
        
        if (call != null) {
            call.cancel();
        }
    }
    
    private void findViews() {
        
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar_actionbar);
        setSupportActionBar(toolbar);
        mSwipeRefreshLayout = (SwipeRefreshLayout) findViewById(R.id.srl_container);
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
        
        mSwipeRefreshLayout.setOnRefreshListener(this::refreshWeatherWitchMessage);
        mSwipeRefreshLayout.setProgressViewEndTarget(true, mSwipeRefreshLayout.getProgressCircleDiameter() + REFRESH_INDICATOR_OFFSET);
    }
    
    private void createSuggestionAdapter() {
        
        if (mSearchView.getSuggestionsAdapter() == null) {
            int[] to = {R.id.city_item};
            SimpleCursorAdapter adapter = new SimpleCursorAdapter(MainActivity.this, R.layout.city_search_item,
                    null, COLUMNS_CITY_NAME, to, 0);
            mSearchView.setSuggestionsAdapter(adapter);
        }
    }
    
    private void refreshCityList(String query, boolean clearText) {
        
        if (DEBUG) {
            Log.d(TAG, "refreshCityList: ");
        }
        mClearText = clearText;
        mDataBaseTolls.getDataUsingSQLCommand(SUGGESTION_QUERY_TAG, query);
        //showRefreshProgress(mSwipeRefreshLayout);
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
            //hideRefreshProgress(mSwipeRefreshLayout);
        }
    }
    
    private void setWeatherValues(CurrentWeather currentWeather) {
        
        setBackground(mBackground, currentWeather.getWeather().get(0).getMain(), currentWeather.getWeather().get(0).getId());
        setWeatherIcon(mCurrentWeatherIcon, currentWeather.getWeather().get(0).getIcon());
        mCityName.setText(currentWeather.getCityName());
        mDateTime.setText(getTimeString(currentWeather.getCalculationDataTime(), WEATHER_TIME_STAMP_PATTERN, MIL_PER_SEC));
        mSunrise.setText(getTimeString(currentWeather.getSys().getSunrise(), SUN_TIME_STAMP_PATTERN, MIL_PER_SEC));
        mSunset.setText(getTimeString(currentWeather.getSys().getSunset(), SUN_TIME_STAMP_PATTERN, MIL_PER_SEC));
        mTemperature.setText(getResources().getString(R.string.temperature, currentWeather.getMain().getTemp()));
        mWeatherDescription.setText(currentWeather.getWeather().get(0).getDescription());
        mPressure.setText(getResources().getString(R.string.pressure, currentWeather.getMain().getPressure()));
        mHumidity.setText(getResources().getString(R.string.humidity, currentWeather.getMain().getHumidity()));
        mCloudiness.setText(getResources().getString(R.string.cloudiness, currentWeather.getClouds().getAll()));
        mWindDescription.setText(getResources().getString(R.string.wind, currentWeather.getWind().getSpeed(),
                determineWindDirection(currentWeather.getWind().getDeg())));
    }
    
    private void setWeatherIcon(ImageView imageView, String iconId) {
        
        String imageUrl = ICON_DOWNLOAD_URL + iconId + ICON_FILE_SUFFIX;
        mPicasso
                .load(imageUrl)
                .memoryPolicy(MemoryPolicy.NO_STORE)
                .error(R.drawable.ic_highlight_off_red_500_24dp)
                .into(imageView);
    }
    
    private void setBackground(ImageView imageView, String weatherGroup, int weatherId) {
        
        int weatherDrawableId = getResources().getIdentifier(WEATHER_DRAWABLE_PREFIX + (Integer.toString(weatherId)),
                "drawable", getApplicationContext().getPackageName());
        int weatherGroupDrawableId = getResources().getIdentifier(weatherGroup.toLowerCase(Locale.US),
                "drawable", getApplicationContext().getPackageName());
        if (weatherDrawableId == 0) {
            weatherDrawableId = weatherGroupDrawableId;
        }
        if (weatherDrawableId == 0) {
            weatherDrawableId = R.drawable.default_background;
        }
    
        mPicasso
                .load(weatherDrawableId)
                .error(R.drawable.default_background)
                .into(imageView);
    }
    
    private String determineWindDirection(double deg) {
        
        int direction = getResources().getIdentifier(WIND_DIRECTION_PREFIX + Integer.toString((int) Math.round(deg / WIND_DIRECTION_DIVIDER)),
                "string", getApplicationContext().getPackageName());
        return getResources().getString(direction);
    }
    
    private String getTimeString(long time, String timePattern) {
        
        return new SimpleDateFormat(timePattern, Locale.US).format(new Date(time));
    }
    
    private String getTimeString(long time, String timePattern, long correction) {
        
        return new SimpleDateFormat(timePattern, Locale.US).format(new Date(time * correction));
    }
    
    private void refreshWeather() {
        
        showRefreshProgress(mSwipeRefreshLayout);
        if (refreshIntervalIsRight() && isInternetAvailable()) {
            refreshWeatherData();
        } else {
            hideRefreshProgress(mSwipeRefreshLayout);
        }
    }
    
    private void refreshWeatherWithCursor(int CursorPosition) {
        
        Cursor cursor = mSearchView.getSuggestionsAdapter().getCursor();
        if (cursor != null) {
            cursor.moveToPosition(CursorPosition);
            refreshWeather(cursor.getInt(cursor.getColumnIndex(COLUMNS_CITY_ID)));
        }
    }
    
    private void refreshWeather(int cityId) {
        
        showRefreshProgress(mSwipeRefreshLayout);
        if (isInternetAvailable()) {
            refreshWeatherData(cityId);
        } else {
            makeToast(getResources().getString(R.string.internet_not_available));
            hideRefreshProgress(mSwipeRefreshLayout);
        }
    }
    
    private void refreshWeatherWitchMessage() {
        
        mSwipeRefreshLayout.setEnabled(false);
        if (!refreshIntervalIsRight()) {
            hideRefreshProgress(mSwipeRefreshLayout);
            String interval = getTimeString(MIN_REQUEST_INTERVAL - (System.currentTimeMillis() - PreferencesManager.getPreferences(this).getLong(LAST_REQUEST_TIME, 0)),
                    REFRESHING_TIME_STAMP_PATTERN);
            makeToast(getResources().getString(R.string.weather_refreshed, interval));
        } else if (!isInternetAvailable()) {
            hideRefreshProgress(mSwipeRefreshLayout);
            makeToast(getResources().getString(R.string.internet_not_available));
        } else {
            refreshWeatherData();
        }
    }
    
    private boolean refreshIntervalIsRight() {
        
        long curTime = System.currentTimeMillis();
        return curTime > PreferencesManager.getPreferences(this).getLong(LAST_REQUEST_TIME, 0) + MIN_REQUEST_INTERVAL;
    }
    
    private boolean isInternetAvailable() {
        
        return InternetConnectionChecker.isInternetAvailable(this);
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
        
        refreshWeatherData(PreferencesManager.getPreferences(this).getInt(PREF_FOR_CURRENT_CITY_ID, DEFAULT_CITY_ID));
    }
    
    private void refreshWeatherData(int cityId) {
        
        mCurrentWeatherCall = mOpenWeatherMapApi.getCurrentWeather(cityId,
                UNITS_PARAMETER_VALUE, API_KEY);
        mCurrentWeatherCall.enqueue(new Callback<String>() {
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
    
    private void makeToast(String message) {
        
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
    }
    
    private void weatherGetError() {
        
        hideRefreshProgress(mSwipeRefreshLayout);
        makeToast(getResources().getString(R.string.weather_get_error));
    }
    
    private void weatherGetSuccessfully() {
        
        hideRefreshProgress(mSwipeRefreshLayout);
        makeToast(getResources().getString(R.string.weather_updated));
    }
    
    private void writeAndPrepareCurrentWeatherData(String jsonCurrentWeather) {
        
        PreferencesManager.getPreferences(this).edit()
                .putString(PREF_FOR_CURRENT_WEATHER_JSON_DATA, jsonCurrentWeather)
                .putLong(LAST_REQUEST_TIME, System.currentTimeMillis())
                .apply();
        prepareCurrentWeatherData(jsonCurrentWeather);
    }
    
    private void getAndPrepareLastWeatherData() {
        
        String jsonCurrentWeather = PreferencesManager.getPreferences(this).getString(PREF_FOR_CURRENT_WEATHER_JSON_DATA, null);
        if (jsonCurrentWeather != null) {
            prepareCurrentWeatherData(jsonCurrentWeather);
        }
    }
    
    private void prepareCurrentWeatherData(String jsonCurrentWeather) {
        
        CurrentWeather currentWeather = mGson.fromJson(jsonCurrentWeather, CurrentWeather.class);
        PreferencesManager.getPreferences(this).edit()
                .putInt(PREF_FOR_CURRENT_CITY_ID, currentWeather.getCityId())
                .apply();
        setWeatherValues(currentWeather);
    }
    
    
}
