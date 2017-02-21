package com.kidsdynamic.swing.androidswingapp;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

/**
 * Created by weichigio on 2017/2/12.
 */

public class WatchDatabase {
    public static final String TABLE_USER = "User";
    public static final String TABLE_KIDS = "Kids";
    public static final String TABLE_UPLOAD = "Upload";
    public static final String TABLE_EVENT = "Event";
    public static final String TABLE_TODO = "Todo";
    public static final String TABLE_EVENT_KITS = "EventKits";

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
                    DATE_CREATED + " INTEGER NOT NULL, " +
                    MAC_ID + " TEXT NOT NULL, " +
                    USER_ID + " INTEGER NOT NULL, " +
                    PROFILE + " TEXT)";

    public static final String CREATE_UPLOAD_TABLE =
            "CREATE TABLE " + TABLE_UPLOAD + " (" +
                    TIME + " TEXT NOT NULL, " +
                    MAC_ID + " TEXT NOT NULL, " +
                    INDOOR_ACTIVITY + " TEXT NOT NULL, " +
                    OUTDOOR_ACTIVITY + " TEXT NOT NULL)";

    public static final String CREATE_EVENT_TABLE =
            "CREATE TABLE " + TABLE_EVENT + " (" +
                    ID + " INTEGER NOT NULL, " +
                    USER_ID + " INTEGER NOT NULL, " +
                    NAME + " TEXT NOT NULL, " +
                    START_DATE + " INTEGER NOT NULL, " +
                    END_DATE + " INTEGER NOT NULL, " +
                    COLOR + " TEXT NOT NULL, " +
                    STATUS + " TEXT NOT NULL, " +
                    DESCRIPTION + " TEXT NOT NULL, " +
                    ALERT + " INTEGER NOT NULL, " +
                    REPEAT + " TEXT NOT NULL, " +
                    TIMEZONE_OFFSET + " INTEGER NOT NULL, " +
                    DATE_CREATED + " INTEGER NOT NULL, " +
                    LAST_UPDATE + " INTEGER NOT NULL)";

    public static final String CREATE_TODO_TABLE =
            "CREATE TABLE " + TABLE_TODO + " (" +
                    ID + " INTEGER NOT NULL, " +
                    USER_ID + " INTEGER NOT NULL, " +
                    EVENT_ID + " INTEGER NOT NULL, " +
                    TEXT + " TEXT NOT NULL, " +
                    STATUS + " TEXT NOT NULL, " +
                    DATE_CREATED + " INTEGER NOT NULL, " +
                    LAST_UPDATE + " INTEGER NOT NULL)";

    public static final String CREATE_EVENT_KIDS_TABLE =
            "CREATE TABLE " + TABLE_EVENT_KITS + " (" +
                    KID_ID + " INTEGER NOT NULL, " +
                    EVENT_ID + " INTEGER NOT NULL)";

    private SQLiteDatabase mDatabase;
    private Context mContext;

    public WatchDatabase(Context context) {
        mContext = context;

        mDatabase = WatchHelper.getDatabase(mContext);
    }

