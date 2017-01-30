package com.kidsdynamic.swing.androidswingapp;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;

import java.util.ArrayList;

/**
 * Created by 03543 on 2017/1/30.
 */

public class FragmentProfileSorry extends ViewFragment {
    private ActivityMain mActivityMain;
    private View mViewMain;

    private Button mButtonSearch;
    private Button mButtonRequest;
    private Button mButtonContact;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mActivityMain = (ActivityMain) getActivity();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        mViewMain = inflater.inflate(R.layout.fragment_profile_sorry, container, false);

        mButtonSearch = (Button) mViewMain.findViewById(R.id.profile_sorry_search);
        mButtonSearch.setOnClickListener(mOnSearchListener);

        mButtonRequest = (Button) mViewMain.findViewById(R.id.profile_sorry_request);
        mButtonRequest.setOnClickListener(mOnRequestListener);

        mButtonContact = (Button) mViewMain.findViewById(R.id.profile_sorry_contact);
        mButtonContact.setOnClickListener(mOnContactListener);

        return mViewMain;
    }

    @Override
    public ViewFragmentConfig getConfig() {
        return new ViewFragmentConfig("Search Device", true, true, false,
                ActivityMain.RESOURCE_IGNORE, R.mipmap.icon_left, ActivityMain.RESOURCE_HIDE);
    }

    @Override
    public void onToolbarAction1() {
        mActivityMain.popFragment();
    }

    private Button.OnClickListener mOnSearchListener = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            onToolbarAction1();
        }
    };

    private Button.OnClickListener mOnRequestListener = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            // todo: intend to request by email
        }
    };

    private Button.OnClickListener mOnContactListener = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            // todo: intend browser to access www.kidsdynamic.com
        }
    };
}
