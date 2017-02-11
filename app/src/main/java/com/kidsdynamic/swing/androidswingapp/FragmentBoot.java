package com.kidsdynamic.swing.androidswingapp;

import android.app.Fragment;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by 03543 on 2017/1/4.
 */

public class FragmentBoot extends ViewFragment {
    private ActivityMain mActivityMain;
    private View mViewMain;

    private Handler mHandler;
    private List<WatchContact.Kid> mKidList;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mActivityMain = (ActivityMain) getActivity();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        mViewMain = inflater.inflate(R.layout.fragment_boot, container, false);

        mHandler = new Handler();
        mHandler.postDelayed(mRunnable, 2000);

        return mViewMain;
    }

    @Override
    public ViewFragmentConfig getConfig() {
        return new ViewFragmentConfig("Boot", false, false, false,
                ActivityMain.RESOURCE_IGNORE, ActivityMain.RESOURCE_IGNORE, ActivityMain.RESOURCE_IGNORE);
    }

    private Runnable mRunnable = new Runnable() {
        @Override
        public void run() {

            if (!mActivityMain.isPermissionGranted()) {
                mHandler.postDelayed(this, 2000);
                return;
            }

            String name = "";
            if (mActivityMain.mConfig.getString(Config.KEY_LANGUAGE).equals("")) {
                name = FragmentSignupLanguage.class.getName();
            } else {
                if (mActivityMain.mConfig.getString(Config.KEY_AUTH_TOKEN).equals("")) {
                    name = FragmentSignupLogin.class.getName();
                } else {
                    mActivityMain.mServiceMachine.userLogin(
                            mUserLoginListener,
                            mActivityMain.mConfig.getString(Config.KEY_MAIL),
                            mActivityMain.mConfig.getString(Config.KEY_PASSWORD));

                }
            }

            if (!name.equals("")) {
                Fragment fragment = Fragment.instantiate(mActivityMain, name, null);
                getFragmentManager()
                        .beginTransaction()
                        .replace(R.id.main_fragment, fragment, name)
                        .commit();
            }
        }
    };

    private void gotoSyncNow() {
        Fragment fragment = Fragment.instantiate(mActivityMain, FragmentSyncNow.class.getName(), null);
        getFragmentManager()
                .beginTransaction()
                .replace(R.id.main_fragment, fragment, FragmentSyncNow.class.getName())
                .commit();
    }

    ServerMachine.userLoginListener mUserLoginListener = new ServerMachine.userLoginListener() {
        @Override
        public void onSuccess(int statusCode, ServerGson.user.login.response result) {
            mActivityMain.mConfig.setString(Config.KEY_AUTH_TOKEN, result.access_token);
            mActivityMain.mServiceMachine.setAuthToken(result.access_token);
            mActivityMain.mServiceMachine.userRetrieveUserProfile(mRetrieveUserProfileListener);
        }

        @Override
        public void onFail(int statusCode) {
            Toast.makeText(mActivityMain,"Offline mode",Toast.LENGTH_SHORT).show();
            gotoSyncNow();
        }
    };

    ServerMachine.userRetrieveUserProfileListener mRetrieveUserProfileListener = new ServerMachine.userRetrieveUserProfileListener() {
        @Override
        public void onSuccess(int statusCode, ServerGson.user.retrieveUserProfile.response response) {
            ServerMachine.ResetAvatar();
            mActivityMain.mOperator.UserUpdate(
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
                mActivityMain.mOperator.KidUpdate(kid);
                mKidList.add(kid);
            }

            if (!response.user.profile.equals(""))
                mActivityMain.mServiceMachine.getAvatar(mGetUserAvatarListener, response.user.profile);
            else
                getKidAvatar(true);
        }

        @Override
        public void onFail(int statusCode) {
            Toast.makeText(mActivityMain,"Offline mode",Toast.LENGTH_SHORT).show();
            gotoSyncNow();
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
            Toast.makeText(mActivityMain,"Offline mode",Toast.LENGTH_SHORT).show();
            gotoSyncNow();
        }
    };

    private int mProcessKidAvatar;
    private void getKidAvatar(boolean start) {
        if (start)
            mProcessKidAvatar = 0;
        else
            mProcessKidAvatar++;

        if (mProcessKidAvatar >= mKidList.size()) {
            gotoSyncNow();
            return;
        }

        while (mKidList.get(mProcessKidAvatar).mProfile.equals("")) {
            mProcessKidAvatar++;
            if (mProcessKidAvatar >= mKidList.size()) {
                gotoSyncNow();
                return;
            }
        }

        mActivityMain.mServiceMachine.getAvatar(mGetKidAvatarListener, mKidList.get(mProcessKidAvatar).mProfile);
    }

    ServerMachine.getAvatarListener mGetKidAvatarListener = new ServerMachine.getAvatarListener() {
        @Override
        public void onSuccess(Bitmap avatar, String filename) {
            ServerMachine.createAvatarFile(avatar, filename, "");
            getKidAvatar(false);
        }

        @Override
        public void onFail(int statusCode) {
            Toast.makeText(mActivityMain,"Offline mode",Toast.LENGTH_SHORT).show();
            gotoSyncNow();
        }
    };
}
