package com.kidsdynamic.swing.androidswingapp;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.widget.Toast;

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

    WatchOperator(Context context) {
        mActivity = (ActivityMain)context;
        mWatchDatabase = new WatchDatabase(context);
    }

    //-------------------------------------------------------------------------
    syncListener mSyncListener = null;
    private List<WatchContact.Kid> mKidList;

    interface syncListener {
        void onSync(String msg);
    }

    void sync(syncListener listener) {
        mSyncListener = listener;
        mActivity.mServiceMachine.userIsTokenValid(
                mUserIsTokenValidListener,
                mActivity.mConfig.getString(Config.KEY_MAIL),
                mActivity.mConfig.getString(Config.KEY_AUTH_TOKEN));
    }

    ServerMachine.userIsTokenValidListener mUserIsTokenValidListener = new ServerMachine.userIsTokenValidListener() {
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

    ServerMachine.userLoginListener mUserLoginListener = new ServerMachine.userLoginListener() {
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

    ServerMachine.userRetrieveUserProfileListener mRetrieveUserProfileListener = new ServerMachine.userRetrieveUserProfileListener() {
        @Override
        public void onSuccess(int statusCode, ServerGson.user.retrieveUserProfile.response response) {
            ServerMachine.ResetAvatar();
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

            mKidList = new ArrayList<>();
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
                mKidList.add(kid);
            }

            if (!response.user.profile.equals(""))
                mActivity.mServiceMachine.getAvatar(mGetUserAvatarListener, response.user.profile);
            else
                getKidAvatar(true);
        }

        @Override
        public void onFail(int statusCode) {
            if (mSyncListener != null)
                mSyncListener.onSync("Retrieve user profile failed!");
        }
    };

    ServerMachine.getAvatarListener mGetUserAvatarListener = new ServerMachine.getAvatarListener() {
        @Override
        public void onSuccess(Bitmap avatar, String filename) {
            ServerMachine.createAvatarFile(avatar, filename, "");
            getKidAvatar(true);
        }

        @Override
        public void onFail(int statusCode) {
            if (mSyncListener != null)
                mSyncListener.onSync("Get user avatar failed!");
        }
    };

    private int mProcessKidAvatar;
    private void getKidAvatar(boolean start) {
        if (start)
            mProcessKidAvatar = 0;
        else
            mProcessKidAvatar++;

        if (mProcessKidAvatar >= mKidList.size()) {
            if (mSyncListener != null)
                mSyncListener.onSync("");
            return;
        }

        while (mKidList.get(mProcessKidAvatar).mProfile.equals("")) {
            mProcessKidAvatar++;
            if (mProcessKidAvatar >= mKidList.size()) {
                if (mSyncListener != null)
                    mSyncListener.onSync("");
                return;
            }
        }

        mActivity.mServiceMachine.getAvatar(mGetKidAvatarListener, mKidList.get(mProcessKidAvatar).mProfile);
    }

    ServerMachine.getAvatarListener mGetKidAvatarListener = new ServerMachine.getAvatarListener() {
        @Override
        public void onSuccess(Bitmap avatar, String filename) {
            ServerMachine.createAvatarFile(avatar, filename, "");
            getKidAvatar(false);
        }

        @Override
        public void onFail(int statusCode) {
            if (mSyncListener != null)
                mSyncListener.onSync("Get kid avatar failed!");
        }
    };

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
        return mWatchDatabase.KidGetFocus();
    }

    List<WatchContact.Kid> getKids() {
        List<WatchContact.Kid> kids = mWatchDatabase.KidGet();
        for(WatchContact.Kid kid : kids) {
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

    public ArrayList<WatchContact.User> getRequestFromUserList() {
        return new ArrayList<>();
    }

    public ArrayList<WatchContact.Kid> getRequestFromKidList(WatchContact.User user) {
        // If user is null, return all requested kid, else just user's requested
        return new ArrayList<>();
    }

    public ArrayList<WatchContact.User> getRequestToList() {
        return new ArrayList<>();
    }
}
