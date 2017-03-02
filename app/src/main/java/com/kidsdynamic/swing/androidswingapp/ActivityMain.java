package com.kidsdynamic.swing.androidswingapp;

import android.animation.ValueAnimator;
import android.app.Dialog;
import android.app.Fragment;
import android.app.ProgressDialog;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentManager;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;
import java.util.Stack;

import static com.kidsdynamic.swing.androidswingapp.ServerMachine.REQUEST_TAG;

public class ActivityMain extends AppCompatActivity
        implements ActivityCompat.OnRequestPermissionsResultCallback {
    public final static int BLUETOOTH_PERMISSION = 0x1000;
    public final static int BLUETOOTH_ADMIN_PERMISSION = 0x1001;
    public final static int INTERNET_PERMISSION = 0x1002;
    public final static int WRITE_STORAGE_PERMISSION = 0x1003;
    public final static int READ_STORAGE_PERMISSION = 0x1004;
    public final static int ACCESS_COARSE_LOCATION_PERMISSION = 0x1005;
    public final static int ACCESS_FINE_LOCATION_PERMISSION = 0x1006;

    private class permission {
        String mName;
        int mResult;

        permission(String name, int result) {
            mName = name;
            mResult = result;
        }
    }

    private List<permission> mPermissionList;

    public ActivityConfig mConfig;
    public WatchOperator mOperator;
    public Stack<Bitmap> mBitmapStack;
    public Stack<WatchContact> mContactStack;
    public Stack<WatchEvent> mEventStack;
    public BLEMachine mBLEMachine = null;
    public ServerMachine mServiceMachine = null;
    private Dialog mProcessDialog = null;
    private String mCurrentFragment = "";
    public boolean mIgnoreSyncOnce = false;

    private View mViewDevice;
    private View mViewCalendar;
    private View mViewDashboard;
    private ViewCircle mViewProfile;
    private ImageView mViewAction1;
    private ImageView mViewAction2;
    private TextView mViewTitle;
    private View mViewConsole;
    private View mViewToolbar;
    private ImageView mViewBackground;

    private int mControlHeight;
    private int mToolbarHeight;

    final private int mTransitionDuration = 500;

    final static int RESOURCE_IGNORE = 0;
    final static int RESOURCE_HIDE = -1;
    private int mBackgroundRes, mIconRes1, mIconRes2;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mConfig = new ActivityConfig(this, null);

        String language = mConfig.getString(ActivityConfig.KEY_LANGUAGE);
        if (!language.equals("")) {
            setLocale(language, mConfig.getString(ActivityConfig.KEY_REGION));
        }

        setContentView(R.layout.activity_main);

        mOperator = new WatchOperator(this);
        mBitmapStack = new Stack<>();
        mContactStack = new Stack<>();
        mEventStack = new Stack<>();

        initPermissionList();

        DisplayMetrics metrics = getResources().getDisplayMetrics();
        mControlHeight = metrics.heightPixels / getResources().getInteger(R.integer.console_height_denominator);
        mToolbarHeight = metrics.heightPixels / getResources().getInteger(R.integer.toolbar_height_denominator);

        mViewDevice = findViewById(R.id.main_console_device);
        mViewDevice.setOnClickListener(mConsoleClickListener);

        mViewCalendar = findViewById(R.id.main_console_calendar);
        mViewCalendar.setOnClickListener(mConsoleClickListener);

        mViewDashboard = findViewById(R.id.main_console_dashboard);
        mViewDashboard.setOnClickListener(mConsoleClickListener);

        mViewProfile = (ViewCircle) findViewById(R.id.main_control_profile);
        mViewProfile.setOnClickListener(mConsoleClickListener);

        mViewTitle = (TextView) findViewById(R.id.main_toolbar_title);

        mViewAction1 = (ImageView) findViewById(R.id.main_toolbar_action1);
        mViewAction1.setOnClickListener(mToolbarClickListener);

        mViewAction2 = (ImageView) findViewById(R.id.main_toolbar_action2);
        mViewAction2.setOnClickListener(mToolbarClickListener);

        mViewConsole = findViewById(R.id.main_console);
        mViewToolbar = findViewById(R.id.main_toolbar);

        mViewBackground = (ImageView) findViewById(R.id.main_background);
    }

    @Override
    public void onResume() {
        super.onResume();
        boolean activeBLE = true;
        boolean activeService = true;

        requestPermission();

        if (activeBLE && mBLEMachine == null)
            mBLEMachine = new BLEMachine(this);

        if (activeService && mServiceMachine == null)
            mServiceMachine = new ServerMachine(this, REQUEST_TAG);

        if (mBLEMachine != null)
            mBLEMachine.Start();

        if (mServiceMachine != null)
            mServiceMachine.Start();

        if (!mConfig.getString(ActivityConfig.KEY_AUTH_TOKEN).equals("") && !mIgnoreSyncOnce) {
            if (mProcessDialog == null) {
                mProcessDialog = ProgressDialog.show(this,
                        getResources().getString(R.string.activity_main_synchronize),
                        getResources().getString(R.string.activity_main_wait), true);

                mOperator.resumeSync(mFinishListener, "", "");
            }
        } else {
            if (mCurrentFragment.equals(""))
                selectFragment(FragmentBoot.class.getName(), null);
        }

        mIgnoreSyncOnce = false;
    }

    @Override
    public void onPause() {
        if (mProcessDialog == null) {
            if (mBLEMachine != null)
                mBLEMachine.Stop();
            if (mServiceMachine != null)
                mServiceMachine.Stop();
        }
        super.onPause();
    }

    WatchOperator.finishListener mFinishListener = new WatchOperator.finishListener() {
        @Override
        public void onFinish(Object arg) {
            eventTest();

            if (mCurrentFragment.equals("")) {
                selectFragment(FragmentBoot.class.getName(), null);
            } else {
                if (mProcessDialog != null)
                    mProcessDialog.dismiss();
                getFragmentManager().popBackStack(null, FragmentManager.POP_BACK_STACK_INCLUSIVE);
                selectFragment(FragmentDashboardMain.class.getName(), null);
            }
        }

        @Override
        public void onFailed(String Command, int statusCode) {
            Toast.makeText(getApplicationContext(), Command, Toast.LENGTH_SHORT).show();

            if (mCurrentFragment.equals("")) {
                selectFragment(FragmentBoot.class.getName(), null);
            } else {
                if (mProcessDialog != null)
                    mProcessDialog.dismiss();
                getFragmentManager().popBackStack(null, FragmentManager.POP_BACK_STACK_INCLUSIVE);
                selectFragment(FragmentDashboardMain.class.getName(), null);
            }
        }
    };

    private void initPermissionList() {
        mPermissionList = new ArrayList<>();
        mPermissionList.add(new permission(android.Manifest.permission.BLUETOOTH, BLUETOOTH_PERMISSION));
        mPermissionList.add(new permission(android.Manifest.permission.BLUETOOTH_ADMIN, BLUETOOTH_ADMIN_PERMISSION));
        mPermissionList.add(new permission(android.Manifest.permission.INTERNET, INTERNET_PERMISSION));
        mPermissionList.add(new permission(android.Manifest.permission.WRITE_EXTERNAL_STORAGE, WRITE_STORAGE_PERMISSION));
        mPermissionList.add(new permission(android.Manifest.permission.READ_EXTERNAL_STORAGE, READ_STORAGE_PERMISSION));
        mPermissionList.add(new permission(android.Manifest.permission.ACCESS_COARSE_LOCATION, ACCESS_COARSE_LOCATION_PERMISSION));
        mPermissionList.add(new permission(android.Manifest.permission.ACCESS_FINE_LOCATION, ACCESS_FINE_LOCATION_PERMISSION));
    }

    public boolean requestPermission() {
        for (permission perm : mPermissionList) {
            if (ContextCompat.checkSelfPermission(this, perm.mName) != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(this, new String[]{perm.mName}, perm.mResult);
                return true;
            }
        }
        return false;
    }

    public boolean isPermissionGranted() {
        for (permission perm : mPermissionList) {
            if (ContextCompat.checkSelfPermission(this, perm.mName) != PackageManager.PERMISSION_GRANTED)
                return false;
        }
        return true;
    }

    public void popFragment() {
        getFragmentManager().popBackStack();
    }

    public void selectFragment(String className, Bundle args) {
        Fragment fragment = Fragment.instantiate(this, className, args);
        mCurrentFragment = className;

        getFragmentManager()
                .beginTransaction()
                .replace(R.id.main_fragment, fragment, className)
                .addToBackStack(null)
                .commit();

        if (mProcessDialog != null) {
            mProcessDialog.dismiss();
            mProcessDialog = null;
        }
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

        mViewProfile.setActive(view == mViewProfile);
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
                selectFragment(FragmentDashboardMain.class.getName(), null);
            else if (view == mViewProfile)
                selectFragment(FragmentProfileMain.class.getName(), null);
        }
    };

    public void updateFocusAvatar() {
        WatchContact.Kid focus = mOperator.getFocusKid();

        if (focus != null && focus.mPhoto != null) {
            mViewProfile.setBitmap(focus.mPhoto);
        } else {
            mViewProfile.setBitmap(BitmapFactory.decodeResource(getResources(), R.mipmap.nofocus));
        }
    }

    public void toolbarShow(boolean visible) {
        boolean cur_visible = mViewToolbar.getLayoutParams().height != 0;
        if (cur_visible == visible)
            return;

        updateFocusAvatar();

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
        if (enableClick) {
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
            mViewBackground.setImageDrawable(null);
        } else {
            mViewBackground.setImageResource(resource);
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
            case BLUETOOTH_ADMIN_PERMISSION:
            case INTERNET_PERMISSION:
            case READ_STORAGE_PERMISSION:
            case WRITE_STORAGE_PERMISSION:
            case ACCESS_COARSE_LOCATION_PERMISSION:
            case ACCESS_FINE_LOCATION_PERMISSION:
                break;
        }

        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
    }

    private void eventTest() {
        //mActivityList = crateFakeData();
        //nextActivity();

        //mOperator.updateActivity(mUpdateActivityListener, mOperator.getFocusKid().mId);
    }

    List<WatchActivityRaw> mActivityList;

    private void nextActivity() {

        if (!mActivityList.isEmpty()) {
            WatchActivityRaw raw = mActivityList.get(0);
            mActivityList.remove(0);
            mServiceMachine.activityUploadRawData(mActivityUploadRawDataListener, raw.mIndoor, raw.mOutdoor, raw.mTime, raw.mMacId);
        }

    }

    WatchOperator.finishListener mUpdateActivityListener = new WatchOperator.finishListener() {
        @Override
        public void onFinish(Object arg) {
            mOperator.getActivityOfYear();
        }

        @Override
        public void onFailed(String Command, int statusCode) {

        }
    };

    private List<WatchActivityRaw> crateFakeData() {
        Calendar cal = Calendar.getInstance();
        long endTime = cal.getTimeInMillis();
        cal.add(Calendar.DATE, -365);
        long time = cal.getTimeInMillis();
        List<WatchActivityRaw> rtn = new ArrayList<>();
        String macId = mOperator.getFocusKid().mMacId + "";

        Log.d("swing", time + " start " + WatchOperator.getTimeString(time));
        Log.d("swing", endTime + " end " + WatchOperator.getTimeString(endTime));


        while (time <= endTime) {
            WatchActivityRaw fake = new WatchActivityRaw();
            cal = Calendar.getInstance();
            long step = cal.getTimeInMillis() % 3000;

            fake.mTime = (int) (time / 1000);
            fake.mMacId = macId;
            fake.mIndoor = fake.mTime + ",0," + (int) (Math.random() * 7500) + ",2,3,4";
            fake.mOutdoor = fake.mTime + ",1," + (int) (Math.random() * 7500) + ",2,3,4";
            rtn.add(fake);

            time += 86400000;
        }

        return rtn;
    }

    ServerMachine.activityUploadRawDataListener mActivityUploadRawDataListener = new ServerMachine.activityUploadRawDataListener() {
        @Override
        public void onSuccess(int statusCode) {
            nextActivity();
        }

        @Override
        public void onConflict(int statusCode) {
            nextActivity();
        }

        @Override
        public void onFail(String command, int statusCode) {
            nextActivity();
        }
    };

    public void setLocale(String language, String region) {
        Locale locale = new Locale(language, region);
        Locale.setDefault(locale);

        android.content.res.Configuration config = new android.content.res.Configuration();
        config.setLocale(locale);

        getResources().updateConfiguration(config, getBaseContext().getResources().getDisplayMetrics());
    }
}
