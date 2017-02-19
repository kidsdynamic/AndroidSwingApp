package com.kidsdynamic.swing.androidswingapp;

import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.Arrays;

/**
 * Created by 03543 on 2017/2/19.
 */

public class FragmentDashboardChart extends ViewFragment {
    private static final int EMOTION_LOW = 0;
    private static final int EMOTION_ALMOST = 1;
    private static final int EMOTION_EXCELLENT = 2;

    private ActivityMain mActivityMain;
    private View mViewMain;

    private ViewDotIndicator mViewIndicator;
    private ViewTextSelector mViewSelector;

    private ImageView mViewEmotionImage;
    private TextView mViewEmotionTitle;
    private TextView mViewEmotionMessage;

    private int mEmotion;

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

        mViewEmotionImage = (ImageView)mViewMain.findViewById(R.id.dashboard_chart_emotion_image);
        mViewEmotionTitle = (TextView)mViewMain.findViewById(R.id.dashboard_chart_emotion_title);
        mViewEmotionMessage = (TextView)mViewMain.findViewById(R.id.dashboard_chart_emotion_message);

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

        // todo: load emotion here.
        setEmotion(EMOTION_EXCELLENT);

        mViewSelector.clear();
        mViewSelector.add(Arrays.asList("Today", "This Week", "This Month", "This Year"));

        mViewIndicator.setDotCount(mViewSelector.getCount());
        mViewIndicator.setDotPosition(0);
    }

    @Override
    public void onPause() {
        super.onPause();
    }

    private ViewTextSelector.OnSelectListener mSelectorListener = new ViewTextSelector.OnSelectListener() {
        @Override
        public void OnSelect(View view, int position) {
            mViewIndicator.setDotPosition(position);
        }
    };

    private void setEmotion(int emotion) {
        int color = Color.BLACK;

        switch(emotion) {
            case EMOTION_LOW:
                color = ContextCompat.getColor(mActivityMain, R.color.color_blue_main);

                mViewMain.setBackgroundResource(R.mipmap.background_dashboard_monster01);
                mViewEmotionImage.setImageResource(R.mipmap.monster_face_blue);
                mViewEmotionTitle.setText("Below Average!");
                mViewEmotionMessage.setText("Don't Give Up!\nYou Can Do This!");
                break;

            case EMOTION_ALMOST:
                color = ContextCompat.getColor(mActivityMain, R.color.color_green_main);

                mViewMain.setBackgroundResource(R.mipmap.background_dashboard_monster02);
                mViewEmotionImage.setImageResource(R.mipmap.monster_face_green);
                mViewEmotionTitle.setText("Almost There!");
                mViewEmotionMessage.setText("One More Time!\nYou Are Almost There!");
                break;

            default:
                color = ContextCompat.getColor(mActivityMain, R.color.color_orange_main);

                mViewMain.setBackgroundResource(R.mipmap.background_dashboard_monster03);
                mViewEmotionImage.setImageResource(R.mipmap.monster_face_yellow);
                mViewEmotionTitle.setText("Excellent!");
                mViewEmotionMessage.setText("Woohoo! You've\nReached Your Goal!");
                break;
        }

        mViewIndicator.setDotColorOn(color);
        mViewSelector.setTextColor(color);
        mViewSelector.setSelectorColor(color);
        mViewEmotionTitle.setTextColor(color);
        mViewEmotionMessage.setTextColor(color);

        mEmotion = emotion;
    }

}
