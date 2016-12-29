package com.kidsdynamic.swing.androidswingapp;

import android.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

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
        mButtonLogin.setOnClickListener(mOnButtonListener);

        mButtonFacebook = (Button) mMainView.findViewById(R.id.signup_login_facebook);
        mButtonFacebook.setOnClickListener(mOnFacebookListener);

        mButtonGoogle = (Button) mMainView.findViewById(R.id.signup_login_facebook);
        mButtonGoogle.setOnClickListener(mOnGoogleListener);

        return mMainView;
    }

    private Button.OnClickListener mOnButtonListener = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            mMainActivity.selectFragment(FragmentSignupCaregiver.class.getName(), null);
        }
    };

    private Button.OnClickListener mOnFacebookListener = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
        }
    };

    private Button.OnClickListener mOnGoogleListener = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
        }
    };
}
