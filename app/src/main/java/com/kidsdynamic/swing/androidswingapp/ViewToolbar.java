package com.kidsdynamic.swing.androidswingapp;

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

/**
 * Created by 03543 on 2017/1/2.
 */

public class ViewToolbar extends ViewGroup {
    static final int ACTION_BUTTON1 = 0;
    static final int ACTION_BUTTON2 = 1;
    static final int ACTION_TITLE = 10;

    private int mMaxWidth;
    private int mMaxHeight;

    private TextView mViewTitle;
    private ImageView mViewBackground;
    private ImageView mViewButton1;
    private ImageView mViewButton2;

    private OnActionListener mOnActionListener;

    public ViewToolbar(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context, attrs);
    }

    public ViewToolbar(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        init(context, attrs);
    }

    private void init(Context context, AttributeSet attrs) {
        mMaxWidth = getContext().getResources().getDisplayMetrics().widthPixels;
        mMaxHeight = getContext().getResources().getDisplayMetrics().heightPixels / 10;

        LayoutInflater inflater = LayoutInflater.from(context);
        inflater.inflate(R.layout.view_toolbar, this, true);

        mViewTitle = (TextView) findViewById(R.id.toolbar_title);
        mViewTitle.setText("Title");
        mViewTitle.setOnClickListener(mItemClickListener);

        mViewBackground = (ImageView) findViewById(R.id.toolbar_background);

        mViewButton1 = (ImageView) findViewById(R.id.toolbar_button1);
        mViewButton1.setOnClickListener(mItemClickListener);

        mViewButton2 = (ImageView) findViewById(R.id.toolbar_button2);
        mViewButton2.setOnClickListener(mItemClickListener);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        int width = measureWidth(widthMeasureSpec);
        int height = measureHeight(heightMeasureSpec);

        measureChildren(
                MeasureSpec.makeMeasureSpec(width, MeasureSpec.getMode(widthMeasureSpec)),
                MeasureSpec.makeMeasureSpec(height, MeasureSpec.getMode(heightMeasureSpec)));

        setMeasuredDimension(width, height);
    }

    private int measureWidth(int measureSpec) {
        int result;
        int specMode = MeasureSpec.getMode(measureSpec);
        int specSize = MeasureSpec.getSize(measureSpec);

        if (specMode == MeasureSpec.EXACTLY) {
            result = specSize;
        } else if (specMode == MeasureSpec.AT_MOST) {
            result = Math.min(mMaxWidth, specSize);
        } else {
            result = mMaxWidth;
        }

        return result;
    }

    private int measureHeight(int measureSpec) {
        int result;
        int specMode = MeasureSpec.getMode(measureSpec);
        int specSize = MeasureSpec.getSize(measureSpec);

        if (specMode == MeasureSpec.EXACTLY) {
            result = specSize;
        } else if (specMode == MeasureSpec.AT_MOST) {
            result = Math.min(mMaxHeight, specSize);
        } else {
            result = mMaxHeight;
        }

        return result;
    }

    @Override
    protected void onLayout(boolean changed, int left, int top, int right, int bottom) {
        final int count = getChildCount();
        for (int idx = 0; idx < count; idx++) {
            getChildAt(idx).layout(left, top, right, bottom);
        }
    }

    public interface OnActionListener {
        void onClick(View view, int action);
    }

    public void setOnActionListener(OnActionListener listener) {
        mOnActionListener = listener;
    }

    private View.OnClickListener mItemClickListener = new OnClickListener() {
        @Override
        public void onClick(View view) {
            if(mOnActionListener == null)
                return;

            if(view == mViewTitle)
                mOnActionListener.onClick(mViewTitle, ACTION_TITLE);
            else if(view == mViewButton1)
                mOnActionListener.onClick(mViewButton1, ACTION_BUTTON1);
            else if(view == mViewButton2)
                mOnActionListener.onClick(mViewButton2, ACTION_BUTTON2);
        }
    };
}
