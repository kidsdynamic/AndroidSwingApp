package com.kidsdynamic.swing.androidswingapp;

import android.os.Bundle;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import java.util.ArrayList;

/**
 * Created by 03543 on 2017/1/25.
 */

public class FragmentProfileSearch extends ViewFragment {

    private ActivityMain mActivityMain;
    private View mViewMain;
    private ViewCircle mViewProgress;
    //private int mTimeOut;

    private ArrayList<WatchContact> mSearchResult;
    private int mSearchResultIndex = 0;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mActivityMain = (ActivityMain) getActivity();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        mViewMain = inflater.inflate(R.layout.fragment_profile_search, container, false);

        mViewProgress = (ViewCircle) mViewMain.findViewById(R.id.profile_search_progress);
        mViewProgress.setOnProgressListener(mSearchProgressListener);
        //mTimeOut = 40;
        mViewProgress.setStrokeBeginEnd(0, 0); // For test, remove me later
        mViewProgress.startProgress(250, -1, -1);

        mActivityMain.mBLEMachine.Search(mOnSearchListener, 10);

        return mViewMain;
    }

    @Override
    public ViewFragment.ViewFragmentConfig getConfig() {
        return new ViewFragment.ViewFragmentConfig("Search Device", true, true, false,
                ActivityMain.RESOURCE_IGNORE, R.mipmap.icon_left, ActivityMain.RESOURCE_HIDE);
    }

    @Override
    public void onToolbarAction1() {
        mActivityMain.mBLEMachine.Search(null);
        mActivityMain.popFragment();
    }

    BLEMachine.onSearchListener mOnSearchListener = new BLEMachine.onSearchListener() {
        @Override
        public void onSearch(ArrayList<BLEMachine.Device> result) {
            mSearchResult = new ArrayList<>();
            for (BLEMachine.Device dev : result) {

                WatchContact.Kid device = new WatchContact.Kid(null, dev.mAddress);
                device.mMacId = ServerMachine.getMacID(dev.mAddress);
                mSearchResult.add(device);
            }

            if (!enumerateRegisteredMacId(true))
                mActivityMain.selectFragment(FragmentProfileSorry.class.getName(), null);
        }
    };

    private boolean enumerateRegisteredMacId(boolean searchStart) {
        boolean rtn = true;

        if (searchStart)
            mSearchResultIndex = 0;
        else
            mSearchResultIndex++;

        if (mSearchResult.size() <= mSearchResultIndex) {
            rtn = false;
        } else {
            String macId = ServerMachine.getMacID(mSearchResult.get(mSearchResultIndex).mLabel);
            mActivityMain.mServiceMachine.kidsWhoRegisteredMacID(mKidsWhoRegisteredMacIDListener, macId);
        }
        return rtn;
    }

    ServerMachine.kidsWhoRegisteredMacIDListener mKidsWhoRegisteredMacIDListener = new ServerMachine.kidsWhoRegisteredMacIDListener() {
        @Override
        public void onSuccess(int statusCode, ServerGson.kids.whoRegisteredMacID.response response) {
            WatchContact.Kid device = (WatchContact.Kid) mSearchResult.get(mSearchResultIndex);
            device.mLabel = response.kid.parent.email;
            device.mUserId = response.kid.parent.id;
            device.mProfile = response.kid.profile;
            device.mBound = true;
            if (!enumerateRegisteredMacId(false))
                gotoWatchSelect();
        }

        @Override
        public void onNotRegistered(int statusCode) {
            if (!enumerateRegisteredMacId(false))
                gotoWatchSelect();
        }

        @Override
        public void onFail(int statusCode) {
            mViewProgress.stopProgress();
            Toast.makeText(mActivityMain, "Search MAC failed(" + statusCode + ").", Toast.LENGTH_SHORT).show();
        }
    };

    private void gotoWatchSelect() {
        for(WatchContact contact : mSearchResult)
            mActivityMain.mContactStack.push(contact);
        mActivityMain.selectFragment(FragmentProfileSelect.class.getName(), null);
    }

    private ViewCircle.OnProgressListener mSearchProgressListener = new ViewCircle.OnProgressListener() {
        @Override
        public void onProgress(ViewCircle view, int begin, int end) {
            //mTimeOut--;
            //if(mTimeOut == 0)
            //    mActivityMain.selectFragment(FragmentProfileSelect.class.getName(), null);
        }
    };
}