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

        for (WatchContact.User user : mActivityMain.mOperator.getRequestToList())
            addPending(user);

        return mViewMain;
    }

    @Override
    public void onResume() {
        super.onResume();
        mProcessDialog = ProgressDialog.show(mActivityMain, "Processing", "Please wait...", true);

        mActivityMain.mServiceMachine.subHostList(mSubHostListListener, "");
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
            mRequestedUserList.get(mUserAvatarCount).mPhoto = avatar;
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

            if (!mail.equals("")) {
                mProcessDialog = ProgressDialog.show(mActivityMain, "Processing", "Please wait...", true);
                mActivityMain.mServiceMachine.userFindByEmail(mUserFindByEmailLintener, mail);
            }

            return true;
        }
    };

    ServerMachine.userFindByEmailListener mUserFindByEmailLintener = new ServerMachine.userFindByEmailListener() {
        @Override
        public void onSuccess(int statusCode, ServerGson.userData response) {
            mActivityMain.mServiceMachine.subHostAdd(mSubHostAddListener, response.id);
        }

        @Override
        public void onFail(int statusCode) {
            mProcessDialog.dismiss();
            Toast.makeText(mActivityMain, "Can't find user by the email.", Toast.LENGTH_SHORT).show();
        }
    };

    ServerMachine.subHostAddListener mSubHostAddListener = new ServerMachine.subHostAddListener() {
        @Override
        public void onSuccess(int statusCode, ServerGson.hostData response) {
            WatchContact.User user = new WatchContact.User();
            user.mId = response.requestToUser.id;
            user.mEmail = response.requestToUser.email;
            user.mFirstName = response.requestToUser.firstName;
            user.mLastName = response.requestToUser.lastName;
            user.mProfile = response.requestToUser.profile;
            mRequestedUserList.add(user);

            if (user.mProfile.equals("")) {
                addPending(user);
                mProcessDialog.dismiss();
            } else {
                mActivityMain.mServiceMachine.getAvatar(mGetNewUserAvatarListener, user.mProfile);
            }
        }

        @Override
        public void onConflict(int statusCode) {
            mProcessDialog.dismiss();
            Toast.makeText(mActivityMain, "The request is already exists.", Toast.LENGTH_SHORT).show();
        }

        @Override
        public void onFail(int statusCode) {
            mProcessDialog.dismiss();
            Toast.makeText(mActivityMain, "Bad request.", Toast.LENGTH_SHORT).show();
        }
    };

    ServerMachine.getAvatarListener mGetNewUserAvatarListener = new ServerMachine.getAvatarListener() {
        @Override
        public void onSuccess(Bitmap avatar, String filename) {
            WatchContact.User user = mRequestedUserList.get(mRequestedUserList.size()-1);
            user.mPhoto = avatar;
            addPending(user);
            mProcessDialog.dismiss();
        }

        @Override
        public void onFail(int statusCode) {
            WatchContact.User user = mRequestedUserList.get(mRequestedUserList.size()-1);
            addPending(user);
            mProcessDialog.dismiss();
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
