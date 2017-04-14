package com.starsoft.dbtolls.main;

/**
 * Created by StarkinDG on 12.04.2017.
 */

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteDatabase.CursorFactory;
import android.database.sqlite.SQLiteException;
import android.database.sqlite.SQLiteOpenHelper;

import com.starsoft.dbtolls.executors.DBHandler;
import com.starsoft.dbtolls.executors.DbCommandExecutor;
import com.starsoft.dbtolls.executors.DbThreadFactory;
import com.starsoft.dbtolls.runables.CursorLoader;
import com.starsoft.dbtolls.runables.DataWriter;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.lang.ref.WeakReference;
import java.util.Arrays;
import java.util.concurrent.LinkedBlockingQueue;

import static com.starsoft.dbtolls.setting.Constants.MIN_THREAD_NUMBER;
import static com.starsoft.dbtolls.setting.Constants.THREAD_IDLE_TIME;
import static com.starsoft.dbtolls.setting.Constants.THREAD_START_TERM;
import static com.starsoft.dbtolls.setting.Constants.TIME_UNIT;

public class DataBaseTolls {
    
    static DataBaseTolls instance;
    private DBHelper mDBHelper;
    private DBHandler mDBHandler;
    private DbCommandExecutor mExecutor;
    private WeakReference<onCursorReadyListener> mCursorReadyListener;
    private WeakReference<onDataWriteListener> mDataWriteListener;
    
    DataBaseTolls(Context context, String dbName, int dbVersion, DataBaseFactory factory, WeakReference<onCursorReadyListener> onCursorReadyListener,
                  WeakReference<onDataWriteListener> onDataWriteListener) {
        
        if (factory == null) {
            throw new IllegalStateException("need dataBaseFactory");
        } else {
            mDBHelper = new DBHelper(context, dbName, null, dbVersion, factory);
        }
        mCursorReadyListener = onCursorReadyListener;
        mDataWriteListener = onDataWriteListener;
    }
    
    public interface onCursorReadyListener {
        
        void onCursorReady(Cursor cursor);
    }
    
    public interface onDataWriteListener {
        
        void onDataWrite(boolean result);
    }
    
    public void setOnCursorReadyListener(onCursorReadyListener listener) {
        
        mCursorReadyListener = new WeakReference<>(listener);
    }
    
    public void setonDataWriteListener(onDataWriteListener listener) {
        
        mDataWriteListener = new WeakReference<>(listener);
    }
    
    public synchronized DBHandler getDBHandler() {
        
        if (mDBHandler == null) {
            mDBHandler = new DBHandler();
        }
        return mDBHandler;
    }
    
    public synchronized static DataBaseTolls getInstance() {
        
        if(instance == null){
            throw new IllegalStateException("DataBaseTolls instance not create");
        }
        return instance;
    }
    
    public synchronized SQLiteDatabase getDataBase() {
        
        try {
            return mDBHelper.getWritableDatabase();
        } catch (SQLiteException ex) {
            ex.printStackTrace();
            try {
                return mDBHelper.getReadableDatabase();
            } catch (SQLiteException e) {
                e.printStackTrace();
                throw new SQLiteException("Fail to open db", e);
            }
        }
    }
    
    public void destroySelf(){
        
        //TODO close executor
        instance = null;
    }
    
    public void onCursorLoaded(Cursor cursor) {
        
        if(mCursorReadyListener.get() != null){
            mCursorReadyListener.get().onCursorReady(cursor);
        }
    }
    
    public void onDataWrite(boolean result) {
        
        if(mDataWriteListener.get() != null){
            mDataWriteListener.get().onDataWrite(result);
        }
    }
    
    private DbCommandExecutor getExecutor() {
        
        if (mExecutor == null) {
            int threadNumber = Runtime.getRuntime().availableProcessors() + THREAD_START_TERM;
            threadNumber = threadNumber < MIN_THREAD_NUMBER ? MIN_THREAD_NUMBER : threadNumber;
            
            mExecutor = new DbCommandExecutor(threadNumber, threadNumber,
                    THREAD_IDLE_TIME, TIME_UNIT, new LinkedBlockingQueue<>(),
                    new DbThreadFactory());
            mExecutor.allowCoreThreadTimeOut(true);
        }
        return mExecutor;
    }
    
    private void closeDataBase() {
        
        if (mDBHelper != null) mDBHelper.close();
    }
    
    public String getDbName() {
        
        if (mDBHelper != null) {
            return mDBHelper.getDatabaseName();
        } else {
            return null;
        }
    }
    
    public int getDbVersion() {
        
        return getDataBase().getVersion();
    }
    
    public Cursor getAllDataFromTable(SQLiteDatabase dataBase, String tableName) {
        
        try {
            return dataBase.query(tableName, null, null, null, null, null, null);
        } catch (SQLiteException e) {
            e.printStackTrace();
            return null;
        }
    }
    
