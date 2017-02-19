package com.kidsdynamic.swing.androidswingapp;

import java.io.Serializable;

/**
 * Created by weichigio on 2017/2/19.
 */

public class WatchActivity implements Serializable {
    public final static String DAILY = "DAILY";
    public final static String WEEKLY = "WEEKLY";
    public final static String MONTHLY = "MONTHLY";
    public final static String YEARLY = "YEARLY";
    public final static String END = "END";

    Act mIndoor = new Act();
    Act mOutdoor = new Act();

    public class Act {
        int mId;
        String mMacId;
        String mKidId;
        int mSteps;
    }

    WatchActivity() {
        init(0, "", "", 0, 0, "", "", 0);
    }

    private void init(
            int indoorId,
            String indoorMacId,
            String indoorKidId,
            int indoorSteps,
            int outdoorId,
            String outdoorMacId,
            String outdoorKidId,
            int outdoorSteps) {

        mIndoor.mId = indoorId;
        mIndoor.mMacId = indoorMacId;
        mIndoor.mKidId = indoorKidId;
        mIndoor.mSteps = indoorSteps;
        mOutdoor.mId = outdoorId;
        mOutdoor.mMacId = outdoorMacId;
        mOutdoor.mKidId = outdoorKidId;
        mOutdoor.mSteps = outdoorSteps;
    }

    WatchActivity(WatchActivity src) {
        init( src.mIndoor.mId,
            src.mIndoor.mMacId,
            src.mIndoor.mKidId,
            src.mIndoor.mSteps,
            src.mOutdoor.mId,
            src.mOutdoor.mMacId,
            src.mOutdoor.mKidId,
            src.mOutdoor.mSteps);
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
