package com.hplasplas.task6.util;

import java.util.concurrent.RejectedExecutionHandler;
import java.util.concurrent.ThreadPoolExecutor;

import static com.hplasplas.task6.setting.Constants.POOL_MAX_SIZE_MULTIPLIER;

/**
 * Created by StarkinDG on 25.03.2017.
 */

public class RejectionHandler implements RejectedExecutionHandler {
    
    @Override
    public void rejectedExecution(Runnable runnable, ThreadPoolExecutor executor) {
        
        if(!executor.isTerminating()){
            executor.setMaximumPoolSize((int)(executor.getMaximumPoolSize() * POOL_MAX_SIZE_MULTIPLIER));
            executor.execute(runnable);
        }
    }
}
