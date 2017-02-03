package com.kidsdynamic.swing.androidswingapp;

import android.app.Fragment;
import android.os.Bundle;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.Locale;

/**
 * Created by 03543 on 2016/12/19.
 */

public class FragmentDevice extends ViewFragment {
    private ActivityMain mActivityMain;
    private View mViewMain;

    private TextView mViewCapacity;
    private ViewCircle mViewProgress;

    private Handler mHandler;
    private boolean mPause = false;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mActivityMain = (ActivityMain) getActivity();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        mViewMain = inflater.inflate(R.layout.fragment_device, container, false);

        mViewCapacity = (TextView) mViewMain.findViewById(R.id.device_capacity);
        mViewProgress = (ViewCircle)mViewMain.findViewById(R.id.device_battery);

        mHandler = new Handler();

        return mViewMain;
    }

    @Override
    public ViewFragmentConfig getConfig() {
        return new ViewFragmentConfig("", true, true, false,
                R.mipmap.city_illinois, ActivityMain.RESOURCE_HIDE, ActivityMain.RESOURCE_HIDE);
    }

    @Override
    public void onResume() {
        super.onResume();

        mPause = false;
        mHandler.postDelayed(mRunnable, 1000);
    }

    @Override
    public void onPause() {
        super.onPause();

        mHandler.removeCallbacksAndMessages(null);
        mPause = true;
    }

    private void setTitle(String name) {
        String string = String.format(Locale.getDefault(), "%s's Watch", name);
        mActivityMain.toolbarSetTitle(string, false);
    }

    private void setCapacity(int percent) {
        String string = String.format(Locale.getDefault(), "%d%%", percent);
        mViewCapacity.setText(string);
        mViewProgress.setStrokeBeginEnd(0, percent);
    }

    private Runnable mRunnable = new Runnable() {
        @Override
        public void run() {
            if(mPause)
                return;

            // Test
            setTitle("Yellow Monster");
            setCapacity(88);
            ////////

            mHandler.postDelayed(mRunnable, 1000);
        }
    };
}
