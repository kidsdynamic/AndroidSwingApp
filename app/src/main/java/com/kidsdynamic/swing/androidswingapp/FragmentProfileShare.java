package com.kidsdynamic.swing.androidswingapp;

import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
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

    private Button mViewSave;
    private TextView mViewTitle;
    private LinearLayout mViewShareContainer;

    private WatchContact.User mRequestTo;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mActivityMain = (ActivityMain) getActivity();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        mViewMain = inflater.inflate(R.layout.fragment_profile_share, container, false);

        mViewSave = (Button) mViewMain.findViewById(R.id.profile_share_save);
        mViewSave.setOnClickListener(mSaveListener);

        mViewTitle = (TextView) mViewMain.findViewById(R.id.profile_share_title);
        mViewShareContainer = (LinearLayout) mViewMain.findViewById(R.id.profile_share_container);

        if(!mActivityMain.mContactStack.isEmpty()) {
            mRequestTo = (WatchContact.User)mActivityMain.mContactStack.pop();

            String title = String.format(Locale.getDefault(), getResources().getString(R.string.profile_share_who), mRequestTo.mLabel);
            mViewTitle.setText(title);

            // Todo: Load Kids of mRequestTo and insert into container. like test below
        }

        // Test
        //addDevice(new WatchContact.Kid(BitmapFactory.decodeResource(getResources(), R.mipmap.monster_green), "Green Monster"));
        //addDevice(new WatchContact.Kid(BitmapFactory.decodeResource(getResources(), R.mipmap.monster_yellow), "Yellow Monster"));
        //addDevice(new WatchContact.Kid(BitmapFactory.decodeResource(getResources(), R.mipmap.monster_purple), "Purple Monster"));
        ///////////////////////

        return mViewMain;
    }

    @Override
    public ViewFragmentConfig getConfig() {
        return new ViewFragmentConfig(
                getResources().getString(R.string.title_request_from), true, true, false,
                ActivityMain.RESOURCE_IGNORE, R.mipmap.icon_left, ActivityMain.RESOURCE_HIDE);
    }

    @Override
    public void onToolbarAction1() {
        mActivityMain.popFragment();
    }

    private View.OnClickListener mSaveListener = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            ArrayList<WatchContact.Kid> list = getSelectList();
            for (WatchContact.Kid kid : list) {
                // Todo: emit request.
            }

            mActivityMain.popFragment();
        }
    };

    public void addDevice(WatchContact.Kid device) {
        View view = WatchContact.inflateCheck(mActivityMain, device, false);

        view.setOnClickListener(mDeviceClickListener);

        mViewShareContainer.addView(view);
    }

    private View.OnClickListener mDeviceClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            WatchContact.Kid device = (WatchContact.Kid) view.getTag();

            View label = mViewShareContainer.findViewWithTag(device);
            View check = label.findViewById(R.id.watch_contact_check_icon);
            check.setSelected(!check.isSelected());
        }
    };

    private ArrayList<WatchContact.Kid> getSelectList() {
        ArrayList<WatchContact.Kid> list = new ArrayList<>();

        int count = mViewShareContainer.getChildCount();
        for (int idx = 0; idx < count; idx++) {
            View label = mViewShareContainer.getChildAt(idx);
            View check = label.findViewById(R.id.watch_contact_check_icon);

            if (check.isSelected())
                list.add((WatchContact.Kid) label.getTag());
        }

        return list;
    }
}
