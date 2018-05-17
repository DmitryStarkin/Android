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

import android.database.Cursor;
import android.os.Bundle;
import android.support.v4.widget.CursorAdapter;
import android.support.v4.widget.SimpleCursorAdapter;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.SearchView;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ProgressBar;

import com.hplasplas.weather.App;
import com.hplasplas.weather.R;
import com.hplasplas.weather.interfaces.MainContract;

import javax.inject.Inject;

import static com.hplasplas.weather.setting.Constants.DEBUG;

//This File Created at 17.05.2018 11:16.
public abstract class SearchPlaceActivity extends AppCompatActivity implements MainContract.SearchCustomer {
    
    private final String TAG = getClass().getSimpleName();
    
    @Inject
    public MainContract.SearchPlaceProvider mSearchPlaceProvider;
    
    private SearchView mSearchView = null;
    private MenuItem mSearchMenuItem = null;
    private ProgressBar mSearchProgressBar = null;
    private boolean clearTextIfNoResult;
    
    /**
     * Get a SearshMenuItem resourse Id.
     * <p>
     * Can call once
     * the link is saved in the super class.
     */
    abstract int getSearchMenuItemId();
    
    /**
     * Get a SearchProgressBar.
     * <p>
     * Can call once the link is saved in the super class.
     */
    abstract ProgressBar getSearchProgressBar();
    
    abstract void searchedPlaceSelected(int placeId);
    
    @Override
    protected void onPause() {
        
        super.onPause();
        closeCursor(mSearchView);
    }
    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        
        super.onCreate(savedInstanceState);
        App.getAppComponent().inject(this);
        getLifecycle().addObserver(mSearchPlaceProvider);
    }
    
    @Override
    protected void onStart() {
        
        super.onStart();
        mSearchProgressBar = getSearchProgressBar();
        setProgressBarState(mSearchProgressBar, false);
    }
    
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        
        if ((mSearchMenuItem = menu.findItem(getSearchMenuItemId())) == null) {
            return true;
        }
        
        mSearchView = (SearchView) mSearchMenuItem.getActionView();
        mSearchView.setQueryHint(getResources().getString(R.string.search_hint));
        mSearchMenuItem.setOnActionExpandListener(new MenuItem.OnActionExpandListener() {
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
                mSearchPlaceProvider.stopSearch();
                mSearchView.setQueryHint(getResources().getString(R.string.search_hint));
                closeCursor(mSearchView);
                setProgressBarState(mSearchProgressBar, false);
                return true;
            }
        });
        
        mSearchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                
                refreshCityList(query, true);
                return true;
            }
            
            @Override
            public boolean onQueryTextChange(String newText) {
                
                if (DEBUG) {
                    Log.d(TAG, "onQueryTextChange: ");
                }
                if (newText.length() > 1) {
                    
                    refreshCityList(newText, false);
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
                
                deliveryId(position);
                mSearchMenuItem.collapseActionView();
                return true;
            }
        });
        return true;
    }
    
    @Override
    public void onNewPlaceCursorReady(MainContract.SearchCursor cursor, MainContract.SearchCustomer whoAsked) {
        
        if (DEBUG) {
            Log.d(TAG, "onCursorReady: ");
        }
        if (whoAsked == this) {
            if (mSearchMenuItem == null || !mSearchMenuItem.isActionViewExpanded()) {
                cursor.close();
            } else {
                Cursor oldCursor = mSearchView.getSuggestionsAdapter().swapCursor(cursor);
                if (oldCursor != null && !oldCursor.isClosed()) {
                    oldCursor.close();
                }
                if (clearTextIfNoResult && (cursor == null || !cursor.moveToFirst())) {
                    mSearchView.setQuery(null, false);
                    mSearchView.setQueryHint(getResources().getString(R.string.no_result));
                }
                setProgressBarState(mSearchProgressBar, false);
            }
        }
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
    
    private void createSuggestionAdapter() {
        
        if (mSearchView.getSuggestionsAdapter() == null) {
            int[] to = {R.id.city_item, R.id.country};
            SimpleCursorAdapter adapter = new SimpleCursorAdapter(SearchPlaceActivity.this, R.layout.city_search_item,
                    null, COLUMNS_PLACE_NAME, to, 0);
            mSearchView.setSuggestionsAdapter(adapter);
        }
    }
    
    private void refreshCityList(String query, boolean isFinalQuery) {
        
        clearTextIfNoResult = isFinalQuery;
        mSearchPlaceProvider.getNewSearchResultCursor(query, isFinalQuery, this);
        setProgressBarState(mSearchProgressBar, true);
    }
    
    private void deliveryId(int CursorPosition) {
        
        Cursor cursor = mSearchView.getSuggestionsAdapter().getCursor();
        if (cursor != null) {
            cursor.moveToPosition(CursorPosition);
            searchedPlaceSelected(cursor.getInt(cursor.getColumnIndex(COLUMNS_PLACE_ID)));
        }
    }
    
    private void setProgressBarState(ProgressBar progressBar, boolean mustVisible) {
        
        if (progressBar != null) {
            progressBar.setVisibility(mustVisible ? View.VISIBLE : View.INVISIBLE);
        }
    }
}
