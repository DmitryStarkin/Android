package com.starsoft.dbtolls.setting;

import java.util.concurrent.TimeUnit;

/**
 * Created by StarkinDG on 12.04.2017.
 */

public final class Constants {
    
    //handler value
    public static final int MESSAGE_GET_CURSOR = 0;
    public static final int MESSAGE_WRITE_DATA = 1;
    
    
    //tread Pool values
    public static final int THREAD_START_TERM = -8;
    public static final int MIN_THREAD_NUMBER = 1;
    public static final long THREAD_IDLE_TIME = 30;
    public static final TimeUnit TIME_UNIT = TimeUnit.SECONDS;
    public static final int THREADS_PRIORITY = 9;
    public static final String THREAD_NAME_PREFIX = "DbWorker";
}