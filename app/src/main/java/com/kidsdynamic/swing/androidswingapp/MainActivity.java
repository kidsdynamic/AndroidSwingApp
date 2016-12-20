package com.kidsdynamic.swing.androidswingapp;

import android.animation.ValueAnimator;
import android.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.ScaleAnimation;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;

public class MainActivity extends AppCompatActivity {
    private View mViewDevice;
    private View mViewCalendar;
    private View mViewDashboard;
    private View mViewProfile;
    private View mViewControl;

    private int mControlHeight;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        DisplayMetrics metrics = getResources().getDisplayMetrics();
        mControlHeight = metrics.heightPixels / 12;

        mViewDevice = findViewById(R.id.main_control_device);
        mViewDevice.setOnClickListener(mControlClickListener);

        mViewCalendar = findViewById(R.id.main_control_calendar);
        mViewCalendar.setOnClickListener(mControlClickListener);

        mViewDashboard = findViewById(R.id.main_control_dashboard);
        mViewDashboard.setOnClickListener(mControlClickListener);

        mViewProfile = findViewById(R.id.main_control_profile);
        mViewProfile.setOnClickListener(mControlClickListener);

        mViewControl = findViewById(R.id.main_control);

        selectFragment(FragmentRegistration.class.getName(), null);
    }

    private void showFragment(String className, Bundle args) {
        Fragment fragment = Fragment.instantiate(this, className, args);
        getFragmentManager()
                .beginTransaction()
                .replace(R.id.main_fragment, fragment, className)
                .commit();
    }

    public void selectFragment(String className, Bundle args) {
        showFragment(className, args);
    }

    public void showControl(boolean enable) {
        ValueAnimator anim = enable ?
                ValueAnimator.ofInt(0, mControlHeight) : ValueAnimator.ofInt(mControlHeight, 0);

        anim.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator valueAnimator) {
                int val = (Integer) valueAnimator.getAnimatedValue();
                RelativeLayout.LayoutParams layoutParams =
                        (RelativeLayout.LayoutParams) mViewControl.getLayoutParams();
                layoutParams.height = val;
                mViewControl.setLayoutParams(layoutParams);
            }
        });
        anim.setDuration(250);
        anim.start();
    }

    public void selectControl(View view) {
        if (view == mViewDevice || view == null) {
            mViewCalendar.setSelected(false);
            mViewDashboard.setSelected(false);
            mViewProfile.setSelected(false);
            mViewDevice.setSelected(true);
            selectFragment(FragmentDevice.class.getName(), null);

        } else if (view == mViewCalendar) {
            mViewDashboard.setSelected(false);
            mViewProfile.setSelected(false);
            mViewDevice.setSelected(false);
            mViewCalendar.setSelected(true);
            selectFragment(FragmentCalendar.class.getName(), null);

        } else if (view == mViewDashboard) {
            mViewProfile.setSelected(false);
            mViewDevice.setSelected(false);
            mViewCalendar.setSelected(false);
            mViewDashboard.setSelected(true);
            selectFragment(FragmentDashboard.class.getName(), null);

        } else if (view == mViewProfile) {
            mViewDevice.setSelected(false);
            mViewCalendar.setSelected(false);
            mViewDashboard.setSelected(false);
            mViewProfile.setSelected(true);
            selectFragment(FragmentProfile.class.getName(), null);

        }
    }

    View.OnClickListener mControlClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            selectControl(view);
        }
    };
}
