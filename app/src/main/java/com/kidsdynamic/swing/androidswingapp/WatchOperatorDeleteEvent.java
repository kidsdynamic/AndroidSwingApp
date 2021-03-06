package com.kidsdynamic.swing.androidswingapp;

/**
 * Created by weichigio on 2017/2/18.
 */

public class WatchOperatorDeleteEvent {
    private WatchOperator mOperator;
    private ServerMachine mServerMachine;
    private WatchOperator.finishListener mListener = null;
    private int mId;

    WatchOperatorDeleteEvent(ActivityMain activityMain) {
        mOperator = activityMain.mOperator;
        mServerMachine = activityMain.mServiceMachine;
    }

    public void start(WatchOperator.finishListener listener, int eventId) {
        mId = eventId;
        mListener = listener;
        mServerMachine.eventDelete(mEventDeleteListener, eventId);
    }

    ServerMachine.eventDeleteListener mEventDeleteListener = new ServerMachine.eventDeleteListener() {
        @Override
        public void onSuccess(int statusCode) {
            mOperator.mWatchDatabase.EventDelete(mId);
            if (mListener != null)
                mListener.onFinish(null);
        }

        @Override
        public void onFail(String command, int statusCode) {
            if (mListener != null)
                mListener.onFailed(mServerMachine.getErrorMessage(command, statusCode), statusCode);

        }
    };
}
