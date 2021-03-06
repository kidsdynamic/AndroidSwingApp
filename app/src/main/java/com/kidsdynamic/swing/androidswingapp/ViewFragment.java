package com.kidsdynamic.swing.androidswingapp;

import android.app.Fragment;
import android.view.View;

/**
 * Created by 03543 on 2017/1/2.
 */

abstract public class ViewFragment extends Fragment {

    final static String BUNDLE_KEY_MAIL = "MAIL";
    final static String BUNDLE_KEY_PASSWORD = "PASSWORD";
    final static String BUNDLE_KEY_AVATAR = "AVATAR";
    final static String BUNDLE_KEY_KID_NAME = "KID_NAME";
    final static String BUNDLE_KEY_DATE = "DATE";
    final static String BUNDLE_KEY_START_DATE = "START_DATE";

    @Override
    public void onResume() {
        super.onResume();

        ViewFragmentConfig config = getConfig();

        ActivityMain activity = (ActivityMain) getActivity();

        activity.toolbarSetTitle(config.mTitle, config.mEnableTitleClick);
        activity.toolbarShow(config.mShowToolbar);
        activity.consoleShow(config.mShowConsole);
        activity.backgroundSet(config.mBackgound);
        activity.toolbarSetIcon1(config.mIcon1);
        activity.toolbarSetIcon2(config.mIcon2);
    }

    // 左上角按鍵事件
    public void onToolbarAction1() {
    }

    // 右上角按鍵事件
    public void onToolbarAction2() {
    }

    // 正上方抬頭文字觸控事件
    public void onToolbarTitle() {
    }

    class ViewFragmentConfig {
        public String mTitle;
        public boolean mShowToolbar;
        public boolean mShowConsole;
        public boolean mEnableTitleClick;
        public int mBackgound;
        public int mIcon1;
        public int mIcon2;

        public ViewFragmentConfig(String title, boolean showToolbar, boolean showConsole, boolean enableTitleClick,
                                  int background, int icon1, int icon2) {
            init(title, showToolbar, showConsole, enableTitleClick, background, icon1, icon2);
        }

        private void init(String title, boolean showToolbar, boolean showConsole, boolean enableTitleClick,
                          int background, int icon1, int icon2) {
            mTitle = title;
            mShowConsole = showConsole;
            mShowToolbar = showToolbar;
            mEnableTitleClick = enableTitleClick;
            mBackgound = background;
            mIcon1 = icon1;
            mIcon2 = icon2;
        }
    }

    abstract public ViewFragmentConfig getConfig();
}
