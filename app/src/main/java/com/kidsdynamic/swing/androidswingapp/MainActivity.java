package com.kidsdynamic.swing.androidswingapp;

import android.Manifest;
import android.animation.ValueAnimator;
import android.app.Fragment;
import android.content.pm.PackageManager;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.ScaleAnimation;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.Toast;

public class MainActivity extends AppCompatActivity
    implements ActivityCompat.OnRequestPermissionsResultCallback {
    public final static int BLUETOOTH_PERMISSION = 0x1000;
    public final static int BLUETOOTH_ADMIN_PERMISSION = 0x1001;

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

        selectFragment(FragmentSignupLanguage.class.getName(), null);
    }

    @Override
    public void onResume() {
        super.onResume();

        if (ContextCompat.checkSelfPermission(this, Manifest.permission.BLUETOOTH) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{android.Manifest.permission.BLUETOOTH}, BLUETOOTH_PERMISSION);
        }
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.BLUETOOTH_ADMIN) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{android.Manifest.permission.BLUETOOTH_ADMIN}, BLUETOOTH_ADMIN_PERMISSION);
        }
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
        anim.setDuration(500);
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

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {

        switch (requestCode) {
            case BLUETOOTH_PERMISSION:
                if (grantResults[0] != PackageManager.PERMISSION_GRANTED) {
                    Toast.makeText(MainActivity.this, "Bluetooth permission denied", Toast.LENGTH_SHORT).show();
                }
                break;
            case BLUETOOTH_ADMIN_PERMISSION:
                if (grantResults[0] != PackageManager.PERMISSION_GRANTED) {
                    Toast.makeText(MainActivity.this, "Bluetooth admin permission denied", Toast.LENGTH_SHORT).show();
                }
                break;
        }

        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
    }

}
