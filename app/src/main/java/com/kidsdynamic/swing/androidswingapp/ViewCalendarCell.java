package com.kidsdynamic.swing.androidswingapp;

import android.content.Context;
import android.util.AttributeSet;
import android.view.ViewParent;
import android.widget.TextView;

/**
 * Created by 03543 on 2017/2/1.
 */

public class ViewCalendarCell extends TextView {
    protected long mDate;
    protected ViewCalendar mCalendar = null;

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
//        Typeface face=Typeface.createFromAsset(context.getAssets(), "Helvetica_Neue.ttf");
//        setTypeface(face);
    }

    public void setDate(long mSecond) {
        mDate = mSecond;
    }

    public long getDate() {
        return mDate;
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

    public void setCalendar(ViewCalendar calendar) {
        mCalendar = calendar;
    }

    public ViewCalendar getCalendar() {
        return mCalendar;
    }
}
