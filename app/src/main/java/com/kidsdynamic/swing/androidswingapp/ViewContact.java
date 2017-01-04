package com.kidsdynamic.swing.androidswingapp;

import android.content.Context;
import android.util.AttributeSet;
import android.util.DisplayMetrics;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

/**
 * Created by 03543 on 2016/12/30.
 */

public class ViewContact extends RelativeLayout {
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
    private Button mViewButton1;
    private Button mViewButton2;

    private ContactItem mContactItem = null;

    public ViewContact(Context context) {
        super(context);
    }

    public ViewContact(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public ViewContact(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        init();
    }

    private void init() {
        DisplayMetrics metrics = getResources().getDisplayMetrics();
        mDesiredHeight = metrics.heightPixels / getResources().getInteger(R.integer.contact_label_denominator);
        mDesiredWidth = mDesiredHeight * 4;

        inflate(getContext(), R.layout.view_contact, this);

        mViewPhoto = (ViewPhoto) findViewById(R.id.view_contact_photo);
        mViewLabel = (TextView) findViewById(R.id.view_contact_label);
        mViewButton1 = (Button) findViewById(R.id.view_contact_button1);
        mViewButton2 = (Button) findViewById(R.id.view_contact_button2);
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
        heightMeasureSpec = MeasureSpec.makeMeasureSpec(heightSize, heightMode);

        getLayoutParams().width = width;
        getLayoutParams().height = height;

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

    public void loadItem(ContactItem item) {
        setChileWidth(mViewButton1, 0);
        setChileWidth(mViewButton2, 0);
    }

    public void loadBindItem() {
        ContactItem.BindItem item = (ContactItem.BindItem)mContactItem;

        setChileWidth(mViewButton1, LayoutParams.WRAP_CONTENT);
        setChileWidth(mViewButton2, 0);

        if (item.mBound)
            mViewButton1.setText("B");
        else
            mViewButton1.setText("F");
    }

    public void loadAddItem() {
        setChileWidth(mViewButton1, LayoutParams.WRAP_CONTENT);
        setChileWidth(mViewButton2, 0);
    }

    public void load(ContactItem item) {
        mContactItem = item;

        mViewLabel.setText(item.mLabel);

        if (item instanceof ContactItem.BindItem) {
            loadBindItem();

        } else if (item instanceof ContactItem.AddItem) {
            loadAddItem();

        } else {
            loadItem(item);

        }
    }

    public ContactItem getItem() {
        return mContactItem;
    }
}
