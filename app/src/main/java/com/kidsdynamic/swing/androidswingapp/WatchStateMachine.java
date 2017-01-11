package com.kidsdynamic.swing.androidswingapp;

import android.bluetooth.BluetoothDevice;
import android.content.Context;
import android.os.Handler;
import android.util.Log;

import java.util.ArrayList;
import java.util.Queue;
import java.util.UUID;
import java.util.concurrent.ConcurrentLinkedQueue;

/**
 * Created by weichigio on 2017/1/11.
 */

public class WatchStateMachine extends BleControl {

    private Handler mHandler = new Handler();

    private void Log(String msg) {
        Log.i("LeControl", msg);
    }

    public WatchStateMachine(Context context) {
        super(context);
    }

    public boolean Start() {
        Init(mEventListener);
        Reset();
        mHandler.postDelayed(stateTransition, TRANSITION_GAP);
        return true;
    }

    public boolean Stop() {
        mHandler.removeCallbacks(stateTransition);
        Deinit();
        return true;
    }

    public boolean Reset() {
        mState = STATE_INIT;
        mRelationDevice.resetFlag();
        EnableBondStateReceiver(false);
        mScanResult = new ArrayList<>();
        return true;
    }

    private static final int TRANSITION_GAP = 100;

    private static final int STATE_INIT = 0;
    private static final int STATE_SCAN = 1;
    private static final int STATE_BONDING = 2;
    private static final int STATE_CONNECTING = 3;
    private static final int STATE_DISCOVERY = 4;
    private static final int STATE_SET_TIME = 5;
    private static final int STATE_GET_ADDRESS = 6;
    private static final int STATE_SEND_ALERT = 7;
    private static final int STATE_GET_HEADER = 8;
    private static final int STATE_GET_TIME = 9;
    private static final int STATE_GET_DATA1 = 10;
    private static final int STATE_GET_DATA2 = 11;

    private Device mRelationDevice = new Device();
    private int mState;
    private ArrayList<Device> mScanResult;

