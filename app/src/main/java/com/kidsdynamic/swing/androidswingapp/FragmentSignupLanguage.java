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
    private ActivityMain mActivityMain;
    private View mViewMain;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mActivityMain = (ActivityMain) getActivity();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        mViewMain = inflater.inflate(R.layout.fragment_signup_language, container, false);

        Button button = (Button) mViewMain.findViewById(R.id.signup_language_select);
        button.setOnClickListener(mOnButtonListener);

        return mViewMain;
    }

    private Button.OnClickListener mOnButtonListener = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            // todo: Save default or language
            mActivityMain.mConfig.setString(Config.KEY_LANGUAGE, "Default");

            mActivityMain.selectFragment(FragmentSignupLogin.class.getName(), null);
        }
    };
}
