package com.kidsdynamic.swing.androidswingapp;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;

import java.util.ArrayList;

/**
 * Created by 03543 on 2016/12/31.
 */

public class FragmentWatchSelect extends ViewFragment {
    private ActivityMain mActivityMain;
    private View mViewMain;

    private Button mButtonDashboard;
    private LinearLayout mViewContainer;
    private ImageView mViewBack;

    private ArrayList<WatchContact.Kid> mDeviceList;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mActivityMain = (ActivityMain) getActivity();

        mDeviceList = new ArrayList<>();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        mViewMain = inflater.inflate(R.layout.fragment_watch_select, container, false);

        mViewContainer = (LinearLayout) mViewMain.findViewById(R.id.watch_select_container);

        mViewBack = (ImageView) mViewMain.findViewById(R.id.fragment_back);
        mViewBack.setOnClickListener(mBackOnClickListener);

        mButtonDashboard = (Button) mViewMain.findViewById(R.id.watch_select_dashboard);
        mButtonDashboard.setOnClickListener(mOnDashboardListener);

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

    @Override
    public void onResume() {
        super.onResume();

        mDeviceList.clear();
        while (!mActivityMain.mContactStack.isEmpty())
            mDeviceList.add((WatchContact.Kid) mActivityMain.mContactStack.pop());

        for (WatchContact.Kid device : mDeviceList)
            addWatch(device);
    }

    private View.OnClickListener mBackOnClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            onToolbarAction1();
        }
    };

    private Button.OnClickListener mOnDashboardListener = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            mActivityMain.clearFragment(FragmentDashboardEmotion.class.getName(), null);
        }
    };

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
                mActivityMain.selectFragment(FragmentWatchRegistered.class.getName(), null);
            else
                mActivityMain.selectFragment(FragmentWatchAdded.class.getName(), null);
        }
    };
}
