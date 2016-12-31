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

public class FragmentSignupLanguage extends Fragment {
    private MainActivity mMainActivity;
    private View mMainView;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mMainActivity = (MainActivity) getActivity();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        mMainView = inflater.inflate(R.layout.fragment_signup_language, container, false);

        Button button = (Button) mMainView.findViewById(R.id.signup_language_select);
        button.setOnClickListener(mOnButtonListener);

        return mMainView;
    }

    private Button.OnClickListener mOnButtonListener = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            // todo: Save default or language
            mMainActivity.mConfig.setString(Config.KEY_LANGUAGE, "Default");

            mMainActivity.selectFragment(FragmentSignupLogin.class.getName(), null);
        }
    };
}
