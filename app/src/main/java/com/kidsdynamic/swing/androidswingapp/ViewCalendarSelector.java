package com.kidsdynamic.swing.androidswingapp;

import android.content.Context;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.View;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

/**
 * Created by 03543 on 2017/1/31.
 */

public class ViewCalendarSelector extends ViewCalendar implements View.OnClickListener {
    static final int SELECT_PREV = -1;
    static final int SELECT_NEXT = 1;
    static final int SELECT_DATE = 0;

    private ViewCalendar mThis = this;
    private TextView mViewDate;
    private TextView mViewPrev;
    private TextView mViewNext;

    private OnSelectListener mSelectListener = null;

    public ViewCalendarSelector(Context context) {
        super(context);
        init(context, null);
    }

    public ViewCalendarSelector(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context, attrs);
    }

    private void init(Context context, AttributeSet attrs) {
    }

    @Override
    public void fillCell(Context context) {
        TableRow tableRow = new TableRow(context);
        tableRow.setLayoutParams(new TableLayout.LayoutParams(TableRow.LayoutParams.MATCH_PARENT, 0, 1));

        mViewPrev = new TextView(context);
        mViewPrev.setText("◀"); // U+25C0 &#9664;
        mViewPrev.setTextSize(TypedValue.COMPLEX_UNIT_PX, mTextSize);
        mViewPrev.setTextColor(mTextColorHint);
        mViewPrev.setGravity(Gravity.CENTER);
        mViewPrev.setLayoutParams(new TableRow.LayoutParams(0, TableRow.LayoutParams.MATCH_PARENT, 2));
        tableRow.addView(mViewPrev);

        mViewDate = new TextView(context);
        mViewDate.setTextSize(TypedValue.COMPLEX_UNIT_PX, mTextSize);
        mViewDate.setTypeface(mViewDate.getTypeface(), mTextStyle);
        mViewDate.setTextColor(mTextColor);
        mViewDate.setGravity(Gravity.CENTER);
        mViewDate.setLayoutParams(new TableRow.LayoutParams(0, TableRow.LayoutParams.MATCH_PARENT, 6));
        updateText();
        tableRow.addView(mViewDate);

        mViewNext = new TextView(context);
        mViewNext.setText("▶"); // U+25B6 &#9654;
        mViewNext.setTextSize(TypedValue.COMPLEX_UNIT_PX, mTextSize);
        mViewNext.setTextColor(mTextColorHint);
        mViewNext.setGravity(Gravity.CENTER);
        mViewNext.setLayoutParams(new TableRow.LayoutParams(0, TableRow.LayoutParams.MATCH_PARENT, 2));
        tableRow.addView(mViewNext);

        addView(tableRow);

        mViewPrev.setOnClickListener(this);
        mViewDate.setOnClickListener(this);
        mViewNext.setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {
        if (mSelectListener == null)
            return;

        int sign;

        if (view == mViewPrev)
            sign = SELECT_PREV;
        else if (view == mViewNext)
            sign = SELECT_NEXT;
        else
            sign = 0;

        Calendar date = ViewCalendar.getInstance();
        date.setTimeInMillis(mDate);

        if (mMode == MODE_YEAR) {
            date.add(Calendar.YEAR, sign);
        } else if (mMode == MODE_MONTH) {
            date.add(Calendar.MONTH, sign);
        } else if (mMode == MODE_WEEK) {
            date.add(Calendar.DAY_OF_MONTH, sign * 7);
        } else {
            date.add(Calendar.DAY_OF_MONTH, sign);
        }

        long offset = date.getTimeInMillis() - mDate;

        setDate(mDate + offset);
        mSelectListener.OnSelect(mThis, offset, date.getTimeInMillis());
    }

    interface OnSelectListener {
        void OnSelect(View view, long offset, long date);
    }

    public void setOnSelectListener(OnSelectListener listener) {
        mSelectListener = listener;
    }

    @Override
    public void setMode(int mode) {
        super.setMode(mode);
        updateText();
    }

    @Override
    public void setDate(long milli) {
        super.setDate(milli);
        updateText();
    }

    private String makeDateString(int mode, long ms) {
        Date date = new Date(ms);

        String format = "yyyy/MM/dd";

        if (mode == MODE_YEAR)
            format = "yyyy";
        else if (mode == MODE_MONTH)
            format = "MMMM yyyy";
        else if (mode == MODE_DAY || mode == MODE_WEEK)
            format = "MMM dd, yyyy";

        SimpleDateFormat simpleDateFormat = new SimpleDateFormat(format, Locale.ENGLISH);

        return simpleDateFormat.format(date);
    }

    public void updateText() {
        Calendar dateNow = ViewCalendar.getInstance();
        int yearNow = dateNow.get(Calendar.YEAR);
        int monthNow = dateNow.get(Calendar.MONTH);
        int dayNow = dateNow.get(Calendar.DAY_OF_MONTH);

        Calendar dateSet = ViewCalendar.getInstance();
        dateSet.setTimeInMillis(mDate);
        int yearSet = dateSet.get(Calendar.YEAR);
        int monthSet = dateSet.get(Calendar.MONTH);
        int daySet = dateSet.get(Calendar.DAY_OF_MONTH);

        if (yearNow == yearSet && monthNow == monthSet && dayNow == daySet)
            mViewDate.setTextColor(mTextColor);
        else
            mViewDate.setTextColor(mTextColorHint);

        mViewDate.setText(makeDateString(mMode, mDate));
    }
}
