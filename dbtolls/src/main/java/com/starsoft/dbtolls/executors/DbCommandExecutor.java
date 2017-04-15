package com.starsoft.dbtolls.executors;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * Created by StarkinDG on 13.04.2017.
 */

public class DbCommandExecutor extends ThreadPoolExecutor {
    
    private AtomicInteger currentRunTasks = new AtomicInteger(0);
    
    public DbCommandExecutor(int corePoolSize, int maximumPoolSize, long keepAliveTime, TimeUnit unit, BlockingQueue<Runnable> workQueue, ThreadFactory threadFactory) {
        
        super(corePoolSize, maximumPoolSize, keepAliveTime, unit, workQueue, threadFactory);
    }
    
    @Override
    protected void beforeExecute(Thread t, Runnable r) {
        
        super.beforeExecute(t, r);
        currentRunTasks.getAndIncrement();
    }
    
    @Override
    protected void afterExecute(Runnable r, Throwable t) {
        
        super.afterExecute(r, t);
        currentRunTasks.getAndDecrement();
    }
    
    @Override
    protected void terminated() {
        
        super.terminated();
    }
    
    public int getCount() {
        
        return currentRunTasks.get();
    }
}
