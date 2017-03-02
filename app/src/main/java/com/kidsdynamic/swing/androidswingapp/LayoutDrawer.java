package com.kidsdynamic.swing.androidswingapp;

import android.content.Context;
import android.content.res.TypedArray;
import android.util.AttributeSet;
import android.util.Log;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;

/**
 * Created by 03543 on 2017/2/28.
 */

public class LayoutDrawer extends ViewGroup {

    private int mDragging;
    private float mDragSlop = 30;

    public LayoutDrawer(Context context) {
        super(context);
        init(context, null);
    }

    public LayoutDrawer(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context, attrs);
    }

    private void init(Context context, AttributeSet attrs) {
        if (attrs != null) {
            TypedArray typedArray = context.obtainStyledAttributes(
                    attrs, R.styleable.LayoutDrawer);

            final int count = typedArray.getIndexCount();
            for (int idx = 0; idx < count; idx++) {
                final int attr = typedArray.getIndex(idx);

            }

            typedArray.recycle();
        }
    }

    float mLastX, mLastY, mStartY, mStartX;

    @Override
    public boolean onInterceptTouchEvent(MotionEvent event) {
        Log.d("xxx", "(" + event.getX() + "," + event.getY() + ") onInterceptTouchEvent");

        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                mStartX = mLastX = event.getX();
                mStartY = mLastY = event.getY();
                break;

            case MotionEvent.ACTION_CANCEL:
            case MotionEvent.ACTION_UP:
                mDragging = 0;
                break;

            case MotionEvent.ACTION_MOVE:
                float x = event.getX();
                float y = event.getY();

                if (Math.abs(x - mStartX) > mDragSlop || Math.abs(y - mStartY) > mDragSlop) {
                    if ((x - mStartX) > mDragSlop)
                        mDragging = Gravity.START;
                    else if ((mStartX - x) > mDragSlop)
                        mDragging = Gravity.END;
                    else if ((y - mStartY) > mDragSlop)
                        mDragging = Gravity.TOP;
                    else if ((mStartY - y) > mDragSlop)
                        mDragging = Gravity.BOTTOM;

                    return true;
                }
                break;
        }

        return false;
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        Log.d("xxx", "(" + event.getX() + "," + event.getY() + ") onTouchEvent");
        switch (event.getAction()) {
            case MotionEvent.ACTION_CANCEL:
            case MotionEvent.ACTION_UP:
                mDragging = 0;
                break;

            case MotionEvent.ACTION_MOVE:
                if (mDragging != 0) {
                    Log.d("xxx", "mDragging:" + mDragging);
                }

                mLastX = event.getX();
                mLastY = event.getY();
                break;
        }

        return true;
    }

    @Override
    protected void onLayout(boolean changed, int left, int top, int right, int bottom) {
        View text2 = findViewById(R.id.text2);
        View centerView = getTopView(Gravity.CENTER);
        Log.d("xxx", "topview: " + (centerView == text2) + "(" + centerView.getMeasuredWidth() + "," + centerView.getMeasuredHeight() + ")");
        centerView.layout(left, top, right, bottom);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        int maxWidth = 0;
        int maxHeight = 0;

        int count = getChildCount();

        for (int idx = 0; idx < count; idx++) {
            final View child = getChildAt(idx);

            measureChild(child, widthMeasureSpec, heightMeasureSpec);

            maxWidth = Math.max(child.getMeasuredWidth(), maxWidth);
            maxHeight = Math.max(child.getMeasuredHeight(), maxHeight);
        }

        setMeasuredDimension(maxWidth, maxHeight);
    }

    private View getTopView(int gravity) {
        View topView = null;
        int count = getChildCount();

        for (int idx = 0; idx < count; idx++) {
            final View child = getChildAt(idx);

            LayoutParams layoutParams = (LayoutParams) child.getLayoutParams();
            if (layoutParams.mGravity == gravity)
                topView = child;
        }

        return topView;
    }

    @Override
    public LayoutParams generateLayoutParams(AttributeSet attrs) {
        return new LayoutDrawer.LayoutParams(getContext(), attrs);
    }

    @Override
    protected LayoutParams generateDefaultLayoutParams() {
        return new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT);
    }

    @Override
    protected ViewGroup.LayoutParams generateLayoutParams(ViewGroup.LayoutParams params) {
        return new LayoutParams(params);
    }

    @Override
    protected boolean checkLayoutParams(ViewGroup.LayoutParams params) {
        return params instanceof LayoutParams;
    }

    public static class LayoutParams extends ViewGroup.LayoutParams {

        public int mGravity = Gravity.CENTER;

        public LayoutParams(Context context, AttributeSet attrs) {
            super(context, attrs);

            init(context, attrs);
        }

        public LayoutParams(int width, int height) {
            super(width, height);

            setGravity(Gravity.CENTER);
        }

        public LayoutParams(int width, int height, int gravity) {
            super(width, height);

            setGravity(gravity);
        }

        public LayoutParams(ViewGroup.LayoutParams source) {
            super(source);
        }

        private void init(Context context, AttributeSet attrs) {
            if (attrs != null) {
                TypedArray typedArray = context.obtainStyledAttributes(attrs, R.styleable.LayoutDrawer);

                final int count = typedArray.getIndexCount();
                for (int idx = 0; idx < count; idx++) {
                    final int attr = typedArray.getIndex(idx);

                    if (attr == R.styleable.LayoutDrawer_android_layout_gravity) {
                        setGravity(typedArray.getInt(attr, mGravity));
                    }
                }
            }
        }

        public void setGravity(int gravity) {
            switch (gravity) {
                case Gravity.CENTER:
                case Gravity.TOP | Gravity.BOTTOM:
                case Gravity.START | Gravity.END | Gravity.LEFT | Gravity.RIGHT:
                    mGravity = gravity;
                    break;

                default:
                    mGravity = 0;
            }
        }
    }
}
