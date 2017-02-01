package com.kidsdynamic.swing.androidswingapp;

import android.Manifest;
import android.animation.ValueAnimator;
import android.app.Fragment;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import java.util.Stack;

public class ActivityMain extends AppCompatActivity
        implements ActivityCompat.OnRequestPermissionsResultCallback {
    public final static int BLUETOOTH_PERMISSION = 0x1000;
    public final static int BLUETOOTH_ADMIN_PERMISSION = 0x1001;
    public final static int INTERNET_PERMISSION = 0x1002;
    public final static int WRITE_STORAGE_PERMISSION = 0x1003;
    public final static int READ_STORAGE_PERMISSION = 0x1004;

    public Config mConfig;
    public WatchOperator mOperator;
    public Stack<Bitmap> mBitmapStack;
    public BLEMachine mBLEMachine = null;
    public ServerMachine mServiceMachine = null;
    public int mRequestingPermission = 0;

    private View mViewDevice;
    private View mViewCalendar;
    private View mViewDashboard;
    private View mViewProfile;
    private ImageView mViewAction1;
    private ImageView mViewAction2;
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
        mOperator = new WatchOperator(this);
        mBitmapStack = new Stack<>();

        //mBLEMachine = new BLEMachine(this, mHandler);
        //mServiceMachine = new ServerMachine(this);

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

        mViewAction1 = (ImageView) findViewById(R.id.main_toolbar_action1);
        mViewAction1.setOnClickListener(mToolbarClickListener);

        mViewAction2 = (ImageView) findViewById(R.id.main_toolbar_action2);
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
        boolean activeBLE = true;
        boolean activeService = true;

        if (ContextCompat.checkSelfPermission(this, Manifest.permission.BLUETOOTH) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{android.Manifest.permission.BLUETOOTH}, BLUETOOTH_PERMISSION);
            mRequestingPermission++;
            activeBLE = false;
        }
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.BLUETOOTH_ADMIN) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{android.Manifest.permission.BLUETOOTH_ADMIN}, BLUETOOTH_ADMIN_PERMISSION);
            mRequestingPermission++;
            activeBLE = false;
        }
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.INTERNET) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{android.Manifest.permission.INTERNET}, INTERNET_PERMISSION);
            mRequestingPermission++;
            activeService = false;
        }
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{android.Manifest.permission.WRITE_EXTERNAL_STORAGE}, WRITE_STORAGE_PERMISSION);
            mRequestingPermission++;
        }
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{android.Manifest.permission.READ_EXTERNAL_STORAGE}, READ_STORAGE_PERMISSION);
            mRequestingPermission++;
        }

        if (activeBLE && mBLEMachine == null)
            mBLEMachine = new BLEMachine(this);

        if (activeService && mServiceMachine == null)
            mServiceMachine = new ServerMachine(this);

        if (mBLEMachine != null)
            mBLEMachine.Start();

        if (mServiceMachine != null)
            mServiceMachine.Start();
