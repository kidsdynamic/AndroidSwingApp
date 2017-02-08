package com.kidsdynamic.swing.androidswingapp;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.support.v4.content.ContextCompat;
import android.util.AttributeSet;
import android.view.ContextThemeWrapper;
import android.view.Gravity;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

/**
 * Created by 03543 on 2017/1/20.
 */

public class ViewPhotoContact extends LinearLayout {
    private ViewCircle mViewPhoto;
    private TextView mViewLabel;

    private WatchContact mContactItem = null;

    public ViewPhotoContact(Context context) {
        super(context);
        init(context, null);
    }

    public ViewPhotoContact(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context, attrs);
    }

    public ViewPhotoContact(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        init(context, attrs);
    }

    private void init(Context context, AttributeSet attrs) {
        inflate(getContext(), R.layout.view_photo_contact, this);

        mViewPhoto = (ViewCircle) findViewById(R.id.view_photo_contact_photo);
        mViewLabel = (TextView) findViewById(R.id.view_photo_contact_label);
    }

    public void load(WatchContact item) {
        mContactItem = item;

        mViewPhoto.setBitmap(item.mPhoto);
        mViewLabel.setText(item.mLabel);
    }

    public WatchContact getItem() {
        return mContactItem;
    }

    public void setSelected(boolean selected) {
        mViewPhoto.setStrokeActive(selected);
    }

    public void setName(String name) {
        mViewLabel.setText(name);
    }
}
