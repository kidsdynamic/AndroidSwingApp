package com.kidsdynamic.swing.androidswingapp;

import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Created by 03543 on 2017/2/19.
 */

public class FragmentDashboardChart extends ViewFragment {
    private static final int EMOTION_LOW = 0;
    private static final int EMOTION_ALMOST = 1;
    private static final int EMOTION_EXCELLENT = 2;

    private static final int INDOOR = 0;
    private static final int OUTDOOR = 1;

    private static final int CHART_TODAY = 0;
    private static final int CHART_WEEK = 1;
    private static final int CHART_MONTH = 2;
    private static final int CHART_YEAR = 3;

    private ActivityMain mActivityMain;
    private View mViewMain;

    private ViewDotIndicator mViewIndicator;
    private ViewTextSelector mViewSelector;

    private TextView mViewMessage;
    private ViewChartKDToday mViewChartToday;
    private ViewChartKDBar mViewChartWeek;
    private ViewChartKDCurve mViewChartMonth;
    private ViewChartKDBar mViewChartYear;
    private ViewBorderButton mViewIndoor;
    private ViewBorderButton mViewOutdoor;

    private int mEmotion;
    private int mEmotionColor;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mActivityMain = (ActivityMain) getActivity();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        mViewMain = inflater.inflate(R.layout.fragment_dashboard_chart, container, false);

        mViewIndicator = (ViewDotIndicator) mViewMain.findViewById(R.id.dashboard_chart_indicator);

        mViewSelector = (ViewTextSelector) mViewMain.findViewById(R.id.dashboard_chart_selector);
        mViewSelector.setOnSelectListener(mSelectorListener);

        mViewMessage = (TextView) mViewMain.findViewById(R.id.dashboard_chart_message);

        mViewChartToday = (ViewChartKDToday) mViewMain.findViewById(R.id.dashboard_chart_today);
        mViewChartWeek = (ViewChartKDBar) mViewMain.findViewById(R.id.dashboard_chart_week);
        mViewChartMonth = (ViewChartKDCurve) mViewMain.findViewById(R.id.dashboard_chart_month);
        mViewChartYear = (ViewChartKDBar) mViewMain.findViewById(R.id.dashboard_chart_year);

        mViewChartWeek.setTitle(getResources().getString(R.string.dashboard_chart_steps));
        mViewChartYear.setTitle(getResources().getString(R.string.dashboard_chart_steps));

        mViewIndoor = (ViewBorderButton) mViewMain.findViewById(R.id.dashboard_chart_indoor);
        mViewIndoor.setOnClickListener(mDoorListener);

        mViewOutdoor = (ViewBorderButton) mViewMain.findViewById(R.id.dashboard_chart_outdoor);
        mViewOutdoor.setOnClickListener(mDoorListener);

