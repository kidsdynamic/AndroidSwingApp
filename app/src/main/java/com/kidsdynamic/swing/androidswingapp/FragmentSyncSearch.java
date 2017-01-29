package com.kidsdynamic.swing.androidswingapp;

import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import java.util.ArrayList;

import static com.kidsdynamic.swing.androidswingapp.BLEMachine.SYNC_RESULT_SUCCESS;

/**
 * Created by 03543 on 2017/1/21.
 */

public class FragmentSyncSearch extends ViewFragment {

    private final int SEARCH_TIMEOUT = 15;  // 15 seconds
    private int mSearchTimeout;

    private final int SYNC_TIMEOUT = 60;  // 8 seconds
    private int mSyncTimeout;

    private ActivityMain mActivityMain;
    private View mViewMain;
    private TextView mViewLabel;
    private Button mViewButton1;
    private Button mViewButton2;
    private ViewProgressCircle mViewProgress;

    private WatchContact.Device mDevice;
    private BLEMachine.Device mSearchResult = null;
    private boolean mSyncFinish = false;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mActivityMain = (ActivityMain) getActivity();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        mViewMain = inflater.inflate(R.layout.fragment_sync_search, container, false);

        mViewProgress = (ViewProgressCircle) mViewMain.findViewById(R.id.sync_search_progress);

        mViewLabel = (TextView) mViewMain.findViewById(R.id.sync_search_title);
        mViewButton1 = (Button) mViewMain.findViewById(R.id.sync_search_button1);
        mViewButton2 = (Button) mViewMain.findViewById(R.id.sync_search_button2);

        mDevice = (WatchContact.Device) getArguments().getSerializable(ViewFragment.BUNDLE_KEY_DEVICE);

        Handler handle = new Handler();
        handle.post(new Runnable() {
            @Override
            public void run() {
                viewSearching();
                bleSearchStart();
            }
        });

