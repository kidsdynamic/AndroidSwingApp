package com.kidsdynamic.swing.androidswingapp;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import java.util.Calendar;
import java.util.List;
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
    private TextView mViewAlertTime;
    private TextView mViewAlertEvent;
    private Button mViewToday;
    private Button mViewMonthly;

    private long mDefaultDate = System.currentTimeMillis();

    private List<WatchEvent> mEventList;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mActivityMain = (ActivityMain) getActivity();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        mViewMain = inflater.inflate(R.layout.fragment_calendar_main, container, false);

        mViewSelector = (ViewCalendarSelector) mViewMain.findViewById(R.id.calendar_main_selector);
        mViewSelector.setOnSelectListener(mSelectorListener);

        mViewCalendar = (ViewCalendarWeek) mViewMain.findViewById(R.id.calendar_main_calendar);
        mViewCalendar.setOnSelectListener(mCalendarListener);

        mViewAlert = (ViewCircle) mViewMain.findViewById(R.id.calendar_main_alert);
        mViewAlertTime = (TextView) mViewMain.findViewById(R.id.calendar_main_alert_time);
        mViewAlertEvent = (TextView) mViewMain.findViewById(R.id.calendar_main_alert_event);

        mViewToday = (Button) mViewMain.findViewById(R.id.calendar_main_today);
        mViewToday.setOnClickListener(mTodayListener);

        mViewMonthly = (Button) mViewMain.findViewById(R.id.calendar_main_monthly);
        mViewMonthly.setOnClickListener(mMonthlyListener);

        return mViewMain;
    }

    @Override
    public ViewFragmentConfig getConfig() {
        return new ViewFragmentConfig(
                getResources().getString(R.string.title_calendar), true, true, false,
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

        long date = ViewCalendar.stripTime(mDefaultDate);
        mEventList = mActivityMain.mOperator.getEventList(date, date + 86400 - 1);

        updateAlert();
    }

    private void updateAlert() {
        Calendar cale = Calendar.getInstance();

        WatchEvent event = WatchEvent.earliestInDay(cale.getTimeInMillis(), mEventList);

        setAlertMessage(event);
        setAlertClock(event);

        if (event != null) {
            mViewAlert.setTag(event);
            mViewAlert.setOnClickListener(mAlertListener);
        } else {
            mViewAlert.setOnClickListener(null);
        }
    }

    private void setAlertMessage(WatchEvent event) {
        String timeString;
        String messageString;

        if (event == null) {
            timeString = "";
            messageString = getResources().getString(R.string.calendar_main_no_incoming_event);

        } else {
            Calendar cale = Calendar.getInstance();
            cale.setTimeInMillis(event.mStartDate);
            timeString = String.format(Locale.getDefault(), "%02d:%02d", cale.get(Calendar.HOUR_OF_DAY), cale.get(Calendar.MINUTE));
            messageString = event.mDescription;
        }

        mViewAlertTime.setText(timeString);
        mViewAlertEvent.setText(messageString);
    }

    private void setAlertClock(WatchEvent event) {

        if (event == null) {
            mViewAlert.setActive(false);
        } else if ((event.mEndDate - event.mStartDate) >= 42480000) { // 11 Hours and 48 minute. if diff is bigger then it, active whole clock
            mViewAlert.setActive(true);
        } else {
            Calendar startCale = Calendar.getInstance();
            startCale.setTimeInMillis(event.mStartDate);

            int startHour = startCale.get(Calendar.HOUR_OF_DAY);
            int startMin = startCale.get(Calendar.MINUTE);

            startHour = startHour >= 12 ? startHour - 12 : startHour;
            int startActive = (int) (startHour * 5 + Math.floor(startMin / 12));

            Calendar endCale = Calendar.getInstance();
            endCale.setTimeInMillis(event.mEndDate);

            int endHour = endCale.get(Calendar.HOUR_OF_DAY);
            int endMin = endCale.get(Calendar.MINUTE);

            endHour = endHour >= 12 ? endHour - 12 : endHour;
            int endActive = (int) (endHour * 5 + Math.floor(endMin / 12));

            mViewAlert.setStrokeBeginEnd(startActive, endActive);
        }
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
            Bundle bundle = new Bundle();
            bundle.putLong(BUNDLE_KEY_DATE, cell.getDate());

            mActivityMain.selectFragment(FragmentCalendarDaily.class.getName(), bundle);
        }
    };

    private View.OnClickListener mAlertListener = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            WatchEvent event = (WatchEvent) view.getTag();
            if (event == null)
                return;

            mActivityMain.mEventStack.push(event);
            mActivityMain.selectFragment(FragmentCalendarEvent.class.getName(), null);
        }
    };

    private View.OnClickListener mTodayListener = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            Calendar cale = Calendar.getInstance();
            long date = ViewCalendar.stripTime(cale.getTimeInMillis());

            Bundle bundle = new Bundle();
            bundle.putLong(BUNDLE_KEY_DATE, date);

            mActivityMain.selectFragment(FragmentCalendarDaily.class.getName(), bundle);
        }
    };

    private View.OnClickListener mMonthlyListener = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            Calendar cale = Calendar.getInstance();
            long date = ViewCalendar.stripTime(cale.getTimeInMillis());

            Bundle bundle = new Bundle();
            bundle.putLong(BUNDLE_KEY_DATE, date);

            mActivityMain.selectFragment(FragmentCalendarMonth.class.getName(), bundle);
        }
    };
}
