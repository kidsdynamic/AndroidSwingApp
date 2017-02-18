package com.kidsdynamic.swing.androidswingapp;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothGatt;
import android.bluetooth.BluetoothGattCallback;
import android.bluetooth.BluetoothGattCharacteristic;
import android.bluetooth.BluetoothGattDescriptor;
import android.bluetooth.BluetoothGattService;
import android.bluetooth.BluetoothManager;
import android.bluetooth.BluetoothProfile;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Build;
import android.util.Log;

import java.lang.reflect.Method;
import java.util.List;
import java.util.Queue;
import java.util.UUID;
import java.util.concurrent.ConcurrentLinkedQueue;

public class BLEControl {
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
        Log.i("BLEControl", msg);
    }


    public BLEControl(Context context, OnEventListener listener) {
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

    protected BLEControl(Context context) {
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
    }

    protected void Init(OnEventListener listener) {
        mEventListener = listener;
        //mContext.registerReceiver(mBroadcastReceiver, new IntentFilter(BluetoothDevice.ACTION_BOND_STATE_CHANGED));
    }

    protected void Deinit() {
        mEventListener = null;
        //mContext.unregisterReceiver(mBroadcastReceiver);
    }

    boolean mBondStateReceiverEnabled = false;
    protected synchronized void EnableBondStateReceiver(boolean enable) {
        if (mBondStateReceiverEnabled == enable)
            return;

        mBondStateReceiverEnabled = enable;
        if (enable)
            mContext.registerReceiver(mBroadcastReceiver, new IntentFilter(BluetoothDevice.ACTION_BOND_STATE_CHANGED));
        else
            mContext.unregisterReceiver(mBroadcastReceiver);
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

    public boolean GetBondState(String address) {
        if (mBluetoothAdapter == null)
            return false;

        BluetoothDevice dev = mBluetoothAdapter.getRemoteDevice(address);

        return dev.getBondState() == BluetoothDevice.BOND_BONDED;
    }

    public synchronized boolean Connect() {
        if (mBluetoothAdapter == null || mDeviceAddress == null) {
            Log("mBluetoothAdapter == null or address == null");
            return false;
        }
        Log("Connect()+");

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

    public synchronized boolean Disconnect() {
        if(mBluetoothGatt == null)
            return false;
        mConnecting = false;
        mBluetoothGatt.disconnect();
        mDeviceAddress = null;
        return true;
    }

    public boolean Read(String service, String characteristic) {
        if(service == null || characteristic == null || !IsConnected())
            return false;

        BluetoothGattService lBleService;
        BluetoothGattCharacteristic lBleCharacteristic;

        lBleService = mBluetoothGatt.getService(UUID.fromString(service));
        if (lBleService != null) {
            lBleCharacteristic = lBleService.getCharacteristic(UUID.fromString(characteristic));
            if (lBleCharacteristic != null) {
                mTaskQueue.push(new TaskItem(lBleCharacteristic, false));
            }
        }
        return false;
    }

    public boolean Write(String service, String characteristic, byte[] value) {
        if (service == null || characteristic == null || value == null || !IsConnected())
            return false;

        BluetoothGattService lBleService;
        BluetoothGattCharacteristic lBleCharacteristic;

        lBleService = mBluetoothGatt.getService(UUID.fromString(service));
        if (lBleService != null) {

            lBleCharacteristic = lBleService.getCharacteristic(UUID.fromString(characteristic));
            if (lBleCharacteristic != null) {
                lBleCharacteristic.setValue(value);
                mTaskQueue.push(new TaskItem(lBleCharacteristic, true));
                return true;
            }
        }

        return false;
    }

    public boolean SetNotification(String service, String characteristic, boolean enable) {
        if (service == null || characteristic == null || !IsConnected())
            return false;

        BluetoothGattService lBleService;
        BluetoothGattCharacteristic lBleCharacteristic;
        BluetoothGattDescriptor lBleDescriptor;

        lBleService = mBluetoothGatt.getService(UUID.fromString(service));
        if (lBleService != null) {

            lBleCharacteristic = lBleService.getCharacteristic(UUID.fromString(characteristic));
            if (lBleCharacteristic != null) {
                lBleDescriptor = lBleCharacteristic.getDescriptor(UUID.fromString("00002902-0000-1000-8000-00805f9b34fb"));
                if (lBleDescriptor != null ) {
                    // https://developer.bluetooth.org/gatt/descriptors/Pages/DescriptorsHomePage.aspx
                    lBleDescriptor.setValue(new byte[]{(byte) (enable ? 0x01 : 0x00), (byte) 0x00});
                    mTaskQueue.push(new TaskItem(lBleDescriptor, true));
                }

                mBluetoothGatt.setCharacteristicNotification(lBleCharacteristic, enable);
                return true;
            }
        }

        return false;
    }

    public boolean IsConnected() {
        return (mBluetoothGatt != null && mConnectionState == BluetoothProfile.STATE_CONNECTED && !mDiscovering);
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
            Log("onServicesDiscovered " + status);
            /*
            List<BluetoothGattService> services = gatt.getServices();
            for (BluetoothGattService service : services) {
                Log("Service - " + service.getUuid().toString());
                List<BluetoothGattCharacteristic> characteristics = service.getCharacteristics();
                for(BluetoothGattCharacteristic characteristic : characteristics) {
                    Log("   Characteristic - " + characteristic.getUuid().toString());
                    List<BluetoothGattDescriptor> descriptors = characteristic.getDescriptors();
                    for(BluetoothGattDescriptor descriptor : descriptors) {
                        Log("       Descriptor - " + descriptor.getUuid().toString());
                    }
                }
            }
            */
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
            mTaskQueue.push(new TaskItem(descriptor, false));
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

    private class TaskItem {
        Object mObject;
        boolean mWrite;

        TaskItem(Object o, boolean write) {
            mObject = o;
            mWrite = write;
        }
    }

    private class TaskQueue {
        boolean mDeviceAccess = false;
        Queue<TaskItem> mDeviceAccessQueue = new ConcurrentLinkedQueue<>();
        long mDeviceAccessTick = 0;

        void reset() {
            mDeviceAccess = false;
            mDeviceAccessQueue.clear();
            mDeviceAccessTick = 0;
        }

        synchronized void push(TaskItem item) {
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

        synchronized boolean access(TaskItem item) {
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
