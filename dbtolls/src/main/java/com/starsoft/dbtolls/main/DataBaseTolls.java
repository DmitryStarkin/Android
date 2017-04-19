package com.starsoft.dbtolls.main;

/**
 * Created by StarkinDG on 12.04.2017.
 */

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import android.os.Message;
import android.util.SparseIntArray;

import com.starsoft.dbtolls.executors.DBHandler;
import com.starsoft.dbtolls.executors.DbCommandExecutor;
import com.starsoft.dbtolls.executors.DbThreadFactory;
import com.starsoft.dbtolls.runables.CursorLoader;
import com.starsoft.dbtolls.runables.DataWriter;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.lang.ref.WeakReference;
import java.util.Arrays;
import java.util.concurrent.LinkedBlockingQueue;

import static com.starsoft.dbtolls.setting.Constants.DB_COPY_BUFFER_LENGTH;
import static com.starsoft.dbtolls.setting.Constants.DEFAULT_DATA_BASE_IDLE_TIME;
import static com.starsoft.dbtolls.setting.Constants.MESSAGE_CLOSE_DB;
import static com.starsoft.dbtolls.setting.Constants.MIN_DB_IDLE_TIME;
import static com.starsoft.dbtolls.setting.Constants.NUMBER_OF_ATTEMPTS_OPEN_DB;
import static com.starsoft.dbtolls.setting.Constants.SPARSE_ARRAY_INIT_CAPACITY;
import static com.starsoft.dbtolls.setting.Constants.THREAD_DEFAULT_IDLE_TIME;
import static com.starsoft.dbtolls.setting.Constants.THREAD_START_TERM;
import static com.starsoft.dbtolls.setting.Constants.TIME_UNIT;

public class DataBaseTolls {
    
    static DataBaseTolls instance;
    private int attemptsDbOpen;
    private InputStream mDbInputStream;
    private SparseIntArray mTasksCounter;
    private boolean mDisableCallback;
    private boolean mNeedClose;
    private long mDataBaseIdleTime;
    private long mThreadLiveTime;
    private int mThreadNumber;
    private int mThreadPriority;
    private DBHelper mDBHelper;
    private DBHandler mDBHandler;
    private DbCommandExecutor mExecutor;
    private WeakReference<onErrorListener> mErrorListener;
    private WeakReference<onCursorReadyListener> mCursorReadyListener;
    private WeakReference<onDataWriteListener> mDataWriteListener;
    
    DataBaseTolls(Context context, String dbName, int dbVersion, DataBaseFactory factory, long dbIdleTime,
                  long threadIdleTime, int threadNumber, int threadPriority, WeakReference<onCursorReadyListener> onCursorReadyListener,
                  WeakReference<onDataWriteListener> onDataWriteListener, WeakReference<onErrorListener> onErrorListener) {
        
        if (factory == null) {
            throw new IllegalStateException("need dataBaseFactory");
        } else {
            mDBHelper = new DBHelper(context, dbName, null, dbVersion, factory);
        }
        mTasksCounter = new SparseIntArray(SPARSE_ARRAY_INIT_CAPACITY);
        if (dbIdleTime <= 0) {
            mDataBaseIdleTime = DEFAULT_DATA_BASE_IDLE_TIME;
        } else {
            mDataBaseIdleTime = dbIdleTime;
        }
        mThreadLiveTime = threadIdleTime;
        mThreadNumber = threadNumber;
        mThreadPriority = threadPriority;
        mCursorReadyListener = onCursorReadyListener;
        mDataWriteListener = onDataWriteListener;
        mErrorListener = onErrorListener;
    }
    
    public void setOnCursorReadyListener(onCursorReadyListener listener) {
        
        mCursorReadyListener = new WeakReference<>(listener);
    }
    
    public void setOnDataWriteListener(onDataWriteListener listener) {
        
        mDataWriteListener = new WeakReference<>(listener);
    }
    
    public void setOnErrorListener(onErrorListener listener) {
    
        mErrorListener = new WeakReference<>(listener);
    }
    
    public synchronized static DataBaseTolls getInstance() {
        
        if (instance == null) {
            throw new IllegalStateException("DataBaseTolls instance not create");
        }
        return instance;
    }
    
    private DbCommandExecutor getExecutor() {
        
        if (mExecutor == null) {
            
            if (mThreadNumber <= 0) {
                mThreadNumber = Runtime.getRuntime().availableProcessors() + THREAD_START_TERM;
            }
            if (mThreadLiveTime <= 0) {
                mThreadLiveTime = THREAD_DEFAULT_IDLE_TIME;
            }
            
            mExecutor = new DbCommandExecutor(mThreadNumber, mThreadNumber,
                    mThreadLiveTime, TIME_UNIT, new LinkedBlockingQueue<Runnable>(), new DbThreadFactory(mThreadPriority));
            mExecutor.allowCoreThreadTimeOut(true);
        }
        return mExecutor;
    }
    
