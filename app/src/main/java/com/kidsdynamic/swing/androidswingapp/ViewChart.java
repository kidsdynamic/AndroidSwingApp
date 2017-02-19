package com.kidsdynamic.swing.androidswingapp;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Color;
import android.graphics.PointF;
import android.graphics.Typeface;
import android.util.AttributeSet;
import android.view.View;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by 03543 on 2017/2/19.
 */

public abstract class ViewChart extends View {

    private int mChartColor = Color.BLACK;
    private int mChartWidth = 10;
    private int mChartTextColor = Color.BLACK;
    private int mChartTextSize = 30;
    private int mChartTextStyle = Typeface.NORMAL;
    private int mNodeColor = Color.GRAY;
    private int mNodeSize = 15;
    private int mAxisColor = Color.BLACK;
    private int mAxisWidth = 6;
    private int mAxisTextSize = 30;
    private int mAxisTextColor = Color.BLACK;
    private int mAxisTextStyle = Typeface.NORMAL;
    private boolean mAxisHEnabled = false;
    private boolean mAxisVEnabled = false;

    private Float mAxisHMax = 100.f;
    private Float mAxisHMin = 0.f;
    private Float mAxisVMax = 100.f;
    private Float mAxisVMin = 0.f;
    private List<Float> mAxisHMarker;
    private List<Float> mAxisVMarker;
    private List<Float> mAxisHNode;
    private List<Float> mAxisVNode;
    private List<PointF> mValueList;

    public ViewChart(Context context) {
        super(context);
        init(context, null);
    }

    public ViewChart(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context, attrs);
    }

    public ViewChart(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        init(context, attrs);
    }

    private void init(Context context, AttributeSet attrs) {
        if (attrs != null) {
            TypedArray typedArray = context.obtainStyledAttributes(attrs, R.styleable.ViewChart);

            final int count = typedArray.getIndexCount();
            for (int idx = 0; idx < count; idx++) {
                final int attr = typedArray.getIndex(idx);

                if (attr == R.styleable.ViewChart_chartColor) {
                    mChartColor = typedArray.getColor(attr, mChartColor);
                } else if (attr == R.styleable.ViewChart_chartWidth) {
                    mChartWidth = typedArray.getDimensionPixelSize(attr, mChartWidth);
                } else if (attr == R.styleable.ViewChart_chartTextColor) {
                    mChartTextColor = typedArray.getColor(attr, mChartTextColor);
                } else if (attr == R.styleable.ViewChart_chartTextSize) {
                    mChartTextSize = typedArray.getDimensionPixelOffset(attr, mChartTextSize);
                } else if (attr == R.styleable.ViewChart_chartTextStyle) {
                    mChartTextStyle = typedArray.getInteger(attr, mChartTextStyle);
                } else if (attr == R.styleable.ViewChart_nodeColor) {
                    mNodeColor = typedArray.getColor(attr, mNodeColor);
                } else if (attr == R.styleable.ViewChart_nodeSize) {
                    mNodeSize = typedArray.getDimensionPixelSize(attr, mNodeSize);
                } else if (attr == R.styleable.ViewChart_axisColor) {
                    mAxisColor = typedArray.getColor(attr, mAxisColor);
                } else if (attr == R.styleable.ViewChart_axisWidth) {
                    mAxisWidth = typedArray.getDimensionPixelSize(attr, mAxisWidth);
                } else if (attr == R.styleable.ViewChart_axisTextColor) {
                    mAxisTextColor = typedArray.getColor(attr, mAxisTextColor);
                } else if (attr == R.styleable.ViewChart_axisTextSize) {
                    mAxisTextSize = typedArray.getDimensionPixelOffset(attr, mAxisTextSize);
                } else if (attr == R.styleable.ViewChart_axisTextStyle) {
                    mAxisTextStyle = typedArray.getInteger(attr, mAxisTextStyle);
                } else if (attr == R.styleable.ViewChart_axisHEnabled) {
                    mAxisHEnabled = typedArray.getBoolean(attr, mAxisHEnabled);
                } else if (attr == R.styleable.ViewChart_axisVEnabled) {
                    mAxisVEnabled = typedArray.getBoolean(attr, mAxisVEnabled);
                }
            }
            typedArray.recycle();
        }

        mValueList = new ArrayList<>();

        mAxisHNode = new ArrayList<>();
        mAxisVNode = new ArrayList<>();

        mAxisHMarker = new ArrayList<>();
        mAxisVMarker = new ArrayList<>();
    }

    public int getChartColor() {
        return mChartColor;
    }

    public void setChartColor(int color) {
        mChartColor = color;
    }

    public int getChartWidth() {
        return mChartWidth;
    }

    public void setChartWidth(int width) {
        mChartWidth = width;
    }

    public int getChartTextColor() {
        return mChartTextColor;
    }

    public void setChartTextColor(int color) {
        mChartTextColor = color;
    }

    public int getChartTextSize() {
        return mChartTextSize;
    }

    public void setChartTextSize(int size) {
        mChartTextSize = size;
    }

    public int getNodeColor() {
        return mNodeColor;
    }

    public void setNodeColor(int color) {
        mNodeColor = color;
    }

    public int getNodeSize() {
        return mNodeSize;
    }

    public void setNodeSize(int size) {
        mNodeSize = size;
    }

    public int getAxisColor() {
        return mAxisColor;
    }

    public void setAxisColor(int color) {
        mAxisColor = color;
    }

    public int getAxisWidth() {
        return mAxisWidth;
    }

    public void setAxisWidth(int width) {
        mAxisWidth = width;
    }

    public int getAxisTextSize() {
        return mAxisTextSize;
    }

    public void setAxisTextSize(int size) {
        mAxisTextSize = size;
    }

    public int getAxisTextColor() {
        return mAxisTextColor;
    }

    public void setAxisTextColor(int color) {
        mAxisTextColor = color;
    }

    public boolean isAxisHEnabled() {
        return mAxisHEnabled;
    }

    public void setAxisHEnabled(boolean enabled) {
        mAxisHEnabled = enabled;
    }

    public boolean isAxisVEnabled() {
        return mAxisVEnabled;
    }

    public void setAxisVEnabled(boolean enabled) {
        mAxisVEnabled = enabled;
    }

    public void addAxisHMarker(float marker) {
        mAxisHMarker.add(marker);
    }

    public List<Float> getAxisHMarker() {
        return mAxisHMarker;
    }

    public void clearAxisHMarker() {
        mAxisHMarker.clear();
    }

    public void addAxisVMarker(float marker) {
        mAxisVMarker.add(marker);
    }

    public List<Float> getAxisVMarker() {
        return mAxisVMarker;
    }

    public void clearAxisVMarker() {
        mAxisVMarker.clear();
    }

    public void addValue(PointF value) {
        mValueList.add(value);
    }

    public void addValue(float x, float y) {
        PointF value = new PointF(x, y);
        addValue(value);
    }

    public void clearValue() {
        mValueList.clear();
    }

    public void addAxisHNode(float node) {
        mAxisHNode.add(node);
    }

    public List<Float> getAxisHNode() {
        return mAxisHNode;
    }

    public void clearAxisHNode() {
        mAxisHNode.clear();
    }

    public void addAxisVNode(float node) {
        mAxisVNode.add(node);
    }

    public List<Float> getAxisVNode() {
        return mAxisVNode;
    }

    public void clearAxisVNode() {
        mAxisVNode.clear();
    }
}
