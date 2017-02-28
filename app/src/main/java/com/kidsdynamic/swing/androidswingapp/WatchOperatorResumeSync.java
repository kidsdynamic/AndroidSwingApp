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
    private ActivityConfig mConfig;
    private WatchOperator.finishListener mFinishListener = null;
    private List<String> mAvatarToGet;

    WatchOperatorResumeSync(ActivityMain activityMain) {
        mOperator = activityMain.mOperator;
        mServerMachine = activityMain.mServiceMachine;
        mConfig = activityMain.mConfig;
    }

    void start(WatchOperator.finishListener listener, String email, String password) {
        mFinishListener = listener;
        if (email.equals("") && password.equals("")) {
            mServerMachine.userIsTokenValid(
                    mUserIsTokenValidListener,
                    mConfig.getString(ActivityConfig.KEY_MAIL),
                    mConfig.getString(ActivityConfig.KEY_AUTH_TOKEN));
        } else {
            mConfig.setString(ActivityConfig.KEY_MAIL, email);
            mConfig.setString(ActivityConfig.KEY_PASSWORD, password);
            mServerMachine.userLogin(mUserLoginListener, email, password);
        }
    }

    private ServerMachine.userIsTokenValidListener mUserIsTokenValidListener = new ServerMachine.userIsTokenValidListener() {
        @Override
        public void onValidState(boolean valid) {
            if (valid) {
                mServerMachine.setAuthToken(mConfig.getString(ActivityConfig.KEY_AUTH_TOKEN));
                mServerMachine.userRetrieveUserProfile(mRetrieveUserProfileListener);
            } else {
                mServerMachine.userLogin(mUserLoginListener, mConfig.getString(ActivityConfig.KEY_MAIL), mConfig.getString(ActivityConfig.KEY_PASSWORD));
            }
        }

        @Override
        public void onFail(String command, int statusCode) {
            mServerMachine.userLogin(mUserLoginListener, mConfig.getString(ActivityConfig.KEY_MAIL), mConfig.getString(ActivityConfig.KEY_PASSWORD));
        }
    };

    private ServerMachine.userLoginListener mUserLoginListener = new ServerMachine.userLoginListener() {
        @Override
        public void onSuccess(int statusCode, ServerGson.user.login.response result) {
            mConfig.setString(ActivityConfig.KEY_AUTH_TOKEN, result.access_token);
            mServerMachine.setAuthToken(result.access_token);
            mServerMachine.userRetrieveUserProfile(mRetrieveUserProfileListener);
        }

        @Override
        public void onFail(String command, int statusCode) {
            if (mFinishListener != null)
                mFinishListener.onFailed(mServerMachine.getErrorMessage(command, statusCode), statusCode);
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

            List<WatchContact.Kid> removeList = mOperator.getDeviceList();
            for (ServerGson.kidData kidData : response.kids) {
                WatchContact.Kid kid = new WatchContact.Kid();
                kid.mId = kidData.id;
                kid.mName = kidData.name;
                kid.mDateCreated = WatchOperator.getTimeStamp(kidData.dateCreated);
                kid.mMacId = kidData.macId;
                kid.mUserId = response.user.id;
                kid.mProfile = kidData.profile;
                kid.mBound = true;

                WatchContact.Kid src = mOperator.mWatchDatabase.KidGet(kid.mId);
                if (src == null)
                    mOperator.mWatchDatabase.KidAdd(kid);
                else
                    mOperator.mWatchDatabase.KidUpdate(kid);

                if (!kidData.profile.equals(""))
                    mAvatarToGet.add(kidData.profile);

                for (int idx = 0; idx < removeList.size(); idx++) {
                    if (removeList.get(idx).mId == kid.mId && removeList.get(idx).mUserId == kid.mUserId) {
                        removeList.remove(idx);
                        break;
                    }
                }
            }

            mOperator.deleteKids(removeList);

            mServerMachine.subHostList(mSubHostListListener, "");
        }

        @Override
        public void onFail(String command, int statusCode) {
            if (mFinishListener != null)
                mFinishListener.onFailed(mServerMachine.getErrorMessage(command, statusCode), statusCode);
        }
    };

    ServerMachine.subHostListListener mSubHostListListener = new ServerMachine.subHostListListener() {
        @Override
        public void onSuccess(int statusCode, ServerGson.subHost.list.response response) {
            List<WatchContact.User> to = new ArrayList<>();
            List<WatchContact.User> from = new ArrayList<>();

            if (response != null) {
                if (response.requestTo != null) {
                    List<WatchContact.Kid> removeList = mOperator.getSharedList();

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

                        if (subHost.status.equals("ACCEPTED")) {
                            for (ServerGson.kidData kidData : subHost.kids) {
                                WatchContact.Kid kid = new WatchContact.Kid();
                                kid.mId = kidData.id;
                                kid.mName = kidData.name;
                                kid.mDateCreated = WatchOperator.getTimeStamp(kidData.dateCreated);
                                kid.mMacId = kidData.macId;
                                kid.mUserId = subHost.requestToUser.id;
                                kid.mProfile = kidData.profile;
                                kid.mBound = true;
                                if (!kid.mProfile.equals(""))
                                    mAvatarToGet.add(kid.mProfile);

                                WatchContact.Kid src = mOperator.mWatchDatabase.KidGet(kid.mId);
                                if (src == null)
                                    mOperator.mWatchDatabase.KidAdd(kid);
                                else
                                    mOperator.mWatchDatabase.KidUpdate(kid);

                                for (int idx = 0; idx < removeList.size(); idx++) {
                                    if (removeList.get(idx).mId == kid.mId && removeList.get(idx).mUserId == kid.mUserId) {
                                        removeList.remove(idx);
                                        break;
                                    }
                                }
                            }
                        }
                    }
                    mOperator.deleteKids(removeList);
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
                        if (subHost.kids != null) {
                            for (ServerGson.kidData kidData : subHost.kids) {
                                WatchContact.Kid kid = new WatchContact.Kid();
                                kid.mLabel = kidData.name;
                                kid.mId = kidData.id;
                                kid.mName = kidData.name;
                                kid.mMacId = kidData.macId;
                                kid.mProfile = kidData.profile;
                                user.mRequestKids.add(kid);
                            }
                        }
                        from.add(user);
                        if (!user.mProfile.equals(""))
                            mAvatarToGet.add(user.mProfile);
                    }
                }
            }
            mOperator.setRequestList(to, from);
            mServerMachine.eventRetrieveAllEventsWithTodo(mEventRetrieveAllEventsWithTodoListener);
        }

        @Override
        public void onFail(String command, int statusCode) {
            mServerMachine.eventRetrieveAllEventsWithTodo(mEventRetrieveAllEventsWithTodoListener);
        }
    };

    ServerMachine.eventRetrieveAllEventsWithTodoListener mEventRetrieveAllEventsWithTodoListener = new ServerMachine.eventRetrieveAllEventsWithTodoListener() {
        @Override
        public void onSuccess(int statusCode, List<ServerGson.eventData> response) {
            mOperator.mWatchDatabase.EventClear();
            if (response != null) {
                for (ServerGson.eventData eventData : response) {
                    WatchEvent watchEvent = new WatchEvent();
                    watchEvent.mId = eventData.id;
                    watchEvent.mUserId = eventData.user.id;
                    for (ServerGson.kidData kidData : eventData.kid)
                        watchEvent.mKids.add(kidData.id);
                    watchEvent.mName = eventData.name;
                    watchEvent.mStartDate = WatchOperator.getTimeStamp(eventData.startDate);
                    watchEvent.mEndDate = WatchOperator.getTimeStamp(eventData.endDate);
                    watchEvent.mColor = eventData.color;
                    watchEvent.mStatus = eventData.status;
                    watchEvent.mDescription = eventData.description;
                    watchEvent.mAlert = eventData.alert;
                    watchEvent.mRepeat = eventData.repeat;
                    watchEvent.mTimezoneOffset = eventData.timezoneOffset;
                    watchEvent.mDateCreated = WatchOperator.getTimeStamp(eventData.dateCreated);
                    watchEvent.mLastUpdated = WatchOperator.getTimeStamp(eventData.lastUpdated);
                    if (eventData.todo != null) {
                        for (ServerGson.todoData todoData : eventData.todo) {
                            watchEvent.mTodoList.add(
                                    new WatchTodo(
                                            todoData.id,
                                            eventData.user.id,
                                            eventData.id,
                                            todoData.text,
                                            todoData.status,
                                            WatchOperator.getTimeStamp(todoData.dateCreated),
                                            WatchOperator.getTimeStamp(todoData.lastUpdated)
                                    )
                            );
                        }
                    }
                    mOperator.mWatchDatabase.EventAdd(watchEvent);
                }
            }
            syncAvatar();
        }

        @Override
        public void onFail(String command, int statusCode) {
            syncAvatar();
        }
    };

    private void syncAvatar() {
        if (mAvatarToGet.isEmpty()) {
            mFinishListener.onFinish(null);
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
