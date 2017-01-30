package com.kidsdynamic.swing.androidswingapp;

import android.database.sqlite.SQLiteDatabase;

import java.util.ArrayList;

/**
 * Created by 03543 on 2017/1/23.
 */

public class WatchOperator {
    public static final String TABLE_USER = "User";
    public static final String TABLE_KIDS = "Kids";

    public static String ID = "ID";
    public static String EMAIL = "EMAIL";
    public static String FIRST_NAME = "FIRST_NAME";
    public static String LAST_NAME = "LAST_NAME";
    public static String LAST_UPDATE = "LAST_UPDATE";
    public static String DATE_CREATED = "DATE_CREATED";
    public static String ZIP_CODE = "ZIP_CODE";
    public static String PHONE_NUMBER = "PHONE_NUMBER";
    public static String PROFILE = "PROFILE";
    public static String MAC_ID = "MAC_ID";

    public static final String CREATE_USER_TABLE =
            "CREATE TABLE " + TABLE_USER + " (" +
                    ID + " INTEGER, " +
                    EMAIL + " TEXT NOT NULL, " +
                    FIRST_NAME + " TEXT NOT NULL, " +
                    LAST_NAME + " TEXT NOT NULL, " +
                    LAST_UPDATE + " TEXT, " +
                    DATE_CREATED + " TEXT, " +
                    ZIP_CODE + " TEXT NOT NULL, " +
                    PHONE_NUMBER + " TEXT NOT NULL, " +
                    PROFILE + " TEXT NOT NULL)";

    public static final String CREATE_KIDS_TABLE =
            "CREATE TABLE " + TABLE_KIDS + " (" +
                    ID + " INTEGER NOT NULL, " +
                    FIRST_NAME + " TEXT NOT NULL, " +
                    LAST_NAME + " TEXT NOT NULL, " +
                    DATE_CREATED + " TEXT NOT NULL, " +
                    PROFILE + " TEXT NOT NULL)";

    private SQLiteDatabase mDatabase;
    private ActivityMain mActivityMain;

    public ArrayList<WatchContact.Kid> mListDevice;
    public ArrayList<WatchContact.Kid> mListShared;
    public ArrayList<WatchContact.User> mListRequest;

    public WatchOperator(ActivityMain activity) {
        mActivityMain = activity;

        mListDevice = new ArrayList<>();
        mListShared = new ArrayList<>();
        mListRequest = new ArrayList<>();

        mDatabase = WatchHelper.getDatabase(mActivityMain);
    }

    public ArrayList<WatchContact.Kid> getDeviceList() {
        return mListDevice;
    }

    public ArrayList<WatchContact.Kid> getSharedList() {
        return mListShared;
    }

    public ArrayList<WatchContact.User> getRequestList() {
        return mListRequest;
    }
}
