/**
 * Copyright © 2017 Dmitry Starkin Contacts: t0506803080@gmail.com. All rights reserved
 *
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
