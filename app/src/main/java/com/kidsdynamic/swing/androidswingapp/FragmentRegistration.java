package com.kidsdynamic.swing.androidswingapp;

import android.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

/**
 * Created by 03543 on 2016/12/19.
 */

public class FragmentRegistration extends Fragment {
    private MainActivity mMainActivity;
    private View mMainView;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mMainActivity = (MainActivity) getActivity();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        mMainView = inflater.inflate(R.layout.fragment_registration, container, false);

        mMainView.findViewById(R.id.registration_label).setOnClickListener(mLabelClickListener);

        return mMainView;
    }

    View.OnClickListener mLabelClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            mMainActivity.showControl(true);
            mMainActivity.selectControl(null);
        }
    };
}
