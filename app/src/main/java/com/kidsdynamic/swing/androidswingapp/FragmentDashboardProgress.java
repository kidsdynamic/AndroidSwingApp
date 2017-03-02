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
import java.util.List;

import static com.kidsdynamic.swing.androidswingapp.BLEMachine.SYNC_RESULT_SUCCESS;

/**
 * Created by 03543 on 2017/1/21.
 */

public class FragmentDashboardProgress extends ViewFragment {
    private final int PROGRESS_INTERVAL = 100;

    private final int SEARCH_TIMEOUT = 150;     // 150 * PROGRESS_INTERVAL milliseconds
    private int mSearchTimeout;
    private final int SYNC_TIMEOUT = 1000;      // 1000 * PROGRESS_INTERVAL milliseconds
    private int mSyncTimeout;
    private final int UPLOAD_TIMEOUT = 1000;      // 1000 * PROGRESS_INTERVAL milliseconds
    private int mUploadTimeout;
    private final int DOWNLOAD_TIMEOUT = 1000;      // 1000 * PROGRESS_INTERVAL milliseconds
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
    private boolean mSyncFinish = false;

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

        return mViewMain;
    }

    @Override
    public void onResume() {
        super.onResume();

        viewSearching();

        mDevice = mActivityMain.mContactStack.isEmpty() ?
                new WatchContact.Kid() :
                (WatchContact.Kid) mActivityMain.mContactStack.pop();

        List<WatchEvent> eventList = mActivityMain.mOperator.getEventsForSync(mDevice);
        mVoiceAlertList = new ArrayList<>();
        for (WatchEvent event : eventList)
            mVoiceAlertList.add(new BLEMachine.VoiceAlert((byte) event.mAlert, event.mAlertTimeStamp));

        mMacAddress = ServerMachine.getMacAddress(mDevice.mMacId);
        bleSearchStart();
    }

    @Override
    public void onPause() {
        bleSearchCancel();
        super.onPause();
    }

    @Override
    public ViewFragmentConfig getConfig() {
        return new ViewFragmentConfig(
                getResources().getString(R.string.title_sync), true, true, false,
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
            } else if (mSearchTimeout == 0) {
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
                viewUpload();
            } else if (mSyncTimeout == 0) {
                viewNotFound();
                bleSyncCancel();
            }
        }
    };

    private ViewCircle.OnProgressListener mUploadProgressListener = new ViewCircle.OnProgressListener() {
        @Override
        public void onProgress(ViewCircle view, int begin, int end) {
            mUploadTimeout--;

            if (false) {    // todo: when the update is finish.
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

            if (false) {     // todo: when the download is finish
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
        mSyncFinish = false;
    }

    private void bleSyncCancel() {
        mActivityMain.mBLEMachine.Disconnect();
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
