package com.kidsdynamic.swing.androidswingapp;

import android.graphics.Bitmap;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import java.io.File;
import java.util.ArrayList;

/**
 * Created by weichigio on 2017/2/17.
 */

public class WatchOperatorSetKid {
    private WatchOperator mOperator;
    private ServerMachine mServerMachine;
    private BLEMachine mBLEMachine;
    private WatchOperator.finishListener mFinishListener = null;
    private WatchContact.Kid mKid;
    private Bitmap mAvatarBitmap = null;

    WatchOperatorSetKid(ActivityMain activityMain) {
        mOperator = activityMain.mOperator;
        mServerMachine = activityMain.mServiceMachine;
        mBLEMachine = activityMain.mBLEMachine;
        mKid = new WatchContact.Kid();
    }

    void start(WatchOperator.finishListener listener, String name, String macId, Bitmap avatar) {
        mFinishListener = listener;
        mAvatarBitmap = avatar;
        mServerMachine.kidsAdd(mKidsAddListener, name, macId);
    }

    void start(WatchOperator.finishListener listener, int id, String name, Bitmap avatar) {
        mFinishListener = listener;
        mAvatarBitmap = avatar;
        mServerMachine.kidsUpdate(mKidsUpdateListener, id, name);
    }

    ServerMachine.kidsAddListener mKidsAddListener = new ServerMachine.kidsAddListener() {
        @Override
        public void onSuccess(int statusCode, ServerGson.kidData response) {
            mKid.mId = response.id;
            mKid.mName = response.name;
            mKid.mDateCreated = WatchOperator.getTimeStamp(response.dateCreated);
            mKid.mMacId = response.macId;
            mKid.mUserId = response.parent.id;
            mOperator.setFocusKid(mKid);

            if (mAvatarBitmap != null)
                mKid.mProfile = ServerMachine.createAvatarFile(mAvatarBitmap, mKid.mName, ".jpg");
            if (mKid.mProfile == null)
                mKid.mProfile = "";

            mBLEMachine.Sync(mOnSyncListener, ServerMachine.getMacAddress(mKid.mMacId));
        }

        @Override
        public void onConflict(int statusCode) {
            if (mFinishListener != null)
                mFinishListener.onFailed("Error", statusCode);
        }

        @Override
        public void onFail(String command, int statusCode) {
            if (mFinishListener != null)
                mFinishListener.onFailed(mServerMachine.getErrorMessage(command, statusCode), statusCode);
        }
    };

    ServerMachine.kidsUpdateListener mKidsUpdateListener = new ServerMachine.kidsUpdateListener() {
        @Override
        public void onSuccess(int statusCode, ServerGson.kids.update.response response) {
            mKid = mOperator.mWatchDatabase.KidGet(response.kid.id);

            mKid.mId = response.kid.id;
            mKid.mName = response.kid.name;
            mKid.mDateCreated = WatchOperator.getTimeStamp(response.kid.dateCreated);
            mKid.mMacId = response.kid.macId;
            mKid.mUserId = response.kid.parent.id;
            mOperator.setFocusKid(mKid);

            if (mAvatarBitmap != null) {
                mKid.mProfile = ServerMachine.createAvatarFile(mAvatarBitmap, mKid.mName, ".jpg");
                mServerMachine.userAvatarUploadKid(mUserAvatarUploadKidListener, "" + mKid.mId, mKid.mProfile);
            } else {
                if (mFinishListener != null)
                    mFinishListener.onFinish(null);
            }
        }

        @Override
        public void onFail(String command, int statusCode) {
            if (mFinishListener != null)
                mFinishListener.onFinish(null);
        }
    };

    BLEMachine.onSyncListener mOnSyncListener = new BLEMachine.onSyncListener() {
        @Override
        public void onSync(int resultCode, ArrayList<WatchActivityRaw> result) {
            if (!mKid.mProfile.equals("")) {
                mServerMachine.userAvatarUploadKid(mUserAvatarUploadKidListener, "" + mKid.mId, mKid.mProfile);
            } else {
                if (mFinishListener != null)
                    mFinishListener.onFinish(null);
            }
        }
    };

    ServerMachine.userAvatarUploadKidListener mUserAvatarUploadKidListener = new ServerMachine.userAvatarUploadKidListener() {

        @Override
        public void onSuccess(int statusCode, ServerGson.user.avatar.uploadKid.response response) {

            File fileFrom = new File(mKid.mProfile);
            File fileTo = new File(ServerMachine.GetAvatarFilePath(), response.kid.profile);
            if (!fileFrom.renameTo(fileTo)) {
                Log.d("swing", "Rename failed! " + mKid.mProfile + " to " + response.kid.profile);
            }
            mKid.mProfile = response.kid.profile;
            mOperator.setFocusKid(mKid);

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
