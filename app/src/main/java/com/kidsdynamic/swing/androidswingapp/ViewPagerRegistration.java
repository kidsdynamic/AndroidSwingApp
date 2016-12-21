package com.kidsdynamic.swing.androidswingapp;

import android.content.Context;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.ArrayList;

/**
 * Created by 03543 on 2016/12/22.
 */

public class ViewPagerRegistration extends ViewPager {
    ArrayList<View> mViewList;

    public ViewPagerRegistration(Context context, AttributeSet attrs) {
        super(context);

        mViewList = new ArrayList<>();

        LayoutInflater inflater = (LayoutInflater) context.getSystemService(context.LAYOUT_INFLATER_SERVICE);

        mViewList.add(inflater.inflate(R.layout.registration_login, null));
        mViewList.add(inflater.inflate(R.layout.registration_account, null));
        mViewList.add(inflater.inflate(R.layout.registration_info, null));
        mViewList.add(inflater.inflate(R.layout.registration_have_watch, null));
        mViewList.add(inflater.inflate(R.layout.registration_select_watch, null));
        mViewList.add(inflater.inflate(R.layout.registration_kid, null));

        setAdapter(mPagerAdapter);
    }

    PagerAdapter mPagerAdapter = new PagerAdapter() {

        @Override
        public boolean isViewFromObject(View arg0, Object arg1) {
            return arg0 == arg1;
        }

        @Override
        public int getCount() {
            return mViewList.size();
        }

        @Override
        public void destroyItem(ViewGroup container, int position, Object object) {
            container.removeView(mViewList.get(position));
        }

        @Override
        public int getItemPosition(Object object) {
            return super.getItemPosition(object);
        }

        @Override
        public CharSequence getPageTitle(int position) {
            return null;
        }

        @Override
        public Object instantiateItem(ViewGroup container, int position) {
            container.addView(mViewList.get(position));
            return mViewList.get(position);
        }
    };
}
