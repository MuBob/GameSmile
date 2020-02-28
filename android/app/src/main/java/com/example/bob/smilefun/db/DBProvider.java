package com.example.bob.smilefun.db;

import android.content.ContentProvider;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

public class DBProvider extends ContentProvider {

    private SQLiteDatabase mDb;
    private UriMatcher matcher;
    public final static String AUTHORITY="com.example.bob.smilefun.db.DBProvider.local";
    private final int URI_CODE_INFO=1;

    private UriMatcher initMatcherByTableNames(){
        UriMatcher matcher = new UriMatcher(UriMatcher.NO_MATCH);
        matcher.addURI(AUTHORITY, GameInfo.TABLE_NAME, URI_CODE_INFO);
        return matcher;
    }

    private String getTableNameByURI(@NonNull Uri uri){
        int match = matcher.match(uri);
        String tableName=null;
        switch (match){
            case URI_CODE_INFO:
                tableName=GameInfo.TABLE_NAME;
                break;
            default:
                break;
        }
        return tableName;
    }

    @Override
    public boolean onCreate() {
        DatabaseHelper imDb=new DatabaseHelper(getContext());
        mDb = imDb.getWritableDatabase();
        matcher = initMatcherByTableNames();
        return true;
    }

    @Nullable
    @Override
    public Cursor query( @NonNull Uri uri,  @Nullable String[] projection,  @Nullable String selection,  @Nullable String[] selectionArgs,  @Nullable String sortOrder) {
        Cursor query=null;
        String tableName = getTableNameByURI(uri);
        if(tableName!=null){
            try {
                query=mDb.query(tableName, projection, selection, selectionArgs, null, null, sortOrder);
            }catch (Exception e){
                e.printStackTrace();
            }
        }
        return query;    }

    @Nullable
    @Override
    public String getType( @NonNull Uri uri) {
        return null;
    }

    
    @Nullable
    @Override
    public Uri insert( @NonNull Uri uri,  @Nullable ContentValues values) {
        String tableName = getTableNameByURI(uri);
        long insert=-1;
        if(tableName!=null){
            insert = mDb.insert(tableName, null, values);
        }
        uri=ContentUris.withAppendedId(uri, insert);
        return uri;
    }

    @Override
    public int delete( @NonNull Uri uri,  @Nullable String selection,  @Nullable String[] selectionArgs) {
        String tableName = getTableNameByURI(uri);
        int delete=-1;
        if(tableName!=null){
            delete= mDb.delete(tableName, selection, selectionArgs);
        }
        return delete;
    }

    @Override
    public int update( @NonNull Uri uri,  @Nullable ContentValues values,  @Nullable String selection,  @Nullable String[] selectionArgs) {
        int update=-1;
        String tableName = getTableNameByURI(uri);
        if(tableName!=null){
            update=mDb.update(tableName, values, selection, selectionArgs);
        }
        return update;
    }
}
