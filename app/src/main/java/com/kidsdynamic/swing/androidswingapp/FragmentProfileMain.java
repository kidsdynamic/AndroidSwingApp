package com.kidsdynamic.swing.androidswingapp;

import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewParent;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.util.Locale;

/**
 * Created by 03543 on 2016/12/19.
 */

public class FragmentProfileMain extends ViewFragment {
    private ActivityMain mActivityMain;
    private View mViewMain;
    private ViewCircle mViewPhoto;
    private ViewCircle mViewDeviceAdd;
    private ViewCircle mViewRequestToAdd;
    private TextView mViewName;
    private TextView mViewRequestFromTitle;
    private LinearLayout mViewDeviceContainer;
    private LinearLayout mViewSharedContainer;
    private LinearLayout mViewRequestToContainer;
    private LinearLayout mViewRequestFromContainer;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mActivityMain = (ActivityMain) getActivity();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        mViewMain = inflater.inflate(R.layout.fragment_profile_main, container, false);

        mViewPhoto = (ViewCircle) mViewMain.findViewById(R.id.profile_main_photo);
        mViewName = (TextView) mViewMain.findViewById(R.id.profile_main_name);

        mViewDeviceContainer = (LinearLayout) mViewMain.findViewById(R.id.profile_main_device_container);
        mViewDeviceAdd = (ViewCircle) mViewMain.findViewById(R.id.profile_main_device_add);
        mViewDeviceAdd.setOnClickListener(mAddDeviceListener);

        mViewSharedContainer = (LinearLayout) mViewMain.findViewById(R.id.profile_main_shared_container);

        mViewRequestToContainer = (LinearLayout) mViewMain.findViewById(R.id.profile_main_request_to_container);
        mViewRequestFromTitle = (TextView) mViewMain.findViewById(R.id.profile_main_request_from_title);
        mViewRequestToAdd = (ViewCircle) mViewMain.findViewById(R.id.profile_main_request_to_add);
        mViewRequestToAdd.setOnClickListener(mAddRequestToListener);

        mViewRequestFromContainer = (LinearLayout) mViewMain.findViewById(R.id.profile_main_request_from_container);


        // Test
        WatchContact.Kid device1 = new WatchContact.Kid(BitmapFactory.decodeResource(getResources(), R.mipmap.monster_purple), "Device 1", true);
        WatchContact.Kid device2 = new WatchContact.Kid(BitmapFactory.decodeResource(getResources(), R.mipmap.monster_green), "Device 2", true);
        addContact(mViewDeviceContainer, device1, mContactListener);
        addContact(mViewDeviceContainer, device2, mContactListener);

        WatchContact.Kid shared1 = new WatchContact.Kid(BitmapFactory.decodeResource(getResources(), R.mipmap.monster_yellow), "Shared 1", true);
        addContact(mViewSharedContainer, shared1, mContactListener);

        focusContact(shared1);

        WatchContact.User to1 = new WatchContact.User(BitmapFactory.decodeResource(getResources(), R.mipmap.monster_green), "RequestTo 1");
        addContact(mViewRequestToContainer, to1, null);

        WatchContact.User from1 = new WatchContact.User(BitmapFactory.decodeResource(getResources(), R.mipmap.monster_purple), "RequestFrom 1");
        addContact(mViewRequestFromContainer, from1, null);
        ///

        for (WatchContact device : mActivityMain.mOperator.getDeviceList())
            addContact(mViewDeviceContainer, device, mContactListener);

        for (WatchContact device : mActivityMain.mOperator.getSharedList())
            addContact(mViewSharedContainer, device, mContactListener);

        for (WatchContact user : mActivityMain.mOperator.getRequestToList())
            addContact(mViewRequestToContainer, user, null);

        for (WatchContact user : mActivityMain.mOperator.getRequestFromList())
            addContact(mViewRequestFromContainer, user, null);
        updateRequestFromTitle();

        return mViewMain;
    }

    @Override
    public ViewFragmentConfig getConfig() {
        return new ViewFragmentConfig("Profile", true, true, false,
                ActivityMain.RESOURCE_IGNORE, R.mipmap.icon_edit, R.mipmap.icon_settings);
    }

    @Override
    public void onToolbarAction1() {
        mActivityMain.selectFragment(FragmentProfileEditor.class.getName(), null);
    }

    @Override
    public void onToolbarAction2() {
        mActivityMain.selectFragment(FragmentProfileOption.class.getName(), null);
    }

    @Override
    public void onToolbarTitle() {
    }

    private View.OnClickListener mAddDeviceListener = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            mActivityMain.selectFragment(FragmentProfileSearch.class.getName(), null);
        }
    };

    private View.OnClickListener mAddRequestToListener = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            mActivityMain.selectFragment(FragmentProfileRequestTo.class.getName(), null);
        }
    };

    private View.OnClickListener mContactListener = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            ViewCircle viewCircle = (ViewCircle) view;
            ViewParent viewContainer = view.getParent();
            WatchContact contact = (WatchContact) viewCircle.getTag();

            if (viewContainer == mViewDeviceContainer) {
                Log.d("xxx", "onClick: device " + contact.mLabel);
                focusContact(contact);

            } else if (viewContainer == mViewSharedContainer) {
                Log.d("xxx", "onClick: shared " + contact.mLabel);
                focusContact(contact);

            } else if (viewContainer == mViewRequestFromContainer) {
                Log.d("xxx", "onClick: requestFrom " + contact.mLabel);

            } else if (viewContainer == mViewRequestToContainer) {
                Log.d("xxx", "onClick: requestTo " + contact.mLabel);
            }
        }
    };

    private void addContact(LinearLayout layout, WatchContact contact, View.OnClickListener listener) {

        ViewCircle photo = new ViewCircle(mActivityMain);
        photo.setBitmap(contact.mPhoto);
        photo.setStrokeCount(12);
        photo.setStrokeBeginEnd(12, -1);
        photo.setStrokeType(ViewCircle.STROKE_TYPE_ARC);
        photo.setStrokeColorActive(ContextCompat.getColor(mActivityMain, R.color.color_orange_main));
        photo.setStrokeColorNormal(ContextCompat.getColor(mActivityMain, R.color.color_white));
        photo.setTag(contact);

        int margin = Math.round(TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 5, getResources().getDisplayMetrics()));

        LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.MATCH_PARENT);
        layoutParams.setMargins(margin, 0, margin, 0);

        layout.addView(photo, 0, layoutParams);
        photo.setOnClickListener(listener);
    }

    private void focusContact(WatchContact contact) {
        int count;

        count = mViewDeviceContainer.getChildCount();
        for (int idx = 0; idx < count; idx++) {
            ViewCircle viewCircle = (ViewCircle) mViewDeviceContainer.getChildAt(idx);
            viewCircle.setStrokeActive(viewCircle.getTag() == contact);
        }

        count = mViewSharedContainer.getChildCount();
        for (int idx = 0; idx < count; idx++) {
            ViewCircle viewCircle = (ViewCircle) mViewSharedContainer.getChildAt(idx);
            viewCircle.setStrokeActive(viewCircle.getTag() == contact);
        }

        // todo: Set focus contact in Database
    }

    private void updateRequestFromTitle() {
        int count = mViewRequestFromContainer.getChildCount();

        String string = String.format(Locale.getDefault(), "You've %d requests from", count);
        mViewRequestFromTitle.setText(string);
    }
}
