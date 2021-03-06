package com.kidsdynamic.swing.androidswingapp;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;

/**
 * Created by 03543 on 2016/12/31.
 */

public class FragmentWatchAdded extends ViewFragment {
    private ActivityMain mActivityMain;
    private View mViewMain;

    private Button mButtonProfile;
    private ImageView mViewBack;
    private WatchContact.Kid mDevice;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mActivityMain = (ActivityMain) getActivity();

        mDevice = mActivityMain.mContactStack.isEmpty() ?
                new WatchContact.Kid() :
                (WatchContact.Kid) mActivityMain.mContactStack.pop();

        Log.d("Register Device", mDevice.mFirmwareVersion);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        mViewMain = inflater.inflate(R.layout.fragment_watch_added, container, false);

        mButtonProfile = (Button) mViewMain.findViewById(R.id.watch_added_profile);
        mButtonProfile.setOnClickListener(mOnProfileListener);

        mViewBack = (ImageView) mViewMain.findViewById(R.id.fragment_back);
        mViewBack.setOnClickListener(mBackOnClickListener);

        return mViewMain;
    }

    @Override
    public ViewFragmentConfig getConfig() {
        return new ViewFragmentConfig("", false, false, false,
                ActivityMain.RESOURCE_IGNORE, ActivityMain.RESOURCE_IGNORE, ActivityMain.RESOURCE_IGNORE);
    }

    @Override
    public void onToolbarAction1() {
        mActivityMain.popFragment();
    }

    private View.OnClickListener mBackOnClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            onToolbarAction1();
        }
    };

    private Button.OnClickListener mOnProfileListener = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            mActivityMain.mContactStack.push(mDevice);
            mActivityMain.selectFragment(FragmentWatchProfile.class.getName(), null);
        }
    };

}
