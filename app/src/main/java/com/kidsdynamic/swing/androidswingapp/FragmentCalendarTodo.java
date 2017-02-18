package com.kidsdynamic.swing.androidswingapp;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;

/**
 * Created by 03543 on 2017/2/10.
 */

public class FragmentCalendarTodo extends ViewFragment {
    private ActivityMain mActivityMain;
    private View mViewMain;

    private ViewCalendarSelector mViewSelector;
    private ViewCalendarWeek mViewCalendar;
    private TextView mViewTitle;
    private TextView mViewDescription;
    private View mViewContainerLine;
    private LinearLayout mViewContainer;
    private View mViewButtonLine;
    private View mViewSave;
    private View mViewDelete;

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

        mViewTitle = (TextView) mViewMain.findViewById(R.id.calendar_todo_title);
        mViewDescription = (TextView) mViewMain.findViewById(R.id.calendar_todo_description);

        mViewContainerLine = mViewMain.findViewById(R.id.calendar_todo_container_line);
        mViewContainer = (LinearLayout) mViewMain.findViewById(R.id.calendar_todo_container);

        mViewButtonLine = mViewMain.findViewById(R.id.calendar_todo_button_line);
        mViewSave = mViewMain.findViewById(R.id.calendar_todo_save);
        mViewSave.setOnClickListener(mSaveListener);
        mViewDelete = mViewMain.findViewById(R.id.calendar_todo_delete);
        mViewDelete.setOnClickListener(mDeleteListener);

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

        mViewTitle.setBackgroundColor(WatchEvent.stringToColor(mEvent.mColor));
        mViewDescription.setBackgroundColor(WatchEvent.stringToColor(mEvent.mColor));
        mViewContainerLine.setBackgroundColor(WatchEvent.stringToColor(mEvent.mColor));
        mViewButtonLine.setBackgroundColor(WatchEvent.stringToColor(mEvent.mColor));

        for (WatchTodo todo : mEvent.mTodoList)
            addTodo(todo);
    }

    private void addTodo(WatchTodo todo) {
        ViewTodo viewTodo = new ViewTodo(mActivityMain);
        viewTodo.setTag(todo);
        viewTodo.load(todo);
        viewTodo.setCheckMode();
        viewTodo.setOnEditListener(null);

        int height = getResources().getDisplayMetrics().heightPixels / 15;
        LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, height);

        mViewContainer.addView(viewTodo, layoutParams);
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

    private View.OnClickListener mSaveListener = new View.OnClickListener() {
        @Override
        public void onClick(View view) {

        }
    };

    private View.OnClickListener mDeleteListener = new View.OnClickListener() {
        @Override
        public void onClick(View view) {

        }
    };
}
