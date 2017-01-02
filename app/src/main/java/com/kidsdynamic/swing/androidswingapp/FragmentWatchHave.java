package com.kidsdynamic.swing.androidswingapp;

import android.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;

/**
 * Created by 03543 on 2016/12/30.
 */

public class FragmentWatchHave extends ViewFragment {
    private ActivityMain mActivityMain;
    private View mViewMain;

    private Button mButtonYes;
    private Button mButtonNo;
    private ImageView mViewBack;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mActivityMain = (ActivityMain) getActivity();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        mViewMain = inflater.inflate(R.layout.fragment_watch_have, container, false);

        mButtonYes = (Button) mViewMain.findViewById(R.id.watch_have_yes);
        mButtonYes.setOnClickListener(mOnYesListener);

        mButtonNo = (Button) mViewMain.findViewById(R.id.watch_have_no);
        mButtonNo.setOnClickListener(mOnNoListener);

        mViewBack = (ImageView) mViewMain.findViewById(R.id.fragment_back);
        mViewBack.setOnClickListener(mBackOnClickListener);

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

    private Button.OnClickListener mOnYesListener = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            mActivityMain.selectFragment(FragmentWatchOwner.class.getName(), null);
        }
    };

    private Button.OnClickListener mOnNoListener = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            mActivityMain.selectFragment(FragmentWatchPurchase.class.getName(), null);
        }
    };
}
