package com.kidsdynamic.swing.androidswingapp;

import java.io.Serializable;

/**
 * Created by weichigio on 2017/2/19.
 */

public class WatchActivity implements Serializable {
    Act mIndoor = new Act();
    Act mOutdoor = new Act();
    long mTimestamp;

    public class Act {
        int mId;
        String mMacId;
        String mKidId;
        int mSteps;
    }

    WatchActivity() {
        init(0, "", "", 0, 0, "", "", 0, 0);
    }

    WatchActivity(int kidId, long timestamp) {
        init(0, "", kidId + "", 0, 0, "", kidId + "", 0, timestamp);
    }

    WatchActivity(WatchActivity src) {
        init(src.mIndoor.mId,
                src.mIndoor.mMacId,
                src.mIndoor.mKidId,
                src.mIndoor.mSteps,
                src.mOutdoor.mId,
                src.mOutdoor.mMacId,
                src.mOutdoor.mKidId,
                src.mOutdoor.mSteps,
                0);
    }

    private void init(
            int indoorId,
            String indoorMacId,
            String indoorKidId,
            int indoorSteps,
            int outdoorId,
            String outdoorMacId,
            String outdoorKidId,
            int outdoorSteps,
            long timestamp) {

        mIndoor.mId = indoorId;
        mIndoor.mMacId = indoorMacId;
        mIndoor.mKidId = indoorKidId;
        mIndoor.mSteps = indoorSteps;
        mOutdoor.mId = outdoorId;
        mOutdoor.mMacId = outdoorMacId;
        mOutdoor.mKidId = outdoorKidId;
        mOutdoor.mSteps = outdoorSteps;
        mTimestamp = timestamp;
    }

    @Override
    public String toString() {

        return new StringBuilder()
                .append("{Indoor mId:").append(mIndoor.mId)
                .append(" Indoor mMacId:").append(mIndoor.mMacId)
                .append(" Indoor mKidId:").append(mIndoor.mKidId)
                .append(" Indoor mStep:").append(mIndoor.mSteps)
                .append(" Outdoor mId:").append(mOutdoor.mId)
                .append(" Outdoor mMacId:").append(mOutdoor.mMacId)
                .append(" Outdoor mKidId:").append(mOutdoor.mKidId)
                .append(" Outdoor mStep:").append(mOutdoor.mSteps)
                .append("}").toString();
    }
}
