package com.kidsdynamic.swing.androidswingapp;

import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import java.util.ArrayList;

/**
 * Created by 03543 on 2017/1/15.
 */

public class FragmentSyncSelect extends ViewFragment {
    private ActivityMain mActivityMain;
    private View mViewMain;
    LinearLayout mViewContainer;

    ArrayList<WatchContact.Device> mDeviceList;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mActivityMain = (ActivityMain) getActivity();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        mViewMain = inflater.inflate(R.layout.fragment_sync_select, container, false);

        mViewContainer = (LinearLayout) mViewMain.findViewById(R.id.sync_select_container);

        mDeviceList = new ArrayList<>();

        // todo: load binded devices from server
        mDeviceList.add(new WatchContact.Device(BitmapFactory.decodeResource(getResources(), R.mipmap.monster_green), "Kid 001", true));
        mDeviceList.add(new WatchContact.Device(BitmapFactory.decodeResource(getResources(), R.mipmap.monster_yellow), "Kid 002", true));
        mDeviceList.add(new WatchContact.Device(BitmapFactory.decodeResource(getResources(), R.mipmap.monster_purple), "Kid XX3", true));

        for (WatchContact.Device device : mDeviceList)
            addDevice(device);

        // todo: select current focused kid
        setSelected(1);

        return mViewMain;
    }

    @Override
    public ViewFragmentConfig getConfig() {
        return new ViewFragmentConfig("Sync", true, true, false,
                R.mipmap.city_florida, R.mipmap.icon_left, ActivityMain.RESOURCE_HIDE);
    }

    @Override
    public void onToolbarAction1() {
        mActivityMain.popFragment();
    }

    private void addDevice(WatchContact.Device device) {
        ViewPhotoContact contact = new ViewPhotoContact(mActivityMain);

        contact.load(device);
        contact.setSelected(false);
        contact.setOnClickListener(mDeviceClickListener);

        mViewContainer.addView(contact);
    }

    private void setSelected(int position) {
        int count = mViewContainer.getChildCount();

        for (int idx = 0; idx < count; idx++) {
            ViewPhotoContact contact = (ViewPhotoContact) mViewContainer.getChildAt(idx);
            contact.setSelected(idx == position);
        }
    }

    private RelativeLayout.OnClickListener mDeviceClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            ViewPhotoContact contact = (ViewPhotoContact) view;
            WatchContact.Device device = (WatchContact.Device) contact.getItem();

            setSelected(mDeviceList.indexOf(device));

            // todo: attach device context to FragmentSyncSearch
            Bundle bundle = new Bundle();
            bundle.putSerializable(ViewFragment.BUNDLE_KEY_DEVICE, device);

            mActivityMain.selectFragment(FragmentSyncSearch.class.getName(), bundle);
        }
    };
}
