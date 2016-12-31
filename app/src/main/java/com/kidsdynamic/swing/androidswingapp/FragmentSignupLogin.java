package com.kidsdynamic.swing.androidswingapp;

import android.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Toast;

/**
 * Created by 03543 on 2016/12/30.
 */

public class FragmentSignupLogin extends Fragment {
    private MainActivity mMainActivity;
    private View mMainView;

    private Button mButtonLogin;
    private Button mButtonFacebook;
    private Button mButtonGoogle;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mMainActivity = (MainActivity) getActivity();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        mMainView = inflater.inflate(R.layout.fragment_signup_login, container, false);

        mButtonLogin = (Button) mMainView.findViewById(R.id.signup_login_login);
        mButtonLogin.setOnClickListener(mOnLoginListener);

        mButtonFacebook = (Button) mMainView.findViewById(R.id.signup_login_facebook);
        mButtonFacebook.setOnClickListener(mOnFacebookListener);

        mButtonGoogle = (Button) mMainView.findViewById(R.id.signup_login_google);
        mButtonGoogle.setOnClickListener(mOnGoogleListener);

        return mMainView;
    }

    private Button.OnClickListener mOnLoginListener = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            String mail = mMainActivity.mConfig.getString(Config.KEY_MAIL);
            String password = mMainActivity.mConfig.getString(Config.KEY_PASSWORD);

            if (mail.equals("") || password.equals(""))
                mMainActivity.selectFragment(FragmentSignupAccount.class.getName(), null);
            else
                mMainActivity.selectFragment(FragmentSyncNow.class.getName(), null);
        }
    };

    private Button.OnClickListener mOnFacebookListener = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            Toast.makeText(mMainActivity, "Login failed!", Toast.LENGTH_SHORT).show();
            mMainActivity.selectFragment(FragmentSignupAccount.class.getName(), null);
        }
    };

    private Button.OnClickListener mOnGoogleListener = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            Toast.makeText(mMainActivity, "Login failed!", Toast.LENGTH_SHORT).show();
            mMainActivity.selectFragment(FragmentSignupAccount.class.getName(), null);
        }
    };
}
