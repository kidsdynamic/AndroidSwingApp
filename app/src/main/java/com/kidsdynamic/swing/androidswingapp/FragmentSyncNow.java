package com.kidsdynamic.swing.androidswingapp;

import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Toast;

/**
 * Created by 03543 on 2017/1/1.
 */

public class FragmentSyncNow extends ViewFragment {
    private ActivityMain mActivityMain;
    private View mViewMain;

    private Button mButtonYes;
    private Button mButtonNo;
    private Button mbuttonAnother;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mActivityMain = (ActivityMain) getActivity();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        mViewMain = inflater.inflate(R.layout.fragment_sync_now, container, false);

        mButtonYes = (Button) mViewMain.findViewById(R.id.sync_now_yes);
        mButtonYes.setOnClickListener(mOnYesListener);

        mButtonNo = (Button) mViewMain.findViewById(R.id.sync_now_no);
        mButtonNo.setOnClickListener(mOnNoListener);

        mbuttonAnother = (Button) mViewMain.findViewById(R.id.sync_now_another);
        mbuttonAnother.setOnClickListener(mOnAnotherListener);

        return mViewMain;
    }

    @Override
    public ViewFragmentConfig getConfig() {
        return new ViewFragmentConfig("Sync", true, true, false,
                R.mipmap.city_overall, ActivityMain.RESOURCE_HIDE, ActivityMain.RESOURCE_HIDE);
    }

    private Button.OnClickListener mOnYesListener = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            WatchContact.Kid device = mActivityMain.mOperator.getFocusKid();

            if (device == null) {
                Toast.makeText(mActivityMain, "There is no bound device", Toast.LENGTH_SHORT).show();
            } else {
                if (device.mProfile != null && !device.mProfile.equals("")) {
                    device.mPhoto = BitmapFactory.decodeFile(device.mProfile);
                } else {
                    device.mPhoto = BitmapFactory.decodeResource(getResources(), R.mipmap.monster_yellow);
                }
                device.mLabel = device.mName;
                device.mBound = true;

                mActivityMain.mContactStack.push(device);
                mActivityMain.selectFragment(FragmentSyncSearch.class.getName(), null);
            }
        }
    };

    private Button.OnClickListener mOnNoListener = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            mActivityMain.consoleSelect(null);
            mActivityMain.consoleShow(true);
            mActivityMain.toolbarShow(true);
            mActivityMain.selectFragment(FragmentDashboardSync.class.getName(), null);
        }
    };

    private Button.OnClickListener mOnAnotherListener = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            mActivityMain.selectFragment(FragmentSyncSelect.class.getName(), null);
        }
    };
}
