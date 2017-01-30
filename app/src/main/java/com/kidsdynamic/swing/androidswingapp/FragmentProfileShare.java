package com.kidsdynamic.swing.androidswingapp;

import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.Locale;

/**
 * Created by 03543 on 2017/1/29.
 */

public class FragmentProfileShare extends ViewFragment {

    private ActivityMain mActivityMain;
    private View mViewMain;

    private TextView mViewTitle;
    private LinearLayout mViewShareContainer;

    private WatchContact.User mRequester;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mActivityMain = (ActivityMain) getActivity();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        mViewMain = inflater.inflate(R.layout.fragment_profile_share, container, false);

        mViewTitle = (TextView) mViewMain.findViewById(R.id.profile_share_title);
        mViewShareContainer = (LinearLayout) mViewMain.findViewById(R.id.profile_share_container);

        if (getArguments() != null) {
            mRequester = (WatchContact.User) getArguments().getSerializable(ViewFragment.BUNDLE_KEY_REQUESTER);
            String title = String.format(Locale.getDefault(), "Select Swing Watch to\nShare with %s", mRequester.mLabel);

            mViewTitle.setText(title);
        }

        // Test
        addDevice(new WatchContact.Kid(BitmapFactory.decodeResource(getResources(), R.mipmap.monster_green), "Green Monster", false));
        addDevice(new WatchContact.Kid(BitmapFactory.decodeResource(getResources(), R.mipmap.monster_yellow), "Yellow Monster", false));
        addDevice(new WatchContact.Kid(BitmapFactory.decodeResource(getResources(), R.mipmap.monster_purple), "Purple Monster", false));
        ///////////////////////

        return mViewMain;
    }

    @Override
    public ViewFragmentConfig getConfig() {
        return new ViewFragmentConfig("Request", true, true, false,
                ActivityMain.RESOURCE_IGNORE, R.mipmap.icon_left, ActivityMain.RESOURCE_HIDE);
    }

    @Override
    public void onToolbarAction1() {
        // Test
        ArrayList<WatchContact.Kid> list = getSelectList();
        for (WatchContact.Kid device : list) {
            Log.d("xxx", "select:" + device.mLabel);
        }
        ////////////

        mActivityMain.popFragment();
    }

    public void addDevice(WatchContact.Kid device) {
        View view = WatchContact.inflateShare(mActivityMain, device);

        view.setOnClickListener(mDeviceClickListener);

        mViewShareContainer.addView(view);
    }

    private View.OnClickListener mDeviceClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            WatchContact.Kid device = (WatchContact.Kid) view.getTag();
            selectDevice(device);
        }
    };

    private void selectDevice(WatchContact.Kid device) {
        View view = mViewShareContainer.findViewWithTag(device);
        View icon = view.findViewById(R.id.watch_contact_share_icon);

        if (icon.getVisibility() == View.VISIBLE)
            icon.setVisibility(View.INVISIBLE);
        else
            icon.setVisibility(View.VISIBLE);
    }

    private ArrayList<WatchContact.Kid> getSelectList() {
        ArrayList<WatchContact.Kid> list = new ArrayList<>();

        int count = mViewShareContainer.getChildCount();
        for (int idx = 0; idx < count; idx++) {
            View view = mViewShareContainer.getChildAt(idx);
            View icon = view.findViewById(R.id.watch_contact_share_icon);

            if (icon.getVisibility() == View.VISIBLE)
                list.add((WatchContact.Kid) view.getTag());
        }

        return list;
    }
}
