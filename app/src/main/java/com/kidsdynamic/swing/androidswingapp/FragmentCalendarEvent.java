package com.kidsdynamic.swing.androidswingapp;

import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.util.ArrayList;

/**
 * Created by 03543 on 2017/2/5.
 */

public class FragmentCalendarEvent extends ViewFragment {
    private ActivityMain mActivityMain;
    private View mViewMain;

    private View mViewAlarmLine;
    private TextView mViewAlarm;

    private View mViewAssignLine;
    private TextView mViewAssignName;
    private ViewCircle mViewAssignPhoto;
    private View mViewAssignOption;
    private LinearLayout mViewAssignContainer;

    private View mViewStartLine;
    private View mViewEndLine;

    private View mViewColorLine;
    private ViewShape mViewColor;
    private View mViewColorOption;
    private LinearLayout mViewColorContainer;

    private View mViewRepeatLine;
    private View mViewDescriptionLine;
    private View mViewTodoLine;
    private View mViewSave;
    private View mViewAdvance;


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

        // Line Alarm
        mViewAlarmLine = mViewMain.findViewById(R.id.calendar_event_alarm_line);
        mViewAlarmLine.setOnClickListener(mAlarmListener);
        mViewAlarm = (TextView) mViewMain.findViewById(R.id.calendar_event_alarm);

        // Line Assign
        mViewAssignLine = mViewMain.findViewById(R.id.calendar_event_assign_line);
        mViewAssignLine.setOnClickListener(mAssignListener);
        mViewAssignName = (TextView) mViewMain.findViewById(R.id.calendar_event_assign_name);
        mViewAssignPhoto = (ViewCircle) mViewMain.findViewById(R.id.calendar_event_assign_photo);
        mViewAssignOption = mViewMain.findViewById(R.id.calendar_event_assign_option);
        mViewAssignContainer = (LinearLayout) mViewMain.findViewById(R.id.calendar_event_assign_container);

        // Line Start
        mViewStartLine = mViewMain.findViewById(R.id.calendar_event_start_line);
        mViewStartLine.setOnClickListener(mStartListener);

        // Line End
        mViewEndLine = mViewMain.findViewById(R.id.calendar_event_end_line);
        mViewEndLine.setOnClickListener(mEndListener);

        // Line Color
        mViewColorLine = mViewMain.findViewById(R.id.calendar_event_color_line);
        mViewColorLine.setOnClickListener(mColorListener);
        mViewColor = (ViewShape) mViewMain.findViewById(R.id.calendar_event_color);
        mViewColorOption = mViewMain.findViewById(R.id.calendar_event_color_option);
        mViewColorContainer = (LinearLayout) mViewMain.findViewById(R.id.calendar_event_color_container);

        int count = mViewColorContainer.getChildCount();
        for (int idx = 0; idx < count; idx++)
            mViewColorContainer.getChildAt(idx).setOnClickListener(mColorOptionListener);

        // Line Repeat
        mViewRepeatLine = mViewMain.findViewById(R.id.calendar_event_repeat_line);
        mViewRepeatLine.setOnClickListener(mRepeatListener);

        // Line Description
        mViewDescriptionLine = mViewMain.findViewById(R.id.calendar_event_description_line);
        mViewDescriptionLine.setOnClickListener(mDescriptionListener);

        // Line To-Do
        mViewTodoLine = mViewMain.findViewById(R.id.calendar_event_todo_line);
        mViewTodoLine.setOnClickListener(mTodoListener);

        // Line Button
        mViewSave = mViewMain.findViewById(R.id.calendar_event_save);
        mViewSave.setOnClickListener(mSaveListener);

        mViewAdvance = mViewMain.findViewById(R.id.calendar_event_advance);
        mViewAdvance.setOnClickListener(mAdvanceListener);

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

