package com.kidsdynamic.swing.androidswingapp;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.AttributeSet;
import android.view.Gravity;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

/**
 * Created by 03543 on 2017/1/20.
 */

public class ViewPhotoContact extends RelativeLayout {
    private int mDesiredWidth;
    private int mDesiredHeight;

    private ViewPhoto mViewPhoto;
    private TextView mViewLabel;

    private WatchContact mContactItem = null;

    public ViewPhotoContact(Context context) {
        super(context);
        init();
    }

    public ViewPhotoContact(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public ViewPhotoContact(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        init();
    }

    private void init() {
        int size = Math.min(
                getResources().getDisplayMetrics().heightPixels,
                getResources().getDisplayMetrics().widthPixels) / 3;

        mDesiredHeight = (int) (size * 1.65);
        mDesiredWidth = size;

        inflate(getContext(), R.layout.view_photo_contact, this);

        mViewPhoto = (ViewPhoto) findViewById(R.id.view_photo_contact_photo);
        mViewLabel = (TextView) findViewById(R.id.view_photo_contact_label);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {

        int widthMode = MeasureSpec.getMode(widthMeasureSpec);
        int widthSize = MeasureSpec.getSize(widthMeasureSpec);

        int heightMode = MeasureSpec.getMode(heightMeasureSpec);
        int heightSize = MeasureSpec.getSize(heightMeasureSpec);

        int width, height;

        if (widthMode == MeasureSpec.EXACTLY) {
            width = widthSize;
        } else if (widthMode == MeasureSpec.AT_MOST) {
            width = Math.min(mDesiredWidth, widthSize);
        } else {
            width = mDesiredWidth;
        }

        if (heightMode == MeasureSpec.EXACTLY) {
            height = heightSize;
        } else if (heightMode == MeasureSpec.AT_MOST) {
            height = Math.min(mDesiredHeight, heightSize);
        } else {
            height = mDesiredHeight;
        }

        widthMeasureSpec = MeasureSpec.makeMeasureSpec(width, widthMode);
        heightMeasureSpec = MeasureSpec.makeMeasureSpec(height, heightMode);

        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
    }

    public void load(WatchContact item) {
        mContactItem = item;

        mViewPhoto.setPhoto(item.mPhoto);
        mViewLabel.setText(item.mLabel);

        RelativeLayout.LayoutParams params = (RelativeLayout.LayoutParams) mViewPhoto.getLayoutParams();
        params.height = mDesiredWidth;
        mViewPhoto.setLayoutParams(params);
    }

    public WatchContact getItem() {
        return mContactItem;
    }

    public void setSelected(boolean selected) {
        mViewPhoto.setSelected(selected);
    }

    public void setShowBorder(boolean show) {
        mViewPhoto.setShowBorder(show);
    }

    public boolean getShowBorder() {
        return mViewPhoto.getShowBorder();
    }

    public void setShowCross(boolean show) {
        mViewPhoto.setShowCross(show);
    }

    public boolean getShowCross() {
        return mViewPhoto.getShowCross();
    }

    public void setShowDarker(boolean show) {
        mViewPhoto.setShowDarker(show);
    }

    public boolean getShowDarker() {
        return mViewPhoto.getShowDarker();
    }

    public void setPhoto(int source) {
        mViewPhoto.setPhoto(source);
    }

    public void setPhoto(Bitmap photo) {
        mViewPhoto.setPhoto(photo);
    }

    public void setName(String name) {
        mViewLabel.setText(name);
    }
}
