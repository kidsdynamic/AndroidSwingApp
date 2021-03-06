package com.kidsdynamic.swing.androidswingapp;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

/**
 * Created by 03543 on 2017/1/15.
 */

public class FragmentWatchFinish extends ViewFragment {
    private ActivityMain mActivityMain;
    private View mViewMain;

    private ViewCircle mViewPhoto;
    private Button mViewDashboard;
    private Button mViewAnother;
    private ImageView mViewBack;
    private String mAvatarFilename = "";
    private String deviceName;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mActivityMain = (ActivityMain) getActivity();

        Bundle arg = getArguments();
        if (arg != null) {
            mAvatarFilename = getArguments().getString(ViewFragment.BUNDLE_KEY_AVATAR, "");
            deviceName = getArguments().getString(ViewFragment.BUNDLE_KEY_KID_NAME, "");
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        mViewMain = inflater.inflate(R.layout.fragment_watch_finish, container, false);

        InputMethodManager imm = (InputMethodManager) mActivityMain.getSystemService(Context.INPUT_METHOD_SERVICE);
        if(imm != null) {
            imm.hideSoftInputFromWindow(mViewMain.getWindowToken(), 0);
        }

//        mViewBack = (ImageView) mViewMain.findViewById(R.id.fragment_back);
//        mViewBack.setOnClickListener(mBackOnClickListener);

        mViewDashboard = (Button)mViewMain.findViewById(R.id.watch_finish_dashboard);
        mViewDashboard.setOnClickListener(mDashboardOnClickListener);

        mViewAnother = (Button)mViewMain.findViewById(R.id.watch_finish_another);
        mViewAnother.setOnClickListener(mAnotherOnClickListener);

        mViewPhoto = (ViewCircle) mViewMain.findViewById(R.id.watch_finish_photo);

        TextView name = (TextView) mViewMain.findViewById(R.id.watch_finish_name);
        name.setText(deviceName);

        return mViewMain;
    }

    @Override
    public void onResume() {
        super.onResume();

        if (!mAvatarFilename.equals("")) {
            Bitmap bitmap = BitmapFactory.decodeFile(mAvatarFilename);
            if (bitmap != null)
                mViewPhoto.setBitmap(bitmap);
        }
    }

    @Override
    public ViewFragmentConfig getConfig() {
        return new ViewFragmentConfig("", false, false, false,
                ActivityMain.RESOURCE_IGNORE, ActivityMain.RESOURCE_IGNORE, ActivityMain.RESOURCE_IGNORE);
    }

    @Override
    public void onToolbarAction1() {
        mActivityMain.popFragment();
    }

    private View.OnClickListener mBackOnClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            onToolbarAction1();
        }
    };

    private View.OnClickListener mDashboardOnClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            mActivityMain.clearFragment(FragmentDashboardEmotion.class.getName(), null);
        }
    };

    private View.OnClickListener mAnotherOnClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            mActivityMain.selectFragment(FragmentWatchOwner.class.getName(), null);
        }
    };
}
