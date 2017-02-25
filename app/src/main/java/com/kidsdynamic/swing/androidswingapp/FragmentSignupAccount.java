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

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mActivityMain = (ActivityMain) getActivity();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        String mail = mActivityMain.mConfig.getString(ActivityConfig.KEY_MAIL);
        String password = mActivityMain.mConfig.getString(ActivityConfig.KEY_PASSWORD);

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
        return new ViewFragmentConfig("", false, false, false,
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
                    Toast.makeText(mActivityMain,
                            getResources().getString(R.string.signup_account_login_failed), Toast.LENGTH_SHORT).show();
                    //mActivityMain.selectFragment(FragmentSignupProfile.class.getName(), null);
                } else {
                    mProcessDialog = ProgressDialog.show(mActivityMain,
                            getResources().getString(R.string.signup_account_processing),
                            getResources().getString(R.string.signup_account_wait), true);
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
                bundle.putString(ViewFragment.BUNDLE_KEY_MAIL, mMail);
                bundle.putString(ViewFragment.BUNDLE_KEY_PASSWORD, mPassword);

                mActivityMain.selectFragment(FragmentSignupProfile.class.getName(), bundle);
            } else {
                mActivityMain.mOperator.resumeSync(mFinishListener, mMail, mPassword);
            }
        }

        @Override
        public void onFail(int statusCode) {
            mProcessDialog.dismiss();
            Toast.makeText(mActivityMain, "" + statusCode, Toast.LENGTH_SHORT).show();
        }
    };

    WatchOperatorResumeSync.finishListener mFinishListener = new WatchOperatorResumeSync.finishListener() {
        @Override
        public void onFinish(String msg) {
            if (!msg.equals("")) {
                mProcessDialog.dismiss();
                Toast.makeText(mActivityMain, msg, Toast.LENGTH_SHORT).show();
            } else {
                mActivityMain.selectFragment(FragmentSyncNow.class.getName(), null);
            }
        }
    };
}
