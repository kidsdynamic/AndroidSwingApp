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

        if (getArguments() != null) {
            mDeviceList = (ArrayList<WatchContact.Kid>) getArguments().getSerializable(ViewFragment.BUNDLE_KEY_CONTACT_LIST);
        }
        if (mDeviceList == null)
            mDeviceList = new ArrayList<>();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        mViewMain = inflater.inflate(R.layout.fragment_profile_select, container, false);

        mViewContainer = (LinearLayout) mViewMain.findViewById(R.id.profile_select_container);

        for (WatchContact.Kid device : mDeviceList) {
            addWatch(device);
        }

        // Test
        addWatch(new WatchContact.Kid(BitmapFactory.decodeResource(getResources(), R.mipmap.monster_purple), "Purple", false));
        addWatch(new WatchContact.Kid(BitmapFactory.decodeResource(getResources(), R.mipmap.monster_yellow), "Monster Yellow", true));
        addWatch(new WatchContact.Kid(BitmapFactory.decodeResource(getResources(), R.mipmap.monster_green), "Green Monster", false));
        /////////////

        return mViewMain;
    }

    @Override
    public ViewFragmentConfig getConfig() {
        return new ViewFragmentConfig("Search Device", true, true, false,
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

            Bundle bundle = new Bundle();
            bundle.putSerializable(ViewFragment.BUNDLE_KEY_CONTACT, device);

            if (device.mBound)
                mActivityMain.selectFragment(FragmentProfileRegistered.class.getName(), bundle);
            else
                mActivityMain.selectFragment(FragmentProfileAdded.class.getName(), bundle);
        }
    };
}
