package com.kidsdynamic.swing.androidswingapp;

import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

/**
 * Created by 03543 on 2016/12/30.
 */

public class FragmentSignupAccount extends ViewFragment {
    private ActivityMain mActivityMain;
    private View mViewMain;

    private EditText mViewEmail;
    private EditText mViewPassword;
    private ImageView mViewBack;

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
    public ViewFragmentConfig getConfig() {
        return new ViewFragmentConfig("Sign up", false, false,
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
                String mail = mViewEmail.getText().toString();
                String password = mViewPassword.getText().toString();

                mActivityMain.mConfig.setString(Config.KEY_MAIL, mail);
                mActivityMain.mConfig.setString(Config.KEY_PASSWORD, password);

                if (mail.equals("") || password.equals("")) {
                    mActivityMain.selectFragment(FragmentSignupProfile.class.getName(), null);
                } else {
                    mActivityMain.mServiceMachine.userLogin(mLoginListener, mail, password);
                    //mActivityMain.selectFragment(FragmentSyncNow.class.getName(), null);
                }
            }

            return false;
        }
    };

    ServerMachine.ResponseListener mLoginListener = new ServerMachine.ResponseListener() {
        @Override
        public void onResponse(boolean success, int resultCode, String result) {
            Log.d("onResponse", "" + success + " resultCode " + resultCode + " result " + result);
            mActivityMain.selectFragment(FragmentSyncNow.class.getName(), null);
        }
    };

}
