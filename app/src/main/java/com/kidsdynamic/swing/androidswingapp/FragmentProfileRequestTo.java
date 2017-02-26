package com.kidsdynamic.swing.androidswingapp;

import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.graphics.Bitmap;
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
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by 03543 on 2017/1/28.
 */

public class FragmentProfileRequestTo extends ViewFragment {

    private ActivityMain mActivityMain;
    private View mViewMain;

    private EditText mViewMail;
    private LinearLayout mViewUserContainer;
    private LinearLayout mViewPendingContainer;
    private Dialog mProcessDialog = null;

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

        return mViewMain;
    }

    @Override
    public void onResume() {
        super.onResume();

        List<WatchContact.User> list = mActivityMain.mOperator.getRequestToList();
        for (WatchContact.User user : list) {
            if (user.mRequestStatus.equals("PENDING")) {
                addPending(user);
            }
        }
    }

    @Override
    public void onPause() {
        if (mProcessDialog != null)
            mProcessDialog.dismiss();
        super.onPause();
    }

    @Override
    public ViewFragmentConfig getConfig() {
        return new ViewFragmentConfig(
                getResources().getString(R.string.title_request_to), true, true, false,
                ActivityMain.RESOURCE_IGNORE, R.mipmap.icon_left, ActivityMain.RESOURCE_HIDE);
    }

    @Override
    public void onToolbarAction1() {
        mActivityMain.popFragment();
    }

    public void addUser(WatchContact.User person) {
        View view = WatchContact.inflateButton(mActivityMain, person, getResources().getString(R.string.profile_request_to_send));

        View button = view.findViewById(R.id.watch_contact_button_button);
        button.setTag(person);
        button.setOnClickListener(mUserListener);

        mViewUserContainer.addView(view);
    }

    public void addPending(WatchContact.User device) {
        View view = WatchContact.inflateButton(mActivityMain, device, getResources().getString(R.string.profile_request_to_cancel));

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

            if (!mail.equals("")) {
                mProcessDialog = ProgressDialog.show(mActivityMain,
                        getResources().getString(R.string.profile_request_to_processing),
                        getResources().getString(R.string.profile_request_to_wait), true);
                mActivityMain.mOperator.requestToSubHost(mAddRequestToListener, mail);
            }

            return true;
        }
    };

    WatchOperator.finishListener mAddRequestToListener = new WatchOperator.finishListener() {
        @Override
        public void onFinish(String msg, Object arg) {
            WatchContact.User user = (WatchContact.User)arg;
            mProcessDialog.dismiss();
            if (!msg.equals(""))
                Toast.makeText(mActivityMain, msg, Toast.LENGTH_SHORT).show();
            if (user != null)
                addPending(user);
        }

        @Override
        public void onFailed(String Command, int statusCode) {

        }
    };

    private View.OnClickListener mUserListener = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            WatchContact.User person = (WatchContact.User) view.getTag();

            mActivityMain.mContactStack.push(person);
            mActivityMain.selectFragment(FragmentProfileShare.class.getName(), null);
        }
    };

    private View.OnClickListener mPendingListener = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            WatchContact kid = (WatchContact) view.getTag();

            View label = mViewPendingContainer.findViewWithTag(view.getTag());
            mViewPendingContainer.removeView(label);

            // todo: Remove request from Database or API
        }
    };

}
