package com.kidsdynamic.swing.androidswingapp;

import android.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

/**
 * Created by 03543 on 2016/12/31.
 */

public class FragmentSignupWatchSelect extends Fragment {
    private MainActivity mMainActivity;
    private View mMainView;

    private Button mButtonDashboard;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mMainActivity = (MainActivity) getActivity();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        mMainView = inflater.inflate(R.layout.fragment_signup_watch_select, container, false);

        mButtonDashboard = (Button) mMainView.findViewById(R.id.signup_watch_select_dashboard);
        mButtonDashboard.setOnClickListener(mOnDashboardListener);

        return mMainView;
    }

    private Button.OnClickListener mOnDashboardListener = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
//            mMainActivity.selectFragment(FragmentSignupLogin.class.getName(), null);
        }
    };
}
