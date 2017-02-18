package com.kidsdynamic.swing.androidswingapp;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.Locale;

/**
 * Created by 03543 on 2016/12/19.
 */

public class FragmentDashboardSync extends ViewFragment {
    private ActivityMain mActivityMain;
    private View mViewMain;

    private ViewCircle mViewProgress;
    private TextView mViewMessage;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mActivityMain = (ActivityMain) getActivity();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        mViewMain = inflater.inflate(R.layout.fragment_dashboard_sync, container, false);

        mViewProgress = (ViewCircle) mViewMain.findViewById(R.id.dashboard_sync_progress);
        mViewProgress.setOnProgressListener(mProgressListener);

        mViewMessage = (TextView) mViewMain.findViewById(R.id.dashboard_sync_message);

        return mViewMain;
    }

    @Override
    public ViewFragmentConfig getConfig() {
        return new ViewFragmentConfig("Dashboard", true, true, false,
                ActivityMain.RESOURCE_IGNORE, ActivityMain.RESOURCE_HIDE, ActivityMain.RESOURCE_HIDE);
    }

    @Override
    public void onResume() {
        super.onResume();

        WatchContact.Kid focus = mActivityMain.mOperator.getFocusKid();

        if (focus != null) {
            setTitle(focus.mName);

            mViewProgress.setStrokeBeginEnd(0, 10);
            mViewProgress.startProgress(30, -1, -1);
        } else {
            mViewProgress.setActive(false);
            mViewMessage.setText("You Haven't Any Watch");
        }
    }

    @Override
    public void onPause() {
        super.onPause();

        mViewProgress.stopProgress();
    }

    private int debug_count_down = 100;
    private ViewCircle.OnProgressListener mProgressListener = new ViewCircle.OnProgressListener() {
        @Override
        public void onProgress(ViewCircle view, int begin, int end) {
            // TEST
            debug_count_down--;
            if (debug_count_down == 0) {
                mViewProgress.stopProgress();
                mActivityMain.selectFragment(FragmentDashboardEmotion.class.getName(), null);
            }
            /////////////////
        }
    };

    private void setTitle(String name) {
        String string = String.format(Locale.getDefault(), "%s's Watch", name);
        mActivityMain.toolbarSetTitle(string, false);
    }

}
