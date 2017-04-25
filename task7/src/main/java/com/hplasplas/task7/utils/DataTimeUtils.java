package com.hplasplas.task7.utils;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

/**
 * Created by StarkinDG on 25.04.2017.
 */

public class DataTimeUtils {
    
    public String getTimeString(long time, String timePattern) {
        
        return new SimpleDateFormat(timePattern, Locale.US).format(new Date(time));
    }
    
    public String getTimeString(long time, String timePattern, long correction) {
        
        return new SimpleDateFormat(timePattern, Locale.US).format(new Date(time * correction));
    }
}
