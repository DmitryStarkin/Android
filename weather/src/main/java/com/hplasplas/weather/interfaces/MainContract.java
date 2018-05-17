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

package com.hplasplas.weather.interfaces;

import android.arch.lifecycle.DefaultLifecycleObserver;
import android.arch.lifecycle.LifecycleOwner;
import android.database.Cursor;
import android.database.CursorWrapper;
import android.support.annotation.NonNull;

import java.util.Arrays;

//This File Created at 16.05.2018 12:41.
public interface MainContract {
    
    interface WeatherCustomer {
    
    }
    
    interface WeatherProvider {
    
    }
    
    interface SearchPlaceProvider extends DefaultLifecycleObserver, CursorColumns {
        
        @Override
        default void onResume(@NonNull LifecycleOwner owner) {
            
            onCustomerAttached((SearchCustomer) owner);
        }
        
        @Override
        default void onPause(@NonNull LifecycleOwner owner) {
            
            onCustomerDetached((SearchCustomer) owner);
        }
        
        void onCustomerAttached(SearchCustomer searchListener);
        
        void onCustomerDetached(SearchCustomer searchListener);
        
        void getNewSearchResultCursor(String query, boolean isFull, SearchCustomer searchListener);
        
        void stopSearch();
    }
    
    interface SearchCustomer extends LifecycleOwner, CursorColumns {
        
        void onNewPlaceCursorReady(SearchCursor placeCursor, SearchCustomer whoAsked);
    }
    
    interface CursorColumns {
        
        String[] COLUMNS_NAMES = {"_id", "id", "name", "country"};
        String[] COLUMNS_PLACE_NAME = Arrays.copyOfRange(COLUMNS_NAMES, 2, COLUMNS_NAMES.length);
        String COLUMNS_PLACE_ID = COLUMNS_NAMES[1];
        String COLUMNS_ROW_ID = COLUMNS_NAMES[0];
        
        default String[] getColumnsNames(){
            return COLUMNS_NAMES;
        }
        
    }
    
    interface WeatherDataSource {
    
    }
    
    interface PlaceSource {
    
    }
    
    class SearchCursor extends CursorWrapper implements CursorColumns {
        
        /**
         * Creates a cursor wrapper.
         *
         * @param cursor The underlying cursor to wrap.
         */
        public SearchCursor(Cursor cursor) {
            
            super(cursor);
            if (!Arrays.equals(COLUMNS_NAMES, cursor.getColumnNames())) {
                close();
                throw new IllegalStateException("Cursors has not comparable columns");
            }
        }
    }
}
