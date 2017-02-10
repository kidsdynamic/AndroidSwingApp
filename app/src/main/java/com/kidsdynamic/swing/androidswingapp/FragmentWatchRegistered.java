package com.kidsdynamic.swing.androidswingapp;

import android.app.Fragment;
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
 * Created by 03543 on 2016/12/31.
 */

public class FragmentWatchRegistered extends ViewFragment {
    private ActivityMain mActivityMain;
    private View mViewMain;

    private Button mButtonRequest;
    private Button mButtonContact;
    private ImageView mViewBack;

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
        mViewMain = inflater.inflate(R.layout.fragment_watch_registered, container, false);

        mButtonRequest = (Button) mViewMain.findViewById(R.id.watch_registered_request);
        mButtonRequest.setOnClickListener(mOnRequestListener);

        mButtonContact = (Button) mViewMain.findViewById(R.id.watch_registered_contact);
        mButtonContact.setOnClickListener(mOnContactListener);

        mViewBack = (ImageView) mViewMain.findViewById(R.id.fragment_back);
        mViewBack.setOnClickListener(mBackOnClickListener);

        return mViewMain;
    }

    @Override
    public ViewFragmentConfig getConfig() {
        return new ViewFragmentConfig("Watch", false, false, false,
                ActivityMain.RESOURCE_IGNORE, ActivityMain.RESOURCE_IGNORE, ActivityMain.RESOURCE_IGNORE);
    }

    @Override
    public void onToolbarAction1() {
        mActivityMain.popFragment();
    }

    private View.OnClickListener mBackOnClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            onToolbarAction1();
        }
    };

    private Button.OnClickListener mOnRequestListener = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            for (WatchContact contact : mKidList)
                mActivityMain.mContactStack.push(contact);

            mActivityMain.selectFragment(FragmentWatchRequest.class.getName(), null);
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
