package com.kidsdynamic.swing.androidswingapp;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

/**
 * Created by weichigio on 2017/2/12.
 */

class WatchDatabase {
    static final String TABLE_USER = "User";
    static final String TABLE_KIDS = "Kids";
    static final String TABLE_UPLOAD = "Upload";
    static final String TABLE_EVENT = "Event";
    static final String TABLE_TODO = "Todo";
    static final String TABLE_EVENT_KITS = "EventKits";
    static final String TABLE_ACTIVITY = "Activity";
    static final String TABLE_BATTERY = "Battery";

    private static String ID = "ID";
    private static String EMAIL = "EMAIL";
    private static String FIRST_NAME = "FIRST_NAME";
    private static String LAST_NAME = "LAST_NAME";
    private static String LAST_UPDATE = "LAST_UPDATE";
    private static String DATE_CREATED = "DATE_CREATED";
    private static String ZIP_CODE = "ZIP_CODE";
    private static String PHONE_NUMBER = "PHONE_NUMBER";
    private static String PROFILE = "PROFILE";
    private static String MAC_ID = "MAC_ID";
    private static String USER_ID = "USER_ID";
    private static String KID_ID = "KID_ID";
    private static String NAME = "NAME";
    private static String START_DATE = "START_DATE";
    private static String END_DATE = "END_DATE";
    private static String COLOR = "COLOR";
    private static String DESCRIPTION = "DESCRIPTION";
    private static String ALERT = "ALERT";
    private static String REPEAT = "REPEAT";
    private static String TIMEZONE_OFFSET = "TIMEZONE_OFFSET";
    private static String EVENT_ID = "EVENT_ID";
    private static String TEXT = "TEXT";
    private static String STATUS = "STATUS";
    private static String FOCUS_ID = "FOCUS_ID";
    private static String FOCUS_PID = "FOCUS_PID";
    private static String TIME = "TIME";
    private static String INDOOR_ACTIVITY = "INDOOR_ACTIVITY";
    private static String OUTDOOR_ACTIVITY = "OUTDOOR_ACTIVITY";
    private static String INDOOR_ID = "INDOOR_ID";
    private static String OUTDOOR_ID = "OUTDOOR_ID";
    private static String INDOOR_STEP = "INDOOR_STEP";
    private static String OUTDOOR_STEP = "OUTDOOR_STEP";
    private static String BATTERY_LIFE = "BATTERY_LIFE";
    private static String DATE_RECEIVED = "DATE_RECEIVED";
    private static String FIRMWARE_VERSION = "FIRMWARE_VERSION";


    static final String CREATE_USER_TABLE =
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

    static final String CREATE_KIDS_TABLE =
            "CREATE TABLE " + TABLE_KIDS + " (" +
                    ID + " INTEGER NOT NULL, " +
                    FIRST_NAME + " TEXT NOT NULL, " +
                    DATE_CREATED + " INTEGER NOT NULL, " +
                    MAC_ID + " TEXT NOT NULL, " +
                    USER_ID + " INTEGER NOT NULL, " +
                    FIRMWARE_VERSION + " TEXT, " +
                    PROFILE + " TEXT)";

    static final String CREATE_UPLOAD_TABLE =
            "CREATE TABLE " + TABLE_UPLOAD + " (" +
                    TIME + " INTEGER NOT NULL, " +
                    MAC_ID + " TEXT NOT NULL, " +
                    INDOOR_ACTIVITY + " TEXT NOT NULL, " +
                    OUTDOOR_ACTIVITY + " TEXT NOT NULL, " +
                    STATUS + " TEXT NOT NULL)";

    static final String CREATE_EVENT_TABLE =
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

    static final String CREATE_TODO_TABLE =
            "CREATE TABLE " + TABLE_TODO + " (" +
                    ID + " INTEGER NOT NULL, " +
                    USER_ID + " INTEGER NOT NULL, " +
                    EVENT_ID + " INTEGER NOT NULL, " +
                    TEXT + " TEXT NOT NULL, " +
                    STATUS + " TEXT NOT NULL, " +
                    DATE_CREATED + " INTEGER NOT NULL, " +
                    LAST_UPDATE + " INTEGER NOT NULL)";

