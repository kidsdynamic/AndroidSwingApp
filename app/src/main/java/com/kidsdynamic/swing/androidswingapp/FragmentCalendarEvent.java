package com.kidsdynamic.swing.androidswingapp;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

/**
 * Created by 03543 on 2017/2/5.
 */

public class FragmentCalendarEvent extends ViewFragment {
    private ActivityMain mActivityMain;
    private View mViewMain;

    private View mViewEventLine;
    private View mViewAssignLine;
    private View mViewStartLine;
    private View mViewEndLine;
    private View mViewColorLine;
    private View mViewRepeatLine;
    private View mViewDescriptionLine;
    private View mViewTodoLine;
    private View mViewSave;
    private View mViewAdvance;

    private View mViewColorOption;
    private ViewShape mViewColorYellow;
    private ViewShape mViewColorBlue;
    private ViewShape mViewColorGreen;
    private ViewShape mViewColorPink;
    private ViewShape mViewcolorOrange;
    private ViewShape mViewColorGray;

    private TextView mViewAlarmLabel;
    private ViewShape mViewColorLabel;

    private long mDefaultDate = System.currentTimeMillis();
    private WatchEvent mEvent;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mActivityMain = (ActivityMain) getActivity();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        mViewMain = inflater.inflate(R.layout.fragment_calendar_event, container, false);

        mViewEventLine = mViewMain.findViewById(R.id.calendar_event_alarm_line);
        mViewEventLine.setOnClickListener(mEventListener);

        mViewAssignLine = mViewMain.findViewById(R.id.calendar_event_assign_line);
        mViewAssignLine.setOnClickListener(mAssignListener);

        mViewStartLine = mViewMain.findViewById(R.id.calendar_event_start_line);
        mViewStartLine.setOnClickListener(mStartListener);

        mViewEndLine = mViewMain.findViewById(R.id.calendar_event_end_line);
        mViewEndLine.setOnClickListener(mEndListener);

        mViewColorLine = mViewMain.findViewById(R.id.calendar_event_color_line);
        mViewColorLine.setOnClickListener(mColorListener);

        mViewRepeatLine = mViewMain.findViewById(R.id.calendar_event_repeat_line);
        mViewRepeatLine.setOnClickListener(mRepeatListener);

        mViewDescriptionLine = mViewMain.findViewById(R.id.calendar_event_description_line);
        mViewDescriptionLine.setOnClickListener(mDescriptionListener);

        mViewTodoLine = mViewMain.findViewById(R.id.calendar_event_todo_line);
        mViewTodoLine.setOnClickListener(mTodoListener);

        mViewSave = mViewMain.findViewById(R.id.calendar_event_save);
        mViewSave.setOnClickListener(mSaveListener);

        mViewAdvance = mViewMain.findViewById(R.id.calendar_event_advance);
        mViewAdvance.setOnClickListener(mAdvanceListener);

        mViewColorOption = mViewMain.findViewById(R.id.calendar_event_color_options);

        mViewColorYellow = (ViewShape) mViewMain.findViewById(R.id.calendar_event_color_yellow);
        mViewColorYellow.setOnClickListener(mColorOptiontListener);

        mViewColorBlue = (ViewShape) mViewMain.findViewById(R.id.calendar_event_color_blue);
        mViewColorBlue.setOnClickListener(mColorOptiontListener);

        mViewColorGreen = (ViewShape) mViewMain.findViewById(R.id.calendar_event_color_green);
        mViewColorGreen.setOnClickListener(mColorOptiontListener);

        mViewColorPink = (ViewShape) mViewMain.findViewById(R.id.calendar_event_color_pink);
        mViewColorPink.setOnClickListener(mColorOptiontListener);

        mViewcolorOrange = (ViewShape) mViewMain.findViewById(R.id.calendar_event_color_orange);
        mViewcolorOrange.setOnClickListener(mColorOptiontListener);

