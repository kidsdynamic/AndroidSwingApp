package com.kidsdynamic.swing.androidswingapp;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.PointF;
import android.graphics.Rect;
import android.graphics.Typeface;
import android.util.AttributeSet;
import android.util.TypedValue;

import java.util.List;
import java.util.Locale;

/**
 * Created by 03543 on 2017/2/22.
 */

public class ViewChartKDWeek extends ViewChart {

    private int mDesiredWidth = 160;
    private int mDesiredHeight = 100;

    private Paint mPaint;
    private Rect mRect;
    private Rect mDayRect;
    private Rect mAxisHRect;

    public ViewChartKDWeek(Context context) {
        super(context);
        init(context, null);
    }

    public ViewChartKDWeek(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context, attrs);
    }

    public ViewChartKDWeek(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        init(context, attrs);
    }

    private void init(Context context, AttributeSet attrs) {
        mPaint = new Paint();
        mRect = new Rect();
        mDayRect = new Rect();
        mAxisHRect = new Rect();
    }

    @Override
    public void setValue(PointF point) {
        setValue(point.x, point.y);
    }

    @Override
    public void setValue(float x, float y) {
        boolean found = false;

        for (PointF point : mValue) {
            if (point.x != x)
                continue;

            found = true;
            point.y = y;
        }

        if (!found)
            mValue.add(new PointF(x, y));
    }

    public void setValue(List<Integer> list) {
        int count = list.size();
        for (int x = 0; x < count; x++)
            setValue(x, list.get(x));
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
        mAxisHRect.set(mRect);
        mAxisHRect.top = mRect.top + (mRect.height() * 4 / 5);
    }

    @Override
    public void onDraw(Canvas canvas) {
        int dayWidth = mRect.width() * 162 / 1734;  // 1.62:1.00 Golder ratio. 7 days + 6 gaps = 1734.
        int gapWidth = mRect.width() * 100 / 1734;

        mPaint.reset();
        mPaint.setAntiAlias(true);
        mPaint.setColor(getChartColor());
        mPaint.setStyle(Paint.Style.FILL);

        mDayRect.set(mRect.left, mRect.top, mRect.left + dayWidth, mAxisHRect.top - 1);
        for (int idx = 0; idx < 7; idx++) {
            drawDay(canvas, mDayRect, idx);
            mDayRect.offset(dayWidth + gapWidth, 0);
        }

        if (isAxisHEnabled()) {
            drawAxisH(canvas, mAxisHRect);
        }
    }

    private void drawDay(Canvas canvas, Rect rect, int day) {
        Rect barRect = new Rect(rect);

        int value = 0;
        if (day < mValue.size())
            value = (int) mValue.get(day).y;

        int bound = value;
        if (bound > mAxisVMax)
            bound = Math.round(mAxisVMax);
        if (bound < mAxisVMin)
            bound = 0;

        barRect.top = barRect.bottom - (int) (bound * barRect.height() / mAxisVMax);

        mPaint.reset();
        mPaint.setAntiAlias(true);
        mPaint.setColor(getChartColor());
        mPaint.setStyle(Paint.Style.FILL);

        canvas.drawRect(barRect, mPaint);

        int textX, textY;
        Rect textRect = new Rect();
        String text = String.format(Locale.US, "%d", (int) value);

        mPaint.setTypeface(Typeface.create(Typeface.DEFAULT, Typeface.BOLD));
        mPaint.setTextSize(getChartTextSize() + 1);
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
        mPaint.setColor(getChartColor());
        mPaint.setStyle(Paint.Style.FILL);

        int y = rect.top;
        y += (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP,
                mAxisWidth, getResources().getDisplayMetrics());
        canvas.drawRect(rect.left, rect.top, rect.right, y, mPaint);
    }
}
