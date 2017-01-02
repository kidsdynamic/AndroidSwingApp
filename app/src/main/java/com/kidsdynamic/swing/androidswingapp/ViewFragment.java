package com.kidsdynamic.swing.androidswingapp;

import android.app.Fragment;
import android.view.View;

/**
 * Created by 03543 on 2017/1/2.
 */

abstract public class ViewFragment extends Fragment {

    @Override
    public void onResume() {
        super.onResume();

        ViewFragmentConfig config = getConfig();

        ActivityMain activity = (ActivityMain) getActivity();

        activity.toolbarSetTitle(config.mTitle);
        activity.toolbarShow(config.mShowToolbar);
        activity.consoleShow(config.mShowConsole);
        activity.backgroundSet(config.mBackgound);
        activity.toolbarSetIcon1(config.mIcon1);
        activity.toolbarSetIcon2(config.mIcon2);
    }

    public void onToolbarAction1() {
    }

    public void onToolbarAction2() {
    }

    class ViewFragmentConfig {
        public String mTitle;
        public boolean mShowToolbar;
        public boolean mShowConsole;
        public int mBackgound;
        public int mIcon1;
        public int mIcon2;

        public ViewFragmentConfig(String title, boolean showToolbar, boolean showConsole,
                                  int background, int icon1, int icon2) {
            init(title, showToolbar, showConsole, background, icon1, icon2);
        }

        private void init(String title, boolean showToolbar, boolean showConsole,
                          int background, int icon1, int icon2) {
            mTitle = title;
            mShowConsole = showConsole;
            mShowToolbar = showToolbar;
            mBackgound = background;
            mIcon1 = icon1;
            mIcon2 = icon2;
        }
    }

    abstract public ViewFragmentConfig getConfig();
}
