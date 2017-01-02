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

        ActivityMain activity = (ActivityMain)getActivity();
        activity.toolbarSetTitle(config.mTitle);
        activity.toolbarShow(config.mShowToolbar);
        activity.consoleShow(config.mShowConsole);
    }

    public void onToolbarAction1() {}
    public void onToolbarAction2() {}

    class ViewFragmentConfig {
        public String mTitle;
        public boolean mShowToolbar;
        public boolean mShowConsole;

        public ViewFragmentConfig(String title, boolean showToolbar, boolean showConsole) {
            init(title, showToolbar, showConsole);
        }

        private void init(String title, boolean showToolbar, boolean showConsole) {
            mTitle = title;
            mShowConsole = showConsole;
            mShowToolbar = showToolbar;
        }
    }
    abstract public ViewFragmentConfig getConfig();
}
