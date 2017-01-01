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

public class FragmentWatchHave extends Fragment {
    private ActivityMain mActivityMain;
    private View mMainView;

    private Button mButtonYes;
    private Button mButtonNo;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mActivityMain = (ActivityMain) getActivity();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        mMainView = inflater.inflate(R.layout.fragment_watch_have, container, false);

        mButtonYes = (Button) mMainView.findViewById(R.id.watch_have_yes);
        mButtonYes.setOnClickListener(mOnYesListener);

        mButtonNo = (Button) mMainView.findViewById(R.id.watch_have_no);
        mButtonNo.setOnClickListener(mOnNoListener);

        return mMainView;
    }

    private Button.OnClickListener mOnYesListener = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            mActivityMain.selectFragment(FragmentWatchOwner.class.getName(), null);
        }
    };

    private Button.OnClickListener mOnNoListener = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            mActivityMain.selectFragment(FragmentWatchPurchase.class.getName(), null);
        }
    };
}
