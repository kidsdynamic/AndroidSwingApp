package com.kidsdynamic.swing.androidswingapp;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

/**
 * Created by 03543 on 2017/1/24.
 */

public class FragmentProfileKid extends ViewFragment {

    private ActivityMain mActivityMain;
    private View mViewMain;
    private ViewCircle mViewPhoto;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mActivityMain = (ActivityMain) getActivity();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        mViewMain = inflater.inflate(R.layout.fragment_profile_kid, container, false);

        mViewPhoto = (ViewCircle) mViewMain.findViewById(R.id.profile_kid_photo);

        return mViewMain;
    }

    @Override
    public ViewFragmentConfig getConfig() {
        return new ViewFragmentConfig("Profile", true, true, false,
                ActivityMain.RESOURCE_IGNORE, R.mipmap.icon_left, R.mipmap.icon_ok);
    }

    @Override
    public void onToolbarAction1() {
        mActivityMain.popFragment();
    }

    @Override
    public void onToolbarAction2() {
        mActivityMain.selectFragment(FragmentProfileOption.class.getName(), null);
    }
}
