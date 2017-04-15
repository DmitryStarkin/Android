package com.starsoft.dbtolls.main;

import android.content.Context;

import java.lang.ref.WeakReference;

/**
 * Created by StarkinDG on 13.04.2017.
 */

public class DbTollsBuilder {
    
    private String mDbName;
    private int mDbVersion;
    private DataBaseFactory mDataBaseFactory;
    private long mDataBaseIdleTime;
    private long mThreadLiveTime;
    private int mThreadNumber;
    private int mThreadPriority;
    private WeakReference<DataBaseTolls.onCursorReadyListener> mCursorReadyListener;
    private WeakReference<DataBaseTolls.onDataWriteListener> mDataWriteListener;
    
    public DbTollsBuilder setName(String name) {
        
        if (name == null) {
            throw new IllegalStateException("Name is null");
        }
        mDbName = name;
        return this;
    }
    
    public DbTollsBuilder setVersion(int version) {
        
        if (version < 1) {
            throw new IllegalStateException("Version must be large of 0");
        }
        mDbVersion = version;
        return this;
    }
    
    public DbTollsBuilder setDataBaseFactory(DataBaseFactory factory) {
        
        if (factory != null) {
            mDataBaseFactory = factory;
        } else {
            throw new IllegalStateException("dataBaseFactory is null");
        }
        
        return this;
    }
    
    public DbTollsBuilder setNumberThreadsForProcessing(int number) {
        
        mThreadNumber = number;
        return this;
    }
    
    public DbTollsBuilder setNumberThreadsPriority(int priority) {
        
        mThreadPriority = priority;
        return this;
    }
    
    public DbTollsBuilder setThreadsIdleTime(long time) {
        
        mThreadLiveTime = time;
        return this;
    }
    
    public DbTollsBuilder setDBOpenedIdleTime(long time) {
        
        mDataBaseIdleTime = time;
        return this;
    }
    
    public DbTollsBuilder setOnCursorReadyListener(DataBaseTolls.onCursorReadyListener listener) {
        
        mCursorReadyListener = new WeakReference<>(listener);
        return this;
    }
    
    public DbTollsBuilder setonDataWriteListener(DataBaseTolls.onDataWriteListener listener) {
        
        mDataWriteListener = new WeakReference<>(listener);
        return this;
    }
    
    public DataBaseTolls buildWith(Context context) {
        
        if (DataBaseTolls.instance == null) {
            DataBaseTolls.instance = new DataBaseTolls(context.getApplicationContext(), mDbName, mDbVersion, mDataBaseFactory,
                    mDataBaseIdleTime, mThreadLiveTime, mThreadNumber, mThreadPriority, mCursorReadyListener, mDataWriteListener);
            return DataBaseTolls.instance;
        } else {
            DataBaseTolls.instance.setOnCursorReadyListener(mCursorReadyListener.get());
            DataBaseTolls.instance.setonDataWriteListener(mDataWriteListener.get());
            return DataBaseTolls.instance;
        }
    }
}

