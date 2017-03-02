package com.kidsdynamic.swing.androidswingapp;

import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

/**
 * Created by 03543 on 2017/2/25.
 */

public class FragmentDashboardEmotion extends ViewFragment {
    private ActivityMain mActivityMain;
    private View mViewMain;

    private TextView mViewHello;
    private ImageView mViewMonster;
    private TextView mViewMessage;

    private ImageView mViewStep;
    private ImageView mViewUv;
    private ImageView mViewGlasses;

    private int mEmotionColor;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mActivityMain = (ActivityMain) getActivity();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        mViewMain = inflater.inflate(R.layout.fragment_dashboard_emotion, container, false);

        mViewHello = (TextView) mViewMain.findViewById(R.id.dashboard_emotion_hello);
        mViewMonster = (ImageView) mViewMain.findViewById(R.id.dashboard_emotion_monster);
        mViewMessage = (TextView) mViewMain.findViewById(R.id.dashboard_emotion_message);

        mViewStep = (ImageView) mViewMain.findViewById(R.id.dashboard_emotion_step);
        mViewStep.setOnClickListener(mSelectListener);

        mViewUv = (ImageView) mViewMain.findViewById(R.id.dashboard_emotion_uv);
        mViewUv.setOnClickListener(mSelectListener);

        mViewGlasses = (ImageView) mViewMain.findViewById(R.id.dashboard_emotion_glasses);
        mViewGlasses.setOnClickListener(mSelectListener);

        return mViewMain;
    }

    @Override
    public ViewFragmentConfig getConfig() {
        return new ViewFragmentConfig(
                getResources().getString(R.string.title_dashboard), true, true, false,
                ActivityMain.RESOURCE_HIDE, ActivityMain.RESOURCE_HIDE, ActivityMain.RESOURCE_HIDE);
    }

    @Override
    public void onResume() {
        super.onResume();

        WatchActivity act = mActivityMain.mOperator.getActivityOfDay();
        int step = act.mIndoor.mSteps + act.mOutdoor.mSteps;

        if (step < WatchActivity.STEP_ALMOST)
            showBelow();
        else if (step < WatchActivity.STEP_GOAL)
            showAlmost();
        else
            showExcellent();
    }

    private View.OnClickListener mSelectListener = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            mActivityMain.selectFragment(FragmentDashboardChart.class.getName(), null);
        }
    };

    private void showExcellent() {
        mViewMain.setBackgroundResource(R.mipmap.background_dashboard_monster03);
        mViewMonster.setImageResource(R.mipmap.monster_yellow);
        mViewMessage.setText(getResources().getString(R.string.dashboard_emotion_excellent));

        mEmotionColor = ContextCompat.getColor(mActivityMain, R.color.color_orange_main);
        mViewHello.setTextColor(mEmotionColor);
        mViewMessage.setTextColor(mEmotionColor);

        mViewStep.setImageResource(R.mipmap.active_step_orange);
        mViewUv.setImageResource(R.mipmap.active_uv_orange);
        mViewGlasses.setImageResource(R.mipmap.active_glasses_orange);
    }

    private void showAlmost() {
        mViewMain.setBackgroundResource(R.mipmap.background_dashboard_monster02);
        mViewMonster.setImageResource(R.mipmap.monster_green);
        mViewMessage.setText(getResources().getString(R.string.dashboard_emotion_almost));

        mEmotionColor = ContextCompat.getColor(mActivityMain, R.color.color_green_main);
        mViewHello.setTextColor(mEmotionColor);
        mViewMessage.setTextColor(mEmotionColor);

        mViewStep.setImageResource(R.mipmap.active_step_green);
        mViewUv.setImageResource(R.mipmap.active_step_green);
        mViewGlasses.setImageResource(R.mipmap.active_step_green);
    }

    private void showBelow() {
        mViewMain.setBackgroundResource(R.mipmap.background_dashboard_monster01);
        mViewMonster.setImageResource(R.mipmap.monster_purple);
        mViewMessage.setText(getResources().getString(R.string.dashboard_emotion_below));

        mEmotionColor = ContextCompat.getColor(mActivityMain, R.color.color_blue_main);
        mViewHello.setTextColor(mEmotionColor);
        mViewMessage.setTextColor(mEmotionColor);

        mViewStep.setImageResource(R.mipmap.active_step_blue);
        mViewUv.setImageResource(R.mipmap.active_step_blue);
        mViewGlasses.setImageResource(R.mipmap.active_step_blue);
    }
}
