package com.kidsdynamic.swing.androidswingapp;

import android.app.Fragment;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;

/**
 * Created by 03543 on 2017/1/4.
 */

public class FragmentBoot extends ViewFragment {
    private ActivityMain mActivityMain;
    private View mViewMain;

    private Handler mHandler;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mActivityMain = (ActivityMain) getActivity();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        mViewMain = inflater.inflate(R.layout.fragment_boot, container, false);

        mHandler = new Handler();
        mHandler.postDelayed(mRunnable, 2000);

        return mViewMain;
    }

    @Override
    public ViewFragmentConfig getConfig() {
        return new ViewFragmentConfig("Boot", false, false, false,
                ActivityMain.RESOURCE_IGNORE, ActivityMain.RESOURCE_IGNORE, ActivityMain.RESOURCE_IGNORE);
    }
/*
    private Runnable mRunnable = new Runnable() {
        @Override
        public void run() {
            mActivityMain.selectFragment(FragmentWatchRequest.class.getName(), null);
        }
    };
*/
    private Runnable mRunnable = new Runnable() {
        @Override
        public void run() {

            if (mActivityMain.mRequestingPermission != 0) {
                mHandler.postDelayed(this, 2000);
                return;
            }

            String name = "";
            if (mActivityMain.mConfig.getString(Config.KEY_LANGUAGE).equals("")) {
                name = FragmentSignupLanguage.class.getName();
            } else {
                if (mActivityMain.mConfig.getString(Config.KEY_AUTH_TOKEN).equals("")) {
                    name = FragmentSignupLogin.class.getName();
                } else {
                    //name = FragmentSyncNow.class.getName();
                    mActivityMain.mServiceMachine.userIsTokenValid(
                            mUserIsTokenValidListener,
                            mActivityMain.mConfig.getString(Config.KEY_MAIL),
                            mActivityMain.mConfig.getString(Config.KEY_AUTH_TOKEN));

                }
            }

            if (!name.equals("")) {
                Fragment fragment = Fragment.instantiate(mActivityMain, name, null);
                getFragmentManager()
                        .beginTransaction()
                        .replace(R.id.main_fragment, fragment, name)
                        .commit();
            }
        }
    };

    private void gotoSyncNow() {
        Fragment fragment = Fragment.instantiate(mActivityMain, FragmentSyncNow.class.getName(), null);
        getFragmentManager()
                .beginTransaction()
                .replace(R.id.main_fragment, fragment, FragmentSyncNow.class.getName())
                .commit();
    }

    ServerMachine.userIsTokenValidListener mUserIsTokenValidListener = new ServerMachine.userIsTokenValidListener() {
        @Override
        public void onValidState(boolean valid) {
            if (valid) {
                mActivityMain.mServiceMachine.setAuthToken(mActivityMain.mConfig.getString(Config.KEY_AUTH_TOKEN));
                gotoSyncNow();
            } else {
                mActivityMain.mServiceMachine.userLogin(mUserLoginListener, mActivityMain.mConfig.getString(Config.KEY_MAIL), mActivityMain.mConfig.getString(Config.KEY_PASSWORD));
            }
        }

        @Override
        public void onFail(int statusCode) {
            //Log.d("FragmentBoot", "Offline.");
            //gotoSyncNow();
            mActivityMain.mServiceMachine.userLogin(mUserLoginListener, mActivityMain.mConfig.getString(Config.KEY_MAIL), mActivityMain.mConfig.getString(Config.KEY_PASSWORD));
        }
    };

    ServerMachine.userLoginListener mUserLoginListener = new ServerMachine.userLoginListener() {
        @Override
        public void onSuccess(int statusCode, ServerGson.user.login.response result) {
            mActivityMain.mConfig.setString(Config.KEY_AUTH_TOKEN, result.access_token);
            mActivityMain.mServiceMachine.setAuthToken(result.access_token);
            gotoSyncNow();
        }

        @Override
        public void onFail(int statusCode) {
            Log.d("FragmentBoot", "Offline.");
            gotoSyncNow();
        }
    };
}
