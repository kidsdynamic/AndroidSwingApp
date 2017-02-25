package com.kidsdynamic.swing.androidswingapp;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

/**
 * Created by 03543 on 2017/1/30.
 */

public class FragmentProfileAdded extends ViewFragment {
    private ActivityMain mActivityMain;
    private View mViewMain;

    private Button mButtonProfile;
    private WatchContact.Kid mDevice;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mActivityMain = (ActivityMain) getActivity();

        mDevice = mActivityMain.mContactStack.isEmpty() ?
                new WatchContact.Kid() :
                (WatchContact.Kid) mActivityMain.mContactStack.pop();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        mViewMain = inflater.inflate(R.layout.fragment_profile_added, container, false);

        mButtonProfile = (Button) mViewMain.findViewById(R.id.profile_added_profile);
        mButtonProfile.setOnClickListener(mOnProfileListener);

        return mViewMain;
    }

    @Override
    public ViewFragmentConfig getConfig() {
        return new ViewFragmentConfig("", true, true, false,
                ActivityMain.RESOURCE_IGNORE, R.mipmap.icon_left, ActivityMain.RESOURCE_HIDE);
    }

    @Override
    public void onToolbarAction1() {
        mActivityMain.popFragment();
    }

    private Button.OnClickListener mOnProfileListener = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            mActivityMain.mContactStack.push(mDevice);
            mActivityMain.selectFragment(FragmentProfileKid.class.getName(), null);
        }
    };

}
