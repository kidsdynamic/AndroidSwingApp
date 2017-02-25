package com.kidsdynamic.swing.androidswingapp;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.os.Bundle;
import android.util.Log;
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
    private ImageView mViewRotateLeft;
    private ImageView mViewRotateRight;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mActivityMain = (ActivityMain) getActivity();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        mViewMain = inflater.inflate(R.layout.fragment_photo_clip, container, false);

        mViewCancel = (TextView) mViewMain.findViewById(R.id.photo_clip_cancel);
        mViewCancel.setOnClickListener(mCancelClickListener);

        mViewEditor = (ViewPhotoClip) mViewMain.findViewById(R.id.photo_clip_editor);

        mViewAction = (Button) mViewMain.findViewById(R.id.photo_clip_action);
        mViewAction.setOnClickListener(mActionClickListener);

        mViewRotateLeft = (ImageView)mViewMain.findViewById(R.id.photo_clip_left);
        mViewRotateLeft.setOnClickListener(mRotateClickListener);

        mViewRotateRight = (ImageView)mViewMain.findViewById(R.id.photo_clip_right);
        mViewRotateRight.setOnClickListener(mRotateClickListener);

        return mViewMain;
    }

    @Override
    public ViewFragmentConfig getConfig() {
        return new ViewFragmentConfig(
                getResources().getString(R.string.title_photo), false, false, false,
                ActivityMain.RESOURCE_IGNORE, ActivityMain.RESOURCE_IGNORE, ActivityMain.RESOURCE_IGNORE);
    }

    @Override
    public void onToolbarAction1() {
        mActivityMain.popFragment();
    }

    @Override
    public void onResume() {
        super.onResume();

        if(!mActivityMain.mBitmapStack.isEmpty()) {
            Bitmap photo = mActivityMain.mBitmapStack.pop();
            mViewEditor.setPhoto(photo);
        }
    }

    private View.OnClickListener mCancelClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            onToolbarAction1();
        }
    };

    private View.OnClickListener mActionClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            Bitmap bitmap = Bitmap.createBitmap(320, 320, Bitmap.Config.ARGB_8888);
            Canvas canvas = new Canvas(bitmap);

            mViewEditor.drawClip(canvas);
            mActivityMain.mBitmapStack.push(bitmap);
            mActivityMain.popFragment();
        }
    };

    private View.OnClickListener mRotateClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            if(view == mViewRotateLeft)
                mViewEditor.rotatePhoto(-90);
            else
                mViewEditor.rotatePhoto(90);
        }
    };
}
