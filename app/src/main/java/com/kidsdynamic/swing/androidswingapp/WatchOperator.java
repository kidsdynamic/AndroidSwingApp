package com.kidsdynamic.swing.androidswingapp;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Log;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.TimeZone;

/**
 * Created by 03543 on 2017/1/23.
 */

public class WatchOperator {
    private WatchDatabase mWatchDatabase;
    private ActivityMain mActivity;
    private List<WatchContact.User> mRequestToList;
    private List<WatchContact.User> mRequestFromList;
    public Sync mSync = new Sync();
    public AddRequestTo mAddRequestTo = new AddRequestTo();
    public ResponseForRequestTo mResponseForRequestTo = new ResponseForRequestTo();

    WatchOperator(Context context) {
        mActivity = (ActivityMain)context;
        mWatchDatabase = new WatchDatabase(context);
        mRequestToList = new ArrayList<>();
        mRequestFromList = new ArrayList<>();
    }

    //-------------------------------------------------------------------------
    interface syncListener {
        void onSync(String msg);
    }

    public class Sync {

        private syncListener mSyncListener = null;
        private List<String> mAvatarToGet;

        void start(syncListener listener, String email, String password) {
            mSyncListener = listener;
            if (email.equals("") && password.equals("")) {
                mActivity.mServiceMachine.userIsTokenValid(
                        mUserIsTokenValidListener,
                        mActivity.mConfig.getString(Config.KEY_MAIL),
                        mActivity.mConfig.getString(Config.KEY_AUTH_TOKEN));
            } else {
                mActivity.mConfig.setString(Config.KEY_MAIL, email);
                mActivity.mConfig.setString(Config.KEY_PASSWORD, password);
                mActivity.mServiceMachine.userLogin(mUserLoginListener, email, password);
            }
        }

        private ServerMachine.userIsTokenValidListener mUserIsTokenValidListener = new ServerMachine.userIsTokenValidListener() {
            @Override
            public void onValidState(boolean valid) {
                if (valid) {
                    mActivity.mServiceMachine.setAuthToken(mActivity.mConfig.getString(Config.KEY_AUTH_TOKEN));
                    mActivity.mServiceMachine.userRetrieveUserProfile(mRetrieveUserProfileListener);
                } else {
                    mActivity.mServiceMachine.userLogin(mUserLoginListener, mActivity.mConfig.getString(Config.KEY_MAIL), mActivity.mConfig.getString(Config.KEY_PASSWORD));
                }
            }

            @Override
            public void onFail(int statusCode) {
                mActivity.mServiceMachine.userLogin(mUserLoginListener, mActivity.mConfig.getString(Config.KEY_MAIL), mActivity.mConfig.getString(Config.KEY_PASSWORD));
            }
        };

        private ServerMachine.userLoginListener mUserLoginListener = new ServerMachine.userLoginListener() {
            @Override
            public void onSuccess(int statusCode, ServerGson.user.login.response result) {
                mActivity.mConfig.setString(Config.KEY_AUTH_TOKEN, result.access_token);
                mActivity.mServiceMachine.setAuthToken(result.access_token);
                mActivity.mServiceMachine.userRetrieveUserProfile(mRetrieveUserProfileListener);
            }

            @Override
            public void onFail(int statusCode) {
                if (mSyncListener != null)
                    mSyncListener.onSync("login failed!");
            }
        };

        private ServerMachine.userRetrieveUserProfileListener mRetrieveUserProfileListener = new ServerMachine.userRetrieveUserProfileListener() {
            @Override
            public void onSuccess(int statusCode, ServerGson.user.retrieveUserProfile.response response) {
                ServerMachine.ResetAvatar();
                mAvatarToGet = new ArrayList<>();
                setUser(
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
                    setKid(kid);
                    setFocusKid(kid);
                    if (!kidData.profile.equals(""))
                        mAvatarToGet.add(kidData.profile);
                }

                mActivity.mServiceMachine.subHostList(mSubHostListListener, "");
            }

            @Override
            public void onFail(int statusCode) {
                if (mSyncListener != null)
                    mSyncListener.onSync("Retrieve user profile failed!");
            }
        };

