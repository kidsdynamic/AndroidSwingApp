package com.kidsdynamic.swing.androidswingapp;

import android.content.Context;
import android.util.AttributeSet;
import android.util.DisplayMetrics;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

/**
 * Created by 03543 on 2016/12/30.
 */

public class ViewWatchContact extends RelativeLayout {
    final static int MODE_NONE = 0;
    final static int MODE_BIND = 1;
    final static int MODE_ADD = 2;
    final static int MODE_OK_CANCEL = 3;
    final static int MODE_PENDING = 4;
    final static int MODE_CHECK = 5;
    final static int MODE_RADIO = 6;

    private int mDesiredWidth;
    private int mDesiredHeight;

    private ViewPhoto mViewPhoto;
    private TextView mViewLabel;
    private ImageView mViewButton1;
    private ImageView mViewButton2;

    private WatchContact mContactItem = null;

    public ViewWatchContact(Context context) {
        super(context);
        init();
    }

    public ViewWatchContact(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public ViewWatchContact(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        init();
    }

    private void init() {
        DisplayMetrics metrics = getResources().getDisplayMetrics();
        mDesiredHeight = metrics.heightPixels / getResources().getInteger(R.integer.contact_label_denominator);
        mDesiredWidth = mDesiredHeight * 4;

        inflate(getContext(), R.layout.view_watch_contact_select, this);

        mViewPhoto = (ViewPhoto) findViewById(R.id.view_watch_contact_photo);
        mViewLabel = (TextView) findViewById(R.id.view_watch_contact_label);
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

    private void setChileWidth(View view, int width) {
        LayoutParams layoutParams;

        layoutParams = (LayoutParams) view.getLayoutParams();

        if (layoutParams.width == width)
            return;

        layoutParams.width = width;
        view.setLayoutParams(layoutParams);
    }

    public void load(WatchContact item) {
        mContactItem = item;

        mViewLabel.setText(item.mLabel);

        if (item.mViewMode == MODE_NONE) {
            setChileWidth(mViewButton1, 0);
            setChileWidth(mViewButton2, 0);

        } else if (item.mViewMode == MODE_BIND) {
            setChileWidth(mViewButton1, LayoutParams.WRAP_CONTENT);
            setChileWidth(mViewButton2, 0);

            WatchContact.Device device = (WatchContact.Device) item;

            mViewButton1.setImageResource(device.mBound ? R.mipmap.iconbutton_bind : R.mipmap.iconbutton_add);
        }

        mContactItem = item;
    }

    public WatchContact getItem() {
        return mContactItem;
    }
}
