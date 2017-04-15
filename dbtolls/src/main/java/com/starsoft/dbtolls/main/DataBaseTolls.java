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
import android.os.Message;
import android.util.Log;
import android.util.SparseIntArray;

import com.starsoft.dbtolls.executors.DBHandler;
import com.starsoft.dbtolls.executors.DbCommandExecutor;
import com.starsoft.dbtolls.executors.DbThreadFactory;
import com.starsoft.dbtolls.runables.CursorLoader;
import com.starsoft.dbtolls.runables.DataWriter;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.lang.ref.WeakReference;
import java.util.Arrays;
import java.util.concurrent.LinkedBlockingQueue;

import static com.starsoft.dbtolls.BuildConfig.DEBUG;
import static com.starsoft.dbtolls.setting.Constants.DEFAULT_DATA_BASE_IDLE_TIME;
import static com.starsoft.dbtolls.setting.Constants.MESSAGE_CLOSE_DB;
import static com.starsoft.dbtolls.setting.Constants.MIN_IDLE_TIME;
import static com.starsoft.dbtolls.setting.Constants.MIN_THREAD_NUMBER;
import static com.starsoft.dbtolls.setting.Constants.NUMBER_OF_ATTEMPTS_OPEN_DB;
import static com.starsoft.dbtolls.setting.Constants.SPARSE_ARRAY_INIT_CAPACITY;
import static com.starsoft.dbtolls.setting.Constants.THREAD_IDLE_TIME;
import static com.starsoft.dbtolls.setting.Constants.THREAD_START_TERM;
import static com.starsoft.dbtolls.setting.Constants.TIME_UNIT;

public class DataBaseTolls {
    
    static DataBaseTolls instance;
    private int attemptsDbOpen;
    private InputStream mDbInputStream;
    private SparseIntArray mTasksCounter = new SparseIntArray(SPARSE_ARRAY_INIT_CAPACITY);
    private boolean mDisableCallback;
    private boolean mNeedClose;
    private long DataBaseIdleTime = DEFAULT_DATA_BASE_IDLE_TIME;
    private long mThreadLiveTime;
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
    
