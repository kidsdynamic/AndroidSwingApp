package com.kidsdynamic.swing.androidswingapp;

import android.graphics.Typeface;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import java.util.Locale;

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

        if(mActivityMain.language.equals(Locale.JAPAN.getLanguage())) {
            addSeparator();
            addTitle(getResources().getString(R.string.calendar_alarm_agenda));
            addAlarm(WatchEvent.AlarmList_ja[0], mLineListener);
            addTitle(getResources().getString(R.string.calendar_alarm_clock));
            addAlarm(WatchEvent.AlarmList_ja[1], mLineListener);
            addRoutine(getResources().getString(R.string.calendar_alarm_morning));
            addAlarm(WatchEvent.AlarmList_ja[2], mLineListener);
            addAlarm(WatchEvent.AlarmList_ja[3], mLineListener);
            addAlarm(WatchEvent.AlarmList_ja[4], mLineListener);
            addAlarm(WatchEvent.AlarmList_ja[5], mLineListener);
            addAlarm(WatchEvent.AlarmList_ja[6], mLineListener);
            addRoutine(getResources().getString(R.string.calendar_alarm_bed));
            addAlarm(WatchEvent.AlarmList_ja[7], mLineListener);
            addRoutine(getResources().getString(R.string.calendar_alarm_activities));
            for (int idx = 8; idx < WatchEvent.AlarmList_ja.length; idx++)
                addAlarm(WatchEvent.AlarmList_ja[idx], mLineListener);
        } else {
            addSeparator();
            addTitle(getResources().getString(R.string.calendar_alarm_agenda));
            addAlarm(WatchEvent.AlarmList[0], mLineListener);
            addTitle(getResources().getString(R.string.calendar_alarm_clock));
            addAlarm(WatchEvent.AlarmList[1], mLineListener);
            addRoutine(getResources().getString(R.string.calendar_alarm_morning));
            addAlarm(WatchEvent.AlarmList[2], mLineListener);
            addAlarm(WatchEvent.AlarmList[3], mLineListener);
            addAlarm(WatchEvent.AlarmList[4], mLineListener);
            addAlarm(WatchEvent.AlarmList[5], mLineListener);
            addAlarm(WatchEvent.AlarmList[6], mLineListener);
            addRoutine(getResources().getString(R.string.calendar_alarm_bed));
            addAlarm(WatchEvent.AlarmList[7], mLineListener);
            addAlarm(WatchEvent.AlarmList[8], mLineListener);
            addAlarm(WatchEvent.AlarmList[9], mLineListener);
            addRoutine(getResources().getString(R.string.calendar_alarm_activities));
            for (int idx = 10; idx < WatchEvent.AlarmList.length; idx++)
                addAlarm(WatchEvent.AlarmList[idx], mLineListener);
        }


        return mViewMain;
    }

    @Override
    public void onResume() {
        super.onResume();

        // 若由Stack非空, 則可取出處於編輯狀態下的Event
        if (!mActivityMain.mEventStack.isEmpty())
            mEvent = mActivityMain.mEventStack.pop();
        else
            mEvent = new WatchEvent();
    }

    @Override
    public ViewFragmentConfig getConfig() {
        return new ViewFragmentConfig(
                getResources().getString(R.string.title_calendar), true, true, false,
                ActivityMain.RESOURCE_IGNORE, R.mipmap.icon_left, ActivityMain.RESOURCE_HIDE);
    }

    @Override
    public void onToolbarAction1() {
        mActivityMain.mEventStack.push(mEvent);
        mActivityMain.popFragment();
    }

    private void addAlarm(WatchEvent.Alarm alarm, View.OnClickListener listener) {
        addAlarm(alarm.mId, getResources().getString(alarm.mName), alarm.mResource, listener);
    }

    // 在UI新增一個Alarm
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

    // 在UI新增一個routine
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

    // 新增UI上的標題列
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

    // 新增UI上的分隔線
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
            Log.d("On CLicked", String.valueOf(mEvent.mAlert));
            mActivityMain.mEventStack.push(mEvent);
            mActivityMain.popFragment();
        }
    };
}
