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

    public static class Device extends WatchContact {
        public boolean mBound = false;

        public Device(Bitmap photo, String label, boolean bind) {
            super(photo, label);

            mBound = bind;
        }
    }

    public static class Person extends WatchContact {
        public Person(Bitmap photo, String label) {
            super(photo, label);
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

    static View inflateBind(Context context, WatchContact.Device device) {
        View view = inflate(context, R.layout.watch_contact_bind, device);

        ImageView viewIcon = (ImageView) view.findViewById(R.id.watch_contact_bind_icon);
        viewIcon.setImageResource(device.mBound ? R.mipmap.iconbutton_bind : R.mipmap.iconbutton_add);

        return view;
    }

    static View inflateRequester(Context context, WatchContact.Person person) {
        View view = inflate(context, R.layout.watch_contact_requester, person);

        return view;
    }

    static View inflatePending(Context context, WatchContact.Device device) {
        View view = inflate(context, R.layout.watch_contact_pending, device);

        return view;
    }
}
