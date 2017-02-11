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
        addAlarm(0, "Agenda Reminders (Only for the App)", 0, mLineListener);
        addTitle("Alarm Clock");
        addAlarm(WatchEvent.NoticeAlarmList[0], mLineListener);
        addRoutine("Morning Routine");
        addAlarm(WatchEvent.NoticeAlarmList[1], mLineListener);
        addAlarm(WatchEvent.NoticeAlarmList[2], mLineListener);
        addAlarm(WatchEvent.NoticeAlarmList[3], mLineListener);
        addAlarm(WatchEvent.NoticeAlarmList[4], mLineListener);
        addAlarm(WatchEvent.NoticeAlarmList[5], mLineListener);
        addRoutine("Bed Time Routine");
        addAlarm(WatchEvent.NoticeAlarmList[6], mLineListener);
        addAlarm(WatchEvent.NoticeAlarmList[7], mLineListener);
        addAlarm(WatchEvent.NoticeAlarmList[8], mLineListener);
        addRoutine("Activities");
        for (int idx = 9; idx < WatchEvent.NoticeAlarmList.length; idx++)
            addAlarm(WatchEvent.NoticeAlarmList[idx], mLineListener);

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

    private void addAlarm(WatchEvent.NoticeAlarm alarm, View.OnClickListener listener) {
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
            mEvent.mAlert = (int) view.getTag();

            mActivityMain.mEventStack.push(mEvent);
            mActivityMain.popFragment();
        }
    };
}