    public Cursor getDataUsingSQLCommand(SQLiteDatabase dataBase, String... sqlQuery) {
        
        String[] selectionArgs = null;
        if (sqlQuery.length > 1) {
            selectionArgs = Arrays.copyOfRange(sqlQuery, 1, sqlQuery.length - 1);
        }
        try {
            return dataBase.rawQuery(sqlQuery[0], selectionArgs);
        } catch (SQLiteException e) {
            e.printStackTrace();
            return null;
        }
    }
    
    public void getDataUsingSQLCommand(String... sqlQuery) {
        
        getExecutor().execute(new CursorLoader(this::getDataUsingSQLCommand, sqlQuery));
    }
    
    public boolean executeSQLCommand(SQLiteDatabase dataBase, String sqlQuery) {
        
        try {
            dataBase.execSQL(sqlQuery);
            return true;
        } catch (SQLiteException e) {
            e.printStackTrace();
            return false;
        }
    }
    
    public boolean executeSQLCommandsFromArray(SQLiteDatabase db, Object sQlCommandArray) {
        
        try {
            db.beginTransaction();
            for (String sql : ((String[])sQlCommandArray)) {
                
                db.execSQL(sql);
            }
            db.setTransactionSuccessful();
            db.endTransaction();
            return true;
        } catch (SQLiteException ex) {
            ex.printStackTrace();
            db.endTransaction();
            return false;
        }
    }
    
    public boolean executeSQLCommandsFromFile(SQLiteDatabase db, Object sQLScriptFile) {
        
        try {
            db.beginTransaction();
            InputStream inputStream = new FileInputStream((File)sQLScriptFile);
            executeSQLCommandFromInputStream(db, inputStream);
            db.setTransactionSuccessful();
            return true;
        } catch (FileNotFoundException e) {
            e.printStackTrace();
            return false;
        } catch (SQLiteException ex) {
            ex.printStackTrace();
            return false;
        } finally {
            db.endTransaction();
        }
    }
    
    public void executeSQLCommandsFromFile(File sQLScriptFile)
    {
        getExecutor().execute(new DataWriter<File>(this::executeSQLCommandsFromFile, sQLScriptFile));
    }
    
    public void executeSQLCommandFromInputStream(InputStream inputStream)
    {
        getExecutor().execute(new DataWriter<InputStream>(this::executeSQLCommandFromInputStream, inputStream));
    }
    
    public void executeSQLCommandFromInputStream(String... sQlCommandArray)
    {
        getExecutor().execute(new DataWriter<String[]>(this::executeSQLCommandsFromArray, sQlCommandArray));
    }
    
    public boolean executeSQLCommandFromInputStream(SQLiteDatabase db, Object inputStream) {
        
        try {
            if (inputStream != null) {
                InputStreamReader isr = new InputStreamReader((InputStream)inputStream);
                BufferedReader reader = new BufferedReader(isr);
                String line, sql;
                StringBuilder builder = new StringBuilder();
                int counter = 0;
                while ((line = reader.readLine()) != null) {
                    counter = (int) (counter + Math.floor(line.length() * 1.12));
                    line = line.trim();
                    if (!(line.endsWith("*/"))) {
                        builder.append(" ");
                        builder.append(line);
                    }
                    if (line.endsWith(";")) {
                        sql = builder.toString();
                        db.execSQL(sql);
                        builder = new StringBuilder();
                    }
                }
                ((InputStream)inputStream).close();
                return true;
            }
            return false;
        } catch (IOException e) {
            throw new SQLiteException("Fail to initial db", e);
        } catch (SQLiteException ex) {
            try {
                ((InputStream)inputStream).close();
                return false;
            } catch (IOException e) {
                // TODO catch
                e.printStackTrace();
            }
            throw new SQLiteException("Fail to initial db", ex);
        }
    }
    
    private class DBHelper extends SQLiteOpenHelper {
        
        private DataBaseFactory mDataBaseFactory;
        
        public DBHelper(Context context, String name, CursorFactory factory, int version, DataBaseFactory dBFactory) {
            
            super(context, name, factory, version);
            mDataBaseFactory = dBFactory;
        }
        
        @Override
        public void onCreate(SQLiteDatabase db) {
            
            mDataBaseFactory.createDataBase(db, DataBaseTolls.this);
        }
        
        @Override
        public void onOpen(SQLiteDatabase db) {
            
            if (!mDataBaseFactory.beforeOpenDataBase(db, DataBaseTolls.this)) {
                //TODO default open command
            }
        }
        
        @Override
        public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
            
            mDataBaseFactory.UpgradeDataBase(db, oldVersion, newVersion, DataBaseTolls.this);
        }
    }
}
