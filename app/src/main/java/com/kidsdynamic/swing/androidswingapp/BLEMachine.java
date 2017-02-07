package com.kidsdynamic.swing.androidswingapp;

import android.bluetooth.BluetoothDevice;
import android.content.Context;
import android.os.Handler;
import android.util.Log;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.UUID;

/**
 * Created by weichigio on 2017/1/11.
 */

public class BLEMachine extends BLEControl {
    private Handler mHandler = new Handler();
    private onFinishListener mOnFinishListener = null;
    private boolean mRunning = false;

    private void Log(String msg) {
        Log.i("BLEMachine", msg);
    }

    public BLEMachine(Context context) {
        super(context);
    }

    public boolean Start() {
        synchronized (this) {
            if (!mRunning) {
                Init(mEventListener);

                mState = STATE_INIT;
                EnableBondStateReceiver(false);
                mRelationDevice.resetFlag();

                mHandler.postDelayed(stateTransition, TRANSITION_GAP);
                mRunning = true;
            }
        }
        return true;
    }

    public boolean Stop() {
        synchronized (this) {
            if (mRunning) {
                mHandler.removeCallbacks(stateTransition);
                Deinit();
                mRunning = false;
            }
        }
        return true;
    }

    private static final int TRANSITION_GAP = 100;

    public static final int STATE_INIT = 0;
    public static final int STATE_SCAN = 1;
    public static final int STATE_BONDING = 2;
    public static final int STATE_CONNECTING = 3;
    public static final int STATE_DISCOVERY = 4;
    public static final int STATE_SET_TIME = 5;
    public static final int STATE_GET_ADDRESS = 6;
    public static final int STATE_SEND_ALERT = 7;
    public static final int STATE_GET_HEADER = 8;
    public static final int STATE_GET_TIME = 9;
    public static final int STATE_GET_DATA1 = 10;
    public static final int STATE_GET_DATA2 = 11;

    private Device mRelationDevice = new Device();
    private int mState;
    private ArrayList<Device> mScanResult;
    private String mSearchAddress = "";
    private boolean mSearchAddressFound = false;
    private List<VoiceAlert> mVoiceAlerts = new ArrayList<>();
    private int mVoiceAlertCount = 0;


