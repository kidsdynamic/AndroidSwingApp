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

public class FragmentSignupWatchRegistered extends Fragment {
    private MainActivity mMainActivity;
    private View mMainView;

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
        mMainView = inflater.inflate(R.layout.fragment_signup_watch_registered, container, false);

        mButtonRequest = (Button) mMainView.findViewById(R.id.signup_watch_registered_request);
        mButtonRequest.setOnClickListener(mOnRequestListener);

        mButtonGuest = (Button) mMainView.findViewById(R.id.signup_watch_registered_guest);
        mButtonGuest.setOnClickListener(mOnGuestListener);

        mButtonContact = (Button) mMainView.findViewById(R.id.signup_watch_registered_contact);
        mButtonContact.setOnClickListener(mOnContactListener);

        return mMainView;
    }

    private Button.OnClickListener mOnRequestListener = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            mMainActivity.selectFragment(FragmentSignupWatchRequest.class.getName(), null);
        }
    };

    private Button.OnClickListener mOnGuestListener = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
//            mMainActivity.selectFragment(FragmentSignupWatchPurchase.class.getName(), null);
        }
    };

    private Button.OnClickListener mOnContactListener = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
//            mMainActivity.selectFragment(FragmentSignupWatchPurchase.class.getName(), null);
        }
    };
}
