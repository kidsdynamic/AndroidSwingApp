package com.kidsdynamic.swing.androidswingapp;

import android.content.Context;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.View;
import android.widget.TableLayout;
import android.widget.TableRow;

import java.util.Calendar;

/**
 * Created by 03543 on 2017/2/4.
 */

public class ViewCalendarMonth extends ViewCalendar implements View.OnClickListener {

    private ViewCalendarCellWeekName[] mViewNameList;
    private ViewCalendarCellMonth[] mViewCellList;

    private OnSelectListener mSelectListener = null;
    private ViewCalendarMonth mThis = this;

    public ViewCalendarMonth(Context context) {
        super(context);
        init(context, null);
    }

    public ViewCalendarMonth(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context, attrs);
    }

    private void init(Context context, AttributeSet attrs) {
        updateNameList(mViewNameList);
        updateCellList(mViewCellList);
    }

    @Override
    public void fillCell(Context context) {
        TableRow tableRow;

        // Week Name
        tableRow = new TableRow(context);
        tableRow.setLayoutParams(new TableLayout.LayoutParams(TableRow.LayoutParams.MATCH_PARENT, 0, 1));

        mViewNameList = new ViewCalendarCellWeekName[7];
        for (int idx = 0; idx < mViewNameList.length; idx++) {
            mViewNameList[idx] = new ViewCalendarCellWeekName(context);

            mViewNameList[idx].setTextSize(TypedValue.COMPLEX_UNIT_PX, mTextSize);
            mViewNameList[idx].setTypeface(mViewNameList[idx].getTypeface(), mTextStyle);
            mViewNameList[idx].setTextColor(mTextColor);
            mViewNameList[idx].setGravity(Gravity.CENTER);
            mViewNameList[idx].setLayoutParams(new TableRow.LayoutParams(0, TableRow.LayoutParams.MATCH_PARENT, 2));

            tableRow.addView(mViewNameList[idx]);
        }
        addView(tableRow);

        // Cells
        mViewCellList = new ViewCalendarCellMonth[42];  // 6 weeks
        int cidx = 0;
        for (int widx = 0; widx < 6; widx++) {
            tableRow = new TableRow(context);
            tableRow.setLayoutParams(new TableLayout.LayoutParams(TableRow.LayoutParams.MATCH_PARENT, 0, 1));

            for (int idx = 0; idx < 7; idx++) {
                mViewCellList[cidx] = new ViewCalendarCellMonth(context);
                mViewCellList[cidx].setCalendar(this);
                mViewCellList[cidx].setTextSize(TypedValue.COMPLEX_UNIT_PX, mTextSize);
                mViewCellList[cidx].setTypeface(mViewCellList[idx].getTypeface(), mTextStyle);
                mViewCellList[cidx].setTextColor(mTextColor);
                mViewCellList[cidx].setGravity(Gravity.CENTER);
                mViewCellList[cidx].setLayoutParams(new TableRow.LayoutParams(0, TableRow.LayoutParams.MATCH_PARENT, 2));

                tableRow.addView(mViewCellList[cidx++]);
            }
            addView(tableRow);
        }
    }

    @Override
    public void setDate(long milli) {
        super.setDate(milli);
        updateCellList(mViewCellList);
    }

    public void updateNameList(ViewCalendarCellWeekName[] list) {
        Calendar cal = ViewCalendar.getInstance();
        cal.setTimeInMillis(mDate);

        cal.set(Calendar.HOUR_OF_DAY, 0); //clear would not reset the hour of day
        cal.clear(Calendar.MINUTE);
        cal.clear(Calendar.SECOND);
        cal.clear(Calendar.MILLISECOND);

//        cal.set(Calendar.DAY_OF_WEEK, cal.getFirstDayOfWeek());
        while(cal.get(Calendar.DAY_OF_WEEK) != cal.getFirstDayOfWeek())
            cal.add(Calendar.DAY_OF_MONTH, -1);

        for (int idx = 0; idx < list.length; idx++) {
            list[idx].setDate(cal.getTimeInMillis());
            cal.add(Calendar.DAY_OF_MONTH, 1);
        }
    }

    public void updateCellList(ViewCalendarCellMonth[] list) {
        Calendar cal = ViewCalendar.getInstance();
        cal.setTimeInMillis(mDate);

        cal.set(Calendar.HOUR_OF_DAY, 0); //clear would not reset the hour of day
        cal.clear(Calendar.MINUTE);
        cal.clear(Calendar.SECOND);
        cal.clear(Calendar.MILLISECOND);

        cal.set(Calendar.DAY_OF_MONTH, 1);
        while(cal.get(Calendar.DAY_OF_WEEK) != cal.getFirstDayOfWeek())
            cal.add(Calendar.DAY_OF_MONTH, -1);

        for (int idx = 0; idx < list.length; idx++) {
            list[idx].setDate(cal.getTimeInMillis());
            cal.add(Calendar.DAY_OF_MONTH, 1);
        }
    }

    @Override
    public void onClick(View view) {
        if (mSelectListener == null)
            return;

        mSelectListener.onSelect(mThis, (ViewCalendarCellMonth) view);
    }

    public void setOnSelectListener(ViewCalendarMonth.OnSelectListener listener) {
        mSelectListener = listener;

        for (ViewCalendarCellMonth cell : mViewCellList)
            if (cell.getText().length() != 0)
                cell.setOnClickListener(this);
    }

    interface OnSelectListener {
        void onSelect(ViewCalendarMonth calendar, ViewCalendarCellMonth cell);
    }

}
