package com.kidsdynamic.swing.androidswingapp;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.RectF;
import android.util.AttributeSet;

import java.util.Calendar;

/**
 * Created by 03543 on 2017/2/4.
 */

public class ViewCalendarCellMonth extends ViewCalendarCell {

    private WatchEvent mEventList;

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

    public void addEvent(WatchEvent event) {

    }

    @Override
    public void setDate(long date) {
        super.setDate(date);

        ViewCalendar calendar = getViewCalendar();

        if (calendar != null) {
            if (calendar.isSameDay(mDate))
                setTextColor(calendar.getFocusColor());
            else if (ViewCalendar.isToday(mDate))
                setTextColor(calendar.getTodayColor());
            else if (calendar.isSameMonth(mDate))
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
        ViewCalendar calendar = getViewCalendar();
        if (calendar != null && calendar.isSameDay(mDate))
            drawFocus(canvas, calendar.getFocusBackgroundColor());

        super.onDraw(canvas);
    }

    private void drawFocus(Canvas canvas, int color) {
        int width = getMeasuredWidth();
        int height = getMeasuredHeight();
        float size = getTextSize() * 1.64f; // golden ratio.

        size = Math.min(size, width);
        size = Math.min(size, height);

        RectF rect = new RectF((width - size) / 2, (height - size) / 2, (width + size) / 2, (height + size) / 2);

        Paint paint = new Paint();
        paint.setAntiAlias(true);
        paint.setColor(color);
        paint.setStyle(Paint.Style.FILL);

        canvas.drawCircle(rect.centerX(), rect.centerY(), size / 2, paint);
    }
}
