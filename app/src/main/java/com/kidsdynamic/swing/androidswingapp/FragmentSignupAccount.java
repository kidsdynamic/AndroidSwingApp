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
import android.widget.Toast;

import com.google.gson.Gson;
import com.google.gson.JsonObject;

import org.json.JSONObject;

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
                mMail = mViewEmail.getText().toString();
                mPassword = mViewPassword.getText().toString();

                if (mMail.equals("") || mPassword.equals("")) {
                    mActivityMain.selectFragment(FragmentSignupProfile.class.getName(), null);
                } else {
                    mActivityMain.mServiceMachine.userLogin(mLoginListener, mMail, mPassword);
                }
            }

            return false;
        }
    };

    private String mMail = "";
    private String mPassword = "";

    ServerMachine.ResponseListener mLoginListener = new ServerMachine.ResponseListener() {
        @Override
        public void onResponse(boolean success, int resultCode, String result) {
            Log.d("onResponse", "[" + success + "](" + resultCode + ")" + result);

            if (resultCode==200) {
                String auth_token = "";
                try {
                    JSONObject jsonObject = new JSONObject(result);
                    auth_token = jsonObject.getString("access_token");
                } catch (Exception e) {
                    e.printStackTrace();
                }
                mActivityMain.mConfig.setString(Config.KEY_AUTH_TOKEN, auth_token);
                mActivityMain.mServiceMachine.setAuthToken(auth_token);
                mActivityMain.mServiceMachine.userRetrieveUserProfile(mRetrieveUserProfileListener);

            } else if (resultCode==400) {
                mActivityMain.mServiceMachine.userIsMailAvailableToRegister(mMailCheckListener, mMail);
            } else {
                Toast.makeText(mActivityMain,"result",Toast.LENGTH_SHORT).show();
            }
        }
    };

    ServerMachine.ResponseListener mRetrieveUserProfileListener = new ServerMachine.ResponseListener() {
        @Override
        public void onResponse(boolean success, int resultCode, String result) {
            Log.d("onResponse", "[" + success + "](" + resultCode + ")" + result);
            if (resultCode==200) {
                mActivityMain.mConfig.setString(Config.KEY_MAIL, mMail);
                mActivityMain.mConfig.setString(Config.KEY_PASSWORD, mPassword);
                try {
                    JSONObject jsonObject = new JSONObject(result);
                    JSONObject user = new JSONObject(jsonObject.getString("user"));

                    mActivityMain.mConfig.setString(Config.KEY_FIRST_NAME, user.getString("firstName"));
                    mActivityMain.mConfig.setString(Config.KEY_LAST_NAME, user.getString("lastName"));
                    mActivityMain.mConfig.setString(Config.KEY_PHONE, user.getString("phoneNumber"));
                    mActivityMain.mConfig.setString(Config.KEY_ZIP, user.getString("zipCode"));

                } catch (Exception e) {
                    e.printStackTrace();
                }

                mActivityMain.selectFragment(FragmentSyncNow.class.getName(), null);
            } else if (resultCode==400) {
                Toast.makeText(mActivityMain,"result",Toast.LENGTH_SHORT).show();
            } else if (resultCode==500) {
                Toast.makeText(mActivityMain,"result",Toast.LENGTH_SHORT).show();
            } else {

            }
        }
    };

    ServerMachine.ResponseListener mMailCheckListener = new ServerMachine.ResponseListener() {
        @Override
        public void onResponse(boolean success, int resultCode, String result) {
            Log.d("onResponse", "[" + success + "](" + resultCode + ")" + result);
            if (resultCode==200) {
                //mActivityMain.mConfig.setString(Config.KEY_MAIL, mMail);
                //mActivityMain.mConfig.setString(Config.KEY_PASSWORD, mPassword);
                Bundle bundle = new Bundle();
                bundle.putString("MAIL", mMail);
                bundle.putString("PASSWORD", mPassword);

                mActivityMain.selectFragment(FragmentSignupProfile.class.getName(), bundle);
            } else if (resultCode==409) {
                Toast.makeText(mActivityMain,"Login failed or the email is already registered.",Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(mActivityMain,"result",Toast.LENGTH_SHORT).show();
            }
        }
    };

}