    private Runnable stateTransition = new Runnable() {

        @Override
        public void run() {
            switch (mState) {
                case STATE_INIT:
                    if (mRelationDevice.mAction.mScanTime != 0) {
                        mState = STATE_SCAN;
                        Scan(true);
                    } else if (mRelationDevice.mAction.mSync && !mRelationDevice.mAddress.equals("")) {
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
                    if (--mRelationDevice.mAction.mScanTime == 0) {
                        mState = STATE_INIT;
                        Scan(false);
                    }
                    break;

                case STATE_BONDING:
                    if (mRelationDevice.mState.mBonded)
                        mState = STATE_INIT;
                    break;

                case STATE_CONNECTING:
                    if (mRelationDevice.mState.mConnected)
                        mState = STATE_DISCOVERY;
                    break;

                case STATE_DISCOVERY:
                    if (mRelationDevice.mState.mDiscovered) {
                        int currentTime = (int) (System.currentTimeMillis() / 1000);
                        byte[] timeInByte = new byte[]{(byte) (currentTime), (byte) (currentTime >> 8), (byte) (currentTime >> 16), (byte) (currentTime >> 24)};
                        mState = STATE_SET_TIME;
                        Write(GattAttributes.WATCH_SERVICE, GattAttributes.ACCEL_ENABLE, new byte[]{1});
                        Write(GattAttributes.WATCH_SERVICE, GattAttributes.TIME, timeInByte);
                    }
                    break;

                case STATE_SET_TIME:
                    mRelationDevice.mState.mAddress = null;
                    mState = STATE_GET_ADDRESS;
                    Read(GattAttributes.WATCH_SERVICE, GattAttributes.ADDRESS);
                    break;

                case STATE_GET_ADDRESS:
                    if (mRelationDevice.mState.mAddress != null) {
                        mState = STATE_SEND_ALERT;
                    }
                    break;

                case STATE_SEND_ALERT:
                    synchronized (mRelationDevice.mAction.mVoiceAlerts) {
                        if (mRelationDevice.mAction.mVoiceAlerts.isEmpty()) {
                            Write(GattAttributes.WATCH_SERVICE, GattAttributes.VOICE_ALERT, new byte[]{0});
                            Write(GattAttributes.WATCH_SERVICE, GattAttributes.VOICE_EVET_ALERT_TIME, new byte[]{0, 0, 0, 0});

                            mRelationDevice.mState.mHeader = null;
                            Read(GattAttributes.WATCH_SERVICE, GattAttributes.HEADER);
                            mState = STATE_GET_HEADER;
                        } else {
                            VoiceAlert alert = mRelationDevice.mAction.mVoiceAlerts.poll();
                            byte[] timeInByte = new byte[]{(byte) (alert.mCountdown), (byte) (alert.mCountdown >> 8), (byte) (alert.mCountdown >> 16), (byte) (alert.mCountdown >> 24)};
                            Write(GattAttributes.WATCH_SERVICE, GattAttributes.VOICE_ALERT, new byte[]{alert.mNumber});
                            Write(GattAttributes.WATCH_SERVICE, GattAttributes.VOICE_EVET_ALERT_TIME, timeInByte);
                        }
                    }
                    break;

                case STATE_GET_HEADER:
                    if (mRelationDevice.mState.mHeader != null) {
                        if (mRelationDevice.mState.mHeader[0] == 1 && mRelationDevice.mState.mHeader[0] == 0) {
                            mRelationDevice.mState.mTime = null;
                            Read(GattAttributes.WATCH_SERVICE, GattAttributes.TIME);
                            mState = STATE_GET_TIME;

                        } else {
                            mRelationDevice.mAction.mSync = false;
                            Disconnect();
                            mState = STATE_INIT;
                        }
                    }
                    break;

                case STATE_GET_TIME:
                    if (mRelationDevice.mState.mTime != null) {
                        mRelationDevice.mState.mData1 = null;
                        Read(GattAttributes.WATCH_SERVICE, GattAttributes.DATA);
                        mState = STATE_GET_DATA1;
                    }
                    break;

                case STATE_GET_DATA1:
                    if (mRelationDevice.mState.mData1 != null) {
                        mRelationDevice.mState.mData2 = null;
                        Read(GattAttributes.WATCH_SERVICE, GattAttributes.DATA);
                        mState = STATE_GET_DATA2;
                    }
                    break;

                case STATE_GET_DATA2:
                    if (mRelationDevice.mState.mData2 != null) {
                        synchronized (mRelationDevice.mState.mInOurDoors) {
                            mRelationDevice.mState.mInOurDoors.add(new InOutDoor(mRelationDevice.mState.mTime, mRelationDevice.mState.mData1, mRelationDevice.mState.mData2));
                        }

                        Write(GattAttributes.WATCH_SERVICE, GattAttributes.CHECKSUM, new byte[]{1});
                        Read(GattAttributes.WATCH_SERVICE, GattAttributes.HEADER);

                        mState = STATE_GET_HEADER;
                    }
                    break;
            }

            mHandler.postDelayed(this, TRANSITION_GAP);
        }
    };

    public int SetScan(int second) {
        mRelationDevice.mAction.mScanTime = second * 1000 / TRANSITION_GAP;
        return mRelationDevice.mAction.mScanTime;
    }

    public int Sync(Device device) {
        mRelationDevice.mAction.mSync = true;
        mRelationDevice.Copy(device);
        return 0;
    }

    public int AddVoiceAlert(byte number, int countdown) {
        synchronized (mRelationDevice.mAction.mVoiceAlerts) {
            mRelationDevice.mAction.mVoiceAlerts.add(new VoiceAlert(number, countdown));
        }
        return mRelationDevice.mAction.mVoiceAlerts.size();
    }

    public class VoiceAlert {
        byte mNumber;
        int mCountdown;

        VoiceAlert(byte number, int countdown) {
            mNumber = number;
            mCountdown = countdown;
        }
    }

    public class InOutDoor {
        byte[] mTime;
        byte[] mData1;
        byte[] mData2;

        InOutDoor(byte[] time, byte[] data1, byte[] data2) {
            mTime = time;
            mData1 = data1;
            mData2 = data2;
        }
    }

    class Device {
        String mName;
        String mAddress;
        Action mAction;
        State mState;

        class Action {
            int mScanTime;
            boolean mSync;
            final Queue<VoiceAlert> mVoiceAlerts = new ConcurrentLinkedQueue<>();
        }

        class State {
            boolean mBonded;
            boolean mConnected;
            boolean mDiscovered;
            byte[] mAddress;
            byte[] mHeader;
            byte[] mTime;
            byte[] mData1;
            byte[] mData2;
            final Queue<InOutDoor> mInOurDoors = new ConcurrentLinkedQueue<>();
        }

        void resetFlag() {
            mAction.mScanTime = 0;
            mAction.mSync = false;
            synchronized (mAction.mVoiceAlerts) {
                mAction.mVoiceAlerts.clear();
            }
            synchronized (mState.mInOurDoors) {
                mState.mInOurDoors.clear();
            }
            mState.mBonded = false;
            mState.mConnected = false;
            mState.mDiscovered = false;
        }

        Device() {
            mName = "";
            mAddress = "";
            resetFlag();
        }

        Device(String name, String address) {
            mName = name;
            mAddress = address;
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
            mScanResult.add(new Device(name, address));
        }

        @Override
        public void onConnectionStateChange(String name, String address, boolean bonded, boolean connected) {
            if (address.equals(mRelationDevice.mAddress)) {
                if (connected)
                    mRelationDevice.mState.mConnected = connected;
                else
                    Log("Disconnected");
            }
        }

        @Override
        public void onServiceDiscovered() {
            mRelationDevice.mState.mDiscovered = true;
        }

        @Override
        public void onCharacteristicRead(UUID service, UUID characteristic, byte[] value) {
            if (service.toString().equals(GattAttributes.WATCH_SERVICE)) {
                switch (characteristic.toString()) {
                    case GattAttributes.ADDRESS:
                        if (value != null) {
                            mRelationDevice.mState.mAddress = value;
                            Log("Address " + bytesToHex(value));
                        }
                        break;
                    case GattAttributes.HEADER:
                        if (value != null) {
                            mRelationDevice.mState.mHeader = value;
                            Log("Header " + bytesToHex(value));
                        }
                        break;
                    case GattAttributes.TIME:
                        if (value != null) {
                            mRelationDevice.mState.mTime = value;
                            Log("Time " + bytesToHex(value));
                        }
                        break;
                    case GattAttributes.DATA:
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
}
