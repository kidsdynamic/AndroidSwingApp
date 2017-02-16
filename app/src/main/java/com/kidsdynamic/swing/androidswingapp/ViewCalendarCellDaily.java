package com.kidsdynamic.swing.androidswingapp;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.Typeface;
import android.support.v4.content.ContextCompat;
import android.text.Layout;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.util.TypedValue;

import java.util.Calendar;

/**
 * Created by 03543 on 2017/2/8.
 */

public class ViewCalendarCellDaily extends ViewCalendarCell {
    private WatchEvent mEvent;
    private Rect mRect;

    public ViewCalendarCellDaily(Context context) {
        super(context);
        init(context, null);
    }

    public ViewCalendarCellDaily(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context, attrs);
    }

    public ViewCalendarCellDaily(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        init(context, attrs);
    }

    private void init(Context context, AttributeSet attrs) {
        mRect = new Rect();

        int padding = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP,
                5, getResources().getDisplayMetrics());

        setPadding(padding, 0, padding, 0);
        setSingleLine();
        setEllipsize(TextUtils.TruncateAt.END);
    }

    public void setEvent(WatchEvent event) {
        mEvent = event;
        setText(event.mDescription);
    }

    public WatchEvent getEvent() {
        return mEvent;
    }

    @Override
    public void setViewCalendar(ViewCalendar calendar) {
        mViewCalendar = calendar;

        setTextSize(TypedValue.COMPLEX_UNIT_PX, mViewCalendar.getTextSize() + 1);
        setTypeface(getTypeface(), Typeface.BOLD);
        setTextColor(Color.WHITE);
    }

    @Override
    public void onDraw(Canvas canvas) {
//        boolean isEllipsize = !((getLayout().getText().toString()).equals(mEvent.mDescription));

        getPaint().getTextBounds(getText().toString(), 0, getText().length(), mRect);
        if (mRect.height() < getMeasuredHeight())
            super.onDraw(canvas);
    }

}
