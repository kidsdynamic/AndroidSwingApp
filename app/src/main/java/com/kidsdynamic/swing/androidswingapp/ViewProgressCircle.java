package com.kidsdynamic.swing.androidswingapp;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.os.Handler;
import android.util.AttributeSet;
import android.util.Log;
import android.util.TypedValue;
import android.view.View;

/**
 * Created by 03543 on 2017/1/3.
 */

public class ViewProgressCircle extends View {
    private int mDesiredWidth;
    private int mDesiredHeight;

    private float mAttDotRadius = 2;

    private int mDotRadius;

    private Rect mDotRect[];
    private Bitmap mBackgroundBitmap;
    private int mBackgroundWidth = 0, mBackgroundHeight = 0;

    private Handler mAnimateHandler;
    private int mAnimateDot;

    public ViewProgressCircle(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context, attrs);
    }

    public ViewProgressCircle(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        init(context, attrs);
    }

    private void init(Context context, AttributeSet attrs) {
        if (attrs != null) {
            TypedArray typedArray = context.obtainStyledAttributes(
                    attrs, R.styleable.ViewProgressCircle);

            final int count = typedArray.getIndexCount();
            for (int idx = 0; idx < count; idx++) {
                final int attr = typedArray.getIndex(idx);

                if (attr == R.styleable.ViewProgressCircle_dotRadius)
                    mAttDotRadius = typedArray.getDimension(R.styleable.ViewProgressCircle_dotRadius, mAttDotRadius);
            }

            mDotRadius = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, mAttDotRadius, getResources().getDisplayMetrics());

            typedArray.recycle();
        }

        mDesiredWidth = (int) (mDotRadius * 14.0);
        mDesiredHeight = (int) (mDotRadius * 14.0);

        mDotRect = new Rect[12];

        mAnimateHandler = new Handler();
        mAnimateDot = 0;
        mAnimateHandler.post(mAnimateRunnable);
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

        for (int idx = 0; idx < mDotRect.length; idx++)
            paintDot(canvas, mDotRect[idx], idx == mAnimateDot);
    }

    private void paintDot(Canvas canvas, Rect rect, boolean focus) {
        final Paint paint = new Paint();
        paint.setAntiAlias(true);
        if (focus)
            paint.setColor(Color.WHITE);
        else
            paint.setColor(Color.LTGRAY);

        canvas.drawCircle(rect.centerX(), rect.centerY(), mDotRadius, paint);
    }

    private void updateRects() {
        int centerX = mBackgroundWidth / 2;
        int centerY = mBackgroundHeight / 2;
        int radius = Math.min(mBackgroundWidth, mBackgroundHeight) / 2 - mDotRadius;

        for (int idx = 0; idx < mDotRect.length; idx++) {
            int x = (int) (centerX + (radius * Math.cos(idx * 30 * 3.1415 / 180)));
            int y = (int) (centerY + (radius * Math.sin(idx * 30 * 3.1415 / 180)));

            mDotRect[idx] = new Rect(x - mDotRadius, y - mDotRadius, x + mDotRadius, y + mDotRadius);
        }
    }

    private void makeBackground(int width, int height) {
        mBackgroundWidth = width;
        mBackgroundHeight = height;

        updateRects();

        mBackgroundBitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);
        final Canvas canvas = new Canvas(mBackgroundBitmap);

        final Paint paint = new Paint();
        paint.setAntiAlias(true);
        canvas.drawARGB(0, 0, 0, 0);
    }

    private Runnable mAnimateRunnable = new Runnable() {
        @Override
        public void run() {
            mAnimateDot++;
            if (mAnimateDot >= mDotRect.length)
                mAnimateDot = 0;

            postInvalidate();
            mAnimateHandler.postDelayed(mAnimateRunnable, 300);
        }
    };
}
