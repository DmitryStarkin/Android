package com.hplasplas.task6.util;

import android.support.annotation.NonNull;

import java.util.concurrent.ThreadFactory;

import static com.hplasplas.task6.setting.Constants.THREADS_PRIORITY;
import static com.hplasplas.task6.setting.Constants.THREAD_NAME_PREFIX;

/**
 * Created by StarkinDG on 16.03.2017.
 */

public class BitmapLoadersThreadFactory implements ThreadFactory {
    
    private static int count = 1;
    
    @Override
    public Thread newThread(@NonNull Runnable runnable) {
        Thread thread = new Thread(runnable);
        thread.setName(THREAD_NAME_PREFIX + count++);
        thread.setDaemon(true);
        thread.setPriority(THREADS_PRIORITY);
        return thread;
    }
}
