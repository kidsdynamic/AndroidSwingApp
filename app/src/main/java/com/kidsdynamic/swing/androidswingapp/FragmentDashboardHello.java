package com.kidsdynamic.swing.androidswingapp;

import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

/**
 * Created by 03543 on 2017/2/19.
 */

public class FragmentDashboardHello extends ViewFragment {
    private ActivityMain mActivityMain;
    private View mViewMain;

    private TextView mViewHello;
    private ImageView mViewMonster;
    private TextView mViewMessage;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mActivityMain = (ActivityMain) getActivity();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        mViewMain = inflater.inflate(R.layout.fragment_dashboard_hello, container, false);

        mViewHello = (TextView) mViewMain.findViewById(R.id.dashboard_hello_hello);
        mViewMonster = (ImageView) mViewMain.findViewById(R.id.dashboard_hello_monster);
        mViewMessage = (TextView) mViewMain.findViewById(R.id.dashboard_hello_message);

        return mViewMain;
    }

    @Override
    public ViewFragmentConfig getConfig() {
        return new ViewFragmentConfig("Dashboard", true, true, false,
                ActivityMain.RESOURCE_HIDE, ActivityMain.RESOURCE_HIDE, ActivityMain.RESOURCE_HIDE);
    }

    @Override
    public void onResume() {
        super.onResume();

        // Todo: load steps here.
        showOrange();
    }

    @Override
    public void onPause() {
        super.onPause();
    }

    private void showPurple() {
        mViewMain.setBackgroundResource(R.mipmap.background_dashboard_monster01);
        mViewHello.setTextColor(ContextCompat.getColor(mActivityMain, R.color.color_blue_main));
        mViewMessage.setTextColor(ContextCompat.getColor(mActivityMain, R.color.color_blue_main));
        mViewMonster.setImageResource(R.mipmap.monster_purple);
        mViewMessage.setText("Below Average!");
    }

    private void showGreen() {
        mViewMain.setBackgroundResource(R.mipmap.background_dashboard_monster02);
        mViewHello.setTextColor(ContextCompat.getColor(mActivityMain, R.color.color_green_main));
        mViewMessage.setTextColor(ContextCompat.getColor(mActivityMain, R.color.color_green_main));
        mViewMonster.setImageResource(R.mipmap.monster_green);
        mViewMessage.setText("Almost There!");
    }

    private void showOrange() {
        mViewMain.setBackgroundResource(R.mipmap.background_dashboard_monster03);
        mViewHello.setTextColor(ContextCompat.getColor(mActivityMain, R.color.color_orange_main));
        mViewMessage.setTextColor(ContextCompat.getColor(mActivityMain, R.color.color_orange_main));
        mViewMonster.setImageResource(R.mipmap.monster_yellow);
        mViewMessage.setText("Excellent!");
    }
}
