package com.kidsdynamic.swing.androidswingapp;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.NumberPicker;

import java.util.Calendar;

/**
 * Created by 03543 on 2017/2/11.
 */

public class FragmentCalendarPicker extends ViewFragment {
    private ActivityMain mActivityMain;
    private View mViewMain;

    private ViewCalendarSelector mViewSelector;
    private ViewCalendarMonth mViewCalendar;
    private NumberPicker mViewHour;
    private NumberPicker mViewMinute;

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

        mViewHour = (NumberPicker) mViewMain.findViewById(R.id.calendar_picker_hour);
        mViewHour.setMaxValue(23);
        mViewHour.setMinValue(0);
        mViewHour.setOnValueChangedListener(mHourListener);

        mViewMinute = (NumberPicker) mViewMain.findViewById(R.id.calendar_picker_minute);
        mViewMinute.setMaxValue(59);
        mViewMinute.setMinValue(0);
        mViewMinute.setOnValueChangedListener(mMinuteListener);

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
        mViewHour.setValue(calc.get(Calendar.HOUR_OF_DAY));
        mViewMinute.setValue(calc.get(Calendar.MINUTE));
    }

    @Override
    public ViewFragmentConfig getConfig() {
        return new ViewFragmentConfig(
                getResources().getString(R.string.title_calendar), true, true, false,
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

        if ((mEvent.mEndDate - mEvent.mStartDate) < 1800000) // 30*60*1000 = 30M, Make event is at least 30 minutes in calendar
            mEvent.mEndDate = mEvent.mStartDate + 1800000;

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

    private NumberPicker.OnValueChangeListener mHourListener = new NumberPicker.OnValueChangeListener() {
        @Override
        public void onValueChange(NumberPicker picker, int oldVal, int newVal) {
            Calendar calc = Calendar.getInstance();

            calc.setTimeInMillis(mDate);
            calc.set(Calendar.HOUR_OF_DAY, newVal);
            calc.set(Calendar.MINUTE, mViewMinute.getValue());

            mDate = calc.getTimeInMillis();
        }
    };

    private NumberPicker.OnValueChangeListener mMinuteListener = new NumberPicker.OnValueChangeListener() {
        @Override
        public void onValueChange(NumberPicker picker, int oldVal, int newVal) {
            Calendar calc = Calendar.getInstance();

            calc.setTimeInMillis(mDate);
            calc.set(Calendar.HOUR_OF_DAY, mViewHour.getValue());
            calc.set(Calendar.MINUTE, newVal);

            mDate = calc.getTimeInMillis();
        }
    };
}
