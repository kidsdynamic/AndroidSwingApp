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
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.support.v4.content.ContextCompat;
import android.util.AttributeSet;
import android.util.Log;
import android.util.TypedValue;
import android.view.View;

/**
 * Created by 03543 on 2017/1/2.
 */

public class ViewPhoto extends View {
    private int mDesiredWidth;
    private int mDesiredHeight;
    private int mMeasureWidth = 0;
    private int mMeasureHeight = 0;

    private float mStrokeWidthDp = 4;

    private Rect mRectPhoto;
    private Rect mRectBorder;
    private int mStrokeWidth;

    private Bitmap mPhotoSource = null;
    private Bitmap mPhotoCircle = null;

    private int mColorSelect = 0xFF000000;
    private int mColorNormal = 0xFF888888;

    private boolean mSelected = true;
    private boolean mShowCross = true;
    private boolean mShowBorder = true;
    private boolean mShowDarker = false;

    public ViewPhoto(Context context) {
        super(context);
        init(context, null);
    }

    public ViewPhoto(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context, attrs);
    }

    public ViewPhoto(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        init(context, attrs);
    }

    private void init(Context context, AttributeSet attrs) {
        mColorSelect = ContextCompat.getColor(context, R.color.color_orange);
        mColorNormal = ContextCompat.getColor(context, R.color.color_gray_deep);

        if (attrs != null) {
            TypedArray typedArray = context.obtainStyledAttributes(
                    attrs, R.styleable.ViewPhoto);

            final int count = typedArray.getIndexCount();
            for (int idx = 0; idx < count; idx++) {
                final int attr = typedArray.getIndex(idx);

                if (attr == R.styleable.ViewPhoto_android_src) {
                    Drawable drawable = typedArray.getDrawable(R.styleable.ViewPhoto_android_src);
                    mPhotoSource = ((BitmapDrawable) drawable).getBitmap();
                } else if (attr == R.styleable.ViewPhoto_borderStroke) {
                    mStrokeWidthDp = typedArray.getDimension(R.styleable.ViewPhoto_borderStroke, mStrokeWidthDp);
                } else if (attr == R.styleable.ViewPhoto_borderSelectColor) {
                    mColorSelect = typedArray.getColor(R.styleable.ViewPhoto_borderSelectColor, mColorSelect);
                } else if (attr == R.styleable.ViewPhoto_borderNormalColor) {
                    mColorSelect = typedArray.getColor(R.styleable.ViewPhoto_borderNormalColor, mColorSelect);
                } else if (attr == R.styleable.ViewPhoto_selected) {
                    mSelected = typedArray.getBoolean(R.styleable.ViewPhoto_selected, mSelected);
                } else if (attr == R.styleable.ViewPhoto_showCross) {
                    mShowCross = typedArray.getBoolean(R.styleable.ViewPhoto_showCross, mShowCross);
                } else if (attr == R.styleable.ViewPhoto_showBorder) {
                    mShowBorder = typedArray.getBoolean(R.styleable.ViewPhoto_showBorder, mShowBorder);
                } else if (attr == R.styleable.ViewPhoto_showDarker) {
                    mShowDarker = typedArray.getBoolean(R.styleable.ViewPhoto_showDarker, mShowDarker);
                }
            }

            typedArray.recycle();
        }

        mStrokeWidth = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, mStrokeWidthDp, context.getResources().

                getDisplayMetrics()

        );

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
        if (mMeasureWidth != getMeasuredWidth() || mMeasureHeight != getMeasuredHeight()) {
            updateRects();

            if (mPhotoCircle != null && !mPhotoCircle.isRecycled())
                mPhotoCircle.recycle();
            mPhotoCircle = makePhoto(mPhotoSource);
        }

        paintBackground(canvas);
        paintPhoto(canvas, mRectPhoto, mShowDarker);

        if (mShowBorder)
            paintBorder(canvas, mRectBorder, mSelected ? mColorSelect : mColorNormal);

        if (mShowCross)
            paintCross(canvas, mRectBorder, mSelected ? mColorSelect : mColorNormal);
    }

    private void paintBackground(Canvas canvas) {
        canvas.drawARGB(0, 0, 0, 0);
    }

    private void paintBorder(Canvas canvas, Rect rect, int color) {
        final Paint paint = new Paint();

        paint.setAntiAlias(true);
        paint.setColor(color);
        paint.setStrokeWidth(mStrokeWidth);
        paint.setStyle(Paint.Style.STROKE);

        canvas.drawCircle(rect.centerX(), rect.centerY(), rect.width() / 2, paint);
    }

    private void paintPhoto(Canvas canvas, Rect rect, boolean darker) {
        canvas.drawBitmap(mPhotoCircle,
                new Rect(0, 0, mPhotoCircle.getWidth(), mPhotoCircle.getHeight()),
                mRectPhoto, null);
    }

    private void paintCross(Canvas canvas, Rect rect, int color) {
        final Paint paint = new Paint();
        final int length = rect.width() / 20;

        paint.setAntiAlias(true);
        paint.setColor(color);
        paint.setStrokeWidth(mStrokeWidth);
        paint.setStyle(Paint.Style.STROKE);

        canvas.drawLine(rect.centerX(), rect.centerY() - length, rect.centerX(), rect.centerY() + length, paint);
        canvas.drawLine(rect.centerX() - length, rect.centerY(), rect.centerX() + length, rect.centerY(), paint);
    }

    private void updateRects() {
        mMeasureWidth = getMeasuredWidth();
        mMeasureHeight = getMeasuredHeight();

        Rect rect = new Rect(0, 0, mMeasureWidth, mMeasureHeight);
        int radius = Math.min(mMeasureWidth, mMeasureHeight) / 2;
        int strokeWidth = mStrokeWidth / 2;

        mRectPhoto = new Rect(
                rect.centerX() - radius,
                rect.centerY() - radius,
                rect.centerX() + radius,
                rect.centerY() + radius);

        mRectBorder = new Rect(
                rect.centerX() - radius + strokeWidth,
                rect.centerY() - radius + strokeWidth,
                rect.centerX() + radius - strokeWidth,
                rect.centerY() + radius - strokeWidth);
    }

    private Bitmap makePhoto(Bitmap srcBitmap) {
        Bitmap tarBitmap = Bitmap.createBitmap(mRectPhoto.width(), mRectPhoto.height(), Bitmap.Config.ARGB_8888);
        int radius = mRectPhoto.width() / 2;
        int centerX = mRectPhoto.width() / 2;
        int centerY = mRectPhoto.width() / 2;

        final Canvas canvas = new Canvas(tarBitmap);
        canvas.drawARGB(0, 255, 0, 0);

        final Paint paint = new Paint();
        paint.setAntiAlias(true);
        paint.setStrokeWidth(0);
        paint.setStyle(Paint.Style.FILL);

        if (srcBitmap != null && !srcBitmap.isRecycled()) {
            paint.setColor(0xFFFFFFFF);
            canvas.drawCircle(centerX, centerY, radius, paint);

            paint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.SRC_IN));
            canvas.drawBitmap(srcBitmap,
                    new Rect(0, 0, srcBitmap.getWidth(), srcBitmap.getHeight()),
                    new Rect(0, 0, tarBitmap.getWidth(), tarBitmap.getHeight()), paint);
        }

        return tarBitmap;
    }

    public void setShowBorder(boolean show) {
        if (show == mShowBorder)
            return;

        mShowBorder = show;
        postInvalidate();
    }

    public boolean getShowBorder() {
        return mShowBorder;
    }

    public void setShowCross(boolean show) {
        if (show == mShowCross)
            return;

        mShowCross = show;
        postInvalidate();
    }

    public boolean getShowCross() {
        return mShowCross;
    }

    public void setShowDarker(boolean show) {
        if (show == mShowDarker)
            return;

        mShowDarker = show;
        postInvalidate();
    }

    public boolean getShowDarker() {
        return mShowDarker;
    }

    public void setPhotoResource(int source) {
        postInvalidate();
    }

    public void setPhotoBitmap(Bitmap source) {
        postInvalidate();
    }

    public void clearPhoto() {
        postInvalidate();
    }
}