    static final String CREATE_EVENT_KIDS_TABLE =
            "CREATE TABLE " + TABLE_EVENT_KITS + " (" +
                    KID_ID + " INTEGER NOT NULL, " +
                    EVENT_ID + " INTEGER NOT NULL)";

    static final String CREATE_ACTIVITY_TABLE =
            "CREATE TABLE " + TABLE_ACTIVITY + " (" +
                    INDOOR_ID + " INTEGER NOT NULL, " +
                    INDOOR_STEP + " INTEGER NOT NULL, " +
                    OUTDOOR_ID + " INTEGER NOT NULL, " +
                    OUTDOOR_STEP + " INTEGER NOT NULL, " +
                    MAC_ID + " TEXT NOT NULL, " +
                    KID_ID + " INTEGER NOT NULL, " +
                    TIME + " INTEGER NOT NULL)";

    static final String CREATE_BATTERY_TABLE =
            "CREATE TABLE " + TABLE_BATTERY + " (" +
                    MAC_ID + " TEXT NOT NULL, " +
                    BATTERY_LIFE + " INTEGER NOT NULL, " +
                    DATE_RECEIVED + " INTEGER NOT NULL)";

    private SQLiteDatabase mDatabase;

    WatchDatabase(Context context) {
        mDatabase = WatchHelper.getDatabase(context);
    }

    void ResetDatabase() {
        mDatabase.execSQL("DROP TABLE IF EXISTS " + TABLE_USER);
        mDatabase.execSQL("DROP TABLE IF EXISTS " + TABLE_KIDS);
        mDatabase.execSQL("DROP TABLE IF EXISTS " + TABLE_UPLOAD);
        mDatabase.execSQL("DROP TABLE IF EXISTS " + TABLE_EVENT);
        mDatabase.execSQL("DROP TABLE IF EXISTS " + TABLE_TODO);
        mDatabase.execSQL("DROP TABLE IF EXISTS " + TABLE_EVENT_KITS);
        mDatabase.execSQL("DROP TABLE IF EXISTS " + TABLE_ACTIVITY);
        mDatabase.execSQL("DROP TABLE IF EXISTS " + TABLE_BATTERY);

        mDatabase.execSQL(CREATE_USER_TABLE);
        mDatabase.execSQL(CREATE_KIDS_TABLE);
        mDatabase.execSQL(CREATE_UPLOAD_TABLE);
        mDatabase.execSQL(CREATE_EVENT_TABLE);
        mDatabase.execSQL(CREATE_TODO_TABLE);
        mDatabase.execSQL(CREATE_EVENT_KIDS_TABLE);
        mDatabase.execSQL(CREATE_ACTIVITY_TABLE);
        mDatabase.execSQL(CREATE_BATTERY_TABLE);
    }

