package com.kidsdynamic.swing.androidswingapp;

import android.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

/**
 * Created by 03543 on 2016/12/30.
 */

public class FragmentWatchRequest extends ViewFragment {
    private ActivityMain mActivityMain;
    private View mViewMain;

    private Button mButtonBack;
    private Button mButtonDashboard;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mActivityMain = (ActivityMain) getActivity();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        mViewMain = inflater.inflate(R.layout.fragment_watch_request, container, false);

        mButtonBack = (Button) mViewMain.findViewById(R.id.watch_request_back);
        mButtonBack.setOnClickListener(mOnBackListener);

        mButtonDashboard = (Button) mViewMain.findViewById(R.id.watch_request_dashboard);
        mButtonDashboard.setOnClickListener(mOnDashboardListener);

        return mViewMain;
    }

    @Override
    public ViewFragmentConfig getConfig() {
        return new ViewFragmentConfig("Watch", false, false,  false,
                ActivityMain.RESOURCE_IGNORE, ActivityMain.RESOURCE_IGNORE, ActivityMain.RESOURCE_IGNORE);
    }

    private Button.OnClickListener mOnBackListener = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            mActivityMain.selectFragment(FragmentWatchOwner.class.getName(), null);
        }
    };

    private Button.OnClickListener mOnDashboardListener = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            mActivityMain.selectFragment(FragmentDashboard.class.getName(), null);
        }
    };
}
