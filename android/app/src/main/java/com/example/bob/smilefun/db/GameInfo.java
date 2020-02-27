package com.example.bob.smilefun.db;

public class GameInfo {
    private int level;
    private long startTime;
    private long endTime;

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

    @Override
    public String toString() {
        return "GameInfo{" +
                "level=" + level +
                ", startTime=" + startTime +
                ", endTime=" + endTime +
                '}';
    }


}
