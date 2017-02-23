package com.kidsdynamic.swing.androidswingapp;

import android.util.Log;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.List;
import java.util.TimeZone;

/**
 * Created by weichigio on 2017/2/19.
 */

class WatchOperatorUpdateActivity {
    private WatchOperator mOperator;
    private ServerMachine mServerMachine;
    private finishListener mListener = null;
    private List<WatchActivity> mActivities;
    private long mSearchStart;
    private long mSearchEnd;

    WatchOperatorUpdateActivity(ActivityMain activityMain) {
        mOperator = activityMain.mOperator;
        mServerMachine = activityMain.mServiceMachine;
    }

    interface finishListener {
        void onFinish(String msg);
    }

    public void start(finishListener listener, int kidId) {
        mListener = listener;
        Calendar cal = Calendar.getInstance();
        cal.set(Calendar.HOUR_OF_DAY, 23);
        cal.set(Calendar.MINUTE, 59);
        cal.set(Calendar.SECOND, 59);
        mSearchEnd = cal.getTimeInMillis();
        cal.add(Calendar.YEAR, -1);
        cal.add(Calendar.SECOND, 1);
        mSearchStart = cal.getTimeInMillis();
        mActivities = new ArrayList<>();

        //Log.d("swing", "Start Time("+mSearchStart+") " + WatchOperator.getDefaultTimeString(mSearchStart));
        //Log.d("swing", "End Time("+mSearchEnd+") " + WatchOperator.getDefaultTimeString(mSearchEnd));

        for (long activityTimeStamp = mSearchStart; activityTimeStamp < mSearchEnd; activityTimeStamp += 86400000) {
            mActivities.add(new WatchActivity(kidId, activityTimeStamp));
            //Log.d("swing", "activityTimeStamp("+activityTimeStamp+") " + WatchOperator.getDefaultTimeString(activityTimeStamp));
        }

        mServerMachine.activityRetrieveDataByTime(
                mActivityRetrieveDataByTimeListener,
                (int) (mSearchStart / 1000),
                (int) (mSearchEnd / 1000),
                kidId);
    }

    private ServerMachine.activityRetrieveDataByTimeListener mActivityRetrieveDataByTimeListener = new ServerMachine.activityRetrieveDataByTimeListener() {
        @Override
        public void onSuccess(int statusCode, ServerGson.activity.retrieveDataByTime.response response) {
            if (response == null || response.activities == null) {
                if (mListener != null)
                    mListener.onFinish("");
                return;
            }

            for (ServerGson.activityData activity : response.activities) {
                long timestamp = WatchOperator.getTimeStamp(activity.receivedDate);
                if (timestamp < mSearchStart || timestamp > mSearchEnd) {
                    Log.d("swing", "Retrieve activity wrong time! " + activity.receivedDate);
                    continue;
                }

                for (WatchActivity act : mActivities) {
                    long actEnd = act.mTimestamp + 86400000;
                    if (timestamp >= act.mTimestamp && timestamp <= actEnd) {
                        if (activity.type.equals("INDOOR")) {
                            act.mIndoor.mId = activity.id;
                            act.mIndoor.mMacId = activity.macId;
                            act.mIndoor.mSteps = activity.steps;
                        } else if (activity.type.equals("OUTDOOR")) {
                            act.mOutdoor.mId = activity.id;
                            act.mOutdoor.mMacId = activity.macId;
                            act.mOutdoor.mSteps = activity.steps;

                        }
                        break;
                    }
                }
            }

            Collections.reverse(mActivities);
            mOperator.setActivityList(mActivities);
            if (mListener != null)
                mListener.onFinish("");
        }

        @Override
        public void onFail(int statusCode) {
            if (mListener != null)
                mListener.onFinish("Retrieve activity failed " + statusCode);
        }
    };

}
