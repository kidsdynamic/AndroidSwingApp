package com.kidsdynamic.swing.androidswingapp;

import android.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;

public class MainActivity extends AppCompatActivity {
    private View mViewDevice;
    private View mViewCalendar;
    private View mViewDashboard;
    private View mViewProfile;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mViewDevice = findViewById(R.id.main_control_device);
        mViewDevice.setOnClickListener(mControlClickListener);

        mViewCalendar = findViewById(R.id.main_control_calendar);
        mViewCalendar.setOnClickListener(mControlClickListener);

        mViewDashboard = findViewById(R.id.main_control_dashboard);
        mViewDashboard.setOnClickListener(mControlClickListener);

        mViewProfile = findViewById(R.id.main_control_profile);
        mViewProfile.setOnClickListener(mControlClickListener);
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

    View.OnClickListener mControlClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            if (view == mViewDevice) {
                selectFragment(FragmentDevice.class.getName(), null);

            } else if (view == mViewCalendar) {
                selectFragment(FragmentCalendar.class.getName(), null);

            } else if (view == mViewDashboard) {
                selectFragment(FragmentDashboard.class.getName(), null);

            } else if (view == mViewProfile) {
                selectFragment(FragmentProfile.class.getName(), null);

            }
        }
    };
}
