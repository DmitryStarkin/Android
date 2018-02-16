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
