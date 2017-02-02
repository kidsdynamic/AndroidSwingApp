package com.kidsdynamic.swing.androidswingapp;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.Rect;
import android.graphics.RectF;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;

/**
 * Created by 03543 on 2017/2/2.
 */

public class ViewCircle extends View {
    private final float START_DEGREE = 270;

    public final int STROKE_TYPE_DOT = 0;
    public final int STROKE_TYPE_ARC = 1;

    private int mStrokeType = STROKE_TYPE_DOT;
    private float mStrokeWidth = 4;
    private int mStrokeColorActive = Color.WHITE;
    private int mStrokeColorNormal = Color.LTGRAY;
    private int mStrokeBegin = 0;
    private int mStrokeEnd = 0;
    private int mStrokeCount = 0;
    private float[] mStrokeDegree;

    private Bitmap mBitmap = null;
    private int mFillColor = Color.TRANSPARENT;
    private boolean mFillDarker = false;
    private float mCrossWidth = 0;
    private int mCrossColor = Color.WHITE;
    private float mCrossRatio = 0.2f;

    private Rect mRectBitmap;
    private Rect mRectBorder;
    private Paint mPaint;

    public ViewCircle(Context context) {
        super(context);
        init(context, null);
    }

    public ViewCircle(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context, attrs);
    }

    public ViewCircle(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        init(context, attrs);
    }

    private void init(Context context, AttributeSet attrs) {
        if (attrs != null) {
            TypedArray typedArray = context.obtainStyledAttributes(attrs, R.styleable.ViewCircle);

            final int count = typedArray.getIndexCount();
            for (int idx = 0; idx < count; idx++) {
                final int attr = typedArray.getIndex(idx);

                if (attr == R.styleable.ViewCircle_strokeWidth) {
                    mStrokeWidth = typedArray.getDimension(attr, mStrokeWidth);
                } else if (attr == R.styleable.ViewCircle_strokeColorActive) {
                    mStrokeColorActive = typedArray.getColor(attr, mStrokeColorActive);
                } else if (attr == R.styleable.ViewCircle_strokeColorNormal) {
                    mStrokeColorNormal = typedArray.getColor(attr, mStrokeColorNormal);
                } else if (attr == R.styleable.ViewCircle_strokeType) {
                    mStrokeType = typedArray.getInteger(attr, mStrokeType);
                } else if (attr == R.styleable.ViewCircle_strokeCount) {
                    mStrokeCount = typedArray.getInteger(attr, mStrokeCount);
                } else if (attr == R.styleable.ViewCircle_strokeBegin) {
                    mStrokeBegin = typedArray.getInt(attr, mStrokeBegin);
                } else if (attr == R.styleable.ViewCircle_strokeEnd) {
                    mStrokeEnd = typedArray.getInt(attr, mStrokeEnd);
                } else if (attr == R.styleable.ViewCircle_android_src) {
                    Drawable drawable = typedArray.getDrawable(attr);
                    setBitmap(((BitmapDrawable) drawable).getBitmap());
                } else if (attr == R.styleable.ViewCircle_crossWidth) {
                    mCrossWidth = typedArray.getDimension(attr, mCrossWidth);
                } else if (attr == R.styleable.ViewCircle_crossColor) {
                    mCrossColor = typedArray.getColor(attr, mCrossColor);
                } else if (attr == R.styleable.ViewCircle_crossRatio) {
                    mCrossRatio = typedArray.getFloat(attr, mCrossRatio);
                } else if (attr == R.styleable.ViewCircle_fillColor) {
                    mFillColor = typedArray.getColor(attr, mFillColor);
                } else if (attr == R.styleable.ViewCircle_fillDarker) {
                    mFillDarker = typedArray.getBoolean(attr, mFillDarker);
                }
            }
            typedArray.recycle();
        }

        mPaint = new Paint();
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        int desireSize = (int) Math.ceil(mStrokeWidth * 2);

        if (mBitmap != null && !mBitmap.isRecycled())
            desireSize += Math.min(mBitmap.getWidth(), mBitmap.getHeight());

        int paddingV = getPaddingTop() + getPaddingBottom();
        int paddingH = getPaddingStart() + getPaddingEnd();
        desireSize += Math.max(paddingV, paddingH);

        int widthMode = MeasureSpec.getMode(widthMeasureSpec);
        int widthSize = MeasureSpec.getSize(widthMeasureSpec);

        int heightMode = MeasureSpec.getMode(heightMeasureSpec);
        int heightSize = MeasureSpec.getSize(heightMeasureSpec);

        int width, height;

        if (widthMode == MeasureSpec.EXACTLY) {
            width = widthSize;
        } else if (widthMode == MeasureSpec.AT_MOST) {
            width = Math.min(desireSize, widthSize);
        } else {
            width = desireSize;
        }

        if (heightMode == MeasureSpec.EXACTLY) {
            height = heightSize;
        } else if (heightMode == MeasureSpec.AT_MOST) {
            height = Math.min(desireSize, heightSize);
        } else {
            height = desireSize;
        }

        setMeasuredDimension(width, height);
        updateVector();
    }

    @Override
    protected void onDraw(Canvas canvas) {
        canvas.drawColor(Color.RED);

        paintCircle(canvas, mRectBorder);

        if (mBitmap != null) {
            paintBitmap(canvas, mRectBitmap);
        }

        if (mCrossWidth > 0) {
            paintCross(canvas, mRectBitmap);
        }

        if (mStrokeWidth > 0) {
            if (mStrokeType == STROKE_TYPE_DOT) {
                paintStrokeDot(canvas, mRectBorder);
            } else if (mStrokeType == STROKE_TYPE_ARC) {
                paintStrokeArc(canvas, mRectBorder);
            }
        }
    }

    private void paintCircle(Canvas canvas, Rect rect) {
        mPaint.reset();
        mPaint.setAntiAlias(true);
        mPaint.setColor(mFillColor);
        mPaint.setStyle(Paint.Style.FILL);

        canvas.drawCircle(rect.centerX(), rect.centerY(), Math.min(rect.width(), rect.height()) / 2, mPaint);
    }

    private void paintBitmap(Canvas canvas, Rect rect) {
        mPaint.reset();

        if (mFillDarker)
            mPaint.setColorFilter(FactoryColorFilter.adjustColor(-64, 0, 0, 0));

        canvas.drawBitmap(mBitmap,
                new Rect(0, 0, mBitmap.getWidth(), mBitmap.getHeight()), rect, mPaint);
    }

    private void paintCross(Canvas canvas, Rect rect) {
        int length = (int) (mCrossRatio * Math.min(rect.width(), rect.height())) / 2;

        mPaint.reset();
        mPaint.setAntiAlias(true);
        mPaint.setColor(mCrossColor);
        mPaint.setStrokeWidth(mCrossWidth);
        mPaint.setStyle(Paint.Style.STROKE);

        canvas.drawLine(rect.centerX(), rect.centerY() - length, rect.centerX(), rect.centerY() + length, mPaint);
        canvas.drawLine(rect.centerX() - length, rect.centerY(), rect.centerX() + length, rect.centerY(), mPaint);
    }

    private void paintStrokeDot(Canvas canvas, Rect rect) {
        mPaint.reset();
        mPaint.setAntiAlias(true);

        int centerX = rect.centerX();
        int centerY = rect.centerY();
        int radius = Math.min(rect.width(), rect.height()) / 2;

        int dotRadius = (int) Math.floor(mStrokeWidth / 2);

        for (int idx = 0; idx < mStrokeCount; idx++) {
            boolean active = (idx >= mStrokeBegin && idx <= mStrokeEnd);
            float degree = (START_DEGREE + mStrokeDegree[idx]) % 360;

            int dotCenterX = (int) (centerX + (radius * Math.cos(degree * 3.1415 / 180)));
            int dotCenterY = (int) (centerY + (radius * Math.sin(degree * 3.1415 / 180)));

            mPaint.setColor(active ? mStrokeColorActive : mStrokeColorNormal);
            canvas.drawCircle(dotCenterX, dotCenterY, dotRadius, mPaint);
        }
    }

    private void paintStrokeArc(Canvas canvas, Rect rect) {
        mPaint.reset();
        mPaint.setAntiAlias(true);
        mPaint.setStrokeWidth(mStrokeWidth);
        mPaint.setStyle(Paint.Style.STROKE);

        float degree, sweep;

        if (mStrokeBegin < 0 && mStrokeEnd >= mStrokeCount) {
            degree = 0;
            sweep = 360;
        } else if ((mStrokeBegin > mStrokeCount && mStrokeEnd < 0) ||
                (mStrokeBegin < 0 && mStrokeEnd < 0) || (mStrokeBegin > mStrokeCount && mStrokeEnd > mStrokeCount)) {
            degree = 0;
            sweep = 0;
        } else if (mStrokeBegin == mStrokeEnd) {
            degree = mStrokeBegin * 360 / mStrokeCount;
            sweep = 360 / mStrokeCount;
        } else if (mStrokeBegin > mStrokeEnd) {
            int begin = mStrokeBegin >= mStrokeCount ? mStrokeCount : mStrokeBegin;
            int end = mStrokeEnd < 0 ? 0 : mStrokeEnd;
            degree = (begin * 360) / mStrokeCount;
            sweep = ((mStrokeCount - begin + end) * 360) / mStrokeCount;
        } else {
            int begin = mStrokeBegin < 0 ? 0 : mStrokeBegin;
            int end = mStrokeEnd >= mStrokeCount ? mStrokeCount : mStrokeEnd;
            degree = (begin * 360) / mStrokeCount;
            sweep = ((end - begin) * 360) / mStrokeCount;
        }
        degree = (START_DEGREE + degree) % 360;

        RectF rectf = new RectF(rect);

        mPaint.setColor(mStrokeColorActive);
        canvas.drawArc(rectf, degree, sweep, false, mPaint);

        mPaint.setColor(mStrokeColorNormal);
        canvas.drawArc(rectf, (degree + sweep) % 360, 360 - sweep, false, mPaint);
    }

    private void updateVector() {
        int width = getMeasuredWidth();
        int height = getMeasuredHeight();

        Rect rect = new Rect(
                getPaddingStart(),
                getPaddingTop(),
                width - getPaddingEnd(),
                height - getPaddingBottom());

        int radius = Math.min(rect.width(), rect.height()) / 2;
        int strokeShift = (int) Math.ceil(mStrokeWidth / 2);

        mRectBitmap = new Rect(
                rect.centerX() - radius,
                rect.centerY() - radius,
                rect.centerX() + radius,
                rect.centerY() + radius);

        mRectBorder = new Rect(
                rect.centerX() - radius + strokeShift,
                rect.centerY() - radius + strokeShift,
                rect.centerX() + radius - strokeShift,
                rect.centerY() + radius - strokeShift);

        mStrokeDegree = new float[mStrokeCount];
        for (int idx = 0; idx < mStrokeCount; idx++) {
            mStrokeDegree[idx] = 360 * idx / mStrokeCount;
        }
    }

    public void setBitmap(Bitmap bitmap) {
        if (mBitmap != null && !mBitmap.isRecycled())
            mBitmap.recycle();
        mBitmap = null;

        if (bitmap == null)
            return;

        int width = bitmap.getWidth();
        int height = bitmap.getHeight();

        if (width == 0 || height == 0)
            return;

        int size = Math.min(width, height);
        Rect rectDst = new Rect(0, 0, size, size);
        Rect rectSrc = new Rect((width - size) / 2, (height - size) / 2, (width + size) / 2, (height + size) / 2);

        mBitmap = Bitmap.createBitmap(size, size, Bitmap.Config.ARGB_8888);

        Canvas canvas = new Canvas(mBitmap);
        canvas.drawColor(Color.WHITE, PorterDuff.Mode.CLEAR);

        Paint paint = new Paint();

        paint.setColor(Color.WHITE);
        canvas.drawCircle(rectDst.centerX(), rectDst.centerY(), rectDst.width() / 2, paint);

        paint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.SRC_IN));
        canvas.drawBitmap(bitmap, rectDst, rectSrc, paint);
    }
}
