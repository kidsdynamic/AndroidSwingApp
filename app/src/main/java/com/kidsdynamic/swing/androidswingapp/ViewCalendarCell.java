package com.kidsdynamic.swing.androidswingapp;

import android.content.Context;
import android.util.AttributeSet;
import android.view.ViewParent;
import android.widget.TextView;

import java.util.Calendar;

/**
 * Created by 03543 on 2017/2/1.
 */

// ViewCalendar物件中, 各日期(欄位)的物件.
public class ViewCalendarCell extends TextView {
    protected long mDate;
    protected ViewCalendar mViewCalendar = null;

    public ViewCalendarCell(Context context) {
        super(context);
        init(context, null);
    }

    public ViewCalendarCell(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context, attrs);
    }

    public ViewCalendarCell(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        init(context, attrs);
    }

    private void init(Context context, AttributeSet attrs) {
    }

    public void setDate(long mSecond) {
        mDate = mSecond;
    }

    public long getDate() {
        return mDate;
    }

    public boolean isSameDay(long date) {
        Calendar calcThis = ViewCalendar.getInstance();
        calcThis.setTimeInMillis(mDate);

        Calendar calcDate = ViewCalendar.getInstance();
        calcDate.setTimeInMillis(date);

        if (calcThis.get(Calendar.YEAR) != calcDate.get(Calendar.YEAR))
            return false;
        if (calcThis.get(Calendar.DAY_OF_YEAR) != calcDate.get(Calendar.DAY_OF_YEAR))
            return false;

        return true;
    }

    public ViewCalendar findCalendar() {
        ViewParent parent = getParent();

        while(parent != null) {
            if(parent instanceof ViewCalendar)
                return (ViewCalendar)parent;

            parent = parent.getParent();
        }

        return null;
    }

    public void setViewCalendar(ViewCalendar calendar) {
        mViewCalendar = calendar;
    }

    public ViewCalendar getViewCalendar() {
        return mViewCalendar;
    }
}
