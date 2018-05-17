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

import android.database.Cursor;
import android.util.Log;

import com.hplasplas.weather.interfaces.MainContract;
import com.starsoft.dbtolls.main.DataBaseTolls;

import static com.hplasplas.weather.setting.Constants.DEBUG;

//This File Created at 16.05.2018 15:38.
public class InDataBasePlaceSearcher implements MainContract.SearchPlaceProvider, DataBaseTolls.onCursorReadyListener {
    
    private final String TAG = getClass().getSimpleName();
    
    private static final String CITY_QUERY_BEGIN_SEARCH_PREFIX = "SELECT _id, id, name, country FROM city_data WHERE name LIKE \'";
    private static final String CITY_QUERY_BEGIN_SEARCH_SUFFIX = "%\' LIMIT 10";
    private static final String CITY_QUERY_FULL_SEARCH_PREFIX = "SELECT _id, id, name, country FROM city_data WHERE name = \'";
    private static final String CITY_QUERY_FULL_SEARCH_SUFFIX = "\'";
    private static final int SUGGESTION_QUERY_TAG = 0;
    
    private MainContract.SearchCustomer mSearchListener = null;
    private DataBaseTolls mDataBaseTolls;
    
    public InDataBasePlaceSearcher(DataBaseTolls dataBaseTolls) {
        
        mDataBaseTolls =  dataBaseTolls;
        mDataBaseTolls.setOnCursorReadyListener(this);
    }
    
    @Override
    public void onCustomerAttached(MainContract.SearchCustomer searchListener) {
    
        if (DEBUG) {
            Log.d(TAG, "onCustomerAttached: ");
        }
        mSearchListener = searchListener;
    }
    
    @Override
    public void onCustomerDetached(MainContract.SearchCustomer searchListener) {
    
        if (DEBUG) {
            Log.d(TAG, "onCustomerDetached: ");
        }
        stopSearch();
        mSearchListener = null;
    }
    
    @Override
    public void getNewSearchResultCursor(String query, boolean isFinalQuery, MainContract.SearchCustomer searchListener) {
        
        if (mSearchListener != null && mSearchListener == searchListener) {
            if(isFinalQuery) {
                mDataBaseTolls.getDataUsingSQLCommand(SUGGESTION_QUERY_TAG, CITY_QUERY_FULL_SEARCH_PREFIX + query + CITY_QUERY_FULL_SEARCH_SUFFIX);
            } else{
                mDataBaseTolls.getDataUsingSQLCommand(SUGGESTION_QUERY_TAG, CITY_QUERY_BEGIN_SEARCH_PREFIX + query + CITY_QUERY_BEGIN_SEARCH_SUFFIX);
            }
        } else {
            stopSearch();
        }
    }
    
    @Override
    public void stopSearch() {
        
        mDataBaseTolls.clearAllTasks();
    }
    
    
    @Override
    public void onCursorReady(Cursor cursor) {
        
        if (mSearchListener != null) {
            mSearchListener.onNewPlaceCursorReady(new MainContract.SearchCursor(cursor), mSearchListener);
        }
    }
    
}

