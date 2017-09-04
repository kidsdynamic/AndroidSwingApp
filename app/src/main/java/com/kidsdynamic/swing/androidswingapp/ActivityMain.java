package com.kidsdynamic.swing.androidswingapp;

import android.animation.ValueAnimator;
import android.app.AlarmManager;
import android.app.Dialog;
import android.app.Fragment;
import android.app.PendingIntent;
import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.os.Handler;
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

import com.google.firebase.analytics.FirebaseAnalytics;
import com.google.firebase.iid.FirebaseInstanceId;
import com.idescout.sql.SqlScoutServer;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
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

    public FirebaseAnalytics mFirebaseAnalytics;

    public final static String RESUME_CHECK_TAG = "RESUME_CHECK_TAG";

    private class permission {
        String mName;
        int mResult;

        permission(String name, int result) {
            mName = name;
            mResult = result;
        }
    }

    private List<permission> mPermissionList;

    private Context mContext;
    public ActivityConfig mConfig;                  // 系統設置物件
    public WatchOperator mOperator;                 // 手機運作控制物件
    public BLEMachine mBLEMachine = null;           // 藍牙運作有限狀態機
    public ServerMachine mServiceMachine = null;    // 伺服器運作有限狀態機

    // Stack<XXX> 用以保存資料做為Fragment交換時, 數據交換時的暫時保存區
    public Stack<Bitmap> mBitmapStack;
    public Stack<WatchContact> mContactStack;
    public Stack<WatchEvent> mEventStack;

    // mViewXXX UI物件
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
    private Handler mHandler = new Handler();

    final private int mTransitionDuration = 500;

    final static int RESOURCE_IGNORE = 0;
    final static int RESOURCE_HIDE = -1;
    private int mBackgroundRes, mIconRes1, mIconRes2;

    public String language;

    private int mBackgroundPositionMinimum, mBackgroundPositionMaximum;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        SqlScoutServer.create(this, getPackageName());
        mContext = this;
        mConfig = new ActivityConfig(this, null);

        setContentView(R.layout.activity_main);

        mOperator = new WatchOperator(this);
        mBitmapStack = new Stack<>();
        mContactStack = new Stack<>();
        mEventStack = new Stack<>();

        initPermissionList();

        mFirebaseAnalytics = FirebaseAnalytics.getInstance(this);
        WatchContact.User user = mOperator.getUser();
        if(user != null) {
            List<WatchContact.Kid> kids = mOperator.getKids();
            mFirebaseAnalytics.setUserProperty(LogEvent.UserProperty.EMAIL, user.mEmail);
            mFirebaseAnalytics.setUserProperty(LogEvent.UserProperty.NAME, user.mFirstName + " " + user.mLastName);
            Date d = new Date(user.mDateCreated * 1000);
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.US);
            mFirebaseAnalytics.setUserProperty(LogEvent.UserProperty.SIGNUP_DATE, sdf.format(d));
            mFirebaseAnalytics.setUserProperty(LogEvent.UserProperty.KID_COUNT, String.valueOf(kids.size()));

            String kidList = "";
            for(WatchContact.Kid kid : kids) {
                kidList += kid.mMacId + ", ";
            }
            kidList = kidList.substring(0, kidList.length()-2);
            mFirebaseAnalytics.setUserProperty(LogEvent.UserProperty.MAC_ID_LIST, kidList);
        }

        FirebaseLog(LogEvent.Event.APP_OPEN, savedInstanceState);

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

        selectFragment(FragmentBoot.class.getName(), null);

        Intent intent = new Intent(this, ServerPushService.class);
        startService(intent);
    }

    @Override
    public void onResume() {
        super.onResume();

        Log.d("ActivityMain", "onResume()");
        boolean activeBLE = true;
        boolean activeService = true;

        language = mConfig.getString(ActivityConfig.KEY_LANGUAGE);
        if (!language.equals("")) {
            setLocale(language, mConfig.getString(ActivityConfig.KEY_REGION));
        }

        requestPermission();

        if (activeBLE && mBLEMachine == null)
            mBLEMachine = new BLEMachine(this);

        if (activeService && mServiceMachine == null)
            mServiceMachine = new ServerMachine(this, REQUEST_TAG, mServerMachineListener);

        if (mBLEMachine != null)
            mBLEMachine.Start();

        if (mServiceMachine != null)
            mServiceMachine.Start();

        if (!mConfig.getString(ActivityConfig.KEY_AUTH_TOKEN).equals("")) {
            mServiceMachine.userIsTokenValid(
                    mUserIsTokenValidListener,
                    mConfig.getString(ActivityConfig.KEY_MAIL),
                    mConfig.getString(ActivityConfig.KEY_AUTH_TOKEN),
                    RESUME_CHECK_TAG);
        }

        IntentFilter filter = new IntentFilter("SWING_LOGOUT");
        registerReceiver(mLogoutReceiver, filter);
    }

    @Override
    public void onPause() {
        Log.d("ActivityMain", "onPause()");
        mServiceMachine.cancelByTag(RESUME_CHECK_TAG);
        unregisterReceiver(mLogoutReceiver);

        super.onPause();
    }

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

        Bundle bundle = new Bundle();
        String[] pageName = className.split("\\.");
        bundle.putString(FirebaseAnalytics.Param.ITEM_NAME, pageName[pageName.length-1]);

        FirebaseLog(LogEvent.Event.SWITCH_PAGE, bundle);
        getFragmentManager()
                .beginTransaction()
                .replace(R.id.main_fragment, fragment, className)
                .addToBackStack(null)
                .commit();
    }

    public void clearFragment(String className, Bundle args) {
        try{
            getFragmentManager().popBackStack(null, FragmentManager.POP_BACK_STACK_INCLUSIVE);


        } catch(Exception e) {
            e.printStackTrace();
        }

        Fragment fragment = Fragment.instantiate(this, className, args);
        getFragmentManager()
                .beginTransaction()
                .replace(R.id.main_fragment, fragment, className)
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

    public void backgroundShuffle() {
        double random = Math.random();
        int pos = (int) Math.floor(random * (mBackgroundPositionMaximum - mBackgroundPositionMinimum));

        RelativeLayout.LayoutParams layoutParams = (RelativeLayout.LayoutParams) mViewBackground.getLayoutParams();
        layoutParams.setMarginStart(-pos);
        mViewBackground.setLayoutParams(layoutParams);
    }

    public void backgroundSet(int resource) {
        if (resource == RESOURCE_IGNORE) {
            return;
        } else if (resource == RESOURCE_HIDE) {
            mViewBackground.setImageDrawable(null);
        } else if (resource == mBackgroundRes) {
            //backgroundShuffle();
        } else {
            mBackgroundPositionMinimum = 0;

            DisplayMetrics displayMetrics = new DisplayMetrics();
            getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);

            mViewBackground.setImageResource(resource);

            mBackgroundPositionMaximum = ((BitmapDrawable) mViewBackground.getDrawable()).getBitmap().getWidth();
            mBackgroundPositionMaximum -= displayMetrics.widthPixels;
            mBackgroundPositionMaximum = Math.max(mBackgroundPositionMaximum, 0);

            //backgroundShuffle();
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

    private ServerMachine.userIsTokenValidListener mUserIsTokenValidListener = new ServerMachine.userIsTokenValidListener() {
        @Override
        public void onValidState(boolean valid) {
            if (valid){
                mServiceMachine.setAuthToken(mConfig.getString(ActivityConfig.KEY_AUTH_TOKEN));
            }
            mServiceMachine.updateRegistrationId();

        }

        @Override
        public void onFail(String command, int statusCode) {
        }
    };

    ServerMachine.onForbiddenListener mServerMachineListener = new ServerMachine.onForbiddenListener() {
        @Override
        public void onForbidden() {
            sendBroadcast(new Intent("SWING_LOGOUT"));
        }
    };


    BroadcastReceiver mLogoutReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            Toast.makeText(mContext, getResources().getString(R.string.profile_option_logout), Toast.LENGTH_SHORT).show();
            mHandler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    logout();
                }
            }, 1000);
        }
    };

    public void logout() {
        mConfig.loadDefaultTable();
        mOperator.ResetDatabase();
        mServiceMachine.setAuthToken(null);
        ServerMachine.ResetAvatar();

        Intent intent = new Intent(this, ServerPushService.class);
        stopService(intent);

        //intent = getBaseContext().getPackageManager().getLaunchIntentForPackage(getBaseContext().getPackageName());
        //intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        //startActivity(intent);

        Intent restartIntent = mContext.getPackageManager().getLaunchIntentForPackage(mContext.getPackageName());
        PendingIntent pendingIntent = PendingIntent.getActivity(
                mContext, 0, restartIntent, PendingIntent.FLAG_CANCEL_CURRENT);
        AlarmManager manager = (AlarmManager) mContext.getSystemService(Context.ALARM_SERVICE);
        manager.set(AlarmManager.RTC, System.currentTimeMillis() + 1, pendingIntent);
        System.exit(2);
    }

    public void setLocale(String language, String region) {
        Locale locale = new Locale(language, region);
        Locale.setDefault(locale);

        // 依 language, regin 設置應用程式當前語系
        android.content.res.Configuration config = new android.content.res.Configuration();
        config.setLocale(locale);

        getResources().updateConfiguration(config, getBaseContext().getResources().getDisplayMetrics());
    }

    public void FirebaseLog(String logName, Bundle bundle){

        mFirebaseAnalytics.logEvent(logName, bundle);
    }
}
