package com.kidsdynamic.swing.androidswingapp;

import android.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

/**
 * Created by 03543 on 2016/12/19.
 */

public class FragmentDevice extends ViewFragment {
    private ActivityMain mActivityMain;
    private View mViewMain;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mActivityMain = (ActivityMain) getActivity();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        mViewMain = inflater.inflate(R.layout.fragment_device, container, false);

        return mViewMain;
    }

    @Override
    public ViewFragmentConfig getConfig() {
        return new ViewFragmentConfig("Device", true, true,
                R.mipmap.city_illinois, ActivityMain.RESOURCE_HIDE, ActivityMain.RESOURCE_HIDE);
    }
}
