package com.example.bob.smilefun.utils;

import android.content.Context;
import android.content.SharedPreferences;

public class SPUtil {

    private static final String SHARED_PREFERENCES = "SP";

//	private static volatile SPHelper sInstance = null;

    private SharedPreferences mSetting = null;

    private SharedPreferences.Editor mEditor = null;

    private SPUtil(Context context) {
        mSetting = context.getSharedPreferences(SHARED_PREFERENCES, Context.MODE_MULTI_PROCESS);
        mEditor = mSetting.edit();
    }

    public static SPUtil build(Context context) {
        return new SPUtil(context);
    }

    public void put(String key, boolean value) {
        mEditor.putBoolean(key, value).apply();
    }

    public void put(String key, float value) {
        mEditor.putFloat(key, value).apply();
    }

    public void put(String key, int value) {
        mEditor.putInt(key, value).apply();
    }

    public void put(String key, long value) {
        mEditor.putLong(key, value).apply();
    }

    public void put(String key, String value) {
        mEditor.putString(key, value).apply();
    }

    public boolean get(String key, boolean defValue) {
        return mSetting.getBoolean(key, defValue);
    }

    public float get(String key, float defValue) {
        return mSetting.getFloat(key, defValue);
    }

    public int get(String key, int defValue) {
        return mSetting.getInt(key, defValue);
    }

    public long get(String key, long defValue) {
        return mSetting.getLong(key, defValue);
    }

    public String get(String key, String defValue) {
        return mSetting.getString(key, defValue);
    }
}
