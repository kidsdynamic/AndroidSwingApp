package com.kidsdynamic.swing.androidswingapp;

import android.os.Bundle;
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
    private ViewCircle mViewProgress;

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

        mViewProgress = (ViewCircle) mViewMain.findViewById(R.id.watch_search_progress);
        mViewProgress.setOnProgressListener(mProgressListener);

        mViewProgress.setStrokeBeginEnd(0, 0);
        mViewProgress.startProgress(250, -1, -1);

        mActivityMain.mBLEMachine.Search(mOnSearchListener, 10);

        return mViewMain;
    }

    @Override
    public ViewFragmentConfig getConfig() {
        return new ViewFragmentConfig("Watch", false, false, false,
                ActivityMain.RESOURCE_IGNORE, ActivityMain.RESOURCE_IGNORE, ActivityMain.RESOURCE_IGNORE);
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
                mActivityMain.selectFragment(FragmentWatchSorry.class.getName(), null);
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
        Bundle bundle = new Bundle();
        bundle.putSerializable(ViewFragment.BUNDLE_KEY_CONTACT_LIST, mSearchResult);

        mActivityMain.selectFragment(FragmentWatchSelect.class.getName(), bundle);
    }

    private View.OnClickListener mBackOnClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            onToolbarAction1();
        }
    };

    private ViewCircle.OnProgressListener mProgressListener = new ViewCircle.OnProgressListener() {
        @Override
        public void onProgress(ViewCircle view, int begin, int end) {
        }
    };
}
