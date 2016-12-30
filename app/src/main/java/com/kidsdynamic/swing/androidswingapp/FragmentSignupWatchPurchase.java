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

public class FragmentSignupWatchPurchase extends Fragment {
    private MainActivity mMainActivity;
    private View mMainView;

    private Button mButtonYes;
    private Button mButtonRequest;
    private Button mButtonGuest;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mMainActivity = (MainActivity) getActivity();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        mMainView = inflater.inflate(R.layout.fragment_signup_watch_purchase, container, false);

        mButtonYes = (Button) mMainView.findViewById(R.id.signup_watch_purchase_yes);
        mButtonYes.setOnClickListener(mOnYesListener);

        mButtonRequest = (Button) mMainView.findViewById(R.id.signup_watch_purchase_request);
        mButtonRequest.setOnClickListener(mOnRequestListener);

        mButtonGuest = (Button) mMainView.findViewById(R.id.signup_watch_purchase_guest);
        mButtonGuest.setOnClickListener(mOnGuestListener);

        return mMainView;
    }

    private Button.OnClickListener mOnYesListener = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
//            mMainActivity.selectFragment(FragmentSignupWatchBind.class.getName(), null);
        }
    };

    private Button.OnClickListener mOnRequestListener = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            mMainActivity.selectFragment(FragmentSignupWatchOwner.class.getName(), null);
        }
    };

    private Button.OnClickListener mOnGuestListener = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
//            mMainActivity.selectFragment(FragmentSignupWatchBind.class.getName(), null);
        }
    };

}
