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

public class FragmentSignupWatchAdded extends Fragment {
    private MainActivity mMainActivity;
    private View mMainView;

    private Button mButtonProfile;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mMainActivity = (MainActivity) getActivity();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        mMainView = inflater.inflate(R.layout.fragment_signup_watch_added, container, false);

        mButtonProfile = (Button) mMainView.findViewById(R.id.signup_watch_added_profile);
        mButtonProfile.setOnClickListener(mOnProfileListener);

        return mMainView;
    }

    private Button.OnClickListener mOnProfileListener = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
//            mMainActivity.selectFragment(FragmentSignupWatchOwner.class.getName(), null);
        }
    };

}
