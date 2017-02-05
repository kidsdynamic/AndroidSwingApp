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
        public int mId;
        public String mFirstName;
        public String mLastName;
        public long mDateCreated;
        public String mMacId;
        public String mProfile;
        public int mUserId;
        public boolean mBound = false;

        public Kid() {
            super(null, "");
            mId = 0;
            mFirstName = "";
            mLastName = "";
            mDateCreated = 0;
            mMacId = "";
            mUserId = 0;
            mProfile = "";

            mBound = false;
        }

        public Kid(Bitmap photo, String label, boolean bind) {
            super(photo, label);
            mId = 0;
            mFirstName = "";
            mLastName = "";
            mDateCreated = 0;
            mMacId = "";
            mUserId = 0;
            mProfile = "";

            mBound = bind;
        }

        public Kid(Bitmap photo, int id, String firstName, String lastName, long dateCreated, String macId, int parentId) {
            super(photo, firstName + " " + lastName);

            mId = id;
            mFirstName = firstName;
            mLastName = lastName;
            mDateCreated = dateCreated;
            mMacId = macId;
            mUserId = parentId;
            mProfile = "";
            mBound = true;
        }
    }

    public static class User extends WatchContact {
        public int mId;
        public String mEmail;
        public String mFirstName;
        public String mLastName;
        public long mLastUpdate;
        public long mDateCreated;
        public String mZipCode;
        public String mPhoneNumber;
        public String mProfile;

        public User() {
            super(null, "");
            mId = 0;
            mEmail = "";
            mFirstName = "";
            mLastName = "";
            mLastUpdate = 0;
            mDateCreated = 0;
            mZipCode = "";
            mPhoneNumber = "";
            mProfile = "";
        }

        public User(Bitmap photo, String label) {
            super(photo, label);
            mId = 0;
            mEmail = "";
            mFirstName = "";
            mLastName = "";
            mLastUpdate = 0;
            mDateCreated = 0;
            mZipCode = "";
            mPhoneNumber = "";
            mProfile = "";
        }

        public User(Bitmap photo, int id, String email, String firstName, String lastName, long lastUpdate, long dateCreated, String zipCode, String phoneNumber) {
            super(photo, firstName + " " + lastName);
            mId = id;
            mEmail = email;
            mFirstName = firstName;
            mLastName = lastName;
            mLastUpdate = lastUpdate;
            mDateCreated = dateCreated;
            mZipCode = zipCode;
            mPhoneNumber = phoneNumber;
            mProfile = "";
        }
    }

    static View inflate(Context context, @LayoutRes int layout, WatchContact contact) {
        LayoutInflater inflater = LayoutInflater.from(context);
        View view = inflater.inflate(layout, null);

        ViewCircle viewPhoto = (ViewCircle) view.findViewById(R.id.watch_contact_photo);
        viewPhoto.setBitmap(contact.mPhoto);

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
