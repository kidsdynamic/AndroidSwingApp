package com.kidsdynamic.swing.androidswingapp;

import android.app.Dialog;
import android.app.ProgressDialog;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.Toast;

import java.util.ArrayList;

/**
 * Created by 03543 on 2016/12/30.
 */

public class FragmentWatchRequest extends ViewFragment {
    private ActivityMain mActivityMain;
    private View mViewMain;
    private LinearLayout mViewContainer;
    private Button mButtonBack;
    private Button mButtonDashboard;

    private Dialog mProcessDialog = null;
    private ArrayList<WatchContact.Kid> mKidList;
    private int mKidListCounter = 0;


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mActivityMain = (ActivityMain) getActivity();

        mKidList = new ArrayList<>();
        mKidListCounter = 0;
        while (!mActivityMain.mContactStack.isEmpty())
            mKidList.add((WatchContact.Kid) mActivityMain.mContactStack.pop());
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        mViewMain = inflater.inflate(R.layout.fragment_watch_request, container, false);

        mViewContainer = (LinearLayout) mViewMain.findViewById(R.id.watch_request_container);

        mButtonBack = (Button) mViewMain.findViewById(R.id.watch_request_back);
        mButtonBack.setOnClickListener(mOnBackListener);

        mButtonDashboard = (Button) mViewMain.findViewById(R.id.watch_request_dashboard);
        mButtonDashboard.setOnClickListener(mOnDashboardListener);

        return mViewMain;
    }

    @Override
    public void onResume() {
        super.onResume();
        mProcessDialog = ProgressDialog.show(mActivityMain,
                getResources().getString(R.string.watch_request_processing),
                getResources().getString(R.string.watch_request_wait), true);
        getKidAvatar(true);
    }

    @Override
    public void onPause() {
        if (mProcessDialog != null)
            mProcessDialog.dismiss();
        super.onPause();
    }

    private void getKidAvatarFinish() {
        for (WatchContact.Kid kid : mKidList)
            addDevice(kid);
        mProcessDialog.dismiss();
    }

    private void getKidAvatar(boolean start) {
        if (start)
            mKidListCounter = 0;
        else
            mKidListCounter++;

        if (mKidListCounter >= mKidList.size()) {
            getKidAvatarFinish();
            return;
        }

        while (mKidList.get(mKidListCounter).mProfile.equals("")) {
            mKidListCounter++;
            if (mKidListCounter >= mKidList.size()) {
                getKidAvatarFinish();
                return;
            }
        }

        mActivityMain.mServiceMachine.getAvatar(mGetKidAvatarListener, mKidList.get(mKidListCounter).mProfile);
    }

    ServerMachine.getAvatarListener mGetKidAvatarListener = new ServerMachine.getAvatarListener() {
        @Override
        public void onSuccess(Bitmap avatar, String filename) {
            mKidList.get(mKidListCounter).mPhoto = avatar;
            getKidAvatar(false);
        }

        @Override
        public void onFail(String command, int statusCode) {
            getKidAvatar(false);
        }
    };

    @Override
    public ViewFragmentConfig getConfig() {
        return new ViewFragmentConfig("", false, false, false,
                ActivityMain.RESOURCE_IGNORE, ActivityMain.RESOURCE_IGNORE, ActivityMain.RESOURCE_IGNORE);
    }

    private Button.OnClickListener mOnBackListener = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            mActivityMain.selectFragment(FragmentWatchOwner.class.getName(), null);
        }
    };

    private Button.OnClickListener mOnDashboardListener = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            mActivityMain.clearFragment(FragmentDashboardEmotion.class.getName(), null);
        }
    };

    private void addDevice(WatchContact.Kid device) {
        View view = WatchContact.inflateButton(mActivityMain, device,
                getResources().getString(R.string.watch_request_add));
        View button = view.findViewById(R.id.watch_contact_button_button);
        button.setOnClickListener(mDeviceClickListener);

        mViewContainer.addView(view);
    }

    private View.OnClickListener mDeviceClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            WatchContact.Kid kid = (WatchContact.Kid) view.getTag();

            mActivityMain.mServiceMachine.subHostAdd(mSubHostAddListener, kid.mUserId);

            // Todo : next page?
        }
    };

    ServerMachine.subHostAddListener mSubHostAddListener = new ServerMachine.subHostAddListener() {
        @Override
        public void onSuccess(int statusCode, ServerGson.hostData response) {
            mActivityMain.clearFragment(FragmentDashboardEmotion.class.getName(), null);
        }

        @Override
        public void onConflict(int statusCode) {
            mActivityMain.clearFragment(FragmentDashboardEmotion.class.getName(), null);
        }

        @Override
        public void onFail(String command, int statusCode) {
            mActivityMain.clearFragment(FragmentDashboardEmotion.class.getName(), null);
            Toast.makeText(mActivityMain,
                    mActivityMain.mServiceMachine.getErrorMessage(command, statusCode) + "(" + statusCode + ").", Toast.LENGTH_SHORT).show();

        }
    };
}
