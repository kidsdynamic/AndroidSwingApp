package com.kidsdynamic.swing.androidswingapp;

import android.app.Dialog;
import android.app.ProgressDialog;
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
                ActivityMain.RESOURCE_IGNORE, ActivityMain.RESOURCE_IGNORE, ActivityMain.RESOURCE_IGNORE);
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
            mActivityMain.mConfig.setString(Config.KEY_MAIL, mMail);
            mActivityMain.mConfig.setString(Config.KEY_PASSWORD, mPassword);
            mActivityMain.mConfig.setString(Config.KEY_FIRST_NAME, response.user.firstName);
            mActivityMain.mConfig.setString(Config.KEY_LAST_NAME, response.user.lastName);
            mActivityMain.mConfig.setString(Config.KEY_PHONE, response.user.phoneNumber);
            mActivityMain.mConfig.setString(Config.KEY_ZIP, response.user.zipCode);
        }

        @Override
        public void onFail(int statusCode) {
            mProcessDialog.dismiss();
            Toast.makeText(mActivityMain,""+statusCode,Toast.LENGTH_SHORT).show();
        }
    };
}
