package com.kidsdynamic.swing.androidswingapp;

import android.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

/**
 * Created by 03543 on 2016/12/30.
 */

public class FragmentSignupWatchBind extends Fragment {
    private MainActivity mMainActivity;
    private View mMainView;

    private Button mButtonSearch;
    private Button mButtonOthers;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mMainActivity = (MainActivity) getActivity();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        mMainView = inflater.inflate(R.layout.fragment_signup_watch_bind, container, false);

        mButtonSearch = (Button) mMainView.findViewById(R.id.signup_watch_bind_search);
        mButtonSearch.setOnClickListener(mOnSearchListener);

        mButtonOthers = (Button) mMainView.findViewById(R.id.signup_watch_bind_others);
        mButtonOthers.setOnClickListener(mOnOthersListener);

        return mMainView;
    }

    private Button.OnClickListener mOnSearchListener = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
//            mMainActivity.selectFragment(FragmentSignupLogin.class.getName(), null);
        }
    };

    private Button.OnClickListener mOnOthersListener = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
//            mMainActivity.selectFragment(FragmentSignupLogin.class.getName(), null);
        }
    };
}