    public synchronized DBHandler getDBHandler() {
        
        if (mDBHandler == null) {
            mDBHandler = new DBHandler();
        }
        return mDBHandler;
    }
    
    public synchronized SQLiteDatabase getDataBase() {
        
        SQLiteDatabase db = getDb();
        boolean dbFileCorrupted = false;
        if (mDbInputStream != null) {
            int currentVersion = db.getVersion();
            String dbFile = db.getPath();
            db.close();
            int bufferLength;
            byte[] buffer = new byte[DB_COPY_BUFFER_LENGTH];
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
                if (dbFileCorrupted && (attemptsDbOpen <= NUMBER_OF_ATTEMPTS_OPEN_DB)) {
                    File corruptedDbFile = new File(dbFile);
                    if (corruptedDbFile.delete()) {
                        attemptsDbOpen++;
                        getDataBase();
                    }
                } else if (!dbFileCorrupted) {
                    db = SQLiteDatabase.openDatabase(dbFile, null, SQLiteDatabase.OPEN_READWRITE);
                    db.setVersion(currentVersion);
                    db.close();
                }
            }
        }
        if (dbFileCorrupted) {
            throw new SQLiteException("Fail to open db, all attempts used");
        } else if (db.isOpen()) {
            return db;
        }
        return getDb();
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
                throw new SQLiteException("DBHelper: Fail to open db", e);
            }
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
    
    public void onCursorLoaded(int tag, Cursor cursor) {
        
        startDbCloseTimerIfPossible();
        decrementTaskCount(mTasksCounter, tag);
        if (mCursorReadyListener.get() != null && !mDisableCallback && mTasksCounter.get(tag) == 0) {
            mCursorReadyListener.get().onCursorReady(cursor);
        } else {
            cursor.close();
        }
    }
    
    public void onDataWrite(int tag, boolean result) {
        
        startDbCloseTimerIfPossible();
        if (mDataWriteListener.get() != null && !mDisableCallback) {
            mDataWriteListener.get().onDataWrite(tag, result);
        }
    }
    
    public void onError(int tag, Throwable e) {
        
        e.printStackTrace();
        startDbCloseTimerIfPossible();
        if (mErrorListener.get() != null && !mDisableCallback) {
            mErrorListener.get().onError(tag, e);
        }
    }
    
    public void closeDb() {
        
        if (mExecutor.getCount() == 0) {
            closeDataBase();
            if (mNeedClose) {
                instance = null;
            }
        } else {
            mDataBaseIdleTime = MIN_DB_IDLE_TIME;
        }
    }
    
    private void closeDataBase() {
        
        if (mDBHelper != null) mDBHelper.close();
    }
    
    public void clearAllTasks() {
        
        getExecutor().purge();
    }
    
    private void startDbCloseTimerIfPossible() {
        
        if (mExecutor.getCount() == 0) {
            restartDbCloseTimer();
        }
    }
    
    private void restartDbCloseTimer() {
        
        getDBHandler().removeMessages(MESSAGE_CLOSE_DB);
        Message message = getDBHandler().obtainMessage(MESSAGE_CLOSE_DB, this);
        getDBHandler().sendMessageDelayed(message, mDataBaseIdleTime);
    }
    
    private void stopDbCloseTimer() {
        
        getDBHandler().removeMessages(MESSAGE_CLOSE_DB);
    }
    
    private void incrementTaskCount(SparseIntArray array, int pos) {
        
        int value = array.get(pos);
        array.put(pos, ++value);
    }
    
    private void decrementTaskCount(SparseIntArray array, int pos) {
        
        int value = array.get(pos);
        array.put(pos, --value);
    }
    
    public void destroy() {
        
        mDisableCallback = true;
        mNeedClose = true;
        clearAllTasks();
        mDataBaseIdleTime = MIN_DB_IDLE_TIME;
        startDbCloseTimerIfPossible();
    }
    
    private void cancelTasks(int tag) {
        
        while (cancelTask(tag)) {
            decrementTaskCount(mTasksCounter, tag);
        }
    }
    
    private boolean cancelTask(int tag) {
        
        return getExecutor().remove(new CursorLoader(tag, new CursorLoader.CursorGetter() {
            @Override
            public Cursor getCursor(SQLiteDatabase dataBase, String... args) {
                
                return null;
            }
        }, null));
    }
    
    public Cursor getAllDataFromTable(SQLiteDatabase dataBase, String tableName) throws  SQLiteException {
        
            return dataBase.query(tableName, null, null, null, null, null, null);
    }
    
    public Cursor getDataUsingSQLCommand(SQLiteDatabase dataBase, String... sqlQuery) throws  SQLiteException{
        
        String[] selectionArgs = null;
        if (sqlQuery.length > 1) {
            selectionArgs = Arrays.copyOfRange(sqlQuery, 1, sqlQuery.length - 1);
        }
            return dataBase.rawQuery(sqlQuery[0], selectionArgs);
    }
    
    public void getDataUsingSQLCommand(int tag, String... sqlQuery) {
        
        if (!mNeedClose) {
            stopDbCloseTimer();
            //TODO if need
            cancelTasks(tag);
            incrementTaskCount(mTasksCounter, tag);
            getExecutor().execute(new CursorLoader(tag, new CursorLoader.CursorGetter() {
                @Override
                public Cursor getCursor(SQLiteDatabase dataBase, String... args) throws SQLiteException {
                    
                    return getDataUsingSQLCommand(dataBase, args);
                }
            }, sqlQuery));
        }
    }
    
    public void executeSQLCommand(SQLiteDatabase db, String sqlQuery) throws  SQLiteException {
        
        try {
            db.beginTransaction();
            db.execSQL(sqlQuery);
            db.setTransactionSuccessful();
        } finally  {
            db.endTransaction();
        }
    }
    
    public void executeSQLCommandsFromArray(SQLiteDatabase db, String[] sQlCommandArray) throws  SQLiteException {
        
        try {
            db.beginTransaction();
            for (String sql : (sQlCommandArray)) {
                db.execSQL(sql);
            }
            db.setTransactionSuccessful();
        } finally  {
            db.endTransaction();
        }
    }
    
    public void executeSQLCommandsFromFile(SQLiteDatabase db, File sQLScriptFile) throws  SQLiteException, IOException{
        
        InputStream inputStream = null;
        try {
            db.beginTransaction();
            inputStream = new FileInputStream(sQLScriptFile);
            executeSQLCommandFromInputStream(db, inputStream);
            db.setTransactionSuccessful();
        } finally {
            db.endTransaction();
            if(inputStream != null){
            inputStream.close();
            }
        }
    }
    
    public void executeSQLCommandsFromFile(int tag, File sQLScriptFile) {
        
        if (!mNeedClose) {
            stopDbCloseTimer();
            getExecutor().execute(new DataWriter<File>(tag, new DataWriter.DBWriter<File>() {
                @Override
                public void writeData(SQLiteDatabase dataBase, File args) throws SQLiteException, IOException {
    
                    executeSQLCommandsFromFile(dataBase, args);
                }
            }, sQLScriptFile));
        }
    }
    
    public void executeSQLCommandFromInputStream(int tag, InputStream inputStream) {
        
        if (!mNeedClose) {
            stopDbCloseTimer();
            getExecutor().execute(new DataWriter<InputStream>(tag, new DataWriter.DBWriter<InputStream>() {
                @Override
                public void writeData(SQLiteDatabase dataBase, InputStream args) throws  SQLiteException, IOException {
                    
                    executeSQLCommandFromInputStream(dataBase, args) ;
                }
            }, inputStream));
        }
    }
    
    public void executeSQLCommandFromArray(int tag, String... sQlCommandArray) {
        
        if (!mNeedClose) {
            stopDbCloseTimer();
            getExecutor().execute(new DataWriter<String[]>(tag, new DataWriter.DBWriter<String[]>() {
                @Override
                public void writeData(SQLiteDatabase dataBase, String[] args) throws  SQLiteException{
                    
                    executeSQLCommandsFromArray(dataBase, args);
                }
            }, sQlCommandArray));
        }
    }
    
    public void executeSQLCommandFromInputStream(SQLiteDatabase db, InputStream inputStream) throws  SQLiteException, IOException {
        
        try {
            db.beginTransaction();
            if (inputStream != null) {
                InputStreamReader isr = new InputStreamReader(inputStream);
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
                db.setTransactionSuccessful();
            }
        } finally {
            db.endTransaction();
            if(inputStream != null)
            {
                inputStream.close();
            }
        }
    }
    
    public interface onCursorReadyListener {
        
        void onCursorReady(Cursor cursor);
    }
    
    public interface onDataWriteListener {
        
        void onDataWrite(int tag, boolean result);
    }
    
    public interface onErrorListener {
        
        void onError(int tag, Throwable e);
    }
    
    public class DbReplacer {
        
        public void replaceFrom(InputStream inputStream) {
            
            mDbInputStream = inputStream;
        }
    }
}
