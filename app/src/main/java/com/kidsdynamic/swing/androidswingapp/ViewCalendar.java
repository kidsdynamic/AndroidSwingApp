package com.kidsdynamic.swing.androidswingapp;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Typeface;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.view.Gravity;
import android.widget.TableLayout;
import android.widget.TableRow;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;

/**
 * Created by 03543 on 2017/1/31.
 */

public class ViewCalendar extends TableLayout {

    protected int mTextSize = 20;
    protected int mTextStyle = Typeface.BOLD;
    protected int mTextColor = 0x000000;
    protected int mTodayColor = 0xFFFFFF;
    protected int mExceedColor = 0x666666;

    protected long mDate = System.currentTimeMillis();

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
                } else if (attr == R.styleable.ViewCalendar_todayColor) {
                    mTodayColor = typedArray.getColor(attr, mTodayColor);
                } else if (attr == R.styleable.ViewCalendar_exceedColor) {
                    mExceedColor = typedArray.getColor(attr, mExceedColor);
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

    public void setTodayColor(int color) {
        mTodayColor = color;
    }

    public int getTodayColor() {
        return mTodayColor;
    }

    public void setExceedColor(int color) {
        mExceedColor = color;
    }

    public int getExceedColor() {
        return mExceedColor;
    }

    public void setDate(long milli) {
        Calendar date = getInstance();

        date.setTimeInMillis(milli);
        date.set(Calendar.HOUR_OF_DAY, 0);
        date.set(Calendar.MINUTE, 0);
        date.set(Calendar.SECOND, 0);
        date.set(Calendar.MILLISECOND, 0);

        mDate = date.getTimeInMillis();
    }

    public long getDate() {
        return mDate;
    }

    static Calendar getInstance() {
        return Calendar.getInstance();
//        return Calendar.getInstance(Locale.US);       // Force to use Sunday to be the first day of week
//        return Calendar.getInstance(Locale.GERMANY);  // Force to use Monday to be the first day of week
    }

    static boolean isToday(long msecond) {
        Calendar calcSet = getInstance();
        calcSet.setTimeInMillis(msecond);

        Calendar calcNow = getInstance();

        if (calcSet.get(Calendar.YEAR) != calcNow.get(Calendar.YEAR))
            return false;
        if (calcSet.get(Calendar.DAY_OF_YEAR) != calcNow.get(Calendar.DAY_OF_YEAR))
            return false;

        return true;
    }

    static boolean isMonth(long msecond) {
        Calendar calcSet = getInstance();
        calcSet.setTimeInMillis(msecond);

        Calendar calcNow = getInstance();

        if (calcSet.get(Calendar.YEAR) != calcNow.get(Calendar.YEAR))
            return false;
        if (calcSet.get(Calendar.MONTH) != calcNow.get(Calendar.MONTH))
            return false;

        return true;
    }
}
