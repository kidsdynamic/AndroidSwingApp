package com.kidsdynamic.swing.androidswingapp;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

/**
 * Created by 03543 on 2016/12/19.
 */

public class FragmentProfileMain extends ViewFragment {
    private ActivityMain mActivityMain;
    private View mViewMain;
    private ViewPhoto mViewPhoto;
    private ViewPhoto mViewDeviceAdd;
    private ViewPhoto mViewSharedAdd;
    private TextView mViewName;
    private LinearLayout mViewDeviceContainer;
    private LinearLayout mViewSharedContainer;
    private Button mViewLogout;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mActivityMain = (ActivityMain) getActivity();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        mViewMain = inflater.inflate(R.layout.fragment_profile_main, container, false);

        mViewPhoto = (ViewPhoto)mViewMain.findViewById(R.id.profile_main_photo);
        mViewName = (TextView)mViewMain.findViewById(R.id.profile_main_name);

        mViewLogout = (Button)mViewMain.findViewById(R.id.profile_main_logout);
        mViewLogout.setOnClickListener(mLogoutListener);

        mViewDeviceContainer = (LinearLayout)mViewMain.findViewById(R.id.profile_main_device_container);
        mViewDeviceAdd = (ViewPhoto)mViewMain.findViewById(R.id.profile_main_device_add);
        mViewDeviceAdd.setOnClickListener(mAddDeviceListener);

        mViewSharedContainer = (LinearLayout)mViewMain.findViewById(R.id.profile_main_shared_container);
        mViewDeviceAdd = (ViewPhoto)mViewMain.findViewById(R.id.profile_main_shared_add);
        mViewDeviceAdd.setOnClickListener(mAddSharedListener);

        return mViewMain;
    }

    @Override
    public ViewFragmentConfig getConfig() {
        return new ViewFragmentConfig("Profile", true, true, true,
                ActivityMain.RESOURCE_IGNORE, R.mipmap.icon_edit, R.mipmap.icon_settings);
    }

    @Override
    public void onToolbarAction1() {
    }

    @Override
    public void onToolbarAction2() {
    }

    @Override
    public void onToolbarTitle() {
    }

    private View.OnClickListener mAddDeviceListener = new View.OnClickListener() {
        @Override
        public void onClick(View view) {

        }
    };

    private View.OnClickListener mAddSharedListener = new View.OnClickListener() {
        @Override
        public void onClick(View view) {

        }
    };

    private View.OnClickListener mLogoutListener = new View.OnClickListener() {
        @Override
        public void onClick(View view) {

        }
    };
}
