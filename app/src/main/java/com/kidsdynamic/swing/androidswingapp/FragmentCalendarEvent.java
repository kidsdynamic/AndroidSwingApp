package com.kidsdynamic.swing.androidswingapp;

import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Locale;

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
    private TextView mViewStart;

    private View mViewEndLine;
    private TextView mViewEnd;

    private View mViewColorLine;
    private ViewShape mViewColor;
    private View mViewColorOption;
    private LinearLayout mViewColorContainer;

    private View mViewRepeatLine;
    private TextView mViewRepeat;
    private View mViewRepeatOption;
    private LinearLayout mViewRepeatContainer;

    private View mViewDescriptionLine;
    private EditText mViewDescription;

    private View mViewTodoLine;
    private LinearLayout mViewTodoOption;
    private LinearLayout mViewTodoContainer;

    private View mViewSave;
    private View mViewAdvance;

    private WatchEvent mEvent;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mActivityMain = (ActivityMain) getActivity();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        int count;

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
        mViewStart = (TextView) mViewMain.findViewById(R.id.calendar_event_start);

        // Line End
        mViewEndLine = mViewMain.findViewById(R.id.calendar_event_end_line);
        mViewEndLine.setOnClickListener(mEndListener);
        mViewEnd = (TextView) mViewMain.findViewById(R.id.calendar_event_end);

        // Line Color
        mViewColorLine = mViewMain.findViewById(R.id.calendar_event_color_line);
        mViewColorLine.setOnClickListener(mColorListener);
        mViewColor = (ViewShape) mViewMain.findViewById(R.id.calendar_event_color);
        mViewColorOption = mViewMain.findViewById(R.id.calendar_event_color_option);
        mViewColorContainer = (LinearLayout) mViewMain.findViewById(R.id.calendar_event_color_container);

        count = mViewColorContainer.getChildCount();
        for (int idx = 0; idx < count; idx++)
            mViewColorContainer.getChildAt(idx).setOnClickListener(mColorOptionListener);

        // Line Repeat
        mViewRepeatLine = mViewMain.findViewById(R.id.calendar_event_repeat_line);
        mViewRepeatLine.setOnClickListener(mRepeatListener);
        mViewRepeat = (TextView) mViewMain.findViewById(R.id.calendar_event_repeat);
        mViewRepeatOption = mViewMain.findViewById(R.id.calendar_event_repeat_option);
        mViewRepeatContainer = (LinearLayout) mViewMain.findViewById(R.id.calendar_event_repeat_container);

        count = mViewRepeatContainer.getChildCount();
        for (int idx = 0; idx < count; idx++)
            mViewRepeatContainer.getChildAt(idx).setOnClickListener(mRepeatOptionListener);

        // Line Description
        mViewDescriptionLine = mViewMain.findViewById(R.id.calendar_event_description_line);
        mViewDescription = (EditText) mViewMain.findViewById(R.id.calendar_event_description);
        mViewDescription.addTextChangedListener(mDescriptionWatcher);

        // Line To-Do
        mViewTodoLine = mViewMain.findViewById(R.id.calendar_event_todo_line);
        mViewTodoLine.setOnClickListener(mTodoListener);
        mViewTodoOption = (LinearLayout) mViewMain.findViewById(R.id.calendar_event_todo_option);
        mViewTodoContainer = (LinearLayout) mViewMain.findViewById(R.id.calendar_event_todo_container);

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

        mEvent = mActivityMain.mEventStack.pop();
        if (mEvent == null)
            mEvent = new WatchEvent(System.currentTimeMillis());

        loadWatchEvent();
    }

    @Override
    public void onToolbarAction1() {
        mActivityMain.popFragment();
    }

    private void viewAdvance() {
        mViewSave.getLayoutParams().width = LinearLayout.LayoutParams.MATCH_PARENT;
        mViewSave.requestLayout();

        mViewRepeatLine.setVisibility(View.VISIBLE);
        mViewDescriptionLine.setVisibility(View.VISIBLE);
        mViewTodoLine.setVisibility(View.VISIBLE);

        if (mEvent.mTodoList.size() != 0) {
            viewTodoOption();
        }
    }

    private void viewTodoOption() {
        mViewTodoOption.getLayoutParams().height = LinearLayout.LayoutParams.WRAP_CONTENT;
        mViewTodoOption.requestLayout();
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
            Bundle bundle = new Bundle();
            bundle.putBoolean(BUNDLE_KEY_START_DATE, true);

            mActivityMain.mEventStack.push(mEvent);
            mActivityMain.selectFragment(FragmentCalendarPicker.class.getName(), bundle);
        }
    };

    private View.OnClickListener mEndListener = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            Bundle bundle = new Bundle();
            bundle.putBoolean(BUNDLE_KEY_START_DATE, false);

            mActivityMain.mEventStack.push(mEvent);
            mActivityMain.selectFragment(FragmentCalendarPicker.class.getName(), bundle);
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
            LinearLayout.LayoutParams params = (LinearLayout.LayoutParams) mViewRepeatOption.getLayoutParams();
            if (params.height == 0)
                params.height = LinearLayout.LayoutParams.WRAP_CONTENT;
            else
                params.height = 0;

            mViewRepeatOption.setLayoutParams(params);
        }
    };

    private View.OnClickListener mTodoListener = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            LinearLayout.LayoutParams layoutParams;

            viewTodoOption();

            if (mViewTodoContainer.getChildCount() > 0) {
                View separator = new View(mActivityMain);
                separator.setBackgroundColor(ContextCompat.getColor(mActivityMain, R.color.color_gray_light));

                int sph = Math.round(TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 1, getResources().getDisplayMetrics()));
                layoutParams = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, sph);

                int margin = Math.round(TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 5, getResources().getDisplayMetrics()));
                layoutParams.setMargins(margin, margin, margin, margin);

                mViewTodoContainer.addView(separator, layoutParams);
            }

            ViewTodo viewTodo = new ViewTodo(mActivityMain);

            int height = getResources().getDisplayMetrics().heightPixels / 15;
            layoutParams = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, height);
            mViewTodoContainer.addView(viewTodo, layoutParams);
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
            viewAdvance();
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

            mEvent.mColor = WatchEvent.colorToString(mViewColor.getColor());

            mEvent.mColor = "";
        }
    };

    private View.OnClickListener mRepeatOptionListener = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            TextView viewRepeat = (TextView) view;
            String repeat = viewRepeat.getText().toString();

            mViewRepeat.setText(repeat);

            repeat = repeat.toUpperCase();
            if (repeat.contains(WatchEvent.REPEAT_DAILY))
                mEvent.mRepeat = WatchEvent.REPEAT_DAILY;
            else if (repeat.contains(WatchEvent.REPEAT_WEEKLY))
                mEvent.mRepeat = WatchEvent.REPEAT_WEEKLY;
            else if (repeat.contains(WatchEvent.REPEAT_MONTHLY))
                mEvent.mRepeat = WatchEvent.REPEAT_MONTHLY;
            else
                mEvent.mRepeat = "";
        }
    };

    private TextWatcher mDescriptionWatcher = new TextWatcher() {
        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {

        }

        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {
            mEvent.mDescription = s.toString();
        }

        @Override
        public void afterTextChanged(Editable s) {

        }
    };

    private void loadAlarm() {
        String alarmName = "";
        for (WatchEvent.NoticeAlarm alarm : WatchEvent.NoticeAlarmList) {
            if (alarm.mId == mEvent.mAlert)
                alarmName = WatchEvent.findAlarmName(mEvent.mAlert);
        }

        if (alarmName.length() == 0) // Event is illegal
            mEvent.mAlert = 0;

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

        if (mEvent.mKidId == 0)    // todo: does id == 0 indicate it is a new event?
            mEvent.mKidId = mActivityMain.mOperator.getFocusKid() != null ?
                    mActivityMain.mOperator.getFocusKid().mId : list.get(0).mId;

        for (WatchContact.Kid kid : list)
            if (kid.mId == mEvent.mKidId)
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

    private void loadDate() {
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy/MM/dd HH:mm", Locale.US);

        mViewStart.setText(simpleDateFormat.format(mEvent.mStartDate));
        mViewEnd.setText(simpleDateFormat.format(mEvent.mEndDate));
    }

    private void loadColor() {
        mViewColor.setColor(WatchEvent.stringToColor(mEvent.mColor));
    }

    private void loadRepeat() {
        if (mEvent.mRepeat.equals(WatchEvent.REPEAT_DAILY))
            mViewRepeat.setText(WatchEvent.REPEAT_DAILY);
        else if (mEvent.mRepeat.equals(WatchEvent.REPEAT_WEEKLY))
            mViewRepeat.setText(WatchEvent.REPEAT_WEEKLY);
        else if (mEvent.mRepeat.equals(WatchEvent.REPEAT_MONTHLY))
            mViewRepeat.setText(WatchEvent.REPEAT_MONTHLY);
        else
            mViewRepeat.setText("Never");
    }

    private void loadDescription() {
        mViewDescription.setText(mEvent.mDescription);
    }

    private void loadWatchEvent() {
        loadAlarm();
        loadAssign();
        loadDate();
        loadColor();
        loadRepeat();
        loadDescription();

        if (mEvent.mRepeat.length() != 0 ||
                mEvent.mDescription.length() != 0)
            viewAdvance();
    }

    private void saveWatchEvent() {

    }
}
