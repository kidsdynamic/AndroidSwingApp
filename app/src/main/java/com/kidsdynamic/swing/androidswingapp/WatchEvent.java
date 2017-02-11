package com.kidsdynamic.swing.androidswingapp;

import android.util.Log;

import java.io.Serializable;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

/**
 * Created by 03543 on 2017/2/10.
 */

public class WatchEvent implements Serializable {
    public final static String REPEAT_NEVER = "";
    public final static String REPEAT_DAILY = "DAILY";
    public final static String REPEAT_WEEKLY = "WEEKLY";
    public final static String REPEAT_MONTHLY = "MONTHLY";

    public int mId;
    public int mUserId;
    public int mKidId;
    public String mName;
    public long mStartDate;
    public long mEndDate;
    public String mColor;
    public String mStatus;
    public String mDescription;
    public int mAlert;
    public String mCity;
    public String mState;
    public String mRepeat;
    public int mTimezoneOffset;
    public long mDateCreated;
    public long mLastUpdated;
    public List<WatchTodo> mTodoList;
    public long mAlertTimeStamp;

    public WatchEvent() {
        long now = System.currentTimeMillis();
        init(0, 0, 0, "", now, now, "#FF7231", "", "", 0, "", "", REPEAT_NEVER, 0, now, now);
    }

    public WatchEvent(long date) {
        long now = System.currentTimeMillis();
        init(0, 0, 0, "", date, date, "#FF7231", "", "", 0, "", "", REPEAT_NEVER, 0, now, now);
    }

    public WatchEvent(long startDate, long endDate) {
        long now = System.currentTimeMillis();
        init(0, 0, 0, "", startDate, endDate, "#FF7231", "", "", 0, "", "", REPEAT_NEVER, 0, now, now);
    }

    public WatchEvent(int id, int userId, int kidId, String name, long startDate,
                      long endDate, String color, String status, String description,
                      int alert, String city, String state, String repeat,
                      int timezoneOffset, long dateCreated, long lastUpdated) {
        init(id, userId, kidId, name, startDate, endDate, color, status, description,
                alert, city, state, repeat, timezoneOffset, dateCreated, lastUpdated);
    }

    public WatchEvent(WatchEvent src) {
        init(src.mId, src.mUserId, src.mKidId, src.mName, src.mStartDate, src.mEndDate, src.mColor, src.mStatus, src.mDescription,
                src.mAlert, src.mCity, src.mState, src.mRepeat, src.mTimezoneOffset, src.mDateCreated, src.mLastUpdated);

        for (WatchTodo todo : src.mTodoList)
            mTodoList.add(new WatchTodo(todo));
    }

    private void init(int id, int userId, int kidId, String name, long startDate,
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
        mTodoList = new ArrayList<>();
        mAlertTimeStamp = startDate;
    }

    @Override
    public String toString() {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy/MM/dd HH/mm/ss", Locale.US);

        return new StringBuilder()
                .append("{mId:").append(mId)
                .append(" mUserId:").append(mUserId)
                .append(" mKidId:").append(mKidId)
                .append(" mName:").append(mName)
                .append(" mStartDate:").append(sdf.format(mStartDate))
                .append(" mEndDate:").append(sdf.format(mEndDate))
                .append(" mColor:").append(mColor)
                .append(" mStatus:").append(mStatus)
                .append(" mDescription:").append(mDescription)
                .append(" mAlert:").append(mAlert)
                .append(" mCity:").append(mCity)
                .append(" mState:").append(mState)
                .append(" mRepeat:").append(mRepeat)
                .append(" mTimezoneOffset:").append(mTimezoneOffset)
                .append(" mDateCreated:").append(sdf.format(mDateCreated))
                .append(" mLastUpdated:").append(sdf.format(mLastUpdated))
                .append(" mTodoList:").append(mTodoList.toString())
                .append(" mAlertTimeStamp:").append(mAlertTimeStamp)
                .append("}").toString();
    }

