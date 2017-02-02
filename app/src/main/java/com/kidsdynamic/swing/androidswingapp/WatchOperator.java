package com.kidsdynamic.swing.androidswingapp;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by 03543 on 2017/1/23.
 */

public class WatchOperator {
    public static final String TABLE_USER = "User";
    public static final String TABLE_KIDS = "Kids";
    public static final String TABLE_UPLOAD = "Upload";

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

    public static String TIME = "TIME";
    public static String INDOOR_ACTIVITY = "INDOOR_ACTIVITY";
    public static String OUTDOOR_ACTIVITY = "OUTDOOR_ACTIVITY";

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

    public static final String CREATE_UPLOAD_TABLE =
            "CREATE TABLE " + TABLE_UPLOAD + " (" +
                    TIME + " INTEGER NOT NULL, " +
                    MAC_ID + " TEXT NOT NULL, " +
                    INDOOR_ACTIVITY + " TEXT NOT NULL, " +
                    OUTDOOR_ACTIVITY + " TEXT NOT NULL)";

    private SQLiteDatabase mDatabase;
    private Context mContext;

    public ArrayList<WatchContact.Kid> mListDevice;
    public ArrayList<WatchContact.Kid> mListShared;
    public ArrayList<WatchContact.User> mListRequest;

    public WatchOperator(Context context) {
        mContext = context;

        mListDevice = new ArrayList<>();
        mListShared = new ArrayList<>();
        mListRequest = new ArrayList<>();

        mDatabase = WatchHelper.getDatabase(mContext);
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

    public long UploadItemAdd(UploadItem item) {
        ContentValues contentValues = new ContentValues();

        contentValues.put(TIME, item.mTime);
        contentValues.put(MAC_ID, item.mMacId);
        contentValues.put(INDOOR_ACTIVITY, item.mIndoorActivity);
        contentValues.put(OUTDOOR_ACTIVITY, item.mOutdoorActivity);

        return mDatabase.insert(TABLE_UPLOAD, null, contentValues);
    }

    public int UploadItemDelete(UploadItem item) {
        return mDatabase.delete(TABLE_UPLOAD, TIME + "=" + item.mTime + " AND " + MAC_ID + "='" + item.mMacId + "'", null);
    }

    public UploadItem UploadItemGet() {
        UploadItem item = null;
        Cursor cursor = mDatabase.rawQuery("SELECT * FROM " + TABLE_UPLOAD + " LIMIT 1", null);

        if (cursor.moveToNext())
            item = cursorToUploadItem(cursor);

        cursor.close();

        return item;
    }

    public int UploadItemCount() {
        int result = 0;
        Cursor cursor = mDatabase.rawQuery("SELECT COUNT(*) FROM " + TABLE_UPLOAD, null);

        if (cursor.moveToNext())
            result = cursor.getInt(0);

        cursor.close();

        return result;
    }

    public static class UploadItem {
        String mIndoorActivity;
        String mOutdoorActivity;
        int mTime;
        String mMacId;

        UploadItem() {
            mIndoorActivity = "";
            mOutdoorActivity = "";
            mTime = 0;
            mMacId = "";
        }
    }

    private UploadItem cursorToUploadItem(Cursor cursor) {
        UploadItem item = new UploadItem();

        item.mTime = cursor.getInt(0);
        item.mMacId = cursor.getString(1);
        item.mIndoorActivity = cursor.getString(2);
        item.mOutdoorActivity = cursor.getString(3);

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
