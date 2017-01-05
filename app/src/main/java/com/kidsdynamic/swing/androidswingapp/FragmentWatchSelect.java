package com.kidsdynamic.swing.androidswingapp;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;

import java.util.ArrayList;

/**
 * Created by 03543 on 2016/12/31.
 */

public class FragmentWatchSelect extends ViewFragment {
    private ActivityMain mActivityMain;
    private View mViewMain;

    private Button mButtonDashboard;
    private ViewWatchContactList mViewWatchContactList;
    private ImageView mViewBack;

    private ArrayList<WatchContact.Device> mDeviceList;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mActivityMain = (ActivityMain) getActivity();

        mDeviceList = (ArrayList<WatchContact.Device>) getArguments().getSerializable(ViewFragment.BUNDLE_KEY_DEVICE_LIST);
        if (mDeviceList == null)
            mDeviceList = new ArrayList<>();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        mViewMain = inflater.inflate(R.layout.fragment_watch_select, container, false);

        mViewWatchContactList = (ViewWatchContactList) mViewMain.findViewById(R.id.watch_select_list);
        mViewWatchContactList.setOnButtonClickListener(mButtonClickListener);

        mViewBack = (ImageView) mViewMain.findViewById(R.id.fragment_back);
        mViewBack.setOnClickListener(mBackOnClickListener);

        mButtonDashboard = (Button) mViewMain.findViewById(R.id.watch_select_dashboard);
        mButtonDashboard.setOnClickListener(mOnDashboardListener);

        for (WatchContact.Device device : mDeviceList) {
            mViewWatchContactList.addItem(device);
        }

        return mViewMain;
    }

    @Override
    public ViewFragmentConfig getConfig() {
        return new ViewFragmentConfig("Watch", false, false,
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

    private Button.OnClickListener mOnDashboardListener = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
//            mActivityMain.selectFragment(FragmentSignupLogin.class.getName(), null);
        }
    };

    private ViewWatchContactList.OnButtonClickListener mButtonClickListener = new ViewWatchContactList.OnButtonClickListener() {
        @Override
        public void onClick(ViewWatchContact contact, int position, int button) {

            WatchContact.Device device = (WatchContact.Device) contact.getItem();
            if (device.mBound)
                mActivityMain.selectFragment(FragmentWatchRegistered.class.getName(), null);
            else
                mActivityMain.selectFragment(FragmentWatchKid.class.getName(), null);
        }
    };
}
