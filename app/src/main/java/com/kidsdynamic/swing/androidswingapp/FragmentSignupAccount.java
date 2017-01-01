package com.kidsdynamic.swing.androidswingapp;

import android.app.Fragment;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;
import android.widget.TextView;

/**
 * Created by 03543 on 2016/12/30.
 */

public class FragmentSignupAccount extends Fragment {
    private ActivityMain mActivityMain;
    private View mViewMain;

    private EditText mViewEmail;
    private EditText mViewPassword;

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

        return mViewMain;
    }

    private EditText.OnEditorActionListener mEdittextActionListener = new TextView.OnEditorActionListener() {
        @Override
        public boolean onEditorAction(TextView view, int actionId, KeyEvent event) {
            if (view == mViewPassword && actionId == EditorInfo.IME_ACTION_DONE) {
                String mail = mViewEmail.getText().toString();
                String password = mViewPassword.getText().toString();

                mActivityMain.mConfig.setString(Config.KEY_MAIL, mail);
                mActivityMain.mConfig.setString(Config.KEY_PASSWORD, password);

                if (mail.equals("") || password.equals(""))
                    mActivityMain.selectFragment(FragmentSignupProfile.class.getName(), null);
                else
                    mActivityMain.selectFragment(FragmentSyncNow.class.getName(), null);
            }

            return false;
        }
    };
}
