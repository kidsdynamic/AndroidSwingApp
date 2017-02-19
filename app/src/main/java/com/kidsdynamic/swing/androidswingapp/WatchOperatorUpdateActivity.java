package com.kidsdynamic.swing.androidswingapp;

/**
 * Created by weichigio on 2017/2/19.
 */

public class WatchOperatorUpdateActivity {
    private WatchOperator mOperator;
    private ServerMachine mServerMachine;
    private finishListener mListener = null;
    private int mKidId;
    private String mPeriod;

    WatchOperatorUpdateActivity(ActivityMain activityMain) {
        mOperator = activityMain.mOperator;
        mServerMachine = activityMain.mServiceMachine;
    }

    interface finishListener {
        void onFinish(String msg);
    }

    public void start(finishListener listener, int kidId) {
        mListener = listener;
        mKidId = kidId;
        nextActivity(true);
    }

    private void nextActivity(boolean start) {
        if (start) {
            mPeriod = WatchActivity.DAILY;
        } else {
            switch (mPeriod) {
                case WatchActivity.DAILY:
                    mPeriod = WatchActivity.WEEKLY;
                    break;
                case WatchActivity.WEEKLY:
                    mPeriod = WatchActivity.MONTHLY;
                    break;
                case WatchActivity.MONTHLY:
                    mPeriod = WatchActivity.YEARLY;
                    break;
                case WatchActivity.YEARLY:
                    mPeriod = WatchActivity.END;
                    break;
            }
        }

        if (mPeriod.equals(WatchActivity.END)) {
            if (mListener != null)
                mListener.onFinish("");
            return;
        }

        mServerMachine.activityRetrieveData(mActivityRetrieveDataListener, mKidId + "", mPeriod);
    }

    ServerMachine.activityRetrieveDataListener mActivityRetrieveDataListener = new ServerMachine.activityRetrieveDataListener() {
        @Override
        public void onSuccess(int statusCode, ServerGson.activity.retrieveData.response response) {
            WatchActivity activity = new WatchActivity();
            ServerGson.activityData act1 = response.activities.get(0);
            ServerGson.activityData act2 = response.activities.get(1);

            activity.mIndoor.mId = act1.type.equals("INDOOR") ? act1.id : act2.id;
            activity.mIndoor.mMacId = act1.type.equals("INDOOR") ? act1.macId : act2.macId;
            activity.mIndoor.mKidId = act1.type.equals("INDOOR") ? act1.kidId : act2.kidId;
            activity.mIndoor.mSteps = act1.type.equals("INDOOR") ? act1.steps : act2.steps;

            activity.mOutdoor.mId = act1.type.equals("OUTDOOR") ? act1.id : act2.id;
            activity.mOutdoor.mMacId = act1.type.equals("OUTDOOR") ? act1.macId : act2.macId;
            activity.mOutdoor.mKidId = act1.type.equals("OUTDOOR") ? act1.kidId : act2.kidId;
            activity.mOutdoor.mSteps = act1.type.equals("OUTDOOR") ? act1.steps : act2.steps;

            mOperator.setActivity(mKidId, mPeriod, activity);
            nextActivity(false);
        }

        @Override
        public void onFail(int statusCode) {
            if (mListener != null)
                mListener.onFinish("Get activity failed " + statusCode);
        }
    };
}
