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
    private ViewCircle mViewProgress;
    private int mTimeOut;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mActivityMain = (ActivityMain) getActivity();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        mViewMain = inflater.inflate(R.layout.fragment_profile_search, container, false);

        mViewProgress = (ViewCircle) mViewMain.findViewById(R.id.profile_search_progress);
        mViewProgress.setOnProgressListener(mSearchProgressListener);
        mTimeOut = 40;
        mViewProgress.setStrokeBeginEnd(0, 0); // For test, remove me later
        mViewProgress.startProgress(250, -1, -1);

        return mViewMain;
    }

    @Override
    public ViewFragment.ViewFragmentConfig getConfig() {
        return new ViewFragment.ViewFragmentConfig("Search Device", true, true, false,
                ActivityMain.RESOURCE_IGNORE, R.mipmap.icon_left, ActivityMain.RESOURCE_HIDE);
    }

    @Override
    public void onToolbarAction1() {
        mActivityMain.popFragment();
    }

    private ViewCircle.OnProgressListener mSearchProgressListener = new ViewCircle.OnProgressListener() {
        @Override
        public void onProgress(ViewCircle view, int begin, int end) {
            mTimeOut--;
            if(mTimeOut == 0)
                mActivityMain.selectFragment(FragmentProfileSelect.class.getName(), null);
        }
    };
}