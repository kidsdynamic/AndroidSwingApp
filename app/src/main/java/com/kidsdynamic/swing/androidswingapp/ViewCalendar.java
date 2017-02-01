package com.kidsdynamic.swing.androidswingapp;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Typeface;
import android.util.AttributeSet;
import android.util.Log;
import android.util.TypedValue;
import android.view.Gravity;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;

/**
 * Created by 03543 on 2017/1/31.
 */

public class ViewCalendar extends TableLayout {

    static final int MODE_INVALID = -1;
    static final int MODE_YEAR = 0;
    static final int MODE_MONTH = 1;
    static final int MODE_WEEK = 2;
    static final int MODE_DAY = 3;

    protected int mTextSize = 12;
    protected int mTextStyle = Typeface.NORMAL;
    protected int mTextColor = 0;
    protected int mTextColorHint = 0;

    protected int mMode = MODE_INVALID;
    protected long mDate = System.currentTimeMillis();
    protected int mWeekStart = Calendar.SUNDAY;

    public ViewCalendar(Context context) {
        super(context);
        init(context, null);
        fillCell(context);
    }

    public ViewCalendar(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context, attrs);
        fillCell(context);
    }

    private void init(Context context, AttributeSet attrs) {
        if (attrs != null) {
            TypedArray typedArray = context.obtainStyledAttributes(
                    attrs, R.styleable.ViewCalendar);

            final int count = typedArray.getIndexCount();
            for (int idx = 0; idx < count; idx++) {
                final int attr = typedArray.getIndex(idx);

                if (attr == R.styleable.ViewCalendar_android_textSize) {
                    mTextSize = typedArray.getDimensionPixelOffset(attr, mTextSize);
                } else if (attr == R.styleable.ViewCalendar_android_textStyle) {
                    mTextStyle = typedArray.getInt(attr, mTextStyle);
                } else if (attr == R.styleable.ViewCalendar_android_textColor) {
                    mTextColor = typedArray.getColor(attr, mTextColor);
                } else if (attr == R.styleable.ViewCalendar_android_textColorHint) {
                    mTextColorHint = typedArray.getColor(attr, mTextColorHint);
                } else if (attr == R.styleable.ViewCalendar_calendarMode) {
                    mMode = typedArray.getInt(attr, mMode);
                }
            }

            typedArray.recycle();
        }
    }

    public void fillCell(Context context) {
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy/MM/dd hh:mm:ss", Locale.getDefault());

        TableRow tableRow = new TableRow(context);
        tableRow.setLayoutParams(new TableLayout.LayoutParams(TableRow.LayoutParams.MATCH_PARENT, 0, 1));

        ViewCalendarCell tableCell = new ViewCalendarCell(context);
        tableCell.setText(simpleDateFormat.format(mDate));
        tableCell.setTextSize(TypedValue.COMPLEX_UNIT_PX, mTextSize);
        tableCell.setTypeface(tableCell.getTypeface(), mTextStyle);
        tableCell.setTextColor(mTextColor);
        tableCell.setGravity(Gravity.CENTER);
        tableCell.setLayoutParams(new TableRow.LayoutParams(0, TableRow.LayoutParams.MATCH_PARENT, 1));

        tableRow.addView(tableCell);

        addView(tableRow);
    }

    public void setTextSize(int pixel) {
        mTextSize = pixel;
    }

    public int getTextSize() {
        return mTextSize;
    }

    public void setTextColor(int color) {
        mTextColor = color;
    }

    public int getTextColor() {
        return mTextColor;
    }

    public void setTextColorHint(int color) {
        mTextColorHint = color;
    }

    public int getTextColorHint() {
        return mTextColorHint;
    }

    public void setMode(int mode) {
        mMode = mode;
    }

    public int getMode() {
        return mMode;
    }

    public void setDate(long milli) {
        Calendar date = Calendar.getInstance();

        date.setTimeInMillis(milli);
        date.set(Calendar.HOUR, 0);
        date.set(Calendar.MINUTE, 0);
        date.set(Calendar.SECOND, 0);

        mDate = date.getTimeInMillis();
    }

    public long getDate() {
        return mDate;
    }

    public void setWeekStart(int weekStart) {
        if (weekStart < Calendar.SUNDAY || weekStart > Calendar.SATURDAY)
            weekStart = Calendar.SUNDAY;
        mWeekStart = weekStart;
    }
}
