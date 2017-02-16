package com.kidsdynamic.swing.androidswingapp;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;

/**
 * Created by 03543 on 2017/2/4.
 */

public class FragmentCalendarMonth extends ViewFragment {
    private ActivityMain mActivityMain;
    private View mViewMain;

    private ViewCalendarSelector mViewSelector;
    private ViewCalendarMonth mViewCalendar;

    private long mDefaultDate = System.currentTimeMillis();
    private List<WatchEvent> mEventList;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mActivityMain = (ActivityMain) getActivity();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        mViewMain = inflater.inflate(R.layout.fragment_calendar_month, container, false);

        mViewSelector = (ViewCalendarSelector) mViewMain.findViewById(R.id.calendar_month_selector);
        mViewSelector.setOnSelectListener(mSelectorListener);

        mViewCalendar = (ViewCalendarMonth) mViewMain.findViewById(R.id.calendar_month_calendar);
        mViewCalendar.setOnSelectListener(mCalendarListener);

        return mViewMain;
    }

    @Override
    public ViewFragmentConfig getConfig() {
        return new ViewFragmentConfig("Calendar", true, true, false,
                R.mipmap.city_florida, R.mipmap.icon_calendar, R.mipmap.icon_add);
    }

    @Override
    public void onToolbarAction1() {
        Bundle bundle = new Bundle();
        bundle.putLong(BUNDLE_KEY_DATE, mViewCalendar.getDate());

        mActivityMain.selectFragment(FragmentCalendarMain.class.getName(), bundle);
    }

    @Override
    public void onToolbarAction2() {
        Bundle bundle = new Bundle();
        bundle.putLong(BUNDLE_KEY_DATE, mViewCalendar.getDate());

        mActivityMain.selectFragment(FragmentCalendarEvent.class.getName(), bundle);
    }

    @Override
    public void onResume() {
        super.onResume();

        if (getArguments() != null)
            mDefaultDate = getArguments().getLong(BUNDLE_KEY_DATE);
        mViewSelector.setDate(mDefaultDate);
        mViewCalendar.setDate(mDefaultDate);

        mEventList = mActivityMain.mOperator.getEventList(mViewCalendar.getDateBegin(), mViewCalendar.getDateEnd());

        // Test
/*
        WatchContact.User me = mActivityMain.mOperator.getUser();
        mEventList.add(makeFakeEvent(1, me.mId, new ArrayList<Integer>(), 7, 15, 8, 15));
        mEventList.add(makeFakeEvent(2, me.mId, new ArrayList<Integer>(), 8, 0, 8, 30));
        mEventList.add(makeFakeEvent(3, me.mId, new ArrayList<Integer>(), 15, 0, 15, 30));
*/
        //////////////

        for (WatchEvent event : mEventList) {
            mViewCalendar.addEvent(event);
        }
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

    private ViewCalendarMonth.OnSelectListener mCalendarListener = new ViewCalendarMonth.OnSelectListener() {
        @Override
        public void onSelect(ViewCalendarMonth calendar, ViewCalendarCellMonth cell) {
            mViewSelector.setDate(cell.getDate());
        }
    };
}
