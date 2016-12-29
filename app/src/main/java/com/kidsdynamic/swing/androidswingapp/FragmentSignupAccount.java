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
        mMainView = inflater.inflate(R.layout.fragment_signup_account, container, false);

        mViewEmail = (EditText) mMainView.findViewById(R.id.signup_account_email);
        mViewEmail.setOnEditorActionListener(mEdittextActionListener);

        mViewPassword = (EditText) mMainView.findViewById(R.id.signup_account_password);
        mViewPassword.setOnEditorActionListener(mEdittextActionListener);

        return mMainView;
    }

    private Button.OnClickListener mOnButtonListener = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            mMainActivity.selectFragment(FragmentSignupCaregiver.class.getName(), null);
        }
    };

    private EditText.OnEditorActionListener mEdittextActionListener = new TextView.OnEditorActionListener() {
        @Override
        public boolean onEditorAction(TextView view, int actionId, KeyEvent event) {
            if(view == mViewPassword && actionId== EditorInfo.IME_ACTION_DONE) {

            }

            return false;
        }
    };
}
