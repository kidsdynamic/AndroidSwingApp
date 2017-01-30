package com.kidsdynamic.swing.androidswingapp;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import java.util.ArrayList;
import java.util.List;

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
    public static String PARENT_ID = "PARENT_ID";

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
                    PROFILE + " TEXT)";

    public static final String CREATE_KIDS_TABLE =
            "CREATE TABLE " + TABLE_KIDS + " (" +
                    ID + " INTEGER NOT NULL, " +
                    FIRST_NAME + " TEXT NOT NULL, " +
                    LAST_NAME + " TEXT NOT NULL, " +
                    DATE_CREATED + " TEXT NOT NULL, " +
                    MAC_ID + " TEXT NOT NULL, " +
                    PARENT_ID + " INTEGER NOT NULL, " +
                    PROFILE + " TEXT)";

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

    public long UserAdd(WatchContact.User user) {
        ContentValues contentValues = new ContentValues();

        contentValues.put(ID, user.mId);
        contentValues.put(EMAIL, user.mEmail);
        contentValues.put(FIRST_NAME, user.mFirstName);
        contentValues.put(LAST_NAME, user.mLastName);
        contentValues.put(LAST_UPDATE, user.mLastUpdate);
        contentValues.put(DATE_CREATED, user.mDateCreated);
        contentValues.put(ZIP_CODE, user.mZipCode);
        contentValues.put(PHONE_NUMBER, user.mPhoneNumber);
        contentValues.put(PROFILE, user.mProfile);

        return mDatabase.insert(TABLE_USER, null, contentValues);
    }

    public long UserUpdate(WatchContact.User user) {
        ContentValues contentValues = new ContentValues();

        contentValues.put(ID, user.mId);
        contentValues.put(EMAIL, user.mEmail);
        contentValues.put(FIRST_NAME, user.mFirstName);
        contentValues.put(LAST_NAME, user.mLastName);
        contentValues.put(LAST_UPDATE, user.mLastUpdate);
        contentValues.put(DATE_CREATED, user.mDateCreated);
        contentValues.put(ZIP_CODE, user.mZipCode);
        contentValues.put(PHONE_NUMBER, user.mPhoneNumber);
        contentValues.put(PROFILE, user.mProfile);

        return mDatabase.update(TABLE_USER, contentValues, ID + "=" + user.mId, null);
    }

    public WatchContact.User UserGet() {
        WatchContact.User user = null;
        Cursor cursor = mDatabase.rawQuery("SELECT * FROM " + TABLE_USER + " LIMIT 1", null);

        if (cursor.moveToNext())
            user = cursorToUser(cursor);

        cursor.close();

        return user;
    }

    private WatchContact.User cursorToUser(Cursor cursor) {
        WatchContact.User item = new WatchContact.User();

        item.mId = cursor.getInt(0);
        item.mEmail = cursor.getString(1);
        item.mFirstName = cursor.getString(2);
        item.mLastName = cursor.getString(3);
        item.mLastUpdate = cursor.getString(4);
        item.mDateCreated = cursor.getString(5);
        item.mZipCode = cursor.getString(6);
        item.mPhoneNumber = cursor.getString(7);
        item.mProfile = cursor.getString(8);

        return item;
    }
    public long KidAdd(WatchContact.Kid kid) {
        ContentValues contentValues = new ContentValues();

        contentValues.put(ID, kid.mId);
        contentValues.put(FIRST_NAME, kid.mFirstName);
        contentValues.put(LAST_NAME, kid.mLastName);
        contentValues.put(DATE_CREATED, kid.mDateCreated);
        contentValues.put(MAC_ID, kid.mMacId);
        contentValues.put(PARENT_ID, kid.mParentId);
        contentValues.put(PROFILE, kid.mProfile);

        return mDatabase.insert(TABLE_KIDS, null, contentValues);
    }

    public long KidUpdate(WatchContact.Kid kid) {
        ContentValues contentValues = new ContentValues();

        contentValues.put(ID, kid.mId);
        contentValues.put(FIRST_NAME, kid.mFirstName);
        contentValues.put(LAST_NAME, kid.mLastName);
        contentValues.put(DATE_CREATED, kid.mDateCreated);
        contentValues.put(MAC_ID, kid.mMacId);
        contentValues.put(PARENT_ID, kid.mParentId);
        contentValues.put(PROFILE, kid.mProfile);

        return mDatabase.update(TABLE_KIDS, contentValues, ID + "=" + kid.mId + " AND " + PARENT_ID + "=" + kid.mParentId, null);
    }

    public List<WatchContact.Kid> KidGet() {
        List<WatchContact.Kid> result = new ArrayList<>();
        Cursor cursor = mDatabase.rawQuery("SELECT * FROM " + TABLE_KIDS, null);

        while (cursor.moveToNext())
            result.add(cursorToKid(cursor));

        cursor.close();

        return result;
    }

    private WatchContact.Kid cursorToKid(Cursor cursor) {
        WatchContact.Kid item = new WatchContact.Kid();

        item.mId = cursor.getInt(0);
        item.mFirstName = cursor.getString(1);
        item.mLastName = cursor.getString(2);
        item.mDateCreated = cursor.getString(3);
        item.mMacId = cursor.getString(4);
        item.mParentId = cursor.getInt(5);
        item.mProfile = cursor.getString(6);

        return item;
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
