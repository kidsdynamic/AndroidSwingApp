package com.kidsdynamic.swing.androidswingapp;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.util.DisplayMetrics;
import android.util.TypedValue;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;

/**
 * Created by 03543 on 2017/1/24.
 */

public class ViewTextEditor extends RelativeLayout {

    private int mDesiredWidth;
    private int mDesiredHeight;

    private ImageView mViewIcon;
    private EditText mViewEditor;

    private float mTextSize;

    public ViewTextEditor(Context context) {
        super(context);
        init(context, null);
    }

    public ViewTextEditor(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context, attrs);
    }

    public ViewTextEditor(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        init(context, attrs);
    }

    private void init(Context context, AttributeSet attrs) {
        mTextSize = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_PX, 16, getResources().getDisplayMetrics());

        if (attrs != null) {
            TypedArray typedArray = context.obtainStyledAttributes(
                    attrs, R.styleable.ViewTextEditor);

            final int count = typedArray.getIndexCount();
            for (int idx = 0; idx < count; idx++) {
                final int attr = typedArray.getIndex(idx);

                if (attr == R.styleable.ViewTextEditor_android_src) {
                    Drawable drawable = typedArray.getDrawable(attr);
                    mViewIcon.setImageBitmap(((BitmapDrawable) drawable).getBitmap());
                } else if (attr == R.styleable.ViewTextEditor_android_textSize) {
                    mTextSize = typedArray.getDimension(attr, mTextSize);
                }
            }

            typedArray.recycle();
        }

        mDesiredHeight = 40;
        mDesiredWidth = 160;

        inflate(getContext(), R.layout.view_text_editor, this);

        mViewIcon = (ImageView) findViewById(R.id.view_text_editor_icon);
        mViewEditor = (EditText) findViewById(R.id.view_text_editor_textedit);
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
}
