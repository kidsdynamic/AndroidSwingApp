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

public class FragmentWatchAdded extends Fragment {
    private ActivityMain mActivityMain;
    private View mViewMain;

    private Button mButtonProfile;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mActivityMain = (ActivityMain) getActivity();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        mViewMain = inflater.inflate(R.layout.fragment_watch_added, container, false);

        mButtonProfile = (Button) mViewMain.findViewById(R.id.watch_added_profile);
        mButtonProfile.setOnClickListener(mOnProfileListener);

        return mViewMain;
    }

    private Button.OnClickListener mOnProfileListener = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
//            mActivityMain.selectFragment(FragmentWatchOwner.class.getName(), null);
        }
    };

}
