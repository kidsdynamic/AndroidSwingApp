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

public class FragmentSignupCaregiver extends Fragment {
    private MainActivity mMainActivity;
    private View mMainView;

    private Button mButtonParent;
    private Button mButtonNanny;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mMainActivity = (MainActivity) getActivity();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        mMainView = inflater.inflate(R.layout.fragment_signup_caregiver, container, false);

        mButtonParent = (Button) mMainView.findViewById(R.id.signup_caregiver_parent);
        mButtonParent.setOnClickListener(mOnButtonListener);

        mButtonNanny = (Button) mMainView.findViewById(R.id.signup_caregiver_nanny);
        mButtonNanny.setOnClickListener(mOnButtonListener);

        return mMainView;
    }

    private Button.OnClickListener mOnButtonListener = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            if (view == mButtonParent) {

            } else if (view == mButtonNanny) {

            }

            mMainActivity.selectFragment(FragmentSignupLogin.class.getName(), null);
        }
    };
}
