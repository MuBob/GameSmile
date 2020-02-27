package com.example.bob.smilefun.db;

import android.content.ContentValues;
import android.net.Uri;

public class GameSetting {
    public static final int COUNT_LINE=3;
    public static final int COUNT_COLUMN=3;
    private int lineCount=COUNT_LINE;
    private int columnCount=COUNT_COLUMN;
    public static final Uri uri=Uri.parse("");
    public static final String NAME = "setting";
    public static final String NUM_LINE= "num_line";
    public static final String NUM_COLUMN= "num_column";

    public int getLineCount() {
        return lineCount;
    }

    public void setLineCount(int lineCount) {
        this.lineCount = lineCount;
    }

    public int getColumnCount() {
        return columnCount;
    }

    public void setColumnCount(int columnCount) {
        this.columnCount = columnCount;
    }

    @Override
    public String toString() {
        return "GameSetting{" +
                "lineCount=" + lineCount +
                ", columnCount=" + columnCount +
                '}';
    }
    public ContentValues getCV(){
        ContentValues cv=new ContentValues();
        cv.put(NUM_LINE, lineCount);
        cv.put(NUM_COLUMN, columnCount);
        return cv;
    }
}
