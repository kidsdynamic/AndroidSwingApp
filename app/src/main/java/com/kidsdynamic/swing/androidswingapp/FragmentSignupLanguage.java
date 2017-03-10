package com.kidsdynamic.swing.androidswingapp;

import android.graphics.Typeface;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

/**
 * Created by 03543 on 2016/12/30.
 */

public class FragmentSignupLanguage extends ViewFragment {
    private ActivityMain mActivityMain;
    private View mViewMain;

    private LinearLayout mViewList;
    private Button mViewSelect;

    private List<Where> mWhereList;
    private Where mWhere;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mActivityMain = (ActivityMain) getActivity();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        mViewMain = inflater.inflate(R.layout.fragment_signup_language, container, false);

        mViewList = (LinearLayout) mViewMain.findViewById(R.id.signup_language_list);

        mViewSelect = (Button) mViewMain.findViewById(R.id.signup_language_select);
        mViewSelect.setOnClickListener(mSelectListener);

        mWhereList = new ArrayList<>();
        mWhereList.add(new Where(getResources().getString(R.string.language_en_us), "en", "US"));
        mWhereList.add(new Where(getResources().getString(R.string.language_zh_tw), "zh", "TW"));
        mWhereList.add(new Where(getResources().getString(R.string.language_zh_cn), "zh", "CN"));
        mWhereList.add(new Where(getResources().getString(R.string.language_ru), "ru", ""));
        mWhereList.add(new Where(getResources().getString(R.string.language_es), "es", ""));

        for (Where where : mWhereList)
            addWhere(where);

        return mViewMain;
    }

    @Override
    public ViewFragment.ViewFragmentConfig getConfig() {
        return new ViewFragment.ViewFragmentConfig(
                "", false, false, false,
                ActivityMain.RESOURCE_IGNORE, ActivityMain.RESOURCE_IGNORE, ActivityMain.RESOURCE_IGNORE);
    }

    @Override
    public void onResume() {
        super.onResume();

        Locale locale = Locale.getDefault();
        selWhere(locale.getLanguage(), locale.getCountry());
    }

    private void addWhere(Where where) {
        TextView view = new TextView(mActivityMain);

        view.setTextColor(ContextCompat.getColor(mActivityMain, R.color.color_gray_deep));
        view.setTextSize(TypedValue.COMPLEX_UNIT_SP, 24);
        view.setTypeface(view.getTypeface(), Typeface.BOLD);
        view.setGravity(Gravity.CENTER);
        view.setPadding(0, 15, 0, 15);

        view.setTag(where);
        view.setText(where.mName);
        view.setOnClickListener(mWhereListener);

        LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);

        mViewList.addView(view, layoutParams);
    }

    private void selWhere(String language, String region) {
        Where target = mWhereList.get(0);

        for (Where where : mWhereList) {
            if (where.mLanguage.equals(language) && where.mRegion.equals(region)) {
                target = where;
            }
        }

        selWhere(target);
    }

    private void selWhere(Where where) {
        mWhere = where;

        int count = mViewList.getChildCount();
        for (int idx = 0; idx < count; idx++) {
            TextView view = (TextView) mViewList.getChildAt(idx);
            Where tag = (Where) view.getTag();

            view.setTextColor(tag == where ?
                    ContextCompat.getColor(mActivityMain, R.color.color_orange_main) :
                    ContextCompat.getColor(mActivityMain, R.color.color_gray_main));
        }
    }

    private View.OnClickListener mWhereListener = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            selWhere((Where) view.getTag());
        }
    };

    private View.OnClickListener mSelectListener = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            mActivityMain.mConfig.setString(ActivityConfig.KEY_LANGUAGE, mWhere.mLanguage);
            mActivityMain.mConfig.setString(ActivityConfig.KEY_REGION, mWhere.mRegion);

            mActivityMain.setLocale(mWhere.mLanguage, mWhere.mRegion);
            mActivityMain.selectFragment(FragmentSignupLogin.class.getName(), null);
        }
    };

    final class Where {
        String mName;
        String mLanguage;
        String mRegion;

        Where(String name, String language, String region) {
            mName = name;
            mLanguage = language;
            mRegion = region;
        }
    }
}
