package com.kidsdynamic.swing.androidswingapp;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.RectF;
import android.graphics.Typeface;
import android.support.v4.content.ContextCompat;
import android.util.AttributeSet;
import android.util.Log;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;

/**
 * Created by 03543 on 2017/2/8.
 */

public class ViewCalendarDaily extends ViewGroup {

    private int mDesiredWidth;
    private int mDesiredHeight;

    private int mHourPadding;
    private int mHourMargin;

    private int mTextSize = 16;
    private int mTextColor = 0xFFFFFFFF;
    private int mNowColor = 0;

    private List<List<LayoutParams>> mLayoutMap;

    private Paint mPaint;
    private Rect mRect;

    public ViewCalendarDaily(Context context) {
        super(context);
        init(context, null);
    }

    public ViewCalendarDaily(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context, attrs);
    }

    private void init(Context context, AttributeSet attrs) {
        if (attrs != null) {
            TypedArray typedArray = context.obtainStyledAttributes(attrs, R.styleable.ViewCalendarDaily);

            final int count = typedArray.getIndexCount();
            for (int idx = 0; idx < count; idx++) {
                final int attr = typedArray.getIndex(idx);

                if (attr == R.styleable.ViewCalendarDaily_android_textSize) {
                    mTextSize = typedArray.getDimensionPixelOffset(attr, mTextSize);
                } else if (attr == R.styleable.ViewCalendarDaily_android_textColor) {
                    mTextColor = typedArray.getColor(attr, mTextColor);
                } else if (attr == R.styleable.ViewCalendarDaily_nowColor) {
                    mNowColor = typedArray.getColor(attr, mNowColor);
                }
            }
            typedArray.recycle();
        }

        setWillNotDraw(false);

        mDesiredWidth = getResources().getDisplayMetrics().widthPixels;
        mDesiredHeight = mTextSize * 48;

        mPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        makePaintHour();
        String patten = "12 AM";
        mHourPadding = (int) mPaint.measureText(patten, 0, patten.length());
        mHourMargin = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 10, getResources().getDisplayMetrics());

        mRect = new Rect();
    }

