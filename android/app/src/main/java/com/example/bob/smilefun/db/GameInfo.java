package com.example.bob.smilefun.db;

import android.content.ContentResolver;
import android.content.ContentValues;
import android.net.Uri;
import android.support.annotation.IntDef;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

public class GameInfo {
    public static final String TABLE_NAME="info";
    public static final String COL_ID="_id";
    public static final String COL_LEVEL="level";
    public static final String COL_TIME_START="time_start";
    public static final String COL_TIME_END="time_end";
    public static final String COL_STATE="state";
    public final static Uri URI_INFO = Uri.parse(ContentResolver.SCHEME_CONTENT + "://" + DBProvider.AUTHORITY + "/" + TABLE_NAME);
    public static final int STATE_START=0;
    public static final int STATE_RUNING=1;
    public static final int STATE_END=2;
    @IntDef({STATE_START, STATE_RUNING, STATE_END})
    @Retention(RetentionPolicy.SOURCE)
    public @interface STATE {}
    private long id;
    private int level;
    private long startTime;
    private long levelTime;
    private long endTime;
    @STATE
    private int state;

    public GameInfo() {
        clear();
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public int getLevel() {
        return level;
    }

    public void setLevel(int level) {
        this.level = level;
    }

    public long getStartTime() {
        return startTime;
    }

    public void setStartTime(long startTime) {
        this.startTime = startTime;
    }

    public long getEndTime() {
        return endTime;
    }

    public void setEndTime(long endTime) {
        this.endTime = endTime;
    }

    public long getLevelTime() {
        return levelTime;
    }

    public void setLevelTime(long levelTime) {
        this.levelTime = levelTime;
    }

    @STATE
    public int getState() {
        return state;
    }

    public void setState(@STATE int state) {
        this.state = state;
    }

    public void clear(){
        id=-1;
        level=0;
        startTime=0;
        endTime=0;
        state=STATE_START;
        levelTime=0;
    }

    public ContentValues getCV(){
      ContentValues cv=new ContentValues();
      cv.put(COL_LEVEL, level);
      cv.put(COL_TIME_START, startTime);
      cv.put(COL_TIME_END, endTime);
      cv.put(COL_STATE, state);
      return cv;
    }

    @Override
    public String toString() {
        return "GameInfo{" +
                "id=" + id +
                ", level=" + level +
                ", startTime=" + startTime +
                ", levelTime=" + levelTime +
                ", endTime=" + endTime +
                ", state=" + state +
                '}'+super.toString();
    }
}
