package com.kidsdynamic.swing.androidswingapp;

import android.os.Bundle;
import android.support.v4.content.ContextCompat;
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

        return mViewMain;
    }

    @Override
    public void onResume() {
        super.onResume();

        delAllContact(mViewDeviceContainer);
        delAllContact(mViewSharedContainer);
        delAllContact(mViewRequestToContainer);
        delAllContact(mViewRequestFromContainer);

        WatchContact.User parent = mActivityMain.mOperator.getUser();
        mViewName.setText(parent.mLabel);
        if (parent.mPhoto != null) {
            mViewPhoto.setBitmap(parent.mPhoto);
        }

        for (WatchContact device : mActivityMain.mOperator.getDeviceList())
            addContact(mViewDeviceContainer, device, mContactListener);

        for (WatchContact device : mActivityMain.mOperator.getSharedList())
            addContact(mViewSharedContainer, device, mContactListener);

        for (WatchContact user : mActivityMain.mOperator.getRequestToList()) {
            if (((WatchContact.User) user).mRequestStatus.equals("PENDING"))
                addContact(mViewRequestToContainer, user, null);
        }

        for (WatchContact user : mActivityMain.mOperator.getRequestFromList()) {
            //if (((WatchContact.User) user).mRequestStatus.equals("PENDING"))
            addContact(mViewRequestFromContainer, user, mContactListener);
        }

        updateRequestFromTitle();

        focusContact(mActivityMain.mOperator.getFocusKid(), true);
    }

    @Override
    public ViewFragmentConfig getConfig() {
        return new ViewFragmentConfig(
                getResources().getString(R.string.title_profile), true, true, false,
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
                focusContact(contact, false);

            } else if (viewContainer == mViewSharedContainer) {
                focusContact(contact, false);

            } else if (viewContainer == mViewRequestToContainer) {

            } else if (viewContainer == mViewRequestFromContainer) {
                mActivityMain.mContactStack.push(contact);
                mActivityMain.selectFragment(FragmentProfileRequestFrom.class.getName(), null);
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

    private void delContact(LinearLayout layout, WatchContact contact) {
        int count = layout.getChildCount();
        for (int idx = 0; idx < count; idx++) {
            View child = layout.getChildAt(idx);
            WatchContact someone = (WatchContact) child.getTag();
            if (someone == contact) {
                layout.removeViewAt(idx);
                break;
            }
        }
    }

    private void delAllContact(LinearLayout layout) {
        int remain = layout == mViewDeviceContainer || layout == mViewRequestToContainer ? 1 : 0;

        while (layout.getChildCount() > remain)
            layout.removeViewAt(layout.getChildCount() - 1);
    }

    private void focusContact(WatchContact contact, boolean onCreate) {
        int count;

        count = mViewDeviceContainer.getChildCount();
        for (int idx = 0; idx < count; idx++) {
            ViewCircle viewCircle = (ViewCircle) mViewDeviceContainer.getChildAt(idx);
            WatchContact.Kid kid1 = (WatchContact.Kid) contact;
            WatchContact.Kid kid2 = (WatchContact.Kid) viewCircle.getTag();

            boolean focus = kid1 != null && kid2 != null && kid1.mId == kid2.mId && kid1.mUserId == kid2.mUserId;
            viewCircle.setActive(focus);
        }

        count = mViewSharedContainer.getChildCount();
        for (int idx = 0; idx < count; idx++) {
            ViewCircle viewCircle = (ViewCircle) mViewSharedContainer.getChildAt(idx);
            WatchContact.Kid kid1 = (WatchContact.Kid) contact;
            WatchContact.Kid kid2 = (WatchContact.Kid) viewCircle.getTag();
            boolean focus = (kid1 != null && kid2 != null && kid1.mId == kid2.mId && kid1.mUserId == kid2.mUserId);
            viewCircle.setActive(focus);
        }

        if (!onCreate)
            mActivityMain.mOperator.setFocusKid((WatchContact.Kid) contact);

        mActivityMain.updateFocusAvatar();
    }

    private void updateRequestFromTitle() {
        int count = mViewRequestFromContainer.getChildCount();

        String string = String.format(Locale.getDefault(),
                getResources().getString(R.string.profile_main_request_from), count);
        mViewRequestFromTitle.setText(string);
    }
}
