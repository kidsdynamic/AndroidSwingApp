package com.kidsdynamic.swing.androidswingapp;

import android.app.AlertDialog;
import android.app.Fragment;
import android.content.DialogInterface;
import android.os.Bundle;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

/**
 * Created by 03543 on 2016/12/31.
 */

public class FragmentWatchSearch extends ViewFragment {
    private ActivityMain mActivityMain;
    private View mViewMain;
    private ImageView mViewBack;

    private Handler mSearchHandler;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mActivityMain = (ActivityMain) getActivity();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        mViewMain = inflater.inflate(R.layout.fragment_watch_search, container, false);

        mViewBack = (ImageView) mViewMain.findViewById(R.id.fragment_back);
        mViewBack.setOnClickListener(mBackOnClickListener);

        searchStart();

        mSearchHandler = new Handler();
        mSearchHandler.postDelayed(mSearchRunnable, 10000);

        return mViewMain;
    }

    @Override
    public ViewFragmentConfig getConfig() {
        return new ViewFragmentConfig("Watch", false, false,
                ActivityMain.RESOURCE_IGNORE, ActivityMain.RESOURCE_IGNORE, ActivityMain.RESOURCE_IGNORE);
    }

    @Override
    public void onToolbarAction1() {
        searchStop();
        mSearchHandler.removeCallbacks(mSearchRunnable);
        mActivityMain.popFragment();
    }

    private View.OnClickListener mBackOnClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            onToolbarAction1();
        }
    };

    private Runnable mSearchRunnable = new Runnable() {
        @Override
        public void run() {
            searchStop();

            // todo: simulation action, replace by real BT response
            AlertDialog.Builder dialog = new AlertDialog.Builder(mActivityMain);
            dialog.setTitle("It is a Test!");
            dialog.setMessage("Do you find Swing Watch?");

            dialog.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    mActivityMain.selectFragment(FragmentWatchSelect.class.getName(), null);
                }
            });

            dialog.setNegativeButton("No", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    mActivityMain.selectFragment(FragmentWatchSorry.class.getName(), null);
                }
            });

            dialog.show();
        }
    };

    private void searchStart() {
        // todo: start search here!
    }

    private void searchStop() {
        // todo: pause search here!
    }
}
