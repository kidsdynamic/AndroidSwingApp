package com.kidsdynamic.swing.androidswingapp;

import android.content.Context;
import android.graphics.Bitmap;
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
        public boolean mBind = false;

        public Device(Bitmap photo, String label, boolean bind) {
            super(photo, label);

            mBind = bind;
        }
    }

    public static class Person extends WatchContact {
        public Person(Bitmap photo, String label) {
            super(photo, label);
        }
    }

    public static View inflateBind(Context context, WatchContact.Device device) {
        LayoutInflater inflater = LayoutInflater.from(context);
        View view = inflater.inflate(R.layout.view_watch_contact_bind, null);

        ViewPhoto viewPhoto = (ViewPhoto) view.findViewById(R.id.view_watch_contact_photo);
        viewPhoto.setPhoto(device.mPhoto);

        TextView viewLabel = (TextView) view.findViewById(R.id.view_watch_contact_label);
        viewLabel.setText(device.mLabel);

        ImageView viewIcon = (ImageView) view.findViewById(R.id.view_watch_contact_button);
        viewIcon.setImageResource(device.mBind ? R.mipmap.iconbutton_bind : R.mipmap.iconbutton_add);

        int height = context.getResources().getDisplayMetrics().heightPixels / 12;
        LinearLayout.LayoutParams layoutParams =
                new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, height);
        layoutParams.gravity = Gravity.CENTER;
        view.setLayoutParams(layoutParams);

        view.setTag(device);

        return view;
    }
}
