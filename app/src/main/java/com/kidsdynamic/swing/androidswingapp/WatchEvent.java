package com.kidsdynamic.swing.androidswingapp;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by 03543 on 2017/2/10.
 */

public class WatchEvent implements Serializable {
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

    public WatchEvent(int id, int alert, String repeat, long startDate, long endDate) {
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
        mTodoList = new ArrayList<WatchTodo>();
        mTodoList.add(new WatchTodo(id, 0, 0, 0, "TEST", "", 0, 0));
        mTodoList.add(new WatchTodo(id, 0, 0, 0, "TEST", "", 0, 0));
        mAlertTimeStamp = startDate;
    }

    public WatchEvent(int id, int userId, int kidId, String name, long startDate,
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
        mTodoList = new ArrayList<WatchTodo>();
        mAlertTimeStamp = startDate;
    }

    public WatchEvent(WatchEvent src) {
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
        for (WatchTodo todo : src.mTodoList)
            mTodoList.add(new WatchTodo(todo));
        mAlertTimeStamp = src.mAlertTimeStamp;
    }
}
