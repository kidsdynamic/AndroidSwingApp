package com.kidsdynamic.swing.androidswingapp;

import android.graphics.Bitmap;
import android.util.Log;
import java.io.File;

/**
 * Created by weichigio on 2017/2/26.
 */

public class WatchOperatorSignUp {
    private WatchOperator mOperator;
    private ServerMachine mServerMachine;
    private ActivityConfig mConfig;
    private WatchOperator.finishListener mFinishListener = null;

    private String mEmail;
    private String mPassword;
    private String mFirstName;
    private String mLastName;
    private String mPhone;
    private String mZip;
    private Bitmap mAvatar;
    private String mRegisterAvatarFilename = null;


    WatchOperatorSignUp(ActivityMain activityMain) {
        mOperator = activityMain.mOperator;
        mServerMachine = activityMain.mServiceMachine;
        mConfig = activityMain.mConfig;
    }

    void start(WatchOperator.finishListener listener, String email, String password, String firstName, String lastName, String phone, String zip, Bitmap avatar) {
        mFinishListener = listener;
        mEmail = email;
        mPassword = password;
        mFirstName = firstName;
        mLastName = lastName;
        mPhone = phone;
        mZip = zip;
        mAvatar = avatar;

        Log.d("HERE", "START+++");

        mServerMachine.userRegister(mRegisterListener, mEmail, mPassword, mFirstName, mLastName, mPhone, mZip);
    }

    private ServerMachine.userRegisterListener mRegisterListener = new ServerMachine.userRegisterListener() {
        @Override
        public void onSuccess(int statusCode) {
            mServerMachine.userLogin(mLoginListener, mEmail, mPassword);
        }

        @Override
        public void onFail(int statusCode, ServerGson.error.e1 error) {
            if (mFinishListener != null)
                mFinishListener.onFinish("Register failed!", null);
        }
    };

    private ServerMachine.userLoginListener mLoginListener = new ServerMachine.userLoginListener() {
        @Override
        public void onSuccess(int statusCode, ServerGson.user.login.response result) {
            mConfig.setString(ActivityConfig.KEY_AUTH_TOKEN, result.access_token);
            mServerMachine.setAuthToken(result.access_token);
            mServerMachine.userUpdateProfile(mUpdateProfileListener, mFirstName, mLastName, mPhone, mZip);
        }

        @Override
        public void onFail(int statusCode) {
            if (mFinishListener != null)
                mFinishListener.onFinish("login failed!", null);
        }
    };

    private ServerMachine.userUpdateProfileListener mUpdateProfileListener = new ServerMachine.userUpdateProfileListener() {
        @Override
        public void onSuccess(int statusCode, ServerGson.userData response) {
            mConfig.setString(ActivityConfig.KEY_MAIL, mEmail);
            mConfig.setString(ActivityConfig.KEY_PASSWORD, mPassword);

            mOperator.ResetDatabase();
            ServerMachine.ResetAvatar();
            mOperator.setUser(
                    new WatchContact.User(
                            null,
                            response.id,
                            response.email,
                            response.firstName,
                            response.lastName,
                            WatchOperator.getTimeStamp(response.lastUpdate),
                            WatchOperator.getTimeStamp(response.dateCreated),
                            response.zipCode,
                            response.phoneNumber)
            );

            if (mAvatar != null) {
                mRegisterAvatarFilename = ServerMachine.createAvatarFile(mAvatar, "User", ".jpg");
                mServerMachine.userAvatarUpload(mUserAvatarUploadListener, mRegisterAvatarFilename);
            } else {
                if (mFinishListener != null)
                    mFinishListener.onFinish("", null);
            }
        }

        @Override
        public void onFail(int statusCode, ServerGson.error.e1 error) {
            if (mFinishListener != null)
                mFinishListener.onFinish("Update failed", null);
        }
    };

    private ServerMachine.userAvatarUploadListener mUserAvatarUploadListener = new ServerMachine.userAvatarUploadListener() {
        @Override
        public void onSuccess(int statusCode, ServerGson.user.avatar.upload.response response) {
            WatchContact.User user = mOperator.getUser();
            File fileFrom = new File(mRegisterAvatarFilename);
            File fileTo = new File(ServerMachine.GetAvatarFilePath(), response.user.profile);
            if (!fileFrom.renameTo(fileTo))
                Log.d("swing", "Rename failed! " + mRegisterAvatarFilename + " to " + response.user.profile);
            user.mProfile = response.user.profile;
            mOperator.setUser(user);

            if (mFinishListener != null)
                mFinishListener.onFinish("", null);
        }

        @Override
        public void onFail(int statusCode) {
            if (mFinishListener != null)
                mFinishListener.onFinish("Upload avatar failed", null);
        }
    };

}
