package com.kidsdynamic.swing.androidswingapp;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;

/**
 * Created by 03543 on 2017/1/24.
 */

public class FragmentProfileEditor extends ViewFragment {

    private ActivityMain mActivityMain;
    private View mViewMain;

    private ViewPhoto mViewPhoto;
    private EditText mViewFirst;
    private EditText mViewLast;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mActivityMain = (ActivityMain) getActivity();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        mViewMain = inflater.inflate(R.layout.fragment_profile_editor, container, false);

        mViewPhoto = (ViewPhoto) mViewMain.findViewById(R.id.profile_editor_photo);

        mViewFirst = (EditText)
                mViewMain.findViewById(R.id.profile_editor_first).
                        findViewById(R.id.view_text_editor_textedit);

        return mViewMain;
    }

    @Override
    public ViewFragmentConfig getConfig() {
        return new ViewFragmentConfig("Profile", true, true, true,
                ActivityMain.RESOURCE_IGNORE, R.mipmap.icon_left, ActivityMain.RESOURCE_HIDE);
    }

    @Override
    public void onToolbarAction1() {
        mActivityMain.popFragment();
    }

}
