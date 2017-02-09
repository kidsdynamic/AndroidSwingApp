package com.kidsdynamic.swing.androidswingapp;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by 03543 on 2017/2/10.
 */

public class WatchEvent implements Serializable {
    public final static String REPEAT_NONE = "";
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
        init(0, 0, 0, "", now, now, "", "", "", 0, "", "", REPEAT_NONE, 0, now, now);
    }

    public WatchEvent(long date) {
        long now = System.currentTimeMillis();
        init(0, 0, 0, "", date, date, "", "", "", 0, "", "", REPEAT_NONE, 0, now, now);
    }

    public WatchEvent(long startDate, long endDate) {
        long now = System.currentTimeMillis();
        init(0, 0, 0, "", startDate, endDate, "", "", "", 0, "", "", REPEAT_NONE, 0, now, now);
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
}
