package com.kidsdynamic.swing.androidswingapp;

import android.content.Context;
import android.graphics.Color;
import android.support.v4.view.ViewPager;
import android.util.AttributeSet;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by 03543 on 2017/1/4.
 */

public class ViewContactList extends ListView {
    Context mContext = null;
    Adapter mAdapter = null;

    private OnButtonClickListener mButtonClickListener = null;

    public ViewContactList(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }

    public ViewContactList(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context);
    }

    private void init(Context context) {
        mContext = context;
        mAdapter = new Adapter(context, new ArrayList<ContactItem>());
        setAdapter(mAdapter);
    }

    public void addItem(ContactItem item) {
        mAdapter.add(item);
    }

    public void clear() {
        mAdapter.clear();
    }

    public class Adapter extends ArrayAdapter<ContactItem> {

        public Adapter(Context context, List<ContactItem> array) {
            super(context, R.layout.view_contact_row, R.id.view_contact_label, array);
        }

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
    }

    private View.OnClickListener mItemButton1ClickListener = new OnClickListener() {
        @Override
        public void onClick(View view) {
            ViewContact parent = ((ViewContact) view.getParent().getParent());
            int position = getPositionForView(parent);

            if(mButtonClickListener != null)
                mButtonClickListener.onClick(parent, position, 0);
        }
    };

    private View.OnClickListener mItemButton2ClickListener = new OnClickListener() {
        @Override
        public void onClick(View view) {
            ViewContact parent = ((ViewContact) view.getParent().getParent());
            int position = getPositionForView(parent);

            if(mButtonClickListener != null)
                mButtonClickListener.onClick(parent, position, 1);
        }
    };

    public interface OnButtonClickListener {
        void onClick(ViewContact contact, int position, int button);
    }

    public void setOnButtonClickListener(OnButtonClickListener listener) {
        mButtonClickListener = listener;
    }
}
