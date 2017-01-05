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

    private OnButtonClickListener mButtonClickListener = null;

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
            super(context, R.layout.view_contact_row, R.id.view_contact_label, array);
        }

        public View getView(int position, View convertView, ViewGroup parent) {
            if (convertView == null) {
                convertView = new ViewWatchContact(getContext());
                convertView.setLayoutParams(new AbsListView.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT));

                convertView.findViewById(R.id.view_contact_button1).setOnClickListener(mItemButton1ClickListener);
                convertView.findViewById(R.id.view_contact_button2).setOnClickListener(mItemButton1ClickListener);
            }

            ((ViewWatchContact) convertView).load(getItem(position));

            return convertView;
        }

        /*
        public View getView(int position, View convertView, ViewGroup parent) {
            ViewContact viewContact;

            if (convertView == null) {
                LayoutInflater inflater = LayoutInflater.from(mContext);
                convertView = inflater.inflate(R.layout.view_contact_row, null);

                convertView.setLayoutParams(new AbsListView.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT));

                viewContact = (ViewContact) convertView.findViewById(R.id.view_contact_row_body);

                viewContact.findViewById(R.id.view_contact_button1).setOnClickListener(mItemButton1ClickListener);
                viewContact.findViewById(R.id.view_contact_button2).setOnClickListener(mItemButton2ClickListener);

                convertView.setTag(viewContact);
            } else {
                viewContact = (ViewContact) convertView.getTag();
            }

            viewContact.load(getItem(position));

            return convertView;
        }
        */
    }

    private View.OnClickListener mItemButton1ClickListener = new OnClickListener() {
        @Override
        public void onClick(View view) {
            ViewWatchContact parent = ((ViewWatchContact) view.getParent().getParent());
            int position = getPositionForView(parent);

            if (mButtonClickListener != null)
                mButtonClickListener.onClick(parent, position, 0);
        }
    };

    private View.OnClickListener mItemButton2ClickListener = new OnClickListener() {
        @Override
        public void onClick(View view) {
            ViewWatchContact parent = ((ViewWatchContact) view.getParent().getParent());
            int position = getPositionForView(parent);

            if (mButtonClickListener != null)
                mButtonClickListener.onClick(parent, position, 1);
        }
    };

    public interface OnButtonClickListener {
        void onClick(ViewWatchContact contact, int position, int button);
    }

    public void setOnButtonClickListener(OnButtonClickListener listener) {
        mButtonClickListener = listener;
    }
}
