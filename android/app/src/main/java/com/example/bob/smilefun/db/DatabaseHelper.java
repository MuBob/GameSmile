package com.example.bob.smilefun.db;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.support.annotation.Nullable;
import android.util.Log;

public class DatabaseHelper extends SQLiteOpenHelper {

    private static final String DATABASE_NAME = "GameFun.db";
    private static final int DATABASE_VERSION_INIT = 1;
    private static final int DATABASE_VERSION_SECOND = 2;
    private static final String sqlCreateTable = "CREATE TABLE IF NOT EXISTS " + GameInfo.TABLE_NAME + "("+
            GameInfo.COL_ID+" INTEGER PRIMARY KEY AUTOINCREMENT, " +
            GameInfo.COL_LEVEL + " INTEGER, " +
            GameInfo.COL_STATE + " INTEGER, " +
            GameInfo.COL_DIFFICULT_LINES + " INTEGER, " +
            GameInfo.COL_DIFFICULT_ROWS + " INTEGER, " +
            GameInfo.COL_TIME_START + " TEXT, " +
            GameInfo.COL_TIME_END + " TEXT);";
    private static final String sqlDropTable="DROP TABLE "+GameInfo.TABLE_NAME+";";

    public DatabaseHelper(@Nullable Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION_SECOND);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(sqlCreateTable);
        Log.i(TAG, "DatabaseHelper.onCreate: ");
    }

    private static final String TAG = "DatabaseHelperTAG";

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        Log.i(TAG, "DatabaseHelper.onUpgrade: old="+oldVersion+", new="+newVersion);
        if(oldVersion==DATABASE_VERSION_INIT&&newVersion>=DATABASE_VERSION_SECOND){
            db.execSQL(sqlDropTable);
            db.execSQL(sqlCreateTable);
        }
    }
}
