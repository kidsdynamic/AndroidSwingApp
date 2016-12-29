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

    private Button mViewParent;
    private Button mViewNanny;
    private Button mViewLogin;
    private Button mViewFacebook;
    private Button mViewGoogle;
    private EditText mViewEmail;
    private EditText mViewPassword;
    private EditText mViewFirstName;
    private EditText mViewLastName;
    private EditText mViewPhone;
    private EditText mViewZip;
    private Button mViewWatchYes;
    private Button mViewWatchNo;
    private Button mViewRegisterWatch;
    private Button mViewRequestWatch;
    private Button mViewRequestBack;
    private Button mViewRequestDashboard;
    private Button mButtonKidFirstName;
    private Button mButtonKidLastName;

    private OnFinishListener mOnFinishListener = null;

    private View mPageWorld;
    private View mPageLogin;
    private View mPageAccount;
    private View mPageInfo;
    private View mPageHaveWatch;
    private View mPageRegisterWatch;
    private View mPageSelectWatch;
    private View mPageRequest;

    public ViewPagerRegistration(Context context, AttributeSet attrs) {
        super(context, attrs);

        mViewList = new ArrayList<>();

        LayoutInflater inflater = (LayoutInflater) context.getSystemService(context.LAYOUT_INFLATER_SERVICE);

        View view;

        // Page Explore the World
/*
        mPageWorld = inflater.inflate(R.layout.registration_explore_world, null);
        mViewList.add(mPageWorld);

        mViewParent = (Button) mPageWorld.findViewById(R.id.registration_parent);
        mViewParent.setOnClickListener(mButtonClickListener);

        mViewNanny = (Button) mPageWorld.findViewById(R.id.registration_nanny);
        mViewNanny.setOnClickListener(mButtonClickListener);
*/
        // Page Login
        /*
        mPageLogin = inflater.inflate(R.layout.registration_login, null);
        mViewList.add(mPageLogin);

        mViewLogin = (Button) mPageLogin.findViewById(R.id.registration_login_login);
        mViewLogin.setOnClickListener(mButtonClickListener);

        mViewFacebook = (Button) mPageLogin.findViewById(R.id.registration_login_facebook);
        mViewFacebook.setOnClickListener(mButtonClickListener);

        mViewGoogle = (Button) mPageLogin.findViewById(R.id.registration_login_google);
        mViewGoogle.setOnClickListener(mButtonClickListener);
*/
        // Page Account
        /*
        mPageAccount = inflater.inflate(R.layout.registration_account, null);
        mViewList.add(mPageAccount);

        mViewEmail = (EditText) mPageAccount.findViewById(R.id.registration_account_email);
        mViewEmail.setOnEditorActionListener(mEdittextActionListener);

        mViewPassword = (EditText) mPageAccount.findViewById(R.id.registration_account_password);
        mViewPassword.setOnEditorActionListener(mEdittextActionListener);
*/
        // Page Info/
        /*
        mPageInfo = inflater.inflate(R.layout.registration_info, null);
        mViewList.add(mPageInfo);

        mViewFirstName = (EditText) mPageInfo.findViewById(R.id.registration_info_first);
        mViewFirstName.setOnEditorActionListener(mEdittextActionListener);

        mViewLastName = (EditText) mPageInfo.findViewById(R.id.registration_info_last);
        mViewLastName.setOnEditorActionListener(mEdittextActionListener);

        mViewPhone = (EditText) mPageInfo.findViewById(R.id.registration_info_phone);
        mViewPhone.setOnEditorActionListener(mEdittextActionListener);

        mViewZip = (EditText) mPageInfo.findViewById(R.id.registration_info_zip);
        mViewZip.setOnEditorActionListener(mEdittextActionListener);
*/
        // Page Have Watch
/*
        mPageHaveWatch = inflater.inflate(R.layout.registration_have_watch, null);
        mViewList.add(mPageHaveWatch);

        mViewWatchYes = (Button) mPageHaveWatch.findViewById(R.id.registration_have_watch_yes);
        mViewWatchYes.setOnClickListener(mButtonClickListener);

        mViewWatchNo = (Button) mPageHaveWatch.findViewById(R.id.registration_have_watch_no);
        mViewWatchNo.setOnClickListener(mButtonClickListener);
*/
        // Page Register Watch
/*
        mPageRegisterWatch = inflater.inflate(R.layout.registration_register_watch, null);
        mViewList.add(mPageRegisterWatch);

        mViewRegisterWatch = (Button)mPageRegisterWatch.findViewById(R.id.registration_register_search);
        mViewRegisterWatch.setOnClickListener(mButtonClickListener);

        mViewRequestWatch = (Button)mPageRegisterWatch.findViewById(R.id.registration_register_other);
        mViewRequestWatch.setOnClickListener(mButtonClickListener);
*/
        // Page Request Other
        /*
        mPageRequest = inflater.inflate(R.layout.registration_request, null);
        mViewList.add(mPageRequest);

        mViewRequestBack = (Button)mPageRequest.findViewById(R.id.registration_request_back);
        mViewRequestBack.setOnClickListener(mButtonClickListener);

        mViewRequestDashboard = (Button)mPageRequest.findViewById(R.id.registration_request_dashboard);
        mViewRequestDashboard.setOnClickListener(mButtonClickListener);
*/
        // Page Select Watch
        mPageSelectWatch = inflater.inflate(R.layout.registration_select_watch, null);
        mViewList.add(mPageSelectWatch);

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
            } else if (view == mViewGoogle) {
                setCurrentItem(2, true);
            } else if (view == mViewWatchYes) {
                setCurrentItem(4, true);
            } else if (view == mViewWatchNo) {
                setCurrentItem(4, true);
            } else if (view == mViewRegisterWatch) {
                setCurrentItem(4, true);
            } else if (view == mViewRequestWatch) {
                setCurrentItem(4, true);
            } else if (view == mViewRequestBack) {
                setCurrentItem(4, true);
            } else if (view == mViewRequestDashboard) {
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
