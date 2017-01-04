package com.kidsdynamic.swing.androidswingapp;

import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;

/**
 * Created by 03543 on 2016/12/31.
 */

public class FragmentWatchSelect extends ViewFragment {
    private ActivityMain mActivityMain;
    private View mViewMain;

    private Button mButtonDashboard;
    private ViewContactList mViewContactList;
    private ImageView mViewBack;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mActivityMain = (ActivityMain) getActivity();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        mViewMain = inflater.inflate(R.layout.fragment_watch_select, container, false);

        mViewContactList = (ViewContactList) mViewMain.findViewById(R.id.watch_select_list);
        mViewContactList.addItem(new ContactItem(null, "Label1"));
        mViewContactList.addItem(new ContactItem.BindItem(null, "Label2", true));
        mViewContactList.addItem(new ContactItem.BindItem(null, "Label3", false));
        mViewContactList.addItem(new ContactItem.AddItem(null, "Label4"));
        mViewContactList.setOnButtonClickListener(mButtonClickListener);

        mViewBack = (ImageView) mViewMain.findViewById(R.id.fragment_back);
        mViewBack.setOnClickListener(mBackOnClickListener);

        mButtonDashboard = (Button) mViewMain.findViewById(R.id.watch_select_dashboard);
        mButtonDashboard.setOnClickListener(mOnDashboardListener);

        return mViewMain;
    }

    @Override
    public ViewFragmentConfig getConfig() {
        return new ViewFragmentConfig("Watch", false, false,
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

    private Button.OnClickListener mOnDashboardListener = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
//            mActivityMain.selectFragment(FragmentSignupLogin.class.getName(), null);
        }
    };

    private ViewContactList.OnButtonClickListener mButtonClickListener = new ViewContactList.OnButtonClickListener() {
        @Override
        public void onClick(ViewContact contact, int position, int button) {

            Log.d("xxx", "Position:" + position + " Button:" + button);

            ContactItem.BindItem item = (ContactItem.BindItem)contact.getItem();

            if (!item.mBound)
                mActivityMain.selectFragment(FragmentWatchAdded.class.getName(), null);
            else
                mActivityMain.selectFragment(FragmentWatchRegistered.class.getName(), null);
        }
    };
}
