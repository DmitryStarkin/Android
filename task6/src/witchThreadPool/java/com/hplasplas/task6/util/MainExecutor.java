package com.hplasplas.task6.util;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.RejectedExecutionHandler;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

import static com.hplasplas.task6.setting.Constants.POSSESSORS_MULTIPLIER;
import static com.hplasplas.task6.setting.Constants.QUEUE_CAPACITY;
import static com.hplasplas.task6.setting.Constants.THREAD_IDLE_TIME;
import static com.hplasplas.task6.setting.Constants.TIME_UNIT;

/**
 * Created by StarkinDG on 25.03.2017.
 */

public class MainExecutor extends ThreadPoolExecutor {
    
    private static MainExecutor sMainExecutor = null;
    
    private MainExecutor(int corePoolSize, int maximumPoolSize, long keepAliveTime, TimeUnit unit,
                         BlockingQueue<Runnable> workQueue, ThreadFactory threadFactory, RejectedExecutionHandler handler) {
        
        super(corePoolSize, maximumPoolSize, keepAliveTime, unit, workQueue, threadFactory, handler);
    }
    
    public static synchronized MainExecutor getExecutor() {
        
        if (sMainExecutor == null) {
            int availablePossessors = Runtime.getRuntime().availableProcessors();
            sMainExecutor = new MainExecutor(availablePossessors, POSSESSORS_MULTIPLIER * availablePossessors,
                    THREAD_IDLE_TIME, TIME_UNIT, new LinkedBlockingQueue<>(QUEUE_CAPACITY),
                    new BitmapLoadersThreadFactory(), new RejectionHandler());
        }
        return sMainExecutor;
    }
}
