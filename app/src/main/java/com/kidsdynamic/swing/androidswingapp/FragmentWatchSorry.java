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

public class FragmentWatchSorry extends ViewFragment {
    private ActivityMain mActivityMain;
    private View mViewMain;

    private Button mButtonSearch;
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
        mViewMain = inflater.inflate(R.layout.fragment_watch_sorry, container, false);

        mButtonSearch = (Button) mViewMain.findViewById(R.id.watch_sorry_search);
        mButtonSearch.setOnClickListener(mOnSearchListener);

        mButtonRequest = (Button) mViewMain.findViewById(R.id.watch_sorry_request);
        mButtonRequest.setOnClickListener(mOnRequestListener);

        mButtonGuest = (Button) mViewMain.findViewById(R.id.watch_sorry_guest);
        mButtonGuest.setOnClickListener(mOnGuestListener);

        mButtonContact = (Button) mViewMain.findViewById(R.id.watch_sorry_contact);
        mButtonContact.setOnClickListener(mOnContactListener);

        return mViewMain;
    }

    @Override
    public ViewFragmentConfig getConfig() {
        return new ViewFragmentConfig("Watch", false, false);
    }

    private Button.OnClickListener mOnSearchListener = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            mActivityMain.selectFragment(FragmentWatchSearch.class.getName(), null);
        }
    };

    private Button.OnClickListener mOnRequestListener = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            mActivityMain.selectFragment(FragmentWatchRequest.class.getName(), null);
        }
    };

    private Button.OnClickListener mOnGuestListener = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
//            mActivityMain.selectFragment(FragmentWatchRequest.class.getName(), null);
        }
    };

    private Button.OnClickListener mOnContactListener = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
//            mActivityMain.selectFragment(FragmentWatchRequest.class.getName(), null);
        }
    };
}
