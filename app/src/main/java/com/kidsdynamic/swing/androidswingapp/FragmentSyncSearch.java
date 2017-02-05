package com.kidsdynamic.swing.androidswingapp;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
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
    private List<WatchOperator.Event> mEventList;
    private List<voiceAlert> mVoiceAlertList;
    //private ArrayList<BLEMachine.InOutDoor> mInOutDoors;
    //private int mInOutDoorsIndex = 0;
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

        if (getArguments() != null)
            mDevice = (WatchContact.Kid) getArguments().getSerializable(ViewFragment.BUNDLE_KEY_DEVICE);
        else
            mDevice = new WatchContact.Kid();
        mEventList = mActivityMain.mOperator.EventGet(mDevice);
        //mMacAddress = ServerMachine.getMacAddress(mDevice.mMacId);
        mMacAddress = "E0:E5:CF:1E:D7:C2";
        Log.d("swing", "mac address " + mMacAddress);

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

    private class voiceAlert {
        public int mAlert;
        public long mTimestamp;
        voiceAlert(int alert, long timeStamp) {
            mAlert = alert;
            mTimestamp = timeStamp;
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        mVoiceAlertList = new ArrayList<>();
        Calendar cal = Calendar.getInstance();

        for (WatchOperator.Event event : mEventList) {
            if (event.mRepeat.equals("")) {
                mVoiceAlertList.add(new voiceAlert(event.mAlert, event.mStartDate));

            } else if (event.mRepeat.contains("DAILY")) {
                mVoiceAlertList.add(new voiceAlert(event.mAlert, event.mStartDate));

            } else if (event.mRepeat.contains("WEEKLY")) {
                mVoiceAlertList.add(new voiceAlert(event.mAlert, event.mStartDate));

            } else if (event.mRepeat.contains("MONTHLY")) {
                mVoiceAlertList.add(new voiceAlert(event.mAlert, event.mStartDate));

            }
        }
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
        mActivityMain.mBLEMachine.Search(mBleListener, mMacAddress);
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
        mViewProgress.setStrokeActive();
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
        mViewProgress.setStrokeActive();
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
        mViewProgress.setStrokeNormal();
    }

    BLEMachine.onFinishListener mBleListener = new BLEMachine.onFinishListener() {
        @Override
        public void onSearch(ArrayList<BLEMachine.Device> result) {
            for (BLEMachine.Device dev : result) {
                if (dev.mAddress.equals(mMacAddress)) {
                    mSearchResult = dev;
                    break;
                }
            }
        }

        @Override
        public void onSync(int resultCode, ArrayList<BLEMachine.InOutDoor> result) {
            if (resultCode == SYNC_RESULT_SUCCESS) {
                for (BLEMachine.InOutDoor res : result) {
                    WatchOperator.Upload uploadItem = new WatchOperator.Upload();
                    uploadItem.mMacId = mDevice.mMacId;
                    uploadItem.mTime = byteToDec(res.mTime[0], res.mTime[1], res.mTime[2], res.mTime[3]);
                    uploadItem.mOutdoorActivity = rawString(res.mData1);
                    uploadItem.mIndoorActivity = rawString(res.mData2);

                    Log.d("TEST", "Add Time " + uploadItem.mTime);
                    mActivityMain.mOperator.UploadItemAdd(uploadItem);
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

    private String rawString(byte[] b) {
        return String.format(Locale.getDefault(), "%s,%d,%s,%s,%s,%s",
                byteToStr(b[0], b[1], b[2], b[3]),
                b[4],
                byteToStr(b[5], b[6], b[7], b[8]),
                byteToStr(b[9], b[10], b[11], b[12]),
                byteToStr(b[13], b[14], b[15], b[16]),
                byteToStr(b[17], b[18], b[19], b[20])
        );
    }

    private int byteToDec(byte b0, byte b1, byte b2, byte b3) {
        int dec;

        dec = b0 & 0xFF;
        dec |= (b1 << 8) & 0xFF00;
        dec |= (b2 << 16) & 0xFF0000;
        dec |= (b3 << 24) & 0xFF000000;

        return dec;
    }

    private String byteToStr(byte b0, byte b1, byte b2, byte b3) {
        return "" + byteToDec(b0, b1, b2, b3);
    }
}
