package com.kidsdynamic.swing.androidswingapp;

import android.app.Dialog;
import android.app.ProgressDialog;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by 03543 on 2016/12/30.
 */

public class FragmentSignupAccount extends ViewFragment {
    private ActivityMain mActivityMain;
    private View mViewMain;

    private EditText mViewEmail;
    private EditText mViewPassword;
    private ImageView mViewBack;

    private Dialog mProcessDialog = null;
    private String mMail = "";
    private String mPassword = "";
    private List<WatchContact.Kid> mKidList;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mActivityMain = (ActivityMain) getActivity();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        String mail = mActivityMain.mConfig.getString(Config.KEY_MAIL);
        String password = mActivityMain.mConfig.getString(Config.KEY_PASSWORD);

        mViewMain = inflater.inflate(R.layout.fragment_signup_account, container, false);

        mViewEmail = (EditText) mViewMain.findViewById(R.id.signup_account_email);
        mViewEmail.setText(mail);
        mViewEmail.setOnEditorActionListener(mEdittextActionListener);

        mViewPassword = (EditText) mViewMain.findViewById(R.id.signup_account_password);
        mViewPassword.setText(password);
        mViewPassword.setOnEditorActionListener(mEdittextActionListener);

        mViewBack = (ImageView) mViewMain.findViewById(R.id.fragment_back);
        mViewBack.setOnClickListener(mBackOnClickListener);

