package com.kidsdynamic.swing.androidswingapp;

import android.app.Fragment;
import android.os.Bundle;
import android.os.Handler;
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

    private Runnable mRunnable = new Runnable() {
        @Override
        public void run() {

            if (mActivityMain.mRequestingPermission != 0) {
                mHandler.postDelayed(this, 2000);
                return;
            }

            String name;
            if (mActivityMain.mConfig.getString(Config.KEY_LANGUAGE).equals("")) {
                name = FragmentSignupLanguage.class.getName();
            } else {
                if (mActivityMain.mConfig.getString(Config.KEY_AUTH_TOKEN).equals("")) {
                    name = FragmentSignupLogin.class.getName();
                } else {
                    name = FragmentSyncNow.class.getName();
                }
            }

            Fragment fragment = Fragment.instantiate(mActivityMain, name, null);
            getFragmentManager()
                    .beginTransaction()
                    .replace(R.id.main_fragment, fragment, name)
                    .commit();
        }
    };

}