//    @Override
//    public void onFillCell(Context context) {
//    }

    @Override
    protected void onLayout(boolean changed, int left, int top, int right, int bottom) {
        final int count = getChildCount();

        buildLayoutMap();
        dumpLayoutMap();

        for (int idx = 0; idx < count; idx++) {
            View child = getChildAt(idx);
            LayoutParams layoutParams = (LayoutParams) child.getLayoutParams();

            if (child.getVisibility() == GONE)
                return;
            if (!checkLayoutParams(layoutParams))
                return;

            Rect rect = getLayoutRect(layoutParams);

            child.measure(MeasureSpec.makeMeasureSpec(rect.width(), MeasureSpec.AT_MOST), MeasureSpec.makeMeasureSpec(rect.height(), MeasureSpec.AT_MOST));
            child.layout(rect.left, rect.top, rect.right, rect.bottom);
        }
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

        setMeasuredDimension(width, height);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        for (int idx = 1; idx < 24; idx++) {
            drawHour(canvas, idx);
            drawSeparator(canvas, idx);
        }

        if (mNowColor != 0) {
            Calendar cale = Calendar.getInstance();
            drawNow(canvas, cale.get(Calendar.HOUR_OF_DAY));
        }
    }

    private void makePaintHour() {
        mPaint.reset();
        mPaint.setAntiAlias(true);
        mPaint.setColor(mTextColor);
        mPaint.setTypeface(Typeface.create(Typeface.DEFAULT, Typeface.BOLD));
        mPaint.setTextSize(mTextSize);
    }

    private void makePaintSeparator() {
        mPaint.reset();
        mPaint.setAntiAlias(true);
        mPaint.setColor(mTextColor);
        mPaint.setStyle(Paint.Style.STROKE);

        int width = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP,
                1, getResources().getDisplayMetrics());
        mPaint.setStrokeWidth(width);
    }

    private void makePaintNow() {
        mPaint.reset();

        mPaint.setAntiAlias(true);
        mPaint.setColor(mNowColor);
        mPaint.setStyle(Paint.Style.STROKE);

        int width = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP,
                2, getResources().getDisplayMetrics());
        mPaint.setStrokeWidth(width);
    }

    private void drawHour(Canvas canvas, int hour) {
        int height = getMeasuredHeight() - getPaddingTop() - getPaddingBottom();

        String text;
        if (hour <= 12)
            text = String.format(Locale.US, "%d AM", hour);
        else
            text = String.format(Locale.US, "%d PM", hour - 12);

        makePaintHour();
        mPaint.getTextBounds(text, 0, text.length(), mRect);
        int x = mHourPadding - (int) mPaint.measureText(text, 0, text.length());
        int y = (hour * 60 * height / 1440) + (mRect.height() / 2);

        canvas.drawText(text, x + getPaddingStart(), y + getPaddingTop(), mPaint);
    }

    private void drawSeparator(Canvas canvas, int hour) {
        int height = getMeasuredHeight() - getPaddingTop() - getPaddingBottom();

        makePaintSeparator();

        int x = mHourPadding + mHourMargin;
        int y = hour * 60 * height / 1440;

        canvas.drawLine(x, y, getMeasuredWidth(), y, mPaint);
    }

    private void drawNow(Canvas canvas, int hour) {
        int height = getMeasuredHeight() - getPaddingTop() - getPaddingBottom();

        makePaintNow();

        int x = mHourPadding + mHourMargin;
        int y = hour * 60 * height / 1440;

        canvas.drawLine(x, y, getMeasuredWidth(), y, mPaint);
    }

    public void addEvent(WatchEvent event) {

        LayoutParams layoutParams = new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT);

        Calendar cale = Calendar.getInstance();

        cale.setTimeInMillis(event.mStartDate);
        layoutParams.start_hour = cale.get(Calendar.HOUR_OF_DAY);
        layoutParams.start_minute = cale.get(Calendar.MINUTE);

        cale.setTimeInMillis(event.mEndDate);
        layoutParams.end_hour = cale.get(Calendar.HOUR_OF_DAY);
        layoutParams.end_minute = cale.get(Calendar.MINUTE);

        ViewCalendarCellDaily cell = new ViewCalendarCellDaily(getContext());
        cell.setEvent(event);
        cell.setBackgroundColor(WatchEvent.stringToColor(event.mColor));
        cell.setLayoutParams(layoutParams);

        addView(cell);
    }

    private Rect getLayoutRect(LayoutParams layoutParams) {
        Rect rect = new Rect();
        int width = getMeasuredWidth() - getPaddingStart() - getPaddingEnd() - mHourPadding - mHourMargin;
        int height = getMeasuredHeight() - getPaddingTop() - getPaddingBottom();

        int column_count = mLayoutMap.size();
        int column_width = width / column_count;
        int column;
        for (column = 0; column < column_count; column++) {
            List<LayoutParams> list = mLayoutMap.get(column);
            if (!list.contains(layoutParams))
                continue;

            rect.left = getPaddingStart() + mHourPadding + mHourMargin + (column_width * column);
            rect.right = rect.left + column_width;
        }

        rect.top = layoutParams.getStartPos() * height / 1440;
        rect.bottom = layoutParams.getEndPos() * height / 1440;

        return rect;
    }

    public void buildLayoutMap() {
        mLayoutMap = new ArrayList<>();

        int count = getChildCount();
        for (int idx = 0; idx < count; idx++) {
            View child = getChildAt(idx);
            LayoutParams layoutParams = (LayoutParams) child.getLayoutParams();

            // ignore child if invisible or layout param is not match
            if (child.getVisibility() == GONE)
                continue;
            if (!checkLayoutParams(layoutParams))
                continue;

            // find a colume to add child
            boolean added = false;
            for (List<LayoutParams> list : mLayoutMap) {
                if (!layoutParams.overLapping(list)) {
                    added = list.add(layoutParams);
                    break;
                }
            }

            // add column if there is not space
            if (!added) {
                List<LayoutParams> list = new ArrayList<>();
                list.add(layoutParams);
                mLayoutMap.add(list);
            }
        }
    }

    public void dumpLayoutMap() {
        Log.d("xxx", "colume count:" + mLayoutMap.size());

        int count = mLayoutMap.size();
        for (int idx = 0; idx < count; idx++) {
            List<LayoutParams> colume = mLayoutMap.get(idx);
            Log.d("xxx", "colume(" + idx + "):" + colume.size());

            for (LayoutParams layout : colume)
                Log.d("xxx", layout.toString());
        }
    }

    @Override
    public LayoutParams generateLayoutParams(AttributeSet attrs) {
        return new LayoutParams(getContext(), attrs);
    }

    @Override
    protected LayoutParams generateDefaultLayoutParams() {
        return new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT);
    }

    @Override
    protected LayoutParams generateLayoutParams(ViewGroup.LayoutParams layoutParams) {
        return new LayoutParams(layoutParams);
    }

    @Override
    protected boolean checkLayoutParams(ViewGroup.LayoutParams layoutParams) {
        return layoutParams instanceof LayoutParams;
    }

    public static class LayoutParams extends MarginLayoutParams {

        public int gravity = Gravity.TOP | Gravity.START;
        public int start_hour = 0;
        public int start_minute = 0;
        public int end_hour = 0;
        public int end_minute = 0;

        public LayoutParams(Context context, AttributeSet attrs) {
            super(context, attrs);

            if (attrs != null) {
                TypedArray typedArray = context.obtainStyledAttributes(
                        attrs, R.styleable.ViewCalendarDaily);

                final int count = typedArray.getIndexCount();
                for (int idx = 0; idx < count; idx++) {
                    final int attr = typedArray.getIndex(idx);

                    if (attr == R.styleable.ViewCalendarDaily_android_layout_gravity) {
                        gravity = typedArray.getInteger(attr, gravity);
                    } else if (attr == R.styleable.ViewCalendarDaily_start_hour) {
                        start_hour = typedArray.getInteger(attr, start_hour);
                    } else if (attr == R.styleable.ViewCalendarDaily_start_minute) {
                        start_minute = typedArray.getInteger(attr, start_minute);
                    } else if (attr == R.styleable.ViewCalendarDaily_end_hour) {
                        end_hour = typedArray.getInteger(attr, end_hour);
                    } else if (attr == R.styleable.ViewCalendarDaily_end_minute) {
                        end_minute = typedArray.getInteger(attr, end_minute);
                    }
                }

                typedArray.recycle();
            }
        }

        public LayoutParams(int width, int height) {
            super(width, height);
        }

        public LayoutParams(ViewGroup.LayoutParams source) {
            super(source);
        }

        public boolean overLapping(LayoutParams other) {
            int start1 = this.start_hour * 60 + this.start_minute;
            int end1 = this.end_hour * 60 + this.end_minute;
            int start2 = other.start_hour * 60 + other.start_minute;
            int end2 = other.end_hour * 60 + other.end_minute;

            return !(end1 <= start2 || end2 <= start1);
        }

        public boolean overLapping(List<LayoutParams> list) {
            for (LayoutParams other : list) {
                if (overLapping(other))
                    return true;
            }
            return false;
        }

        public int getPos(int hour, int minute) {
            return hour * 60 + minute;
        }

        public int getStartPos() {
            return getPos(start_hour, start_minute);
        }

        public int getEndPos() {
            return getPos(end_hour, end_minute);
        }

        @Override
        public String toString() {
            return new StringBuilder()
                    .append("{start_hour:").append(start_hour)
                    .append(" start_minute:").append(start_minute)
                    .append(" end_hour:").append(end_hour)
                    .append(" end_minute:").append(end_minute)
                    .append(" gravity:").append(gravity)
                    .append("}").toString();
        }
    }
}