    public synchronized static DataBaseTolls getInstance() {
        
        if (instance == null) {
            throw new IllegalStateException("DataBaseTolls instance not create");
        }
        return instance;
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
    
    public synchronized SQLiteDatabase getDataBase() {
        
        if (mDbInputStream != null) {
            boolean dbFileCorrupted = false;
            SQLiteDatabase db = getDb();
            int currentVersion = db.getVersion();
            String dbFile = db.getPath();
            db.close();
            int bufferLength;
            byte[] buffer = new byte[1024];
            FileOutputStream fileOutputStream = null;
            try {
                fileOutputStream = new FileOutputStream(dbFile);
                while ((bufferLength = mDbInputStream.read(buffer)) > 0) {
                    fileOutputStream.write(buffer, 0, bufferLength);
                }
            } catch (IOException e) {
                e.printStackTrace();
                dbFileCorrupted = true;
            } finally {
                if (fileOutputStream != null) {
                    try {
                        fileOutputStream.close();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
                try {
                    mDbInputStream.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
                mDbInputStream = null;
                if (dbFileCorrupted) {
                    File corruptedDbFile = new File(dbFile);
                    if (corruptedDbFile.delete()) {
                        attemptsDbOpen++;
                    }
                } else {
                    db = SQLiteDatabase.openDatabase(dbFile, null, SQLiteDatabase.OPEN_READWRITE);
                    db.setVersion(currentVersion);
                    db.close();
                }
            }
        }
        if (attemptsDbOpen <= NUMBER_OF_ATTEMPTS_OPEN_DB) {
            return getDb();
        } else {
            throw new SQLiteException("Fail to open db, all attempts used");
        }
    }
    
    private SQLiteDatabase getDb() {
        
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
    
    public void destroy() {
        
        mDisableCallback = true;
        mNeedClose = true;
        clearAllTasks();
        DataBaseIdleTime = MIN_IDLE_TIME;
        startDbCloseTimerIfPossible();
    }
    
    public void clearAllTasks() {
        
        getExecutor().purge();
    }
    
    public void onCursorLoaded(int tag, Cursor cursor) {
        
        startDbCloseTimerIfPossible();
        decrementTaskCount(mTasksCounter, tag);
        if (mCursorReadyListener.get() != null && !mDisableCallback && mTasksCounter.get(tag) == 0) {
            mCursorReadyListener.get().onCursorReady(cursor);
        } else {
            cursor.close();
        }
        if (DEBUG) {
            Log.d("MainActivity", "onCursorLoaded: ");
        }
    }
    
    public void onDataWrite(boolean result) {
        
        startDbCloseTimerIfPossible();
        if (mDataWriteListener.get() != null && !mDisableCallback) {
            mDataWriteListener.get().onDataWrite(result);
        }
    }
    
    private void startDbCloseTimerIfPossible() {
        
        if (mExecutor.getCount() == 0) {
            restartDbCloseTimer();
        }
    }
    
    private void restartDbCloseTimer() {
        
        getDBHandler().removeMessages(MESSAGE_CLOSE_DB);
        Message message = getDBHandler().obtainMessage(MESSAGE_CLOSE_DB, this);
        getDBHandler().sendMessageDelayed(message, DataBaseIdleTime);
    }
    
    private void stopDbCloseTimer() {
        
        getDBHandler().removeMessages(MESSAGE_CLOSE_DB);
    }
    
    public void closeDb() {
        
        if (mExecutor.getCount() == 0) {
            closeDataBase();
            if (mNeedClose) {
                instance = null;
            }
        } else {
            DataBaseIdleTime = MIN_IDLE_TIME;
        }
    }
    
    private DbCommandExecutor getExecutor() {
        
        if (mExecutor == null) {
            int threadNumber = Runtime.getRuntime().availableProcessors() + THREAD_START_TERM;
            threadNumber = threadNumber < MIN_THREAD_NUMBER ? MIN_THREAD_NUMBER : threadNumber;
            
            mExecutor = new DbCommandExecutor(threadNumber, threadNumber,
                    THREAD_IDLE_TIME, TIME_UNIT, new LinkedBlockingQueue<Runnable>(),
                    new DbThreadFactory());
            mExecutor.allowCoreThreadTimeOut(true);
        }
        return mExecutor;
    }
    
    private void closeDataBase() {
        
        if (mDBHelper != null) mDBHelper.close();
    }
    
    private boolean cancelTask(int tag) {
        
        return getExecutor().remove(new CursorLoader(tag, new CursorLoader.CursorGetter() {
            @Override
            public Cursor getCursor(SQLiteDatabase dataBase, String... args) {
                
                return null;
            }
        }, null));
    }
    
    private void cancelTasks(int tag) {
        
        while (cancelTask(tag)) {
            decrementTaskCount(mTasksCounter, tag);
        }
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
    
    public void getDataUsingSQLCommand(int tag, String... sqlQuery) {
        
        if (!mNeedClose) {
            stopDbCloseTimer();
            //TODO if need
            cancelTasks(tag);
            incrementTaskCount(mTasksCounter, tag);
            getExecutor().execute(new CursorLoader(tag, new CursorLoader.CursorGetter() {
                @Override
                public Cursor getCursor(SQLiteDatabase dataBase, String... args) {
                    
                    return getDataUsingSQLCommand(dataBase, args);
                }
            }, sqlQuery));
        }
    }
    
    private void incrementTaskCount(SparseIntArray array, int pos) {
        
        int value = array.get(pos);
        array.put(pos, ++value);
    }
    
    private void decrementTaskCount(SparseIntArray array, int pos) {
        
        int value = array.get(pos);
        array.put(pos, --value);
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
            for (String sql : ((String[]) sQlCommandArray)) {
                
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
            InputStream inputStream = new FileInputStream((File) sQLScriptFile);
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
    
    public void executeSQLCommandsFromFile(File sQLScriptFile) {
        
        if (!mNeedClose) {
            stopDbCloseTimer();
            getExecutor().execute(new DataWriter<File>(new DataWriter.DBWriter() {
                @Override
                public boolean writeData(SQLiteDatabase dataBase, Object args) {
                    
                    return executeSQLCommandsFromFile(dataBase, args);
                }
            }, sQLScriptFile));
        }
    }
    
    public void executeSQLCommandFromInputStream(InputStream inputStream) {
        
        if (!mNeedClose) {
            stopDbCloseTimer();
            getExecutor().execute(new DataWriter<InputStream>(new DataWriter.DBWriter() {
                @Override
                public boolean writeData(SQLiteDatabase dataBase, Object args) {
                    
                    return executeSQLCommandFromInputStream(dataBase, args);
                }
            }, inputStream));
        }
    }
    
    public void executeSQLCommandFromArray(String... sQlCommandArray) {
        
        if (!mNeedClose) {
            stopDbCloseTimer();
            getExecutor().execute(new DataWriter<String[]>(new DataWriter.DBWriter() {
                @Override
                public boolean writeData(SQLiteDatabase dataBase, Object args) {
                    
                    return executeSQLCommandsFromArray(dataBase, args);
                }
            }, sQlCommandArray));
        }
    }
    
    public boolean executeSQLCommandFromInputStream(SQLiteDatabase db, Object inputStream) {
        
        try {
            if (inputStream != null) {
                InputStreamReader isr = new InputStreamReader((InputStream) inputStream);
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
                ((InputStream) inputStream).close();
                return true;
            }
            return false;
        } catch (IOException e) {
            throw new SQLiteException("Fail to initial db", e);
        } catch (SQLiteException ex) {
            try {
                ((InputStream) inputStream).close();
                return false;
            } catch (IOException e) {
                // TODO catch
                e.printStackTrace();
            }
            throw new SQLiteException("Fail to initial db", ex);
        }
    }
    
    public interface onCursorReadyListener {
        
        void onCursorReady(Cursor cursor);
    }
    
    public interface onDataWriteListener {
        
        void onDataWrite(boolean result);
    }
    
    private class DBHelper extends SQLiteOpenHelper {
        
        private DataBaseFactory mDataBaseFactory;
        
        public DBHelper(Context context, String name, CursorFactory factory, int version, DataBaseFactory dBFactory) {
            
            super(context, name, factory, version);
            mDataBaseFactory = dBFactory;
        }
        
        @Override
        public void onCreate(SQLiteDatabase db) {
            
            mDataBaseFactory.createDataBase(db, DataBaseTolls.this, new DbReplacer());
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
    
    public class DbReplacer {
        
        public void replaceFrom(InputStream inputStream) {
            
            mDbInputStream = inputStream;
        }
    }
}
