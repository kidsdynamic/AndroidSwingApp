package com.kidsdynamic.swing.androidswingapp;

import android.app.Fragment;
import android.os.Bundle;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

/**
 * Created by 03543 on 2016/12/31.
 */

public class FragmentWatchSearch extends Fragment {
    private ActivityMain mActivityMain;
    private View mMainView;

    private TextView mTextViewSearching;

    private Handler mSearchHandler;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mActivityMain = (ActivityMain) getActivity();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        mMainView = inflater.inflate(R.layout.fragment_watch_search, container, false);

        mTextViewSearching = (TextView) mMainView.findViewById(R.id.watch_searching);
        mTextViewSearching.setOnClickListener(mSearchCancelListener);

        mSearchHandler = new Handler();
        mSearchHandler.postDelayed(mSearchRunnable, 3000);

        return mMainView;
    }

    private View.OnClickListener mSearchCancelListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            mSearchHandler.removeCallbacks(mSearchRunnable);
            mActivityMain.selectFragment(FragmentWatchSorry.class.getName(), null);
        }
    };

    private Runnable mSearchRunnable = new Runnable() {
        @Override
        public void run() {
            mActivityMain.selectFragment(FragmentWatchSelect.class.getName(), null);
        }
    };
}
