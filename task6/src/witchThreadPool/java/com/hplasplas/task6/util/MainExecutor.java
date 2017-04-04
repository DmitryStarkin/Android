package com.hplasplas.task6.util;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.RejectedExecutionHandler;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

import static com.hplasplas.task6.setting.Constants.QUEUE_CAPACITY;
import static com.hplasplas.task6.setting.Constants.MIN_THREAD_NUMBER;
import static com.hplasplas.task6.setting.Constants.THREAD_IDLE_TIME;
import static com.hplasplas.task6.setting.Constants.THREAD_START_TERM;
import static com.hplasplas.task6.setting.Constants.TIME_UNIT;

/**
 * Created by StarkinDG on 25.03.2017.
 */

public class MainExecutor extends ThreadPoolExecutor {
    
    private static MainExecutor instance = null;
    
    private MainExecutor(int corePoolSize, int maximumPoolSize, long keepAliveTime, TimeUnit unit,
                         BlockingQueue<Runnable> workQueue, ThreadFactory threadFactory, RejectedExecutionHandler handler) {
        
        super(corePoolSize, maximumPoolSize, keepAliveTime, unit, workQueue, threadFactory, handler);
    }
    
    public static MainExecutor getInstance() {
        
        if (instance == null) {
            int threadNumber = Runtime.getRuntime().availableProcessors() + THREAD_START_TERM;
            threadNumber = threadNumber < MIN_THREAD_NUMBER ? MIN_THREAD_NUMBER : threadNumber;
            
            instance = new MainExecutor(threadNumber, threadNumber,
                    THREAD_IDLE_TIME, TIME_UNIT, new LinkedBlockingQueue<>(QUEUE_CAPACITY),
                    new BitmapLoadersThreadFactory(), new RejectionHandler());
            instance.allowCoreThreadTimeOut(true);
        }
        return instance;
    }
}
