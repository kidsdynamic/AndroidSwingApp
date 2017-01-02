package com.kidsdynamic.swing.androidswingapp;

import android.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

/**
 * Created by 03543 on 2016/12/31.
 */

public class FragmentWatchSelect extends ViewFragment {
    private ActivityMain mActivityMain;
    private View mViewMain;

    private Button mButtonDashboard;
    private ViewContactLabel mViewContactLabel1;
    private ViewContactLabel mViewContactLabel2;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mActivityMain = (ActivityMain) getActivity();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        mViewMain = inflater.inflate(R.layout.fragment_watch_select, container, false);

        mViewContactLabel1 = (ViewContactLabel) mViewMain.findViewById(R.id.watch_select_contact1);
        mViewContactLabel1.setOnClickListener(mOnContactListener);

        mViewContactLabel2 = (ViewContactLabel) mViewMain.findViewById(R.id.watch_select_contact2);
        mViewContactLabel2.setOnClickListener(mOnContactListener);

        mButtonDashboard = (Button) mViewMain.findViewById(R.id.watch_select_dashboard);
        mButtonDashboard.setOnClickListener(mOnDashboardListener);

        return mViewMain;
    }

    @Override
    public ViewFragmentConfig getConfig() {
        return new ViewFragmentConfig("Watch", false, false);
    }

    private View.OnClickListener mOnContactListener = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            if (view == mViewContactLabel1)
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
