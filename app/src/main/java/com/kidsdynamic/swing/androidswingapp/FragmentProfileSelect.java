package com.kidsdynamic.swing.androidswingapp;

import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import java.util.ArrayList;

/**
 * Created by 03543 on 2017/1/30.
 */

public class FragmentProfileSelect extends ViewFragment {
    private ActivityMain mActivityMain;
    private View mViewMain;

    private LinearLayout mViewContainer;

    private ArrayList<WatchContact.Kid> mDeviceList;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mActivityMain = (ActivityMain) getActivity();

        mDeviceList = new ArrayList<>();
        while(!mActivityMain.mContactStack.isEmpty())
            mDeviceList.add((WatchContact.Kid)mActivityMain.mContactStack.pop());
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        mViewMain = inflater.inflate(R.layout.fragment_profile_select, container, false);

        mViewContainer = (LinearLayout) mViewMain.findViewById(R.id.profile_select_container);

        for (WatchContact.Kid device : mDeviceList) {
            addWatch(device);
        }

        return mViewMain;
    }

    @Override
    public ViewFragmentConfig getConfig() {
        return new ViewFragmentConfig(
                getResources().getString(R.string.title_search_device), true, true, false,
                ActivityMain.RESOURCE_IGNORE, R.mipmap.icon_left, ActivityMain.RESOURCE_HIDE);
    }

    @Override
    public void onToolbarAction1() {
        mActivityMain.popFragment();
    }

    private void addWatch(WatchContact.Kid device) {
        View view = WatchContact.inflateBind(mActivityMain, device);
        view.setOnClickListener(mDeviceClickListener);

        mViewContainer.addView(view);
    }

    private View.OnClickListener mDeviceClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            WatchContact.Kid device = (WatchContact.Kid) view.getTag();

            mActivityMain.mContactStack.push(device);
            if (device.mBound)
                mActivityMain.selectFragment(FragmentProfileRegistered.class.getName(), null);
            else
                mActivityMain.selectFragment(FragmentProfileAdded.class.getName(), null);
        }
    };
}
