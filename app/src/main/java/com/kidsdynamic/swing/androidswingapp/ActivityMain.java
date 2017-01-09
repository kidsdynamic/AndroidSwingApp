package com.kidsdynamic.swing.androidswingapp;

import android.Manifest;
import android.animation.ValueAnimator;
import android.app.Fragment;
import android.content.pm.PackageManager;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.RequestQueue;
import com.android.volley.toolbox.Volley;

public class ActivityMain extends AppCompatActivity
        implements ActivityCompat.OnRequestPermissionsResultCallback {
    public final static int BLUETOOTH_PERMISSION = 0x1000;
    public final static int BLUETOOTH_ADMIN_PERMISSION = 0x1001;

    public Config mConfig;
    public RequestQueue mRequestQueue;

    private View mViewDevice;
    private View mViewCalendar;
    private View mViewDashboard;
    private View mViewProfile;
    private View mViewAction1;
    private View mViewAction2;
    private TextView mViewTitle;
    private View mViewConsole;
    private View mViewToolbar;

    private int mControlHeight;
    private int mToolbarHeight;

    final private int mTransitionDuration = 500;

    final static int RESOURCE_IGNORE = 0;
    final static int RESOURCE_HIDE = -1;
    private int mBackgroundRes, mIconRes1, mIconRes2;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mConfig = new Config(this, null);
        mRequestQueue = Volley.newRequestQueue(getApplicationContext());

        DisplayMetrics metrics = getResources().getDisplayMetrics();
        mControlHeight = metrics.heightPixels / getResources().getInteger(R.integer.console_height_denominator);
        mToolbarHeight = metrics.heightPixels / getResources().getInteger(R.integer.toolbar_height_denominator);

        mViewDevice = findViewById(R.id.main_console_device);
        mViewDevice.setOnClickListener(mConsoleClickListener);

        mViewCalendar = findViewById(R.id.main_console_calendar);
        mViewCalendar.setOnClickListener(mConsoleClickListener);

        mViewDashboard = findViewById(R.id.main_console_dashboard);
        mViewDashboard.setOnClickListener(mConsoleClickListener);

        mViewProfile = findViewById(R.id.main_control_profile);
        mViewProfile.setOnClickListener(mConsoleClickListener);

        mViewTitle = (TextView) findViewById(R.id.main_toolbar_title);

        mViewAction1 = findViewById(R.id.main_toolbar_action1);
        mViewAction1.setOnClickListener(mToolbarClickListener);

        mViewAction2 = findViewById(R.id.main_toolbar_action2);
        mViewAction2.setOnClickListener(mToolbarClickListener);

        mViewConsole = findViewById(R.id.main_console);
        mViewToolbar = findViewById(R.id.main_toolbar);

        String fragmentName = FragmentBoot.class.getName();
        Fragment fragment = Fragment.instantiate(this, fragmentName, null);
        getFragmentManager()
                .beginTransaction()
                .replace(R.id.main_fragment, fragment, fragmentName)
                .commit();
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

    public void popFragment() {
        getFragmentManager().popBackStack();
    }

    public void selectFragment(String className, Bundle args) {
        Fragment fragment = Fragment.instantiate(this, className, args);

        getFragmentManager()
                .beginTransaction()
                .replace(R.id.main_fragment, fragment, className)
                .addToBackStack(null)
                .commit();
    }

    public ViewFragment getTopViewFragment() {
        Fragment fragment = getFragmentManager().findFragmentById(R.id.main_fragment);

        if (fragment instanceof ViewFragment)
            return (ViewFragment) fragment;
        return null;
    }

    public void consoleShow(boolean visible) {
        boolean cur_visible = mViewConsole.getLayoutParams().height != 0;
        if (cur_visible == visible)
            return;

        ValueAnimator anim = visible ?
                ValueAnimator.ofInt(0, mControlHeight) : ValueAnimator.ofInt(mControlHeight, 0);

        anim.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator valueAnimator) {
                int val = (Integer) valueAnimator.getAnimatedValue();
                RelativeLayout.LayoutParams layoutParams =
                        (RelativeLayout.LayoutParams) mViewConsole.getLayoutParams();
                layoutParams.height = val;
                mViewConsole.setLayoutParams(layoutParams);
            }
        });
        anim.setDuration(mTransitionDuration);
        anim.start();
    }

    public void consoleSelect(View view) {
        mViewDevice.setSelected(view == mViewDevice);
        mViewCalendar.setSelected(view == mViewCalendar);
        mViewDashboard.setSelected(view == null || view == mViewDashboard);
        mViewProfile.setSelected(view == mViewProfile);
    }

    private View.OnClickListener mConsoleClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            consoleSelect(view);
        }
    };

    public void toolbarShow(boolean visible) {
        boolean cur_visible = mViewToolbar.getLayoutParams().height != 0;
        if (cur_visible == visible)
            return;

        ValueAnimator anim = visible ?
                ValueAnimator.ofInt(0, mToolbarHeight) : ValueAnimator.ofInt(mToolbarHeight, 0);

        anim.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator valueAnimator) {
                int val = (Integer) valueAnimator.getAnimatedValue();
                RelativeLayout.LayoutParams layoutParams =
                        (RelativeLayout.LayoutParams) mViewToolbar.getLayoutParams();
                layoutParams.height = val;
                mViewToolbar.setLayoutParams(layoutParams);
            }
        });
        anim.setDuration(mTransitionDuration);
        anim.start();
    }

    public void toolbarSetTitle(String title) {
        mViewTitle.setText(title);
    }

    public void toolbarSetIcon1(int resource) {
        if (resource == mIconRes1 || resource == RESOURCE_IGNORE)
            return;

        if (resource == RESOURCE_HIDE) {
            // todo: hide action1 in toolbar
        } else {
            if( mIconRes1 == RESOURCE_HIDE) {
                // todo: show action1 in toolbar
            }

            // todo: change action1 image
        }

        mIconRes1 = resource;
    }

    public void toolbarSetIcon2(int resource) {
        if (resource == mIconRes2 || resource == RESOURCE_IGNORE)
            return;

        if (resource == RESOURCE_HIDE) {
            // todo: hide action2 in toolbar
        } else {
            if( mIconRes2 == RESOURCE_HIDE) {
                // todo: show action2 in toolbar
            }

            // todo: change action2 image
        }

        mIconRes2 = resource;
    }

    public void backgroundSet(int resource) {
        if (resource == mBackgroundRes || resource == RESOURCE_IGNORE)
            return;

        if (resource == RESOURCE_HIDE) {
            // todo: set background in primary color (light blue)
        } else {
            // todo: change background resource
        }

        mBackgroundRes = resource;
    }

    private View.OnClickListener mToolbarClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
        }
    };

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {

        switch (requestCode) {
            case BLUETOOTH_PERMISSION:
                if (grantResults[0] != PackageManager.PERMISSION_GRANTED) {
                    Toast.makeText(ActivityMain.this, "Bluetooth permission denied", Toast.LENGTH_SHORT).show();
                }
                break;
            case BLUETOOTH_ADMIN_PERMISSION:
                if (grantResults[0] != PackageManager.PERMISSION_GRANTED) {
                    Toast.makeText(ActivityMain.this, "Bluetooth admin permission denied", Toast.LENGTH_SHORT).show();
                }
                break;
        }

        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
    }

}
