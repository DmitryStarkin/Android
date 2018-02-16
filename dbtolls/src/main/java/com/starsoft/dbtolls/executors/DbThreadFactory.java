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

package com.starsoft.dbtolls.executors;

import android.support.annotation.NonNull;

import java.util.concurrent.ThreadFactory;

import static com.starsoft.dbtolls.setting.Constants.THREADS_DEFAULT_PRIORITY;
import static com.starsoft.dbtolls.setting.Constants.THREAD_NAME_PREFIX;

/**
 * Created by StarkinDG on 13.04.2017.
 */

public class DbThreadFactory implements ThreadFactory {
    
    private static int count = 1;
    private int threadPriority;
    
    public DbThreadFactory(int threadPriority){
        
        if ((threadPriority < 1) || (threadPriority > 10)){
            this.threadPriority = THREADS_DEFAULT_PRIORITY;
        } else {
            this.threadPriority = threadPriority;
        }
    }
    
    @Override
    public Thread newThread(@NonNull Runnable runnable) {
        
        Thread thread = new Thread(runnable);
        thread.setName(THREAD_NAME_PREFIX + count++);
        thread.setDaemon(true);
        thread.setPriority(threadPriority);
        return thread;
    }
}
