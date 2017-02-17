package com.kidsdynamic.swing.androidswingapp;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;

/**
 * Created by 03543 on 2017/2/10.
 */

public class FragmentCalendarTodo extends ViewFragment {
    private ActivityMain mActivityMain;
    private View mViewMain;

    private ViewCalendarSelector mViewSelector;
    private ViewCalendarWeek mViewCalendar;
    private LinearLayout mViewContainer;

    private long mDefaultDate = System.currentTimeMillis();
    private WatchEvent mEvent;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mActivityMain = (ActivityMain) getActivity();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        mViewMain = inflater.inflate(R.layout.fragment_calendar_todo, container, false);

        mViewSelector = (ViewCalendarSelector) mViewMain.findViewById(R.id.calendar_todo_selector);
        mViewSelector.setOnSelectListener(mSelectorListener);

        mViewCalendar = (ViewCalendarWeek) mViewMain.findViewById(R.id.calendar_todo_calendar);
        mViewCalendar.setOnSelectListener(mCalendarListener);

        mViewContainer = (LinearLayout) mViewMain.findViewById(R.id.calendar_todo_container);

        return mViewMain;
    }

    @Override
    public ViewFragmentConfig getConfig() {
        return new ViewFragmentConfig("Calendar", true, true, false,
                ActivityMain.RESOURCE_IGNORE, R.mipmap.icon_left, R.mipmap.icon_edit);
    }

    @Override
    public void onToolbarAction1() {
        mActivityMain.popFragment();
    }

    @Override
    public void onToolbarAction2() {
        mActivityMain.mEventStack.push(mEvent);
        mActivityMain.selectFragment(FragmentCalendarEvent.class.getName(), null);
    }

    @Override
    public void onResume() {
        super.onResume();

        if (getArguments() != null)
            mDefaultDate = getArguments().getLong(BUNDLE_KEY_DATE);
        mViewSelector.setDate(mDefaultDate);
        mViewCalendar.setDate(mDefaultDate);

        if (mActivityMain.mEventStack.isEmpty())
            mEvent = new WatchEvent();
        else
            mEvent = mActivityMain.mEventStack.pop();

        mViewContainer.setBackgroundColor(WatchEvent.stringToColor(mEvent.mColor));

        for (WatchTodo todo : mEvent.mTodoList)
            addTodo(todo);
    }

    private void addTodo(WatchTodo todo) {

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
}
