package com.kidsdynamic.swing.androidswingapp;

import android.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

/**
 * Created by 03543 on 2017/1/1.
 */

public class FragmentSyncNow extends ViewFragment {
    private ActivityMain mActivityMain;
    private View mViewMain;

    private Button mButtonYes;
    private Button mButtonNo;
    private Button mbuttonAnother;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mActivityMain = (ActivityMain) getActivity();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        mViewMain = inflater.inflate(R.layout.fragment_sync_now, container, false);

        mButtonYes = (Button) mViewMain.findViewById(R.id.sync_now_yes);
        mButtonYes.setOnClickListener(mOnYesListener);

        mButtonNo = (Button) mViewMain.findViewById(R.id.sync_now_no);
        mButtonNo.setOnClickListener(mOnNoListener);

        mbuttonAnother = (Button) mViewMain.findViewById(R.id.sync_now_another);
        mbuttonAnother.setOnClickListener(mOnAnotherListener);

        return mViewMain;
    }

    private Button.OnClickListener mOnYesListener = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
//            mActivityMain.selectFragment(FragmentWatchOwner.class.getName(), null);
        }
    };

    private Button.OnClickListener mOnNoListener = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            mActivityMain.consoleSelect(null);
            mActivityMain.consoleShow(true);
            mActivityMain.toolbarShow(true);
            mActivityMain.selectFragment(FragmentDashboard.class.getName(), null);
        }
    };

    private Button.OnClickListener mOnAnotherListener = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
//            mActivityMain.selectFragment(FragmentWatchPurchase.class.getName(), null);
        }
    };
}
