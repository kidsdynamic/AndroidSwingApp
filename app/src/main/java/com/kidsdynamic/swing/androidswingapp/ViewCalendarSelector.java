package com.kidsdynamic.swing.androidswingapp;

import android.content.Context;
import android.content.res.TypedArray;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.TextView;

/**
 * Created by 03543 on 2017/1/31.
 */

public class ViewCalendarSelector extends RelativeLayout {
    static final int SELECT_PREV = -1;
    static final int SELECT_NEXT = 1;
    static final int SELECT_DATE = 0;

    private RelativeLayout mThis = this;
    private TextView mViewDate;
    private TextView mViewTriangleLeft;
    private TextView mViewTriangleRight;

    private String mText = "";
    private int mTextSize = 12;
    private int mTextColor = 0;
    private int mTextColorHint = 0;

    private OnSelectListener mSelectListener = null;

    public ViewCalendarSelector(Context context) {
        super(context);
        init(context, null);
    }

    public ViewCalendarSelector(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context, attrs);
    }

    public ViewCalendarSelector(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        init(context, attrs);
    }

    private void init(Context context, AttributeSet attrs) {
        if (attrs != null) {
            TypedArray typedArray = context.obtainStyledAttributes(
                    attrs, R.styleable.ViewCalendarSelector);

            final int count = typedArray.getIndexCount();
            for (int idx = 0; idx < count; idx++) {
                final int attr = typedArray.getIndex(idx);

                if (attr == R.styleable.ViewCalendarSelector_android_text) {
                    mText = typedArray.getString(attr);
                } else if (attr == R.styleable.ViewCalendarSelector_android_textSize) {
                    mTextSize = typedArray.getDimensionPixelOffset(attr, mTextSize);
                } else if (attr == R.styleable.ViewCalendarSelector_android_textColor) {
                    mTextColor = typedArray.getColor(attr, mTextColor);
                } else if (attr == R.styleable.ViewCalendarSelector_android_textColorHint) {
                    mTextColorHint = typedArray.getColor(attr, mTextColorHint);
                }
            }

            typedArray.recycle();
        }

        inflate(getContext(), R.layout.view_calendar_selector, this);

        mViewTriangleLeft = (TextView) findViewById(R.id.view_calendar_selector_left);
        mViewTriangleLeft.setTextSize(TypedValue.COMPLEX_UNIT_PX, mTextSize);
        mViewTriangleLeft.setTextColor(mTextColorHint);
        mViewTriangleLeft.setOnClickListener(mOnClickListener);

        mViewTriangleRight = (TextView) findViewById(R.id.view_calendar_selector_right);
        mViewTriangleRight.setTextSize(TypedValue.COMPLEX_UNIT_PX, mTextSize);
        mViewTriangleRight.setTextColor(mTextColorHint);
        mViewTriangleRight.setOnClickListener(mOnClickListener);

        mViewDate = (TextView) findViewById(R.id.view_calendar_selector_date);
        mViewDate.setTextSize(TypedValue.COMPLEX_UNIT_PX, mTextSize);
        mViewDate.setTextColor(mTextColor);
        mViewDate.setText(mText);
        mViewDate.setOnClickListener(mOnClickListener);
    }

    private View.OnClickListener mOnClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            if (mSelectListener == null)
                return;

            if (view == mViewTriangleLeft)
                mSelectListener.OnClick(mThis, SELECT_PREV);
            else if (view == mViewTriangleRight)
                mSelectListener.OnClick(mThis, SELECT_NEXT);
            else if (view == mViewDate)
                mSelectListener.OnClick(mThis, SELECT_DATE);
        }
    };

    public void setOnSelectListener(OnSelectListener listener) {
        mSelectListener = listener;
    }

    interface OnSelectListener {
        void OnClick(View view, int option);
    }
}
