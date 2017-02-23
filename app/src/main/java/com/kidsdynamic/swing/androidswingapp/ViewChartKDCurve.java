package com.kidsdynamic.swing.androidswingapp;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.Typeface;
import android.util.AttributeSet;
import android.util.TypedValue;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;

/**
 * Created by 03543 on 2017/2/24.
 */

public class ViewChartKDCurve extends ViewChart {

    private int mDesiredWidth = 160;
    private int mDesiredHeight = 100;

    private Paint mPaint;
    private Rect mRect;
    private Rect mRectH;
    private Rect mRectV;

    private List<WatchActivity.Act> mValue;
    private String mTitle = "Step";

    public ViewChartKDCurve(Context context) {
        super(context);
        init(context, null);
    }

    public ViewChartKDCurve(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context, attrs);
    }

    public ViewChartKDCurve(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        init(context, attrs);
    }

    private void init(Context context, AttributeSet attrs) {
        mValue = new ArrayList<>();

        mPaint = new Paint();
        mRect = new Rect();
        mRectV = new Rect();
        mRectH = new Rect();
    }

    public void setValue(List<WatchActivity.Act> list) {
        mValue.clear();

        for (WatchActivity.Act act : list) {
            mValue.add(act);
        }
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
        int colCount = mValue.size();
        int gapCount = colCount - 1;

        int sum = (colCount * 162) + (gapCount * 100); // 1.62:1.00 Golder ratio.

        int colWidth = mRect.width() * 162 / sum;
        int gapWidth = mRect.width() * 100 / sum;

        mRectV.set(mRect.left, mRect.top, mRect.right, mRect.top + mRect.height() * 3 / 4);
        drawCurve(canvas, mRectV);

        mRectV.top = mRectV.bottom;
        mRectV.bottom = mRectV.top + (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP,
                mAxisWidth, getResources().getDisplayMetrics());
        drawAxisH(canvas, mRectV);

        mRectV.top = mRectV.bottom;
        mRectV.bottom = (mRect.bottom + mRectV.top) / 2;
        mRectH.set(mRectV.left, mRectV.top, mRectV.left + colWidth, mRectV.bottom);
        for (int idx = 0; idx < mValue.size(); idx++) {
            drawDate(canvas, mRectH, idx);
            mRectH.offset(colWidth + gapWidth, 0);
        }

        mRectV.top = mRectV.bottom;
        mRectV.bottom = mRect.bottom;
        drawTitle(canvas, mRectV, mTitle);
    }

    private void drawCurve(Canvas canvas, Rect rect) {

    }

    private void drawAxisH(Canvas canvas, Rect rect) {
        mPaint.reset();
        mPaint.setAntiAlias(true);
        mPaint.setColor(mChartColor);
        mPaint.setStyle(Paint.Style.FILL);

        canvas.drawRect(rect, mPaint);
    }

    private void drawDate(Canvas canvas, Rect rect, int index) {
        if (index != 0 && index != (mValue.size() - 1))
            return;

        Calendar cale = Calendar.getInstance();
        long date = cale.getTimeInMillis();

        if (index < mValue.size())
            date = mValue.get(index).mTimestamp;

        cale.setTimeInMillis(date);

        String text = String.format(Locale.US, "%d/%d", cale.get(Calendar.MONTH) + 1, cale.get(Calendar.DAY_OF_MONTH));

        mPaint.reset();
        mPaint.setAntiAlias(true);
        mPaint.setTypeface(Typeface.create(Typeface.DEFAULT, mChartTextStyle));
        mPaint.setTextSize(mAxisTextSize + 1);
        mPaint.setColor(Color.WHITE);

        Rect textRect = new Rect();
        mPaint.getTextBounds(text, 0, text.length(), textRect);

        int textX = rect.left + (rect.width() - textRect.width()) / 2;
        int textY = rect.top + mAxisTextSize + 5; // padding 5

        canvas.drawText(text, textX, textY, mPaint);
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

        int x = rect.left + (rect.width() - textRect.width()) / 2;
        int y = rect.bottom - ((rect.height() - textRect.height()) / 2) - 5; // padding 5

        canvas.drawText(title, x, y, mPaint);
    }
}

