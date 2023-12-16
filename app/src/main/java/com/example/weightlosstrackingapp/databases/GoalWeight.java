package com.example.weightlosstrackingapp.databases;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class GoalWeight extends SQLiteOpenHelper {
    private static final String DATABASE_NAME = "goal_weight.db";
    private static final int version = 1;

    public GoalWeight(Context context) {
        super(context, DATABASE_NAME, null, version);
    }

    public final static class GoalTable {
        private final static String TABLE = "recorded_weights";
        private final static String COL_ID = "_id";
        private final static String COL_GOAL_WEIGHT = "chosen_goal_weight";
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL("create table " + GoalWeight.GoalTable.TABLE + " (" +
                GoalWeight.GoalTable.COL_ID + " integer primary key autoincrement, " +
                GoalWeight.GoalTable.COL_GOAL_WEIGHT + " integer )");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("drop table if exists " + GoalWeight.GoalTable.TABLE);
        onCreate(db);
    }

    public long setGoalWeight(int weight) {
        SQLiteDatabase db = getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(GoalTable.COL_GOAL_WEIGHT, weight);
        return db.insert(GoalTable.TABLE, null, values);
    }

    public void updateGoalWeight(int weight) {
        SQLiteDatabase db = getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(GoalTable.COL_GOAL_WEIGHT, weight);

        String whereClause = GoalTable.COL_ID + " = ?";
        String[] whereArgs = {"1"};

        db.update(GoalTable.TABLE, values, whereClause, whereArgs);
    }

    public int retrieveGoalWeight() {
        SQLiteDatabase db = getReadableDatabase();
        String sql = "select * from " + GoalTable.TABLE;
        Cursor cursor = db.rawQuery(sql, null);
        int goalWeight = -1;
        if (cursor.moveToFirst()) {
            goalWeight = cursor.getInt(1);
        }
        cursor.close();
        return goalWeight;
    }
}

