package com.kidsdynamic.swing.androidswingapp;

import android.app.Service;
import android.content.Intent;
import android.os.Handler;
import android.os.IBinder;
import android.util.Log;

/**
 * Created by weichigio on 2017/2/1.
 */

public class ServerPushService extends Service {
    private WatchOperator mWatchOperator;
    private boolean token_available = false;
    public ServerMachine mServiceMachine;
    public Config mConfig;
    private Handler mHandler = new Handler();

    private WatchOperator.Upload mUploadItem = null;

    @Override
    public void onCreate() {
        super.onCreate();
        Log.d("ServiceUpload", "onCreate");
        mConfig = new Config(this, null);
        mWatchOperator = new WatchOperator(this);
        mServiceMachine = new ServerMachine(this);
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
            mWatchOperator.UploadItemDelete(mUploadItem);
            mUploadItem = null;
        }

        @Override
        public void onConflict(int statusCode) {
            mWatchOperator.UploadItemDelete(mUploadItem);
            mUploadItem = null;
        }

        @Override
        public void onFail(int statusCode) {
            mUploadItem = null;
        }
    };

    private final Runnable DoPush = new Runnable() {
        @Override
        public void run() {
            boolean stop = false;
            if (!token_available) {
                if (mConfig.getString(Config.KEY_MAIL).equals("") || mConfig.getString(Config.KEY_PASSWORD).equals("") || mConfig.getString(Config.KEY_AUTH_TOKEN).equals("")) {
                    Log.d("PushService", "No login information.");
                } else {
                    mServiceMachine.userIsTokenValid(mUserIsTokenValidListener, mConfig.getString(Config.KEY_MAIL), mConfig.getString(Config.KEY_AUTH_TOKEN));
                    stop = true;
                }

            } else if (mUploadItem == null && mWatchOperator.UploadItemCount() > 0) {
                mUploadItem = mWatchOperator.UploadItemGet();
                Log.d("PushService", " MAC " + mUploadItem.mMacId + " time " + mUploadItem.mTime);
                mServiceMachine.activityUploadRawData(mActivityUploadRawDataListener, mUploadItem.mIndoorActivity, mUploadItem.mOutdoorActivity, mUploadItem.mTime, mUploadItem.mMacId);
            }
            if (!stop)
                mHandler.postDelayed(this, 1000);
        }
    };

    ServerMachine.userIsTokenValidListener mUserIsTokenValidListener = new ServerMachine.userIsTokenValidListener() {
        @Override
        public void onValidState(boolean valid) {
            if (valid) {
                mServiceMachine.setAuthToken(mConfig.getString(Config.KEY_AUTH_TOKEN));
                token_available = true;
                mHandler.postDelayed(DoPush, 1);
            } else {
                mServiceMachine.userLogin(mUserLoginListener, mConfig.getString(Config.KEY_MAIL), mConfig.getString(Config.KEY_PASSWORD));
            }
        }

        @Override
        public void onFail(int statusCode) {
            mServiceMachine.userLogin(mUserLoginListener, mConfig.getString(Config.KEY_MAIL), mConfig.getString(Config.KEY_PASSWORD));
        }
    };

    ServerMachine.userLoginListener mUserLoginListener = new ServerMachine.userLoginListener() {
        @Override
        public void onSuccess(int statusCode, ServerGson.user.login.response result) {
            token_available = true;
            mConfig.setString(Config.KEY_AUTH_TOKEN, result.access_token);
            mServiceMachine.setAuthToken(result.access_token);
            mHandler.postDelayed(DoPush, 1);
        }

        @Override
        public void onFail(int statusCode) {
            Log.d("PushService", "Login failed!");
            mHandler.postDelayed(DoPush, 1000);
        }
    };
}
