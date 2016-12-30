package com.kidsdynamic.swing.androidswingapp;

import android.app.Fragment;
import android.os.Bundle;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

/**
 * Created by 03543 on 2016/12/31.
 */

public class FragmentSignupWatchSearch extends Fragment {
    private MainActivity mMainActivity;
    private View mMainView;

    private Handler mSearchHandler;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mMainActivity = (MainActivity) getActivity();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        mMainView = inflater.inflate(R.layout.fragment_signup_watch_search, container, false);

        mSearchHandler = new Handler();
        mSearchHandler.postDelayed(mSearchRunnable, 3000);

        return mMainView;
    }

    private Runnable mSearchRunnable = new Runnable() {
        @Override
        public void run() {
//            mMainActivity.selectFragment(FragmentSignupWatchPurchase.class.getName(), null);
        }
    };
}
