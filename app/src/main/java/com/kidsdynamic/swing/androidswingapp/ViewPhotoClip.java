package com.kidsdynamic.swing.androidswingapp;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Rect;
import android.util.AttributeSet;
import android.view.View;

/**
 * Created by 03543 on 2017/1/7.
 */

public class ViewPhotoClip extends View {
    private int mDesiredWidth;
    private int mDesiredHeight;
    private int mMeasureWidth;
    private int mMeasureHeight;

    private Bitmap mBackground;

    public ViewPhotoClip(Context context) {
        super(context);
        init();
    }

    public ViewPhotoClip(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public ViewPhotoClip(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        init();
    }

    private void init() {
        mDesiredWidth = 100;
        mDesiredHeight = 100;
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

    @Override
    protected void onDraw(Canvas canvas) {
        if (mMeasureWidth != getMeasuredWidth() || mMeasureHeight != getMeasuredHeight()) {
            makeBackground(getMeasuredWidth(), getMeasuredHeight());
            updateRects();
        }

        paintBackground(canvas);
    }

    private void paintBackground(Canvas canvas) {
        canvas.drawBitmap(mBackground,
                new Rect(0, 0, mBackground.getWidth(), mBackground.getHeight()),
                new Rect(0, 0, mMeasureWidth, mMeasureHeight), null);
    }

    private void updateRects() {

    }

    private void makeBackground(int width, int height) {
        mMeasureWidth = width;
        mMeasureHeight = height;

        if (mBackground != null && mBackground.isRecycled())
            mBackground.recycle();

        mBackground = Bitmap.createBitmap(mMeasureWidth, mMeasureHeight, Bitmap.Config.ARGB_8888);

        final Canvas canvas = new Canvas(mBackground);
        canvas.drawARGB(0, 0, 0, 0);

        canvas.drawColor(Color.YELLOW);
    }
}

