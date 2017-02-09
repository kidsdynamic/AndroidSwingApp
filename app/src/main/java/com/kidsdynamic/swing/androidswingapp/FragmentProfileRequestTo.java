package com.kidsdynamic.swing.androidswingapp;

import android.content.Context;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

/**
 * Created by 03543 on 2017/1/28.
 */

public class FragmentProfileRequestTo extends ViewFragment {

    private ActivityMain mActivityMain;
    private View mViewMain;

    private EditText mViewMail;
    private LinearLayout mViewUserContainer;
    private LinearLayout mViewPendingContainer;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mActivityMain = (ActivityMain) getActivity();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        mViewMain = inflater.inflate(R.layout.fragment_profile_request_to, container, false);

        mViewMail = (EditText) mViewMain.findViewById(R.id.profile_request_to_mail);
        mViewMail.setOnEditorActionListener(mMailActionListener);

        mViewUserContainer = (LinearLayout) mViewMain.findViewById(R.id.profile_request_to_user_container);
        mViewPendingContainer = (LinearLayout) mViewMain.findViewById(R.id.profile_request_to_pending_container);

        // Test
        addPending(new WatchContact.Kid(BitmapFactory.decodeResource(getResources(), R.mipmap.monster_yellow), "Yellow Monster"));
        addPending(new WatchContact.Kid(BitmapFactory.decodeResource(getResources(), R.mipmap.monster_green), "Green Monster"));
        addPending(new WatchContact.Kid(BitmapFactory.decodeResource(getResources(), R.mipmap.monster_purple), "Purple Monster"));
        ////////////

        for (WatchContact.Kid kid : mActivityMain.mOperator.getRequestToKidList(null))
            addPending(kid);

        return mViewMain;
    }

    @Override
    public ViewFragmentConfig getConfig() {
        return new ViewFragmentConfig("Send Requests To", true, true, false,
                ActivityMain.RESOURCE_IGNORE, R.mipmap.icon_left, ActivityMain.RESOURCE_HIDE);
    }

    @Override
    public void onToolbarAction1() {
        mActivityMain.popFragment();
    }

    public void addUser(WatchContact.User person) {
        View view = WatchContact.inflateButton(mActivityMain, person, "Send");

        View button = view.findViewById(R.id.watch_contact_button_button);
        button.setTag(person);
        button.setOnClickListener(mUserListener);

        mViewUserContainer.addView(view);
    }

    public void addPending(WatchContact.Kid device) {
        View view = WatchContact.inflateButton(mActivityMain, device, "Cancel");

        View button = view.findViewById(R.id.watch_contact_button_button);
        button.setTag(device);
        button.setOnClickListener(mPendingListener);

        mViewPendingContainer.addView(view);
    }

    private EditText.OnEditorActionListener mMailActionListener = new TextView.OnEditorActionListener() {
        @Override
        public boolean onEditorAction(TextView view, int actionId, KeyEvent event) {
            if (actionId != EditorInfo.IME_ACTION_DONE)
                return false;

            mViewUserContainer.removeAllViews();

            InputMethodManager imm = (InputMethodManager) mActivityMain.getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(mViewMail.getWindowToken(), 0);

            String mail = mViewMail.getText().toString();

            // todo: kickoff search for mail.

            // Test
            addUser(new WatchContact.User(BitmapFactory.decodeResource(getResources(), R.mipmap.monster_purple), mail));
            ///////

            return true;
        }
    };

    private View.OnClickListener mUserListener = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            WatchContact.User person = (WatchContact.User) view.getTag();

            Bundle bundle = new Bundle();
            bundle.putSerializable(BUNDLE_KEY_CONTACT, person);

            mActivityMain.selectFragment(FragmentProfileShare.class.getName(), bundle);
        }
    };

    private View.OnClickListener mPendingListener = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            WatchContact kid = (WatchContact) view.getTag();

            View label = mViewPendingContainer.findViewWithTag(view.getTag());
            mViewPendingContainer.removeView(label);

            Log.d("xxx", "Remove pending request: " + kid.mLabel);
            // todo: Remove request from Database or API
        }
    };

}
