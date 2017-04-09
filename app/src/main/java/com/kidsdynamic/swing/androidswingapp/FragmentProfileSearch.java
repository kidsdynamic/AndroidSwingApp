package com.kidsdynamic.swing.androidswingapp;

import android.os.Bundle;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

/**
 * Created by 03543 on 2017/1/25.
 */

public class FragmentProfileSearch extends ViewFragment {

    private ActivityMain mActivityMain;
    private View mViewMain;
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
        mViewMain = inflater.inflate(R.layout.fragment_profile_search, container, false);

        mViewProgress = (ViewCircle) mViewMain.findViewById(R.id.profile_search_progress);
        //mViewProgress.setOnClickListener(mFakeListener); // for test

        return mViewMain;
    }

    @Override
    public void onResume() {
        super.onResume();

        mViewProgress.setStrokeBeginEnd(0, 10);
        mViewProgress.startProgress(30, -1, -1);

        mActivityMain.mBLEMachine.Search(mOnSearchListener, 10);
    }

    @Override
    public void onPause() {
        super.onPause();

        mViewProgress.stopProgress();
        mActivityMain.mBLEMachine.Search(null, 0);
    }

    @Override
    public ViewFragment.ViewFragmentConfig getConfig() {
        return new ViewFragment.ViewFragmentConfig(
                getResources().getString(R.string.title_search_device), true, true, false,
                ActivityMain.RESOURCE_IGNORE, R.mipmap.icon_left, ActivityMain.RESOURCE_HIDE);
    }

    @Override
    public void onToolbarAction1() {
        mActivityMain.popFragment();
    }

    BLEMachine.onSearchListener mOnSearchListener = new BLEMachine.onSearchListener() {
        @Override
        public void onSearch(ArrayList<BLEMachine.Device> result) {
            List<WatchContact.Kid> bondKids = mActivityMain.mOperator.getKids();

            mSearchResult = new ArrayList<>();
            for (BLEMachine.Device dev : result) {

                WatchContact.Kid device = new WatchContact.Kid(null, dev.mAddress);
                device.mMacId = ServerMachine.getMacID(dev.mAddress);

                for (WatchContact.Kid bond : bondKids) {
                    if (bond.mMacId.equals(device.mMacId)) {
                        device = null;
                        break;
                    }
                }
                if (device != null)
                    mSearchResult.add(device);
            }

            if (!enumerateRegisteredMacId(true))
                mActivityMain.selectFragment(FragmentProfileSorry.class.getName(), null);
        }
    };

    // For Debug, 產生假MAC
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
        public void onFail(String command, int statusCode) {
            mViewProgress.stopProgress();
            Toast.makeText(mActivityMain,
                    mActivityMain.mServiceMachine.getErrorMessage(command, statusCode) + "(" + statusCode + ").", Toast.LENGTH_SHORT).show();
        }
    };

    private void gotoWatchSelect() {
        for (WatchContact contact : mSearchResult)
            mActivityMain.mContactStack.push(contact);
        mActivityMain.selectFragment(FragmentProfileSelect.class.getName(), null);
    }

    // For debug, 註冊一個假手錶
    private View.OnClickListener mFakeListener = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            mViewProgress.stopProgress();
            mActivityMain.mBLEMachine.Search(null, 0);

            WatchContact.Kid device = new WatchContact.Kid(null, "");
            device.mMacId = String.format(Locale.US, "%02X%02X%02X%02X%02X%02X",
                    (int) (Math.random() * 0xFF), (int) (Math.random() * 0xFF), (int) (Math.random() * 0xFF),
                    (int) (Math.random() * 0xFF), (int) (Math.random() * 0xFF), (int) (Math.random() * 0xFF));
            device.mLabel = device.mMacId;

            mSearchResult = new ArrayList<>();
            mSearchResult.add(device);

            mActivityMain.mServiceMachine.kidsWhoRegisteredMacID(mKidsWhoRegisteredMacIDListener, device.mMacId);
        }
    };
}