    private Runnable stateTransition = new Runnable() {

        @Override
        public void run() {
            switch (mState) {
                case STATE_INIT:
                    if (mRelationDevice.mAction.mScanTime != 0) {
                        mState = STATE_SCAN;
                        mScanResult = new ArrayList<>();
                        Scan(true);
                    } else if (mRelationDevice.mAction.mSync && !mRelationDevice.mAddress.equals("")) {
                        mInOurDoors = new ArrayList<>();
                        mRelationDevice.mState.mTick = 150;
                        if (GetBondState(mRelationDevice.mAddress)) {
                            EnableBondStateReceiver(false);
                            mState = STATE_CONNECTING;
                            Connect(mRelationDevice.mAddress);
                        } else {
                            mState = STATE_BONDING;
                            EnableBondStateReceiver(true);
                            Connect(mRelationDevice.mAddress);
                        }
                    }
                    break;

                case STATE_SCAN:
                    if (--mRelationDevice.mAction.mScanTime == 0 || mOnFinishListener == null || mSearchAddressFound) {
                        mState = STATE_INIT;
                        Scan(false);

                        if (mOnFinishListener != null)
                            mOnFinishListener.onSearch(mScanResult);

                        resetSearchCondition();
                    }
                    break;

                case STATE_BONDING:
                    mRelationDevice.mState.mTick--;
                    if (mRelationDevice.mState.mBonded) {
                        mState = STATE_INIT;
                    } else if (mRelationDevice.mState.mTick == 0) {
                        syncFailProcess();
                    }
                    break;

                case STATE_CONNECTING:
                    mRelationDevice.mState.mTick--;
                    if (mRelationDevice.mState.mConnected) {
                        mState = STATE_DISCOVERY;
                    } else if (mRelationDevice.mState.mTick == 0) {
                        syncFailProcess();
                    }
                    break;

                case STATE_DISCOVERY:
                    if (mRelationDevice.mState.mDiscovered) {
                        int currentTime = (int)(getCurrentTime()/1000);
                        byte[] timeInByte = new byte[]{(byte) (currentTime), (byte) (currentTime >> 8), (byte) (currentTime >> 16), (byte) (currentTime >> 24)};
                        mState = STATE_SET_TIME;
                        Write(BLECustomAttributes.WATCH_SERVICE, BLECustomAttributes.ACCEL_ENABLE, new byte[]{1});
                        Write(BLECustomAttributes.WATCH_SERVICE, BLECustomAttributes.TIME, timeInByte);
                    } else if (!mRelationDevice.mState.mConnected) {
                        syncFailProcess();
                    }
                    break;

                case STATE_SET_TIME:
                    mRelationDevice.mState.mAddress = null;
                    mState = STATE_GET_ADDRESS;
                    Read(BLECustomAttributes.WATCH_SERVICE, BLECustomAttributes.ADDRESS);
                    break;

                case STATE_GET_ADDRESS:
                    if (mRelationDevice.mState.mAddress != null) {
                        mState = STATE_SEND_ALERT;
                        mVoiceAlertCount = 0;
                    } else if (!mRelationDevice.mState.mConnected) {
                        syncFailProcess();
                    }

                    break;

                case STATE_SEND_ALERT:
                    synchronized (mVoiceAlerts) {
                        if (!mRelationDevice.mState.mConnected) {
                            syncFailProcess();
                        } else if (mVoiceAlerts.isEmpty() || mVoiceAlertCount >= 250) {
                            Write(BLECustomAttributes.WATCH_SERVICE, BLECustomAttributes.VOICE_ALERT, new byte[]{0});
                            Write(BLECustomAttributes.WATCH_SERVICE, BLECustomAttributes.VOICE_EVET_ALERT_TIME, new byte[]{0, 0, 0, 0});

                            mRelationDevice.mState.mHeader = null;
                            Read(BLECustomAttributes.WATCH_SERVICE, BLECustomAttributes.HEADER);
                            mState = STATE_GET_HEADER;
                        } else {
                            VoiceAlert alert = mVoiceAlerts.get(0);
                            mVoiceAlerts.remove(0);
                            Calendar cal = Calendar.getInstance();
                            int countdown = (int)((alert.mTimeStamp-cal.getTimeInMillis())/1000);
                            if (countdown > 0) {
                                byte[] timeInByte = new byte[]{(byte) (countdown), (byte) (countdown >> 8), (byte) (countdown >> 16), (byte) (countdown >> 24)};
                                Write(BLECustomAttributes.WATCH_SERVICE, BLECustomAttributes.VOICE_ALERT, new byte[]{alert.mAlert});
                                Write(BLECustomAttributes.WATCH_SERVICE, BLECustomAttributes.VOICE_EVET_ALERT_TIME, timeInByte);
                                mVoiceAlertCount++;
                            }
                        }
                    }
                    break;

                case STATE_GET_HEADER:
                    if (mRelationDevice.mState.mHeader != null) {
                        if (mRelationDevice.mState.mHeader[0] == 1 && mRelationDevice.mState.mHeader[1] == 0) {
                            mRelationDevice.mState.mTime = null;
                            Read(BLECustomAttributes.WATCH_SERVICE, BLECustomAttributes.TIME);
                            mState = STATE_GET_TIME;

                        } else {
                            mRelationDevice.mAction.mSync = false;
                            Disconnect();

                            if (mOnFinishListener != null) {
                                mOnFinishListener.onSync(SYNC_RESULT_SUCCESS, mInOurDoors);
                            }

                            mState = STATE_INIT;
                        }
                    } else if (!mRelationDevice.mState.mConnected) {
                        syncFailProcess();
                    }

                    break;

                case STATE_GET_TIME:
                    if (mRelationDevice.mState.mTime != null) {
                        mRelationDevice.mState.mData1 = null;
                        Read(BLECustomAttributes.WATCH_SERVICE, BLECustomAttributes.DATA);
                        mState = STATE_GET_DATA1;
                    } else if (!mRelationDevice.mState.mConnected) {
                        syncFailProcess();
                    }

                    break;

                case STATE_GET_DATA1:
                    if (mRelationDevice.mState.mData1 != null) {
                        mRelationDevice.mState.mData2 = null;
                        Read(BLECustomAttributes.WATCH_SERVICE, BLECustomAttributes.DATA);
                        mState = STATE_GET_DATA2;
                    } else if (!mRelationDevice.mState.mConnected) {
                        syncFailProcess();
                    }

                    break;

                case STATE_GET_DATA2:
                    if (mRelationDevice.mState.mData2 != null) {
                        mInOurDoors.add(new InOutDoor(mRelationDevice.mState.mTime, mRelationDevice.mState.mData1, mRelationDevice.mState.mData2));
                        Write(BLECustomAttributes.WATCH_SERVICE, BLECustomAttributes.CHECKSUM, new byte[]{1});
                        mRelationDevice.mState.mHeader = null;
                        Read(BLECustomAttributes.WATCH_SERVICE, BLECustomAttributes.HEADER);

                        mState = STATE_GET_HEADER;
                    } else if (!mRelationDevice.mState.mConnected) {
                        syncFailProcess();
                    }

                    break;
            }

            if (mHandler != null)
                mHandler.postDelayed(this, TRANSITION_GAP);
        }
    };

