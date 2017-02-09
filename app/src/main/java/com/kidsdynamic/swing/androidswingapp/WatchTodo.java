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

    public WatchTodo() {
        long now = System.currentTimeMillis();
        init(0, 0, 0, 0, "", "", now, now);
    }

    public WatchTodo(int id, int userId, int kidId, int eventId, String text, String status,
                     long dateCreated, long lastUpdated) {
        init(id, userId, kidId, eventId, text, status, dateCreated, lastUpdated);
    }

    public WatchTodo(WatchTodo src) {
        init(src.mId, src.mUserId, src.mKidId, src.mEventId, src.mText, src.mStatus, src.mDateCreated, src.mLastUpdated);
    }

    private void init(int id, int userId, int kidId, int eventId, String text, String status,
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
}
