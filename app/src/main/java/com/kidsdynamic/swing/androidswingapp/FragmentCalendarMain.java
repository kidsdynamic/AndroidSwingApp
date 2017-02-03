package com.kidsdynamic.swing.androidswingapp;

import android.animation.ValueAnimator;
import android.os.Bundle;
import android.text.format.DateUtils;
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
    private ViewCircle mViewAlert;

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

        mViewAlert = (ViewCircle) mViewMain.findViewById(R.id.calendar_main_alert);

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
            mViewCalendar.setDate(date);

            mViewAlert.setOnProgressListener(mAlertListener);
            mViewAlert.startProgress(200, mViewAlert.getStrokeCount(), mViewAlert.getStrokeCount());
        }
    };

    private ViewCircle.OnProgressListener mAlertListener = new ViewCircle.OnProgressListener() {
        @Override
        public void onProgress(ViewCircle view, int begin, int end) {
            Log.d("xxx", "AlertListener:" + begin + "," + end);
        }
    };

    private ViewCalendarWeek.OnSelectListener mCalendarListener = new ViewCalendarWeek.OnSelectListener() {
        @Override
        public void onSelect(ViewCalendarWeek week, ViewCalendarCellWeek cell) {
            mViewSelector.setDate(cell.getDate());
        }
    };
}
