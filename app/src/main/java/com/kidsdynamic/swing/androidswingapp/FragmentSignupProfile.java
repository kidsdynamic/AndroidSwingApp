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

public class FragmentSignupProfile extends ViewFragment {
    private ActivityMain mActivityMain;
    private View mViewMain;

    private EditText mViewFirstName;
    private EditText mViewLastName;
    private EditText mViewPhone;
    private EditText mViewZip;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mActivityMain = (ActivityMain) getActivity();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        mViewMain = inflater.inflate(R.layout.fragment_signup_profile, container, false);

        mViewFirstName = (EditText) mViewMain.findViewById(R.id.signup_profile_first);
        mViewFirstName.setOnEditorActionListener(mEdittextActionListener);

        mViewLastName = (EditText) mViewMain.findViewById(R.id.signup_profile_last);
        mViewLastName.setOnEditorActionListener(mEdittextActionListener);

        mViewPhone = (EditText) mViewMain.findViewById(R.id.signup_profile_phone);
        mViewPhone.setOnEditorActionListener(mEdittextActionListener);

        mViewZip = (EditText) mViewMain.findViewById(R.id.signup_profile_zip);
        mViewZip.setOnEditorActionListener(mEdittextActionListener);

        return mViewMain;
    }

    private EditText.OnEditorActionListener mEdittextActionListener = new TextView.OnEditorActionListener() {
        @Override
        public boolean onEditorAction(TextView view, int actionId, KeyEvent event) {
            if (view == mViewZip && actionId == EditorInfo.IME_ACTION_DONE) {
                mActivityMain.mConfig.setString(Config.KEY_FIRST_NAME, mViewFirstName.getText().toString());
                mActivityMain.mConfig.setString(Config.KEY_LAST_NAME, mViewLastName.getText().toString());
                mActivityMain.mConfig.setString(Config.KEY_PHONE, mViewPhone.getText().toString());
                mActivityMain.mConfig.setString(Config.KEY_ZIP, mViewZip.getText().toString());

                mActivityMain.selectFragment(FragmentWatchHave.class.getName(), null);
            }

            return false;
        }
    };

}