package com.kidsdynamic.swing.androidswingapp;

import java.io.Serializable;

/**
 * Created by 03543 on 2017/2/10.
 */

public class WatchTodo implements Serializable {

    public int mId;
    public int mUserId;
    public int mKidId;
    public int mEventId;
    public String mText;
    public String mStatus;
    public long mDateCreated;
    public long mLastUpdated;

    public WatchTodo(int id, int userId, int kidId, int eventId, String text, String status,
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

    public WatchTodo(WatchTodo src) {
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
