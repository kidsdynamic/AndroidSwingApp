package com.kidsdynamic.swing.androidswingapp;

import android.content.Context;
import android.util.AttributeSet;

import java.util.Calendar;

/**
 * Created by 03543 on 2017/2/1.
 */

public class ViewCalendarCellWeekName extends ViewCalendarCell {

    public ViewCalendarCellWeekName(Context context) {
        super(context);
        init(context, null);
    }

    public ViewCalendarCellWeekName(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context, attrs);
    }

    public ViewCalendarCellWeekName(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        init(context, attrs);
    }

    private void init(Context context, AttributeSet attrs) {
    }

    static final String dayName[] = new String[]{"?", "S", "M", "T", "W", "T", "F", "S"};

    @Override
    public void setDate(long milli) {
        super.setDate(milli);

        Calendar date = ViewCalendar.getInstance();
        date.setTimeInMillis(mDate);

        int weekDay = date.get(Calendar.DAY_OF_WEEK);
        setText(dayName[weekDay]);
    }
}