    long UserAdd(WatchContact.User user) {
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

    long UserUpdate(WatchContact.User user) {
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

    WatchContact.User UserGet() {
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

    void KidClear() {
        mDatabase.execSQL("delete from " + TABLE_KIDS);
    }

    long KidAdd(WatchContact.Kid kid) {
        ContentValues contentValues = new ContentValues();

        contentValues.put(ID, kid.mId);
        contentValues.put(FIRST_NAME, kid.mName);
        contentValues.put(DATE_CREATED, kid.mDateCreated);
        contentValues.put(MAC_ID, kid.mMacId);
        contentValues.put(USER_ID, kid.mUserId);
        contentValues.put(PROFILE, kid.mProfile);
        contentValues.put(FIRMWARE_VERSION, kid.mFirmwareVersion);

        return mDatabase.insert(TABLE_KIDS, null, contentValues);
    }

    long KidUpdate(WatchContact.Kid kid) {
        ContentValues contentValues = new ContentValues();

        contentValues.put(ID, kid.mId);
        contentValues.put(FIRST_NAME, kid.mName);
        contentValues.put(DATE_CREATED, kid.mDateCreated);
        contentValues.put(MAC_ID, kid.mMacId);
        contentValues.put(USER_ID, kid.mUserId);
        contentValues.put(PROFILE, kid.mProfile);
        contentValues.put(FIRMWARE_VERSION, kid.mFirmwareVersion);

        return mDatabase.update(TABLE_KIDS, contentValues, ID + "=" + kid.mId + " AND " + USER_ID + "=" + kid.mUserId, null);
    }

    int KidDelete(int id) {
        return mDatabase.delete(TABLE_KIDS, ID + "=" + id, null);
    }

    WatchContact.Kid KidGet(int id) {
        WatchContact.Kid kid = null;
        Cursor cursor = mDatabase.rawQuery("SELECT * FROM " + TABLE_KIDS + " WHERE " + ID + "=" + id, null);
        if (cursor.moveToNext()) {
            kid = cursorToKid(cursor);
        }
        cursor.close();

        return kid;
    }

    List<WatchContact.Kid> KidGet() {
        List<WatchContact.Kid> result = new ArrayList<>();
        Cursor cursor = mDatabase.rawQuery("SELECT * FROM " + TABLE_KIDS, null);

        while (cursor.moveToNext())
            result.add(cursorToKid(cursor));

        cursor.close();

        return result;
    }

    void KidSetFocus(WatchContact.Kid kid) {
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

    WatchContact.Kid KidGetFocus() {
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
        item.mFirmwareVersion = cursor.getString(5);
        item.mProfile = cursor.getString(6);


        return item;
    }

    WatchActivityRaw UploadItemGetByTime(long time) {
        WatchActivityRaw watchActivityRaw = null;

        Cursor cursor = mDatabase.rawQuery("SELECT * FROM " + TABLE_UPLOAD + " WHERE " + TIME + "=" + time, null);
        if (cursor.moveToNext()) {
            watchActivityRaw = cursorToUpload(cursor);
        }
        cursor.close();

        return watchActivityRaw;
    }

    long UploadItemAdd(WatchActivityRaw item) {
        if (UploadItemGetByTime(item.mTime) != null)
            return 0;

        ContentValues contentValues = new ContentValues();

        contentValues.put(TIME, item.mTime);
        contentValues.put(MAC_ID, item.mMacId);
        contentValues.put(INDOOR_ACTIVITY, item.mIndoor);
        contentValues.put(OUTDOOR_ACTIVITY, item.mOutdoor);
        contentValues.put(STATUS, "PENDING");

        return mDatabase.insert(TABLE_UPLOAD, null, contentValues);
    }

    long UploadBatteryAdd(WatchBattery battery) {
        ContentValues contentValues = new ContentValues();
        contentValues.put(DATE_RECEIVED, battery.dateReceived);
        contentValues.put(MAC_ID, battery.macId);
        contentValues.put(BATTERY_LIFE, battery.batteryLife);

        return mDatabase.insert(TABLE_BATTERY, null, contentValues);
    }

    long UpdateFirmwareVersion(String firmwareVersion, String macId) {
        ContentValues contentValues = new ContentValues();

        contentValues.put(FIRMWARE_VERSION, firmwareVersion);
        return mDatabase.update(TABLE_KIDS, contentValues, MAC_ID + "='" + macId + "'", null);
    }

    long UploadItemDone(WatchActivityRaw item) {
        ContentValues contentValues = new ContentValues();

        contentValues.put(TIME, item.mTime);
        contentValues.put(MAC_ID, item.mMacId);
        contentValues.put(INDOOR_ACTIVITY, item.mIndoor);
        contentValues.put(OUTDOOR_ACTIVITY, item.mOutdoor);
        contentValues.put(STATUS, "DONE");

        return mDatabase.update(TABLE_UPLOAD, contentValues, TIME + "=" + item.mTime + " AND " + MAC_ID + "='" + item.mMacId + "'", null);
    }

    int UploadItemDelete(WatchActivityRaw item) {
        return mDatabase.delete(TABLE_UPLOAD, TIME + "='" + item.mTime + "' AND " + MAC_ID + "='" + item.mMacId + "'", null);
    }

    int UploadItemRemoveDone() {
        return mDatabase.delete(TABLE_UPLOAD, STATUS + "='DONE'", null);
    }

    List<WatchActivityRaw> UploadItemGet(String macId) {
        List<WatchActivityRaw> result = new ArrayList<>();
        Cursor cursor = mDatabase.rawQuery("SELECT * FROM " + TABLE_UPLOAD + " WHERE " + MAC_ID + " ='" + macId + "'", null);

        while (cursor.moveToNext())
            result.add(cursorToUpload(cursor));

        cursor.close();
        return result;
    }

    WatchActivityRaw UploadItemGet() {
        WatchActivityRaw item = null;
        Cursor cursor = mDatabase.rawQuery("SELECT * FROM " + TABLE_UPLOAD + " WHERE " + STATUS + " ='PENDING'" + " LIMIT 1", null);

        if (cursor.moveToNext())
            item = cursorToUpload(cursor);

        cursor.close();

        return item;
    }

    int UploadBatteryCount() {
        int result = 0;
        Cursor cursor = mDatabase.rawQuery("SELECT COUNT(*) FROM " + TABLE_BATTERY, null);

        if (cursor.moveToNext())
            result = cursor.getInt(0);

        cursor.close();

        return result;
    }

    WatchBattery UploadBatteryGet() {
        WatchBattery battery = null;
        Cursor cursor = mDatabase.rawQuery("SELECT * FROM " + TABLE_BATTERY + " LIMIT 1", null);
        if(cursor.moveToNext()) {
            battery = cursorBattery(cursor);
            mDatabase.delete(TABLE_BATTERY, DATE_RECEIVED + "=?", new String[]{String.valueOf(battery.dateReceived)});
        }

        cursor.close();
        return battery;
    }

    int UploadItemCount() {
        int result = 0;
        Cursor cursor = mDatabase.rawQuery("SELECT COUNT(*) FROM " + TABLE_UPLOAD + " WHERE " + STATUS + " ='PENDING'", null);

        if (cursor.moveToNext())
            result = cursor.getInt(0);

        cursor.close();

        return result;
    }

    private WatchActivityRaw cursorToUpload(Cursor cursor) {
        WatchActivityRaw item = new WatchActivityRaw();

        item.mTime = cursor.getInt(0);
        item.mMacId = cursor.getString(1);
        item.mIndoor = cursor.getString(2);
        item.mOutdoor = cursor.getString(3);

        return item;
    }

    private class EventKid {
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

    private long EventKidAdd(EventKid eventKid) {
        ContentValues contentValues = new ContentValues();
        contentValues.put(KID_ID, eventKid.mKidId);
        contentValues.put(EVENT_ID, eventKid.mEventId);
        return mDatabase.insert(TABLE_EVENT_KITS, null, contentValues);
    }

    private List<EventKid> EventKidGet(int eventId) {
        List<EventKid> result = new ArrayList<>();
        Cursor cursor = mDatabase.rawQuery("SELECT * FROM " + TABLE_EVENT_KITS + " WHERE " + EVENT_ID + "=" + eventId, null);

        while (cursor.moveToNext())
            result.add(cursorToEventKid(cursor));

        cursor.close();

        return result;
    }

    private long EventKidDelete(int eventId) {
        return mDatabase.delete(TABLE_EVENT_KITS, EVENT_ID + "=" + eventId, null);

    }

    private EventKid cursorToEventKid(Cursor cursor) {
        EventKid item = new EventKid();

        item.mKidId = cursor.getInt(0);
        item.mEventId = cursor.getInt(1);

        return item;
    }

    void EventClear() {
        mDatabase.execSQL("delete from " + TABLE_EVENT);
        mDatabase.execSQL("delete from " + TABLE_TODO);
        mDatabase.execSQL("delete from " + TABLE_EVENT_KITS);
    }

    long EventAdd(WatchEvent event) {
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

    long EventUpdate(WatchEvent event) {
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

    int EventDelete(int id) {
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
        Calendar alertDate = Calendar.getInstance();
        Calendar startDate = Calendar.getInstance();
        Calendar endDate = Calendar.getInstance();

        Calendar untilDate = Calendar.getInstance();
        untilDate.setTimeInMillis(alertDate.getTimeInMillis());
        switch (repeat) {
            case WatchEvent.REPEAT_MONTHLY:
                untilDate.add(Calendar.YEAR, 1);
                break;
            case WatchEvent.REPEAT_WEEKLY:
                untilDate.add(Calendar.YEAR, 1);
                break;
            default:
            case WatchEvent.REPEAT_DAILY:
                untilDate.add(Calendar.MONTH, 1);
                break;
        }
        untilDate.add(Calendar.DATE, -1);
        long untilTimeStamp = untilDate.getTimeInMillis();

        for (WatchEvent event : repeatResult) {
            alertDate.setTimeInMillis(event.mAlertTimeStamp);
            startDate.setTimeInMillis(event.mStartDate);
            endDate.setTimeInMillis(event.mEndDate);

            do {
                if (event.mAlertTimeStamp >= startTimeStamp && event.mAlertTimeStamp <= endTimeStamp) {
                    result.add(new WatchEvent(event));
                }

                switch (repeat) {
                    case WatchEvent.REPEAT_DAILY:
                        alertDate.add(Calendar.DATE, 1);
                        startDate.add(Calendar.DATE, 1);
                        endDate.add(Calendar.DATE, 1);
                        break;
                    case WatchEvent.REPEAT_WEEKLY:
                        alertDate.add(Calendar.DATE, 7);
                        startDate.add(Calendar.DATE, 7);
                        endDate.add(Calendar.DATE, 7);
                        break;
                    case WatchEvent.REPEAT_MONTHLY:
                        int day1 = alertDate.get(Calendar.DAY_OF_MONTH);
                        int day2 = 100;

                        while (day1 != day2) {
                            alertDate.add(Calendar.DATE, 1);
                            startDate.add(Calendar.DATE, 1);
                            endDate.add(Calendar.DATE, 1);

                            day2 = alertDate.get(Calendar.DAY_OF_MONTH);
                        }
                        break;
                }
                event.mAlertTimeStamp = alertDate.getTimeInMillis();
                event.mStartDate = startDate.getTimeInMillis();
                event.mEndDate = endDate.getTimeInMillis();
            }
            while (event.mAlertTimeStamp <= endTimeStamp && event.mAlertTimeStamp < untilTimeStamp);
        }

        return result;
    }

    void EventTest() {
        List<WatchEvent> result = new ArrayList<>();
        Cursor cursor = mDatabase.rawQuery("SELECT * FROM " + TABLE_EVENT, null);

        while (cursor.moveToNext())
            result.add(cursorToEvent(cursor));

        cursor.close();

        for (WatchEvent r : result) {
            Log.d("TEST", r.mName + " " + r.mAlertTimeStamp);
        }

    }

    List<WatchEvent> EventGet(long startTimeStamp, long endTimeStamp) {
        List<WatchEvent> result = new ArrayList<>();
        // 先取得不是repeat，且時間符合的event
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

        // 分別取得Daily, Weekly及Monthly的event, 此回傳列表已會展開repeat, 換言之，若有Daily，則會直接
        // 展成一個月的events.
        List<WatchEvent> dailyResult = EventGetRepeat(startTimeStamp, endTimeStamp, "DAILY");
        List<WatchEvent> weeklyResult = EventGetRepeat(startTimeStamp, endTimeStamp, "WEEKLY");
        List<WatchEvent> monthlyResult = EventGetRepeat(startTimeStamp, endTimeStamp, "MONTHLY");

        for (WatchEvent event : dailyResult)
            result.add(event);

        for (WatchEvent event : weeklyResult)
            result.add(event);

        for (WatchEvent event : monthlyResult)
            result.add(event);

        // 反序，符合UI需求
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

    WatchEvent EventGet(int eventId) {
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

    private long TodoAdd(WatchTodo todo) {
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

    long TodoUpdate(WatchTodo todo) {
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

    private long TodoDelete(int eventId) {
        return mDatabase.delete(TABLE_TODO, EVENT_ID + "=" + eventId, null);
    }

    /*
        public WatchTodo TodoGet(int eventId, int todoId) {
            WatchTodo result = null;
            Cursor cursor = mDatabase.rawQuery("SELECT * FROM " + TABLE_TODO + " WHERE " + ID + "=" + todoId + " AND " + EVENT_ID + "=" + eventId, null);

            if (cursor.moveToNext())
                result = cursorToTodo(cursor);

            cursor.close();

            return result;
        }
    */
    private List<WatchTodo> TodoGetByUser(int eventId, int userId) {
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

    public void activityDeleteByKidId(int kidId) {
        mDatabase.delete(TABLE_ACTIVITY, KID_ID + "=" + kidId, null);
    }

    public void activityImport(List<WatchActivity> watchActivities) {
        ContentValues contentValues = new ContentValues();

        for (WatchActivity watchActivity : watchActivities) {
            contentValues.put(INDOOR_ID, watchActivity.mIndoor.mId);
            contentValues.put(INDOOR_STEP, watchActivity.mIndoor.mSteps);
            contentValues.put(OUTDOOR_ID, watchActivity.mOutdoor.mId);
            contentValues.put(OUTDOOR_STEP, watchActivity.mOutdoor.mSteps);
            contentValues.put(MAC_ID, watchActivity.mIndoor.mMacId);
            contentValues.put(KID_ID, Integer.valueOf(watchActivity.mIndoor.mKidId));
            contentValues.put(TIME, watchActivity.mIndoor.mTimestamp);

            mDatabase.insert(TABLE_ACTIVITY, null, contentValues);
        }
    }

    public List<WatchActivity> activityExport(int KidId) {
        List<WatchActivity> result = new ArrayList<>();
        Cursor cursor = mDatabase.rawQuery("SELECT * FROM " + TABLE_ACTIVITY + " WHERE " + KID_ID + "=" + KidId, null);

        while (cursor.moveToNext()) {
            result.add(cursorActivity(cursor));
        }

        cursor.close();

        return result;
    }

    public List<WatchBattery> batteryExport(int macId) {
        List<WatchBattery> result = new ArrayList<>();
        Cursor cursor = mDatabase.rawQuery("SELECT * FROM " + TABLE_BATTERY + " WHERE " + MAC_ID + "=" + macId, null);

        while (cursor.moveToNext()) {
            result.add(cursorBattery(cursor));
        }

        cursor.close();

        return result;
    }

    int activityCount() {
        int result = 0;
        Cursor cursor = mDatabase.rawQuery("SELECT COUNT(*) FROM " + TABLE_ACTIVITY, null);

        if (cursor.moveToNext())
            result = cursor.getInt(0);

        cursor.close();

        return result;
    }


    private WatchActivity cursorActivity(Cursor cursor) {
        WatchActivity watchActivity = new WatchActivity();

        watchActivity.mIndoor.mId = cursor.getInt(0);
        watchActivity.mIndoor.mSteps = cursor.getInt(1);
        watchActivity.mOutdoor.mId = cursor.getInt(2);
        watchActivity.mOutdoor.mSteps = cursor.getInt(3);
        watchActivity.mIndoor.mMacId = cursor.getString(4);
        watchActivity.mOutdoor.mMacId = watchActivity.mIndoor.mMacId;
        watchActivity.mIndoor.mKidId = String.valueOf(cursor.getInt(5));
        watchActivity.mOutdoor.mKidId = watchActivity.mIndoor.mKidId;
        watchActivity.mIndoor.mTimestamp = cursor.getLong(6);
        watchActivity.mOutdoor.mTimestamp = watchActivity.mIndoor.mTimestamp;

        return watchActivity;
    }

    private WatchBattery cursorBattery(Cursor cursor) {
        WatchBattery watchBattery = new WatchBattery();
        watchBattery.batteryLife = cursor.getInt(1);
        watchBattery.macId = cursor.getString(0);
        watchBattery.dateReceived = cursor.getInt(2);
        Log.d("Battery retreive", watchBattery.toString());
        return watchBattery;

    }
}
