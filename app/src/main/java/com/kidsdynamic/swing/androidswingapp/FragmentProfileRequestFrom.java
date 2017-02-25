package com.kidsdynamic.swing.androidswingapp;

import android.app.Dialog;
import android.app.ProgressDialog;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

/**
 * Created by 03543 on 2017/2/9.
 */

public class FragmentProfileRequestFrom extends ViewFragment {

    private ActivityMain mActivityMain;
    private View mViewMain;

    private TextView mViewCount;
    private LinearLayout mViewContainer;
    private Dialog mProcessDialog = null;


    WatchContact.User mRequestFrom;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mActivityMain = (ActivityMain) getActivity();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        mViewMain = inflater.inflate(R.layout.fragment_profile_request_from, container, false);

        mViewCount = (TextView) mViewMain.findViewById(R.id.profile_request_from_count);
        mViewContainer = (LinearLayout) mViewMain.findViewById(R.id.profile_request_from_container);

        if (!mActivityMain.mContactStack.isEmpty()) {
            mRequestFrom = (WatchContact.User) mActivityMain.mContactStack.pop();
            ArrayList<WatchContact.Kid> list = mActivityMain.mOperator.getDeviceList();
            for (WatchContact.Kid kid : list) {
                boolean isCheck = false;
                if (mRequestFrom.mRequestStatus.equals("ACCEPTED")) {
                    for (WatchContact.Kid accepted : mRequestFrom.mRequestKids) {
                        if (accepted.mId == kid.mId) {
                            isCheck = true;
                            break;
                        }
                    }
                }
                addKid(kid, isCheck);
            }
        }

        updateCount();

        return mViewMain;
    }

    @Override
    public ViewFragmentConfig getConfig() {
        return new ViewFragmentConfig(
                getResources().getString(R.string.title_request_from), true, true, false,
                ActivityMain.RESOURCE_IGNORE, R.mipmap.icon_left, ActivityMain.RESOURCE_HIDE);
    }

    @Override
    public void onToolbarAction1() {
        mProcessDialog = ProgressDialog.show(mActivityMain,
                getResources().getString(R.string.profile_request_from_processing),
                getResources().getString(R.string.profile_request_from_wait), true);
        int count = mViewContainer.getChildCount();
        List<Integer> list = new ArrayList<>();

        for (int idx = 0; idx < count; idx++) {
            View view = mViewContainer.getChildAt(idx);
            View check = view.findViewById(R.id.watch_contact_check_icon);

            WatchContact.Kid kid = (WatchContact.Kid) view.getTag();
            if (check.isSelected())
                list.add(kid.mId);
        }

        mActivityMain.mOperator.replyToSubHost(mResponseForRequestToListener, mRequestFrom.mSubHostId, list);
    }

    WatchOperatorReplyToSubHost.finishListener mResponseForRequestToListener = new WatchOperatorReplyToSubHost.finishListener() {
        @Override
        public void onFinish(String msg) {
            mProcessDialog.dismiss();
            mActivityMain.popFragment();
        }
    };

    @Override
    public void onPause() {
        super.onPause();
    }

    private View.OnClickListener mDeviceListener = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            WatchContact.Kid device = (WatchContact.Kid) view.getTag();

            View label = mViewContainer.findViewWithTag(device);
            View check = label.findViewById(R.id.watch_contact_check_icon);
            check.setSelected(!check.isSelected());
        }
    };

    private void addKid(WatchContact.Kid kid, boolean isCheck) {
        View view = WatchContact.inflateCheck(mActivityMain, kid, isCheck);
        view.setOnClickListener(mDeviceListener);

        mViewContainer.addView(view);
    }

    private void updateCount() {
        int count = mViewContainer.getChildCount();
        String string = String.format(Locale.getDefault(),
                getResources().getString(R.string.profile_request_from_count), count);

        mViewCount.setText(string);
    }
}
