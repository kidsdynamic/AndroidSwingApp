package com.kidsdynamic.swing.androidswingapp;

import android.graphics.Bitmap;

import java.io.Serializable;

/**
 * Created by 03543 on 2017/1/4.
 */

public class WatchContact implements Serializable {
    public Bitmap mPhoto;
    public String mLabel;
    public int mViewMode;

    public WatchContact() {
        mPhoto = null;
        mLabel = "";
    }

    public WatchContact(Bitmap photo, String label, int viewMode) {
        mPhoto = photo;
        mLabel = label;
        mViewMode = viewMode;
    }

    public static class Device extends WatchContact {
        public boolean mBound = false;

        public Device(Bitmap photo, String label, int viewMode) {
            super(photo, label, viewMode);
        }
    }

    public static class Person extends WatchContact {
        public Person(Bitmap photo, String label, int viewMode) {
            super(photo, label, viewMode);
        }
    }
}
