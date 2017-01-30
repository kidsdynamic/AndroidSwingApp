package com.kidsdynamic.swing.androidswingapp;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.RectF;
import android.os.Handler;
import android.support.v4.content.ContextCompat;
import android.util.AttributeSet;
import android.util.Log;
import android.util.TypedValue;
import android.view.View;

/**
 * Created by 03543 on 2017/1/3.
 */

public class ViewProgressCircle extends View {
    private final float START_DEGREE = 270;

    public final int STROKE_TYPE_DOT = 0;
    public final int STROKE_TYPE_ARC = 1;

    private int mDesiredSize;

    private float mStrokeWidth = 4;
    private int mStrokeType = STROKE_TYPE_DOT;

    private int mColorActive;
    private int mColorNormal;

    private int mDuration = 15000;  // ms
    private int mTick;

    private int mTotal = 12;
    private int mProgress = 0;
    private boolean mRepeat = false;
    private boolean mPause;

    private Handler mHandler;

    private float mDegree[] = null;
    private float mRadius;
    private int mCenterX;
    private int mCenterY;

    private OnProgressListener mOnProgressListener = null;

    public ViewProgressCircle(Context context) {
        super(context);
        init(context, null);
    }

    public ViewProgressCircle(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context, attrs);
    }

    public ViewProgressCircle(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        init(context, attrs);
    }

    private void init(Context context, AttributeSet attrs) {
        mColorActive = ContextCompat.getColor(context, R.color.color_orange);
        mColorNormal = ContextCompat.getColor(context, R.color.color_white_snow);

        if (attrs != null) {
            TypedArray typedArray = context.obtainStyledAttributes(
                    attrs, R.styleable.ViewProgressCircle);

            final int count = typedArray.getIndexCount();
            for (int idx = 0; idx < count; idx++) {
                final int attr = typedArray.getIndex(idx);

                if (attr == R.styleable.ViewProgressCircle_strokeWidth) {
                    mStrokeWidth = typedArray.getDimension(attr, mStrokeWidth);
                } else if (attr == R.styleable.ViewProgressCircle_strokeActiveColor) {
                    mColorActive = typedArray.getColor(attr, mColorActive);
                } else if (attr == R.styleable.ViewProgressCircle_strokeNormalColor) {
                    mColorNormal = typedArray.getColor(attr, mColorNormal);
                } else if (attr == R.styleable.ViewProgressCircle_strokeType) {
                    mStrokeType = typedArray.getInteger(attr, mStrokeType);
                } else if (attr == R.styleable.ViewProgressCircle_duration) {
                    mDuration = typedArray.getInteger(attr, mDuration);
                } else if (attr == R.styleable.ViewProgressCircle_total) {
                    mTotal = typedArray.getInteger(attr, mTotal);
                } else if (attr == R.styleable.ViewProgressCircle_repeat) {
                    mRepeat = typedArray.getBoolean(attr, mRepeat);
                } else if (attr == R.styleable.ViewProgressCircle_android_progress) {
                    mProgress = typedArray.getInt(attr, 0);
                }
            }

            typedArray.recycle();
        }

        mDesiredSize = (int) (mStrokeWidth * 14.0);
        mHandler = new Handler();
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
            width = Math.min(mDesiredSize, widthSize);
        } else {
            width = mDesiredSize;
        }

        if (heightMode == MeasureSpec.EXACTLY) {
            height = heightSize;
        } else if (heightMode == MeasureSpec.AT_MOST) {
            height = Math.min(mDesiredSize, heightSize);
        } else {
            height = mDesiredSize;
        }

        setMeasuredDimension(width, height);

        updateVector();
    }

    @Override
    protected void onDraw(Canvas canvas) {
        if (mStrokeType == STROKE_TYPE_DOT) {
            drawDot(canvas, mProgress, mRepeat);
        } else if (mStrokeType == STROKE_TYPE_ARC) {
            drawArc(canvas, mProgress, mRepeat);
        }
    }

    private void drawDot(Canvas canvas, int progress, boolean repeat) {
        final Paint paint = new Paint();
        paint.setAntiAlias(true);

        for (int idx = 0; idx < mTotal; idx++) {
            boolean reach = repeat ? idx == mProgress : idx <= progress;
            float degree = (START_DEGREE + mDegree[idx]) % 360;

            int dotCenterX = (int) (mCenterX + (mRadius * Math.cos(degree * 3.1415 / 180)));
            int dotCenterY = (int) (mCenterY + (mRadius * Math.sin(degree * 3.1415 / 180)));

            paint.setColor(reach ? mColorActive : mColorNormal);
            canvas.drawCircle(dotCenterX, dotCenterY, mStrokeWidth / 2, paint);
        }
    }

    private void drawArc(Canvas canvas, int progress, boolean repeat) {
        final Paint paint = new Paint();
        paint.setAntiAlias(true);
        paint.setStrokeWidth(mStrokeWidth);
        paint.setStyle(Paint.Style.STROKE);

        RectF rect = new RectF(
                mCenterX - mRadius, mCenterY - mRadius,
                mCenterX + mRadius, mCenterY + mRadius);

        float degree, sweep;

        if (progress < 0) {
            degree = START_DEGREE;
            sweep = 0;
        } else if (repeat) {
            degree = (START_DEGREE + mDegree[progress]) % 360;
            sweep = 360 / mTotal;
        } else {
            degree = START_DEGREE;
            sweep = progress >= mTotal ? 360 : mDegree[progress];
        }

        paint.setColor(mColorActive);
        canvas.drawArc(rect, degree, sweep, false, paint);

        paint.setColor(mColorNormal);
        canvas.drawArc(rect, (degree + sweep) % 360, 360 - sweep, false, paint);
    }

    private void updateVector() {
        mDegree = new float[mTotal];

        float deg = 360;
        for (int idx = 0; idx < mTotal; idx++) {
            mDegree[idx] = deg * idx / mTotal;
        }

        int width = getMeasuredWidth();
        int height = getMeasuredHeight();

        mRadius = (Math.min(width, height) - mStrokeWidth) / 2;
        mCenterX = width / 2;
        mCenterY = height / 2;

        mTick = mDuration / mTotal;
    }

    private ViewProgressCircle mThis = this;
    private Runnable mRunnable = new Runnable() {
        @Override
        public void run() {
            if (mPause)
                return;

            mProgress++;
            postInvalidate();

            if (mOnProgressListener != null) {
                mOnProgressListener.onProgress(mThis, mProgress, mTotal);
            }

            if (mRepeat && mProgress >= mTotal)
                mProgress = 0;

            if (mProgress < mTotal)
                mHandler.postDelayed(mRunnable, mTick);
        }
    };

    public void setOnProgressListener(OnProgressListener listener) {
        mOnProgressListener = listener;
    }

    public void start() {
        mPause = false;
        if (mOnProgressListener != null)
            mOnProgressListener.onProgress(mThis, mProgress, mTotal);

        mHandler.postDelayed(mRunnable, mTick);
    }

    public void pause() {
        mPause = true;
        mHandler.removeCallbacks(mRunnable);
    }

    public void setTotal(int total) {
        pause();
        mTotal = total;
        updateVector();
        postInvalidate();
    }

    public int getTotal() {
        return mTotal;
    }

    public void setProgress(int progress) {
        pause();
        mProgress = progress;
        updateVector();
        postInvalidate();
    }

    public int getProgress() {
        return mProgress;
    }

    public void setDuration(int duration) {
        pause();
        mDuration = duration;
        updateVector();
        postInvalidate();
    }

    public int getDuration() {
        return mDuration;
    }

    public void setRepeat(boolean repeat) {
        pause();
        mRepeat = repeat;
        updateVector();
        postInvalidate();
    }

    public interface OnProgressListener {
        void onProgress(ViewProgressCircle view, int progress, int total);
    }

}