        ServerMachine.subHostListListener mSubHostListListener = new ServerMachine.subHostListListener() {
            @Override
            public void onSuccess(int statusCode, ServerGson.subHost.list.response response) {
                mRequestToList = new ArrayList<>();
                mRequestFromList = new ArrayList<>();

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
                            mRequestToList.add(user);
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
                            mRequestFromList.add(user);
                            if (!user.mProfile.equals(""))
                                mAvatarToGet.add(user.mProfile);
                        }
                    }
                }

                syncAvatar();
            }

            @Override
            public void onFail(int statusCode) {
                syncAvatar();
            }
        };

        private void syncAvatar() {
            if (mAvatarToGet.isEmpty()) {
                mSyncListener.onSync("");
            } else {
                mActivity.mServiceMachine.getAvatar(mGetAvatarListener, mAvatarToGet.get(0));
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
    //-------------------------------------------------------------------------

    interface responseForRequestToListener {
        void onResponse(String msg);
    }

    public class ResponseForRequestTo {
        private responseForRequestToListener mListener = null;
        private List<String> mAvatarToGet;

        public void start(responseForRequestToListener listener, int subHostId, List<Integer> kidsId) {
            mListener = listener;
            if (kidsId == null)
                kidsId = new ArrayList<>();

            mAvatarToGet = new ArrayList<>();

            if (kidsId.isEmpty())
                mActivity.mServiceMachine.subHostDeny(mSubHostDenyListener, subHostId);
            else
                mActivity.mServiceMachine.subHostAccept(mSubHostAcceptListener, subHostId, kidsId);
        }

        ServerMachine.subHostAcceptListener mSubHostAcceptListener = new ServerMachine.subHostAcceptListener() {
            @Override
            public void onSuccess(int statusCode, ServerGson.hostData response) {
                mActivity.mServiceMachine.subHostList(mSubHostListListener, "");
            }

            @Override
            public void onFail(int statusCode) {
                if (mListener != null)
                    mListener.onResponse("subHostAccept failed " + statusCode);
            }
        };

        ServerMachine.subHostDenyListener mSubHostDenyListener = new ServerMachine.subHostDenyListener() {
            @Override
            public void onSuccess(int statusCode, ServerGson.hostData response) {
                mActivity.mServiceMachine.subHostList(mSubHostListListener, "");
            }

            @Override
            public void onFail(int statusCode) {
                if (mListener != null)
                    mListener.onResponse("subHostDeny failed " + statusCode);
            }
        };
        ServerMachine.subHostListListener mSubHostListListener = new ServerMachine.subHostListListener() {
            @Override
            public void onSuccess(int statusCode, ServerGson.subHost.list.response response) {
                mRequestToList = new ArrayList<>();
                mRequestFromList = new ArrayList<>();

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
                            mRequestToList.add(user);
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
                            mRequestFromList.add(user);
                            if (!user.mProfile.equals(""))
                                mAvatarToGet.add(user.mProfile);
                        }
                    }
                }

                syncAvatar();
            }

            @Override
            public void onFail(int statusCode) {
                syncAvatar();
            }
        };

        private void syncAvatar() {
            if (mAvatarToGet.isEmpty()) {
                mListener.onResponse("");
            } else {
                mActivity.mServiceMachine.getAvatar(mGetAvatarListener, mAvatarToGet.get(0));
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

    //-------------------------------------------------------------------------

    interface addRequestToListener {
        void onAddRequestTo(String msg, WatchContact.User user);
    }

    public class AddRequestTo {
        private addRequestToListener mListener = null;

        public void start(addRequestToListener listener, String mail) {
            mListener = listener;
            mActivity.mServiceMachine.userFindByEmail(mUserFindByEmailListener, mail);
        }

        ServerMachine.userFindByEmailListener mUserFindByEmailListener = new ServerMachine.userFindByEmailListener() {
            @Override
            public void onSuccess(int statusCode, ServerGson.userData response) {
                mActivity.mServiceMachine.subHostAdd(mSubHostAddListener, response.id);
            }

            @Override
            public void onFail(int statusCode) {
                if (mListener != null)
                    mListener.onAddRequestTo("Can't find user by the email.", null);
            }
        };

        ServerMachine.subHostAddListener mSubHostAddListener = new ServerMachine.subHostAddListener() {
            @Override
            public void onSuccess(int statusCode, ServerGson.hostData response) {
                WatchContact.User user = new WatchContact.User();
                user.mPhoto = null;
                user.mId = response.requestToUser.id;
                user.mEmail = response.requestToUser.email;
                user.mFirstName = response.requestToUser.firstName;
                user.mLastName = response.requestToUser.lastName;
                user.mProfile = response.requestToUser.profile;
                user.mRequestStatus = response.status;
                user.mLabel = user.mFirstName + " " + user.mLastName;
                mRequestToList.add(user);
                if (!user.mProfile.equals("")) {
                    mActivity.mServiceMachine.getAvatar(mGetNewUserAvatarListener, user.mProfile);
                } else {
                    if (mListener != null)
                        mListener.onAddRequestTo("", user);
                }
            }

            @Override
            public void onConflict(int statusCode) {
                if (mListener != null)
                    mListener.onAddRequestTo("The request is already exists.", null);
            }

            @Override
            public void onFail(int statusCode) {
                if (mListener != null)
                    mListener.onAddRequestTo("Bad request.", null);
            }
        };

        ServerMachine.getAvatarListener mGetNewUserAvatarListener = new ServerMachine.getAvatarListener() {
            @Override
            public void onSuccess(Bitmap avatar, String filename) {
                ServerMachine.createAvatarFile(avatar, filename, "");
                WatchContact.User user = mRequestToList.get(mRequestToList.size()-1);
                user.mPhoto = avatar;

                if (mListener != null)
                    mListener.onAddRequestTo("", user);
            }

            @Override
            public void onFail(int statusCode) {
                if (mListener != null)
                    mListener.onAddRequestTo("", mRequestToList.get(mRequestToList.size()-1));
            }
        };

    }

    //-------------------------------------------------------------------------

    void ResetDatabase() {
        mWatchDatabase.ResetDatabase();
    }

    void setUser(WatchContact.User user) {
        WatchContact.User src = mWatchDatabase.UserGet();
        if (src == null)
            mWatchDatabase.UserAdd(user);
        else
            mWatchDatabase.UserUpdate(user);
    }

    WatchContact.User getUser() {
        WatchContact.User src = mWatchDatabase.UserGet();

        if (src != null) {
            src.mLabel = src.mFirstName + " " + src.mLastName;
            if (!src.mProfile.equals("")) {
                src.mPhoto = BitmapFactory.decodeFile(ServerMachine.GetAvatarFilePath() + "/" + src.mProfile);
            }
        }
        return src;
    }

    void setKid(WatchContact.Kid kid) {
        WatchContact.Kid src = mWatchDatabase.KidGet(kid);
        if (src == null)
            mWatchDatabase.KidAdd(kid);
        else
            mWatchDatabase.KidUpdate(kid);
    }

    void setFocusKid(WatchContact.Kid kid) {
        mWatchDatabase.KidSetFocus(kid);
    }

    WatchContact.Kid getFocusKid() {
        WatchContact.Kid kid = mWatchDatabase.KidGetFocus();
        kid.mBound = true;
        kid.mLabel = kid.mFirstName;
        kid.mPhoto = kid.mProfile.equals("") ? null : BitmapFactory.decodeFile(ServerMachine.GetAvatarFilePath() + "/" + kid.mProfile);

        return kid;
    }

    List<WatchContact.Kid> getKids() {
        List<WatchContact.Kid> kids = mWatchDatabase.KidGet();
        for(WatchContact.Kid kid : kids) {
            kid.mBound = true;
            kid.mLabel = kid.mFirstName;
            kid.mPhoto = kid.mProfile.equals("") ? null : BitmapFactory.decodeFile(ServerMachine.GetAvatarFilePath() + "/" + kid.mProfile);
        }

        return kids;
    }

    List<WatchEvent> getEventsForSync(WatchContact.Kid kid) {
        Calendar cal = Calendar.getInstance();
        long startTimeStamp = cal.getTimeInMillis();
        cal.add(Calendar.MONTH, 2);
        long endTimeStamp = cal.getTimeInMillis();

        return mWatchDatabase.EventGet(kid, startTimeStamp, endTimeStamp);
    }

    void pushUploadItem(String macId, byte[] time, byte[] outdoor, byte[] indoor) {
        WatchDatabase.Upload uploadItem = new WatchDatabase.Upload();

        uploadItem.mMacId = macId;
        uploadItem.mTime = byteToDec(time[0], time[1], time[2], time[3]);
        uploadItem.mOutdoorActivity = rawString(outdoor);
        uploadItem.mIndoorActivity = rawString(indoor);

        mWatchDatabase.UploadItemAdd(uploadItem);
    }

    private String rawString(byte[] b) {
        return String.format(Locale.getDefault(), "%s,%d,%s,%s,%s,%s",
                byteToStr(b[0], b[1], b[2], b[3]),
                b[4],
                byteToStr(b[5], b[6], b[7], b[8]),
                byteToStr(b[9], b[10], b[11], b[12]),
                byteToStr(b[13], b[14], b[15], b[16]),
                byteToStr(b[17], b[18], b[19], b[20])
        );
    }

    private int byteToDec(byte b0, byte b1, byte b2, byte b3) {
        int dec;

        dec = b0 & 0xFF;
        dec |= (b1 << 8) & 0xFF00;
        dec |= (b2 << 16) & 0xFF0000;
        dec |= (b3 << 24) & 0xFF000000;

        return dec;
    }

    private String byteToStr(byte b0, byte b1, byte b2, byte b3) {
        return "" + byteToDec(b0, b1, b2, b3);
    }
    static long getTimeStamp(String dateString) {
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'", Locale.getDefault());
        format.setTimeZone(TimeZone.getTimeZone("UTC"));
        Date date;
        try {
            date = format.parse(dateString);
        } catch (Exception e) {
            e.printStackTrace();
            date = null;
        }
        if (date == null)
            return 0;

        Calendar cal = Calendar.getInstance();
        cal.setTime(date);

        return cal.getTimeInMillis();
    }

    static String getTimeString(long timeStamp) {
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'", Locale.getDefault());
        format.setTimeZone(TimeZone.getTimeZone("UTC"));
        Date date = new Date();
        date.setTime(timeStamp);
        return format.format(date);
    }

    public ArrayList<WatchContact.Kid> getDeviceList() {
        WatchContact.User user = getUser();
        List<WatchContact.Kid> src = getKids();
        ArrayList<WatchContact.Kid> rtn = new ArrayList<>();

        for (WatchContact.Kid kid : src) {
            if (kid.mUserId == user.mId)
                rtn.add(kid);
        }
        return rtn;
    }

    public ArrayList<WatchContact.Kid> getSharedList() {
        WatchContact.User user = getUser();
        List<WatchContact.Kid> src = getKids();
        ArrayList<WatchContact.Kid> rtn = new ArrayList<>();

        for (WatchContact.Kid kid : src) {
            if (kid.mUserId != user.mId)
                rtn.add(kid);
        }
        return rtn;
    }

    public List<WatchContact.User> getRequestFromUserList() {
        for (WatchContact.User user : mRequestFromList) {
            if (user.mPhoto == null && !user.mProfile.equals(""))
                user.mPhoto = BitmapFactory.decodeFile(ServerMachine.GetAvatarFilePath() + "/" + user.mProfile);
        }

        return mRequestFromList;
    }

    public ArrayList<WatchContact.Kid> getRequestFromKidList(WatchContact.User user) {
        // If user is null, return all requested kid, else just user's requested
        return new ArrayList<>();
    }

    public List<WatchContact.User> getRequestToList() {
        for (WatchContact.User user : mRequestToList) {
            if (user.mPhoto == null && !user.mProfile.equals(""))
                user.mPhoto = BitmapFactory.decodeFile(ServerMachine.GetAvatarFilePath() + "/" + user.mProfile);
        }

        return mRequestToList;
    }
}
