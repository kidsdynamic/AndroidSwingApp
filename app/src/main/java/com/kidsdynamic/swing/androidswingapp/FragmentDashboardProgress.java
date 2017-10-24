package com.kidsdynamic.swing.androidswingapp;

import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.analytics.FirebaseAnalytics;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import static com.kidsdynamic.swing.androidswingapp.BLEMachine.SYNC_RESULT_SUCCESS;

/**
 * Created by 03543 on 2017/1/21.
 */

public class FragmentDashboardProgress extends ViewFragment {
    private final int PROGRESS_INTERVAL = 100;

    private final static int SEARCH_TIMEOUT = 150;     // 150 * PROGRESS_INTERVAL milliseconds
    private int mSearchTimeout;
    private final static int SYNC_TIMEOUT = 1000;      // 1000 * PROGRESS_INTERVAL milliseconds
    private int mSyncTimeout;
    private final static int UPLOAD_TIMEOUT = 1000;      // 1000 * PROGRESS_INTERVAL milliseconds
    private int mUploadTimeout;
    private final static int DOWNLOAD_TIMEOUT = 1000;      // 1000 * PROGRESS_INTERVAL milliseconds
    private int mDownloadTimeout;


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
    private final static int SYNC_STATE_INIT = 0;
    private final static int SYNC_STATE_SUCCESS = 1;
    private final static int SYNC_STATE_FAIL = 2;
    private int mSyncState = SYNC_STATE_INIT;
    private boolean mActivityUpdateFinish = false;
    private boolean mServerSyncFinish = false;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mActivityMain = (ActivityMain) getActivity();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        mViewMain = inflater.inflate(R.layout.fragment_dashboard_progress, container, false);

        mViewProgress = (ViewCircle) mViewMain.findViewById(R.id.dashboard_progress_progress);

        mViewLabel = (TextView) mViewMain.findViewById(R.id.dashboard_progress_title);
        mViewButton1 = (Button) mViewMain.findViewById(R.id.dashboard_progress_button1);
        mViewButton2 = (Button) mViewMain.findViewById(R.id.dashboard_progress_button2);