    public void ResetDatabase() {
        mDatabase.execSQL("DROP TABLE IF EXISTS " + TABLE_USER);
        mDatabase.execSQL("DROP TABLE IF EXISTS " + TABLE_KIDS);
        mDatabase.execSQL("DROP TABLE IF EXISTS " + TABLE_UPLOAD);
        mDatabase.execSQL("DROP TABLE IF EXISTS " + TABLE_EVENT);
        mDatabase.execSQL("DROP TABLE IF EXISTS " + TABLE_TODO);
        mDatabase.execSQL("DROP TABLE IF EXISTS " + TABLE_EVENT_KITS);

        mDatabase.execSQL(CREATE_USER_TABLE);
        mDatabase.execSQL(CREATE_KIDS_TABLE);
        mDatabase.execSQL(CREATE_UPLOAD_TABLE);
        mDatabase.execSQL(CREATE_EVENT_TABLE);
        mDatabase.execSQL(CREATE_TODO_TABLE);
        mDatabase.execSQL(CREATE_EVENT_KIDS_TABLE);
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

    public void KidClear() {
        mDatabase.execSQL("delete from " + TABLE_KIDS);
    }

    public long KidAdd(WatchContact.Kid kid) {
        ContentValues contentValues = new ContentValues();

        contentValues.put(ID, kid.mId);
        contentValues.put(FIRST_NAME, kid.mName);
        contentValues.put(DATE_CREATED, kid.mDateCreated);
        contentValues.put(MAC_ID, kid.mMacId);
        contentValues.put(USER_ID, kid.mUserId);
        contentValues.put(PROFILE, kid.mProfile);

        return mDatabase.insert(TABLE_KIDS, null, contentValues);
    }

    public long KidUpdate(WatchContact.Kid kid) {
        ContentValues contentValues = new ContentValues();

        contentValues.put(ID, kid.mId);
        contentValues.put(FIRST_NAME, kid.mName);
        contentValues.put(DATE_CREATED, kid.mDateCreated);
        contentValues.put(MAC_ID, kid.mMacId);
        contentValues.put(USER_ID, kid.mUserId);
        contentValues.put(PROFILE, kid.mProfile);

        return mDatabase.update(TABLE_KIDS, contentValues, ID + "=" + kid.mId + " AND " + USER_ID + "=" + kid.mUserId, null);
    }

    public int KidDelete(int id) {
        return mDatabase.delete(TABLE_KIDS, ID + "=" + id, null);
    }

    public WatchContact.Kid KidGet(int id) {
        WatchContact.Kid kid = null;
        Cursor cursor = mDatabase.rawQuery("SELECT * FROM " + TABLE_KIDS + " WHERE " + ID + "=" + id, null);
        if (cursor.moveToNext()) {
            kid = cursorToKid(cursor);
        }
        cursor.close();

        return kid;
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
        Cursor cursor = mDatabase.rawQuery("SELECT " + FOCUS_ID + "," + FOCUS_PID + " FROM " + TABLE_USER + " LIMIT 1", null);
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
        item.mName = cursor.getString(1);
        item.mDateCreated = cursor.getLong(2);
        item.mMacId = cursor.getString(3);
        item.mUserId = cursor.getInt(4);
        item.mProfile = cursor.getString(5);

        return item;
    }

    public long UploadItemAdd(WatchActivityRaw item) {
        ContentValues contentValues = new ContentValues();

        contentValues.put(TIME, item.mTime);
        contentValues.put(MAC_ID, item.mMacId);
        contentValues.put(INDOOR_ACTIVITY, item.mIndoor);
        contentValues.put(OUTDOOR_ACTIVITY, item.mOutdoor);

        return mDatabase.insert(TABLE_UPLOAD, null, contentValues);
    }

    public int UploadItemDelete(WatchActivityRaw item) {
        return mDatabase.delete(TABLE_UPLOAD, TIME + "='" + item.mTime + "' AND " + MAC_ID + "='" + item.mMacId + "'", null);
    }

    public WatchActivityRaw UploadItemGet() {
        WatchActivityRaw item = null;
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

    private WatchActivityRaw cursorToUpload(Cursor cursor) {
        WatchActivityRaw item = new WatchActivityRaw();

        item.mTime = cursor.getString(0);
        item.mMacId = cursor.getString(1);
        item.mIndoor = cursor.getString(2);
        item.mOutdoor = cursor.getString(3);

        return item;
    }

    public class EventKid {
        int mEventId;
        int mKidId;

        EventKid(int eventId, int kidId) {
            mEventId = eventId;
            mKidId = kidId;
        }

        EventKid() {
            mEventId = 0;
            mKidId = 0;
        }
    }

    public long EventKidAdd(EventKid eventKid) {
        ContentValues contentValues = new ContentValues();
        contentValues.put(KID_ID, eventKid.mKidId);
        contentValues.put(EVENT_ID, eventKid.mEventId);
        return mDatabase.insert(TABLE_EVENT_KITS, null, contentValues);
    }

    public List<EventKid> EventKidGet(int eventId) {
        List<EventKid> result = new ArrayList<>();
        Cursor cursor = mDatabase.rawQuery("SELECT * FROM " + TABLE_EVENT_KITS + " WHERE " + EVENT_ID + "=" + eventId, null);

        while (cursor.moveToNext())
            result.add(cursorToEventKid(cursor));

        cursor.close();

        return result;
    }

    public long EventKidDelete(int eventId) {
        return mDatabase.delete(TABLE_EVENT_KITS, EVENT_ID + "=" + eventId, null);

    }

    private EventKid cursorToEventKid(Cursor cursor) {
        EventKid item = new EventKid();

        item.mKidId = cursor.getInt(0);
        item.mEventId = cursor.getInt(1);

        return item;
    }

    public void EventClear() {
        mDatabase.execSQL("delete from " + TABLE_EVENT);
        mDatabase.execSQL("delete from " + TABLE_TODO);
        mDatabase.execSQL("delete from " + TABLE_EVENT_KITS);
    }

    public long EventAdd(WatchEvent event) {
        long rtn;
        ContentValues contentValues = new ContentValues();

        contentValues.put(ID, event.mId);
        contentValues.put(USER_ID, event.mUserId);
        contentValues.put(NAME, event.mName);
        contentValues.put(START_DATE, event.mStartDate);
        contentValues.put(END_DATE, event.mEndDate);
        contentValues.put(COLOR, event.mColor);
        contentValues.put(STATUS, event.mStatus);
        contentValues.put(DESCRIPTION, event.mDescription);
        contentValues.put(ALERT, event.mAlert);
        contentValues.put(REPEAT, event.mRepeat);
        contentValues.put(TIMEZONE_OFFSET, event.mTimezoneOffset);
        contentValues.put(DATE_CREATED, event.mDateCreated);
        contentValues.put(LAST_UPDATE, event.mLastUpdated);

        rtn = mDatabase.insert(TABLE_EVENT, null, contentValues);

        for (int kidId : event.mKids)
            EventKidAdd(new EventKid(kidId, event.mId));

        for (WatchTodo todo : event.mTodoList)
            TodoAdd(todo);

        return rtn;
    }

    public long EventUpdate(WatchEvent event) {
        ContentValues contentValues = new ContentValues();

        contentValues.put(ID, event.mId);
        contentValues.put(USER_ID, event.mUserId);
        contentValues.put(NAME, event.mName);
        contentValues.put(START_DATE, event.mStartDate);
        contentValues.put(END_DATE, event.mEndDate);
        contentValues.put(COLOR, event.mColor);
        contentValues.put(STATUS, event.mStatus);
        contentValues.put(DESCRIPTION, event.mDescription);
        contentValues.put(ALERT, event.mAlert);
        contentValues.put(REPEAT, event.mRepeat);
        contentValues.put(TIMEZONE_OFFSET, event.mTimezoneOffset);
        contentValues.put(DATE_CREATED, event.mDateCreated);
        contentValues.put(LAST_UPDATE, event.mLastUpdated);

        EventKidDelete(event.mId);
        TodoDelete(event.mId);

        for (int kidId : event.mKids)
            EventKidAdd(new EventKid(kidId, event.mId));

        for (WatchTodo todo : event.mTodoList)
            TodoAdd(todo);

        return mDatabase.update(TABLE_EVENT, contentValues, ID + "=" + event.mId + " AND " + USER_ID + "=" + event.mUserId, null);
    }

    public int EventDelete(int id) {
        mDatabase.delete(TABLE_EVENT, ID + "=" + id, null);
        EventKidDelete(id);
        TodoDelete(id);

        return 0;
    }


    private List<WatchEvent> EventGetRepeat(long startTimeStamp, long endTimeStamp, String repeat) {
        List<WatchEvent> repeatResult = new ArrayList<>();
        Cursor cursor = mDatabase.rawQuery("SELECT * FROM " + TABLE_EVENT +
                " WHERE " +
                REPEAT + "='" + repeat + "'" + " AND " +
                endTimeStamp + ">=" + START_DATE, null);

        while (cursor.moveToNext())
            repeatResult.add(cursorToEvent(cursor));

        cursor.close();

        for (WatchEvent event : repeatResult) {
            event.mTodoList = TodoGetByUser(event.mId, event.mUserId);
            List<EventKid> eventKidList = EventKidGet(event.mId);
            event.mKids = new ArrayList<>();
            for (EventKid eventKid : eventKidList)
                event.mKids.add(eventKid.mKidId);
        }

        List<WatchEvent> result = new ArrayList<>();
        Calendar cal = Calendar.getInstance();

        for (WatchEvent event : repeatResult) {
            cal.setTimeInMillis(event.mAlertTimeStamp);
            do {
                if (event.mAlertTimeStamp >= startTimeStamp && event.mAlertTimeStamp <= endTimeStamp) {
                    result.add(new WatchEvent(event));
                }
                switch (repeat) {
                    case WatchEvent.REPEAT_DAILY:
                        cal.add(Calendar.DATE, 1);
                        break;
                    case WatchEvent.REPEAT_WEEKLY:
                        cal.add(Calendar.DATE, 7);
                        break;
                    case WatchEvent.REPEAT_MONTHLY:
                        cal.add(Calendar.MONTH, 1);
                        break;
                }
                event.mAlertTimeStamp = cal.getTimeInMillis();
            } while (event.mAlertTimeStamp <= endTimeStamp);
        }

        return result;
    }

    public List<WatchEvent> EventGet(long startTimeStamp, long endTimeStamp) {
        List<WatchEvent> result = new ArrayList<>();
        Cursor cursor = mDatabase.rawQuery("SELECT * FROM " + TABLE_EVENT +
                " WHERE " + REPEAT + "=''" + " AND " +
                "((" + startTimeStamp + ">=" + START_DATE + " AND " + startTimeStamp + "<=" + END_DATE + ") OR" +
                " (" + startTimeStamp + "<=" + START_DATE + " AND " + endTimeStamp + ">=" + END_DATE + ") OR" +
                " (" + endTimeStamp + ">=" + START_DATE + " AND " + endTimeStamp + "<=" + END_DATE + "))", null);


        while (cursor.moveToNext())
            result.add(cursorToEvent(cursor));

        cursor.close();

        for (WatchEvent event : result) {
            event.mTodoList = TodoGetByUser(event.mId, event.mUserId);
            List<EventKid> eventKidList = EventKidGet(event.mId);
            event.mKids = new ArrayList<>();
            for (EventKid eventKid : eventKidList)
                event.mKids.add(eventKid.mKidId);
        }

        List<WatchEvent> dailyResult = EventGetRepeat(startTimeStamp, endTimeStamp, "DAILY");
        List<WatchEvent> weeklyResult = EventGetRepeat(startTimeStamp, endTimeStamp, "WEEKLY");
        List<WatchEvent> monthlyResult = EventGetRepeat(startTimeStamp, endTimeStamp, "MONTHLY");

        for (WatchEvent event : dailyResult)
            result.add(event);

        for (WatchEvent event : weeklyResult)
            result.add(event);

        for (WatchEvent event : monthlyResult)
            result.add(event);

        Collections.sort(result, new Comparator<WatchEvent>() {
            @Override
            public int compare(WatchEvent t1, WatchEvent t2) {
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

    public WatchEvent EventGet(int eventId) {
        WatchEvent rtn = null;
        Cursor cursor = mDatabase.rawQuery("SELECT * FROM " + TABLE_EVENT + " WHERE " + ID + "=" + eventId, null);
        if (cursor.moveToNext()) {
            rtn = cursorToEvent(cursor);
            rtn.mTodoList = TodoGetByUser(rtn.mId, rtn.mUserId);
            List<EventKid> eventKidList = EventKidGet(rtn.mId);
            rtn.mKids = new ArrayList<>();
            for (EventKid eventKid : eventKidList)
                rtn.mKids.add(eventKid.mKidId);
        }

        return rtn;
    }

    private WatchEvent cursorToEvent(Cursor cursor) {
        return new WatchEvent(
                cursor.getInt(0),
                cursor.getInt(1),
                new ArrayList<Integer>(),
                cursor.getString(2),
                cursor.getLong(3),
                cursor.getLong(4),
                cursor.getString(5),
                cursor.getString(6),
                cursor.getString(7),
                cursor.getInt(8),
                cursor.getString(9),
                cursor.getInt(10),
                cursor.getLong(11),
                cursor.getLong(12));
    }

    public long TodoAdd(WatchTodo todo) {
        ContentValues contentValues = new ContentValues();

        contentValues.put(ID, todo.mId);
        contentValues.put(USER_ID, todo.mUserId);
        contentValues.put(EVENT_ID, todo.mEventId);
        contentValues.put(TEXT, todo.mText);
        contentValues.put(STATUS, todo.mStatus);
        contentValues.put(DATE_CREATED, todo.mDateCreated);
        contentValues.put(LAST_UPDATE, todo.mLastUpdated);

        return mDatabase.insert(TABLE_TODO, null, contentValues);
    }

    public long TodoUpdate(WatchTodo todo) {
        ContentValues contentValues = new ContentValues();

        contentValues.put(ID, todo.mId);
        contentValues.put(USER_ID, todo.mUserId);
        contentValues.put(EVENT_ID, todo.mEventId);
        contentValues.put(TEXT, todo.mText);
        contentValues.put(STATUS, todo.mStatus);
        contentValues.put(DATE_CREATED, todo.mDateCreated);
        contentValues.put(LAST_UPDATE, todo.mLastUpdated);

        return mDatabase.update(TABLE_TODO, contentValues, ID + "=" + todo.mId, null);
    }

    public long TodoDelete(int eventId) {
        return mDatabase.delete(TABLE_TODO, EVENT_ID + "=" + eventId, null);
    }

    public WatchTodo TodoGet(int eventId, int todoId) {
        WatchTodo result = null;
        Cursor cursor = mDatabase.rawQuery("SELECT * FROM " + TABLE_TODO + " WHERE " + ID + "=" + todoId + " AND " + EVENT_ID + "=" + eventId, null);

        if (cursor.moveToNext())
            result = cursorToTodo(cursor);

        cursor.close();

        return result;
    }

    public List<WatchTodo> TodoGetByUser(int eventId, int userId) {
        List<WatchTodo> result = new ArrayList<>();
        Cursor cursor = mDatabase.rawQuery("SELECT * FROM " + TABLE_TODO + " WHERE " + EVENT_ID + "=" + eventId + " AND " + USER_ID + "=" + userId, null);

        while (cursor.moveToNext())
            result.add(cursorToTodo(cursor));

        cursor.close();

        return result;
    }

    private WatchTodo cursorToTodo(Cursor cursor) {
        return new WatchTodo(
                cursor.getInt(0),
                cursor.getInt(1),
                cursor.getInt(2),
                cursor.getString(3),
                cursor.getString(4),
                cursor.getLong(5),
                cursor.getLong(6)
        );
    }
}
