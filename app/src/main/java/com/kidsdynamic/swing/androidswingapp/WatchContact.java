package com.kidsdynamic.swing.androidswingapp;

import android.content.Context;
import android.graphics.Bitmap;
import android.support.annotation.LayoutRes;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.io.Serializable;

/**
 * Created by 03543 on 2017/1/4.
 */

public class WatchContact implements Serializable {
    public Bitmap mPhoto;
    public String mLabel;

    public WatchContact() {
        mPhoto = null;
        mLabel = "";
    }

    public WatchContact(Bitmap photo, String label) {
        mPhoto = photo;
        mLabel = label;
    }

    public static class Kid extends WatchContact {
        public boolean mBound = false;

        public Kid(Bitmap photo, String label, boolean bind) {
            super(photo, label);

            mBound = bind;
        }
    }

    public static class User extends WatchContact {
        public int mId;
        public String mEmail;
        public String mFirstName;
        public String mLastName;
        public String mLastUpdate;
        public String mDateCreated;
        public String mZipCode;
        public String mPhoneNumber;
        public String mProfile;

        public User(Bitmap photo, String label) {
            super(photo, label);
            mId = 0;
            mEmail = "";
            mFirstName = "";
            mLastName = "";
            mLastUpdate = "";
            mDateCreated = "";
            mZipCode = "";
            mPhoneNumber = "";
            mProfile = "";
        }

        public User(int id, String email, String firstName, String lastName, String lastUpdate, String dateCreated, String zipCode, String phoneNumber) {
            super(null, firstName + " " + lastName);
            mId = id;
            mEmail = email;
            mFirstName = firstName;
            mLastName = lastName;
            mLastUpdate = lastUpdate;
            mDateCreated = dateCreated;
            mZipCode = zipCode;
            mPhoneNumber = phoneNumber;
        }
    }

    static View inflate(Context context, @LayoutRes int layout, WatchContact contact) {
        LayoutInflater inflater = LayoutInflater.from(context);
        View view = inflater.inflate(layout, null);

        ViewPhoto viewPhoto = (ViewPhoto) view.findViewById(R.id.watch_contact_photo);
        viewPhoto.setPhoto(contact.mPhoto);

        TextView viewLabel = (TextView) view.findViewById(R.id.watch_contact_label);
        viewLabel.setText(contact.mLabel);

        int height = context.getResources().getDisplayMetrics().heightPixels / 12;
        LinearLayout.LayoutParams layoutParams =
                new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, height);
        layoutParams.gravity = Gravity.CENTER;
        view.setLayoutParams(layoutParams);

        view.setTag(contact);

        return view;
    }

    static View inflateBind(Context context, Kid device) {
        View view = inflate(context, R.layout.watch_contact_bind, device);

        ImageView viewIcon = (ImageView) view.findViewById(R.id.watch_contact_bind_icon);
        viewIcon.setImageResource(device.mBound ? R.mipmap.iconbutton_bind : R.mipmap.iconbutton_add);

        return view;
    }

    static View inflateRequester(Context context, User person) {
        View view = inflate(context, R.layout.watch_contact_requester, person);

        return view;
    }

    static View inflateAdd(Context context, Kid device) {
        View view = inflate(context, R.layout.watch_contact_add, device);

        return view;
    }

    static View inflatePending(Context context, Kid device) {
        View view = inflate(context, R.layout.watch_contact_pending, device);

        return view;
    }

    static View inflateShare(Context context, Kid device) {
        View view = inflate(context, R.layout.watch_contact_share, device);

        return view;
    }
}
