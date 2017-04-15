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
