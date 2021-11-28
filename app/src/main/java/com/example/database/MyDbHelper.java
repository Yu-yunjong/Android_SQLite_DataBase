package com.example.database;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

public class MyDbHelper extends SQLiteOpenHelper {
    private static final String TAG = "MyDbHelper";

    public static final int DATABASE_VERSION = 2;
    public static final String DATABASE_NAME = "ClassGroup.db";

    // Create문
    private static final String SQL_CREATE_TABLE =
            "CREATE TABLE " + TableInfo.TABLE_NAME + " (" +
                    TableInfo.COLUMN_NAME_ID + " INTEGER PRIMARY KEY, " +
                    TableInfo.COLUMN_NAME_NAME + " TEXT, " +
                    TableInfo.COLUMN_NAME_AGE + " INTEGER, " +
                    TableInfo.COLUMN_NAME_DEP + " TEXT)";

    private static final String SQL_DELETE_TABLE =
            "DROP TABLE IF EXISTS " + TableInfo.TABLE_NAME;

    // 생성자
    public MyDbHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        Log.i(TAG, "*** onCreate");
        db.execSQL(SQL_CREATE_TABLE); // 우리가 만든 SQL문 넣기
    }

    // 여기에 있는 데이터를 보존하려면 마이그레이션을 수행해야 함.
    // SQLite Migration에 관심 있다면, 찾아보길
    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        Log.i(TAG, "*** onUpgrade");
        db.execSQL(SQL_DELETE_TABLE); // 우리가 만든 SQL문 넣기
        onCreate(db);
    }
}
