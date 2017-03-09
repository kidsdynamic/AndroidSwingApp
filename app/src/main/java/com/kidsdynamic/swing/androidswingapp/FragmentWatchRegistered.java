package com.kidsdynamic.swing.androidswingapp;

import android.app.Dialog;
import android.app.Fragment;
import android.app.ProgressDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import java.util.ArrayList;

/**
 * Created by 03543 on 2016/12/31.
 */

public class FragmentWatchRegistered extends ViewFragment {
    private ActivityMain mActivityMain;
    private View mViewMain;

    private Button mButtonRequest;
    private Button mButtonContact;
    private ImageView mViewBack;
    private Dialog mProcessDialog = null;

    private ArrayList<WatchContact.Kid> mKidList;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mActivityMain = (ActivityMain) getActivity();

        mKidList = new ArrayList<>();
        while (!mActivityMain.mContactStack.isEmpty())
            mKidList.add((WatchContact.Kid) mActivityMain.mContactStack.pop());
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        mViewMain = inflater.inflate(R.layout.fragment_watch_registered, container, false);

        mButtonRequest = (Button) mViewMain.findViewById(R.id.watch_registered_request);
        mButtonRequest.setOnClickListener(mOnRequestListener);

        mButtonContact = (Button) mViewMain.findViewById(R.id.watch_registered_contact);
        mButtonContact.setOnClickListener(mOnContactListener);

        mViewBack = (ImageView) mViewMain.findViewById(R.id.fragment_back);
        mViewBack.setOnClickListener(mBackOnClickListener);

        return mViewMain;
    }

    @Override
    public ViewFragmentConfig getConfig() {
        return new ViewFragmentConfig("", false, false, false,
                ActivityMain.RESOURCE_IGNORE, ActivityMain.RESOURCE_IGNORE, ActivityMain.RESOURCE_IGNORE);
    }

    @Override
    public void onToolbarAction1() {
        mActivityMain.popFragment();
    }

    private View.OnClickListener mBackOnClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            onToolbarAction1();
        }
    };

    private Button.OnClickListener mOnRequestListener = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            WatchContact.User user = mActivityMain.mOperator.getUser();
            if (!user.mEmail.equals(mKidList.get(0).mLabel)) {
                for (WatchContact contact : mKidList)
                    mActivityMain.mContactStack.push(contact);

                mProcessDialog = ProgressDialog.show(mActivityMain,
                        getResources().getString(R.string.watch_registered_processing),
                        getResources().getString(R.string.watch_registered_wait), true);
                mActivityMain.mOperator.requestToSubHost(mAddRequestToListener, mKidList.get(0).mLabel);
            } else {
                Toast.makeText(mActivityMain, "Can't request for self", Toast.LENGTH_SHORT).show();
            }
        }
    };

    WatchOperator.finishListener mAddRequestToListener = new WatchOperator.finishListener() {
        @Override
        public void onFinish(Object arg) {
            mProcessDialog.dismiss();
            mActivityMain.selectFragment(FragmentWatchRequest.class.getName(), null);
        }

        @Override
        public void onFailed(String Command, int statusCode) {
            mProcessDialog.dismiss();
            Toast.makeText(mActivityMain, Command, Toast.LENGTH_SHORT).show();
        }
    };

    private Button.OnClickListener mOnContactListener = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            String url = "http://www.kidsdynamic.com";
            Intent i = new Intent(Intent.ACTION_VIEW);
            i.setData(Uri.parse(url));
            startActivity(i);
        }
    };
}
