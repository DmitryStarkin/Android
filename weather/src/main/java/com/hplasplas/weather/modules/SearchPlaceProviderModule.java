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

package com.hplasplas.weather.modules;

//This File Created at 17.05.2018 10:24.

import com.hplasplas.weather.interfaces.MainContract;
import com.hplasplas.weather.managers.InDataBasePlaceSearcher;
import com.starsoft.dbtolls.main.DataBaseTolls;

import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;

@Module
public class SearchPlaceProviderModule {
    
    @Provides
    @Singleton
    public MainContract.SearchPlaceProvider provideSearchPlaceProvider(DataBaseTolls dataBaseTolls){
        
        return new InDataBasePlaceSearcher(dataBaseTolls);
    }
    
}
