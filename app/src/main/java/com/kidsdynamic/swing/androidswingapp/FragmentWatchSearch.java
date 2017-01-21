package com.kidsdynamic.swing.androidswingapp;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import java.util.ArrayList;

/**
 * Created by 03543 on 2016/12/31.
 */

public class FragmentWatchSearch extends ViewFragment {

    private ActivityMain mActivityMain;
    private View mViewMain;
    private ImageView mViewBack;
    private ViewProgressCircle mViewProgress;

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

        mViewProgress = (ViewProgressCircle) mViewMain.findViewById(R.id.watch_search_progress);
        mViewProgress.setOnProgressListener(mProgressListener);

        Handler handle = new Handler();
        handle.post(new Runnable() {
            @Override
            public void run() {
                mViewProgress.start();
                searchStart();
            }
        });

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
        mActivityMain.popFragment();
    }

    private View.OnClickListener mBackOnClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            onToolbarAction1();
        }
    };

    private ViewProgressCircle.OnProgressListener mProgressListener = new ViewProgressCircle.OnProgressListener() {
        @Override
        public void onProgress(ViewProgressCircle view, int progress, int total) {

            if(progress >= total) {
                searchStop();
                showSimulateDialog();
            }
        }
    };

    private void showSimulateDialog() {
        // todo: simulation action, replace by real BT response
        AlertDialog.Builder dialog = new AlertDialog.Builder(mActivityMain);
        dialog.setTitle("It is a Test!");
        dialog.setMessage("Do you find Swing Watch?");

        dialog.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                ArrayList<WatchContact> list = new ArrayList<WatchContact>();
                WatchContact.Device device;

                device = new WatchContact.Device(null, "Tobias Martin", ViewWatchContact.MODE_BIND);
                device.mBound = true;
                list.add(device);

                device = new WatchContact.Device(null, "SwingWatch568DANG5E", ViewWatchContact.MODE_BIND);
                device.mBound = false;
                list.add(device);

                Bundle bundle = new Bundle();
                bundle.putSerializable(ViewFragment.BUNDLE_KEY_DEVICE_LIST, list);

                mActivityMain.selectFragment(FragmentWatchSelect.class.getName(), bundle);
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

    private void searchStart() {
        // todo: start search here!
        Log.d("xxx", "searchStart");
    }

    private void searchStop() {
        // todo: pause search here!
        Log.d("xxx", "searchStop");
    }
}
