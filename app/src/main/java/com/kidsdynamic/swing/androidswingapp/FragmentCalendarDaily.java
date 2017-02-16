package com.kidsdynamic.swing.androidswingapp;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.ArrayList;
import java.util.Calendar;
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

    private List<WatchEvent> mEventList;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mActivityMain = (ActivityMain) getActivity();

        if (getArguments() != null) {
            mDefaultDate = getArguments().getLong(BUNDLE_KEY_DATE);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        mViewMain = inflater.inflate(R.layout.fragment_calendar_daily, container, false);

        mViewSelector = (ViewCalendarSelector) mViewMain.findViewById(R.id.calendar_daily_selector);
        mViewSelector.setDate(mDefaultDate);
        mViewSelector.setOnSelectListener(mSelectorListener);

        mViewCalendar = (ViewCalendarWeek) mViewMain.findViewById(R.id.calendar_daily_calendar);
        mViewCalendar.setDate(mDefaultDate);
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
    public void onResume() {
        super.onResume();

        long date = mViewCalendar.getDate();
        mEventList = mActivityMain.mOperator.getEventList(date, date + 86400 - 1);

        // Test
        WatchContact.User me = mActivityMain.mOperator.getUser();
        mEventList.add(makeFakeEvent(1, me.mId, new ArrayList<Integer>(), 7, 15, 12, 15));
        mEventList.add(makeFakeEvent(2, me.mId, new ArrayList<Integer>(), 8, 0, 8, 30));
        mEventList.add(makeFakeEvent(3, me.mId, new ArrayList<Integer>(), 15, 0, 15, 30));
        //////////////

        for (WatchEvent event : mEventList)
            mViewSchedule.addEvent(event);
    }

    private WatchEvent makeFakeEvent(int eventId, int userId, List<Integer> kids, int startHour, int startMin, int endHour, int endMin) {
        // for debug only!
        WatchEvent event = new WatchEvent();

        event.mId = eventId;
        event.mUserId = userId;
        event.mKids = kids;
        event.mColor = WatchEvent.colorToString(WatchEvent.StockColorList[2].mColor);
        event.mName = String.format(Locale.getDefault(), "Name(%d)", eventId);
        event.mDescription = String.format(Locale.getDefault(), "Desc(%d) This is a Dog", eventId);
        event.mRepeat = WatchEvent.REPEAT_NEVER;

        Calendar calc = Calendar.getInstance();
        calc.setTimeInMillis(event.mStartDate);

        calc.set(Calendar.HOUR_OF_DAY, startHour);
        calc.set(Calendar.MINUTE, startMin);
        event.mStartDate = calc.getTimeInMillis();

        calc.set(Calendar.HOUR_OF_DAY, endHour);
        calc.set(Calendar.MINUTE, endMin);
        event.mEndDate = calc.getTimeInMillis();

        return event;
    }

    private ViewCalendarSelector.OnSelectListener mSelectorListener = new ViewCalendarSelector.OnSelectListener() {
        @Override
        public void OnSelect(View view, long offset, long date) {
            mViewCalendar.setDate(date);
        }
    };

    private ViewCalendarWeek.OnSelectListener mCalendarListener = new ViewCalendarWeek.OnSelectListener() {
        @Override
        public void onSelect(ViewCalendarWeek calendar, ViewCalendarCellWeek cell) {
            mViewSelector.setDate(cell.getDate());
        }
    };

    private ViewCalendarDaily.OnSelectListener mScheduleListener = new ViewCalendarDaily.OnSelectListener() {
        @Override
        public void OnSelect(View view, WatchEvent event) {
            mActivityMain.mEventStack.push(event);
            mActivityMain.selectFragment(FragmentCalendarEvent.class.getName(), null);
        }
    };
}
