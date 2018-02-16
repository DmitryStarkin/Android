/*
 * Copyright © 2018. Dmitry Starkin Contacts: t0506803080@gmail.com
 *
 * This file is part of cam_capture
 *
 *     cam_capture is free software: you can redistribute it and/or modify
 *     it under the terms of the GNU General Public License as published by
 *     the Free Software Foundation, either version 3 of the License, or
 *     (at your option) any later version.
 *
 *    cam_capture is distributed in the hope that it will be useful,
 *     but WITHOUT ANY WARRANTY; without even the implied warranty of
 *     MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *     GNU General Public License for more details.
 *
 *     You should have received a copy of the GNU General Public License
 *     along with cam_capture  If not, see <http://www.gnu.org/licenses/>.
 */
package com.hplasplas.cam_capture.util;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.RejectedExecutionHandler;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

import static com.hplasplas.cam_capture.setting.Constants.QUEUE_CAPACITY;
import static com.hplasplas.cam_capture.setting.Constants.MIN_THREAD_NUMBER;
import static com.hplasplas.cam_capture.setting.Constants.THREAD_IDLE_TIME;
import static com.hplasplas.cam_capture.setting.Constants.THREAD_START_TERM;
import static com.hplasplas.cam_capture.setting.Constants.TIME_UNIT;

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
