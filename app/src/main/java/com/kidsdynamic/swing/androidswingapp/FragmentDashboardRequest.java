package com.kidsdynamic.swing.androidswingapp;

import android.app.ProgressDialog;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import java.util.List;
import java.util.Locale;

/**
 * Created by 03543 on 2016/12/19.
 */

public class FragmentDashboardRequest extends ViewFragment {
    private ActivityMain mActivityMain;
    private View mViewMain;

    private TextView mViewMessage;
    private Button mViewRequest;
    private Button mViewProfile;
    private Button mAddWatch;

    private ProgressDialog mProcessDialog;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mActivityMain = (ActivityMain) getActivity();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        mViewMain = inflater.inflate(R.layout.fragment_dashboard_request, container, false);

        mViewMessage = (TextView) mViewMain.findViewById(R.id.dashboard_request_message);

        mViewRequest = (Button) mViewMain.findViewById(R.id.dashboard_request_request);
        mViewRequest.setOnClickListener(mRequestListener);

        mViewProfile = (Button) mViewMain.findViewById(R.id.dashboard_request_profile);
        mViewProfile.setOnClickListener(mProfileListener);

        mAddWatch = (Button) mViewMain.findViewById(R.id.dashboard_device_add);
        mAddWatch.setOnClickListener(mAddDeviceListener);

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

        boolean requested = false;
        List<WatchContact.User> list = mActivityMain.mOperator.getRequestToList();
        if (list != null && list.size() > 0) {
            for (WatchContact.User user : list) {
                if (user.mRequestStatus.equals(WatchContact.User.STATUS_PENDING))
                    requested = true;
            }
        }

        if (requested) {
            mViewMessage.setVisibility(View.VISIBLE);
            mViewRequest.setVisibility(View.INVISIBLE);
            mViewProfile.setVisibility(View.VISIBLE);

            mViewMessage.setText(getResources().getString(R.string.dashboard_request_pending));

        } else {
            mViewMessage.setVisibility(View.VISIBLE);
            mViewRequest.setVisibility(View.VISIBLE);
            mViewProfile.setVisibility(View.VISIBLE);

            mViewMessage.setText(getResources().getString(R.string.dashboard_request_no_data));
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
        public void onFinish(Object arg) {
            mProcessDialog.dismiss();
            mActivityMain.selectFragment(FragmentDashboardEmotion.class.getName(), null);
        }

        @Override
        public void onFailed(String Command, int statusCode) {
            mProcessDialog.dismiss();
            Toast.makeText(mActivityMain, Command, Toast.LENGTH_SHORT).show();
        }
    };

    private void setTitle(String name) {
        String string = String.format(Locale.getDefault(),
                getResources().getString(R.string.dashboard_request_owner), name);
        mActivityMain.toolbarSetTitle(string, false);
    }

    private View.OnClickListener mRequestListener = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            mActivityMain.selectFragment(FragmentProfileRequestTo.class.getName(), null);
        }
    };

    private View.OnClickListener mProfileListener = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            mActivityMain.selectFragment(FragmentProfileMain.class.getName(), null);
        }
    };

    private View.OnClickListener mAddDeviceListener = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            mActivityMain.selectFragment(FragmentProfileSearch.class.getName(), null);
        }
    };


}
