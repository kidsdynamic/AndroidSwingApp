package com.kidsdynamic.swing.androidswingapp;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.PointF;
import android.graphics.Rect;
import android.graphics.Typeface;
import android.support.v4.util.Pair;
import android.util.AttributeSet;
import android.util.TypedValue;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
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
    private int mEndpointRadius;

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

        mEndpointRadius = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP,
                mChartWidth / 2, getResources().getDisplayMetrics());
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
        Rect curveRect = new Rect(rect);
        int paddintY = curveRect.height() / 6;  // add paddingY to avoid curve over range
        curveRect.top += paddintY;
        curveRect.bottom -= paddintY;

        List<PointF> curve = new ArrayList<>();

        int xpos = curveRect.left + mEndpointRadius;
        int xgap = (curveRect.width() - (mEndpointRadius * 2)) / mValue.size();
        for (int idx = 0; idx < mValue.size(); idx++) {
            int value = 0;
            if (idx < mValue.size())
                value = mValue.get(idx).mSteps;

            int bound = value;
            if (bound > mAxisVMax)
                bound = Math.round(mAxisVMax);
            if (bound < mAxisVMin)
                bound = 0;

            PointF point = new PointF();
            point.x = xpos;
            point.y = curveRect.bottom - (int) (bound * curveRect.height() / mAxisVMax);

            curve.add(point);
            xpos += xgap;
        }

        mPaint.reset();
        mPaint.setAntiAlias(true);
        mPaint.setColor(mChartColor);
        mPaint.setStrokeWidth(mChartWidth);
        mPaint.setStyle(Paint.Style.STROKE);

        canvas.drawPath(makePath(curve), mPaint);

        mPaint.setStrokeWidth(0);
        mPaint.setColor(mChartColor);
        mPaint.setStyle(Paint.Style.FILL);
        canvas.drawCircle(curve.get(0).x, curve.get(0).y, mEndpointRadius, mPaint);
        canvas.drawCircle(curve.get(curve.size() - 1).x, curve.get(curve.size() - 1).y, mEndpointRadius, mPaint);

        mPaint.setStrokeWidth(0);
        mPaint.setColor(Color.WHITE);
        mPaint.setStyle(Paint.Style.FILL);
        int radius = mEndpointRadius / 2;
        canvas.drawCircle(curve.get(0).x, curve.get(0).y, radius, mPaint);
        canvas.drawCircle(curve.get(curve.size() - 1).x, curve.get(curve.size() - 1).y, radius, mPaint);
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

    public Path makePath(List<PointF> points) {
        final Path path = new Path();

        final List<List<PointF>> continuousCurves = prepareListOfContinuousCurves(points);
        for (List<PointF> curve : continuousCurves) {
            addCurveToPath(path, curve);
        }

        return path;
    }

    private void addCurveToPath(Path outPath, List<PointF> curve) {
        List<Pair<PointF, PointF>> bezierControlPoints = Collections.emptyList();
        if (curve.size() > 1) {
            bezierControlPoints = getBezierControlPoints(curve);
        }

        for (int i = 0, size = curve.size(); i < size; i++) {
            final PointF point = curve.get(i);
            if (i == 0) {
                outPath.moveTo(point.x, point.y);
            } else {
                final Pair<PointF, PointF> bezierControlPointPair = bezierControlPoints.get(i - 1);
                outPath.cubicTo(bezierControlPointPair.first.x, bezierControlPointPair.first.y, bezierControlPointPair.second.x, bezierControlPointPair.second.y, point.x, point.y);
            }
        }
    }

    private List<List<PointF>> prepareListOfContinuousCurves(List<PointF> points) {
        final List<List<PointF>> continuousCurves = new ArrayList<>();

        List<PointF> curve = null;
        for (PointF point : points) {
            if (point == null) {
                if (curve != null) {
                    continuousCurves.add(curve);
                }
                curve = null;
            } else {
                if (curve == null) {
                    curve = new ArrayList<>();
                }
                curve.add(point);
            }
        }
        if (curve != null) {
            continuousCurves.add(curve);
        }

        return continuousCurves;
    }

    private List<Pair<PointF, PointF>> getBezierControlPoints(List<PointF> knots) {
        if (knots == null) {
            throw new NullPointerException("Knots cannot be null.");
        }

        int controlPointsSize = knots.size() - 1;
        if (controlPointsSize < 1) {
            throw new IllegalArgumentException("At least two knot points required.");
        }

        // Special case: Bezier curve should be a straight line.
        if (controlPointsSize == 1) {
            final PointF firstControlPoint = new PointF((2 * knots.get(0).x + knots.get(1).x) / 3, (2 * knots.get(0).y + knots.get(1).y) / 3);
            final PointF secondControlPoint = new PointF(2 * firstControlPoint.x - knots.get(0).x, 2 * firstControlPoint.y - knots.get(0).y);

            final List<Pair<PointF, PointF>> bezierControlPoints = new ArrayList<>();
            bezierControlPoints.add(Pair.create(firstControlPoint, secondControlPoint));
            return bezierControlPoints;
        }

        // Calculate first Bezier control points
        // Right hand side vector
        float[] rhs = new float[controlPointsSize];

        // Set right hand side X values
        for (int i = 1; i < controlPointsSize - 1; ++i) {
            rhs[i] = 4 * knots.get(i).x + 2 * knots.get(i + 1).x;
        }
        rhs[0] = knots.get(0).x + 2 * knots.get(1).x;
        rhs[controlPointsSize - 1] = (8 * knots.get(controlPointsSize - 1).x + knots.get(controlPointsSize).x) / 2.0f;
        // Get first control points X-values
        float[] x = getFirstControlPoints(rhs);

        // Set right hand side Y values
        for (int i = 1; i < controlPointsSize - 1; ++i) {
            rhs[i] = 4 * knots.get(i).y + 2 * knots.get(i + 1).y;
        }
        rhs[0] = knots.get(0).y + 2 * knots.get(1).y;
        rhs[controlPointsSize - 1] = (8 * knots.get(controlPointsSize - 1).y + knots.get(controlPointsSize).y) / 2.0f;
        // Get first control points Y-values
        float[] y = getFirstControlPoints(rhs);

        // Fill output arrays.
        //firstControlPoints = new PointF[n];
        //secondControlPoints = new PointF[n];
        final List<Pair<PointF, PointF>> bezierControlPoints = new ArrayList<>();
        for (int i = 0; i < controlPointsSize; ++i) {
            // First control point
            final PointF firstControlPoint = new PointF(x[i], y[i]);

            // Second control point
            final PointF secondControlPoint;
            if (i < controlPointsSize - 1) {
                secondControlPoint = new PointF(2 * knots.get(i + 1).x - x[i + 1], 2 * knots.get(i + 1).y - y[i + 1]);
            } else {
                secondControlPoint = new PointF((knots.get(controlPointsSize).x + x[controlPointsSize - 1]) / 2, (knots.get(controlPointsSize).y + y[controlPointsSize - 1]) / 2);
            }

            bezierControlPoints.add(Pair.create(firstControlPoint, secondControlPoint));
        }

        return bezierControlPoints;
    }

    private float[] getFirstControlPoints(float[] rhs) {
        int n = rhs.length;
        float[] x = new float[n]; // Solution vector.
        float[] tmp = new float[n]; // Temp workspace.

        float b = 2.0f;
        x[0] = rhs[0] / b;
        for (int i = 1; i < n; i++) // Decomposition and forward substitution.
        {
            tmp[i] = 1 / b;
            b = (i < n - 1 ? 4.0f : 3.5f) - tmp[i];
            x[i] = (rhs[i] - x[i - 1]) / b;
        }
        for (int i = 1; i < n; i++) {
            x[n - i - 1] -= tmp[n - i] * x[n - i]; // Backsubstitution.
        }

        return x;
    }
}

