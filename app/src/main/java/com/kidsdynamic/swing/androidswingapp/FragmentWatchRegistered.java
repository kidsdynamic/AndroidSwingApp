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

public class FragmentWatchRegistered extends Fragment {
    private ActivityMain mActivityMain;
    private View mViewMain;

    private Button mButtonRequest;
    private Button mButtonGuest;
    private Button mButtonContact;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mActivityMain = (ActivityMain) getActivity();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        mViewMain = inflater.inflate(R.layout.fragment_watch_registered, container, false);

        mButtonRequest = (Button) mViewMain.findViewById(R.id.watch_registered_request);
        mButtonRequest.setOnClickListener(mOnRequestListener);

        mButtonGuest = (Button) mViewMain.findViewById(R.id.watch_registered_guest);
        mButtonGuest.setOnClickListener(mOnGuestListener);

        mButtonContact = (Button) mViewMain.findViewById(R.id.watch_registered_contact);
        mButtonContact.setOnClickListener(mOnContactListener);

        return mViewMain;
    }

    private Button.OnClickListener mOnRequestListener = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            mActivityMain.selectFragment(FragmentWatchRequest.class.getName(), null);
        }
    };

    private Button.OnClickListener mOnGuestListener = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
//            mActivityMain.selectFragment(FragmentWatchPurchase.class.getName(), null);
        }
    };

    private Button.OnClickListener mOnContactListener = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
//            mActivityMain.selectFragment(FragmentWatchPurchase.class.getName(), null);
        }
    };
}
