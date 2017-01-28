package com.kidsdynamic.swing.androidswingapp;

import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.Toast;

import java.util.ArrayList;

/**
 * Created by 03543 on 2016/12/31.
 */

public class FragmentWatchSearch extends ViewFragment {

    private ActivityMain mActivityMain;
    private View mViewMain;
    private ImageView mViewBack;
    private ViewProgressCircle mViewProgress;

    private ArrayList<WatchContact> mSearchResult;
    private int mSearchResultIndex = 0;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mActivityMain = (ActivityMain) getActivity();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        mViewMain = inflater.inflate(R.layout.fragment_watch_search, container, false);

        mViewBack = (ImageView) mViewMain.findViewById(R.id.fragment_back);
        mViewBack.setOnClickListener(mBackOnClickListener);

        mViewProgress = (ViewProgressCircle) mViewMain.findViewById(R.id.watch_search_progress);
        mViewProgress.setOnProgressListener(mProgressListener);
        mViewProgress.setRepeat(true);
        mViewProgress.setDuration(1000);

        Handler handle = new Handler();
        handle.post(new Runnable() {
            @Override
            public void run() {
                mViewProgress.start();
            }
        });

        mActivityMain.mBLEMachine.Search(mBleListener, 10);

        return mViewMain;
    }

    @Override
    public ViewFragmentConfig getConfig() {
        return new ViewFragmentConfig("Watch", false, false, false,
                ActivityMain.RESOURCE_IGNORE, ActivityMain.RESOURCE_IGNORE, ActivityMain.RESOURCE_IGNORE);
    }

    @Override
    public void onToolbarAction1() {
        mActivityMain.mBLEMachine.Search(null, 0);
        mActivityMain.popFragment();
    }

    BLEMachine.onFinishListener mBleListener = new BLEMachine.onFinishListener() {
        @Override
        public void onScan(ArrayList<BLEMachine.Device> result) {
            mSearchResult = new ArrayList<>();
            for (BLEMachine.Device dev : result) {

                WatchContact.Device device = new WatchContact.Device(null, dev.mAddress, false);
                mSearchResult.add(device);
            }

            if (!mSearchResult.isEmpty()) {
                searchMac(true);
            } else {
                mActivityMain.selectFragment(FragmentWatchSorry.class.getName(), null);
            }
        }

        @Override
        public void onSync(int resultCode, ArrayList<BLEMachine.InOutDoor> result) {

        }
    };

    private void searchMac(boolean searchStart) {
        if (searchStart)
            mSearchResultIndex = 0;
        else
            mSearchResultIndex++;

        if (mSearchResult.size() <= mSearchResultIndex) {
            Bundle bundle = new Bundle();
            bundle.putSerializable(ViewFragment.BUNDLE_KEY_DEVICE_LIST, mSearchResult);

            mActivityMain.selectFragment(FragmentWatchSelect.class.getName(), bundle);
        } else {
            String[] separated = mSearchResult.get(mSearchResultIndex).mLabel.split(":");
            String macId = "";
            for (String s : separated)
                macId += s;
            mActivityMain.mServiceMachine.kidsWhoRegisteredMacID(mKidsWhoRegisteredMacIDListener, macId);
        }
    }

    ServerMachine.kidsWhoRegisteredMacIDListener mKidsWhoRegisteredMacIDListener = new ServerMachine.kidsWhoRegisteredMacIDListener() {
        @Override
        public void onSuccess(int statusCode, ServerGson.kids.whoRegisteredMacID.response response) {
            WatchContact.Device device = (WatchContact.Device) mSearchResult.get(mSearchResultIndex);
            device.mLabel = response.user.email;
            device.mBound = true;
            Log.d("swing", "Register user " + device.mLabel);
            searchMac(false);
        }

        @Override
        public void onNotRegistered(int statusCode) {
            searchMac(false);
        }

        @Override
        public void onFail(int statusCode) {
            mViewProgress.pause();
            Toast.makeText(mActivityMain, "Search MAC failed(" + statusCode + ").", Toast.LENGTH_SHORT).show();
        }
    };

    private View.OnClickListener mBackOnClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            onToolbarAction1();
        }
    };

    private ViewProgressCircle.OnProgressListener mProgressListener = new ViewProgressCircle.OnProgressListener() {
        @Override
        public void onProgress(ViewProgressCircle view, int progress, int total) {
        }
    };
}
