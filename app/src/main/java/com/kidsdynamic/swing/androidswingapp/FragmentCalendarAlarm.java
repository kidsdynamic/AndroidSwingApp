package com.kidsdynamic.swing.androidswingapp;

import android.graphics.Typeface;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import static android.util.TypedValue.COMPLEX_UNIT_SP;

/**
 * Created by 03543 on 2017/2/8.
 */

public class FragmentCalendarAlarm extends ViewFragment {
    private ActivityMain mActivityMain;
    private View mViewMain;
    private LinearLayout mViewContainer;

    private int mLineHeight = 200;
    private int mLineMarginStart = 10;
    private int mLineMarginEnd = 10;

    private WatchEvent mEvent;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mActivityMain = (ActivityMain) getActivity();

        mLineHeight = getResources().getDisplayMetrics().heightPixels / 16;
        mLineMarginStart = getResources().getDisplayMetrics().widthPixels / 16;
        mLineMarginEnd = getResources().getDisplayMetrics().widthPixels / 16;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        mViewMain = inflater.inflate(R.layout.fragment_calendar_alarm, container, false);

        mViewContainer = (LinearLayout) mViewMain.findViewById(R.id.calendar_alert_container);

        addSeparator();
        addTitle("Agenda");
        addAlarm(NoticeAlarmList[0], mLineListener);
        addTitle("Alarm Clock");
        addAlarm(NoticeAlarmList[1], mLineListener);
        addRoutine("Morning Routine");
        addAlarm(NoticeAlarmList[2], mLineListener);
        addAlarm(NoticeAlarmList[3], mLineListener);
        addAlarm(NoticeAlarmList[4], mLineListener);
        addAlarm(NoticeAlarmList[5], mLineListener);
        addAlarm(NoticeAlarmList[6], mLineListener);
        addRoutine("Bed Time Routine");
        addAlarm(NoticeAlarmList[7], mLineListener);
        addAlarm(NoticeAlarmList[8], mLineListener);
        addAlarm(NoticeAlarmList[9], mLineListener);
        addRoutine("Activities");
        for (int idx = 10; idx < NoticeAlarmList.length; idx++)
            addAlarm(NoticeAlarmList[idx], mLineListener);

