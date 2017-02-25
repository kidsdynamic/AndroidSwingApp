package com.kidsdynamic.swing.androidswingapp;

import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;

import java.util.List;

/**
 * Created by 03543 on 2017/1/15.
 */

public class FragmentSyncSelect extends ViewFragment {
    private ActivityMain mActivityMain;
    private View mViewMain;
    LinearLayout mViewContainer;

    List<WatchContact.Kid> mDeviceList;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mActivityMain = (ActivityMain) getActivity();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        mViewMain = inflater.inflate(R.layout.fragment_sync_select, container, false);

        mViewContainer = (LinearLayout) mViewMain.findViewById(R.id.sync_select_container);

        return mViewMain;
    }

    @Override
    public void onResume() {
        super.onResume();

        mDeviceList = mActivityMain.mOperator.getKids();
        for (WatchContact.Kid device : mDeviceList)
            addDevice(device);

        WatchContact.Kid focusKid = mActivityMain.mOperator.getFocusKid();
        for (int idx = 0; idx < mDeviceList.size(); idx++) {
            if (focusKid.mId == mDeviceList.get(idx).mId && focusKid.mUserId == mDeviceList.get(idx).mUserId) {
                setSelected(idx);
                break;
            }
        }
    }

    @Override
    public ViewFragmentConfig getConfig() {
        return new ViewFragmentConfig(
                getResources().getString(R.string.title_sync), true, true, false,
                R.mipmap.city_florida, R.mipmap.icon_left, ActivityMain.RESOURCE_HIDE);
    }

    @Override
    public void onToolbarAction1() {
        mActivityMain.popFragment();
    }

    private void addDevice(WatchContact.Kid device) {
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
            WatchContact.Kid device = (WatchContact.Kid) contact.getItem();

            mActivityMain.mOperator.setFocusKid(device);

            setSelected(mDeviceList.indexOf(device));

            mActivityMain.mContactStack.push(device);
            mActivityMain.selectFragment(FragmentSyncSearch.class.getName(), null);
        }
    };
}
