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

public class FragmentWatchSelect extends Fragment {
    private MainActivity mMainActivity;
    private View mMainView;

    private Button mButtonDashboard;
    private ContactLabelView mContactLabelView1;
    private ContactLabelView mContactLabelView2;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mMainActivity = (MainActivity) getActivity();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        mMainView = inflater.inflate(R.layout.fragment_watch_select, container, false);

        mContactLabelView1 = (ContactLabelView) mMainView.findViewById(R.id.watch_select_contact1);
        mContactLabelView1.setOnClickListener(mOnContactListener);

        mContactLabelView2 = (ContactLabelView) mMainView.findViewById(R.id.watch_select_contact2);
        mContactLabelView2.setOnClickListener(mOnContactListener);

        mButtonDashboard = (Button) mMainView.findViewById(R.id.watch_select_dashboard);
        mButtonDashboard.setOnClickListener(mOnDashboardListener);

        return mMainView;
    }

    private View.OnClickListener mOnContactListener = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            if (view == mContactLabelView1)
                mMainActivity.selectFragment(FragmentWatchAdded.class.getName(), null);
            else
                mMainActivity.selectFragment(FragmentWatchRegistered.class.getName(), null);
        }
    };

    private Button.OnClickListener mOnDashboardListener = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
//            mMainActivity.selectFragment(FragmentSignupLogin.class.getName(), null);
        }
    };
}
