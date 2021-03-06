package com.kidsdynamic.swing.androidswingapp;

import android.app.Fragment;
import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.TextView;

import org.w3c.dom.Text;

import java.util.Locale;

/**
 * Created by 03543 on 2016/12/19.
 */

public class FragmentDevice extends ViewFragment {
    private final static int STATUS_SEARCH = 0;
    private final static int STATUS_NOTFOUND = 1;
    private final static int STATUS_FOUND = 2;
    private final static int STATUS_NOKID = 3;

    private ActivityMain mActivityMain;
    private View mViewMain;

    private TextView mViewStatus;
    private TextView mViewCapacity;
    private ViewCircle mViewProgress;

    private Handler mHandler;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mActivityMain = (ActivityMain) getActivity();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        mViewMain = inflater.inflate(R.layout.fragment_device, container, false);

        InputMethodManager imm = (InputMethodManager) mActivityMain.getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(mViewMain.getWindowToken(), 0);


        mViewStatus = (TextView) mViewMain.findViewById(R.id.device_status);
        mViewCapacity = (TextView) mViewMain.findViewById(R.id.device_capacity);
        mViewProgress = (ViewCircle) mViewMain.findViewById(R.id.device_battery);
        mViewProgress.setOnProgressListener(mProgressListener);

        mHandler = new Handler();

        return mViewMain;
    }

    @Override
    public ViewFragmentConfig getConfig() {
        return new ViewFragmentConfig("", true, true, false,
                R.mipmap.city_california, ActivityMain.RESOURCE_HIDE, ActivityMain.RESOURCE_HIDE);
    }

    @Override
    public void onResume() {
        super.onResume();

        if (mActivityMain.mOperator.mFocusBatteryName.equals("")) {
            mHandler.postDelayed(mRunnable, 1000);

            if (mActivityMain.mOperator.getFocusKid() != null) {
                setStatus(STATUS_SEARCH);
                setTitle(mActivityMain.mOperator.getFocusKid().mName);

                mViewProgress.setStrokeBeginEnd(0, 10);
                mViewProgress.startProgress(30, -1, -1);
            } else {
                setStatus(STATUS_NOKID);
                mViewProgress.setActive(false);
            }
        } else {
            setTitle(mActivityMain.mOperator.mFocusBatteryName);
            setStatus(STATUS_FOUND);
            setCapacity(mActivityMain.mOperator.mFocusBatteryValue);
        }
    }

    @Override
    public void onPause() {
        super.onPause();
        mActivityMain.mBLEMachine.Cancel();

        mViewProgress.stopProgress();

        mHandler.removeCallbacksAndMessages(null);
        mHandler.removeCallbacks(mRunnable);
    }

    private void setStatus(int status) {
        if (status == STATUS_SEARCH) {
            mViewStatus.setText(getResources().getString(R.string.device_searching));
        } else if (status == STATUS_NOTFOUND) {
            mViewStatus.setText(getResources().getString(R.string.device_not_found));
        } else if (status == STATUS_FOUND) {
            mViewStatus.setText(getResources().getString(R.string.device_battery));
        } else if (status == STATUS_NOKID) {
            mViewStatus.setText(getResources().getString(R.string.device_no_watch));
        }
    }

    private void setTitle(String name) {
        String string = String.format(Locale.getDefault(),
                getResources().getString(R.string.device_owner), name);
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
            WatchContact.Kid kid = mActivityMain.mOperator.getFocusKid();
            if (kid != null)
                mActivityMain.mBLEMachine.Battery(mOnBatteryListener, ServerMachine.getMacAddress(kid.mMacId));
        }
    };

    BLEMachine.onBatteryListener mOnBatteryListener = new BLEMachine.onBatteryListener() {
        @Override
        public void onBattery(byte value) {
            mViewProgress.stopProgress();

            if (value == (byte) 0xFF) {
                setStatus(STATUS_NOTFOUND);
                mViewProgress.setActive(false);
            } else {
                setStatus(STATUS_FOUND);
                setCapacity(value);

                mActivityMain.mOperator.mFocusBatteryName = mActivityMain.mOperator.getFocusKid().mName;
                mActivityMain.mOperator.mFocusBatteryValue = value;
            }
        }
    };

    private ViewCircle.OnProgressListener mProgressListener = new ViewCircle.OnProgressListener() {
        @Override
        public void onProgress(ViewCircle view, int begin, int end) {
        }
    };
}
