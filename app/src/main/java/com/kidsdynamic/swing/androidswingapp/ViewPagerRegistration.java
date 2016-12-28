package com.kidsdynamic.swing.androidswingapp;

import android.content.Context;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.util.AttributeSet;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import java.util.ArrayList;

/**
 * Created by 03543 on 2016/12/22.
 */

public class ViewPagerRegistration extends ViewPager {
    private ArrayList<View> mViewList;

    private Button mViewLogin;
    private Button mViewFacebook;
    private EditText mViewEmail;
    private EditText mViewPassword;
    private EditText mViewFirstName;
    private EditText mViewLastName;
    private EditText mViewPhone;
    private EditText mViewZip;
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

        mViewLogin = (Button) view.findViewById(R.id.registration_login_login);
        mViewLogin.setOnClickListener(mButtonClickListener);

        mViewFacebook = (Button) view.findViewById(R.id.registration_login_facebook);
        mViewFacebook.setOnClickListener(mButtonClickListener);

        // Page Account
        view = inflater.inflate(R.layout.registration_account, null);
        mViewList.add(view);

        mViewEmail = (EditText) view.findViewById(R.id.registration_account_email);
        mViewEmail.setOnEditorActionListener(mEdittextActionListener);

        mViewPassword = (EditText) view.findViewById(R.id.registration_account_password);
        mViewPassword.setOnEditorActionListener(mEdittextActionListener);

        // Page Info
        view = inflater.inflate(R.layout.registration_info, null);
        mViewList.add(view);

        mViewFirstName = (EditText) view.findViewById(R.id.registration_info_first);
        mViewFirstName.setOnEditorActionListener(mEdittextActionListener);

        mViewLastName = (EditText) view.findViewById(R.id.registration_info_last);
        mViewLastName.setOnEditorActionListener(mEdittextActionListener);

        mViewPhone = (EditText) view.findViewById(R.id.registration_info_phone);
        mViewPhone.setOnEditorActionListener(mEdittextActionListener);

        mViewZip = (EditText) view.findViewById(R.id.registration_info_zip);
        mViewZip.setOnEditorActionListener(mEdittextActionListener);

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
            if (view == mViewLogin) {
                setCurrentItem(1, true);
            } else if (view == mViewFacebook) {
                setCurrentItem(2, true);
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

    private EditText.OnEditorActionListener mEdittextActionListener = new TextView.OnEditorActionListener() {
        @Override
        public boolean onEditorAction(TextView view, int actionId, KeyEvent event) {
            if(view == mViewPassword && actionId== EditorInfo.IME_ACTION_DONE)
                setCurrentItem(2, true);

            if(view == mViewZip && actionId== EditorInfo.IME_ACTION_DONE)
                setCurrentItem(3, true);

            return false;
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
