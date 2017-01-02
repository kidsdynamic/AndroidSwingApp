package com.kidsdynamic.swing.androidswingapp;

import android.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

/**
 * Created by 03543 on 2016/12/19.
 */

public class FragmentDashboard extends ViewFragment {
    private ActivityMain mActivityMain;
    private View mViewMain;

    private int mBackgroundRes;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mActivityMain = (ActivityMain) getActivity();

        mBackgroundRes = R.mipmap.city_florida;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        mViewMain = inflater.inflate(R.layout.fragment_dashboard, container, false);

        return mViewMain;
    }

    @Override
    public ViewFragmentConfig getConfig() {
        return new ViewFragmentConfig("Dashboard", true, true,
                mBackgroundRes, ActivityMain.RESOURCE_HIDE, ActivityMain.RESOURCE_HIDE);
    }
}
