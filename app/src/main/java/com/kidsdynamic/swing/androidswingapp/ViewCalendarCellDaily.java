package com.kidsdynamic.swing.androidswingapp;

import android.content.Context;
import android.util.AttributeSet;

import java.util.Calendar;

/**
 * Created by 03543 on 2017/2/8.
 */

public class ViewCalendarCellDaily extends ViewCalendarCell {
    private WatchEvent mEvent;

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
    }

    public void setEvent(WatchEvent event) {
        mEvent = event;
    }

    public WatchEvent getEvent() {
        return mEvent;
    }
}
