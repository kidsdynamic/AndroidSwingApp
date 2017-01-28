package com.kidsdynamic.swing.androidswingapp;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.TextView;

/**
 * Created by 03543 on 2017/1/28.
 */

public class FragmentProfileRequest extends ViewFragment {

    private ActivityMain mActivityMain;
    private View mViewMain;

    private EditText mViewMail;
    private TextView mViewCount;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mActivityMain = (ActivityMain) getActivity();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        mViewMain = inflater.inflate(R.layout.fragment_profile_request, container, false);

        mViewMail = (EditText) mViewMain.findViewById(R.id.profile_request_mail);

        mViewCount = (TextView) mViewMain.findViewById(R.id.profile_request_count);

        return mViewMain;
    }

    @Override
    public ViewFragmentConfig getConfig() {
        return new ViewFragmentConfig("Request", true, true, false,
                ActivityMain.RESOURCE_IGNORE, R.mipmap.icon_left, ActivityMain.RESOURCE_HIDE);
    }

    @Override
    public void onToolbarAction1() {
        mActivityMain.popFragment();
    }

    public void addRequester(WatchContact.Person person) {

    }
}
