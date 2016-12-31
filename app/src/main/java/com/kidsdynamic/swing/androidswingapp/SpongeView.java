package com.kidsdynamic.swing.androidswingapp;

import android.content.Context;
import android.content.res.TypedArray;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;

/**
 * Created by 03543 on 2016/12/31.
 */

public class SpongeView extends View {
    private float mShrinkWidth = 0f;
    private float mShrinkHeight = 0f;

    public SpongeView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context, attrs);

    }

    public SpongeView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        init(context, attrs);
    }

    private void init(Context context, AttributeSet attrs) {
        if (attrs != null) {
            TypedArray typedArray = context.obtainStyledAttributes(
                    attrs, R.styleable.SpongeView);

            final int count = typedArray.getIndexCount();
            for (int idx = 0; idx < count; idx++) {
                final int attr = typedArray.getIndex(idx);

                if (attr == R.styleable.SpongeView_shrinkWidth) {
                    mShrinkWidth = typedArray.getFloat(R.styleable.SpongeView_shrinkWidth, 0f);
                } else if (attr == R.styleable.SpongeView_shrinkHeight) {
                    mShrinkHeight = typedArray.getFloat(R.styleable.SpongeView_shrinkHeight, 0f);
                }
            }

            typedArray.recycle();
        }
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {

        int parentWidth = ((View) getParent()).getMeasuredWidth();
        int parentHeight = ((View) getParent()).getMeasuredHeight();

        int desiredWidth = (int) (parentWidth * mShrinkWidth);
        int desiredHeight = (int) (parentHeight * mShrinkHeight);

        int widthMode = MeasureSpec.getMode(widthMeasureSpec);
        int widthSize = MeasureSpec.getSize(widthMeasureSpec);

        int heightMode = MeasureSpec.getMode(heightMeasureSpec);
        int heightSize = MeasureSpec.getSize(heightMeasureSpec);

        int width, height;

        if (widthMode == MeasureSpec.EXACTLY) {
            width = widthSize;
        } else if (widthMode == MeasureSpec.AT_MOST) {
            width = Math.min(desiredWidth, widthSize);
        } else {
            width = desiredWidth;
        }

        if (heightMode == MeasureSpec.EXACTLY) {
            height = heightSize;
        } else if (heightMode == MeasureSpec.AT_MOST) {
            height = Math.min(desiredHeight, heightSize);
        } else {
            height = desiredHeight;
        }

        setMeasuredDimension(width, height);

        Log.d("xxx", "P:" + parentWidth + "," + parentHeight + " D:" + desiredWidth + "," + desiredHeight + " M:" + width + "," + height);
    }

}
