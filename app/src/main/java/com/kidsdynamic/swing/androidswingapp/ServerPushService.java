package com.kidsdynamic.swing.androidswingapp;

import android.app.Service;
import android.content.Intent;
import android.os.Handler;
import android.os.IBinder;
import android.util.Log;

import static com.kidsdynamic.swing.androidswingapp.ServerMachine.REQUEST_UPLOAD_TAG;

/**
 * Created by weichigio on 2017/2/1.
 */

public class ServerPushService extends Service {
    private WatchDatabase mWatchDatabase;
    private boolean token_available = false;
    public ServerMachine mServiceMachine;
    public ActivityConfig mConfig;
    private Handler mHandler = new Handler();

    private WatchActivityRaw mUploadItem = null;

    @Override
    public void onCreate() {
        super.onCreate();
        Log.d("ServiceUpload", "onCreate");
        mConfig = new ActivityConfig(this, null);
        mWatchDatabase = new WatchDatabase(this);
        mServiceMachine = new ServerMachine(this, REQUEST_UPLOAD_TAG);
        mServiceMachine.Start();
    }

    @Override
    public void onDestroy() {
        Log.d("ServiceUpload", "onDestroy");
        mServiceMachine.Stop();
        super.onDestroy();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Log.d("ServiceUpload", "onStartCommand");
        mHandler.removeCallbacks(DoPush);
        if (intent != null && !intent.getBooleanExtra("PAUSE", false))
            mHandler.postDelayed(DoPush, 1000);

        return START_STICKY;
    }

    @Override
    public IBinder onBind(Intent intent) {
        // TODO: Return the communication channel to the service.
        throw new UnsupportedOperationException("Not yet implemented");
    }

    ServerMachine.activityUploadRawDataListener mActivityUploadRawDataListener = new ServerMachine.activityUploadRawDataListener() {
        @Override
        public void onSuccess(int statusCode) {
            mWatchDatabase.UploadItemDone(mUploadItem);
            mUploadItem = null;
        }

        @Override
        public void onConflict(int statusCode) {
            mWatchDatabase.UploadItemDone(mUploadItem);
            mUploadItem = null;
        }

        @Override
        public void onFail(String command, int statusCode) {
            if (statusCode == 409)
                mWatchDatabase.UploadItemDone(mUploadItem);
            mUploadItem = null;
        }
    };

    private final Runnable DoPush = new Runnable() {
        @Override
        public void run() {
            boolean stop = false;
            if (!token_available) {
                if (mConfig.getString(ActivityConfig.KEY_MAIL).equals("") || mConfig.getString(ActivityConfig.KEY_PASSWORD).equals("") || mConfig.getString(ActivityConfig.KEY_AUTH_TOKEN).equals("")) {
                    Log.d("PushService", "No login information.");
                } else {
                    mServiceMachine.userIsTokenValid(mUserIsTokenValidListener, mConfig.getString(ActivityConfig.KEY_MAIL), mConfig.getString(ActivityConfig.KEY_AUTH_TOKEN));
                    stop = true;
                }

            } else if (mUploadItem == null && mWatchDatabase.UploadItemCount() > 0) {
                mUploadItem = mWatchDatabase.UploadItemGet();
                Log.d("PushService", " MAC " + mUploadItem.mMacId + " time " + mUploadItem.mTime);
                mServiceMachine.activityUploadRawData(mActivityUploadRawDataListener, mUploadItem.mIndoor, mUploadItem.mOutdoor, mUploadItem.mTime, mUploadItem.mMacId);
            }
            if (!stop)
                mHandler.postDelayed(this, 100);
        }
    };

    ServerMachine.userIsTokenValidListener mUserIsTokenValidListener = new ServerMachine.userIsTokenValidListener() {
        @Override
        public void onValidState(boolean valid) {
            if (valid) {
                mServiceMachine.setAuthToken(mConfig.getString(ActivityConfig.KEY_AUTH_TOKEN));
                token_available = true;
                mHandler.postDelayed(DoPush, 1);
            } else {
                mServiceMachine.userLogin(mUserLoginListener, mConfig.getString(ActivityConfig.KEY_MAIL), mConfig.getString(ActivityConfig.KEY_PASSWORD));
            }
        }

        @Override
        public void onFail(String command, int statusCode) {
            mServiceMachine.userLogin(mUserLoginListener, mConfig.getString(ActivityConfig.KEY_MAIL), mConfig.getString(ActivityConfig.KEY_PASSWORD));
        }
    };

    ServerMachine.userLoginListener mUserLoginListener = new ServerMachine.userLoginListener() {
        @Override
        public void onSuccess(int statusCode, ServerGson.user.login.response result) {
            token_available = true;
            mConfig.setString(ActivityConfig.KEY_AUTH_TOKEN, result.access_token);
            mServiceMachine.setAuthToken(result.access_token);
            mHandler.postDelayed(DoPush, 1);
        }

        @Override
        public void onFail(String command, int statusCode) {
            Log.d("PushService", "Login failed!");
            mHandler.postDelayed(DoPush, 1000);
        }
    };
}
