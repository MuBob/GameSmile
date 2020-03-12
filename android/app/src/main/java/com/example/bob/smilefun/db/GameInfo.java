package com.example.bob.smilefun.db;

import android.content.ContentResolver;
import android.content.ContentValues;
import android.database.Cursor;
import android.database.DatabaseUtils;
import android.net.Uri;
import android.os.Parcel;
import android.os.Parcelable;
import android.support.annotation.IntDef;
import android.support.annotation.NonNull;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

public class GameInfo implements Parcelable, Comparable<GameInfo>{
    public static final String TABLE_NAME="info";
    public static final String COL_ID="_id";
    public static final String COL_LEVEL="level";
    public static final String COL_TIME_START="time_start";
    public static final String COL_TIME_END="time_end";
    public static final String COL_STATE="state";
    public static final String COL_DIFFICULT_LINES="difficult_lines";
    public static final String COL_DIFFICULT_ROWS="difficult_rows";
    public final static Uri URI_INFO = Uri.parse(ContentResolver.SCHEME_CONTENT + "://" + DBProvider.AUTHORITY + "/" + TABLE_NAME);
    public static final int STATE_START=0;
    public static final int STATE_RUNING=1;
    public static final int STATE_END=2;

    public GameInfo() {
        clear();
    }

    public GameInfo(Cursor cursor) {
        if(cursor!=null){
            ContentValues args = new ContentValues();
            DatabaseUtils.cursorRowToContentValues(cursor, args);
            createFromCV(args);
        }
    }

    protected GameInfo(Parcel in) {
        id = in.readLong();
        level = in.readInt();
        startTime = in.readLong();
        levelTime = in.readLong();
        endTime = in.readLong();
        state = in.readInt();
        difficultLine=in.readInt();
        difficultRow=in.readInt();
    }

    public static final Creator<GameInfo> CREATOR = new Creator<GameInfo>() {
        @Override
        public GameInfo createFromParcel(Parcel in) {
            return new GameInfo(in);
        }

        @Override
        public GameInfo[] newArray(int size) {
            return new GameInfo[size];
        }
    };

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeLong(id);
        dest.writeInt(level);
        dest.writeLong(startTime);
        dest.writeLong(levelTime);
        dest.writeLong(endTime);
        dest.writeInt(state);
        dest.writeInt(difficultLine);
        dest.writeInt(difficultRow);
    }

    @Override
    public int compareTo(@NonNull GameInfo o) {
        return  id>o.id?1:-1;
    }

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
    private int difficultLine;
    private int difficultRow;

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

    public int getDifficultLine() {
        return difficultLine;
    }

    public void setDifficult(int difficultLine, int difficultRow) {
        this.difficultLine = difficultLine;
        this.difficultRow = difficultRow;
    }

    public int getDifficultRow() {
        return difficultRow;
    }

    public void clear(){
        setId(-1);
        setLevel(0);
        setStartTime(0);
        setEndTime(0);
        setState(STATE_START);
        setLevelTime(0);
        setDifficult(0,0);
    }

    public ContentValues getCV(){
      ContentValues cv=new ContentValues();
      cv.put(COL_LEVEL, level);
      cv.put(COL_TIME_START, startTime);
      cv.put(COL_TIME_END, endTime);
      cv.put(COL_STATE, state);
      cv.put(COL_DIFFICULT_LINES, difficultLine);
      cv.put(COL_DIFFICULT_ROWS, difficultRow);
      return cv;
    }

    public void createFromCV(ContentValues args) {
        Integer tmp_i;
        Long tmp_l;
        tmp_l=args.getAsLong(COL_ID);
        if(tmp_l!=null){
            id=tmp_l;
        }
        tmp_i=args.getAsInteger(COL_LEVEL);
        if(tmp_i!=null){
            level=tmp_i;
        }
        tmp_l=args.getAsLong(COL_TIME_START);
        if(tmp_l!=null){
            startTime=tmp_l;
        }
        tmp_l=args.getAsLong(COL_TIME_END);
        if(tmp_l!=null){
            endTime=tmp_l;
        }
        tmp_i=args.getAsInteger(COL_STATE);
        if(tmp_i!=null){
            state=tmp_i;
        }
        tmp_i=args.getAsInteger(COL_DIFFICULT_LINES);
        if(tmp_i!=null){
            difficultLine=tmp_i;
        }
        tmp_i=args.getAsInteger(COL_DIFFICULT_ROWS);
        if(tmp_i!=null){
            difficultRow=tmp_i;
        }

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
                ", difficultLine=" + difficultLine +
                ", difficultRow=" + difficultRow +
                '}';
    }
}
