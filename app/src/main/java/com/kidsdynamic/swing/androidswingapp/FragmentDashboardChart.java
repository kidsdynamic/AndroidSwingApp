package com.kidsdynamic.swing.androidswingapp;

import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.TranslateAnimation;
import android.widget.ImageView;
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

    private ImageView mViewEmotionImage;
    private TextView mViewEmotionTitle;
    private TextView mViewEmotionMessage;
    private ViewChartKDToday mViewChartToday;
    private TextView mViewChartWeek;
    private TextView mViewChartMonth;
    private TextView mViewChartYear;
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

        mViewEmotionImage = (ImageView) mViewMain.findViewById(R.id.dashboard_chart_emotion_image);
        mViewEmotionTitle = (TextView) mViewMain.findViewById(R.id.dashboard_chart_emotion_title);
        mViewEmotionMessage = (TextView) mViewMain.findViewById(R.id.dashboard_chart_emotion_message);

        mViewChartToday = (ViewChartKDToday) mViewMain.findViewById(R.id.dashboard_chart_today);
        mViewChartWeek = (TextView) mViewMain.findViewById(R.id.dashboard_chart_week);
        mViewChartMonth = (TextView) mViewMain.findViewById(R.id.dashboard_chart_month);
        mViewChartYear = (TextView) mViewMain.findViewById(R.id.dashboard_chart_year);

        mViewIndoor = (ViewBorderButton) mViewMain.findViewById(R.id.dashboard_chart_indoor);
        mViewIndoor.setOnClickListener(mDoorListener);

        mViewOutdoor = (ViewBorderButton) mViewMain.findViewById(R.id.dashboard_chart_outdoor);
        mViewOutdoor.setOnClickListener(mDoorListener);

        return mViewMain;
    }

    @Override
    public ViewFragmentConfig getConfig() {
        return new ViewFragmentConfig("Dashboard", true, true, false,
                R.mipmap.city_newyork, ActivityMain.RESOURCE_HIDE, ActivityMain.RESOURCE_HIDE);
    }

    @Override
    public void onResume() {
        super.onResume();

        // todo: load today step here.
        int step = getStepToday(INDOOR) + getStepToday(OUTDOOR);

        int emotion = EMOTION_LOW;
        if (step > 6000)
            emotion = EMOTION_ALMOST;
        if (step > 12000)
            emotion = EMOTION_EXCELLENT;

        setEmotion(emotion);

        mViewSelector.clear();
        mViewSelector.add(Arrays.asList("Today", "This Week", "This Month", "This Year"));

        mViewIndicator.setDotCount(mViewSelector.getCount());
        mViewIndicator.setDotPosition(0);

        setDoor(INDOOR);
        showToday();

        Animation animation = new TranslateAnimation(0, 0, 300, 0);
        animation.setDuration(500);
        animation.setFillAfter(true);
        mViewEmotionImage.startAnimation(animation);
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
            Log.d("xxx", "indoor:" + (view == mViewIndoor) + " chart:" + getChart());
            setChart(getChart());
        }
    };

    private void setEmotion(int emotion) {
        switch (emotion) {
            case EMOTION_LOW:
                mEmotionColor = ContextCompat.getColor(mActivityMain, R.color.color_blue_main);

                mViewMain.setBackgroundResource(R.mipmap.background_dashboard_monster01);
                mViewEmotionImage.setImageResource(R.mipmap.monster_face_blue);
                mViewEmotionTitle.setText("Below Average!");
                mViewEmotionMessage.setText("Don't Give Up!\nYou Can Do This!");
                break;

            case EMOTION_ALMOST:
                mEmotionColor = ContextCompat.getColor(mActivityMain, R.color.color_green_main);

                mViewMain.setBackgroundResource(R.mipmap.background_dashboard_monster02);
                mViewEmotionImage.setImageResource(R.mipmap.monster_face_green);
                mViewEmotionTitle.setText("Almost There!");
                mViewEmotionMessage.setText("One More Time!\nYou Are Almost There!");
                break;

            default:
                mEmotionColor = ContextCompat.getColor(mActivityMain, R.color.color_orange_main);

                mViewMain.setBackgroundResource(R.mipmap.background_dashboard_monster03);
                mViewEmotionImage.setImageResource(R.mipmap.monster_face_yellow);
                mViewEmotionTitle.setText("Excellent!");
                mViewEmotionMessage.setText("Woohoo! You've\nReached Your Goal!");
                break;
        }

        mViewIndicator.setDotColorOn(mEmotionColor);
        mViewSelector.setTextColor(mEmotionColor);
        mViewSelector.setSelectorColor(mEmotionColor);
        mViewEmotionTitle.setTextColor(mEmotionColor);
        mViewEmotionMessage.setTextColor(mEmotionColor);
        mViewIndoor.setBorderColor(mEmotionColor);
        mViewOutdoor.setBorderColor(mEmotionColor);

        mViewChartToday.setChartColor(mEmotionColor);

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
        mViewChartToday.setVisibility(View.VISIBLE);
        mViewChartWeek.setVisibility(View.GONE);
        mViewChartMonth.setVisibility(View.GONE);
        mViewChartYear.setVisibility(View.GONE);

        mViewChartToday.setValue(getStepToday(getDoor()), 0);
        mViewChartToday.setTotal(getStepToday(INDOOR) + getStepToday(OUTDOOR));
        mViewChartToday.setGoal(12000);
        mViewChartToday.invalidate();
    }

    private void showWeek() {
        mViewChartToday.setVisibility(View.GONE);
        mViewChartWeek.setVisibility(View.VISIBLE);
        mViewChartMonth.setVisibility(View.GONE);
        mViewChartYear.setVisibility(View.GONE);

        mViewChartWeek.setText(getStepWeek(getDoor()).toString());
    }

    private void showMonth() {
        mViewChartToday.setVisibility(View.GONE);
        mViewChartWeek.setVisibility(View.GONE);
        mViewChartMonth.setVisibility(View.VISIBLE);
        mViewChartYear.setVisibility(View.GONE);

        mViewChartMonth.setText(getStepMonth(getDoor()).toString());
    }

    private void showYear() {
        mViewChartToday.setVisibility(View.GONE);
        mViewChartWeek.setVisibility(View.GONE);
        mViewChartMonth.setVisibility(View.GONE);
        mViewChartYear.setVisibility(View.VISIBLE);

        mViewChartYear.setText(getStepYear(getDoor()).toString());
    }

    private List<Integer> makeFakeList(int count, int maximum) {
        List<Integer> list = new ArrayList<>();
        for (int idx = 0; idx < count; idx++)
            list.add((int) (Math.random() * maximum));
        return list;
    }

    int todayStepIndoor = (int) (Math.random() * 7500);
    int todayStepOutdoor = (int) (Math.random() * 7500);
    List<Integer> weekStepIndoor = makeFakeList(7, 7500);
    List<Integer> weekStepOutdoor = makeFakeList(7, 7500);
    List<Integer> monthStepIndoor = makeFakeList(30, 7500);
    List<Integer> monthStepOutdoor = makeFakeList(30, 7500);
    List<Integer> yearStepIndoor = makeFakeList(12, 30 * 7500);
    List<Integer> yearStepOutdoor = makeFakeList(12, 30 * 7500);

    private int getStepToday(int door) {
        WatchActivity act = mActivityMain.mOperator.getActivityOfDay();
        return door == INDOOR ? act.mIndoor.mSteps : act.mOutdoor.mSteps;
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