        Bundle bundle = new Bundle();
        bundle.putString(FirebaseAnalytics.Param.ITEM_NAME, "Sync Start");
        mActivityMain.FirebaseLog(LogEvent.Event.SYNC_WATCH_DATA, bundle);
        return mViewMain;
    }

    @Override
    public void onResume() {
        super.onResume();

        Intent intent = new Intent(mActivityMain, ServerPushService.class);
        intent.putExtra("PAUSE", true);
        mActivityMain.startService(intent);

        mDevice = mActivityMain.mContactStack.isEmpty() ?
                mActivityMain.mOperator.getFocusKid() :
                (WatchContact.Kid) mActivityMain.mContactStack.pop();

        if (mDevice == null)
            mDevice = new WatchContact.Kid();

        List<WatchEvent> eventList = mActivityMain.mOperator.getEventsForSync(mDevice);
        mVoiceAlertList = new ArrayList<>();
        for (WatchEvent event : eventList) {
            mVoiceAlertList.add(new BLEMachine.VoiceAlert((byte) event.mAlert, event.mAlertTimeStamp));
        }

        mMacAddress = ServerMachine.getMacAddress(mDevice.mMacId);
        Log.d("swing", "Kid mac " + mMacAddress);

        viewSync();
        mServerSyncFinish = false;
        mActivityMain.mOperator.resumeSync(mFinishListener, "", "");
    }

    @Override
    public void onPause() {
        bleSearchCancel();
        mActivityMain.mBLEMachine.Cancel();

        if (!mServerSyncFinish)
            mActivityMain.mServiceMachine.Restart();

        Intent intent = new Intent(mActivityMain, ServerPushService.class);
        mActivityMain.startService(intent);

        super.onPause();
    }

    WatchOperator.finishListener mFinishListener = new WatchOperator.finishListener() {
        @Override
        public void onFinish(Object arg) {
            mServerSyncFinish = true;
        }

        @Override
        public void onFailed(String Command, int statusCode) {
            mServerSyncFinish = true;
        }
    };


    @Override
    public ViewFragmentConfig getConfig() {
        return new ViewFragmentConfig(
                getResources().getString(R.string.title_sync), true, true, false,
                R.mipmap.city_california, R.mipmap.icon_left, ActivityMain.RESOURCE_HIDE);
    }

    @Override
    public void onToolbarAction1() {
        mActivityMain.popFragment();
    }

    private int mServerSyncState = 0;
    private ViewCircle.OnProgressListener mServerSyncProgressListener = new ViewCircle.OnProgressListener() {
        @Override
        public void onProgress(ViewCircle view, int begin, int end) {
            switch(mServerSyncState) {
                case 0:
                    if (mServerSyncFinish) {
                        Bundle bundle = new Bundle();
                        bundle.putString(FirebaseAnalytics.Param.ITEM_NAME, "Server Sync Finished");
                        mActivityMain.FirebaseLog(LogEvent.Event.SYNC_WATCH_DATA, bundle);
                        mActivityMain.mOperator.updateActivity(mActivityUpdateListener, mDevice.mId);
                        mServerSyncState = 1;
                    }
                    break;
                case 1:
                    if (mActivityUpdateFinish) {
                        viewSearching();
                        bleSearchStart();
                        mServerSyncState = 2;
                    }
                    break;
                case 2:
                    mServerSyncFinish = false;
                    mActivityUpdateFinish = false;
                    mServerSyncState = 0;
                    break;
            }
        }
    };

    private ViewCircle.OnProgressListener mSearchProgressListener = new ViewCircle.OnProgressListener() {
        @Override
        public void onProgress(ViewCircle view, int begin, int end) {
            mSearchTimeout--;

            if (mSearchResult != null && mMacAddress.equals(mSearchResult.mAddress)) {
                viewFound();
            } else if (mSearchTimeout == 0) {
                Bundle bundle = new Bundle();
                bundle.putString(FirebaseAnalytics.Param.ITEM_NAME, "Search Swing Watch Timeout");
                mActivityMain.FirebaseLog(LogEvent.Event.SYNC_WATCH_DATA, bundle);
                viewNotFound();
                bleSearchCancel();
            }
        }
    };

    private ViewCircle.OnProgressListener mSyncProgressListener = new ViewCircle.OnProgressListener() {
        @Override
        public void onProgress(ViewCircle view, int begin, int end) {
            //mSyncTimeout--;

            if (mSyncState == SYNC_STATE_SUCCESS) {
                viewCompleted();
            } else if (mSyncTimeout == 0 || mSyncState == SYNC_STATE_FAIL) {
                viewNotFound();
                bleSyncCancel();
            }
        }
    };

    private ViewCircle.OnProgressListener mUploadProgressListener = new ViewCircle.OnProgressListener() {
        @Override
        public void onProgress(ViewCircle view, int begin, int end) {
            mUploadTimeout--;

            if (true) {
                // Gio Say: asynchronous upload
                viewDownload();

            } else if (mUploadTimeout == 0) {
                viewServerFailed();
            }
        }
    };

    private ViewCircle.OnProgressListener mDownloadProgressListener = new ViewCircle.OnProgressListener() {
        @Override
        public void onProgress(ViewCircle view, int begin, int end) {
            mDownloadTimeout--;

            if (mActivityUpdateFinish) {
                viewCompleted();
            } else if (mDownloadTimeout == 0) {
                viewServerFailed();
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
            mActivityMain.selectFragment(FragmentDashboardEmotion.class.getName(), null);
        }
    };

    private Button.OnClickListener mSyncListener = new Button.OnClickListener() {
        @Override
        public void onClick(View view) {
            viewSyncing();
            bleSyncStart();
        }
    };

    private void bleSearchStart() {
        mActivityMain.mBLEMachine.Search(mOnSearchListener, mMacAddress);
        mSearchResult = null;
    }

    private void bleSearchCancel() {
        mActivityMain.mBLEMachine.Search(null, "");
    }

    private void bleSyncStart() {
        if (mSearchResult != null)
            mActivityMain.mBLEMachine.Sync(mOnSyncListener, mSearchResult, mVoiceAlertList);
        mSyncState = SYNC_STATE_INIT;
    }

    private void bleSyncCancel() {
        mActivityMain.mBLEMachine.Disconnect();
    }

    private void viewSync() {
        mViewLabel.setText(getResources().getString(R.string.signup_profile_processing));

        mViewButton1.setVisibility(View.INVISIBLE);
        mViewButton1.setOnClickListener(null);

        mViewButton2.setVisibility(View.INVISIBLE);
        mViewButton2.setOnClickListener(null);

        mViewProgress.setOnProgressListener(mServerSyncProgressListener);
        mViewProgress.setStrokeBeginEnd(0, 10);
        mViewProgress.startProgress(PROGRESS_INTERVAL, -1, -1);
    }

    private void viewSearching() {
        mViewLabel.setText(getResources().getString(R.string.dashboard_progress_searching));

        mViewButton1.setVisibility(View.INVISIBLE);
        mViewButton1.setOnClickListener(null);

        mViewButton2.setVisibility(View.INVISIBLE);
        mViewButton2.setOnClickListener(null);

        mSearchTimeout = SEARCH_TIMEOUT;
        mViewProgress.setOnProgressListener(mSearchProgressListener);
        mViewProgress.setStrokeBeginEnd(0, 10);
        mViewProgress.startProgress(PROGRESS_INTERVAL, -1, -1);
    }

    private void viewFound() {
        Bundle bundle = new Bundle();
        bundle.putString(FirebaseAnalytics.Param.ITEM_NAME, "Watch Found");
        bundle.putString(FirebaseAnalytics.Param.ITEM_ID, mDevice.mMacId);
        mActivityMain.FirebaseLog(LogEvent.Event.SYNC_WATCH_DATA, bundle);

        mViewLabel.setText(getResources().getString(R.string.dashboard_progress_found));
        mViewButton1.setText(getResources().getString(R.string.dashboard_progress_sync_now));

        mViewButton1.setVisibility(View.VISIBLE);
        mViewButton1.setOnClickListener(mSyncListener);

        mViewButton2.setVisibility(View.INVISIBLE);
        mViewButton2.setOnClickListener(null);

        mViewProgress.stopProgress();
        mViewProgress.setOnProgressListener(null);
        mViewProgress.setActive(false);
    }

    private void viewUpload() {
        mViewLabel.setText(getResources().getString(R.string.dashboard_progress_syncing));

        mViewButton1.setVisibility(View.INVISIBLE);
        mViewButton1.setOnClickListener(null);

        mViewButton2.setVisibility(View.INVISIBLE);
        mViewButton2.setOnClickListener(null);

        mUploadTimeout = UPLOAD_TIMEOUT;
        mViewProgress.setOnProgressListener(mUploadProgressListener);
        mViewProgress.setStrokeBeginEnd(0, 10);
        mViewProgress.startProgress(PROGRESS_INTERVAL, -1, -1);
    }

    private void viewDownload() {
        mViewLabel.setText(getResources().getString(R.string.dashboard_progress_syncing));

        mViewButton1.setVisibility(View.INVISIBLE);
        mViewButton1.setOnClickListener(null);

        mViewButton2.setVisibility(View.INVISIBLE);
        mViewButton2.setOnClickListener(null);

        mDownloadTimeout = DOWNLOAD_TIMEOUT;
        mViewProgress.setOnProgressListener(mDownloadProgressListener);
        mViewProgress.setStrokeBeginEnd(0, 10);
        mViewProgress.startProgress(PROGRESS_INTERVAL, -1, -1);
    }

    private void viewSyncing() {
        Bundle bundle = new Bundle();
        bundle.putString(FirebaseAnalytics.Param.ITEM_NAME, "Start to Sync Data");
        bundle.putString(FirebaseAnalytics.Param.ITEM_ID, mDevice.mMacId);
        mActivityMain.FirebaseLog(LogEvent.Event.SYNC_WATCH_DATA, bundle);

        mViewLabel.setText(getResources().getString(R.string.dashboard_progress_syncing));

        mViewButton1.setVisibility(View.INVISIBLE);
        mViewButton1.setOnClickListener(null);

        mViewButton2.setVisibility(View.INVISIBLE);
        mViewButton2.setOnClickListener(null);

        mSyncTimeout = SYNC_TIMEOUT;
        mViewProgress.setOnProgressListener(mSyncProgressListener);
        mViewProgress.setStrokeBeginEnd(0, 10);
        mViewProgress.startProgress(PROGRESS_INTERVAL, -1, -1);
    }

    private void viewCompleted() {
        Bundle bundle = new Bundle();
        bundle.putString(FirebaseAnalytics.Param.ITEM_NAME, "Sync Data Completed");
        bundle.putString(FirebaseAnalytics.Param.ITEM_ID, mDevice.mMacId);
        mActivityMain.FirebaseLog(LogEvent.Event.SYNC_WATCH_DATA, bundle);

        mViewLabel.setText(getResources().getString(R.string.dashboard_progress_completed));
        mViewButton1.setText(getResources().getString(R.string.dashboard_progress_dashboard));

        mViewButton1.setVisibility(View.VISIBLE);
        mViewButton1.setOnClickListener(mExitListener);

        mViewButton2.setVisibility(View.INVISIBLE);
        mViewButton2.setOnClickListener(null);

        mViewProgress.stopProgress();
        mViewProgress.setOnProgressListener(null);
        mViewProgress.setActive(true);
    }

    private void viewNotFound() {
        Bundle bundle = new Bundle();
        bundle.putString(FirebaseAnalytics.Param.ITEM_NAME, "Swing Watch Not Found");
        bundle.putString(FirebaseAnalytics.Param.ITEM_ID, mDevice.mMacId);
        mActivityMain.FirebaseLog(LogEvent.Event.SYNC_WATCH_DATA, bundle);

        mViewLabel.setText(getResources().getString(R.string.dashboard_progress_not_found));

        mViewButton1.setText(getResources().getString(R.string.dashboard_progress_again));
        mViewButton2.setText(getResources().getString(R.string.dashboard_progress_last));

        mViewButton1.setVisibility(View.VISIBLE);
        mViewButton1.setOnClickListener(mAgainListener);

        mViewButton2.setVisibility(View.VISIBLE);
        mViewButton2.setOnClickListener(mExitListener);

        mViewProgress.stopProgress();
        mViewProgress.setOnProgressListener(null);
        mViewProgress.setActive(false);
    }

    private void viewServerFailed() {
        Bundle bundle = new Bundle();
        bundle.putString(FirebaseAnalytics.Param.ITEM_NAME, "Can't upload data to server");
        bundle.putString(FirebaseAnalytics.Param.ITEM_ID, mDevice.mMacId);
        mActivityMain.FirebaseLog(LogEvent.Event.SYNC_WATCH_DATA, bundle);

        mViewLabel.setText(getResources().getString(R.string.dashboard_progress_server_not_reach));

        mViewButton1.setText(getResources().getString(R.string.dashboard_progress_again));
        mViewButton2.setText(getResources().getString(R.string.dashboard_progress_last));

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
        public void onSync(int resultCode, ArrayList<WatchActivityRaw> result) {
            if (resultCode == SYNC_RESULT_SUCCESS) {
                for (WatchActivityRaw res : result) {
                    mActivityMain.mOperator.pushUploadItem(res);
                }
                mSyncState = SYNC_STATE_SUCCESS;
                mActivityMain.mBLEMachine.FirmwareVersion(mOnFirmwareListener, mSearchResult.mAddress);
            } else {
                // Todo : first connect?
                mSyncState = SYNC_STATE_FAIL;
            }

        }

        @Override
        public void onBattery(WatchBattery battery) {
            Log.d("Battery: ",  String.valueOf(battery));
            mActivityMain.mOperator.mFocusBatteryName = mDevice.mName;
            mActivityMain.mOperator.mFocusBatteryValue = battery.batteryLife;

            mActivityMain.mOperator.pushUploadBattery(battery);
        }
    };

    BLEMachine.onFirmwareListener mOnFirmwareListener = new BLEMachine.onFirmwareListener() {
        @Override
        public void onFirmwareVersion(String firmwareVersion) {
            Log.d("Firmwareversion: ",  firmwareVersion + " " + ServerMachine.getMacID(mSearchResult.mAddress));

            mSearchResult.mFirmwareVersion = firmwareVersion;
            mActivityMain.mServiceMachine.deviceFirmwareUpload(firmwareVersion, ServerMachine.getMacID(mSearchResult.mAddress));
            firmwareVersion = firmwareVersion.replace("KDV00", "KDV01");
            mActivityMain.mOperator.updateFirmwareVersion(firmwareVersion, ServerMachine.getMacID(mSearchResult.mAddress));
        }
    };

    WatchOperator.finishListener mActivityUpdateListener = new WatchOperator.finishListener() {
        @Override
        public void onFinish(Object arg) {
            mActivityUpdateFinish = true;
        }

        @Override
        public void onFailed(String Command, int statusCode) {
            mActivityUpdateFinish = true;
            Toast.makeText(mActivityMain, Command, Toast.LENGTH_SHORT).show();
        }
    };
}
