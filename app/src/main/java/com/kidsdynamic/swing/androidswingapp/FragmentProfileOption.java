package com.kidsdynamic.swing.androidswingapp;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

/**
 * Created by 03543 on 2017/1/24.
 */

public class FragmentProfileOption extends ViewFragment {

    private ActivityMain mActivityMain;
    private View mViewMain;
    private View mViewPassword;
    private View mViewProfile;
    private View mViewLogout;
    private View mViewContact;
    private View mViewPrivacy;
    private View mViewManual;
    private ServerMachine mServerMachine;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mActivityMain = (ActivityMain) getActivity();
        mServerMachine = mActivityMain.mServiceMachine;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        mViewMain = inflater.inflate(R.layout.fragment_profile_option, container, false);

        InputMethodManager imm = (InputMethodManager) mActivityMain.getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(mViewMain.getWindowToken(), 0);

        mViewProfile = mViewMain.findViewById(R.id.profile_option_profile);
        mViewProfile.setOnClickListener(mProfileListener);

        mViewPassword = mViewMain.findViewById(R.id.profile_option_password);
        mViewPassword.setOnClickListener(mPasswordListener);

        mViewLogout = mViewMain.findViewById(R.id.profile_option_logout);
        mViewLogout.setOnClickListener(mLogoutListener);

        mViewContact = mViewMain.findViewById(R.id.profile_option_contact);
        mViewContact.setOnClickListener(mContactListener);

        mViewPrivacy = mViewMain.findViewById(R.id.profile_option_privacy);
        mViewPrivacy.setOnClickListener(mPrivacyListener);

        mViewManual = mViewMain.findViewById(R.id.profile_option_manual);
        mViewManual.setOnClickListener(mManualListener);

        TextView view = (TextView) mViewMain.findViewById(R.id.profile_option_version);
        view.setText(BuildConfig.VERSION_NAME + " " + BuildConfig.BUILD_TYPE);

        return mViewMain;
    }

    @Override
    public ViewFragmentConfig getConfig() {
        return new ViewFragmentConfig(
                getResources().getString(R.string.title_option), true, true, false,
                ActivityMain.RESOURCE_IGNORE, R.mipmap.icon_left, ActivityMain.RESOURCE_HIDE);
    }

    @Override
    public void onToolbarAction1() {
        mActivityMain.popFragment();
    }

    private View.OnClickListener mProfileListener = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            mActivityMain.mContactStack.push(mActivityMain.mOperator.getFocusKid());
            mActivityMain.selectFragment(FragmentProfileKid.class.getName(), null);
        }
    };

    private View.OnClickListener mPasswordListener = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
//            mActivityMain.selectFragment(FragmentProfilePassword.class.getName(), null);
            new AlertDialog.Builder(mViewMain.getContext())
                    .setTitle(mActivityMain.getString(R.string.profile_option_password))
                    .setMessage(mActivityMain.getString(R.string.profile_reset_password_confirmation))
                    .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int which) {
                            mServerMachine.userResetPassword();
                            WatchContact.User user = mActivityMain.mOperator.getUser();
                            String text = mActivityMain.getString(R.string.profile_reset_password_note, user.mEmail);
                            Toast toast = Toast.makeText(mViewMain.getContext(), text, Toast.LENGTH_LONG);
                            toast.show();
                        }
                    })
                    .setNegativeButton(android.R.string.no, new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int which) {
                            // do nothing
                        }
                    })
                    .setIcon(android.R.drawable.ic_dialog_alert)
                    .show();
        }
    };

    private View.OnClickListener mLogoutListener = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            mActivityMain.logout();
            /*
            mActivityMain.mConfig.loadDefaultTable();
            mActivityMain.mOperator.ResetDatabase();
            mActivityMain.mServiceMachine.setAuthToken(null);
            ServerMachine.ResetAvatar();

            Intent i = mActivityMain.getBaseContext().getPackageManager()
                    .getLaunchIntentForPackage(mActivityMain.getBaseContext().getPackageName());
            i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            startActivity(i);
            */
        }
    };

    private View.OnClickListener mContactListener = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            String url = "http://www.kidsdynamic.com";
            String language = mActivityMain.mConfig.getString(ActivityConfig.KEY_LANGUAGE);
            switch (language) {
                case "ru":
                case "es":
                    url = "http://www.imaginarium.info/";
                    break;
            }

            // force to KD's customer webpage
            url = "http://www.imaginarium.info/";

            Intent i = new Intent(Intent.ACTION_VIEW);
            i.setData(Uri.parse(url));
            startActivity(i);
        }
    };

    private View.OnClickListener mPrivacyListener = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            String url = "http://www.kidsdynamic.com";
            String language = mActivityMain.mConfig.getString(ActivityConfig.KEY_LANGUAGE);

            switch (language) {
                case "ru":
                case "es":
                    url = "http://www.imaginarium.info/";
                    break;
            }

            Intent i = new Intent(Intent.ACTION_VIEW);
            i.setData(Uri.parse(url));
            startActivity(i);
        }
    };

    private View.OnClickListener mManualListener = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            String url = "https://childrenlab.s3.amazonaws.com/pdf/Swing_User_Guide.pdf";

            Intent i = new Intent(Intent.ACTION_VIEW);
            i.setData(Uri.parse(url));
            startActivity(i);
        }
    };
}
