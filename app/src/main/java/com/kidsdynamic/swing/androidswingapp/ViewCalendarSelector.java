package com.kidsdynamic.swing.androidswingapp;

import android.content.Context;
import android.graphics.Color;
import android.util.AttributeSet;
import android.util.Log;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.View;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;

import java.text.SimpleDateFormat;
import java.util.Locale;

/**
 * Created by 03543 on 2017/1/31.
 */

public class ViewCalendarSelector extends ViewCalendar implements View.OnClickListener{
    static final int SELECT_PREV = -1;
    static final int SELECT_NEXT = 1;
    static final int SELECT_DATE = 0;

    private ViewCalendar mThis = this;
    private TextView mViewDate;
    private TextView mViewPrev;
    private TextView mViewNext;

    private OnSelectListener mSelectListener = null;

    public ViewCalendarSelector(Context context) {
        super(context);
        init(context, null);
    }

    public ViewCalendarSelector(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context, attrs);
    }

    private void init(Context context, AttributeSet attrs) {
    }

    @Override
    public void fillCell(Context context) {
        TableRow tableRow = new TableRow(context);
        tableRow.setLayoutParams(new TableLayout.LayoutParams(TableRow.LayoutParams.MATCH_PARENT, 0, 1));

        mViewPrev = new TextView(context);
        mViewPrev.setText("◀"); // U+25C0 &#9664;
        mViewPrev.setTextSize(TypedValue.COMPLEX_UNIT_PX, mTextSize);
        mViewPrev.setTextColor(mTextColorHint);
        mViewPrev.setGravity(Gravity.CENTER);
        mViewPrev.setLayoutParams(new TableRow.LayoutParams(0, TableRow.LayoutParams.MATCH_PARENT, 2));
        tableRow.addView(mViewPrev);

        mViewDate = new TextView(context);
        mViewDate.setText(makeDateString(mMode));
        mViewDate.setTextSize(TypedValue.COMPLEX_UNIT_PX, mTextSize);
        mViewDate.setTextColor(mTextColor);
        mViewDate.setGravity(Gravity.CENTER);
        mViewDate.setLayoutParams(new TableRow.LayoutParams(0, TableRow.LayoutParams.MATCH_PARENT, 6));
        tableRow.addView(mViewDate);

        mViewNext = new TextView(context);
        mViewNext.setText("▶"); // U+25B6 &#9654;
        mViewNext.setTextSize(TypedValue.COMPLEX_UNIT_PX, mTextSize);
        mViewNext.setTextColor(mTextColorHint);
        mViewNext.setGravity(Gravity.CENTER);
        mViewNext.setLayoutParams(new TableRow.LayoutParams(0, TableRow.LayoutParams.MATCH_PARENT, 2));
        tableRow.addView(mViewNext);

        addView(tableRow);

        mViewPrev.setOnClickListener(this);
        mViewDate.setOnClickListener(this);
        mViewNext.setOnClickListener(this);

        /*
        for (int row = 0; row < 5; row++) {
            TableRow tableRow = new TableRow(context);
            tableRow.setLayoutParams(new LayoutParams(TableRow.LayoutParams.MATCH_PARENT, 0, 1));

            for (int col = 0; col < 7; col++) {
                TextView tableCell = new TextView(context);
                tableCell.setText("(" + row + "," + col + ")");
                tableCell.setGravity(Gravity.CENTER);
                tableCell.setLayoutParams(new TableRow.LayoutParams(0, TableRow.LayoutParams.MATCH_PARENT, 1));

                tableRow.addView(tableCell);
            }

            addView(tableRow);
        }
        */
    }

    @Override
    public void onClick(View view) {
        if (mSelectListener == null)
            return;

        if (view == mViewPrev)
            mSelectListener.OnSelect(mThis, SELECT_PREV);
        else if (view == mViewNext)
            mSelectListener.OnSelect(mThis, SELECT_NEXT);
        else if (view == mViewDate)
            mSelectListener.OnSelect(mThis, SELECT_DATE);
    }

    interface OnSelectListener {
        void OnSelect(View view, int option);
    }

    public void setOnSelectListener(OnSelectListener listener) {
        mSelectListener = listener;
    }

    @Override
    public void setMode(int mode) {
        mode = (mode == MODE_MONTH) ? MODE_MONTH : MODE_DAY;

        super.setMode(mode);
        mViewDate.setText(makeDateString(mMode));
    }

    public String makeDateString(int mode) {
        String format = "yyyy/MM/dd";

        if (mode == MODE_MONTH)
            format = "MMMM yyyy";
        else if (mode == MODE_DAY)
            format = "MMM dd, yyyy";

        SimpleDateFormat simpleDateFormat = new SimpleDateFormat(format, Locale.ENGLISH);

        return simpleDateFormat.format(mDate);
    }
}
