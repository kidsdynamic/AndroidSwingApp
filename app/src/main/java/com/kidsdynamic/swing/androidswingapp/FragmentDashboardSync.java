package com.kidsdynamic.swing.androidswingapp;

import android.app.ProgressDialog;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import java.util.Locale;

/**
 * Created by 03543 on 2016/12/19.
 */

public class FragmentDashboardSync extends ViewFragment {
    private ActivityMain mActivityMain;
    private View mViewMain;

    private TextView mViewMessage;
    private Button mViewRequest;
    private Button mViewProfile;

    private ProgressDialog mProcessDialog;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mActivityMain = (ActivityMain) getActivity();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        mViewMain = inflater.inflate(R.layout.fragment_dashboard_sync, container, false);

        mViewMessage = (TextView) mViewMain.findViewById(R.id.dashboard_sync_message);

        mViewRequest = (Button) mViewMain.findViewById(R.id.dashboard_sync_request);
        mViewProfile = (Button) mViewMain.findViewById(R.id.dashboard_sync_profile);

        return mViewMain;
    }

    @Override
    public ViewFragmentConfig getConfig() {
        return new ViewFragmentConfig(
                getResources().getString(R.string.title_dashboard), true, true, false,
                ActivityMain.RESOURCE_IGNORE, ActivityMain.RESOURCE_HIDE, ActivityMain.RESOURCE_HIDE);
    }

    @Override
    public void onResume() {
        super.onResume();

        WatchContact.Kid focus = mActivityMain.mOperator.getFocusKid();

        if (focus != null) {
            setTitle(focus.mName);
            mViewMessage.setVisibility(View.INVISIBLE);
            mViewRequest.setVisibility(View.INVISIBLE);
            mViewProfile.setVisibility(View.INVISIBLE);

            mProcessDialog = ProgressDialog.show(mActivityMain,
                    getResources().getString(R.string.dashboard_sync_process),
                    getResources().getString(R.string.dashboard_sync_waiting), true);
            mActivityMain.mOperator.updateActivity(mUpdateActivityListener, focus.mId);

        } else if (mActivityMain.mOperator.getRequestToList().size() > 0) {
            mViewMessage.setVisibility(View.VISIBLE);
            mViewRequest.setVisibility(View.VISIBLE);
            mViewProfile.setVisibility(View.VISIBLE);

            mViewMessage.setText(getResources().getString(R.string.dashboard_sync_pending));

        } else {
            mViewMessage.setVisibility(View.VISIBLE);
            mViewRequest.setVisibility(View.VISIBLE);
            mViewProfile.setVisibility(View.VISIBLE);

            mViewMessage.setText(getResources().getString(R.string.dashboard_sync_no_data));
        }
    }

    @Override
    public void onPause() {
        if (mProcessDialog != null)
            mProcessDialog.dismiss();
        super.onPause();
    }

    WatchOperator.finishListener mUpdateActivityListener = new WatchOperator.finishListener() {
        @Override
        public void onFinish(String msg, Object arg) {

            mProcessDialog.dismiss();
            if (msg.equals(""))
                mActivityMain.selectFragment(FragmentDashboardSelect.class.getName(), null);
            else
                Toast.makeText(mActivityMain, msg, Toast.LENGTH_SHORT).show();
        }

        @Override
        public void onFailed(String Command, int statusCode) {

        }
    };

    private void setTitle(String name) {
        String string = String.format(Locale.getDefault(),
                getResources().getString(R.string.dashboard_sync_owner), name);
        mActivityMain.toolbarSetTitle(string, false);
    }

}
