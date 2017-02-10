package com.kidsdynamic.swing.androidswingapp;

import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TimePicker;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;

/**
 * Created by 03543 on 2017/2/11.
 */

public class FragmentCalendarPicker extends ViewFragment {
    private ActivityMain mActivityMain;
    private View mViewMain;

    private ViewCalendarSelector mViewSelector;
    private ViewCalendarMonth mViewCalendar;
    private TimePicker mViewTime;

    private WatchEvent mEvent;
    private boolean mIsStartDate;
    private long mDate;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mActivityMain = (ActivityMain) getActivity();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        mViewMain = inflater.inflate(R.layout.fragment_calendar_picker, container, false);

        mViewSelector = (ViewCalendarSelector) mViewMain.findViewById(R.id.calendar_picker_selector);
        mViewSelector.setOnSelectListener(mSelectorListener);

        mViewCalendar = (ViewCalendarMonth) mViewMain.findViewById(R.id.calendar_picker_calendar);
        mViewCalendar.setOnSelectListener(mCalendarListener);

        mViewTime = (TimePicker) mViewMain.findViewById(R.id.calendar_picker_time);
        mViewTime.setOnTimeChangedListener(mTimeListener);

        return mViewMain;
    }

    @Override
    public void onResume() {
        super.onResume();

        mIsStartDate = getArguments().getBoolean(BUNDLE_KEY_START_DATE, false);

        mEvent = mActivityMain.mEventStack.pop();
        if (mEvent == null)
            mEvent = new WatchEvent();

        mDate = mIsStartDate ? mEvent.mStartDate : mEvent.mEndDate;

        mViewSelector.setDate(mDate);
        mViewCalendar.setDate(mDate);

        Calendar calc = Calendar.getInstance();
        calc.setTimeInMillis(mDate);
        if (Build.VERSION.SDK_INT >= 23) {
            mViewTime.setHour(calc.get(Calendar.HOUR_OF_DAY));
            mViewTime.setMinute(calc.get(Calendar.MINUTE));
        } else {
            mViewTime.setCurrentHour(calc.get(Calendar.HOUR_OF_DAY));
            mViewTime.setCurrentMinute(calc.get(Calendar.MINUTE));
        }
    }

    @Override
    public ViewFragmentConfig getConfig() {
        return new ViewFragmentConfig("Calendar", true, true, false,
                R.mipmap.city_florida, R.mipmap.icon_left, R.mipmap.icon_ok);
    }

    @Override
    public void onToolbarAction1() {
        mActivityMain.mEventStack.push(mEvent);
        mActivityMain.popFragment();
    }

    @Override
    public void onToolbarAction2() {
        if (mIsStartDate)
            mEvent.mStartDate = mDate;
        else
            mEvent.mEndDate = mDate;

        mActivityMain.mEventStack.push(mEvent);
        mActivityMain.popFragment();
    }

    private ViewCalendarSelector.OnSelectListener mSelectorListener = new ViewCalendarSelector.OnSelectListener() {
        @Override
        public void OnSelect(View view, long offset, long date) {
            mDate = date;
            mViewCalendar.setDate(date);
        }
    };

    private ViewCalendarMonth.OnSelectListener mCalendarListener = new ViewCalendarMonth.OnSelectListener() {
        @Override
        public void onSelect(ViewCalendarMonth calendar, ViewCalendarCellMonth cell) {
            mDate = cell.getDate();
            mViewSelector.setDate(cell.getDate());
        }
    };

    private TimePicker.OnTimeChangedListener mTimeListener = new TimePicker.OnTimeChangedListener() {
        @Override
        public void onTimeChanged(TimePicker view, int hourOfDay, int minute) {
            Calendar calc = Calendar.getInstance();

            calc.setTimeInMillis(mDate);
            calc.set(Calendar.HOUR_OF_DAY, hourOfDay);
            calc.set(Calendar.MINUTE, minute);

            mDate = calc.getTimeInMillis();
        }
    };
}
