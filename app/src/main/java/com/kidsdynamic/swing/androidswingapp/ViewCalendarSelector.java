package com.kidsdynamic.swing.androidswingapp;

import android.content.Context;
import android.content.res.TypedArray;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.TextView;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;

/**
 * Created by 03543 on 2017/1/31.
 */

public class ViewCalendarSelector extends RelativeLayout {
    static final int SELECT_PREV = -1;
    static final int SELECT_NEXT = 1;
    static final int SELECT_DATE = 0;

    static final int MODE_INVALID = -1;
    static final int MODE_YEAR = 0;
    static final int MODE_MONTH = 1;
    static final int MODE_DAY = 2;

    private RelativeLayout mThis = this;
    private TextView mViewDate;
    private TextView mViewTriangleLeft;
    private TextView mViewTriangleRight;

    private int mTextSize = 12;
    private int mTextColor = 0;
    private int mTextColorHint = 0;

    private int mMode = MODE_INVALID;
    private long mDate = Calendar.getInstance().getTimeInMillis();

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

                if (attr == R.styleable.ViewCalendarSelector_android_textSize) {
                    mTextSize = typedArray.getDimensionPixelOffset(attr, mTextSize);
                } else if (attr == R.styleable.ViewCalendarSelector_android_textColor) {
                    mTextColor = typedArray.getColor(attr, mTextColor);
                } else if (attr == R.styleable.ViewCalendarSelector_android_textColorHint) {
                    mTextColorHint = typedArray.getColor(attr, mTextColorHint);
                } else if (attr == R.styleable.ViewCalendarSelector_calendarMode) {
                    mMode = typedArray.getInt(attr, mMode);
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
        mViewDate.setText(makeDateString(mMode));
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

    public void setCalendarMode(int mode) {
        if (mode == mMode)
            return;

        if (mode == MODE_MONTH)
            mMode = MODE_MONTH;
        else
            mMode = MODE_DAY;

        mViewDate.setText(makeDateString(mMode));
    }

    public int getCalendarMode() {
        return mMode;
    }

    public void setDate(long mSecond) {
    }

    public long getDate() {
        return mDate;
    }

    public String makeDateString(int mode) {
        String format = "yyyy/MM/dd";

        if(mode == MODE_MONTH)
            format = "MMMM yyyy";
        else if(mode == MODE_DAY)
            format = "MMM dd, yyyy";

        SimpleDateFormat simpleDateFormat = new SimpleDateFormat(format, Locale.ENGLISH);

        return simpleDateFormat.format(mDate);
    }
}
