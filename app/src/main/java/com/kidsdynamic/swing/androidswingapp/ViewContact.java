package com.kidsdynamic.swing.androidswingapp;

import android.content.Context;
import android.util.AttributeSet;
import android.util.DisplayMetrics;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

/**
 * Created by 03543 on 2016/12/30.
 */

public class ViewContact extends RelativeLayout {
    private int mDesiredWidth;
    private int mDesiredHeight;

    private View mViewPhoto;
    private TextView mViewLabel;
    private Button mViewButton1;
    private Button mViewButton2;

    public ViewContact(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public ViewContact(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        init();
    }

    private void init() {
        DisplayMetrics metrics = getResources().getDisplayMetrics();
        mDesiredHeight = metrics.heightPixels / getResources().getInteger(R.integer.contact_label_denominator);
        mDesiredWidth = mDesiredHeight * 4;

        inflate(getContext(), R.layout.view_contact, this);

        mViewPhoto = (ViewPhoto) findViewById(R.id.view_contact_photo);
        mViewLabel = (TextView) findViewById(R.id.view_contact_label);
        mViewButton1 = (Button) findViewById(R.id.view_contact_button1);
        mViewButton2 = (Button) findViewById(R.id.view_contact_button2);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {

        int widthMode = MeasureSpec.getMode(widthMeasureSpec);
        int widthSize = MeasureSpec.getSize(widthMeasureSpec);

        int heightMode = MeasureSpec.getMode(heightMeasureSpec);
        int heightSize = MeasureSpec.getSize(heightMeasureSpec);

        int width, height;

        if (widthMode == MeasureSpec.EXACTLY) {
            width = widthSize;
        } else if (widthMode == MeasureSpec.AT_MOST) {
            width = Math.min(mDesiredWidth, widthSize);
        } else {
            width = mDesiredWidth;
        }

        if (heightMode == MeasureSpec.EXACTLY) {
            height = heightSize;
        } else if (heightMode == MeasureSpec.AT_MOST) {
            height = Math.min(mDesiredHeight, heightSize);
        } else {
            height = mDesiredHeight;
        }

        widthMeasureSpec = MeasureSpec.makeMeasureSpec(width, widthMode);
        heightMeasureSpec = MeasureSpec.makeMeasureSpec(heightSize, heightMode);

        getLayoutParams().width = width;
        getLayoutParams().height = height;

        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
    }
}
