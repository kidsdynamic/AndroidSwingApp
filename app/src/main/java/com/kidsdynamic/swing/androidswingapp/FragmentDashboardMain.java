package com.kidsdynamic.swing.androidswingapp;

import android.content.Context;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.Toast;

/**
 * Created by 03543 on 2017/1/1.
 */

public class FragmentDashboardMain extends ViewFragment {
    private ActivityMain mActivityMain;
    private View mViewMain;

    private Button mButtonYes;
    private Button mButtonNo;
    private Button mbuttonAnother;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mActivityMain = (ActivityMain) getActivity();
        mActivityMain.getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        mViewMain = inflater.inflate(R.layout.fragment_dashboard_main, container, false);

        InputMethodManager imm = (InputMethodManager) mActivityMain.getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(mViewMain.getWindowToken(), 0);

        mButtonYes = (Button) mViewMain.findViewById(R.id.dashboard_main_yes);
        mButtonNo = (Button) mViewMain.findViewById(R.id.dashboard_main_no);
        mbuttonAnother = (Button) mViewMain.findViewById(R.id.dashboard_main_another);

        return mViewMain;
    }

    @Override
    public void onResume() {
        super.onResume();

        // 用戶並沒有任何kid
        if (mActivityMain.mOperator.getFocusKid() == null) {
            mActivityMain.clearFragment(FragmentDashboardRequest.class.getName(), null);
        } else {
            mButtonYes.setOnClickListener(mOnYesListener);
            mButtonNo.setOnClickListener(mOnNoListener);
            mbuttonAnother.setOnClickListener(mOnAnotherListener);
        }
    }

    @Override
    public void onPause() {
        mButtonYes.setOnClickListener(null);
        mButtonNo.setOnClickListener(null);
        mbuttonAnother.setOnClickListener(null);
        mActivityMain.getWindow().clearFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        super.onPause();
    }

    @Override
    public ViewFragmentConfig getConfig() {
        return new ViewFragmentConfig(
                getResources().getString(R.string.title_sync), true, true, false,
                R.mipmap.city_california, ActivityMain.RESOURCE_HIDE, ActivityMain.RESOURCE_HIDE);
    }

    private Button.OnClickListener mOnYesListener = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            WatchContact.Kid device = mActivityMain.mOperator.getFocusKid();

            if (device == null) {
                Toast.makeText(mActivityMain, getResources().getString(R.string.dashboard_main_no_device), Toast.LENGTH_SHORT).show();
            } else {
                // Todo : don't load avatar here.
                if (device.mProfile != null && !device.mProfile.equals("")) {
                    device.mPhoto = BitmapFactory.decodeFile(device.mProfile);
                } else {
                    device.mPhoto = BitmapFactory.decodeResource(getResources(), R.mipmap.monster_yellow);
                }
                device.mLabel = device.mName;
                device.mBound = true;

                mActivityMain.mContactStack.push(device);
                mActivityMain.selectFragment(FragmentDashboardProgress.class.getName(), null);
            }
        }
    };

    private Button.OnClickListener mOnNoListener = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            mActivityMain.consoleSelect(null);
            mActivityMain.consoleShow(true);
            mActivityMain.toolbarShow(true);
            mActivityMain.selectFragment(FragmentDashboardEmotion.class.getName(), null);
        }
    };

    private Button.OnClickListener mOnAnotherListener = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            mActivityMain.selectFragment(FragmentDashboardWatch.class.getName(), null);
        }
    };
}
