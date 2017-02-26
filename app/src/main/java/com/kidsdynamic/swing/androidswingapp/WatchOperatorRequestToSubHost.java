package com.kidsdynamic.swing.androidswingapp;

import android.graphics.Bitmap;

import java.util.List;

/**
 * Created by weichigio on 2017/2/13.
 */

public class WatchOperatorRequestToSubHost {
    private WatchOperator mOperator;
    private ServerMachine mServerMachine;
    private WatchOperator.finishListener mListener = null;

    WatchOperatorRequestToSubHost(ActivityMain activityMain) {
        mOperator = activityMain.mOperator;
        mServerMachine = activityMain.mServiceMachine;
    }

    public void start(WatchOperator.finishListener listener, String mail) {
        mListener = listener;
        mServerMachine.userFindByEmail(mUserFindByEmailListener, mail);
    }

    ServerMachine.userFindByEmailListener mUserFindByEmailListener = new ServerMachine.userFindByEmailListener() {
        @Override
        public void onSuccess(int statusCode, ServerGson.userData response) {
            mServerMachine.subHostAdd(mSubHostAddListener, response.id);
        }

        @Override
        public void onFail(int statusCode) {
            if (mListener != null)
                mListener.onFinish("Can't find user by the email.", null);
        }
    };

    ServerMachine.subHostAddListener mSubHostAddListener = new ServerMachine.subHostAddListener() {
        @Override
        public void onSuccess(int statusCode, ServerGson.hostData response) {
            List<WatchContact.User> to = mOperator.getRequestToList();

            WatchContact.User user = new WatchContact.User();
            user.mPhoto = null;
            user.mId = response.requestToUser.id;
            user.mEmail = response.requestToUser.email;
            user.mFirstName = response.requestToUser.firstName;
            user.mLastName = response.requestToUser.lastName;
            user.mProfile = response.requestToUser.profile;
            user.mRequestStatus = response.status;
            user.mLabel = user.mFirstName + " " + user.mLastName;
            to.add(user);
            mOperator.setRequestList(to, mOperator.getRequestFromList());

            if (!user.mProfile.equals("")) {
                mServerMachine.getAvatar(mGetNewUserAvatarListener, user.mProfile);
            } else {
                if (mListener != null)
                    mListener.onFinish("", user);
            }
        }

        @Override
        public void onConflict(int statusCode) {
            if (mListener != null)
                mListener.onFinish("The request is already exists.", null);
        }

        @Override
        public void onFail(int statusCode) {
            if (mListener != null)
                mListener.onFinish("Bad request.", null);
        }
    };

    ServerMachine.getAvatarListener mGetNewUserAvatarListener = new ServerMachine.getAvatarListener() {
        @Override
        public void onSuccess(Bitmap avatar, String filename) {
            List<WatchContact.User> to = mOperator.getRequestToList();

            ServerMachine.createAvatarFile(avatar, filename, "");
            WatchContact.User user = to.get(to.size()-1);
            user.mPhoto = avatar;

            if (mListener != null)
                mListener.onFinish("", user);
        }

        @Override
        public void onFail(int statusCode) {
            List<WatchContact.User> to = mOperator.getRequestToList();

            if (mListener != null)
                mListener.onFinish("", to.get(to.size()-1));
        }
    };

}
