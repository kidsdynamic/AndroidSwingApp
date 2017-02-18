package com.kidsdynamic.swing.androidswingapp;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Color;
import android.graphics.Typeface;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.view.Gravity;
import android.widget.LinearLayout;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;

/**
 * Created by 03543 on 2017/2/19.
 */

public class ViewListSelector extends LinearLayout {

    private int mTextSize = 20;
    private int mTextColor = Color.BLACK;
    private int mTextStyle = Typeface.BOLD;
    private int mSelectorColor = Color.BLACK;

    private TextView mViewPrev;
    private TextView mViewNext;
    private TextView mViewList;

    public ViewListSelector(Context context) {
        super(context);
        init(context, null);
    }

    public ViewListSelector(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context, attrs);
    }

    private void init(Context context, AttributeSet attrs) {
        if (attrs != null) {
            TypedArray typedArray = context.obtainStyledAttributes(
                    attrs, R.styleable.ViewListSelector);

            final int count = typedArray.getIndexCount();
            for (int idx = 0; idx < count; idx++) {
                final int attr = typedArray.getIndex(idx);

                if (attr == R.styleable.ViewListSelector_android_textSize) {
                    mTextSize = typedArray.getDimensionPixelOffset(attr, mTextSize);
                } else if(attr == R.styleable.ViewListSelector_android_textColor) {
                    mTextColor = typedArray.getColor(attr, mTextColor);
                } else if(attr == R.styleable.ViewListSelector_android_textStyle) {
                    mTextStyle = typedArray.getInteger(attr, mTextStyle);
                } else if(attr == R.styleable.ViewListSelector_selectorColor) {
                    mSelectorColor = typedArray.getColor(attr, mSelectorColor);
                }
            }

            typedArray.recycle();
        }

        mViewPrev = new TextView(getContext());
        mViewPrev.setText("◀"); // U+25C0 &#9664;
        mViewPrev.setTextSize(TypedValue.COMPLEX_UNIT_PX, mTextSize);
        mViewPrev.setTextColor(mTextColor);
        mViewPrev.setGravity(Gravity.CENTER);
        mViewPrev.setLayoutParams(new LinearLayout.LayoutParams(0, LinearLayout.LayoutParams.MATCH_PARENT, 1));
        addView(mViewPrev);

        mViewList = new TextView(context);
        mViewList.setTextSize(TypedValue.COMPLEX_UNIT_PX, mTextSize);
        mViewList.setTypeface(mViewList.getTypeface(), mTextStyle);
        mViewList.setTextColor(mTextColor);
        mViewList.setGravity(Gravity.CENTER);
        mViewList.setLayoutParams(new TableRow.LayoutParams(0, LinearLayout.LayoutParams.MATCH_PARENT, 3));
        addView(mViewList);

        mViewNext = new TextView(getContext());
        mViewNext.setText("▶"); // U+25B6 &#9654;
        mViewNext.setTextSize(TypedValue.COMPLEX_UNIT_PX, mTextSize);
        mViewNext.setTextColor(mTextColor);
        mViewNext.setGravity(Gravity.CENTER);
        mViewNext.setLayoutParams(new LinearLayout.LayoutParams(0, LinearLayout.LayoutParams.MATCH_PARENT, 1));
        addView(mViewNext);
    }

}
