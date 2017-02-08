package com.kidsdynamic.swing.androidswingapp;

import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;

/**
 * Created by 03543 on 2016/12/30.
 */

public class FragmentWatchRequest extends ViewFragment {
    private ActivityMain mActivityMain;
    private View mViewMain;
    private LinearLayout mViewContainer;
    private Button mButtonBack;
    private Button mButtonDashboard;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mActivityMain = (ActivityMain) getActivity();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        mViewMain = inflater.inflate(R.layout.fragment_watch_request, container, false);

        mViewContainer = (LinearLayout)mViewMain.findViewById(R.id.watch_request_container);

        mButtonBack = (Button) mViewMain.findViewById(R.id.watch_request_back);
        mButtonBack.setOnClickListener(mOnBackListener);

        mButtonDashboard = (Button) mViewMain.findViewById(R.id.watch_request_dashboard);
        mButtonDashboard.setOnClickListener(mOnDashboardListener);

        // Test
        addDevice(new WatchContact.Kid(BitmapFactory.decodeResource(getResources(), R.mipmap.monster_purple), "Monster Purple", false));
        //////////////

        return mViewMain;
    }

    @Override
    public ViewFragmentConfig getConfig() {
        return new ViewFragmentConfig("Watch", false, false,  false,
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
            mActivityMain.selectFragment(FragmentDashboard.class.getName(), null);
        }
    };

    private void addDevice(WatchContact.Kid device) {
        View view = WatchContact.inflateButton(mActivityMain, device, "Add");
        View button = view.findViewById(R.id.watch_contact_button_button);
        button.setOnClickListener(mDeviceClickListener);

        mViewContainer.addView(view);
    }

    private View.OnClickListener mDeviceClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            WatchContact.Kid device = (WatchContact.Kid) view.getTag();
        }
    };
}