        return mViewMain;
    }

    @Override
    public ViewFragmentConfig getConfig() {
        return new ViewFragmentConfig(
                getResources().getString(R.string.title_dashboard), true, true, false,
                R.mipmap.city_california, ActivityMain.RESOURCE_HIDE, ActivityMain.RESOURCE_HIDE);
    }

    @Override
    public void onResume() {
        super.onResume();

        int step = getStepToday(INDOOR).mSteps + getStepToday(OUTDOOR).mSteps;

        int emotion = EMOTION_LOW;
        if (step > 6000)
            emotion = EMOTION_ALMOST;
        if (step > 12000)
            emotion = EMOTION_EXCELLENT;

        setEmotion(emotion);

        mViewSelector.clear();
        mViewSelector.add(Arrays.asList(
                getResources().getString(R.string.dashboard_chart_today),
                getResources().getString(R.string.dashboard_chart_this_week),
                getResources().getString(R.string.dashboard_chart_this_month),
                getResources().getString(R.string.dashboard_chart_this_year)));

        mViewIndicator.setDotCount(mViewSelector.getCount());
        mViewIndicator.setDotPosition(0);

        setDoor(INDOOR);
        showToday();
    }

    @Override
    public void onPause() {
        super.onPause();
    }

    private ViewTextSelector.OnSelectListener mSelectorListener = new ViewTextSelector.OnSelectListener() {
        @Override
        public void OnSelect(View view, int position) {
            mViewIndicator.setDotPosition(position);
            setChart(position);
        }
    };

    private View.OnClickListener mDoorListener = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            setDoor(view == mViewIndoor ? INDOOR : OUTDOOR);
            setChart(getChart());
        }
    };

    private void setEmotion(int emotion) {
        switch (emotion) {
            case EMOTION_LOW:
                mEmotionColor = ContextCompat.getColor(mActivityMain, R.color.color_blue_main);

                mViewMain.setBackgroundResource(R.mipmap.background_dashboard_monster01);
                mViewMessage.setText(getResources().getString(R.string.dashboard_chart_message_below));
                break;

            case EMOTION_ALMOST:
                mEmotionColor = ContextCompat.getColor(mActivityMain, R.color.color_green_main);

                mViewMain.setBackgroundResource(R.mipmap.background_dashboard_monster02);
                mViewMessage.setText(getResources().getString(R.string.dashboard_chart_message_almost));
                break;

            default:
                mEmotionColor = ContextCompat.getColor(mActivityMain, R.color.color_orange_main);

                mViewMain.setBackgroundResource(R.mipmap.background_dashboard_monster03);
                mViewMessage.setText(getResources().getString(R.string.dashboard_chart_message_excellent));
                break;
        }

        mViewIndicator.setDotColorOn(mEmotionColor);
        mViewSelector.setTextColor(mEmotionColor);
        mViewSelector.setSelectorColor(mEmotionColor);
        mViewMessage.setTextColor(mEmotionColor);
        mViewIndoor.setBorderColor(mEmotionColor);
        mViewOutdoor.setBorderColor(mEmotionColor);

        mViewChartToday.mChartColor = mEmotionColor;
        mViewChartWeek.mChartColor = mEmotionColor;
        mViewChartMonth.mChartColor = mEmotionColor;
        mViewChartYear.mChartColor = mEmotionColor;

        mEmotion = emotion;
    }

    private int getDoor() {
        return mViewIndoor.isSelected() ? INDOOR : OUTDOOR;
    }

    private void setDoor(int door) {
        if (door == INDOOR) {
            mViewIndoor.setSelected(true);
            mViewIndoor.setOnClickListener(null);
            mViewOutdoor.setSelected(false);
            mViewOutdoor.setOnClickListener(mDoorListener);
        } else {
            mViewIndoor.setSelected(false);
            mViewIndoor.setOnClickListener(mDoorListener);
            mViewOutdoor.setSelected(true);
            mViewOutdoor.setOnClickListener(null);
        }
    }

    private int getChart() {
        if (mViewChartToday.getVisibility() == View.VISIBLE)
            return CHART_TODAY;
        else if (mViewChartWeek.getVisibility() == View.VISIBLE)
            return CHART_WEEK;
        else if (mViewChartMonth.getVisibility() == View.VISIBLE)
            return CHART_MONTH;
        else if (mViewChartYear.getVisibility() == View.VISIBLE)
            return CHART_YEAR;

        return CHART_TODAY;
    }

    private void setChart(int chart) {
        if (chart == CHART_TODAY)
            showToday();
        else if (chart == CHART_WEEK)
            showWeek();
        else if (chart == CHART_MONTH)
            showMonth();
        else if (chart == CHART_YEAR)
            showYear();
    }

    private void showToday() {
        mViewMessage.setVisibility(View.VISIBLE);
        mViewChartToday.setVisibility(View.VISIBLE);
        mViewChartWeek.setVisibility(View.GONE);
        mViewChartMonth.setVisibility(View.GONE);
        mViewChartYear.setVisibility(View.GONE);

        mViewChartToday.setValue(getStepToday(getDoor()));
        mViewChartToday.setTotal(getStepToday(INDOOR).mSteps + getStepToday(OUTDOOR).mSteps);
        mViewChartToday.setGoal(12000);
        mViewChartToday.invalidate();
    }

    private void showWeek() {
        mViewMessage.setVisibility(View.GONE);
        mViewChartToday.setVisibility(View.GONE);
        mViewChartWeek.setVisibility(View.VISIBLE);
        mViewChartMonth.setVisibility(View.GONE);
        mViewChartYear.setVisibility(View.GONE);

        mViewChartWeek.setValue(getStepWeek(getDoor()));
        mViewChartWeek.invalidate();
    }

    private void showMonth() {
        mViewMessage.setVisibility(View.GONE);
        mViewChartToday.setVisibility(View.GONE);
        mViewChartWeek.setVisibility(View.GONE);
        mViewChartMonth.setVisibility(View.VISIBLE);
        mViewChartYear.setVisibility(View.GONE);

        mViewChartMonth.setValue(getStepMonth(getDoor()));
        mViewChartMonth.invalidate();
    }

    private void showYear() {
        mViewMessage.setVisibility(View.GONE);
        mViewChartToday.setVisibility(View.GONE);
        mViewChartWeek.setVisibility(View.GONE);
        mViewChartMonth.setVisibility(View.GONE);
        mViewChartYear.setVisibility(View.VISIBLE);

        mViewChartYear.setValue(getStepYear(getDoor()));
        mViewChartYear.invalidate();
    }

    private WatchActivity.Act getStepToday(int door) {
        WatchActivity act = mActivityMain.mOperator.getActivityOfDay();
        return door == INDOOR ? act.mIndoor : act.mOutdoor;
    }

    private List<WatchActivity.Act> getStepWeek(int door) {
        List<WatchActivity> thisWeek = mActivityMain.mOperator.getActivityOfWeek();
        List<WatchActivity.Act> rtn = new ArrayList<>();

        for (WatchActivity activity : thisWeek)
            rtn.add(new WatchActivity.Act(door == INDOOR ? activity.mIndoor : activity.mOutdoor));

        return rtn;
    }

    private List<WatchActivity.Act> getStepMonth(int door) {
        List<WatchActivity> thisWeek = mActivityMain.mOperator.getActivityOfMonth();
        List<WatchActivity.Act> rtn = new ArrayList<>();

        for (WatchActivity activity : thisWeek)
            rtn.add(new WatchActivity.Act(door == INDOOR ? activity.mIndoor : activity.mOutdoor));

//        for (WatchActivity.Act act : rtn) {
//            act.mSteps = (int) (Math.random() * mViewChartMonth.mAxisVMax);
//        }

        return rtn;
    }

    private List<WatchActivity.Act> getStepYear(int door) {
        List<WatchActivity> thisWeek = mActivityMain.mOperator.getActivityOfYear();
        List<WatchActivity.Act> rtn = new ArrayList<>();

        for (WatchActivity activity : thisWeek)
            rtn.add(new WatchActivity.Act(door == INDOOR ? activity.mIndoor : activity.mOutdoor));

        return rtn;
    }

}
