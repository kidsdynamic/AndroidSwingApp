package com.kidsdynamic.swing.androidswingapp;

import android.content.ContentValues;
import android.content.Context;
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
    public static final String TABLE_UPLOAD = "Upload";
    public static final String TABLE_EVENT = "Event";
    public static final String TABLE_TODO = "Todo";

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
    public static String USER_ID = "USER_ID";
    public static String KID_ID = "KID_ID";
    public static String NAME = "NAME";
    public static String START_DATE = "START_DATE";
    public static String END_DATE = "END_DATE";
    public static String COLOR = "COLOR";
    public static String DESCRIPTION = "DESCRIPTION";
    public static String ALERT = "ALERT";
    public static String CITY = "CITY";
    public static String STATE = "STATE";
    public static String REPEAT = "REPEAT";
    public static String TIMEZONE_OFFSET = "TIMEZONE_OFFSET";
    public static String EVENT_ID = "EVENT_ID";
    public static String TEXT = "TEXT";
    public static String STATUS = "STATUS";
    public static String FOCUS_ID = "FOCUS_ID";
    public static String FOCUS_PID = "FOCUS_PID";


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
                    PROFILE + " TEXT, " +
                    FOCUS_ID + " INTEGER, " +
                    FOCUS_PID + " INTEGER)";

    public static final String CREATE_KIDS_TABLE =
            "CREATE TABLE " + TABLE_KIDS + " (" +
                    ID + " INTEGER NOT NULL, " +
                    FIRST_NAME + " TEXT NOT NULL, " +
                    LAST_NAME + " TEXT NOT NULL, " +
                    DATE_CREATED + " TEXT NOT NULL, " +
                    MAC_ID + " TEXT NOT NULL, " +
                    USER_ID + " INTEGER NOT NULL, " +
                    PROFILE + " TEXT)";

    public static final String CREATE_UPLOAD_TABLE =
            "CREATE TABLE " + TABLE_UPLOAD + " (" +
                    TIME + " INTEGER NOT NULL, " +
                    MAC_ID + " TEXT NOT NULL, " +
                    INDOOR_ACTIVITY + " TEXT NOT NULL, " +
                    OUTDOOR_ACTIVITY + " TEXT NOT NULL)";

    public static final String CREATE_EVENT_TABLE =
            "CREATE TABLE " + TABLE_EVENT + " (" +
                    ID + " INTEGER NOT NULL, " +
                    USER_ID + " INTEGER NOT NULL, " +
                    KID_ID + " INTEGER NOT NULL, " +
                    NAME + " TEXT NOT NULL, " +
                    START_DATE + " TEXT NOT NULL, " +
                    END_DATE + " TEXT NOT NULL, " +
                    COLOR + " TEXT NOT NULL, " +
                    STATUS + " TEXT NOT NULL, " +
                    DESCRIPTION + " TEXT NOT NULL, " +
                    ALERT + " INTEGER NOT NULL, " +
                    CITY + " TEXT NOT NULL, " +
                    STATE + " TEXT NOT NULL, " +
                    REPEAT + " TEXT NOT NULL, " +
                    TIMEZONE_OFFSET + " INTEGER NOT NULL, " +
                    DATE_CREATED + " TEXT NOT NULL, " +
                    LAST_UPDATE + " TEXT NOT NULL)";

    public static final String CREATE_TODO_TABLE =
            "CREATE TABLE " + TABLE_TODO + " (" +
                    ID + " INTEGER NOT NULL, " +
                    USER_ID + " INTEGER NOT NULL, " +
                    KID_ID + " INTEGER NOT NULL, " +
                    EVENT_ID + " INTEGER NOT NULL, " +
                    TEXT + " TEXT NOT NULL, " +
                    STATUS + " TEXT NOT NULL, " +
                    DATE_CREATED + " TEXT NOT NULL, " +
                    LAST_UPDATE + " TEXT NOT NULL)";

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
        contentValues.put(USER_ID, kid.mUserId);
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
        contentValues.put(USER_ID, kid.mUserId);
        contentValues.put(PROFILE, kid.mProfile);

        return mDatabase.update(TABLE_KIDS, contentValues, ID + "=" + kid.mId + " AND " + USER_ID + "=" + kid.mUserId, null);
    }

    public List<WatchContact.Kid> KidGet() {
        List<WatchContact.Kid> result = new ArrayList<>();
        Cursor cursor = mDatabase.rawQuery("SELECT * FROM " + TABLE_KIDS, null);

        while (cursor.moveToNext())
            result.add(cursorToKid(cursor));

        cursor.close();

        return result;
    }

    public void KidSetFocus(WatchContact.Kid kid) {
        Cursor cursor = mDatabase.rawQuery("SELECT * FROM " + TABLE_KIDS + " WHERE " + ID + "=" + kid.mId + " AND " + USER_ID + "=" + kid.mUserId, null);

        if (cursor.moveToNext())
            KidUpdate(kid);
        else
            KidAdd(kid);
        cursor.close();

        ContentValues contentValues = new ContentValues();
        WatchContact.User user = UserGet();
        contentValues.put(FOCUS_ID, kid.mId);
        contentValues.put(FOCUS_PID, kid.mUserId);
        mDatabase.update(TABLE_USER, contentValues, ID + "=" + user.mId, null);
    }

    public WatchContact.Kid KidGetFocus() {
        int id = 0;
        int pid = 0;
        Cursor cursor = mDatabase.rawQuery("SELECT " + FOCUS_ID + "," + FOCUS_PID +" FROM " + TABLE_USER + " LIMIT 1", null);
        if (cursor.moveToNext()) {
            id = cursor.getInt(0);
            pid = cursor.getInt(1);
        }
        cursor.close();

        WatchContact.Kid kid = null;
        cursor = mDatabase.rawQuery("SELECT * FROM " + TABLE_KIDS + " WHERE " + ID + "=" + id + " AND " + USER_ID + "=" + pid + " LIMIT 1", null);
        if (cursor.moveToNext()) {
            kid = cursorToKid(cursor);
        }
        cursor.close();

        return kid;
    }

    private WatchContact.Kid cursorToKid(Cursor cursor) {
        WatchContact.Kid item = new WatchContact.Kid();

        item.mId = cursor.getInt(0);
        item.mFirstName = cursor.getString(1);
        item.mLastName = cursor.getString(2);
        item.mDateCreated = cursor.getString(3);
        item.mMacId = cursor.getString(4);
        item.mUserId = cursor.getInt(5);
        item.mProfile = cursor.getString(6);

        return item;
    }

    public long UploadItemAdd(Upload item) {
        ContentValues contentValues = new ContentValues();

        contentValues.put(TIME, item.mTime);
        contentValues.put(MAC_ID, item.mMacId);
        contentValues.put(INDOOR_ACTIVITY, item.mIndoorActivity);
        contentValues.put(OUTDOOR_ACTIVITY, item.mOutdoorActivity);

        return mDatabase.insert(TABLE_UPLOAD, null, contentValues);
    }

    public int UploadItemDelete(Upload item) {
        return mDatabase.delete(TABLE_UPLOAD, TIME + "=" + item.mTime + " AND " + MAC_ID + "='" + item.mMacId + "'", null);
    }

    public Upload UploadItemGet() {
        Upload item = null;
        Cursor cursor = mDatabase.rawQuery("SELECT * FROM " + TABLE_UPLOAD + " LIMIT 1", null);

        if (cursor.moveToNext())
            item = cursorToUpload(cursor);

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

    public static class Upload {
        String mIndoorActivity;
        String mOutdoorActivity;
        int mTime;
        String mMacId;

        Upload() {
            mIndoorActivity = "";
            mOutdoorActivity = "";
            mTime = 0;
            mMacId = "";
        }
    }

    private Upload cursorToUpload(Cursor cursor) {
        Upload item = new Upload();

        item.mTime = cursor.getInt(0);
        item.mMacId = cursor.getString(1);
        item.mIndoorActivity = cursor.getString(2);
        item.mOutdoorActivity = cursor.getString(3);

        return item;
    }

    public long EventAdd(Event event) {
        ContentValues contentValues = new ContentValues();

        contentValues.put(ID, event.mId);
        contentValues.put(USER_ID, event.mUserId);
        contentValues.put(KID_ID, event.mKidId);
        contentValues.put(NAME, event.mName);
        contentValues.put(START_DATE, event.mStartDate);
        contentValues.put(END_DATE, event.mEndDate);
        contentValues.put(COLOR, event.mColor);
        contentValues.put(STATUS, event.mStatus);
        contentValues.put(DESCRIPTION, event.mDescription);
        contentValues.put(ALERT, event.mAlert);
        contentValues.put(CITY, event.mCity);
        contentValues.put(STATE, event.mState);
        contentValues.put(REPEAT, event.mRepeat);
        contentValues.put(TIMEZONE_OFFSET, event.mTimezoneOffset);
        contentValues.put(DATE_CREATED, event.mDateCreated);
        contentValues.put(LAST_UPDATE, event.mLastUpdated);

        return mDatabase.insert(TABLE_EVENT, null, contentValues);
    }

    public long EventUpdate(Event event) {
        ContentValues contentValues = new ContentValues();

        contentValues.put(ID, event.mId);
        contentValues.put(USER_ID, event.mUserId);
        contentValues.put(KID_ID, event.mKidId);
        contentValues.put(NAME, event.mName);
        contentValues.put(START_DATE, event.mStartDate);
        contentValues.put(END_DATE, event.mEndDate);
        contentValues.put(COLOR, event.mColor);
        contentValues.put(STATUS, event.mStatus);
        contentValues.put(DESCRIPTION, event.mDescription);
        contentValues.put(ALERT, event.mAlert);
        contentValues.put(CITY, event.mCity);
        contentValues.put(STATE, event.mState);
        contentValues.put(REPEAT, event.mRepeat);
        contentValues.put(TIMEZONE_OFFSET, event.mTimezoneOffset);
        contentValues.put(DATE_CREATED, event.mDateCreated);
        contentValues.put(LAST_UPDATE, event.mLastUpdated);

        return mDatabase.update(TABLE_EVENT, contentValues, ID + "=" + event.mId + " AND " + USER_ID + "=" + event.mUserId + " AND " + KID_ID + "=" + event.mKidId, null);
    }

    public List<Event> EventGet() {
        List<Event> result = new ArrayList<>();
        Cursor cursor = mDatabase.rawQuery("SELECT * FROM " + TABLE_EVENT, null);

        while (cursor.moveToNext())
            result.add(cursorToEvent(cursor));

        cursor.close();

        return result;
    }

    public static class Event {
        int mId;
        int mUserId;
        int mKidId;
        String mName;
        String mStartDate;
        String mEndDate;
        String mColor;
        String mStatus;
        String mDescription;
        int mAlert;
        String mCity;
        String mState;
        String mRepeat;
        int mTimezoneOffset;
        String mDateCreated;
        String mLastUpdated;
        Event(int id, int userId, int kidId, String name, String startDate,
              String endDate, String color, String status, String description,
              int alert, String city, String state, String repeat,
              int timezoneOffset, String dateCreated, String lastUpdated) {
            mId = id;
            mUserId = userId;
            mKidId = kidId;
            mName = name;
            mStartDate = startDate;
            mEndDate = endDate;
            mColor = color;
            mStatus = status;
            mDescription = description;
            mAlert = alert;
            mCity = city;
            mState = state;
            mRepeat = repeat;
            mTimezoneOffset = timezoneOffset;
            mDateCreated = dateCreated;
            mLastUpdated = lastUpdated;
        }
    }

    private Event cursorToEvent(Cursor cursor) {
        return new Event(
                cursor.getInt(0),
                cursor.getInt(1),
                cursor.getInt(2),
                cursor.getString(3),
                cursor.getString(4),
                cursor.getString(5),
                cursor.getString(6),
                cursor.getString(7),
                cursor.getString(8),
                cursor.getInt(9),
                cursor.getString(10),
                cursor.getString(11),
                cursor.getString(12),
                cursor.getInt(13),
                cursor.getString(14),
                cursor.getString(15)
        );
    }

    public long TodoAdd(Todo todo) {
        ContentValues contentValues = new ContentValues();

        contentValues.put(ID, todo.mId);
        contentValues.put(USER_ID, todo.mUserId);
        contentValues.put(KID_ID, todo.mKidId);
        contentValues.put(EVENT_ID, todo.mEventId);
        contentValues.put(NAME, todo.mText);
        contentValues.put(START_DATE, todo.mStatus);
        contentValues.put(END_DATE, todo.mDateCreated);
        contentValues.put(COLOR, todo.mLastUpdated);

        return mDatabase.insert(TABLE_TODO, null, contentValues);
    }

    public long TodoUpdate(Todo todo) {
        ContentValues contentValues = new ContentValues();

        contentValues.put(ID, todo.mId);
        contentValues.put(USER_ID, todo.mUserId);
        contentValues.put(KID_ID, todo.mKidId);
        contentValues.put(EVENT_ID, todo.mEventId);
        contentValues.put(NAME, todo.mText);
        contentValues.put(START_DATE, todo.mStatus);
        contentValues.put(END_DATE, todo.mDateCreated);
        contentValues.put(COLOR, todo.mLastUpdated);

        return mDatabase.update(TABLE_TODO, contentValues, ID + "=" + todo.mId + " AND " + USER_ID + "=" + todo.mUserId + " AND " + KID_ID + "=" + todo.mKidId + " AND " + EVENT_ID + "=" + todo.mEventId, null);
    }

    public List<Todo> TodoGet() {
        List<Todo> result = new ArrayList<>();
        Cursor cursor = mDatabase.rawQuery("SELECT * FROM " + TABLE_TODO, null);

        while (cursor.moveToNext())
            result.add(cursorToTodo(cursor));

        cursor.close();

        return result;
    }

    public static class Todo {
        int mId;
        int mUserId;
        int mKidId;
        int mEventId;
        String mText;
        String mStatus;
        String mDateCreated;
        String mLastUpdated;

        Todo(int id, int userId, int kidId, int eventId, String text, String status,
             String dateCreated, String lastUpdated) {
            mId = id;
            mUserId = userId;
            mKidId = kidId;
            mEventId = eventId;
            mText = text;
            mStatus = status;
            mDateCreated = dateCreated;
            mLastUpdated = lastUpdated;
        }
    }

    private Todo cursorToTodo(Cursor cursor) {
        return new Todo(
                cursor.getInt(0),
                cursor.getInt(1),
                cursor.getInt(2),
                cursor.getInt(3),
                cursor.getString(4),
                cursor.getString(5),
                cursor.getString(6),
                cursor.getString(7)
        );
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
