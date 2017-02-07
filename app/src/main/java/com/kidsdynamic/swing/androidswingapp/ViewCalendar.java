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
    protected int mFocusColor = 0xFFFFFF;
    protected int mFocusBackgroundColor = 0xA00000;
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
                } else if (attr == R.styleable.ViewCalendar_focusColor) {
                    mFocusColor = typedArray.getColor(attr, mFocusColor);
                } else if (attr == R.styleable.ViewCalendar_focusBackgroundColor) {
                    mFocusBackgroundColor = typedArray.getColor(attr, mFocusBackgroundColor);
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

    public void setFocusColor(int color) {
        mFocusColor = color;
    }

    public int getFocusColor() {
        return mFocusColor;
    }

    public void setFocusBackgroundColor(int color) {
        mFocusBackgroundColor = color;
    }

    public int getFocusBackgroundColor() {
        return mFocusBackgroundColor;
    }

    public void setExceedColor(int color) {
        mExceedColor = color;
    }

    public int getExceedColor() {
        return mExceedColor;
    }

    public void setDate(long date) {
        Calendar calc = getInstance();

        calc.setTimeInMillis(date);
        calc.clear(Calendar.HOUR_OF_DAY);
        calc.clear(Calendar.MINUTE);
        calc.clear(Calendar.SECOND);
        calc.clear(Calendar.MILLISECOND);

        mDate = calc.getTimeInMillis();
    }

    public long getDate() {
        return mDate;
    }

    public boolean isInDay(long date) {
        Calendar calcThis = getInstance();
        calcThis.setTimeInMillis(mDate);

        Calendar calcDate = getInstance();
        calcDate.setTimeInMillis(date);

        if (calcThis.get(Calendar.YEAR) != calcDate.get(Calendar.YEAR))
            return false;
        if (calcThis.get(Calendar.DAY_OF_YEAR) != calcDate.get(Calendar.DAY_OF_YEAR))
            return false;

        return true;
    }

    public boolean isInWeek(long date) {
        Calendar calcThis = getInstance();
        calcThis.setTimeInMillis(mDate);

        Calendar calcDate = getInstance();
        calcDate.setTimeInMillis(date);

        if (calcThis.get(Calendar.YEAR) != calcDate.get(Calendar.YEAR))
            return false;
        if (calcThis.get(Calendar.WEEK_OF_YEAR) != calcDate.get(Calendar.WEEK_OF_YEAR))
            return false;

        return true;
    }

    public boolean isInMonth(long date) {
        Calendar calcThis = getInstance();
        calcThis.setTimeInMillis(mDate);

        Calendar calcDate = getInstance();
        calcDate.setTimeInMillis(date);

        if (calcThis.get(Calendar.YEAR) != calcDate.get(Calendar.YEAR))
            return false;
        if (calcThis.get(Calendar.MONTH) != calcDate.get(Calendar.MONTH))
            return false;

        return true;
    }

    public boolean isInYear(long date) {
        Calendar calcThis = getInstance();
        calcThis.setTimeInMillis(mDate);

        Calendar calcDate = getInstance();
        calcDate.setTimeInMillis(date);

        if (calcThis.get(Calendar.YEAR) != calcDate.get(Calendar.YEAR))
            return false;

        return true;
    }

    static Calendar getInstance() {
        return Calendar.getInstance();
//        return Calendar.getInstance(Locale.US);       // Force to use Sunday to be the first day of week
//        return Calendar.getInstance(Locale.GERMANY);  // Force to use Monday to be the first day of week
    }

    static boolean isToday(long date) {
        Calendar calcSet = getInstance();
        calcSet.setTimeInMillis(date);

        Calendar calcNow = getInstance();

        if (calcSet.get(Calendar.YEAR) != calcNow.get(Calendar.YEAR))
            return false;
        if (calcSet.get(Calendar.DAY_OF_YEAR) != calcNow.get(Calendar.DAY_OF_YEAR))
            return false;

        return true;
    }

    static boolean isMonth(long date) {
        Calendar calcSet = getInstance();
        calcSet.setTimeInMillis(date);

        Calendar calcNow = getInstance();

        if (calcSet.get(Calendar.YEAR) != calcNow.get(Calendar.YEAR))
            return false;
        if (calcSet.get(Calendar.MONTH) != calcNow.get(Calendar.MONTH))
            return false;

        return true;
    }
}
