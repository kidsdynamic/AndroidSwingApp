package com.kidsdynamic.swing.androidswingapp;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothGatt;
import android.bluetooth.BluetoothGattCallback;
import android.bluetooth.BluetoothGattCharacteristic;
import android.bluetooth.BluetoothGattDescriptor;
import android.bluetooth.BluetoothManager;
import android.bluetooth.BluetoothProfile;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Build;
import android.util.Log;

import java.lang.reflect.Method;
import java.util.Queue;
import java.util.UUID;
import java.util.concurrent.ConcurrentLinkedQueue;

/**
 * Created by 03542 on 2016/12/29.
 */

public class BleControl {
    private BluetoothAdapter mBluetoothAdapter;
    private BluetoothGatt mBluetoothGatt = null;
    private String mDeviceAddress = null;

    private Context mContext;
    private OnEventListener mEventListener = null;

    private int mConnectionState = BluetoothProfile.STATE_DISCONNECTED;
    private boolean mScanning = false;
    private boolean mBonding = false;
    private boolean mConnecting = false;
    private boolean mDiscovering = false;
    private TaskQueue mTaskQueue = new TaskQueue();

    private void Log(String msg) {
        Log.i("LeControl", msg);
    }


    public BleControl(Context context, OnEventListener listener) {
        mContext = context;

        mBluetoothAdapter = ((BluetoothManager) mContext.getSystemService(Context.BLUETOOTH_SERVICE)).getAdapter();
        while (!mBluetoothAdapter.isEnabled()) {
            mBluetoothAdapter.enable();
            try {
                Thread.sleep(10);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        mEventListener = listener;

        mContext.registerReceiver(mBroadcastReceiver, new IntentFilter(BluetoothDevice.ACTION_BOND_STATE_CHANGED));
    }

    private final BroadcastReceiver mBroadcastReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            if (BluetoothDevice.ACTION_BOND_STATE_CHANGED.equals(intent.getAction())) {
                BluetoothDevice device = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);

                if (mDeviceAddress == null || !mDeviceAddress.equals(device.getAddress())) return;

                switch (device.getBondState()) {
                    case BluetoothDevice.BOND_BONDED:
                        Log("BluetoothDevice.BOND_BONDED");
                        break;
                    case BluetoothDevice.BOND_NONE:
                        Log("BluetoothDevice.BOND_NONE");
                        break;
                    case BluetoothDevice.BOND_BONDING:
                        Log("BluetoothDevice.BOND_BONDING");
                        break;
                }
                if (mEventListener != null)
                    mEventListener.onBondStateChange(device.getBondState());
            }
        }

    };

    private BluetoothAdapter.LeScanCallback mLeScanResult = new BluetoothAdapter.LeScanCallback() {
        @Override
        public void onLeScan(final BluetoothDevice device, int rssi, byte[] scanRecord) {
            if (mEventListener != null) {
                String name = device.getName();
                String address = device.getAddress();

                if (name == null || address == null)
                    return;

                mEventListener.onScanResult(name, address, device.getBondState() == BluetoothDevice.BOND_BONDED, rssi);
            }
        }
    };

    public boolean Scan(boolean enable) {

        if (enable == mScanning)
            return false;

        if (enable)
            mBluetoothAdapter.startLeScan(mLeScanResult);
        else
            mBluetoothAdapter.stopLeScan(mLeScanResult);

        mScanning = enable;

        return true;
    }

    public synchronized boolean Connect() {
        if (mBluetoothAdapter == null || mDeviceAddress == null) {
            Log("mBluetoothAdapter == null or address == null");
            return false;
        }
        Log("Connect()");

        mBonding = true;
        mTaskQueue.reset();

        BluetoothDevice dev = mBluetoothAdapter.getRemoteDevice(mDeviceAddress);

        if (dev.getBondState() == BluetoothDevice.BOND_NONE) {
            Log("Bond state is none, pair first...");
            if (Build.VERSION.SDK_INT <= Build.VERSION_CODES.JELLY_BEAN_MR2) {
                try {
                    Method m = dev.getClass().getMethod("createBond", (Class[]) null);
                    m.invoke(dev, (Object[]) null);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            } else {
                dev.createBond();
            }
            return false;
        }

        if (mBluetoothGatt != null && (mConnecting || mDiscovering)) {
            mBluetoothGatt.disconnect();
            mDiscovering = false;
        }

        mConnecting = true;
        mBonding = false;
        Close();
        mBluetoothGatt = dev.connectGatt(mContext, false, mGattCallback);

        return true;
    }

    public boolean Connect(String address) {
        mDeviceAddress = address;
        return Connect();
    }

    private boolean Close() {
        if (mBluetoothGatt == null)
            return false;

        mBluetoothGatt.close();
        mBluetoothGatt = null;

        return true;
    }

    private final BluetoothGattCallback mGattCallback = new BluetoothGattCallback() {
        @Override
        public void onConnectionStateChange(BluetoothGatt gatt, int status, int newState) {
            mConnecting = false;
            switch (newState) {
                case BluetoothProfile.STATE_DISCONNECTED:
                    Log("STATE_DISCONNECTED Address : " + gatt.getDevice().getAddress());
                    mDiscovering = false;
                    break;
                case BluetoothProfile.STATE_CONNECTED:
                    Log("STATE_CONNECTED Address : " + gatt.getDevice().getAddress());
                    mDiscovering = true;
                    mBluetoothGatt.discoverServices();
                    break;
            }

            if (mEventListener != null)
                mEventListener.onConnectionStateChange(gatt.getDevice().getName(), gatt.getDevice().getAddress(), gatt.getDevice().getBondState() == BluetoothDevice.BOND_BONDED, newState == BluetoothProfile.STATE_CONNECTED);
            mConnectionState = newState;
        }

        @Override
        public void onServicesDiscovered(BluetoothGatt gatt, int status) {
            mDiscovering = false;
            if (mEventListener != null)
                mEventListener.onServiceDiscovered();
        }

        @Override
        public void onCharacteristicRead(BluetoothGatt gatt, BluetoothGattCharacteristic characteristic, int status) {
            if (mEventListener != null) {
                UUID uuidServ = characteristic.getService().getUuid();
                UUID uuidChar = characteristic.getUuid();
                byte[] value = characteristic.getValue();
                mEventListener.onCharacteristicRead(uuidServ, uuidChar, value);
            }
            mTaskQueue.pop(true);
        }

        @Override
        public void onCharacteristicChanged(BluetoothGatt gatt, BluetoothGattCharacteristic characteristic) {
            if (mEventListener != null) {
                UUID uuidServ = characteristic.getService().getUuid();
                UUID uuidChar = characteristic.getUuid();
                byte[] value = characteristic.getValue();
                mEventListener.onCharacteristicChange(uuidServ, uuidChar, value);
            }
        }

        @Override
        public void onCharacteristicWrite(BluetoothGatt gatt, BluetoothGattCharacteristic characteristic, int status) {
            if (mEventListener != null) {
                UUID uuidServ = characteristic.getService().getUuid();
                UUID uuidChar = characteristic.getUuid();
                mEventListener.onCharacteristicWrite(uuidServ, uuidChar);
            }

            mTaskQueue.pop(true);
        }

        @Override
        public void onDescriptorRead(BluetoothGatt gatt, BluetoothGattDescriptor descriptor, int status) {
            mTaskQueue.pop(true);
        }

        @Override
        public void onDescriptorWrite(BluetoothGatt gatt, BluetoothGattDescriptor descriptor, int status) {
            mTaskQueue.push(new DeviceAccessQueueItem(descriptor, false));
            mTaskQueue.pop(true);
        }

        @Override
        public void onReadRemoteRssi(BluetoothGatt gatt, int rssi, int status) {
            if (mEventListener != null) {
                mEventListener.onRssiUpdate(rssi);
            }
        }

    };

    public interface OnEventListener {
        void onBondStateChange(int bondState);

        void onScanResult(String name, String address, boolean bonded, int rssi);

        void onConnectionStateChange(String name, String address, boolean bonded, boolean connected);

        void onServiceDiscovered();

        void onCharacteristicRead(UUID service, UUID characteristic, byte[] value);

        void onCharacteristicChange(UUID service, UUID characteristic, byte[] value);

        void onCharacteristicWrite(UUID service, UUID characteristic);

        void onRssiUpdate(int rssi);
    }

    private class DeviceAccessQueueItem {
        Object mObject;
        boolean mWrite;

        DeviceAccessQueueItem(Object o, boolean write) {
            mObject = o;
            mWrite = write;
        }
    }

    private class TaskQueue {
        boolean mDeviceAccess = false;
        Queue<DeviceAccessQueueItem> mDeviceAccessQueue = new ConcurrentLinkedQueue<>();
        long mDeviceAccessTick = 0;

        void reset() {
            mDeviceAccess = false;
            mDeviceAccessQueue.clear();
            mDeviceAccessTick = 0;
        }

        synchronized void push(DeviceAccessQueueItem item) {
            mDeviceAccessQueue.add(item);
            pop(false);
        }

        synchronized void pop(boolean accessDone) {
            if (accessDone)
                mDeviceAccess = false;

            if (!mDeviceAccessQueue.isEmpty() && !mDeviceAccess) {
                mDeviceAccess = access(mDeviceAccessQueue.poll());
                mDeviceAccessTick = System.currentTimeMillis();
            } else if (!mDeviceAccessQueue.isEmpty()) {
                if (System.currentTimeMillis() - mDeviceAccessTick > 2000) {
                    Log("Blocked! Purge queue!");
                    mDeviceAccess = false;
                    mDeviceAccessQueue.clear();
                }
            }
        }

        synchronized boolean access(DeviceAccessQueueItem item) {
            if (item.mObject instanceof BluetoothGattCharacteristic) {

                if (item.mWrite) {
                    //Log("WRITE : " + ((BluetoothGattCharacteristic)item.mObject).getUuid().toString());
                    mBluetoothGatt.writeCharacteristic((BluetoothGattCharacteristic) item.mObject);
                } else {
                    //Log("READ : " + ((BluetoothGattCharacteristic)item.mObject).getUuid().toString());
                    mBluetoothGatt.readCharacteristic((BluetoothGattCharacteristic) item.mObject);
                }

                return true;
            } else if (item.mObject instanceof BluetoothGattDescriptor) {

                if (item.mWrite) {
                    mBluetoothGatt.writeDescriptor((BluetoothGattDescriptor) item.mObject);
                } else {
                    mBluetoothGatt.readDescriptor((BluetoothGattDescriptor) item.mObject);
                }

                return true;
            } else {
                Log("Invalid item!");
            }

            return false;
        }

    }
}