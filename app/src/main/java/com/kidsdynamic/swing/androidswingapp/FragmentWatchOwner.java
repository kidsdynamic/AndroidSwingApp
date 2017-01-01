package com.kidsdynamic.swing.androidswingapp;

import android.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

/**
 * Created by 03543 on 2016/12/30.
 */

public class FragmentWatchOwner extends Fragment {
    private ActivityMain mActivityMain;
    private View mViewMain;

    private Button mButtonSearch;
    private Button mButtonOthers;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mActivityMain = (ActivityMain) getActivity();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        mViewMain = inflater.inflate(R.layout.fragment_watch_owner, container, false);

        mButtonSearch = (Button) mViewMain.findViewById(R.id.watch_owner_search);
        mButtonSearch.setOnClickListener(mOnSearchListener);

        mButtonOthers = (Button) mViewMain.findViewById(R.id.watch_owner_others);
        mButtonOthers.setOnClickListener(mOnOthersListener);

        return mViewMain;
    }

    private Button.OnClickListener mOnSearchListener = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            mActivityMain.selectFragment(FragmentWatchSearch.class.getName(), null);
        }
    };

    private Button.OnClickListener mOnOthersListener = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            mActivityMain.selectFragment(FragmentWatchRequest.class.getName(), null);
        }
    };
}
