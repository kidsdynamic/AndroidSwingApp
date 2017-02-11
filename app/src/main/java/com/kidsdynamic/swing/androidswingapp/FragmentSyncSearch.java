package com.kidsdynamic.swing.androidswingapp;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;

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
    private ViewCircle mViewProgress;

    private WatchContact.Kid mDevice;
    private String mMacAddress;
    private BLEMachine.Device mSearchResult = null;
    private List<BLEMachine.VoiceAlert> mVoiceAlertList;
    private boolean mSyncFinish = false;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mActivityMain = (ActivityMain) getActivity();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        mViewMain = inflater.inflate(R.layout.fragment_sync_search, container, false);

        mViewProgress = (ViewCircle) mViewMain.findViewById(R.id.sync_search_progress);

        mViewLabel = (TextView) mViewMain.findViewById(R.id.sync_search_title);
        mViewButton1 = (Button) mViewMain.findViewById(R.id.sync_search_button1);
        mViewButton2 = (Button) mViewMain.findViewById(R.id.sync_search_button2);

        mDevice = mActivityMain.mContactStack.isEmpty() ?
                new WatchContact.Kid() :
                (WatchContact.Kid) mActivityMain.mContactStack.pop();

        List<WatchEvent> eventList = mActivityMain.mOperator.getEventsForSync(mDevice);
        mVoiceAlertList = new ArrayList<>();
        for (WatchEvent event : eventList)
            mVoiceAlertList.add(new BLEMachine.VoiceAlert((byte) event.mAlert, event.mAlertTimeStamp));

        mMacAddress = ServerMachine.getMacAddress(mDevice.mMacId);
        //mMacAddress = "E0:E5:CF:1E:D7:C2";

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
    public void onResume() {
        super.onResume();
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

    private ViewCircle.OnProgressListener mSearchProgressListener = new ViewCircle.OnProgressListener() {
        @Override
        public void onProgress(ViewCircle view, int begin, int end) {
            mSearchTimeout--;

            if (mSearchResult != null && mMacAddress.equals(mSearchResult.mAddress)) {
                viewFound();
            }

            if (mSearchTimeout == 0) {
                viewNotFound();
                bleSearchCancel();
            }
        }
    };

    private ViewCircle.OnProgressListener mSyncProgressListener = new ViewCircle.OnProgressListener() {
        @Override
        public void onProgress(ViewCircle view, int begin, int end) {
            mSyncTimeout--;

            if (mSyncFinish) {
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
        mActivityMain.mBLEMachine.Search(mOnSearchListener, mMacAddress);
        mSearchResult = null;
    }

    private void bleSearchCancel() {
        mActivityMain.mBLEMachine.Search(null);
    }

    private void bleSyncStart() {
        if (mSearchResult != null)
            mActivityMain.mBLEMachine.Sync(mOnSyncListener, mSearchResult, mVoiceAlertList);
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

        mViewProgress.setOnProgressListener(mSearchProgressListener);
        mViewProgress.setStrokeBeginEnd(0, 0);
        mViewProgress.startProgress(250, -1, -1);
    }

    private void viewFound() {
        mViewLabel.setText("Found\nYour\nDevice!");
        mViewButton1.setText("Sync Now");

        mViewButton1.setVisibility(View.VISIBLE);
        mViewButton1.setOnClickListener(mSyncListener);

        mViewButton2.setVisibility(View.INVISIBLE);
        mViewButton2.setOnClickListener(null);

        mViewProgress.stopProgress();
        mViewProgress.setOnProgressListener(null);
        mViewProgress.setActive(false);
    }

    private void viewSyncing() {
        mViewLabel.setText("Syncing");

        mViewButton1.setVisibility(View.INVISIBLE);
        mViewButton1.setOnClickListener(null);

        mViewButton2.setVisibility(View.INVISIBLE);
        mViewButton2.setOnClickListener(null);

        mSyncTimeout = SYNC_TIMEOUT;
        mViewProgress.setOnProgressListener(mSyncProgressListener);
        mViewProgress.setStrokeBeginEnd(0, 0);
        mViewProgress.startProgress(250, -1, -1);
    }

    private void viewCompleted() {
        mViewLabel.setText("Sync\nCompleted");
        mViewButton1.setText("Go to Dashboard");

        mViewButton1.setVisibility(View.VISIBLE);
        mViewButton1.setOnClickListener(mExitListener);

        mViewButton2.setVisibility(View.INVISIBLE);
        mViewButton2.setOnClickListener(null);

        mViewProgress.stopProgress();
        mViewProgress.setOnProgressListener(null);
        mViewProgress.setActive(true);
    }

    private void viewNotFound() {
        mViewLabel.setText("We Can't\nFind Your\nDevice!");

        mViewButton1.setText("Try Again");
        mViewButton2.setText("Go to Last Synced Data");

        mViewButton1.setVisibility(View.VISIBLE);
        mViewButton1.setOnClickListener(mAgainListener);

        mViewButton2.setVisibility(View.VISIBLE);
        mViewButton2.setOnClickListener(mExitListener);

        mViewProgress.stopProgress();
        mViewProgress.setOnProgressListener(null);
        mViewProgress.setActive(false);
    }

    BLEMachine.onSearchListener mOnSearchListener = new BLEMachine.onSearchListener() {
        @Override
        public void onSearch(ArrayList<BLEMachine.Device> result) {
            for (BLEMachine.Device dev : result) {
                if (dev.mAddress.equals(mMacAddress)) {
                    mSearchResult = dev;
                    break;
                }
            }
        }
    };

    BLEMachine.onSyncListener mOnSyncListener = new BLEMachine.onSyncListener() {
        @Override
        public void onSync(int resultCode, ArrayList<BLEMachine.InOutDoor> result) {
            if (resultCode == SYNC_RESULT_SUCCESS) {
                for (BLEMachine.InOutDoor res : result) {
                    mActivityMain.mOperator.pushUploadItem(mDevice.mMacId, res.mTime, res.mData1, res.mData2);
                }
                Intent intent = new Intent(mActivityMain, ServerPushService.class);
                mActivityMain.startService(intent);

                mSyncFinish = true;
            } else {
                // Todo : first connect?
                mSyncFinish = true;

            }
        }
    };


}
