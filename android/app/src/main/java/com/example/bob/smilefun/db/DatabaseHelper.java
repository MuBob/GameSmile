package com.example.bob.smilefun.db;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.support.annotation.Nullable;

public class DatabaseHelper extends SQLiteOpenHelper {

    private static final String DATABASE_NAME = "GameSmile.db";
    private static final int DATABASE_VERSION = 1 ;

    public DatabaseHelper(@Nullable Context context) {
        super(context,DATABASE_NAME,null,DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        String sql="CREATE TABLE IF NOT EXISTS " + GameSetting.NAME+
                "(_id INTEGER PRIMARY KEY AUTOINCREMENT, " +
                GameSetting.NUM_LINE+" INTEGER, " +
                GameSetting.NUM_COLUMN+" INTEGER);";
        db.execSQL(sql);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }
}
