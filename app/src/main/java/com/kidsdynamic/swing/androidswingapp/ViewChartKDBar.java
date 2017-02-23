package com.kidsdynamic.swing.androidswingapp;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.Typeface;
import android.util.AttributeSet;
import android.util.Log;
import android.util.TypedValue;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

/**
 * Created by 03543 on 2017/2/22.
 */

public class ViewChartKDBar extends ViewChart {

    private int mDesiredWidth = 160;
    private int mDesiredHeight = 100;

    private Paint mPaint;
    private Rect mRect;
    private Rect mRectH;
    private Rect mRectV;

    private List<Integer> mValue;
    private List<Long> mDate;
    private String mTitle = "Step";

    public ViewChartKDBar(Context context) {
        super(context);
        init(context, null);
    }

    public ViewChartKDBar(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context, attrs);
    }

    public ViewChartKDBar(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        init(context, attrs);
    }

    private void init(Context context, AttributeSet attrs) {
        mValue = new ArrayList<>();
        mDate = new ArrayList<>();

        mPaint = new Paint();
        mRect = new Rect();
        mRectV = new Rect();
        mRectH = new Rect();
    }

    public void addValue(int value) {
        mValue.add(value);
    }

    public void setValue(List<Integer> value) {
        mValue.clear();
        mValue.addAll(value);
    }

    public void addDate(long date) {
        mDate.add(date);
    }

    public void setDate(List<Long> date) {
        mDate.clear();
        mDate.addAll(date);
    }

    public void setTitle(String title) {
        mTitle = title;
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        int widthMode = MeasureSpec.getMode(widthMeasureSpec);
        int widthSize = MeasureSpec.getSize(widthMeasureSpec);

        int heightMode = MeasureSpec.getMode(heightMeasureSpec);
        int heightSize = MeasureSpec.getSize(heightMeasureSpec);

        int desiredWidth = mDesiredWidth + getPaddingStart() + getPaddingEnd();
        int desiredHeight = mDesiredHeight + getPaddingTop() + getPaddingBottom();

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

        mRect.set(getPaddingStart(), getPaddingTop(), getMeasuredWidth() - getPaddingEnd(), getMeasuredHeight() - getPaddingBottom());
    }

    @Override
    public void onDraw(Canvas canvas) {
        int dayWidth = mRect.width() * 162 / 1734;  // 1.62:1.00 Golder ratio. 7 days + 6 gaps = 1734.
        int gapWidth = mRect.width() * 100 / 1734;

        mRectV.set(mRect.left, mRect.top, mRect.right, mRect.top + mRect.height() * 3 / 4);
        mRectH.set(mRectV.left, mRectV.top, mRectV.left + dayWidth, mRectV.bottom);
        for (int idx = 0; idx < 7; idx++) {
            drawValue(canvas, mRectH, idx);
            mRectH.offset(dayWidth + gapWidth, 0);
        }

        mRectV.top = mRectV.bottom;
        mRectV.bottom = mRectV.top + (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP,
                mAxisWidth, getResources().getDisplayMetrics());
        drawAxisH(canvas, mRectV);

        mRectV.top = mRectV.bottom;
        mRectV.bottom = (mRect.bottom + mRectV.top) / 2;
        mRectH.set(mRectV.left, mRectV.top, mRectV.left + dayWidth, mRectV.bottom);
        for (int idx = 0; idx < 7; idx++) {
            drawDate(canvas, mRectH, idx);
            mRectH.offset(dayWidth + gapWidth, 0);
        }

        mRectV.top = mRectV.bottom;
        mRectV.bottom = mRect.bottom;
        drawTitle(canvas, mRectV, mTitle);
    }

    private void drawValue(Canvas canvas, Rect rect, int day) {
        Rect barRect = new Rect(rect);

        int value = 0;
        if (day < mValue.size())
            value = Math.round(mValue.get(day));

        int bound = value;
        if (bound > mAxisVMax)
            bound = Math.round(mAxisVMax);
        if (bound < mAxisVMin)
            bound = 0;

        barRect.top = barRect.bottom - (int) (bound * barRect.height() / mAxisVMax);

        mPaint.reset();
        mPaint.setAntiAlias(true);
        mPaint.setColor(mChartColor);
        mPaint.setStyle(Paint.Style.FILL);

        canvas.drawRect(barRect, mPaint);

        int textX, textY;
        Rect textRect = new Rect();
        String text = String.format(Locale.US, "%d", (int) value);

        mPaint.setTypeface(Typeface.create(Typeface.DEFAULT, mChartTextStyle));
        mPaint.setTextSize(mChartTextSize + 1);
        mPaint.getTextBounds(text, 0, text.length(), textRect);
        mPaint.setColor(Color.WHITE);

        int textPadding = 4;
        textX = barRect.left + (barRect.width() - textRect.width()) / 2;
        if ((barRect.height() - (textPadding * 2)) > textRect.height())
            textY = barRect.top + textRect.height() + textPadding;
        else
            textY = barRect.top - textPadding;

        canvas.drawText(text, textX, textY, mPaint);
    }

    private void drawAxisH(Canvas canvas, Rect rect) {
        mPaint.reset();
        mPaint.setAntiAlias(true);
        mPaint.setColor(mChartColor);
        mPaint.setStyle(Paint.Style.FILL);

        canvas.drawRect(rect, mPaint);
    }

    private void drawDate(Canvas canvas, Rect rect, long date) {

    }

    private void drawTitle(Canvas canvas, Rect rect, String title) {
        mPaint.reset();
        mPaint.setAntiAlias(true);
        mPaint.setColor(mChartColor);
        mPaint.setStyle(Paint.Style.FILL);

        canvas.drawRect(rect, mPaint);

        mPaint.setTextSize(mAxisTextSize + 1);
        mPaint.setColor(Color.WHITE);
        mPaint.setTypeface(Typeface.create(Typeface.DEFAULT, Typeface.BOLD));

        Rect textRect = new Rect();
        mPaint.getTextBounds(title, 0, title.length(), textRect);

        int x = (rect.width() - textRect.width()) / 2;
        int y = rect.bottom - ((rect.height() - textRect.height()) / 2);

        Log.d("xxx", "title:" + title);
        Log.d("xxx", "rect:" + rect.toString());
        Log.d("xxx", "textrect:" + textRect.toString());

        canvas.drawText(title, x, y, mPaint);
    }
}
