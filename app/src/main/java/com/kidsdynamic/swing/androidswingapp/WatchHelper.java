package com.kidsdynamic.swing.androidswingapp;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * Created by weichigio on 2017/1/30.
 */

public class WatchHelper extends SQLiteOpenHelper {
    private static final String DB_NAME = "Swing.db";
    private static final int DB_VERSION = 1;

    private static SQLiteDatabase mDatabase;

    public WatchHelper(Context context, String name, SQLiteDatabase.CursorFactory factory, int version) {
        super(context, name, factory, version);
    }

    public static SQLiteDatabase getDatabase(Context context) {
        if (mDatabase == null || !mDatabase.isOpen()) {
            mDatabase = new WatchHelper(context, DB_NAME, null, DB_VERSION).getWritableDatabase();
        }

        return mDatabase;
    }

    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {
        //mDatabase.execSQL(WatchOperator.CREATE_USER_TABLE);
        //mDatabase.execSQL(WatchOperator.CREATE_KIDS_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {
        //mDatabase.execSQL("DROP TABLE IF EXISTS " + WatchOperator.CREATE_USER_TABLE);
        //mDatabase.execSQL("DROP TABLE IF EXISTS " + WatchOperator.CREATE_KIDS_TABLE);
        //onCreate(mDatabase);
    }
}
