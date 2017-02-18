package com.kidsdynamic.swing.androidswingapp;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.text.SimpleDateFormat;
import java.util.List;
import java.util.Locale;

/**
 * Created by 03543 on 2017/2/10.
 */

public class FragmentCalendarDaily extends ViewFragment {
    private ActivityMain mActivityMain;
    private View mViewMain;

    private ViewCalendarSelector mViewSelector;
    private ViewCalendarWeek mViewCalendar;
    private ViewCalendarDaily mViewSchedule;

    private long mDefaultDate = System.currentTimeMillis();

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mActivityMain = (ActivityMain) getActivity();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        mViewMain = inflater.inflate(R.layout.fragment_calendar_daily, container, false);

        mViewSelector = (ViewCalendarSelector) mViewMain.findViewById(R.id.calendar_daily_selector);
        mViewSelector.setOnSelectListener(mSelectorListener);

        mViewCalendar = (ViewCalendarWeek) mViewMain.findViewById(R.id.calendar_daily_calendar);
        mViewCalendar.setOnSelectListener(mCalendarListener);

        mViewSchedule = (ViewCalendarDaily) mViewMain.findViewById(R.id.calendar_daily_schedule);
        mViewSchedule.setOnSelectListener(mScheduleListener);

        return mViewMain;
    }

    @Override
    public ViewFragmentConfig getConfig() {
        return new ViewFragmentConfig("Calendar", true, true, false,
                ActivityMain.RESOURCE_IGNORE, R.mipmap.icon_left, R.mipmap.icon_add);
    }

    @Override
    public void onToolbarAction1() {
        mActivityMain.popFragment();
    }

    @Override
    public void onToolbarAction2() {
        WatchEvent event = new WatchEvent(mViewCalendar.getDate());
        event.mUserId = mActivityMain.mOperator.getUser().mId;

        mActivityMain.mEventStack.push(event);
        mActivityMain.selectFragment(FragmentCalendarEvent.class.getName(), null);
    }

    @Override
    public void onResume() {
        super.onResume();

        if (getArguments() != null)
            mDefaultDate = getArguments().getLong(BUNDLE_KEY_DATE);
        mViewSelector.setDate(mDefaultDate);
        mViewCalendar.setDate(mDefaultDate);
        mViewSchedule.setDate(mDefaultDate);

        loadEventList(mDefaultDate);
    }

    private void loadEventList(long date) {
        mViewSchedule.delAllEvent();

        long start = ViewCalendar.stripTime(date);
        long end = start + 86400000 - 1;
        List<WatchEvent> list = mActivityMain.mOperator.getEventList(start, end);

        for (WatchEvent event : list)
            mViewSchedule.addEvent(event);
    }

    private ViewCalendarSelector.OnSelectListener mSelectorListener = new ViewCalendarSelector.OnSelectListener() {
        @Override
        public void OnSelect(View view, long offset, long date) {
            mViewCalendar.setDate(date);
            mViewSchedule.setDate(date);
            loadEventList(date);
        }
    };

    private ViewCalendarWeek.OnSelectListener mCalendarListener = new ViewCalendarWeek.OnSelectListener() {
        @Override
        public void onSelect(ViewCalendarWeek calendar, ViewCalendarCellWeek cell) {
            mViewSelector.setDate(cell.getDate());
            mViewSchedule.setDate(cell.getDate());
            loadEventList(cell.getDate());
        }
    };

    private ViewCalendarDaily.OnSelectListener mScheduleListener = new ViewCalendarDaily.OnSelectListener() {
        @Override
        public void OnSelect(View view, WatchEvent event) {
            mActivityMain.mEventStack.push(event);
            if (event.mTodoList.size() > 0)
                mActivityMain.selectFragment(FragmentCalendarTodo.class.getName(), null);
            else
                mActivityMain.selectFragment(FragmentCalendarEvent.class.getName(), null);
        }
    };
}