        return mViewMain;
    }

    @Override
    public void onPause() {
        if (mProcessDialog != null) {
            mProcessDialog.dismiss();
            mProcessDialog = null;
        }
        super.onPause();
    }

    @Override
    public ViewFragmentConfig getConfig() {
        return new ViewFragmentConfig("Sign up", false, false, false,
                ActivityMain.RESOURCE_IGNORE, R.mipmap.icon_left, ActivityMain.RESOURCE_HIDE);
    }

    @Override
    public void onToolbarAction1() {
        mActivityMain.popFragment();
    }

    private View.OnClickListener mBackOnClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            onToolbarAction1();
        }
    };

    private EditText.OnEditorActionListener mEdittextActionListener = new TextView.OnEditorActionListener() {
        @Override
        public boolean onEditorAction(TextView view, int actionId, KeyEvent event) {
            if (view == mViewPassword && actionId == EditorInfo.IME_ACTION_DONE) {
                mMail = mViewEmail.getText().toString();
                mPassword = mViewPassword.getText().toString();

                if (mMail.equals("") || mPassword.equals("")) {
                    Toast.makeText(mActivityMain,"Login failed.",Toast.LENGTH_SHORT).show();
                    //mActivityMain.selectFragment(FragmentSignupProfile.class.getName(), null);
                } else {
                    mProcessDialog = ProgressDialog.show(mActivityMain, "Processing", "Please wait...",true);
                    mActivityMain.mServiceMachine.userIsMailAvailableToRegister(mMailCheckListener, mMail);
                }
            }

            return false;
        }
    };

    ServerMachine.userIsMailAvailableToRegisterListener mMailCheckListener = new ServerMachine.userIsMailAvailableToRegisterListener() {
        @Override
        public void onValidState(boolean valid) {
            if (valid) {
                Bundle bundle = new Bundle();
                bundle.putString("MAIL", mMail);
                bundle.putString("PASSWORD", mPassword);

                mActivityMain.selectFragment(FragmentSignupProfile.class.getName(), bundle);
            } else {
                mActivityMain.mServiceMachine.userLogin(mLoginListener, mMail, mPassword);
            }
        }

        @Override
        public void onFail(int statusCode) {
            mProcessDialog.dismiss();
            Toast.makeText(mActivityMain,""+statusCode,Toast.LENGTH_SHORT).show();
        }
    };

    ServerMachine.userLoginListener mLoginListener = new ServerMachine.userLoginListener() {
        @Override
        public void onSuccess(int statusCode, ServerGson.user.login.response result) {
            mActivityMain.mConfig.setString(Config.KEY_AUTH_TOKEN, result.access_token);
            mActivityMain.mServiceMachine.setAuthToken(result.access_token);
            mActivityMain.mServiceMachine.userRetrieveUserProfile(mRetrieveUserProfileListener);
        }

        @Override
        public void onFail(int statusCode) {
            mProcessDialog.dismiss();
            Toast.makeText(mActivityMain,"Login failed("+statusCode+").",Toast.LENGTH_SHORT).show();
        }
    };

    ServerMachine.userRetrieveUserProfileListener mRetrieveUserProfileListener = new ServerMachine.userRetrieveUserProfileListener() {
        @Override
        public void onSuccess(int statusCode, ServerGson.user.retrieveUserProfile.response response) {
            mActivityMain.mOperator.ResetDatabase();
            mActivityMain.mOperator.UserAdd(
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
                kid.mFirstName = kidData.firstName;
                kid.mLastName = kidData.lastName;
                kid.mDateCreated = WatchOperator.getTimeStamp(kidData.dateCreated);
                kid.mMacId = kidData.macId;
                kid.mUserId = response.user.id;
                kid.mProfile = kidData.profile;
                kid.mBound = true;
                mActivityMain.mOperator.KidSetFocus(kid);
                mKidList.add(kid);
            }

            if (!response.user.profile.equals("")) {
                String name = response.user.profile;
                int pos = name.lastIndexOf(".");
                if (pos>0)
                    name = name.substring(0, pos);

                mActivityMain.mServiceMachine.getAvatar(mGetUserAvatarListener, name);
            } else {
                getKidAvatar(true);
            }
        }

        @Override
        public void onFail(int statusCode) {
            mProcessDialog.dismiss();
            Toast.makeText(mActivityMain,""+statusCode,Toast.LENGTH_SHORT).show();
        }
    };

    ServerMachine.getAvatarListener mGetUserAvatarListener = new ServerMachine.getAvatarListener() {
        @Override
        public void onSuccess(Bitmap avatar, String filename) {
            ServerMachine.createAvatarFile(avatar, filename);
            if (mKidList.isEmpty()) {
                mActivityMain.selectFragment(FragmentWatchHave.class.getName(), null);
            } else {
                getKidAvatar(true);
            }
        }

        @Override
        public void onFail(int statusCode) {
            mProcessDialog.dismiss();
            Toast.makeText(mActivityMain,""+statusCode,Toast.LENGTH_SHORT).show();
        }
    };

    private int mProcessKidAvatar = 0;

    private void getKidAvatar(boolean start) {
        if (start)
            mProcessKidAvatar = 0;
        else
            mProcessKidAvatar++;

        if (mProcessKidAvatar >= mKidList.size()) {
            mActivityMain.selectFragment(FragmentSyncNow.class.getName(), null);
        } else {
            while (mKidList.get(mProcessKidAvatar).mProfile.equals("")) {
                mProcessKidAvatar++;
                if (mProcessKidAvatar >= mKidList.size()) {
                    mActivityMain.selectFragment(FragmentSyncNow.class.getName(), null);
                    return;
                }
            }
            mActivityMain.mServiceMachine.getAvatar(mGetKidAvatarListener, mKidList.get(mProcessKidAvatar).mProfile);
        }
    }

    ServerMachine.getAvatarListener mGetKidAvatarListener = new ServerMachine.getAvatarListener() {
        @Override
        public void onSuccess(Bitmap avatar, String filename) {
            ServerMachine.createAvatarFile(avatar, filename);
            getKidAvatar(false);
        }

        @Override
        public void onFail(int statusCode) {
            mProcessDialog.dismiss();
            Toast.makeText(mActivityMain,""+statusCode,Toast.LENGTH_SHORT).show();
        }
    };

}