        loadWatchEvent();
    }

    @Override
    public void onToolbarAction1() {
        mActivityMain.popFragment();
    }

    private View.OnClickListener mAlarmListener = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            mActivityMain.mEventStack.push(mEvent);
            mActivityMain.selectFragment(FragmentCalendarAlarm.class.getName(), null);
        }
    };

    private View.OnClickListener mAssignListener = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            LinearLayout.LayoutParams params = (LinearLayout.LayoutParams) mViewAssignOption.getLayoutParams();
            if (params.height == 0)
                params.height = LinearLayout.LayoutParams.WRAP_CONTENT;
            else
                params.height = 0;

            mViewAssignOption.setLayoutParams(params);
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

    private View.OnClickListener mAssignOptionListener = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            WatchContact.Kid kid = (WatchContact.Kid) view.getTag();

            mViewAssignName.setText(kid.mLabel);
            mViewAssignPhoto.setBitmap(kid.mPhoto);

            mEvent.mKidId = kid.mId;

            int count = mViewAssignContainer.getChildCount();
            for (int idx = 0; idx < count; idx++) {
                ViewCircle viewCircle = (ViewCircle) mViewAssignContainer.getChildAt(idx);
                WatchContact.Kid contact = (WatchContact.Kid) viewCircle.getTag();

                viewCircle.setActive(contact.mId == kid.mId);
            }
        }
    };

    private View.OnClickListener mColorOptionListener = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            ViewShape shape = (ViewShape) view;
            mViewColor.setColor(shape.getColor());

            mEvent.mColor = "";
        }
    };

    private void loadAlarm() {
        String alarmName = "";
        for (FragmentCalendarAlarm.NoticeAlarm alarm : FragmentCalendarAlarm.NoticeAlarmList) {
            if (alarm.mId == mEvent.mAlert)
                alarmName = FragmentCalendarAlarm.findAlarmName(mEvent.mAlert);
        }

        if (alarmName.length() == 0) // Event is illegal, select first one
            mEvent.mAlert = FragmentCalendarAlarm.NoticeAlarmList[0].mId;

        if (mEvent.mAlert == 0)// simple name
            alarmName = "App Only";
        mViewAlarm.setText(alarmName);
    }

    private void loadAssign() {
        ArrayList<WatchContact.Kid> list = new ArrayList<>();
        WatchContact.Kid contact = null;

        list.addAll(mActivityMain.mOperator.getDeviceList());
        list.addAll(mActivityMain.mOperator.getSharedList());

        if (list.size() == 0)
            return;

        if (mEvent.mId == 0)    // todo: does id == 0 indicate it is a new event?
            mEvent.mId = mActivityMain.mOperator.getFocusKid() != null ?
                    mActivityMain.mOperator.getFocusKid().mId : list.get(0).mId;

        for (WatchContact.Kid kid : list)
            if (kid.mId == mEvent.mId)
                contact = kid;

        mViewAssignName.setText(contact.mLabel);
        mViewAssignPhoto.setBitmap(contact.mPhoto);

        int size = Math.round(TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 32, getResources().getDisplayMetrics()));
        int margin = Math.round(TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 5, getResources().getDisplayMetrics()));
        for (WatchContact.Kid kid : list) {
            ViewCircle viewCircle = new ViewCircle(mActivityMain);

            viewCircle.setTag(kid);
            viewCircle.setBitmap(kid.mPhoto);
            viewCircle.setStrokeWidth(mViewAssignPhoto.getStrokeWidth());
            viewCircle.setStrokeColorActive(ContextCompat.getColor(mActivityMain, R.color.color_orange_main));
            viewCircle.setStrokeColorNormal(ContextCompat.getColor(mActivityMain, R.color.color_white));
            viewCircle.setActive(kid.mId == contact.mId);
            viewCircle.setOnClickListener(mAssignOptionListener);

            LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(size, size);
            layoutParams.setMarginStart(margin);
            layoutParams.setMarginEnd(margin);
            viewCircle.setLayoutParams(layoutParams);

            mViewAssignContainer.addView(viewCircle);
        }
    }

    private void loadWatchEvent() {
        loadAlarm();
        loadAssign();
    }

    private void saveWatchEvent() {

    }
}