        return mViewMain;
    }

    @Override
    public void onResume() {
        super.onResume();

        if (!mActivityMain.mEventStack.isEmpty())
            mEvent = mActivityMain.mEventStack.pop();
        else
            mEvent = new WatchEvent();
    }

    @Override
    public ViewFragmentConfig getConfig() {
        return new ViewFragmentConfig("Calendar", true, true, false,
                ActivityMain.RESOURCE_IGNORE, R.mipmap.icon_left, ActivityMain.RESOURCE_HIDE);
    }

    @Override
    public void onToolbarAction1() {
        mActivityMain.mEventStack.push(mEvent);
        mActivityMain.popFragment();
    }

    private void addAlarm(NoticeAlarm alarm, View.OnClickListener listener) {
        addAlarm(alarm.mId, alarm.mName, alarm.mResource, listener);
    }

    private void addAlarm(int id, String title, int resource, View.OnClickListener listener) {
        RelativeLayout.LayoutParams layoutParams;

        RelativeLayout viewLine = new RelativeLayout(mActivityMain);
        viewLine.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, mLineHeight));

        layoutParams = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.WRAP_CONTENT, RelativeLayout.LayoutParams.MATCH_PARENT);
        layoutParams.addRule(RelativeLayout.ALIGN_PARENT_START);
        layoutParams.setMarginStart(mLineMarginStart);

        TextView viewLabel = new TextView(mActivityMain);
        viewLabel.setGravity(Gravity.CENTER);
        viewLabel.setText(title);
        viewLabel.setLayoutParams(layoutParams);

        layoutParams = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.WRAP_CONTENT, RelativeLayout.LayoutParams.MATCH_PARENT);
        layoutParams.addRule(RelativeLayout.ALIGN_PARENT_END);
        layoutParams.setMarginStart(mLineMarginEnd);

        ImageView viewIcon = new ImageView(mActivityMain);
        viewIcon.setAdjustViewBounds(true);
        if (resource != 0)
            viewIcon.setImageResource(resource);
        viewIcon.setLayoutParams(layoutParams);

        viewLine.setTag(id);
        viewLine.addView(viewLabel);
        viewLine.addView(viewIcon);

        if (listener != null)
            viewLine.setOnClickListener(listener);

        mViewContainer.addView(viewLine);
        addSeparator();
    }

    private void addRoutine(String label) {
        RelativeLayout.LayoutParams layoutParams;

        RelativeLayout viewLine = new RelativeLayout(mActivityMain);
        viewLine.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, mLineHeight));

        layoutParams = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.WRAP_CONTENT, RelativeLayout.LayoutParams.MATCH_PARENT);
        layoutParams.addRule(RelativeLayout.ALIGN_PARENT_START);
        layoutParams.setMarginStart(mLineMarginStart);

        TextView viewLabel = new TextView(mActivityMain);
        viewLabel.setGravity(Gravity.CENTER);
        viewLabel.setTextColor(ContextCompat.getColor(mActivityMain, R.color.color_orange_main));
        viewLabel.setTextSize(COMPLEX_UNIT_SP, 20);
        viewLabel.setTypeface(null, Typeface.BOLD);
        viewLabel.setText(label);
        viewLabel.setLayoutParams(layoutParams);

        viewLine.addView(viewLabel);

        mViewContainer.addView(viewLine);
        addSeparator();
    }

    private void addTitle(String label) {
        RelativeLayout.LayoutParams layoutParams;

        RelativeLayout viewLine = new RelativeLayout(mActivityMain);
        viewLine.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, mLineHeight));

        layoutParams = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.WRAP_CONTENT, RelativeLayout.LayoutParams.MATCH_PARENT);
        layoutParams.addRule(RelativeLayout.CENTER_IN_PARENT);
        layoutParams.setMarginStart(mLineMarginStart);

        TextView viewLabel = new TextView(mActivityMain);
        viewLabel.setGravity(Gravity.CENTER);
        viewLabel.setTextColor(ContextCompat.getColor(mActivityMain, R.color.color_orange_main));
        viewLabel.setTextSize(COMPLEX_UNIT_SP, 20);
        viewLabel.setTypeface(null, Typeface.BOLD);
        viewLabel.setText(label);
        viewLabel.setLayoutParams(layoutParams);

        viewLine.addView(viewLabel);

        mViewContainer.addView(viewLine);
        addSeparator();
    }

    private void addSeparator() {
        View viewSeparator = new View(mActivityMain);
        viewSeparator.setBackgroundColor(ContextCompat.getColor(mActivityMain, R.color.color_gray));
        viewSeparator.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, (int) getResources().getDisplayMetrics().density));

        mViewContainer.addView(viewSeparator);
    }

    private View.OnClickListener mLineListener = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            int id = (int) view.getTag();
            mEvent.mAlert = id;

            mActivityMain.mEventStack.push(mEvent);
            mActivityMain.popFragment();
        }
    };

    static public class NoticeAlarm {
        int mId;
        String mName;
        int mResource;

        NoticeAlarm(int id, String name, int resource) {
            mId = id;
            mName = name;
            mResource = resource;
        }
    }

    final static NoticeAlarm[] NoticeAlarmList = new NoticeAlarm[]{
            new NoticeAlarm(0, "Agenda Reminders (Only for the App", 0),
            new NoticeAlarm(36, "Good Morning", R.mipmap.icon_alert),
            new NoticeAlarm(37, "Make Bed", R.mipmap.icon_sound),
            new NoticeAlarm(38, "Get Dress", R.mipmap.icon_sound),
            new NoticeAlarm(39, "Eat Breakfast", R.mipmap.icon_sound),
            new NoticeAlarm(40, "Brush Teeth", R.mipmap.icon_sound),
            new NoticeAlarm(41, "Get Ready for School", R.mipmap.icon_sound),
            new NoticeAlarm(42, "Put on Pajamas", R.mipmap.icon_sound),
            new NoticeAlarm(43, "Story Time", R.mipmap.icon_sound),
            new NoticeAlarm(44, "Good Night", R.mipmap.icon_sound),
            new NoticeAlarm(45, "Collect Toys", R.mipmap.icon_sound),
            new NoticeAlarm(46, "Set Table", R.mipmap.icon_sound),
            new NoticeAlarm(47, "Feed Pet", R.mipmap.icon_sound),
            new NoticeAlarm(48, "Water Plants", R.mipmap.icon_sound),
            new NoticeAlarm(49, "Clean Table", R.mipmap.icon_sound),
            new NoticeAlarm(50, "Clean Bedroom", R.mipmap.icon_sound),
            new NoticeAlarm(51, "Homework Time", R.mipmap.icon_sound),
            new NoticeAlarm(52, "Take a Nap", R.mipmap.icon_sound),
            new NoticeAlarm(53, "Outdoor Play Time", R.mipmap.icon_sound),
            new NoticeAlarm(54, "Fun time", R.mipmap.icon_sound),
            new NoticeAlarm(55, "Exercise", R.mipmap.icon_sound),
            new NoticeAlarm(56, "Practice Music", R.mipmap.icon_sound),
            new NoticeAlarm(57, "Drawing Time", R.mipmap.icon_sound),
            new NoticeAlarm(58, "Reading Time", R.mipmap.icon_sound),
            new NoticeAlarm(59, "Take a Bath", R.mipmap.icon_sound),
            new NoticeAlarm(60, "Family Time", R.mipmap.icon_sound),
            new NoticeAlarm(61, "Lunch Time", R.mipmap.icon_sound),
            new NoticeAlarm(62, "Dinner Time", R.mipmap.icon_sound),
            new NoticeAlarm(63, "Afternoon Snack Time", R.mipmap.icon_sound),
            new NoticeAlarm(64, "Review the Backpack", R.mipmap.icon_sound),
    };


    public static String findAlarmName(int id) {
        for (NoticeAlarm alarm : NoticeAlarmList)
            if (alarm.mId == id)
                return alarm.mName;
        return "";
    }

    public static int findAlarmId(String name) {
        for (NoticeAlarm alarm : NoticeAlarmList)
            if (alarm.mName.equals(name))
                return alarm.mId;
        return -1;
    }
}