        return mViewMain;
    }

    @Override
    public void onPause() {
        bleSearchCancel();
        super.onPause();
    }

    @Override
    public ViewFragmentConfig getConfig() {
        return new ViewFragmentConfig("Sync", true, true, false,
                R.mipmap.city_florida, R.mipmap.icon_left, ActivityMain.RESOURCE_HIDE);
    }

    @Override
    public void onToolbarAction1() {
        mActivityMain.popFragment();
    }

    private ViewProgressCircle.OnProgressListener mSearchProgressListener = new ViewProgressCircle.OnProgressListener() {
        @Override
        public void onProgress(ViewProgressCircle view, int progress, int total) {
            mSearchTimeout--;

            if (mSearchResult != null && mDevice.mLabel.equals(mSearchResult.mAddress)) {
                viewFound();
            }

            if (mSearchTimeout == 0) {
                viewNotFound();
                bleSearchCancel();
            }
        }
    };

    private ViewProgressCircle.OnProgressListener mSyncProgressListener = new ViewProgressCircle.OnProgressListener() {
        @Override
        public void onProgress(ViewProgressCircle view, int progress, int total) {
            mSyncTimeout--;

            // todo: Below is a simulation. Device has synced after progress 4
            if(mSyncFinish) {
                viewCompleted();
            }

            if (mSyncTimeout == 0) {
                viewNotFound();
                bleSyncCancel();
            }
        }
    };

    private Button.OnClickListener mAgainListener = new Button.OnClickListener() {
        @Override
        public void onClick(View view) {
            viewSearching();
            bleSearchStart();
        }
    };

    private Button.OnClickListener mExitListener = new Button.OnClickListener() {
        @Override
        public void onClick(View view) {
            mActivityMain.selectFragment(FragmentDashboard.class.getName(), null);
        }
    };

    private Button.OnClickListener mSyncListener = new Button.OnClickListener() {
        @Override
        public void onClick(View v) {
            viewSyncing();
            bleSyncStart();
        }
    };

    private void bleSearchStart() {
        mActivityMain.mBLEMachine.Search(mBleListener, mDevice.mLabel);
        mSearchResult = null;
    }

    private void bleSearchCancel() {
        mActivityMain.mBLEMachine.Search(null);
    }

    private void bleSyncStart() {
        if (mSearchResult != null)
            mActivityMain.mBLEMachine.Sync(mBleListener, mSearchResult);
        mSyncFinish = false;
    }

    private void bleSyncCancel() {
        mActivityMain.mBLEMachine.Disconnect();
    }

    private void viewSearching() {
        mViewLabel.setText("Searching\nfor Your\nDevice!");

        mViewButton1.setVisibility(View.INVISIBLE);
        mViewButton1.setOnClickListener(null);

        mViewButton2.setVisibility(View.INVISIBLE);
        mViewButton2.setOnClickListener(null);

        mSearchTimeout = SEARCH_TIMEOUT;

        mViewProgress.pause();
        mViewProgress.setRepeat(true);
        mViewProgress.setProgress(0);
        mViewProgress.setOnProgressListener(mSearchProgressListener);
        mViewProgress.start();
    }

    private void viewFound() {
        mViewLabel.setText("Found\nYour\nDevice!");
        mViewButton1.setText("Sync Now");

        mViewButton1.setVisibility(View.VISIBLE);
        mViewButton1.setOnClickListener(mSyncListener);

        mViewButton2.setVisibility(View.INVISIBLE);
        mViewButton2.setOnClickListener(null);

        mViewProgress.pause();
        mViewProgress.setRepeat(false);
        mViewProgress.setProgress(mViewProgress.getTotal());
        mViewProgress.setOnProgressListener(null);
    }

    private void viewSyncing() {
        mViewLabel.setText("Syncing");

        mViewButton1.setVisibility(View.INVISIBLE);
        mViewButton1.setOnClickListener(null);

        mViewButton2.setVisibility(View.INVISIBLE);
        mViewButton2.setOnClickListener(null);

        mSyncTimeout = SYNC_TIMEOUT;
        mViewProgress.pause();
        mViewProgress.setRepeat(true);
        mViewProgress.setProgress(0);
        mViewProgress.setOnProgressListener(mSyncProgressListener);
        mViewProgress.start();
    }

    private void viewCompleted() {
        mViewLabel.setText("Sync\nCompleted");
        mViewButton1.setText("Go to Dashboard");

        mViewButton1.setVisibility(View.VISIBLE);
        mViewButton1.setOnClickListener(mExitListener);

        mViewButton2.setVisibility(View.INVISIBLE);
        mViewButton2.setOnClickListener(null);

        mViewProgress.pause();
        mViewProgress.setRepeat(false);
        mViewProgress.setProgress(mViewProgress.getTotal());
        mViewProgress.setOnProgressListener(null);
    }

    private void viewNotFound() {
        mViewLabel.setText("We Can't\nFind Your\nDevice!");

        mViewButton1.setText("Try Again");
        mViewButton2.setText("Go to Last Synced Data");

        mViewButton1.setVisibility(View.VISIBLE);
        mViewButton1.setOnClickListener(mAgainListener);

        mViewButton2.setVisibility(View.VISIBLE);
        mViewButton2.setOnClickListener(mExitListener);

        mViewProgress.pause();
        mViewProgress.setRepeat(false);
        mViewProgress.setProgress(0);
        mViewProgress.setOnProgressListener(null);
    }

    BLEMachine.onFinishListener mBleListener = new BLEMachine.onFinishListener() {
        @Override
        public void onSearch(ArrayList<BLEMachine.Device> result) {
            for (BLEMachine.Device dev : result) {
                if (dev.mAddress.equals(mDevice.mLabel)) {
                    mSearchResult = dev;
                    break;
                }
            }
        }

        @Override
        public void onSync(int resultCode, ArrayList<BLEMachine.InOutDoor> result) {
            if (resultCode == SYNC_RESULT_SUCCESS) {
                // Todo : upload indoor/outdoor
                Log.d("TEST", "Sync done!");
                mSyncFinish = true;
            } else {
                // Todo : first connect?
                Log.d("TEST", "Sync failed!");
                mSyncFinish = true;

            }
        }
    };
}
