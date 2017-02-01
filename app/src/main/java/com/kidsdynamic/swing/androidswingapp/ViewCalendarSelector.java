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

    private long mOffsetDate = 0;
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
        updateDateString();
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

        /*
        for (int row = 0; row < 5; row++) {
            TableRow tableRow = new TableRow(context);
            tableRow.setLayoutParams(new LayoutParams(TableRow.LayoutParams.MATCH_PARENT, 0, 1));

            for (int col = 0; col < 7; col++) {
                TextView tableCell = new TextView(context);
                tableCell.setText("(" + row + "," + col + ")");
                tableCell.setGravity(Gravity.CENTER);
                tableCell.setLayoutParams(new TableRow.LayoutParams(0, TableRow.LayoutParams.MATCH_PARENT, 1));

                tableRow.addView(tableCell);
            }

            addView(tableRow);
        }
        */
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

        Calendar date = Calendar.getInstance();
        date.setTimeInMillis(mDate + mOffsetDate);

        if (mMode == MODE_YEAR) {
            date.add(Calendar.YEAR, sign);
        } else if (mMode == MODE_MONTH) {
            date.add(Calendar.MONTH, sign);
        } else if (mMode == MODE_WEEK) {
            date.add(Calendar.DAY_OF_MONTH, sign * 7);
        } else {
            date.add(Calendar.DAY_OF_MONTH, sign);
        }

        mOffsetDate = date.getTimeInMillis() - mDate;
        updateDateString();
        mSelectListener.OnSelect(mThis, mOffsetDate, date.getTimeInMillis());
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
        mOffsetDate = 0;
        updateDateString();
    }

    @Override
    public void setDate(long milli) {
        super.setDate(milli);
        mOffsetDate = 0;
        updateDateString();
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

    public void updateDateString() {
        Calendar dateNow = Calendar.getInstance();
        int yearNow = dateNow.get(Calendar.YEAR);
        int weekNow = dateNow.get(Calendar.WEEK_OF_YEAR);

        Calendar dateSet = Calendar.getInstance();
        dateSet.setTimeInMillis(mDate + mOffsetDate);
        int yearSet = dateSet.get(Calendar.YEAR);
        int weekSet = dateSet.get(Calendar.WEEK_OF_YEAR);

        if (yearNow == yearSet && weekNow == weekSet)
            mViewDate.setTextColor(mTextColor);
        else
            mViewDate.setTextColor(mTextColorHint);

        mViewDate.setText(makeDateString(mMode, mDate + mOffsetDate));
    }
}
