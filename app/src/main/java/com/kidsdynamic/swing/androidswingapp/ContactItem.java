package com.kidsdynamic.swing.androidswingapp;

import android.graphics.Bitmap;

/**
 * Created by 03543 on 2017/1/4.
 */

public class ContactItem {
    public Bitmap mPhoto;
    public String mLabel;

    public ContactItem() {
        mPhoto = null;
        mLabel = "";
    }

    public ContactItem(Bitmap photo, String label) {
        mPhoto = photo;
        mLabel = label;
    }

    public static class BindItem extends ContactItem {
        public boolean mBound;

        public BindItem(Bitmap photo, String label, boolean bound) {
            super(photo, label);
            mBound = bound;
        }
    }

    public static class AddItem extends ContactItem {
        public AddItem(Bitmap photo, String label) {
            super(photo, label);
        }
    }
}
