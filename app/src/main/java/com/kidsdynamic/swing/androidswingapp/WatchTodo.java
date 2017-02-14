package com.kidsdynamic.swing.androidswingapp;

import java.io.Serializable;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

/**
 * Created by 03543 on 2017/2/10.
 */

public class WatchTodo implements Serializable {

    public final static String STATUS_DONE = "DONE";
    public final static String STATUS_PENDING = "PENDING";

    public int mId;
    public int mUserId;
    public List<Integer> mKids;
    public int mEventId;
    public String mText;
    public String mStatus;
    public long mDateCreated;
    public long mLastUpdated;

    public WatchTodo() {
        long now = System.currentTimeMillis();
        init(0, 0, new ArrayList<Integer>(), 0, "", "", now, now);
    }

    public WatchTodo(int id, int userId, List<Integer> kidId, int eventId, String text, String status,
                     long dateCreated, long lastUpdated) {
        init(id, userId, kidId, eventId, text, status, dateCreated, lastUpdated);
    }

    public WatchTodo(WatchTodo src) {
        init(src.mId, src.mUserId, src.mKids, src.mEventId, src.mText, src.mStatus, src.mDateCreated, src.mLastUpdated);
    }

    private void init(int id, int userId, List<Integer> kidId, int eventId, String text, String status,
                      long dateCreated, long lastUpdated) {
        mId = id;
        mUserId = userId;
        mKids = kidId;
        mEventId = eventId;
        mText = text;
        mStatus = status;
        mDateCreated = dateCreated;
        mLastUpdated = lastUpdated;
    }

    @Override
    public String toString() {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy/MM/dd HH/mm/ss", Locale.US);

        return new StringBuilder()
                .append("{mId:").append(mId)
                .append(" mUserId:").append(mUserId)
                .append(" mKids:").append(mKids)
                .append(" mEventId:").append(mEventId)
                .append(" mText:").append(mText)
                .append(" mStatus").append(mStatus)
                .append(" mDateCreated").append(sdf.format(mDateCreated))
                .append(" mLastUpdated").append(sdf.format(mLastUpdated))
                .append("}").toString();
    }
}
