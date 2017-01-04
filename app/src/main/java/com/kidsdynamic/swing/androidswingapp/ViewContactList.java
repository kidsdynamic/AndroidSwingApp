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
            super(context, R.layout.view_contact, R.id.view_contact_label, array);
        }

        public View getView(int position, View convertView, ViewGroup parent) {
            Holder holder;

            if (convertView == null) {
                LayoutInflater inflater = LayoutInflater.from(mContext);
                convertView = inflater.inflate(R.layout.view_contact, null);

                convertView.setLayoutParams(new AbsListView.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT));

                ViewPhoto viewPhoto = (ViewPhoto) convertView.findViewById(R.id.view_contact_photo);
                TextView viewLabel = (TextView) convertView.findViewById(R.id.view_contact_label);

                holder = new Holder(viewPhoto, viewLabel);

                convertView.setTag(holder);
            } else {
                holder = (Holder) convertView.getTag();
            }

            holder.load(getItem(position));

            return convertView;
        }
    }

    public class Holder {
        public ViewPhoto mPhoto;
        public TextView mLabel;

        public Holder(ViewPhoto photo, TextView label) {
            mPhoto = photo;
            mLabel = label;
        }

        public void load(ContactItem item) {
            mPhoto.setBackgroundColor(Color.RED);
            mLabel.setText(item.getLabel());

            if (item instanceof ContactItem.Request) {
            } else if (item instanceof ContactItem.Kid) {

            }
        }
    }
}
