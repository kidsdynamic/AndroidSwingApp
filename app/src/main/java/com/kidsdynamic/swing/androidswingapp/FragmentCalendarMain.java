package com.kidsdynamic.swing.androidswingapp;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

/**
 * Created by 03543 on 2016/12/19.
 */

public class FragmentCalendarMain extends ViewFragment {
    private ActivityMain mActivityMain;
    private View mViewMain;

    private ViewCalendarSelector mViewSelector;
    private ViewCalendarWeek mViewCalendar;
    private ViewCircle mViewAlert;
    private TextView mViewTime;
    private TextView mViewEvent;
    private Button mViewToday;
    private Button mViewMonthly;

    private long mDefaultDate = System.currentTimeMillis();

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mActivityMain = (ActivityMain) getActivity();

        if(getArguments()!= null) {
            mDefaultDate = getArguments().getLong(BUNDLE_KEY_DATE);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        mViewMain = inflater.inflate(R.layout.fragment_calendar_main, container, false);

        mViewSelector = (ViewCalendarSelector) mViewMain.findViewById(R.id.calendar_daily_selector);
        mViewSelector.setDate(mDefaultDate);
        mViewSelector.setOnSelectListener(mSelectorListener);

        mViewCalendar = (ViewCalendarWeek) mViewMain.findViewById(R.id.calendar_daily_calendar);
        mViewCalendar.setDate(mDefaultDate);
        mViewCalendar.setOnSelectListener(mCalendarListener);

        mViewAlert = (ViewCircle) mViewMain.findViewById(R.id.calendar_main_alert);

        mViewTime = (TextView) mViewMain.findViewById(R.id.calendar_main_time);
        mViewEvent = (TextView) mViewMain.findViewById(R.id.calendar_main_event);

        mViewToday = (Button) mViewMain.findViewById(R.id.calendar_main_today);
        mViewToday.setOnClickListener(mTodayListener);

        mViewMonthly = (Button) mViewMain.findViewById(R.id.calendar_main_monthly);
        mViewMonthly.setOnClickListener(mMonthlyListener);

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

        mActivityMain.selectFragment(FragmentCalendarMonth.class.getName(), bundle);
    }

    @Override
    public void onToolbarAction2() {
        mActivityMain.mEventStack.push(new WatchEvent(mViewCalendar.getDate()));
        mActivityMain.selectFragment(FragmentCalendarEvent.class.getName(), null);
    }

    @Override
    public void onResume() {
        super.onResume();

        long date = mViewCalendar.getDate();
        loadCalendar(date, date + 86400 - 1);
        updateAlert();
    }

    private ViewCalendarSelector.OnSelectListener mSelectorListener = new ViewCalendarSelector.OnSelectListener() {
        @Override
        public void OnSelect(View view, long offset, long date) {
            Bundle bundle = new Bundle();
            bundle.putLong(BUNDLE_KEY_DATE, date);

            mActivityMain.selectFragment(FragmentCalendarDaily.class.getName(), bundle);
        }
    };

    private ViewCalendarWeek.OnSelectListener mCalendarListener = new ViewCalendarWeek.OnSelectListener() {
        @Override
        public void onSelect(ViewCalendarWeek calendar, ViewCalendarCellWeek cell) {
            Bundle bundle = new Bundle();
            bundle.putLong(BUNDLE_KEY_DATE, cell.getDate());

            mActivityMain.selectFragment(FragmentCalendarDaily.class.getName(), bundle);
        }
    };

    private ViewCircle.OnProgressListener mAlertListener = new ViewCircle.OnProgressListener() {
        @Override
        public void onProgress(ViewCircle view, int begin, int end) {
        }
    };

    private View.OnClickListener mTodayListener = new View.OnClickListener() {
        @Override
        public void onClick(View view) {

        }
    };

    private View.OnClickListener mMonthlyListener = new View.OnClickListener() {
        @Override
        public void onClick(View view) {

        }
    };

    private void loadCalendar(long begin, long end) {
        // todo: load events between "begin" and "end". (include begin and end)
    }

    private void updateAlert() {
        // todo: load the first coming event
    }
}
