package com.kidsdynamic.swing.androidswingapp;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;

/**
 * Created by 03543 on 2017/1/24.
 */

public class FragmentProfileOption extends ViewFragment {

    private ActivityMain mActivityMain;
    private View mViewMain;
    private View mViewPassword;
    private View mViewProfile;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mActivityMain = (ActivityMain) getActivity();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        mViewMain = inflater.inflate(R.layout.fragment_profile_option, container, false);

        mViewProfile = mViewMain.findViewById(R.id.profile_option_profile);
        mViewProfile.setOnClickListener(mProfileListener);

        mViewPassword = mViewMain.findViewById(R.id.profile_option_password);
        mViewPassword.setOnClickListener(mPasswordListener);

        TextView view = (TextView) mViewMain.findViewById(R.id.profile_option_version);
        view.setText(BuildConfig.VERSION_NAME);

        return mViewMain;
    }

    @Override
    public ViewFragmentConfig getConfig() {
        return new ViewFragmentConfig("Option", true, true, false,
                ActivityMain.RESOURCE_IGNORE, R.mipmap.icon_left, ActivityMain.RESOURCE_HIDE);
    }

    @Override
    public void onToolbarAction1() {
        mActivityMain.popFragment();
    }

    private View.OnClickListener mProfileListener = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            mActivityMain.selectFragment(FragmentProfileKid.class.getName(), null);
        }
    };

    private View.OnClickListener mPasswordListener = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            mActivityMain.selectFragment(FragmentProfilePassword.class.getName(), null);
        }
    };
}
