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
    private List<WatchContact.User> mRequestedUserList;


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
        //addPending(new WatchContact.User(BitmapFactory.decodeResource(getResources(), R.mipmap.monster_yellow), "Yellow Monster"));
        //addPending(new WatchContact.User(BitmapFactory.decodeResource(getResources(), R.mipmap.monster_green), "Green Monster"));
        //addPending(new WatchContact.User(BitmapFactory.decodeResource(getResources(), R.mipmap.monster_purple), "Purple Monster"));
        ////////////

        for (WatchContact.User user : mActivityMain.mOperator.getRequestToList())
            addPending(user);

        return mViewMain;
    }

    @Override
    public void onResume() {
        super.onResume();
        mProcessDialog = ProgressDialog.show(mActivityMain, "Processing", "Please wait...", true);

        mActivityMain.mServiceMachine.subHostList(mSubHostListListener, "PENDING");
    }

    @Override
    public void onPause() {
        if (mProcessDialog != null)
            mProcessDialog.dismiss();
        super.onPause();
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

    ServerMachine.subHostListListener mSubHostListListener = new ServerMachine.subHostListListener() {
        @Override
        public void onSuccess(int statusCode, List<ServerGson.hostData> response) {
            mRequestedUserList = new ArrayList<>();
            if (response!=null && !response.isEmpty()) {
                for (ServerGson.hostData subHost : response) {
                    WatchContact.User user = new WatchContact.User();
                    user.mId = subHost.requestToUser.id;
                    user.mEmail = subHost.requestToUser.email;
                    user.mFirstName = subHost.requestToUser.firstName;
                    user.mLastName = subHost.requestToUser.lastName;
                    user.mProfile = subHost.requestToUser.profile;
                    mRequestedUserList.add(user);
                }
                getUserAvatar(true);
            } else {
                mProcessDialog.dismiss();
            }

        }

        @Override
        public void onFail(int statusCode) {
            mProcessDialog.dismiss();
        }
    };

    private int mUserAvatarCount;
    private void getUserAvatarFinish() {
        for (WatchContact.User user : mRequestedUserList)
            addPending(user);
        mProcessDialog.dismiss();

    }

    private void getUserAvatar(boolean start) {
        if (start)
            mUserAvatarCount = 0;
        else
            mUserAvatarCount++;

        if (mUserAvatarCount >= mRequestedUserList.size()) {
            getUserAvatarFinish();
            return;
        }

        while (mRequestedUserList.get(mUserAvatarCount).mProfile.equals("")) {
            mUserAvatarCount++;
            if (mUserAvatarCount >= mRequestedUserList.size()) {
                getUserAvatarFinish();
                return;
            }
        }

        mActivityMain.mServiceMachine.getAvatar(mGetUserAvatarListener, mRequestedUserList.get(mUserAvatarCount).mProfile);
    }

    ServerMachine.getAvatarListener mGetUserAvatarListener = new ServerMachine.getAvatarListener() {
        @Override
        public void onSuccess(Bitmap avatar, String filename) {
            ServerMachine.createAvatarFile(avatar, filename, "");
            getUserAvatar(false);
        }

        @Override
        public void onFail(int statusCode) {
            getUserAvatarFinish();
        }
    };

    public void addUser(WatchContact.User person) {
        View view = WatchContact.inflateButton(mActivityMain, person, "Send");

        View button = view.findViewById(R.id.watch_contact_button_button);
        button.setTag(person);
        button.setOnClickListener(mUserListener);

        mViewUserContainer.addView(view);
    }

    public void addPending(WatchContact.User device) {
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

            Log.d("xxx", "Remove pending request: " + kid.mLabel);
            // todo: Remove request from Database or API
        }
    };

}
