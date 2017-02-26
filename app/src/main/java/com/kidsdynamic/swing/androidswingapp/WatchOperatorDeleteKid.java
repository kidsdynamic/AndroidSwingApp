package com.kidsdynamic.swing.androidswingapp;

import android.graphics.Bitmap;

/**
 * Created by weichigio on 2017/2/17.
 */

public class WatchOperatorDeleteKid {
    private WatchOperator mOperator;
    private ServerMachine mServerMachine;
    private WatchOperator.finishListener mFinishListener = null;
    private int mId;

    WatchOperatorDeleteKid(ActivityMain activityMain) {
        mOperator = activityMain.mOperator;
        mServerMachine = activityMain.mServiceMachine;
    }

    void start(WatchOperator.finishListener listener, int id) {
        mFinishListener = listener;
        mId = id;
        mServerMachine.kidsDelete(mKidsDeleteListener, id);
    }

    ServerMachine.kidsDeleteListener mKidsDeleteListener = new ServerMachine.kidsDeleteListener() {
        @Override
        public void onSuccess(int statusCode) {
            mOperator.mWatchDatabase.KidDelete(mId);
            if (mFinishListener != null)
                mFinishListener.onFinish(null);
        }

        @Override
        public void onFail(String command, int statusCode) {
            if (mFinishListener != null)
                mFinishListener.onFailed(mServerMachine.getErrorMessage(command, statusCode), statusCode);
        }
    };

}
