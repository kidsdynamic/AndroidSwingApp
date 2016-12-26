package com.kidsdynamic.swing.androidswingapp;

import android.content.Context;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import java.util.ArrayList;

/**
 * Created by 03543 on 2016/12/22.
 */

public class ViewPagerRegistration extends ViewPager {
    private ArrayList<View> mViewList;

    private Button mButtonLogin;
    private Button mButtonFacebook;
    private Button mButtonEmail;
    private Button mButtonPassword;
    private Button mButtonFirstName;
    private Button mButtonLastName;
    private Button mButtonPhone;
    private Button mButtonZip;
    private Button mButtonWatchYes;
    private Button mButtonWatchNo;
    private Button mButtonKidFirstName;
    private Button mButtonKidLastName;

    private OnFinishListener mOnFinishListener = null;

    public ViewPagerRegistration(Context context, AttributeSet attrs) {
        super(context, attrs);

        mViewList = new ArrayList<>();

        LayoutInflater inflater = (LayoutInflater) context.getSystemService(context.LAYOUT_INFLATER_SERVICE);

        View view;

        // Page Login
        view = inflater.inflate(R.layout.registration_login, null);
        mViewList.add(view);

        mButtonLogin = (Button) view.findViewById(R.id.registration_login_login);
        mButtonLogin.setOnClickListener(mButtonClickListener);

        mButtonFacebook = (Button) view.findViewById(R.id.registration_login_facebook);
        mButtonFacebook.setOnClickListener(mButtonClickListener);

        // Page Account
        view = inflater.inflate(R.layout.registration_account, null);
        mViewList.add(view);

        mButtonEmail = (Button) view.findViewById(R.id.registration_account_email);
        mButtonEmail.setOnClickListener(mButtonClickListener);

        mButtonPassword = (Button) view.findViewById(R.id.registration_account_password);
        mButtonPassword.setOnClickListener(mButtonClickListener);

        // Page Info
        view = inflater.inflate(R.layout.registration_info, null);
        mViewList.add(view);

        mButtonFirstName = (Button) view.findViewById(R.id.registration_info_first);
        mButtonFirstName.setOnClickListener(mButtonClickListener);

        mButtonLastName = (Button) view.findViewById(R.id.registration_info_last);
        mButtonLastName.setOnClickListener(mButtonClickListener);

        mButtonPhone = (Button) view.findViewById(R.id.registration_info_phone);
        mButtonPhone.setOnClickListener(mButtonClickListener);

        mButtonZip = (Button) view.findViewById(R.id.registration_info_zip);
        mButtonZip.setOnClickListener(mButtonClickListener);

        // Page Have Watch
        view = inflater.inflate(R.layout.registration_have_watch, null);
        mViewList.add(view);

        mButtonWatchYes = (Button) view.findViewById(R.id.registration_have_watch_yes);
        mButtonWatchYes.setOnClickListener(mButtonClickListener);

        mButtonWatchNo = (Button) view.findViewById(R.id.registration_have_watch_no);
        mButtonWatchNo.setOnClickListener(mButtonClickListener);

        // Page Select Watch
        view = inflater.inflate(R.layout.registration_select_watch, null);
        mViewList.add(view);

        // Page Kid
        view = inflater.inflate(R.layout.registration_kid, null);
        mViewList.add(view);

        mButtonKidFirstName = (Button) view.findViewById(R.id.registration_kid_first);
        mButtonKidFirstName.setOnClickListener(mButtonClickListener);

        mButtonKidLastName = (Button) view.findViewById(R.id.registration_kid_last);
        mButtonKidLastName.setOnClickListener(mButtonClickListener);

        setAdapter(mPagerAdapter);

        //beginFakeDrag();    // Disable scroll
    }

    Button.OnClickListener mButtonClickListener = new OnClickListener() {
        @Override
        public void onClick(View view) {
            if (view == mButtonLogin) {
                setCurrentItem(1, true);
            } else if (view == mButtonFacebook) {
                setCurrentItem(2, true);
            } else if (view == mButtonEmail) {
                setCurrentItem(2, true);
            } else if (view == mButtonPassword) {
                setCurrentItem(2, true);
            } else if (view == mButtonFirstName) {
                setCurrentItem(3, true);
            } else if (view == mButtonLastName) {
                setCurrentItem(3, true);
            } else if (view == mButtonPhone) {
                setCurrentItem(3, true);
            } else if (view == mButtonZip) {
                setCurrentItem(3, true);
            } else if (view == mButtonWatchYes) {
                setCurrentItem(4, true);
            } else if (view == mButtonWatchNo) {
                setCurrentItem(4, true);
            } else if (view == mButtonKidFirstName) {
                if(mOnFinishListener != null)
                    mOnFinishListener.finish();
            } else if (view == mButtonKidLastName) {
                if(mOnFinishListener != null)
                    mOnFinishListener.finish();
            }
        }
    };

    public interface OnFinishListener {
        void finish();
    }

    public void setOnFinishListener(OnFinishListener listener) {
        mOnFinishListener = listener;
    }

    PagerAdapter mPagerAdapter = new PagerAdapter() {

        @Override
        public boolean isViewFromObject(View arg0, Object arg1) {
            return arg0 == arg1;
        }

        @Override
        public int getCount() {
            return mViewList.size();
        }

        @Override
        public void destroyItem(ViewGroup container, int position, Object object) {
            container.removeView(mViewList.get(position));
        }

        @Override
        public int getItemPosition(Object object) {
            return super.getItemPosition(object);
        }

        @Override
        public CharSequence getPageTitle(int position) {
            return null;
        }

        @Override
        public Object instantiateItem(ViewGroup container, int position) {
            container.addView(mViewList.get(position));
            return mViewList.get(position);
        }
    };
}
