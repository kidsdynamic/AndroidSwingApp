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

public class FragmentSignupWatchSorry extends Fragment {
    private MainActivity mMainActivity;
    private View mMainView;

    private Button mButtonSearch;
    private Button mButtonRequest;
    private Button mButtonGuest;
    private Button mButtonContact;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mMainActivity = (MainActivity) getActivity();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        mMainView = inflater.inflate(R.layout.fragment_signup_watch_sorry, container, false);

        mButtonSearch = (Button) mMainView.findViewById(R.id.signup_watch_sorry_search);
        mButtonSearch.setOnClickListener(mOnSearchListener);

        mButtonRequest = (Button) mMainView.findViewById(R.id.signup_watch_sorry_request);
        mButtonRequest.setOnClickListener(mOnRequestListener);

        mButtonGuest = (Button) mMainView.findViewById(R.id.signup_watch_sorry_guest);
        mButtonGuest.setOnClickListener(mOnGuestListener);

        mButtonContact = (Button) mMainView.findViewById(R.id.signup_watch_sorry_contact);
        mButtonContact.setOnClickListener(mOnContactListener);

        return mMainView;
    }

    private Button.OnClickListener mOnSearchListener = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            mMainActivity.selectFragment(FragmentSignupWatchSearch.class.getName(), null);
        }
    };

    private Button.OnClickListener mOnRequestListener = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            mMainActivity.selectFragment(FragmentSignupWatchRequest.class.getName(), null);
        }
    };

    private Button.OnClickListener mOnGuestListener = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
//            mMainActivity.selectFragment(FragmentSignupWatchRequest.class.getName(), null);
        }
    };

    private Button.OnClickListener mOnContactListener = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
//            mMainActivity.selectFragment(FragmentSignupWatchRequest.class.getName(), null);
        }
    };
}
