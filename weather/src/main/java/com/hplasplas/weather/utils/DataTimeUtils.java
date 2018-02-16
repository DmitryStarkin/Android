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
package com.hplasplas.weather.utils;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

/**
 * Created by StarkinDG on 25.04.2017.
 */

public class DataTimeUtils {
    
    public synchronized String getTimeString(long time, String timePattern) {
        
        return new SimpleDateFormat(timePattern, Locale.US).format(new Date(time));
    }
    
    public synchronized String getTimeString(long time, String timePattern, long correction) {
        
        return new SimpleDateFormat(timePattern, Locale.US).format(new Date(time * correction));
    }
}
