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
 * BLE有限狀態機
 */

class BLEMachine extends BLEControl {
    private Handler mHandler = new Handler();
    private onSearchListener mOnSearchListener = null;      // 搜尋裝置時的callback
    private onSyncListener mOnSyncListener = null;          // 同步時的callback
    private onBatteryListener mOnBatteryListener = null;    // 取得電量時的callback
    private boolean mRunning = false;                       // 狀態機是否運行中
    private ArrayList<WatchActivityRaw> mActivities;        // 同步時，由手表取得activity的暫存列表


    private void Log(String msg) {
        Log.i("BLEMachine", msg);
    }

    BLEMachine(Context context) {
        super(context);
    }

    boolean Start() {
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

    boolean Stop() {
        synchronized (this) {
            if (mRunning) {
                mHandler.removeCallbacks(stateTransition);
                Deinit();
                mRunning = false;
            }
        }
        return true;
    }

    private static final int TRANSITION_GAP = 1;

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
    private static final int STATE_GET_BATTERY = 12;
    private static final int STATE_PRE_INIT = 13;
    private static final int STATE_BYPASS_ALERT = 14;
    private static final int STATE_CANCEL = 15;

    private Device mRelationDevice = new Device();          // 狀態機當前使用的手表
    private int mState;                                     // 狀態機的狀態
    private ArrayList<Device> mScanResult;                  // 搜尋到的手表列表

    private List<VoiceAlert> mVoiceAlerts = new ArrayList<>();  // 欲同步的event列表
    private int mVoiceAlertCount = 0;                           // 已同步的event計數

    private long mTransmissionTick;                         // timeout的tick

    private void setTimeout(int value) {
        Calendar cal = Calendar.getInstance();
        mTransmissionTick = cal.getTimeInMillis() + value;
    }

    private boolean isTimeout() {
        Calendar cal = Calendar.getInstance();
        long currentTick = cal.getTimeInMillis();
        return currentTick >= mTransmissionTick;
    }


    private Runnable stateTransition = new Runnable() {

        @Override
        public void run() {

            if (mRelationDevice.mAction.mCancel) {
                if (mRelationDevice.mAction.mSync || mRelationDevice.mAction.mSendEvent || mRelationDevice.mAction.mBattery) {
                    // Some phone need time to release device's connection.
                    mState = STATE_CANCEL;
                    setTimeout(5000);
                }
                Disconnect();
                mRelationDevice.mAction.mCancel = false;
            }

            switch (mState) {
                case STATE_CANCEL:
                    if (isTimeout()) {
                        mState = STATE_INIT;
                        mRelationDevice.resetFlag();
                    }
                    break;

                case STATE_PRE_INIT:
                    if (!mRelationDevice.mState.mConnected) {
                        mRelationDevice.mState.mDiscovered = false;
                        mState = STATE_INIT;
                        if (mHandler != null) {
                            mHandler.postDelayed(this, 1000);
                            return;
                        }
                    }
                    break;

                case STATE_INIT:
                    if (mRelationDevice.mAction.mScanTime != 0) {
                        mState = STATE_SCAN;
                        mScanResult = new ArrayList<>();
                        mRelationDevice.mState.mFoundSearchAddress = false;
                        Scan(true);

                    } else if (mRelationDevice.mAction.mSync || mRelationDevice.mAction.mBattery || mRelationDevice.mAction.mSendEvent) {
                        setTimeout(10000);
                        //if (GetBondState(mRelationDevice.mAddress)) {
                        EnableBondStateReceiver(false);
                        mState = STATE_CONNECTING;
                        Connect(mRelationDevice.mAddress);
                        //} else {
                        //    mState = STATE_BONDING;
                        //    EnableBondStateReceiver(true);
                        //    Connect(mRelationDevice.mAddress);
                        //}
                    } else {
                        if (mHandler != null) {
                            mHandler.postDelayed(this, 100);
                            return;
                        }
                    }
                    break;

                case STATE_SCAN:
                    if (--mRelationDevice.mAction.mScanTime == 0 ||
                            mOnSearchListener == null ||
                            mRelationDevice.mState.mFoundSearchAddress) {
                        mState = STATE_INIT;
                        Scan(false);

                        if (mOnSearchListener != null)
                            mOnSearchListener.onSearch(mScanResult);

                        mRelationDevice.mAction.mScanTime = 0;
                    }
                    break;

                case STATE_BONDING:
                    if (mRelationDevice.mState.mBonded) {
                        mState = STATE_INIT;
                    } else if (isTimeout()) {
                        syncFailProcess();
                    }
                    break;

                case STATE_CONNECTING:
                    if (mRelationDevice.mState.mConnected) {
                        mState = STATE_DISCOVERY;

                    } else if (isTimeout()) {

                        mState = STATE_PRE_INIT;
                        if (++mRelationDevice.mState.mRetryTimes > 10)
                            syncFailProcess();
                        else
                            Log.d("XXXXX", "Connect failed, retry " + mRelationDevice.mState.mRetryTimes);
                    }
                    break;

                case STATE_DISCOVERY:
                    if (mRelationDevice.mState.mDiscovered) {
                        if (mRelationDevice.mAction.mSendEvent) {
                            int currentTime = (int) (getCurrentTime() / 1000);
                            byte[] timeInByte = new byte[]{(byte) (currentTime), (byte) (currentTime >> 8), (byte) (currentTime >> 16), (byte) (currentTime >> 24)};
                            mState = STATE_SET_TIME;
                            Write(BLECustomAttributes.WATCH_SERVICE, BLECustomAttributes.ACCEL_ENABLE, new byte[]{1});
                            Write(BLECustomAttributes.WATCH_SERVICE, BLECustomAttributes.TIME, timeInByte);
                        } else {
                            setTimeout(4500);
                            mState = STATE_GET_BATTERY;
                            mRelationDevice.mState.mBatteryUpdated = false;
                            Read(BLECustomAttributes.BATTERY_SERVICE, BLECustomAttributes.BATTERY_LEVEL);
                        }
                    } else if (!mRelationDevice.mState.mConnected) {
                        mState = STATE_PRE_INIT;
                        //syncFailProcess();
                    }
                    break;

                case STATE_SET_TIME:
                    mRelationDevice.mState.mAddress = null;
                    mState = STATE_GET_ADDRESS;
                    Read(BLECustomAttributes.WATCH_SERVICE, BLECustomAttributes.ADDRESS);
                    break;

                case STATE_GET_ADDRESS:
                    if (mRelationDevice.mState.mAddress != null) {
                        if (mRelationDevice.mAction.mSendEvent) {
                            mState = STATE_SEND_ALERT;
                            mVoiceAlertCount = 0;
                            mRelationDevice.mState.mAlertDataDone = true;
                            mRelationDevice.mState.mAlertTimeDone = true;
                        } else {
                            mState = STATE_BYPASS_ALERT;
                        }
                    } else if (!mRelationDevice.mState.mConnected) {
                        syncFailProcess();
                    }

                    break;

                case STATE_BYPASS_ALERT:
                    Write(BLECustomAttributes.WATCH_SERVICE, BLECustomAttributes.VOICE_ALERT, new byte[]{0});
                    Write(BLECustomAttributes.WATCH_SERVICE, BLECustomAttributes.VOICE_EVET_ALERT_TIME, new byte[]{0, 0, 0, 0});

                    mRelationDevice.mState.mHeader = null;
                    Read(BLECustomAttributes.WATCH_SERVICE, BLECustomAttributes.HEADER);
                    mState = STATE_GET_HEADER;
                    break;

                case STATE_SEND_ALERT:
                    if (!mRelationDevice.mState.mConnected) {
                        syncFailProcess();
                    } else if (mRelationDevice.mState.mAlertDataDone && mRelationDevice.mState.mAlertTimeDone) {
                        if (mVoiceAlerts.isEmpty() || mVoiceAlertCount >= 100) {
                            Log("Event finish");
                            mRelationDevice.mAction.mSendEvent = false;
                            Disconnect();
                            mState = STATE_PRE_INIT;

                            if (mOnSyncListener != null)
                                mOnSyncListener.onSync(SYNC_RESULT_SUCCESS, mActivities);

                            mState = STATE_INIT;
                        } else {
                            mRelationDevice.mState.mAlertDataDone = false;
                            mRelationDevice.mState.mAlertTimeDone = false;
                            VoiceAlert alert = mVoiceAlerts.get(0);
                            mVoiceAlerts.remove(0);

                            long countdown = toWatchTime(alert.mTimeStamp);
                            if (countdown > 0) {
                                byte[] timeInByte = new byte[]{(byte) (countdown), (byte) (countdown >> 8), (byte) (countdown >> 16), (byte) (countdown >> 24)};
                                Write(BLECustomAttributes.WATCH_SERVICE, BLECustomAttributes.VOICE_ALERT, new byte[]{alert.mAlert});
                                Write(BLECustomAttributes.WATCH_SERVICE, BLECustomAttributes.VOICE_EVET_ALERT_TIME, timeInByte);
                                mVoiceAlertCount++;
                                Log("Event[" + mVoiceAlertCount + "] " + alert.mAlert + " " + countdown);
                            }
                        }
                    }
                    break;

                case STATE_GET_HEADER:
                    if (isTimeout()) {
                        // The throughput of device becomes very slow when the communication for more than 5 seconds.
                        Disconnect();
                        mRelationDevice.mState.mRetryTimes = 0;
                        mState = STATE_PRE_INIT;
                    } else if (mRelationDevice.mState.mHeader != null) {
                        if (mRelationDevice.mState.mHeader[0] == 1 && mRelationDevice.mState.mHeader[1] == 0) {
                            mRelationDevice.mState.mTime = null;
                            Read(BLECustomAttributes.WATCH_SERVICE, BLECustomAttributes.TIME);
                            mState = STATE_GET_TIME;

                        } else {
                            mRelationDevice.mAction.mSync = false;
                            mRelationDevice.mAction.mSendEvent = true;
                            Disconnect();
                            mRelationDevice.mState.mRetryTimes = 0;
                            mState = STATE_PRE_INIT;

                            //if (mOnSyncListener != null)
                            //    mOnSyncListener.onSync(SYNC_RESULT_SUCCESS, mActivities);

                            //mState = STATE_INIT;
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
                        WatchActivityRaw activityRaw = new WatchActivityRaw(ServerMachine.getMacID(mRelationDevice.mAddress), mRelationDevice.mState.mTime, mRelationDevice.mState.mData1, mRelationDevice.mState.mData2);
                        Log("Activity " + activityRaw.mTime + " " + activityRaw.mIndoor + " " + activityRaw.mOutdoor);
                        mActivities.add(activityRaw);
                        Write(BLECustomAttributes.WATCH_SERVICE, BLECustomAttributes.CHECKSUM, new byte[]{1});
                        mRelationDevice.mState.mHeader = null;
                        Read(BLECustomAttributes.WATCH_SERVICE, BLECustomAttributes.HEADER);

                        mState = STATE_GET_HEADER;
                    } else if (!mRelationDevice.mState.mConnected) {
                        syncFailProcess();
                    }

                    break;

                case STATE_GET_BATTERY:
                    if (mRelationDevice.mState.mBatteryUpdated) {
                        if (mRelationDevice.mAction.mBattery) {
                            mRelationDevice.mAction.mBattery = false;
                            Disconnect();

                            if (mOnBatteryListener != null)
                                mOnBatteryListener.onBattery(mRelationDevice.mState.mBattery);
                            mState = STATE_INIT;
                        } else {
                            int currentTime = (int) (getCurrentTime() / 1000);
                            byte[] timeInByte = new byte[]{(byte) (currentTime), (byte) (currentTime >> 8), (byte) (currentTime >> 16), (byte) (currentTime >> 24)};
                            mState = STATE_SET_TIME;
                            Write(BLECustomAttributes.WATCH_SERVICE, BLECustomAttributes.ACCEL_ENABLE, new byte[]{1});
                            Write(BLECustomAttributes.WATCH_SERVICE, BLECustomAttributes.TIME, timeInByte);

                            if (mOnSyncListener != null) {
                                WatchBattery wb = new WatchBattery(mRelationDevice.mState.mBattery, ServerMachine.getMacID(mRelationDevice.mAddress), timeInByte);
                                mOnSyncListener.onBattery(wb);

                            }
                        }

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

        if (mRelationDevice.mAction.mSync && mOnSyncListener != null)
            mOnSyncListener.onSync(mState, mActivities);

        if (mRelationDevice.mAction.mBattery && mOnBatteryListener != null)
            mOnBatteryListener.onBattery((byte) 0xFF);

        mRelationDevice.resetFlag();

        mState = STATE_INIT;
    }

    final static int SYNC_RESULT_SUCCESS = 0xFFFFFFFF;

    interface onSearchListener {
        void onSearch(ArrayList<Device> result);

        //void onFinish(int resultCode, ArrayList<InOutDoor> result);
    }

    interface onSyncListener {
        void onSync(int resultCode, ArrayList<WatchActivityRaw> result);

        void onBattery(WatchBattery battery);
    }

    interface onBatteryListener {
        void onBattery(byte value);
    }

    /**
     * 用在搜尋裝置，時間到會停步搜尋，並回傳裝置列表
     * @param listener 回傳裝置列表的callback
     * @param second 想要搜尋的時間，以秒數計
     * @return 搜尋的時間
     */
    int Search(onSearchListener listener, int second) {
        if (listener == null) {
            mOnSearchListener = null;
            mRelationDevice.mAction.mScanTime = 0;
        } else {
            mOnSearchListener = listener;
            mRelationDevice = new Device("Swing", "", 0);
            mRelationDevice.mAction.mScanTime = second * 1000 / TRANSITION_GAP;
        }
        return mRelationDevice.mAction.mScanTime;
    }

    /**
     * 用以搜尋特定手表，搜尋到該手表或10秒未找到，會停止搜尋
     * @param listener 回傳裝置列表的callback回傳裝置列表的callback
     * @param address 想要尋的手表MAC
     * @return 搜尋的時間
     */
    int Search(onSearchListener listener, String address) {
        if (listener == null) {
            mOnSearchListener = null;
            mRelationDevice.mAction.mScanTime = 0;
        } else {
            mOnSearchListener = listener;
            mRelationDevice = new Device("Swing", address, 0);
            mRelationDevice.mAction.mScanTime = 10 * 1000 / TRANSITION_GAP;
        }
        return mRelationDevice.mAction.mScanTime;
    }

    /**
     * 同步手表
     * @param listener 同步完成的callback
     * @param device 欲同步的手表
     * @param alerts event列表
     * @return always 0
     */
    int Sync(onSyncListener listener, Device device, List<VoiceAlert> alerts) {
        mOnSyncListener = listener;
        mRelationDevice = new Device(device.mName, device.mAddress, 0);
        mRelationDevice.mAction.mSync = true;
        mVoiceAlerts = alerts;
        mActivities = new ArrayList<>();
        return 0;
    }

    /**
     * 同步手表，用於第一次連線
     * @param listener 同步完成的callback
     * @param macAddress 欲同步的手表MAC
     * @return always 0
     */
    int Sync(onSyncListener listener, String macAddress) {
        mOnSyncListener = listener;
        mRelationDevice = new Device("Swing", macAddress, 0);
        mRelationDevice.mAction.mSync = true;
        mVoiceAlerts = new ArrayList<>();
        mActivities = new ArrayList<>();
        return 0;
    }

    /**
     * 取得手表電量
     * @param listener 取得手表電量的callback
     * @param macAddress 欲取得電量的手表MAC
     * @return always 0
     */
    int Battery(onBatteryListener listener, String macAddress) {
        mOnBatteryListener = listener;
        mRelationDevice = new Device("Swing", macAddress, 0);
        mRelationDevice.mAction.mBattery = true;
        return 0;
    }

    boolean Cancel() {
        mRelationDevice.mAction.mCancel = true;
        return true;
    }

    static class VoiceAlert {
        byte mAlert;
        long mTimeStamp;

        VoiceAlert(byte number, long countdown) {
            mAlert = number;
            mTimeStamp = countdown;
        }
    }

    class Device {
        String mName;
        String mAddress;
        int mRssi;
        Action mAction;
        State mState;

        class Action {
            int mScanTime;
            boolean mSync;
            boolean mBattery;
            boolean mSendEvent;
            boolean mCancel;
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
            boolean mFoundSearchAddress;
            boolean mBatteryUpdated;
            byte mBattery;
            boolean mAlertTimeDone;
            boolean mAlertDataDone;
            int mRetryTimes;
        }

        void resetFlag() {
            mAction.mScanTime = 0;
            mAction.mSync = false;
            mAction.mSendEvent = false;
            mAction.mBattery = false;
            mAction.mCancel = false;
            mState.mBonded = false;
            mState.mConnected = false;
            mState.mDiscovered = false;
            mState.mRetryTimes = 0;
            mState.mAlertDataDone = false;
            mState.mAlertTimeDone = false;
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
                if (dev.mName.equals(name) && dev.mAddress.equals(address)) {
                    dev.mRssi = rssi;
                    return;
                }
            }
            mScanResult.add(new Device(name, address, rssi));
            mRelationDevice.mState.mFoundSearchAddress = address.equals(mRelationDevice.mAddress);
        }

        @Override
        public void onConnectionStateChange(String name, String address, boolean bonded, boolean connected) {
            if (address.equals(mRelationDevice.mAddress)) {
                mRelationDevice.mState.mConnected = connected;

                if (!connected) {
                    if (mRelationDevice.mAction.mSync || mRelationDevice.mAction.mBattery || mRelationDevice.mAction.mSendEvent) {
                        Log("Disconnect state " + mState);
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
                            //Log("Address " + bytesToHex(value));
                        }
                        break;
                    case BLECustomAttributes.HEADER:
                        if (value != null) {
                            mRelationDevice.mState.mHeader = value;
                            //Log("Header " + bytesToHex(value));
                        }
                        break;
                    case BLECustomAttributes.TIME:
                        if (value != null) {
                            mRelationDevice.mState.mTime = value;
                            //Log("Time " + bytesToHex(value));
                        }
                        break;
                    case BLECustomAttributes.DATA:
                        if (value != null) {
                            if (mRelationDevice.mState.mData1 == null) {
                                mRelationDevice.mState.mData1 = value;
                                //Log("Data1 " + bytesToHex(value));
                            } else {
                                mRelationDevice.mState.mData2 = value;
                                //Log("Data2 " + bytesToHex(value));
                            }
                        }
                        break;
                }
            } else if (service.toString().equals(BLECustomAttributes.BATTERY_SERVICE)) {
                if (characteristic.toString().equals(BLECustomAttributes.BATTERY_LEVEL)) {
                    //Log("Battery " + value[0]);

                    if (value != null && value.length > 0)
                        mRelationDevice.mState.mBattery = value[0];
                    //else
                    //    mRelationDevice.mState.mBattery = 0;

                    mRelationDevice.mState.mBatteryUpdated = true;
                }
            }
        }

        @Override
        public void onCharacteristicChange(UUID service, UUID characteristic, byte[] value) {

        }

        @Override
        public void onCharacteristicWrite(UUID service, UUID characteristic) {
            if (service.toString().equals(BLECustomAttributes.WATCH_SERVICE)) {
                switch (characteristic.toString()) {
                    case BLECustomAttributes.VOICE_ALERT:
                        mRelationDevice.mState.mAlertDataDone = true;
                        break;
                    case BLECustomAttributes.VOICE_EVET_ALERT_TIME:
                        mRelationDevice.mState.mAlertTimeDone = true;
                        break;
                }
            }
        }

        @Override
        public void onRssiUpdate(int rssi) {

        }
    };

    private static String bytesToHex(byte[] in) {
        final StringBuilder builder = new StringBuilder();
        for (byte b : in) {
            builder.append(String.format("0x%02X ", b));
        }
        return builder.toString();
    }

    private static long getCurrentTime() {
        Calendar now = Calendar.getInstance();
        int offset = now.getTimeZone().getOffset(now.getTimeInMillis());
        return now.getTimeInMillis() + offset;
    }

    private static int toWatchTime(long utc) {
        Calendar now = Calendar.getInstance();
        int offset = now.getTimeZone().getOffset(now.getTimeInMillis());
        return (int) ((utc + offset) / 1000);
    }

    public static long toUtcTime(int time) {
        Calendar now = Calendar.getInstance();
        int offset = now.getTimeZone().getOffset(now.getTimeInMillis());
        return (time - offset) * 1000;
    }
}
