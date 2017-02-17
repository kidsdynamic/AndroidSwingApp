package com.kidsdynamic.swing.androidswingapp;

/**
 * Created by weichigio on 2017/2/18.
 */

public class WatchOperatorDeleteEvent {
    private WatchOperator mOperator;
    private ServerMachine mServerMachine;
    private finishListener mListener = null;
    private int mId;

    WatchOperatorDeleteEvent(ActivityMain activityMain) {
        mOperator = activityMain.mOperator;
        mServerMachine = activityMain.mServiceMachine;
    }

    interface finishListener {
        void onFinish(String msg);
    }

    public void start(finishListener listener, int eventId) {
        mId = eventId;
        mServerMachine.eventDelete(mEventDeleteListener, eventId);
    }

    ServerMachine.eventDeleteListener mEventDeleteListener = new ServerMachine.eventDeleteListener() {
        @Override
        public void onSuccess(int statusCode) {
            mOperator.mWatchDatabase.EventDelete(mId);
            if (mListener != null)
                mListener.onFinish("");
        }

        @Override
        public void onFail(int statusCode) {
            if (mListener != null)
                mListener.onFinish("Delete event NG " + statusCode);
        }
    };
}
