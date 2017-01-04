package com.kidsdynamic.swing.androidswingapp;

import android.graphics.Bitmap;

/**
 * Created by 03543 on 2017/1/4.
 */

public class ContactItem {
    private Bitmap mPhoto;
    private String mLabel;

    public ContactItem() {
    }

    public ContactItem(Bitmap photo, String label) {
        mPhoto = photo;
        mLabel = label;
    }

    public Bitmap getPhoto() {
        return mPhoto;
    }

    public void setPhoto(Bitmap photo) {
        mPhoto = photo;
    }

    public String getLabel() {
        return mLabel;
    }

    public void setLabel(String label) {
        mLabel = label;
    }

    public class Request extends ContactItem {
    }

    public class Kid extends ContactItem {

    }
}
