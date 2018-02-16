/*
 * Copyright © 2018. Dmitry Starkin Contacts: t0506803080@gmail.com
 *
 * Licensed under the Apache License, Version 2.0 (the «License»);
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * //www.apache.org/licenses/LICENSE-2.0
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an «AS IS» BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.starsoft.dbtolls.setting;

import java.util.concurrent.TimeUnit;

/**
 * Created by StarkinDG on 12.04.2017.
 */

public final class Constants {
    
    //handler values
    public static final int MESSAGE_CURSOR_RECEIVED = 0;
    public static final int MESSAGE_DATA_RECORDED = 1;
    public static final int MESSAGE_CLOSE_DB = 2;
    public static final int MESSAGE_ERROR = 3;
    
    //tread Pool values
    public static final int THREAD_START_TERM = 0;
    public static final long THREAD_DEFAULT_IDLE_TIME = 30;
    public static final TimeUnit TIME_UNIT = TimeUnit.SECONDS;
    public static final int THREADS_DEFAULT_PRIORITY = 7;
    public static final String THREAD_NAME_PREFIX = "DbWorker";
    
    //Data base values
    public static final long MIN_DB_IDLE_TIME = 1000;
    public static final int SPARSE_ARRAY_INIT_CAPACITY = 10;
    public static final long DEFAULT_DATA_BASE_IDLE_TIME = 35000;
    public static final int NUMBER_OF_ATTEMPTS_OPEN_DB = 3;
    
    //other
    public static final int DB_COPY_BUFFER_LENGTH = 1024;
    public static final long TEMP_SLEEP_INTERVAL = 1000;
}