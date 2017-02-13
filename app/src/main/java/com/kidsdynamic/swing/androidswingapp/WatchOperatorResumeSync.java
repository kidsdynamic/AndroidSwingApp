package com.kidsdynamic.swing.androidswingapp;

import android.graphics.Bitmap;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by weichigio on 2017/2/13.
 */

public class WatchOperatorResumeSync {

    private WatchOperator mOperator;
    private ServerMachine mServerMachine;
    private Config mConfig;
    private finishListener mFinishListener = null;
    private List<String> mAvatarToGet;
    
    WatchOperatorResumeSync(ActivityMain activityMain) {
        mOperator = activityMain.mOperator;
        mServerMachine = activityMain.mServiceMachine;
        mConfig = activityMain.mConfig;
    }

    interface finishListener {
        void onFinish(String msg);
    }

    void start(finishListener listener, String email, String password) {
        mFinishListener = listener;
        if (email.equals("") && password.equals("")) {
            mServerMachine.userIsTokenValid(
                    mUserIsTokenValidListener,
                    mConfig.getString(Config.KEY_MAIL),
                    mConfig.getString(Config.KEY_AUTH_TOKEN));
        } else {
            mConfig.setString(Config.KEY_MAIL, email);
            mConfig.setString(Config.KEY_PASSWORD, password);
            mServerMachine.userLogin(mUserLoginListener, email, password);
        }
    }

    private ServerMachine.userIsTokenValidListener mUserIsTokenValidListener = new ServerMachine.userIsTokenValidListener() {
        @Override
        public void onValidState(boolean valid) {
            if (valid) {
                mServerMachine.setAuthToken(mConfig.getString(Config.KEY_AUTH_TOKEN));
                mServerMachine.userRetrieveUserProfile(mRetrieveUserProfileListener);
            } else {
                mServerMachine.userLogin(mUserLoginListener, mConfig.getString(Config.KEY_MAIL), mConfig.getString(Config.KEY_PASSWORD));
            }
        }

        @Override
        public void onFail(int statusCode) {
            mServerMachine.userLogin(mUserLoginListener, mConfig.getString(Config.KEY_MAIL), mConfig.getString(Config.KEY_PASSWORD));
        }
    };

    private ServerMachine.userLoginListener mUserLoginListener = new ServerMachine.userLoginListener() {
        @Override
        public void onSuccess(int statusCode, ServerGson.user.login.response result) {
            mConfig.setString(Config.KEY_AUTH_TOKEN, result.access_token);
            mServerMachine.setAuthToken(result.access_token);
            mServerMachine.userRetrieveUserProfile(mRetrieveUserProfileListener);
        }

        @Override
        public void onFail(int statusCode) {
            if (mFinishListener != null)
                mFinishListener.onFinish("login failed!");
        }
    };

    private ServerMachine.userRetrieveUserProfileListener mRetrieveUserProfileListener = new ServerMachine.userRetrieveUserProfileListener() {
        @Override
        public void onSuccess(int statusCode, ServerGson.user.retrieveUserProfile.response response) {
            ServerMachine.ResetAvatar();
            mAvatarToGet = new ArrayList<>();
            mOperator.setUser(
                    new WatchContact.User(
                            null,
                            response.user.id,
                            response.user.email,
                            response.user.firstName,
                            response.user.lastName,
                            WatchOperator.getTimeStamp(response.user.lastUpdate),
                            WatchOperator.getTimeStamp(response.user.dateCreated),
                            response.user.zipCode,
                            response.user.phoneNumber,
                            response.user.profile)
            );
            if (!response.user.profile.equals(""))
                mAvatarToGet.add(response.user.profile);

            mOperator.clearKids();
            for (ServerGson.kidData kidData : response.kids) {
                WatchContact.Kid kid = new WatchContact.Kid();
                kid.mId = kidData.id;
                kid.mFirstName = kidData.name;
                kid.mLastName = "";
                kid.mDateCreated = WatchOperator.getTimeStamp(kidData.dateCreated);
                kid.mMacId = kidData.macId;
                kid.mUserId = response.user.id;
                kid.mProfile = kidData.profile;
                kid.mBound = true;
                mOperator.setKid(kid);
                mOperator.setFocusKid(kid);
                if (!kidData.profile.equals(""))
                    mAvatarToGet.add(kidData.profile);
            }

            mServerMachine.subHostList(mSubHostListListener, "");
        }

        @Override
        public void onFail(int statusCode) {
            if (mFinishListener != null)
                mFinishListener.onFinish("Retrieve user profile failed!");
        }
    };

    ServerMachine.subHostListListener mSubHostListListener = new ServerMachine.subHostListListener() {
        @Override
        public void onSuccess(int statusCode, ServerGson.subHost.list.response response) {
            List<WatchContact.User> to = new ArrayList<>();
            List<WatchContact.User> from = new ArrayList<>();

            if (response != null) {
                if (response.requestTo != null) {
                    for (ServerGson.hostData subHost : response.requestTo) {
                        WatchContact.User user = new WatchContact.User();
                        user.mPhoto = null;
                        user.mId = subHost.requestToUser.id;
                        user.mEmail = subHost.requestToUser.email;
                        user.mFirstName = subHost.requestToUser.firstName;
                        user.mLastName = subHost.requestToUser.lastName;
                        user.mProfile = subHost.requestToUser.profile;
                        user.mRequestStatus = subHost.status;
                        user.mSubHostId = subHost.id;
                        user.mLabel = user.mFirstName + " " + user.mLastName;
                        to.add(user);
                        if (!user.mProfile.equals(""))
                            mAvatarToGet.add(user.mProfile);
                    }
                }

                if (response.requestFrom != null) {
                    for (ServerGson.hostData subHost : response.requestFrom) {
                        WatchContact.User user = new WatchContact.User();
                        user.mPhoto = null;
                        user.mId = subHost.requestFromUser.id;
                        user.mEmail = subHost.requestFromUser.email;
                        user.mFirstName = subHost.requestFromUser.firstName;
                        user.mLastName = subHost.requestFromUser.lastName;
                        user.mProfile = subHost.requestFromUser.profile;
                        user.mRequestStatus = subHost.status;
                        user.mSubHostId = subHost.id;
                        user.mLabel = user.mFirstName + " " + user.mLastName;
                        from.add(user);
                        if (!user.mProfile.equals(""))
                            mAvatarToGet.add(user.mProfile);
                    }
                }
            }
            mOperator.setRequestList(to, from);
            syncAvatar();
        }

        @Override
        public void onFail(int statusCode) {
            syncAvatar();
        }
    };

    private void syncAvatar() {
        if (mAvatarToGet.isEmpty()) {
            mFinishListener.onFinish("");
        } else {
            mServerMachine.getAvatar(mGetAvatarListener, mAvatarToGet.get(0));
            mAvatarToGet.remove(0);
        }
    }

    private ServerMachine.getAvatarListener mGetAvatarListener = new ServerMachine.getAvatarListener() {
        @Override
        public void onSuccess(Bitmap avatar, String filename) {
            ServerMachine.createAvatarFile(avatar, filename, "");
            syncAvatar();
        }

        @Override
        public void onFail(int statusCode) {
            syncAvatar();
        }
    };

}
