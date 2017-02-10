package com.kidsdynamic.swing.androidswingapp;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;

import java.util.ArrayList;

/**
 * Created by 03543 on 2017/1/30.
 */

public class FragmentProfileRegistered extends ViewFragment {
    private ActivityMain mActivityMain;
    private View mViewMain;

    private Button mButtonRequest;
    private Button mButtonContact;

    private ArrayList<WatchContact.Kid> mKidList;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mActivityMain = (ActivityMain) getActivity();

        mKidList = new ArrayList<>();
        while (!mActivityMain.mContactStack.isEmpty())
            mKidList.add((WatchContact.Kid) mActivityMain.mContactStack.pop());
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        mViewMain = inflater.inflate(R.layout.fragment_profile_registered, container, false);

        mButtonRequest = (Button) mViewMain.findViewById(R.id.profile_registered_request);
        mButtonRequest.setOnClickListener(mOnRequestListener);

        mButtonContact = (Button) mViewMain.findViewById(R.id.profile_registered_contact);
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

    private Button.OnClickListener mOnRequestListener = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            for (WatchContact contact : mKidList)
                mActivityMain.mContactStack.push(contact);

            // Todo : make request
            //mActivityMain.selectFragment(FragmentProfileMain.class.getName(), null);
        }
    };

    private Button.OnClickListener mOnContactListener = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            String url = "http://www.kidsdynamic.com";
            Intent i = new Intent(Intent.ACTION_VIEW);
            i.setData(Uri.parse(url));
            startActivity(i);
        }
    };
}