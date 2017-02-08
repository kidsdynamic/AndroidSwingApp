package com.kidsdynamic.swing.androidswingapp;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.TimeZone;

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
                    LAST_UPDATE + " INTEGER, " +
                    DATE_CREATED + " INTEGER, " +
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
                    DATE_CREATED + " INTEGER NOT NULL, " +
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
                    START_DATE + " INTEGER NOT NULL, " +
                    END_DATE + " INTEGER NOT NULL, " +
                    COLOR + " TEXT NOT NULL, " +
                    STATUS + " TEXT NOT NULL, " +
                    DESCRIPTION + " TEXT NOT NULL, " +
                    ALERT + " INTEGER NOT NULL, " +
                    CITY + " TEXT NOT NULL, " +
                    STATE + " TEXT NOT NULL, " +
                    REPEAT + " TEXT NOT NULL, " +
                    TIMEZONE_OFFSET + " INTEGER NOT NULL, " +
                    DATE_CREATED + " INTEGER NOT NULL, " +
                    LAST_UPDATE + " INTEGER NOT NULL)";

    public static final String CREATE_TODO_TABLE =
            "CREATE TABLE " + TABLE_TODO + " (" +
                    ID + " INTEGER NOT NULL, " +
                    USER_ID + " INTEGER NOT NULL, " +
                    KID_ID + " INTEGER NOT NULL, " +
                    EVENT_ID + " INTEGER NOT NULL, " +
                    TEXT + " TEXT NOT NULL, " +
                    STATUS + " TEXT NOT NULL, " +
                    DATE_CREATED + " INTEGER NOT NULL, " +
                    LAST_UPDATE + " INTEGER NOT NULL)";

    private SQLiteDatabase mDatabase;
    private Context mContext;

    public WatchOperator(Context context) {
        mContext = context;

        mDatabase = WatchHelper.getDatabase(mContext);
    }

    public void ResetDatabase() {
        mDatabase.execSQL("DROP TABLE IF EXISTS " + WatchOperator.TABLE_USER);
        mDatabase.execSQL("DROP TABLE IF EXISTS " + WatchOperator.TABLE_KIDS);
        mDatabase.execSQL("DROP TABLE IF EXISTS " + WatchOperator.TABLE_UPLOAD);
        mDatabase.execSQL("DROP TABLE IF EXISTS " + WatchOperator.TABLE_EVENT);
        mDatabase.execSQL("DROP TABLE IF EXISTS " + WatchOperator.TABLE_TODO);

        mDatabase.execSQL(WatchOperator.CREATE_USER_TABLE);
        mDatabase.execSQL(WatchOperator.CREATE_KIDS_TABLE);
        mDatabase.execSQL(WatchOperator.CREATE_UPLOAD_TABLE);
        mDatabase.execSQL(WatchOperator.CREATE_EVENT_TABLE);
        mDatabase.execSQL(WatchOperator.CREATE_TODO_TABLE);
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
        item.mLastUpdate = cursor.getLong(4);
        item.mDateCreated = cursor.getLong(5);
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
        item.mDateCreated = cursor.getLong(3);
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

    public void EventReset() {
        mDatabase.execSQL("DROP TABLE IF EXISTS " + WatchOperator.TABLE_EVENT);
        mDatabase.execSQL(WatchOperator.CREATE_EVENT_TABLE);
    }

    public long EventAdd(Event event) {
        long rtn;
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

        rtn = mDatabase.insert(TABLE_EVENT, null, contentValues);

        for (Todo todo : event.mTodoList) {
            todo.mEventId = event.mId;
            todo.mUserId = event.mUserId;
            todo.mKidId = event.mKidId;
            TodoAdd(todo);
        }

        return rtn;
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

    private List<Event>EventGetRepeat(int userId, int kidId, long startTimeStamp, long endTimeStamp, String repeat) {
        List<Event>repeatResult = new ArrayList<>();
        Cursor cursor = mDatabase.rawQuery("SELECT * FROM " + TABLE_EVENT +
                " WHERE " +
                USER_ID + "=" + userId + " AND " +
                KID_ID + "=" + kidId + " AND " +
                REPEAT + "='" + repeat + "'" + " AND " +
                endTimeStamp + ">=" + START_DATE, null);

        while (cursor.moveToNext())
            repeatResult.add(cursorToEvent(cursor));

        cursor.close();

        for (Event event : repeatResult)
            event.mTodoList = TodoGet(event.mId, event.mUserId, event.mKidId);

        List<Event> result = new ArrayList<>();
        Calendar cal = Calendar.getInstance();

        for(Event event : repeatResult) {
            cal.setTimeInMillis(event.mAlertTimeStamp);
            do {
                if (event.mAlertTimeStamp >= startTimeStamp && event.mAlertTimeStamp <= endTimeStamp) {
                    result.add(new Event(event));
                }
                switch(repeat) {
                    case "DAILY":
                        cal.add(Calendar.DATE, 1);
                        break;
                    case "WEEKLY":
                        cal.add(Calendar.DATE, 7);
                        break;
                    case "MONTHLY":
                        cal.add(Calendar.MONTH, 1);
                        break;
                }
                event.mAlertTimeStamp = cal.getTimeInMillis();
            } while(event.mAlertTimeStamp <= endTimeStamp);
        }

        return result;
    }

    public List<Event> EventGet(int userId, int kidId, long startTimeStamp, long endTimeStamp) {
        List<Event> result = new ArrayList<>();
        Cursor cursor = mDatabase.rawQuery("SELECT * FROM " + TABLE_EVENT +
                " WHERE (" + USER_ID + "=" + userId + " AND " + KID_ID + "=" + kidId + " AND " + REPEAT + "=''" + ") AND " +
                "((" + startTimeStamp + ">=" + START_DATE + " AND " + startTimeStamp + "<=" + END_DATE + ") OR" +
                " (" + startTimeStamp + "<=" + START_DATE + " AND " + endTimeStamp + ">=" + END_DATE + ") OR" +
                " (" + endTimeStamp + ">=" + START_DATE + " AND " + endTimeStamp + "<=" + END_DATE + "))", null);


        while (cursor.moveToNext())
            result.add(cursorToEvent(cursor));

        cursor.close();

        for (Event event : result)
            event.mTodoList = TodoGet(event.mId, event.mUserId, event.mKidId);

        List<Event>dailyResult = EventGetRepeat(userId, kidId, startTimeStamp, endTimeStamp, "DAILY");
        List<Event>weeklyResult = EventGetRepeat(userId, kidId, startTimeStamp, endTimeStamp, "WEEKLY");
        List<Event>monthlyResult = EventGetRepeat(userId, kidId, startTimeStamp, endTimeStamp, "MONTHLY");

        for (Event event : dailyResult)
            result.add(event);

        for (Event event : weeklyResult)
            result.add(event);

        for (Event event : monthlyResult)
            result.add(event);

        Collections.sort(result, new Comparator<Event>() {
            @Override
            public int compare(Event t1, Event t2) {
                if (t2.mAlertTimeStamp > t1.mAlertTimeStamp) {
                    return -1;
                } else if (t2.mAlertTimeStamp < t1.mAlertTimeStamp) {
                    return 1;
                } else {
                    return 0;
                }
            }
        });

        return result;
    }

    public List<Event> EventGet(WatchContact.Kid kid, long startTimeStamp, long endTimeStamp) {
        return EventGet(kid.mUserId, kid.mId, startTimeStamp, endTimeStamp);
    }

    public static class Event {
        int mId;
        int mUserId;
        int mKidId;
        String mName;
        long mStartDate;
        long mEndDate;
        String mColor;
        String mStatus;
        String mDescription;
        int mAlert;
        String mCity;
        String mState;
        String mRepeat;
        int mTimezoneOffset;
        long mDateCreated;
        long mLastUpdated;
        List<Todo> mTodoList;
        long mAlertTimeStamp;

        Event(int id, int alert, String repeat, long startDate, long endDate) {
            mId = id;
            mUserId = 0;
            mKidId = 0;
            mName = "";
            mStartDate = startDate;
            mEndDate = endDate;
            mColor = "";
            mStatus = "";
            mDescription = "";
            mAlert = alert;
            mCity = "";
            mState = "";
            mRepeat = repeat;
            mTimezoneOffset = 0;
            mDateCreated = 0;
            mLastUpdated = 0;
            mTodoList = new ArrayList<Todo>();
            mTodoList.add(new Todo(id, 0, 0, 0, "TEST", "", 0, 0));
            mTodoList.add(new Todo(id, 0, 0, 0, "TEST", "", 0, 0));
            mAlertTimeStamp = startDate;
        }

        Event(int id, int userId, int kidId, String name, long startDate,
              long endDate, String color, String status, String description,
              int alert, String city, String state, String repeat,
              int timezoneOffset, long dateCreated, long lastUpdated) {
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
            mTodoList = new ArrayList<Todo>();
            mAlertTimeStamp = startDate;
        }

        Event(Event src) {
            mId = src.mId;
            mUserId = src.mUserId;
            mKidId = src.mKidId;
            mName = src.mName;
            mStartDate = src.mStartDate;
            mEndDate = src.mEndDate;
            mColor = src.mColor;
            mStatus = src.mStatus;
            mDescription = src.mDescription;
            mAlert = src.mAlert;
            mCity = src.mCity;
            mState = src.mState;
            mRepeat = src.mRepeat;
            mTimezoneOffset = src.mTimezoneOffset;
            mDateCreated = src.mDateCreated;
            mLastUpdated = src.mLastUpdated;
            mTodoList = new ArrayList<>();
            for(Todo todo : src.mTodoList)
                mTodoList.add(new Todo(todo));
            mAlertTimeStamp = src.mAlertTimeStamp;
        }
    }

    private Event cursorToEvent(Cursor cursor) {
        return new Event(
                cursor.getInt(0),
                cursor.getInt(1),
                cursor.getInt(2),
                cursor.getString(3),
                cursor.getLong(4),
                cursor.getLong(5),
                cursor.getString(6),
                cursor.getString(7),
                cursor.getString(8),
                cursor.getInt(9),
                cursor.getString(10),
                cursor.getString(11),
                cursor.getString(12),
                cursor.getInt(13),
                cursor.getLong(14),
                cursor.getLong(15)
        );
    }

    public long TodoAdd(Todo todo) {
        ContentValues contentValues = new ContentValues();

        contentValues.put(ID, todo.mId);
        contentValues.put(USER_ID, todo.mUserId);
        contentValues.put(KID_ID, todo.mKidId);
        contentValues.put(EVENT_ID, todo.mEventId);
        contentValues.put(TEXT, todo.mText);
        contentValues.put(STATUS, todo.mStatus);
        contentValues.put(DATE_CREATED, todo.mDateCreated);
        contentValues.put(LAST_UPDATE, todo.mLastUpdated);

        return mDatabase.insert(TABLE_TODO, null, contentValues);
    }

    public long TodoUpdate(Todo todo) {
        ContentValues contentValues = new ContentValues();

        contentValues.put(ID, todo.mId);
        contentValues.put(USER_ID, todo.mUserId);
        contentValues.put(KID_ID, todo.mKidId);
        contentValues.put(EVENT_ID, todo.mEventId);
        contentValues.put(TEXT, todo.mText);
        contentValues.put(STATUS, todo.mStatus);
        contentValues.put(DATE_CREATED, todo.mDateCreated);
        contentValues.put(LAST_UPDATE, todo.mLastUpdated);

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

    public List<Todo> TodoGet(int eventId, int userId, int kidId) {
        List<Todo> result = new ArrayList<>();
        Cursor cursor = mDatabase.rawQuery("SELECT * FROM " + TABLE_TODO + " WHERE " + EVENT_ID + "=" + eventId + " AND " + USER_ID + "=" + userId + " AND " + KID_ID + "=" + kidId, null);

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
        long mDateCreated;
        long mLastUpdated;

        Todo(int id, int userId, int kidId, int eventId, String text, String status,
             long dateCreated, long lastUpdated) {
            mId = id;
            mUserId = userId;
            mKidId = kidId;
            mEventId = eventId;
            mText = text;
            mStatus = status;
            mDateCreated = dateCreated;
            mLastUpdated = lastUpdated;
        }

        Todo(Todo src) {
            mId = src.mId;
            mUserId = src.mUserId;
            mKidId = src.mKidId;
            mEventId = src.mEventId;
            mText = src.mText;
            mStatus = src.mStatus;
            mDateCreated = src.mDateCreated;
            mLastUpdated = src.mLastUpdated;
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
                cursor.getLong(6),
                cursor.getLong(7)
        );
    }

    public static long getTimeStamp(String dateString) {
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'", Locale.getDefault());
        format.setTimeZone(TimeZone.getTimeZone("UTC"));
        Date date;
        try {
            date = format.parse(dateString);
        } catch (Exception e) {
            e.printStackTrace();
            date = null;
        }
        if (date == null)
            return 0;

        Calendar cal = Calendar.getInstance();
        cal.setTime(date);

        return cal.getTimeInMillis();
    }

    public static String getTimeString(long timeStamp) {
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'", Locale.getDefault());
        format.setTimeZone(TimeZone.getTimeZone("UTC"));
        Date date = new Date();
        date.setTime(timeStamp);
        return format.format(date);
    }

    public ArrayList<WatchContact.Kid> getDeviceList() {
        return new ArrayList<>();
    }

    public ArrayList<WatchContact.Kid> getSharedList() {
        return new ArrayList<>();
    }

    public ArrayList<WatchContact.User> getRequestFromUserList() {
        return new ArrayList<>();
    }

    public ArrayList<WatchContact.Kid> getRequestFromKidList(WatchContact.User user) {
        // If user is null, return all requested kid, else just user's requested
        return new ArrayList<>();
    }

    public ArrayList<WatchContact.User> getRequestToUserList() {
        return new ArrayList<>();
    }

    public ArrayList<WatchContact.Kid> getRequestToKidList(WatchContact.User user) {
        // If user is null, return all requested kid, else just user's kids
        return new ArrayList<>();
    }
}
