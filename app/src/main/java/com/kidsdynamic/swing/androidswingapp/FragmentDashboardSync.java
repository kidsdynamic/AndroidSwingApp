package com.kidsdynamic.swing.androidswingapp;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import java.util.Locale;

/**
 * Created by 03543 on 2016/12/19.
 */

public class FragmentDashboardSync extends ViewFragment {
    private ActivityMain mActivityMain;
    private View mViewMain;

    private ViewCircle mViewProgress;
    private TextView mViewMessage;

    private boolean mIsPause = true;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mActivityMain = (ActivityMain) getActivity();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        mViewMain = inflater.inflate(R.layout.fragment_dashboard_sync, container, false);

        mViewProgress = (ViewCircle) mViewMain.findViewById(R.id.dashboard_sync_progress);
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
        mIsPause = false;

        WatchContact.Kid focus = mActivityMain.mOperator.getFocusKid();

        if (focus != null) {
            setTitle(focus.mName);

            mViewProgress.setStrokeBeginEnd(0, 10);
            mViewProgress.startProgress(30, -1, -1);
            mActivityMain.mOperator.updateActivity(mUpdateActivityListener, focus.mId);
        } else {
            mViewProgress.setActive(false);
            mViewMessage.setText("You Haven't Any Watch");
        }
    }

    @Override
    public void onPause() {
        super.onPause();
        mIsPause = true;

        mViewProgress.stopProgress();
    }

    WatchOperatorUpdateActivity.finishListener mUpdateActivityListener = new WatchOperatorUpdateActivity.finishListener() {
        @Override
        public void onFinish(String msg) {
            if(mIsPause)
                return;

            mViewProgress.stopProgress();
            if (msg.equals(""))
                mActivityMain.selectFragment(FragmentDashboardChart.class.getName(), null);
            else
                Toast.makeText(mActivityMain, msg, Toast.LENGTH_SHORT).show();
        }
    };

    private void setTitle(String name) {
        String string = String.format(Locale.getDefault(), "%s's Watch", name);
        mActivityMain.toolbarSetTitle(string, false);
    }

}
