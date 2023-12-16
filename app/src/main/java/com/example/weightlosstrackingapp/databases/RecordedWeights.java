package com.example.weightlosstrackingapp.databases;

import android.annotation.SuppressLint;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.example.weightlosstrackingapp.WeightData;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class RecordedWeights extends SQLiteOpenHelper {
    private static final String DATABASE_NAME = "weights.db";
    private static final int VERSION = 3;

    public RecordedWeights(Context context) {
        super(context, DATABASE_NAME, null, VERSION);
    }


    public static final class WeightsTable {
        private final static String TABLE = "recorded_weights";
        private final static String COL_ID = "_id";
        private final static String COL_WEIGHT = "weight";
        private final static String COL_DATE = "date_recorded";
    }


    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL("create table " + RecordedWeights.WeightsTable.TABLE + " (" +
                RecordedWeights.WeightsTable.COL_ID + " integer primary key autoincrement, " +
                RecordedWeights.WeightsTable.COL_WEIGHT + " integer, " +
                RecordedWeights.WeightsTable.COL_DATE + " date)");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("drop table if exists " + RecordedWeights.WeightsTable.TABLE);
        onCreate(db);
    }

    public long addWeight(Date date, double weight) {
        SQLiteDatabase db = getWritableDatabase();
        ContentValues values = new ContentValues();

        values.put(WeightsTable.COL_DATE, date.getTime());
        values.put(WeightsTable.COL_WEIGHT, weight);
        return db.insert(WeightsTable.TABLE, null, values);
    }

    public List<WeightData> retrieveWeights() {
        List<WeightData> weights = new ArrayList<>();
        SQLiteDatabase db = getReadableDatabase();
        String sql = "select * from " + WeightsTable.TABLE;
        Cursor cursor = db.rawQuery(sql, null);

        if (cursor.moveToFirst()) {
            do {
                weights.add(new WeightData(cursor.getLong(0),
                        cursor.getInt(1),
                        cursor.getLong(2)));
            } while (cursor.moveToNext());
        }

        cursor.close();
        return weights;
    }

    @SuppressLint("Range")
    public WeightData retrieveByID(long id) {
        WeightData weightData = null;
        SQLiteDatabase db = getReadableDatabase();
        String sql = "SELECT * FROM " + WeightsTable.TABLE + " WHERE " + WeightsTable.COL_ID + " = ?";

        Cursor cursor = db.rawQuery(sql, new String[]{String.valueOf(id)});

        if (cursor.moveToFirst()) {
            weightData = new WeightData(cursor.getLong(0),
                    cursor.getInt(1),
                    cursor.getLong(2));
        }

        cursor.close();
        return weightData;
    }

    public void update(long id, double weight, Date date) {
        SQLiteDatabase db = getWritableDatabase();
        ContentValues values = new ContentValues();

        values.put(WeightsTable.COL_WEIGHT, weight);
        values.put(WeightsTable.COL_DATE, date.getTime());

        String whereClause = WeightsTable.COL_ID + " = ?";
        String[] whereArgs = {String.valueOf(id)};

        db.update(WeightsTable.TABLE, values, whereClause, whereArgs);
    }

    public void delete(long id) {
        SQLiteDatabase db = getWritableDatabase();
        String whereClause = WeightsTable.COL_ID + " = ?";
        String[] whereArgs = {String.valueOf(id)};
        db.delete(WeightsTable.TABLE, whereClause, whereArgs);
    }
}