    private void syncFailProcess() {
        mRelationDevice.resetFlag();
        if (mOnFinishListener != null)
            mOnFinishListener.onSync(mState, mInOurDoors);
        mState = STATE_INIT;
    }

    public final static int SYNC_RESULT_SUCCESS = 0xFFFFFFFF;

    public interface onFinishListener {
        void onSearch(ArrayList<Device> result);

        void onSync(int resultCode, ArrayList<InOutDoor> result);
    }

    private void resetSearchCondition() {
        mOnFinishListener = null;
        mRelationDevice.mAction.mScanTime = 0;
        mSearchAddress = "";
        mSearchAddressFound = false;
    }

    public int Search(onFinishListener listener) {
        if (listener == null) {
            resetSearchCondition();
        } else {
            mOnFinishListener = listener;
            mRelationDevice.mAction.mScanTime = 10 * 1000 / TRANSITION_GAP;
        }
        return mRelationDevice.mAction.mScanTime;
    }

    public int Search(onFinishListener listener, int second) {
        if (listener == null) {
            resetSearchCondition();
        } else {
            mOnFinishListener = listener;
            mRelationDevice.mAction.mScanTime = second * 1000 / TRANSITION_GAP;
        }
        return mRelationDevice.mAction.mScanTime;
    }

    public int Search(onFinishListener listener, String address) {
        if (listener == null) {
            resetSearchCondition();
        } else {
            mOnFinishListener = listener;
            mRelationDevice.mAction.mScanTime = 10 * 1000 / TRANSITION_GAP;
            mSearchAddress = address;
            mSearchAddressFound = false;
        }
        return mRelationDevice.mAction.mScanTime;
    }

    public int Sync(onFinishListener listener, Device device, List<VoiceAlert> alerts) {
        mOnFinishListener = listener;
        mRelationDevice.Copy(device);
        mRelationDevice.mAction.mSync = true;
        mVoiceAlerts = alerts;
        return 0;
    }

    public int Sync(onFinishListener listener, String macAddress) {
        mOnFinishListener = listener;
        mRelationDevice = new Device("Swing", macAddress, 0);
        mRelationDevice.mAction.mSync = true;
        return 0;
    }

    public static class VoiceAlert {
        byte mAlert;
        long mTimeStamp;

        VoiceAlert(byte number, long countdown) {
            mAlert = number;
            mTimeStamp = countdown;
        }
    }

    public static class InOutDoor {
        byte[] mTime;
        byte[] mData1;
        byte[] mData2;

        InOutDoor(byte[] time, byte[] data1, byte[] data2) {
            mTime = time;
            mData1 = data1;
            mData2 = data2;
        }
    }

    private ArrayList<InOutDoor> mInOurDoors;

    class Device {
        String mName;
        String mAddress;
        int mRssi;
        Action mAction;
        State mState;

