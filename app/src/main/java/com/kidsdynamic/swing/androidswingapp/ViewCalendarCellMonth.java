package com.kidsdynamic.swing.androidswingapp;

import android.content.Context;
import android.graphics.Canvas;
import android.util.AttributeSet;

import java.util.Calendar;

/**
 * Created by 03543 on 2017/2/4.
 */

public class ViewCalendarCellMonth extends ViewCalendarCell {

    public ViewCalendarCellMonth(Context context) {
        super(context);
        init(context, null);
    }

    public ViewCalendarCellMonth(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context, attrs);
    }

    public ViewCalendarCellMonth(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        init(context, attrs);
    }

    private void init(Context context, AttributeSet attrs) {
    }

    @Override
    public void setDate(long date) {
        super.setDate(date);

        ViewCalendar calendar = getCalendar();

        if (calendar != null) {
            if (ViewCalendar.isToday(mDate))
                setTextColor(calendar.getTodayColor());
            else if (calendar.isInMonth(mDate))
                setTextColor(calendar.getTextColor());
            else
                setTextColor(calendar.getExceedColor());
        }

        Calendar calc = ViewCalendar.getInstance();
        calc.setTimeInMillis(mDate);

        int day = calc.get(Calendar.DAY_OF_MONTH);
        setText(String.valueOf(day));
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
    }
}
