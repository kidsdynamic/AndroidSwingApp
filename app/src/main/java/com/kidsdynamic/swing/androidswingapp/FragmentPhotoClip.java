package com.kidsdynamic.swing.androidswingapp;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

/**
 * Created by 03543 on 2017/1/7.
 */

public class FragmentPhotoClip extends ViewFragment {
    private ActivityMain mActivityMain;
    private View mViewMain;

    private TextView mViewCancel;
    private ViewPhotoClip mViewEditor;
    private Button mViewAction;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mActivityMain = (ActivityMain) getActivity();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        mViewMain = inflater.inflate(R.layout.fragment_photo_clip, container, false);

        mViewCancel = (TextView)mViewMain.findViewById(R.id.photo_clip_cancel);
        mViewCancel.setOnClickListener(mCancelClickListener);

        mViewEditor = (ViewPhotoClip)mViewMain.findViewById(R.id.photo_clip_editor);

        mViewAction = (Button)mViewMain.findViewById(R.id.photo_clip_action);
        mViewAction.setOnClickListener(mActionClickListener);

        return mViewMain;
    }

    @Override
    public ViewFragmentConfig getConfig() {
        return new ViewFragmentConfig("Photo", false, false,
                ActivityMain.RESOURCE_IGNORE, ActivityMain.RESOURCE_IGNORE, ActivityMain.RESOURCE_IGNORE);
    }

    @Override
    public void onToolbarAction1() {
        mActivityMain.popFragment();
    }

    private View.OnClickListener mCancelClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            onToolbarAction1();
        }
    };

    private View.OnClickListener mActionClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
        }
    };

}
