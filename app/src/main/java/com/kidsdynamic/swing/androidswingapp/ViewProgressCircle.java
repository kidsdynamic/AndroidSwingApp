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
    public final int STROKE_TYPE_DOT = 0;
    public final int STROKE_TYPE_ARC = 1;

    private int mDesiredSize;

    private float mStrokeWidthDp = 4;
    private int mStrokeWidth;
    private int mStrokeType = STROKE_TYPE_DOT;

    private int mColorActive;
    private int mColorNormal;

    private int mDuration = 15000;  // ms
    private int mTick;

    private int mTotal = 12;
    private int mProgress = 0;
    private boolean mRepeat = false;

    private Handler mAnimateHandler;

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
        mColorNormal = ContextCompat.getColor(context, R.color.color_white);

        if (attrs != null) {
            TypedArray typedArray = context.obtainStyledAttributes(
                    attrs, R.styleable.ViewProgressCircle);

            final int count = typedArray.getIndexCount();
            for (int idx = 0; idx < count; idx++) {
                final int attr = typedArray.getIndex(idx);

                if (attr == R.styleable.ViewProgressCircle_strokeWidth) {
                    mStrokeWidthDp = typedArray.getDimension(attr, mStrokeWidthDp);
                    mStrokeWidth = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, mStrokeWidthDp, getResources().getDisplayMetrics());
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
                }
            }

            typedArray.recycle();
        }

        mDesiredSize = (int) (mStrokeWidth * 14.0);
        mAnimateHandler = new Handler();
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
        int progress = mProgress % mTotal;

        if (mStrokeType == STROKE_TYPE_DOT) {
            drawDot(canvas, progress, mRepeat);

        } else if (mStrokeType == STROKE_TYPE_ARC) {
            drawArc(canvas, progress, mRepeat);
        }
    }

    private void drawDot(Canvas canvas, int progress, boolean repeat) {
        final Paint paint = new Paint();
        paint.setAntiAlias(true);

        for (int idx = 0; idx < mTotal; idx++) {
            boolean reach = repeat ? idx == mProgress : idx <= progress;

            int dotCenterX = (int) (mCenterX + (mRadius * Math.cos(mDegree[idx] * 3.1415 / 180)));
            int dotCenterY = (int) (mCenterY + (mRadius * Math.sin(mDegree[idx] * 3.1415 / 180)));

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

        float degreeActive, degreeNormal, sweep;

        if (repeat) {
            degreeNormal = mDegree[progress];
            sweep = (360 / mTotal);
            degreeActive = mDegree[progress] - sweep;
        } else {
            degreeNormal = mDegree[progress];
            degreeActive = 270;
            sweep = degreeActive <= degreeNormal ? (degreeNormal + 90) : (degreeNormal - degreeActive);
        }

        paint.setColor(mColorActive);
        canvas.drawArc(rect, (float) Math.floor(degreeActive), (float) Math.ceil(sweep), false, paint);

        paint.setColor(mColorNormal);
        canvas.drawArc(rect, (float) Math.floor(degreeNormal), (float) Math.ceil(360 - sweep), false, paint);
    }

    private void updateVector() {
        mDegree = new float[mTotal];

        float degree = 270;
        float step = 360 / mTotal;
        for (int idx = 0; idx < mTotal; idx++) {
            mDegree[idx] = degree;

            degree += step;
            if (degree < 0)
                degree += 360;
        }

        int width = getMeasuredWidth();
        int height = getMeasuredHeight();

        mRadius = (Math.min(width, height) - mStrokeWidth) / 2;
        mCenterX = width / 2;
        mCenterY = height / 2;

        mTick = mDuration / mTotal;
    }

    private ViewProgressCircle mThis = this;
    private Runnable mAnimateRunnable = new Runnable() {
        @Override
        public void run() {
            if (mOnProgressListener != null) {
                mOnProgressListener.onProgress(mThis, mProgress);

                if (!mRepeat) {
                    if (!mRepeat && mProgress == 0)
                        mOnProgressListener.onStart(mThis);
                    if (mProgress >= (mTotal - 1))
                        mOnProgressListener.onFinish(mThis);
                }
            }

            mProgress++;

            if (mRepeat && mProgress >= mTotal)
                mProgress = 0;

            if (mProgress <= mTotal)
                postInvalidate();

            if (mProgress < mTotal)
                mAnimateHandler.postDelayed(mAnimateRunnable, mTick);
        }
    };

    public void setOnProgressListener(OnProgressListener listener) {
        mOnProgressListener = listener;
    }

    public void reset() {

    }

    public void start() {
        mAnimateHandler.post(mAnimateRunnable);
    }

    public void pause() {

    }

    public void setTotal(int total) {
        mTotal = total;
        updateVector();
    }

    public int getTotal() {
        return mTotal;
    }

    public void setProgress(int progress) {
        mProgress = progress;
    }

    public int getProgress() {
        return mProgress;
    }

    public void setDuration(int duration) {

    }

    public int getDuration() {
        return mDuration;
    }

    public interface OnProgressListener {
        void onStart(ViewProgressCircle view);

        void onProgress(ViewProgressCircle view, int progress);

        void onFinish(ViewProgressCircle view);
    }

}