/*
        mOperator.UserAdd(new WatchContact.User(null, 3, "email", "first", "last", "update", "create", "zip", "phone"));
        WatchContact.User user = mOperator.UserGet();
        Log.d("TEST", user.mEmail + " " + user.mFirstName + " " + user.mLastName + " " + user.mLastUpdate+ " " + user.mDateCreated+ " " + user.mZipCode+ " " + user.mPhoneNumber+ " " + user.mProfile);
        user.mFirstName = "TEST";
        user.mProfile = "jpg";
        mOperator.UserUpdate(user);
        user = mOperator.UserGet();
        Log.d("TEST", user.mEmail + " " + user.mFirstName + " " + user.mLastName + " " + user.mLastUpdate+ " " + user.mDateCreated+ " " + user.mZipCode+ " " + user.mPhoneNumber+ " " + user.mProfile);

        mOperator.KidAdd(new WatchContact.Kid(null, 1, "Kid1", "last1", "ccc", "ddd", 3));
        mOperator.KidAdd(new WatchContact.Kid(null, 2, "Kid2", "last2", "ccc", "ddd", 3));
        mOperator.KidAdd(new WatchContact.Kid(null, 3, "Kid3", "last3", "ccc", "ddd", 3));
        mOperator.KidAdd(new WatchContact.Kid(null, 4, "Kid4", "last4", "ccc", "ddd", 3));
        mOperator.KidAdd(new WatchContact.Kid(null, 5, "Kid5", "last5", "ccc", "ddd", 3));
        List<WatchContact.Kid> kids = mOperator.KidGet();
        for (WatchContact.Kid kid : kids) {
            Log.d("TEST", "name " + kid.mFirstName + " " + kid.mLastName + " " + kid.mDateCreated + " " + kid.mMacId + " " + kid.mParentId + " "+ kid.mProfile);
            kid.mProfile = kid.mFirstName+ "+profile";
            mOperator.KidUpdate(kid);
        }
        kids = mOperator.KidGet();
        for (WatchContact.Kid kid : kids)
            Log.d("TEST", "name " + kid.mFirstName + " " + kid.mLastName + " " + kid.mDateCreated + " " + kid.mMacId + " " + kid.mParentId + " "+ kid.mProfile);
*/
        }

    @Override
    public void onPause() {
        if (mBLEMachine != null)
            mBLEMachine.Stop();
        if (mServiceMachine != null)
            mServiceMachine.Stop();
        super.onPause();
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

            if (view == mViewDevice)
                selectFragment(FragmentDevice.class.getName(), null);
            else if (view == mViewCalendar)
                selectFragment(FragmentCalendarMain.class.getName(), null);
            else if (view == mViewDashboard)
                selectFragment(FragmentDashboard.class.getName(), null);
            else if (view == mViewProfile)
                selectFragment(FragmentProfileMain.class.getName(), null);
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

    public void toolbarSetTitle(String title, boolean enableClick) {
        if(enableClick) {
            mViewTitle.setText(title + " ▼");        // &#x25BC;
            mViewTitle.setOnClickListener(mToolbarClickListener);
        } else {
            mViewTitle.setText(title);
            mViewTitle.setOnClickListener(null);
        }
    }

    public void toolbarSetIcon1(int resource) {
        if (resource == mIconRes1 || resource == RESOURCE_IGNORE)
            return;

        if (resource == RESOURCE_HIDE) {
            mViewAction1.setImageDrawable(null);
        } else {
            mViewAction1.setImageResource(resource);
        }

        mIconRes1 = resource;
    }

    public void toolbarSetIcon2(int resource) {
        if (resource == mIconRes2 || resource == RESOURCE_IGNORE)
            return;

        if (resource == RESOURCE_HIDE) {
            mViewAction2.setImageDrawable(null);
        } else {
            mViewAction2.setImageResource(resource);
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
            if (view == mViewAction1) {
                getTopViewFragment().onToolbarAction1();
            } else if (view == mViewAction2) {
                getTopViewFragment().onToolbarAction2();
            } else if (view == mViewTitle) {
                getTopViewFragment().onToolbarTitle();
            }
        }
    };

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {

        switch (requestCode) {
            case BLUETOOTH_PERMISSION:
                mRequestingPermission--;
                if (grantResults[0] != PackageManager.PERMISSION_GRANTED) {
                    Toast.makeText(ActivityMain.this, "Bluetooth permission denied", Toast.LENGTH_SHORT).show();
                }
                break;
            case BLUETOOTH_ADMIN_PERMISSION:
                mRequestingPermission--;
                if (grantResults[0] != PackageManager.PERMISSION_GRANTED) {
                    Toast.makeText(ActivityMain.this, "Bluetooth admin permission denied", Toast.LENGTH_SHORT).show();
                }
                break;
            case INTERNET_PERMISSION:
                mRequestingPermission--;
                break;
            case READ_STORAGE_PERMISSION:
                mRequestingPermission--;
                break;
            case WRITE_STORAGE_PERMISSION:
                mRequestingPermission--;
                break;
        }

        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
    }
}
