package com.hplasplas.task7.utils;

/**
 * Created by StarkinDG on 08.04.2017.
 */

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import android.database.sqlite.SQLiteOpenHelper;

import com.hplasplas.task7.App;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

import static com.hplasplas.task7.setting.Constants.DB_SQL_NAME;

public class DataBaseTolls {
    
    private DBHelper myDBHelper;
    private SQLiteDatabase myDataBase;
    
    public DataBaseTolls(String dBaseName, int dBVersion) {
        
        myDBHelper = new DBHelper(App.getAppContext(), dBaseName, dBVersion);
    }
    
    private DataBaseTolls() {
        
    }
    
    public boolean openDataBase() {
        
        if (myDataBase == null || !myDataBase.isOpen()) {
            try {
                myDataBase = myDBHelper.getWritableDatabase();
                return true;
            } catch (SQLiteException ex) {
                ex.printStackTrace();
                try {
                    myDataBase = myDBHelper.getReadableDatabase();
                    return true;
                } catch (SQLiteException e) {
                    e.printStackTrace();
                    return false;
                }
            }
        }
        return true;
    }
    
    public void closeDataBase() {
        
        if (myDBHelper != null) myDBHelper.close();
    }
    
    public Cursor getAllDataFromTable(String tableName) {
        
        try {
            return myDataBase.query(tableName, null, null, null, null, null, null);
        } catch (SQLiteException e) {
            return null;
        }
    }
    
    public Cursor getDataUsingSQLCommand(String sqlQuery) {
        
        try {
            return myDataBase.rawQuery(sqlQuery, null);
        } catch (SQLiteException e) {
            e.printStackTrace();
            return null;
        } catch (IllegalStateException e){
            e.printStackTrace();
            return null;
        }
    }
    
    public boolean executeSQLCommand(String sqlQuery) {
        
        try {
            myDataBase.execSQL(sqlQuery);
            return true;
        } catch (SQLiteException e) {
            e.printStackTrace();
            return false;
        }
    }
    
    public boolean executeSQLCommandsFromArray(String... sQlCommandArray) {
        
        return (executeSQLCommandsFromArray(myDataBase, sQlCommandArray));
    }
    
    public boolean executeSQLCommandsFromArray(SQLiteDatabase db, String... sQlCommandArray) {
        
        try {
            db.beginTransaction();
            for (String sql : sQlCommandArray) {
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
    
    public boolean executeSQLCommandsFromFile(File sQLScriptFile) {
        
        try {
            myDataBase.beginTransaction();
            InputStream inputStream = new FileInputStream(sQLScriptFile);
            executeSQLCommandFromInputStream(myDataBase, inputStream);
            myDataBase.setTransactionSuccessful();
            return true;
        } catch (FileNotFoundException e) {
            e.printStackTrace();
            return false;
        } catch (SQLiteException ex) {
            ex.printStackTrace();
            return false;
        } finally {
            myDataBase.endTransaction();
        }
    }
    
    public void executeSQLCommandFromInputStream(SQLiteDatabase db, InputStream inputStream) {
        
        try {
            if (inputStream != null) {
                InputStreamReader isr = new InputStreamReader(inputStream);
                BufferedReader reader = new BufferedReader(isr);
                String line, sql;
                StringBuilder builder = new StringBuilder();
                int counter = 0;
                while ((line = reader.readLine()) != null) {
                    counter = (int) (counter + Math.floor(line.length() * 1.12));
                    line.trim();
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
                inputStream.close();
            }
        } catch (IOException e) {
            e.printStackTrace();
            throw new SQLiteException("Fail to initial db", e);
        } catch (SQLiteException ex) {
            ex.printStackTrace();
            try {
                inputStream.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
            throw new SQLiteException("Fail to initial db", ex);
        }
    }
    
    private class DBHelper extends SQLiteOpenHelper {
        
        DBHelper(Context context, String name, int version) {
            
            super(context, name, null, version);
        }
        
        @Override
        public void onCreate(SQLiteDatabase db) {
            
            InputStream inputStream;
            try {
                inputStream = App.getAppContext().getAssets().open(DB_SQL_NAME);
                executeSQLCommandFromInputStream(db, inputStream);
            } catch (IOException e) {
                e.printStackTrace();
                throw new SQLiteException("Fail to initial db", e);
            }
        }
        
        @Override
        public void onOpen(SQLiteDatabase db) {
            // executed command is necessary before opening database
            
        }
        
        @Override
        public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
            
        }
    }
}