        mViewColorGray = (ViewShape) mViewMain.findViewById(R.id.calendar_event_color_gray);
        mViewColorGray.setOnClickListener(mColorOptiontListener);

        mViewAlarmLabel = (TextView) mViewMain.findViewById(R.id.calendar_event_alarm);
        mViewColorLabel = (ViewShape) mViewMain.findViewById(R.id.calendar_event_color_label);

        return mViewMain;
    }

    @Override
    public ViewFragmentConfig getConfig() {
        return new ViewFragmentConfig("Calendar", true, true, false,
                ActivityMain.RESOURCE_IGNORE, R.mipmap.icon_calendar, ActivityMain.RESOURCE_HIDE);
    }

    @Override
    public void onResume() {
        super.onResume();

        if (getArguments() != null)
            mDefaultDate = getArguments().getLong(BUNDLE_KEY_DATE);

        if (!mActivityMain.mEventStack.isEmpty())
            mEvent = mActivityMain.mEventStack.pop();
        else
            mEvent = new WatchEvent(mDefaultDate);

        eventLoad();
    }

    @Override
    public void onToolbarAction1() {
        mActivityMain.popFragment();
    }

    private View.OnClickListener mEventListener = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            mActivityMain.mEventStack.push(mEvent);
            mActivityMain.selectFragment(FragmentCalendarAlarm.class.getName(), null);
        }
    };

    private View.OnClickListener mAssignListener = new View.OnClickListener() {
        @Override
        public void onClick(View view) {

        }
    };

    private View.OnClickListener mStartListener = new View.OnClickListener() {
        @Override
        public void onClick(View view) {

        }
    };

    private View.OnClickListener mEndListener = new View.OnClickListener() {
        @Override
        public void onClick(View view) {

        }
    };

    private View.OnClickListener mColorListener = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            LinearLayout.LayoutParams params = (LinearLayout.LayoutParams) mViewColorOption.getLayoutParams();
            if (params.height == 0)
                params.height = LinearLayout.LayoutParams.WRAP_CONTENT;
            else
                params.height = 0;

            mViewColorOption.setLayoutParams(params);
        }
    };

    private View.OnClickListener mRepeatListener = new View.OnClickListener() {
        @Override
        public void onClick(View view) {

        }
    };

    private View.OnClickListener mDescriptionListener = new View.OnClickListener() {
        @Override
        public void onClick(View view) {

        }
    };

    private View.OnClickListener mTodoListener = new View.OnClickListener() {
        @Override
        public void onClick(View view) {

        }
    };

    private View.OnClickListener mSaveListener = new View.OnClickListener() {
        @Override
        public void onClick(View view) {

        }
    };

    private View.OnClickListener mAdvanceListener = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            LinearLayout.LayoutParams params = (LinearLayout.LayoutParams) mViewSave.getLayoutParams();
            params.width = LinearLayout.LayoutParams.MATCH_PARENT;
            mViewSave.setLayoutParams(params);

            mViewRepeatLine.setVisibility(View.VISIBLE);
            mViewDescriptionLine.setVisibility(View.VISIBLE);
            mViewTodoLine.setVisibility(View.VISIBLE);
        }
    };

    private View.OnClickListener mColorOptiontListener = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            ViewShape shape = (ViewShape) view;
            mViewColorLabel.setColor(shape.getColor());
        }
    };

    private void eventLoad() {
        String alarmName = "";
        for (FragmentCalendarAlarm.NoticeAlarm alarm : FragmentCalendarAlarm.NoticeAlarmList) {
            if (alarm.mId == mEvent.mAlert)
                alarmName = FragmentCalendarAlarm.findAlarmName(mEvent.mAlert);
        }

        if (alarmName.length() == 0 || mEvent.mAlert == 0)
            alarmName = "App Only";
        mViewAlarmLabel.setText(alarmName);
    }

    private void eventSave() {

    }
}