        class Action {
            int mScanTime;
            boolean mSync;
        }

        class State {
            int mTick;
            boolean mBonded;
            boolean mConnected;
            boolean mDiscovered;
            byte[] mAddress;
            byte[] mHeader;
            byte[] mTime;
            byte[] mData1;
            byte[] mData2;
        }

        void resetFlag() {
            mAction.mScanTime = 0;
            mAction.mSync = false;
            mState.mBonded = false;
            mState.mConnected = false;
            mState.mDiscovered = false;
        }

        Device() {
            mName = "";
            mAddress = "";
            mRssi = 0;
            mAction = new Action();
            mState = new State();
            resetFlag();
        }

        Device(String name, String address, int rssi) {
            mName = name;
            mAddress = address;
            mRssi = rssi;
            mAction = new Action();
            mState = new State();
            resetFlag();
        }

        void Copy(Device src) {
            mName = src.mName;
            mAddress = src.mAddress;
        }
    }

    private OnEventListener mEventListener = new OnEventListener() {
        @Override
        public void onBondStateChange(int bondState) {
            if (bondState == BluetoothDevice.BOND_BONDED)
                mRelationDevice.mState.mBonded = true;
            else if (bondState == BluetoothDevice.BOND_NONE)
                Log("BluetoothDevice.BOND_NONE");
        }

        @Override
        public void onScanResult(String name, String address, boolean bonded, int rssi) {
            if (!name.contains("SWING"))
                return;

            for (Device dev : mScanResult) {
                if (dev.mName.equals(name)) {
                    dev.mRssi = rssi;
                    return;
                }
            }
            mScanResult.add(new Device(name, address, rssi));
            mSearchAddressFound = address.equals(mSearchAddress);
        }

        @Override
        public void onConnectionStateChange(String name, String address, boolean bonded, boolean connected) {
            if (address.equals(mRelationDevice.mAddress)) {
                mRelationDevice.mState.mConnected = connected;

                if (!connected) {
                    if (mRelationDevice.mAction.mSync) {
                        Log("Retry? " + mState);
                    }
                    Log("Disconnected");
                }
            }
        }

        @Override
        public void onServiceDiscovered() {
            mRelationDevice.mState.mDiscovered = true;
        }

        @Override
        public void onCharacteristicRead(UUID service, UUID characteristic, byte[] value) {
            if (service.toString().equals(BLECustomAttributes.WATCH_SERVICE)) {
                switch (characteristic.toString()) {
                    case BLECustomAttributes.ADDRESS:
                        if (value != null) {
                            mRelationDevice.mState.mAddress = value;
                            Log("Address " + bytesToHex(value));
                        }
                        break;
                    case BLECustomAttributes.HEADER:
                        if (value != null) {
                            mRelationDevice.mState.mHeader = value;
                            Log("Header " + bytesToHex(value));
                        }
                        break;
                    case BLECustomAttributes.TIME:
                        if (value != null) {
                            mRelationDevice.mState.mTime = value;
                            Log("Time " + bytesToHex(value));
                        }
                        break;
                    case BLECustomAttributes.DATA:
                        if (value != null) {
                            if (mRelationDevice.mState.mData1 == null) {
                                mRelationDevice.mState.mData1 = value;
                                Log("Data1 " + bytesToHex(value));
                            } else {
                                mRelationDevice.mState.mData2 = value;
                                Log("Data2 " + bytesToHex(value));
                            }
                        }
                        break;
                }
            }
        }

        @Override
        public void onCharacteristicChange(UUID service, UUID characteristic, byte[] value) {

        }

        @Override
        public void onCharacteristicWrite(UUID service, UUID characteristic) {

        }

        @Override
        public void onRssiUpdate(int rssi) {

        }
    };

    public static String bytesToHex(byte[] in) {
        final StringBuilder builder = new StringBuilder();
        for (byte b : in) {
            builder.append(String.format("0x%02X ", b));
        }
        return builder.toString();
    }

    public static long getCurrentTime() {
        Calendar now = Calendar.getInstance();
        int offset = now.getTimeZone().getOffset(now.getTimeInMillis());
        return now.getTimeInMillis() + offset;
    }
}
