package com.kidsdynamic.swing.androidswingapp;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.view.View;

/**
 * Created by 03543 on 2017/1/2.
 */

public class ViewPhoto extends View {
    private int mDesiredWidth;
    private int mDesiredHeight;

    private Bitmap mBackgroundBitmap;
    private int mBackgroundWidth = 0, mBackgroundHeight = 0;

    public ViewPhoto(Context context) {
        super(context);
        init();
    }

    public ViewPhoto(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public ViewPhoto(Context context, AttributeSet attrs, int defStyle) {
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

        setMeasuredDimension(width, height);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        if (getMeasuredWidth() != mBackgroundWidth || getMeasuredHeight() != mBackgroundHeight)
            makeBackground(getMeasuredWidth(), getMeasuredHeight());

        canvas.drawBitmap(mBackgroundBitmap, 0, 0, null);
    }

    private void updateRects() {
    }

    private void makeBackground(int width, int height) {
        mBackgroundWidth = width;
        mBackgroundHeight = height;

        updateRects();

        mBackgroundBitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);
        final Canvas canvas = new Canvas(mBackgroundBitmap);

        final Paint paint = new Paint();
        paint.setAntiAlias(true);
        paint.setColor(Color.BLUE);

        canvas.drawRect(0, 0, mBackgroundWidth, mBackgroundHeight, paint);
    }
}
