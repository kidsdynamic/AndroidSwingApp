package com.kidsdynamic.swing.androidswingapp;

import android.os.Bundle;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

/**
 * Created by 03543 on 2017/1/25.
 */

public class FragmentProfileSearch extends ViewFragment {

    private ActivityMain mActivityMain;
    private View mViewMain;
    private ViewProgressCircle mViewProgress;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mActivityMain = (ActivityMain) getActivity();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        mViewMain = inflater.inflate(R.layout.fragment_profile_search, container, false);

        mViewProgress = (ViewProgressCircle) mViewMain.findViewById(R.id.profile_search_progress);
        mViewProgress.setOnProgressListener(mSearchProgressListener);

        Handler handle = new Handler();
        handle.post(new Runnable() {
            @Override
            public void run() {
                mViewProgress.start();
            }
        });

        return mViewMain;
    }

    @Override
    public ViewFragment.ViewFragmentConfig getConfig() {
        return new ViewFragment.ViewFragmentConfig("Password", true, true, false,
                ActivityMain.RESOURCE_IGNORE, R.mipmap.icon_left, ActivityMain.RESOURCE_HIDE);
    }

    @Override
    public void onToolbarAction1() {
        mActivityMain.popFragment();
    }

    private ViewProgressCircle.OnProgressListener mSearchProgressListener = new ViewProgressCircle.OnProgressListener() {
        @Override
        public void onProgress(ViewProgressCircle view, int progress, int total) {

        }
    };
}