    static String colorToString(int color) {
        String string = "#000000";

        color &= 0x00FFFFFF; // Format is argb, remove alpha value.

        try {
            string = String.format("#%06X", color);
        } catch (NumberFormatException e) {
            Log.d("Swing", e.getMessage());
        }

        return string;
    }

    static int stringToColor(String string) {
        int color = 0;

        if (string.length() != 7 || string.charAt(0) != '#')
            return 0;

        string = string.substring(1);
        try {
            color = Integer.parseInt(string, 16);
            color |= 0xFF000000;
        } catch (NumberFormatException e) {
            Log.d("Swing", e.getMessage());
        }

        return color;
    }

    static public class NoticeAlarm {
        int mId;
        String mName;
        int mResource;

        NoticeAlarm(int id, String name, int resource) {
            mId = id;
            mName = name;
            mResource = resource;
        }
    }

    final static NoticeAlarm[] NoticeAlarmList = new NoticeAlarm[]{
            new NoticeAlarm(36, "Good Morning", R.mipmap.icon_alert),
            new NoticeAlarm(37, "Make Bed", R.mipmap.icon_sound),
            new NoticeAlarm(38, "Get Dress", R.mipmap.icon_sound),
            new NoticeAlarm(39, "Eat Breakfast", R.mipmap.icon_sound),
            new NoticeAlarm(40, "Brush Teeth", R.mipmap.icon_sound),
            new NoticeAlarm(41, "Get Ready for School", R.mipmap.icon_sound),
            new NoticeAlarm(42, "Put on Pajamas", R.mipmap.icon_sound),
            new NoticeAlarm(43, "Story Time", R.mipmap.icon_sound),
            new NoticeAlarm(44, "Good Night", R.mipmap.icon_sound),
            new NoticeAlarm(45, "Collect Toys", R.mipmap.icon_sound),
            new NoticeAlarm(46, "Set Table", R.mipmap.icon_sound),
            new NoticeAlarm(47, "Feed Pet", R.mipmap.icon_sound),
            new NoticeAlarm(48, "Water Plants", R.mipmap.icon_sound),
            new NoticeAlarm(49, "Clean Table", R.mipmap.icon_sound),
            new NoticeAlarm(50, "Clean Bedroom", R.mipmap.icon_sound),
            new NoticeAlarm(51, "Homework Time", R.mipmap.icon_sound),
            new NoticeAlarm(52, "Take a Nap", R.mipmap.icon_sound),
            new NoticeAlarm(53, "Outdoor Play Time", R.mipmap.icon_sound),
            new NoticeAlarm(54, "Fun time", R.mipmap.icon_sound),
            new NoticeAlarm(55, "Exercise", R.mipmap.icon_sound),
            new NoticeAlarm(56, "Practice Music", R.mipmap.icon_sound),
            new NoticeAlarm(57, "Drawing Time", R.mipmap.icon_sound),
            new NoticeAlarm(58, "Reading Time", R.mipmap.icon_sound),
            new NoticeAlarm(59, "Take a Bath", R.mipmap.icon_sound),
            new NoticeAlarm(60, "Family Time", R.mipmap.icon_sound),
            new NoticeAlarm(61, "Lunch Time", R.mipmap.icon_sound),
            new NoticeAlarm(62, "Dinner Time", R.mipmap.icon_sound),
            new NoticeAlarm(63, "Afternoon Snack Time", R.mipmap.icon_sound),
            new NoticeAlarm(64, "Review the Backpack", R.mipmap.icon_sound),
    };

    public static String findAlarmName(int id) {
        for (NoticeAlarm alarm : NoticeAlarmList)
            if (alarm.mId == id)
                return alarm.mName;
        return "";
    }

    public static int findAlarmId(String name) {
        for (NoticeAlarm alarm : NoticeAlarmList)
            if (alarm.mName.equals(name))
                return alarm.mId;
        return -1;
    }
}
