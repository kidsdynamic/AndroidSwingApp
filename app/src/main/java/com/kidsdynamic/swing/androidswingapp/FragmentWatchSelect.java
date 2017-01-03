package com.kidsdynamic.swing.androidswingapp;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;

/**
 * Created by 03543 on 2016/12/31.
 */

public class FragmentWatchSelect extends ViewFragment {
    private ActivityMain mActivityMain;
    private View mViewMain;

    private Button mButtonDashboard;
    private ViewContact mViewContact1;
    private ViewContact mViewContact2;
    private ImageView mViewBack;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mActivityMain = (ActivityMain) getActivity();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        mViewMain = inflater.inflate(R.layout.fragment_watch_select, container, false);

        mViewContact1 = (ViewContact) mViewMain.findViewById(R.id.watch_select_contact1);
        mViewContact1.setOnClickListener(mOnContactListener);

        mViewContact2 = (ViewContact) mViewMain.findViewById(R.id.watch_select_contact2);
        mViewContact2.setOnClickListener(mOnContactListener);

        mViewBack = (ImageView) mViewMain.findViewById(R.id.fragment_back);
        mViewBack.setOnClickListener(mBackOnClickListener);

        mButtonDashboard = (Button) mViewMain.findViewById(R.id.watch_select_dashboard);
        mButtonDashboard.setOnClickListener(mOnDashboardListener);

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

    private View.OnClickListener mOnContactListener = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            if (view == mViewContact1)
                mActivityMain.selectFragment(FragmentWatchAdded.class.getName(), null);
            else
                mActivityMain.selectFragment(FragmentWatchRegistered.class.getName(), null);
        }
    };

    private Button.OnClickListener mOnDashboardListener = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
//            mActivityMain.selectFragment(FragmentSignupLogin.class.getName(), null);
        }
    };
}
