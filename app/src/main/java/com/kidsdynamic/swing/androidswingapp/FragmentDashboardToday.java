package com.kidsdynamic.swing.androidswingapp;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.Arrays;

/**
 * Created by 03543 on 2017/2/19.
 */

public class FragmentDashboardToday extends ViewFragment {
    private ActivityMain mActivityMain;
    private View mViewMain;

    private ViewDotIndicator mIndicator;
    private ViewTextSelector mSelector;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mActivityMain = (ActivityMain) getActivity();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        mViewMain = inflater.inflate(R.layout.fragment_dashboard_today, container, false);

        mIndicator = (ViewDotIndicator) mViewMain.findViewById(R.id.dashboard_today_indicator);

        mSelector = (ViewTextSelector) mViewMain.findViewById(R.id.dashboard_today_selector);
        mSelector.setOnSelectListener(mSelectorListener);

        return mViewMain;
    }

    @Override
    public ViewFragmentConfig getConfig() {
        return new ViewFragmentConfig("Dashboard", true, true, false,
                R.mipmap.city_newyork, ActivityMain.RESOURCE_HIDE, ActivityMain.RESOURCE_HIDE);
    }

    @Override
    public void onResume() {
        super.onResume();

        mSelector.clear();
        mSelector.add(Arrays.asList("Today", "This Week", "This Month", "This Year"));

        mIndicator.setDotCount(mSelector.getCount());
        mIndicator.setDotPosition(0);
    }

    @Override
    public void onPause() {
        super.onPause();
    }

    private ViewTextSelector.OnSelectListener mSelectorListener = new ViewTextSelector.OnSelectListener() {
        @Override
        public void OnSelect(View view, int position) {
            mIndicator.setDotPosition(position);
        }
    };

}
