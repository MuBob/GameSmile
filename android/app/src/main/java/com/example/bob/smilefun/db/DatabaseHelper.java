package com.example.bob.smilefun.db;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.support.annotation.Nullable;

public class DatabaseHelper extends SQLiteOpenHelper {

    private static final String DATABASE_NAME = "GameFun.db";
    private static final int DATABASE_VERSION = 1;

    public DatabaseHelper(@Nullable Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        String sql = "CREATE TABLE IF NOT EXISTS " + GameInfo.TABLE_NAME + "("+
                GameInfo.COL_ID+" INTEGER PRIMARY KEY AUTOINCREMENT, " +
                GameInfo.COL_LEVEL + " INTEGER, " +
                GameInfo.COL_STATE + " INTEGER, " +
                GameInfo.COL_TIME_START + " TEXT, " +
                GameInfo.COL_TIME_END + " TEXT);";
        db.execSQL(sql);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }
}
