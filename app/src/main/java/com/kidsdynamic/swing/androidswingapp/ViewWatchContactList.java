package com.kidsdynamic.swing.androidswingapp;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.ArrayAdapter;
import android.widget.LinearLayout;
import android.widget.ListView;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by 03543 on 2017/1/4.
 */

public class ViewWatchContactList extends ListView {
    Context mContext = null;
    Adapter mAdapter = null;

    final static int ON_ITEM = 0x00;
    final static int ON_BUTTON1 = 0x01;
    final static int ON_BUTTON2 = 0x02;

    private OnContactClickListener mButtonClickListener = null;

    public ViewWatchContactList(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }

    public ViewWatchContactList(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context);
    }

    private void init(Context context) {
        mContext = context;
        mAdapter = new Adapter(context, new ArrayList<WatchContact>());
        setAdapter(mAdapter);
    }

    public void addItem(WatchContact item) {
        mAdapter.add(item);
    }

    public void clear() {
        mAdapter.clear();
    }

    public class Adapter extends ArrayAdapter<WatchContact> {

        public Adapter(Context context, List<WatchContact> array) {
            super(context, R.layout.view_watch_contact_row, R.id.view_watch_contact_label, array);
        }

        public View getView(int position, View convertView, ViewGroup parent) {
            if (convertView == null) {
                convertView = new ViewWatchContact(getContext());
                convertView.setLayoutParams(new AbsListView.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT));

                convertView.setOnClickListener(mItemClickListener);
                convertView.findViewById(R.id.view_watch_contact_button1).setOnClickListener(mItemButton1ClickListener);
                convertView.findViewById(R.id.view_watch_contact_button2).setOnClickListener(mItemButton1ClickListener);
            }

            ((ViewWatchContact) convertView).load(getItem(position));

            return convertView;
        }
    }

    private View.OnClickListener mItemClickListener = new OnClickListener() {
        @Override
        public void onClick(View view) {
            int position = getPositionForView(view);

            if (mButtonClickListener != null)
                mButtonClickListener.onClick((ViewWatchContact) view, position, ON_ITEM);
        }
    };

    private View.OnClickListener mItemButton1ClickListener = new OnClickListener() {
        @Override
        public void onClick(View view) {
            ViewWatchContact parent = ((ViewWatchContact) view.getParent().getParent());
            int position = getPositionForView(parent);

            if (mButtonClickListener != null)
                mButtonClickListener.onClick(parent, position, ON_ITEM | ON_BUTTON1);
        }
    };

    private View.OnClickListener mItemButton2ClickListener = new OnClickListener() {
        @Override
        public void onClick(View view) {
            ViewWatchContact parent = ((ViewWatchContact) view.getParent().getParent());
            int position = getPositionForView(parent);

            if (mButtonClickListener != null)
                mButtonClickListener.onClick(parent, position, ON_ITEM | ON_BUTTON2);
        }
    };

    public interface OnContactClickListener {
        void onClick(ViewWatchContact contact, int position, int button);
    }

    public void setOnButtonClickListener(OnContactClickListener listener) {
        mButtonClickListener = listener;
    }
}
