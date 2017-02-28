package com.kidsdynamic.swing.androidswingapp;

import android.graphics.Bitmap;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by weichigio on 2017/2/13.
 */

public class WatchOperatorReplyToSubHost {
    
    private WatchOperator mOperator;
    private ServerMachine mServerMachine;
    private WatchOperator.finishListener mListener = null;
    private int mSubHostId;
    private List<Integer> mKidsId;
    private List<String> mAvatarToGet;
    
    WatchOperatorReplyToSubHost(ActivityMain activityMain) {
        mOperator = activityMain.mOperator;
        mServerMachine = activityMain.mServiceMachine;
    }

    public void start(WatchOperator.finishListener listener, int subHostId, List<Integer> kidsId) {
        mListener = listener;
        mSubHostId = subHostId;
        mKidsId = kidsId;
        mAvatarToGet = new ArrayList<>();

        if (!mKidsId.isEmpty())
            mServerMachine.subHostAccept(mSubHostAcceptListener, mSubHostId, mKidsId);
        else
            mServerMachine.subHostDeny(mSubHostDenyListener, subHostId);
    }

    ServerMachine.subHostAcceptListener mSubHostAcceptListener = new ServerMachine.subHostAcceptListener() {
        @Override
        public void onSuccess(int statusCode, ServerGson.hostData response) {
            mServerMachine.subHostList(mSubHostListListener, "");
        }

        @Override
        public void onFail(String command, int statusCode) {
            if (mListener != null)
                mListener.onFailed(mServerMachine.getErrorMessage(command, statusCode), statusCode);
        }
    };

    ServerMachine.subHostDenyListener mSubHostDenyListener = new ServerMachine.subHostDenyListener() {
        @Override
        public void onSuccess(int statusCode, ServerGson.hostData response) {
            if (!mKidsId.isEmpty())
                mServerMachine.subHostAccept(mSubHostAcceptListener, mSubHostId, mKidsId);
            else
                mServerMachine.subHostList(mSubHostListListener, "");
        }

        @Override
        public void onFail(String command, int statusCode) {
            if (!mKidsId.isEmpty())
                mServerMachine.subHostAccept(mSubHostAcceptListener, mSubHostId, mKidsId);
            else
                mServerMachine.subHostList(mSubHostListListener, "");
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
                        user.mLabel = user.mFirstName + " " + user.mLastName;
                        user.mSubHostId = subHost.id;
                        for (ServerGson.kidData kidData : subHost.kids) {
                            WatchContact.Kid kid = new WatchContact.Kid();
                            kid.mLabel = kidData.name;
                            kid.mId = kidData.id;
                            kid.mName = kidData.name;
                            kid.mMacId = kidData.macId;
                            kid.mProfile = kidData.profile;
                            user.mRequestKids.add(kid);
                        }
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
        public void onFail(String command, int statusCode) {
            syncAvatar();
        }
    };

    private void syncAvatar() {
        if (mAvatarToGet.isEmpty()) {
            mListener.onFinish(null);
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
        public void onFail(String command, int statusCode) {
            syncAvatar();
        }
    };
    
}
