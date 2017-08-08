package com.kidsdynamic.swing.androidswingapp;

import android.content.DialogInterface;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import java.util.List;

/**
 * Created by 03543 on 2017/2/4.
 */

public class FragmentCalendarMonth extends ViewFragment {
    private ActivityMain mActivityMain;
    private View mViewMain;

    private ViewCalendarSelector mViewSelector;
    private ViewCalendarMonth mViewCalendar;
    private Button mSyncButton;

    private long mDefaultDate = System.currentTimeMillis();

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

        mSyncButton = (Button) mViewMain.findViewById(R.id.dashboard_month_sync);
        mSyncButton.setOnClickListener(mOnClickedListener);
//        showSyncDialog();
        return mViewMain;
    }

    @Override
    public ViewFragmentConfig getConfig() {
        return new ViewFragmentConfig(
                getResources().getString(R.string.title_calendar), true, true, false,
                R.mipmap.city_florida, R.mipmap.icon_left, R.mipmap.icon_add);
    }

    @Override
    public void onToolbarAction1() {
        Bundle bundle = new Bundle();
        bundle.putLong(BUNDLE_KEY_DATE, mViewCalendar.getDate());

        mActivityMain.selectFragment(FragmentCalendarMain.class.getName(), bundle);
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

        // 判定帶入的參數是否包含日期, 若有則作為當前日期的依據
        if (getArguments() != null)
            mDefaultDate = getArguments().getLong(BUNDLE_KEY_DATE);
        mViewSelector.setDate(mDefaultDate);
        mViewCalendar.setDate(mDefaultDate);

        loadEventList(mViewCalendar.getDateBegin(), mViewCalendar.getDateEnd());
    }

    private void loadEventList(long start, long end) {
        mViewCalendar.delAllEvent();

        List<WatchEvent> list = mActivityMain.mOperator.getEventList(start, end);

        for (WatchEvent event : list) {
/*
            Calendar cale = Calendar.getInstance();
            cale.setTimeInMillis(event.mStartDate);
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy/MM/dd HH:mm", Locale.US);
            String s = sdf.format(cale.getTimeInMillis());
            cale.setTimeInMillis(event.mEndDate);
            String e = sdf.format(cale.getTimeInMillis());
            Log.d("xxx", "Event:" + event.mName + "," + s + "--" + e );
*/
            mViewCalendar.addEvent(event);
        }
    }

    private ViewCalendarSelector.OnSelectListener mSelectorListener = new ViewCalendarSelector.OnSelectListener() {
        @Override
        public void OnSelect(View view, long offset, long date) {
            mViewCalendar.setDate(date);
            loadEventList(mViewCalendar.getDateBegin(), mViewCalendar.getDateEnd());
        }
    };

    private ViewCalendarMonth.OnSelectListener mCalendarListener = new ViewCalendarMonth.OnSelectListener() {
        @Override
        public void onSelect(ViewCalendarMonth calendar, ViewCalendarCellMonth cell) {
            Bundle bundle = new Bundle();
            bundle.putLong(BUNDLE_KEY_DATE, cell.getDate());

            mActivityMain.selectFragment(FragmentCalendarDaily.class.getName(), bundle);
        }
    };

    private Button.OnClickListener mOnClickedListener = new Button.OnClickListener() {
        @Override
        public void onClick(View v) {
//            mActivityMain.selectFragment(FragmentDashboardMain.class.getName(), null);
            mActivityMain.selectFragment(FragmentDashboardProgress.class.getName(), null);
        }
    };

}
