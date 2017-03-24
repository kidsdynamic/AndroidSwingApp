package com.kidsdynamic.swing.androidswingapp;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

/**
 * Created by 03543 on 2016/12/30.
 */

public class FragmentSignupLogin extends ViewFragment {
    private ActivityMain mActivityMain;
    private View mViewMain;

    private Button mButtonLogin;
    private Button mButtonFacebook;
    private Button mButtonGoogle;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mActivityMain = (ActivityMain) getActivity();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        mViewMain = inflater.inflate(R.layout.fragment_signup_login, container, false);

        mButtonLogin = (Button) mViewMain.findViewById(R.id.signup_login_login);
        mButtonLogin.setOnClickListener(mOnLoginListener);

        mButtonFacebook = (Button) mViewMain.findViewById(R.id.signup_login_facebook);
        mButtonFacebook.setOnClickListener(mOnFacebookListener);

        mButtonGoogle = (Button) mViewMain.findViewById(R.id.signup_login_google);
        mButtonGoogle.setOnClickListener(mOnGoogleListener);

        return mViewMain;
    }

    @Override
    public ViewFragmentConfig getConfig() {
        return new ViewFragmentConfig("", false, false, false,
                ActivityMain.RESOURCE_IGNORE, ActivityMain.RESOURCE_IGNORE, ActivityMain.RESOURCE_IGNORE);
    }

    private Button.OnClickListener mOnLoginListener = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            mActivityMain.selectFragment(FragmentSignupAccount.class.getName(), null);
        }
    };

    private Button.OnClickListener mOnFacebookListener = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            mActivityMain.clearFragment(FragmentDashboardMain.class.getName(), null);
        }
    };

    private Button.OnClickListener mOnGoogleListener = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            mActivityMain.clearFragment(FragmentDashboardMain.class.getName(), null);
        }
    };
}
