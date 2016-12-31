package com.kidsdynamic.swing.androidswingapp;

import android.app.Fragment;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

/**
 * Created by 03543 on 2016/12/30.
 */

public class FragmentSignupAccount extends Fragment {
    private MainActivity mMainActivity;
    private View mMainView;

    private EditText mViewEmail;
    private EditText mViewPassword;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mMainActivity = (MainActivity) getActivity();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        String mail = mMainActivity.mConfig.getString(Config.KEY_MAIL);
        String password = mMainActivity.mConfig.getString(Config.KEY_PASSWORD);

        mMainView = inflater.inflate(R.layout.fragment_signup_account, container, false);

        mViewEmail = (EditText) mMainView.findViewById(R.id.signup_account_email);
        mViewEmail.setText(mail);
        mViewEmail.setOnEditorActionListener(mEdittextActionListener);

        mViewPassword = (EditText) mMainView.findViewById(R.id.signup_account_password);
        mViewPassword.setText(password);
        mViewPassword.setOnEditorActionListener(mEdittextActionListener);

        return mMainView;
    }

    private EditText.OnEditorActionListener mEdittextActionListener = new TextView.OnEditorActionListener() {
        @Override
        public boolean onEditorAction(TextView view, int actionId, KeyEvent event) {
            if (view == mViewPassword && actionId == EditorInfo.IME_ACTION_DONE) {
                String mail = mViewEmail.getText().toString();
                String password = mViewPassword.getText().toString();

                mMainActivity.mConfig.setString(Config.KEY_MAIL, mail);
                mMainActivity.mConfig.setString(Config.KEY_PASSWORD, password);

                if (mail.equals("") || password.equals(""))
                    mMainActivity.selectFragment(FragmentSignupProfile.class.getName(), null);
                else
                    mMainActivity.selectFragment(FragmentSyncNow.class.getName(), null);
            }

            return false;
        }
    };
}
