package com.kidsdynamic.swing.androidswingapp;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.ColorFilter;
import android.graphics.ColorMatrix;
import android.graphics.ColorMatrixColorFilter;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.Rect;
import android.graphics.RectF;
import android.graphics.Xfermode;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.util.DisplayMetrics;
import android.util.Log;
import android.util.TypedValue;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.ScaleGestureDetector;
import android.view.View;

/**
 * Created by 03543 on 2017/1/7.
 */

public class ViewPhotoClip extends View {

    private int mDesiredWidth;
    private int mDesiredHeight;

    private RectF mRectClipSource;
    private RectF mRectClip;
    private Bitmap mBitmapPhoto = null;

    private int mRotate;

    private GestureDetector mGestures;
    private ScaleGestureDetector mScaleGesture;

    private Matrix mMatrixPhoto;
    private Matrix mMatrixClip;
    private Matrix mMatrixSelect;

    private Paint mPaint;
    private Xfermode mXfermodeSrcOut;
    private Xfermode mXfermodeSrcIn;
    private Xfermode mXfermodeDstOver;
    private ColorFilter mColorFilter;

    public ViewPhotoClip(Context context) {
        super(context);
        init(context, null);
    }

    public ViewPhotoClip(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context, attrs);
    }

    public ViewPhotoClip(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        init(context, attrs);
    }

    private void init(Context context, AttributeSet attrs) {
        if (attrs != null) {
            TypedArray typedArray = context.obtainStyledAttributes(
                    attrs, R.styleable.ViewPhotoClip);

            final int count = typedArray.getIndexCount();
            for (int idx = 0; idx < count; idx++) {
                final int attr = typedArray.getIndex(idx);

                if (attr == R.styleable.ViewPhotoClip_android_src) {
                    Drawable drawable = typedArray.getDrawable(attr);
                    try {
                        mBitmapPhoto = ((BitmapDrawable) drawable).getBitmap().copy(Bitmap.Config.ARGB_8888, true);
                    } catch (NullPointerException e) {
                        mBitmapPhoto = null;
                        Log.d("swing", "ViewPhotoClip(init):" + Log.getStackTraceString(e));
                    }
                }
            }

            typedArray.recycle();
        }

        mDesiredWidth = getResources().getDisplayMetrics().widthPixels;
        mDesiredHeight = getResources().getDisplayMetrics().heightPixels;

        if (mBitmapPhoto == null) {
            mBitmapPhoto = Bitmap.createBitmap(mDesiredWidth, mDesiredHeight, Bitmap.Config.ARGB_8888);
            mBitmapPhoto.eraseColor(Color.WHITE);
        }

        mRotate = 0;

        mXfermodeSrcOut = new PorterDuffXfermode(PorterDuff.Mode.SRC_OUT);
        mXfermodeSrcIn = new PorterDuffXfermode(PorterDuff.Mode.SRC_IN);
        mXfermodeDstOver = new PorterDuffXfermode(PorterDuff.Mode.DST_OVER);

        mColorFilter = FactoryColorFilter.adjustColor(32, 0, 0, 0);

        mRectClipSource = new RectF();
        mRectClip = new RectF();

        mPaint = new Paint();

        mScaleGesture = new ScaleGestureDetector(getContext(), new ScaleListener());
        mGestures = new GestureDetector(getContext(), new GestureListener());
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {

        if (event.getAction() == MotionEvent.ACTION_DOWN) {
            if (mRectClip.contains(event.getX(), event.getY()))
                mMatrixSelect = mMatrixClip;
            else
                mMatrixSelect = mMatrixPhoto;
        }

        mScaleGesture.onTouchEvent(event);
        mGestures.onTouchEvent(event);
        return true;
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
        heightMeasureSpec = MeasureSpec.makeMeasureSpec(height, heightMode);

        super.onMeasure(widthMeasureSpec, heightMeasureSpec);

        reset();
    }

    @Override
    protected void onDraw(Canvas canvas) {
        paintPhoto(canvas);

        // Paint border
        mPaint.reset();
        mPaint.setAntiAlias(true);
        mPaint.setStyle(Paint.Style.STROKE);
        mPaint.setStrokeWidth(10);
        mPaint.setColor(Color.WHITE);
        canvas.drawCircle(mRectClip.centerX(), mRectClip.centerY(), mRectClip.width() / 2, mPaint);
    }

    private void paintPhoto(Canvas canvas) {
        canvas.drawColor(Color.WHITE, PorterDuff.Mode.CLEAR);

        mPaint.reset();
        mPaint.setColor(Color.WHITE);
        canvas.drawCircle(mRectClip.centerX(), mRectClip.centerY(), mRectClip.width() / 2, mPaint);

        mPaint.reset();
        mPaint.setColorFilter(mColorFilter);
        mPaint.setXfermode(mXfermodeSrcOut);
        canvas.drawBitmap(mBitmapPhoto, mMatrixPhoto, mPaint);

        mPaint.reset();
        mPaint.setXfermode(mXfermodeDstOver);
        canvas.drawBitmap(mBitmapPhoto, mMatrixPhoto, mPaint);

        canvas.drawColor(Color.WHITE, PorterDuff.Mode.DST_OVER);
    }

    public void setPhoto(Bitmap photo) {
        if (mBitmapPhoto != null && !mBitmapPhoto.isRecycled())
            mBitmapPhoto.recycle();

        // Scale down photo. the maximum size is screen size
        int maxImageSize = Math.max(
                getResources().getDisplayMetrics().widthPixels,
                getResources().getDisplayMetrics().heightPixels);

        float ratio = Math.min(
                (float) maxImageSize / photo.getWidth(),
                (float) maxImageSize / photo.getHeight());

        int width = Math.round(ratio * photo.getWidth());
        int height = Math.round(ratio * photo.getHeight());

        mBitmapPhoto = Bitmap.createScaledBitmap(photo, width, height, true);

        reset();
    }

    public void rotatePhoto(int degree) {
        Matrix matrix = new Matrix();
        matrix.postRotate(degree);

        Bitmap bitmap = Bitmap.createBitmap(mBitmapPhoto, 0, 0, mBitmapPhoto.getWidth(), mBitmapPhoto.getHeight(), matrix, true);
        setPhoto(bitmap);

        bitmap.recycle();
    }

    public void drawClip(Canvas canvas) {
        if (mBitmapPhoto == null)
            return;

        Bitmap tmpBitmap = Bitmap.createBitmap(getMeasuredWidth(), getMeasuredHeight(), Bitmap.Config.ARGB_8888);
        Canvas tmpCanvas = new Canvas(tmpBitmap);

        paintPhoto(tmpCanvas);

        canvas.drawBitmap(tmpBitmap,
                new Rect((int) mRectClip.left, (int) mRectClip.top, (int) mRectClip.right, (int) mRectClip.bottom),
                new Rect(0, 0, canvas.getWidth(), canvas.getHeight()), null);

        tmpBitmap.recycle();
    }

    private void reset() {
        float transX, transY;

        int size = (Math.min(getMeasuredWidth(), getMeasuredHeight())) * 2 / 3;
        mRectClipSource = new RectF(0, 0, size, size);

        mMatrixPhoto = new Matrix();
        if (mBitmapPhoto != null) {
            transX = (getMeasuredWidth() - mBitmapPhoto.getWidth()) / 2;
            transY = (getMeasuredHeight() - mBitmapPhoto.getHeight()) / 2;
            mMatrixPhoto.postTranslate(transX, transY);
        }

        mMatrixClip = new Matrix();
        transX = (getMeasuredWidth() - mRectClipSource.width()) / 2;
        transY = (getMeasuredHeight() - mRectClipSource.height()) / 2;
        mMatrixClip.postTranslate(transX, transY);

        mMatrixClip.mapRect(mRectClip, mRectClipSource);

        invalidate();
    }

    private boolean matrixContains(Matrix matrix, RectF rect) {
        RectF rectMeasure = new RectF(0, 0, getMeasuredWidth(), getMeasuredHeight());
        RectF rectObject = new RectF(rect);
        matrix.mapRect(rectObject);

        return rectMeasure.contains(rectObject);
    }

    private boolean matrixContains(Matrix matrix, Bitmap photo) {
        int border = Math.min(getMeasuredWidth(), getMeasuredHeight()) / 2;

        RectF rectMeasure = new RectF(border, border, getMeasuredWidth() - border, getMeasuredHeight() - border); // Avoid photo move out of view
        RectF rectObject = new RectF(0, 0, photo.getWidth(), photo.getHeight());
        matrix.mapRect(rectObject);

        return rectMeasure.intersect(rectObject);
    }

    private class ScaleListener implements ScaleGestureDetector.OnScaleGestureListener {
        float mLastFocusX;
        float mLastFocusY;

        @Override
        public boolean onScale(ScaleGestureDetector detector) {
            Matrix saveMatrix = new Matrix(mMatrixSelect);
            Matrix transformationMatrix = new Matrix();
            float focusX = detector.getFocusX();
            float focusY = detector.getFocusY();

            //Zoom focus is where the fingers are centered,
            transformationMatrix.postTranslate(-focusX, -focusY);
            transformationMatrix.postScale(detector.getScaleFactor(), detector.getScaleFactor());

            // Adding focus shift to allow for scrolling with two pointers down.
            // Remove it to skip this functionality.
            // This could be done in fewer lines, but for clarity I do it this way here
            float focusShiftX = focusX - mLastFocusX;
            float focusShiftY = focusY - mLastFocusY;
            transformationMatrix.postTranslate(focusX + focusShiftX, focusY + focusShiftY);

            saveMatrix.postConcat(transformationMatrix);

            // Adding allow for target inside measure border
            boolean contains = mMatrixSelect == mMatrixClip ?
                    matrixContains(saveMatrix, mRectClipSource) :
                    matrixContains(saveMatrix, mBitmapPhoto);

            if (contains) {
                mMatrixSelect.set(saveMatrix);
                if (mMatrixSelect == mMatrixClip)
                    mMatrixClip.mapRect(mRectClip, mRectClipSource);

                mLastFocusX = focusX;
                mLastFocusY = focusY;

                invalidate();
            }

            return true;
        }

        @Override
        public boolean onScaleBegin(ScaleGestureDetector detector) {
            mLastFocusX = detector.getFocusX();
            mLastFocusY = detector.getFocusY();
            return true;
        }

        @Override
        public void onScaleEnd(ScaleGestureDetector detector) {

        }
    }

    public class GestureListener implements
            GestureDetector.OnGestureListener,
            GestureDetector.OnDoubleTapListener {

        @Override
        public boolean onSingleTapConfirmed(MotionEvent e) {
            return false;
        }

        @Override
        public boolean onDoubleTap(MotionEvent e) {
            reset();
            return true;
        }

        @Override
        public boolean onDoubleTapEvent(MotionEvent e) {
            return false;
        }

        @Override
        public boolean onDown(MotionEvent e) {
            return false;
        }

        @Override
        public void onShowPress(MotionEvent e) {

        }

        @Override
        public boolean onSingleTapUp(MotionEvent e) {
            return false;
        }

        @Override
        public boolean onScroll(MotionEvent e1, MotionEvent e2, float distanceX, float distanceY) {
            Matrix saveMatrix = new Matrix(mMatrixSelect);

            saveMatrix.postTranslate(-distanceX, -distanceY);

            // Adding allow for target inside measure border
            boolean contains = mMatrixSelect == mMatrixClip ?
                    matrixContains(saveMatrix, mRectClipSource) : matrixContains(saveMatrix, mBitmapPhoto);
            if (contains) {
                mMatrixSelect.set(saveMatrix);
                if (mMatrixSelect == mMatrixClip)
                    mMatrixClip.mapRect(mRectClip, mRectClipSource);

                invalidate();
            }

            return true;
        }

        @Override
        public void onLongPress(MotionEvent e) {

        }

        @Override
        public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX, float velocityY) {
            return false;
        }
    }
}

