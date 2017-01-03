package com.kidsdynamic.swing.androidswingapp;

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
        return new ViewFragmentConfig("Boot", false, false,
                ActivityMain.RESOURCE_IGNORE, ActivityMain.RESOURCE_IGNORE, ActivityMain.RESOURCE_IGNORE);
    }

    private Runnable mRunnable = new Runnable() {
        @Override
        public void run() {
            if (mActivityMain.mConfig.getString(Config.KEY_LANGUAGE).equals(""))
                mActivityMain.selectFragment(FragmentSignupLanguage.class.getName(), null);
            else
                mActivityMain.selectFragment(FragmentSignupLogin.class.getName(), null);
        }
    };

}
