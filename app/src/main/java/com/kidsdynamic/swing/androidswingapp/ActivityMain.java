package com.kidsdynamic.swing.androidswingapp;

import android.animation.ValueAnimator;
import android.app.Dialog;
import android.app.Fragment;
import android.app.ProgressDialog;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
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
import java.util.List;
import java.util.Stack;

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

    public Config mConfig;
    public WatchOperator mOperator;
    public Stack<Bitmap> mBitmapStack;
    public Stack<WatchContact> mContactStack;
    public Stack<WatchEvent> mEventStack;
    public BLEMachine mBLEMachine = null;
    public ServerMachine mServiceMachine = null;
    private Dialog mProcessDialog = null;
    private List<WatchContact.Kid> mKidList;


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

        mViewProfile = findViewById(R.id.main_control_profile);
        mViewProfile.setOnClickListener(mConsoleClickListener);

        mViewTitle = (TextView) findViewById(R.id.main_toolbar_title);

        mViewAction1 = (ImageView) findViewById(R.id.main_toolbar_action1);
        mViewAction1.setOnClickListener(mToolbarClickListener);

        mViewAction2 = (ImageView) findViewById(R.id.main_toolbar_action2);
        mViewAction2.setOnClickListener(mToolbarClickListener);

        mViewConsole = findViewById(R.id.main_console);
        mViewToolbar = findViewById(R.id.main_toolbar);
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
            mServiceMachine = new ServerMachine(this);

        if (mBLEMachine != null)
            mBLEMachine.Start();

        if (mServiceMachine != null)
            mServiceMachine.Start();

        if (!mConfig.getString(Config.KEY_AUTH_TOKEN).equals("")) {
            if (mProcessDialog == null) {
                mProcessDialog = ProgressDialog.show(this, "Synchronize", "Please wait...", true);

                mServiceMachine.userIsTokenValid(
                        mUserIsTokenValidListener,
                        mConfig.getString(Config.KEY_MAIL),
                        mConfig.getString(Config.KEY_AUTH_TOKEN));
            }
        } else {
            selectFragment(FragmentBoot.class.getName(), null);
        }
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

    ServerMachine.userIsTokenValidListener mUserIsTokenValidListener = new ServerMachine.userIsTokenValidListener() {
        @Override
        public void onValidState(boolean valid) {
            if (valid) {
                mServiceMachine.setAuthToken(mConfig.getString(Config.KEY_AUTH_TOKEN));
                mServiceMachine.userRetrieveUserProfile(mRetrieveUserProfileListener);
            } else {
                mServiceMachine.userLogin(mUserLoginListener, mConfig.getString(Config.KEY_MAIL), mConfig.getString(Config.KEY_PASSWORD));
            }
        }

        @Override
        public void onFail(int statusCode) {
            mServiceMachine.userLogin(mUserLoginListener, mConfig.getString(Config.KEY_MAIL), mConfig.getString(Config.KEY_PASSWORD));
        }
    };

    ServerMachine.userLoginListener mUserLoginListener = new ServerMachine.userLoginListener() {
        @Override
        public void onSuccess(int statusCode, ServerGson.user.login.response result) {
            mConfig.setString(Config.KEY_AUTH_TOKEN, result.access_token);
            mServiceMachine.setAuthToken(result.access_token);
            mServiceMachine.userRetrieveUserProfile(mRetrieveUserProfileListener);
        }

        @Override
        public void onFail(int statusCode) {
            Toast.makeText(getApplicationContext(), "login failed!",Toast.LENGTH_SHORT).show();
            selectFragment(FragmentBoot.class.getName(), null);
        }
    };

    ServerMachine.userRetrieveUserProfileListener mRetrieveUserProfileListener = new ServerMachine.userRetrieveUserProfileListener() {
        @Override
        public void onSuccess(int statusCode, ServerGson.user.retrieveUserProfile.response response) {
            ServerMachine.ResetAvatar();
            mOperator.UserUpdate(
                    new WatchContact.User(
                            null,
                            response.user.id,
                            response.user.email,
                            response.user.firstName,
                            response.user.lastName,
                            WatchOperator.getTimeStamp(response.user.lastUpdate),
                            WatchOperator.getTimeStamp(response.user.dateCreated),
                            response.user.zipCode,
                            response.user.phoneNumber,
                            response.user.profile)
            );

            mKidList = new ArrayList<>();
            for (ServerGson.kidData kidData : response.kids) {
                WatchContact.Kid kid = new WatchContact.Kid();
                kid.mId = kidData.id;
                kid.mFirstName = kidData.name;
                kid.mLastName = "";
                kid.mDateCreated = WatchOperator.getTimeStamp(kidData.dateCreated);
                kid.mMacId = kidData.macId;
                kid.mUserId = response.user.id;
                kid.mProfile = kidData.profile;
                kid.mBound = true;
                mOperator.KidUpdate(kid);
                mKidList.add(kid);
            }

            if (!response.user.profile.equals(""))
                mServiceMachine.getAvatar(mGetUserAvatarListener, response.user.profile);
            else
                getKidAvatar(true);
        }

        @Override
        public void onFail(int statusCode) {
            Toast.makeText(getApplicationContext(), "Retrieve user profile failed!",Toast.LENGTH_SHORT).show();
            selectFragment(FragmentBoot.class.getName(), null);
        }
    };

    ServerMachine.getAvatarListener mGetUserAvatarListener = new ServerMachine.getAvatarListener() {
        @Override
        public void onSuccess(Bitmap avatar, String filename) {
            ServerMachine.createAvatarFile(avatar, filename, "");
            getKidAvatar(true);
        }

        @Override
        public void onFail(int statusCode) {
            Toast.makeText(getApplicationContext(), "Get user avatar failed!",Toast.LENGTH_SHORT).show();
            selectFragment(FragmentBoot.class.getName(), null);
        }
    };

    private int mProcessKidAvatar;
    private void getKidAvatar(boolean start) {
        if (start)
            mProcessKidAvatar = 0;
        else
            mProcessKidAvatar++;

        if (mProcessKidAvatar >= mKidList.size()) {
            selectFragment(FragmentBoot.class.getName(), null);
            return;
        }

        while (mKidList.get(mProcessKidAvatar).mProfile.equals("")) {
            mProcessKidAvatar++;
            if (mProcessKidAvatar >= mKidList.size()) {
                selectFragment(FragmentBoot.class.getName(), null);
                return;
            }
        }

        mServiceMachine.getAvatar(mGetKidAvatarListener, mKidList.get(mProcessKidAvatar).mProfile);
    }

    ServerMachine.getAvatarListener mGetKidAvatarListener = new ServerMachine.getAvatarListener() {
        @Override
        public void onSuccess(Bitmap avatar, String filename) {
            ServerMachine.createAvatarFile(avatar, filename, "");
            getKidAvatar(false);
        }

        @Override
        public void onFail(int statusCode) {
            Toast.makeText(getApplicationContext(), "Get kid avatar failed!",Toast.LENGTH_SHORT).show();
            selectFragment(FragmentBoot.class.getName(), null);
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
        /*
        mOperator.EventReset();
        mOperator.EventAdd(new WatchOperator.Event(0, 7, "", WatchOperator.getTimeStamp("2015-08-31T06:20:00Z"), WatchOperator.getTimeStamp("2015-08-31T08:20:00Z")));
        mOperator.EventAdd(new WatchOperator.Event(1, 6, "", WatchOperator.getTimeStamp("2015-08-30T09:20:00Z"), WatchOperator.getTimeStamp("2015-08-30T11:20:00Z")));
        mOperator.EventAdd(new WatchOperator.Event(2, 5, "", WatchOperator.getTimeStamp("2015-09-01T05:20:00Z"), WatchOperator.getTimeStamp("2015-09-01T07:20:00Z")));
        mOperator.EventAdd(new WatchOperator.Event(3, 4, "", WatchOperator.getTimeStamp("2015-08-01T08:20:00Z"), WatchOperator.getTimeStamp("2015-08-01T09:20:00Z")));
        mOperator.EventAdd(new WatchOperator.Event(4, 3, "WEEKLY", WatchOperator.getTimeStamp("2015-08-31T13:00:00Z"), WatchOperator.getTimeStamp("2015-08-31T15:20:00Z")));
        mOperator.EventAdd(new WatchOperator.Event(5, 2, "WEEKLY", WatchOperator.getTimeStamp("2015-09-01T16:00:00Z"), WatchOperator.getTimeStamp("2015-09-01T18:00:00Z")));
        mOperator.EventAdd(new WatchOperator.Event(6, 1, "WEEKLY", WatchOperator.getTimeStamp("2015-08-30T12:00:00Z"), WatchOperator.getTimeStamp("2015-08-30T12:20:00Z")));
        mOperator.EventAdd(new WatchOperator.Event(7, 0, "", WatchOperator.getTimeStamp("2015-07-10T08:20:00Z"), WatchOperator.getTimeStamp("2015-07-10T12:00:00Z")));
        List<WatchOperator.Event> events = mOperator.EventGet(0, 0, WatchOperator.getTimeStamp("2015-08-29T08:20:00Z"), WatchOperator.getTimeStamp("2015-09-10T08:20:00Z"));

        mOperator.UserAdd(new WatchContact.User(null, 3, "email", "first", "last", "update", "create", "zip", "phone"));
        WatchContact.User user = mOperator.UserGet();
        Log.d("TEST", user.mEmail + " " + user.mFirstName + " " + user.mLastName + " " + user.mLastUpdate+ " " + user.mDateCreated+ " " + user.mZipCode+ " " + user.mPhoneNumber+ " " + user.mProfile);
        user.mFirstName = "TEST";
        user.mProfile = "jpg";
        mOperator.UserUpdate(user);
        user = mOperator.UserGet();
        Log.d("TEST", user.mEmail + " " + user.mFirstName + " " + user.mLastName + " " + user.mLastUpdate+ " " + user.mDateCreated+ " " + user.mZipCode+ " " + user.mPhoneNumber+ " " + user.mProfile);
        WatchContact.Kid focus = mOperator.KidGetFocus();
        if (focus == null)
            Log.d("TEST", "No focus item");

        mOperator.KidAdd(new WatchContact.Kid(null, 1, "Kid1", "last1", "ccc", "ddd", 3));
        mOperator.KidAdd(new WatchContact.Kid(null, 2, "Kid2", "last2", "ccc", "ddd", 3));
        mOperator.KidAdd(new WatchContact.Kid(null, 3, "Kid3", "last3", "ccc", "ddd", 3));
        mOperator.KidAdd(new WatchContact.Kid(null, 4, "Kid4", "last4", "ccc", "ddd", 3));
        mOperator.KidAdd(new WatchContact.Kid(null, 5, "Kid5", "last5", "ccc", "ddd", 3));
        List<WatchContact.Kid> kids = mOperator.KidGet();
        for (WatchContact.Kid kid : kids) {
            Log.d("TEST", "name " + kid.mFirstName + " " + kid.mLastName + " " + kid.mDateCreated + " " + kid.mMacId + " " + kid.mUserId + " "+ kid.mProfile);
            kid.mProfile = kid.mFirstName+ "+profile";
            mOperator.KidUpdate(kid);
        }
        kids = mOperator.KidGet();
        for (WatchContact.Kid kid : kids)
            Log.d("TEST", "name " + kid.mFirstName + " " + kid.mLastName + " " + kid.mDateCreated + " " + kid.mMacId + " " + kid.mUserId + " "+ kid.mProfile);

        focus = new WatchContact.Kid(null, 6, "Kid6", "last6", "ccc", "ddd", 3);
        mOperator.KidSetFocus(focus);
        focus = mOperator.KidGetFocus();
        Log.d("TEST", "name " + focus.mFirstName + " " + focus.mLastName + " " + focus.mDateCreated + " " + focus.mMacId + " " + focus.mUserId + " "+ focus.mProfile);
    */
    }
}
