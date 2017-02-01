package com.kidsdynamic.swing.androidswingapp;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

/**
 * Created by 03543 on 2016/12/19.
 */

public class FragmentCalendarMain extends ViewFragment {
    private ActivityMain mActivityMain;
    private View mViewMain;

    private ViewCalendarSelector mViewSelector;
    private ViewCalendarWeek mViewCalendar;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mActivityMain = (ActivityMain) getActivity();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        mViewMain = inflater.inflate(R.layout.fragment_calendar_main, container, false);

        mViewSelector = (ViewCalendarSelector) mViewMain.findViewById(R.id.calendar_main_selector);
        mViewSelector.setDate(System.currentTimeMillis());
        mViewSelector.setOnSelectListener(mSelectorListener);

        mViewCalendar = (ViewCalendarWeek) mViewMain.findViewById(R.id.calendar_main_calendar);
        mViewCalendar.setDate(System.currentTimeMillis());
        mViewCalendar.setOnSelectListener(mCalendarListener);

        return mViewMain;
    }

    @Override
    public ViewFragmentConfig getConfig() {
        return new ViewFragmentConfig("Calendar", true, true, false,
                R.mipmap.city_florida, R.mipmap.icon_pen, R.mipmap.icon_add);
    }

    private ViewCalendarSelector.OnSelectListener mSelectorListener = new ViewCalendarSelector.OnSelectListener() {
        @Override
        public void OnSelect(View view, long offset, long date) {
            SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy/MM/dd hh:mm:ss", Locale.getDefault());
            Log.d("xxx", "select offs:" + offset + " date: " + simpleDateFormat.format(date));

            mViewCalendar.setDate(date);
        }
    };

    private ViewCalendarWeek.OnSelectListener mCalendarListener = new ViewCalendarWeek.OnSelectListener() {
        @Override
        public void onSelect(ViewCalendarWeek week, ViewCalendarCellWeek cell) {
            SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy/MM/dd hh:mm:ss", Locale.getDefault());
            Log.d("xxx", "date: " + simpleDateFormat.format(cell.getDate()));
        }
    };
}
