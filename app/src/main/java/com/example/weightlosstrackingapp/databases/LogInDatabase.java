package com.example.weightlosstrackingapp.databases;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;



public class LogInDatabase extends SQLiteOpenHelper {
    private static final String DATABASE_NAME = "logIns.db";
    private static final int VERSION = 1;

    public LogInDatabase(Context context){
        super(context, DATABASE_NAME, null, VERSION);
    }

    public static final class LogInTable{
        private final static String TABLE = "log_ins";
        private final static String COL_ID = "_id";
        private final static String COL_USERNAME = "username";
        private final static String COL_PASSWORD = "password";
    }


    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL("create table " + LogInTable.TABLE + " (" +
                LogInTable.COL_ID + " integer primary key autoincrement, " +
                LogInTable.COL_USERNAME + " text, " +
                LogInTable.COL_PASSWORD + " text)");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("drop table if exists " + LogInTable.TABLE);
        onCreate(db);
    }

    public long addLogIn(String username, String password) {
        long existingId = getLogInByUsername(username);

        // data already exists returns existing id
        if(existingId != -1) {
            return existingId; // returns -1 to handle error in LogInActivity
        }

        SQLiteDatabase db = getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(LogInTable.COL_USERNAME, username);
        values.put(LogInTable.COL_PASSWORD, password);
        return db.insert(LogInTable.TABLE, null, values);
    }

    public long getLogInByUsername(String username) {
        SQLiteDatabase db = getReadableDatabase();
        String sql = "select * from " + LogInTable.TABLE + " where username = ?";
        Cursor cursor = db.rawQuery(sql, new String[] {username});
        // username already exists in database, returns id
        if (cursor.moveToFirst()){
            long id = cursor.getLong(0);
            cursor.close();
            return id;
        }
        // username doesn't exists returns -1
        cursor.close();
        return -1;
    }

    public boolean isLogInSuccessful(String username, String password) {
        SQLiteDatabase db = getReadableDatabase();
        String sql = "select * from " + LogInTable.TABLE + " where username = ?";
        Cursor cursor = db.rawQuery(sql, new String[] {username});
        if (cursor.moveToFirst()){
            if(password.equals(cursor.getString(2))){
                cursor.close();
                return true;
            }
        }
        cursor.close();
        return false;
    }
}
