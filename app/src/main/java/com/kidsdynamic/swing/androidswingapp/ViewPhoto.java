package com.kidsdynamic.swing.androidswingapp;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
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

public class ViewPhoto extends ViewSponge {
    private float mStrokeWidthDp = 4;

    private Rect mRectPhoto;
    private Rect mRectBorder;
    private int mStrokeWidth;

    private Bitmap mSource = null;
    private Bitmap mPhoto = null;

    private int mColorSelect;
    private int mColorNormal;

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
        mColorNormal = ContextCompat.getColor(context, R.color.color_white);

        if (attrs != null) {
            TypedArray typedArray = context.obtainStyledAttributes(
                    attrs, R.styleable.ViewPhoto);

            final int count = typedArray.getIndexCount();
            for (int idx = 0; idx < count; idx++) {
                final int attr = typedArray.getIndex(idx);

                if (attr == R.styleable.ViewPhoto_android_src) {
                    Drawable drawable = typedArray.getDrawable(R.styleable.ViewPhoto_android_src);
                    setPhoto(((BitmapDrawable) drawable).getBitmap());
                } else if (attr == R.styleable.ViewPhoto_borderStroke) {
                    mStrokeWidthDp = typedArray.getDimension(R.styleable.ViewPhoto_borderStroke, mStrokeWidthDp);
                } else if (attr == R.styleable.ViewPhoto_borderSelectColor) {
                    mColorSelect = typedArray.getColor(R.styleable.ViewPhoto_borderSelectColor, mColorSelect);
                } else if (attr == R.styleable.ViewPhoto_borderNormalColor) {
                    mColorNormal = typedArray.getColor(R.styleable.ViewPhoto_borderNormalColor, mColorNormal);
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

        mStrokeWidth = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, mStrokeWidthDp, context.getResources().getDisplayMetrics());
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);

        updateRects(getMeasuredWidth(), getMeasuredHeight());
        makePhoto(mSource);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        canvas.drawARGB(0, 0, 0, 0);

        if (mPhoto != null)
            paintPhoto(canvas, mRectPhoto, mShowDarker);

        if (mShowBorder)
            paintBorder(canvas, mRectBorder, mSelected ? mColorSelect : mColorNormal);

        if (mShowCross)
            paintCross(canvas, mRectBorder, mSelected ? mColorSelect : mColorNormal);
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
        Paint paint = new Paint();

        if (darker) {
            paint.setColorFilter(FactoryColorFilter.adjustColor(-64, 0, 0, 0));
        }

        canvas.drawBitmap(mPhoto,
                new Rect(0, 0, mPhoto.getWidth(), mPhoto.getHeight()), rect, paint);
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

    private void updateRects(int width, int height) {
        Rect rect = new Rect(0, 0, width, height);
        int radius = Math.min(width, height) / 2;
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

    public void setSelected(boolean selected) {
        mSelected = selected;
        invalidate();
    }

    public boolean getSelected() {
        return mSelected;
    }

    public void setShowBorder(boolean show) {
        mShowBorder = show;
        invalidate();
    }

    public boolean getShowBorder() {
        return mShowBorder;
    }

    public void setShowCross(boolean show) {
        mShowCross = show;
        invalidate();
    }

    public boolean getShowCross() {
        return mShowCross;
    }

    public void setShowDarker(boolean show) {
        mShowDarker = show;
        invalidate();
    }

    public boolean getShowDarker() {
        return mShowDarker;
    }

    public void setPhoto(int source) {
        setPhoto(BitmapFactory.decodeResource(getResources(), source));
    }

    public void setPhoto(Bitmap photo) {
        if (mSource != null && !mSource.isRecycled())
            mSource.recycle();
        mSource = null;

        if (photo == null)
            return;

        mSource = Bitmap.createBitmap(photo, 0, 0, photo.getWidth(), photo.getHeight());
        makePhoto(mSource);
    }

    public void makePhoto(Bitmap photo) {

        if (mPhoto != null && !mPhoto.isRecycled())
            mPhoto.recycle();
        mPhoto = null;

        if (photo == null)
            return;

        int width = photo.getWidth();
        int height = photo.getHeight();

        if (width == 0 || height == 0)
            return;

        int size = Math.min(width, height);
        Rect rectDst = new Rect(0, 0, size, size);
        Rect rectSrc = new Rect((width - size) / 2, (height - size) / 2, (width + size) / 2, (height + size) / 2);

        mPhoto = Bitmap.createBitmap(size, size, Bitmap.Config.ARGB_8888);

        Canvas canvas = new Canvas(mPhoto);
        canvas.drawColor(Color.WHITE, PorterDuff.Mode.CLEAR);

        Paint paint = new Paint();

        paint.setColor(Color.WHITE);
        canvas.drawCircle(rectDst.centerX(), rectDst.centerY(), rectDst.width() / 2, paint);

        paint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.SRC_IN));
        canvas.drawBitmap(photo, rectDst, rectSrc, paint);
    }
}
