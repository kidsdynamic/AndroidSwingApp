package com.kidsdynamic.swing.androidswingapp;

import android.app.Fragment;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

/**
 * Created by 03543 on 2016/12/30.
 */

public class FragmentSignupWatchHave extends Fragment {
    private MainActivity mMainActivity;
    private View mMainView;

    private Button mButtonYes;
    private Button mButtonNo;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mMainActivity = (MainActivity) getActivity();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        mMainView = inflater.inflate(R.layout.fragment_signup_watch_have, container, false);

        mButtonYes = (Button) mMainView.findViewById(R.id.signup_watch_have_yes);
        mButtonYes.setOnClickListener(mOnYesListener);

        mButtonNo = (Button) mMainView.findViewById(R.id.signup_watch_have_no);
        mButtonNo.setOnClickListener(mOnNoListener);

        return mMainView;
    }

    private Button.OnClickListener mOnYesListener = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            mMainActivity.selectFragment(FragmentSignupWatchBind.class.getName(), null);
        }
    };

    private Button.OnClickListener mOnNoListener = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            mMainActivity.selectFragment(FragmentSignupWatchPurchase.class.getName(), null);
        }
    };
}
