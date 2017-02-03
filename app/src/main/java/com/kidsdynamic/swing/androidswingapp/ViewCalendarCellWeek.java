package com.kidsdynamic.swing.androidswingapp;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.RectF;
import android.graphics.Typeface;
import android.graphics.Xfermode;
import android.text.format.DateUtils;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;
import android.view.ViewParent;

import java.util.Calendar;

/**
 * Created by 03543 on 2017/2/1.
 */

public class ViewCalendarCellWeek extends ViewCalendarCell {

    public ViewCalendarCellWeek(Context context) {
        super(context);
        init(context, null);
    }

    public ViewCalendarCellWeek(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context, attrs);
    }

    public ViewCalendarCellWeek(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        init(context, attrs);
    }

    private void init(Context context, AttributeSet attrs) {
    }

    @Override
    public void setDate(long milli) {
        super.setDate(milli);

        ViewCalendar calendar = findCalendar();
        if (calendar != null) {
            if (ViewCalendar.isToday(mDate))
                setTextColor(Color.WHITE);
            else
                setTextColor(calendar.getTextColor());
        }

        Calendar date = ViewCalendar.getInstance();
        date.setTimeInMillis(mDate);

        int day = date.get(Calendar.DAY_OF_MONTH);
        setText(String.valueOf(day));
    }

    @Override
    protected void onDraw(Canvas canvas) {
        if (ViewCalendar.isToday(mDate))
            drawToday(canvas);

        super.onDraw(canvas);
    }

    private void drawToday(Canvas canvas) {
        int circleColor = getCurrentTextColor();
        ViewCalendar calendar = findCalendar();
        if (calendar != null)
            circleColor = calendar.getTodayColor();

        int width = getMeasuredWidth();
        int height = getMeasuredHeight();
        float size = getTextSize() * 1.64f; // golden ratio.

        size = Math.min(size, width);
        size = Math.min(size, height);

        RectF rect = new RectF((width - size) / 2, (height - size) / 2, (width + size) / 2, (height + size) / 2);

        Paint paint = new Paint();
        paint.setAntiAlias(true);
        paint.setColor(circleColor);
        paint.setStyle(Paint.Style.FILL);

        canvas.drawCircle(rect.centerX(), rect.centerY(), size / 2, paint);
    }
}
