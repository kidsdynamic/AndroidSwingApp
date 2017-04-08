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
import java.util.Queue;
import java.util.UUID;
import java.util.concurrent.ConcurrentLinkedQueue;

import static android.bluetooth.BluetoothGatt.CONNECTION_PRIORITY_HIGH;

/**
 * 基本的BLE控制，最低限度的包裝Android的BLE相關功能，包含對BLE裝置的Scan, Connect, Disconnect, Read及
 * Write。 此外，對BLE的讀及寫，使用同步化的佇列。
 */
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

    /**
     * Constructor
     * @param context App's context.
     */
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

    /**
     * Initialization
     * @param listener The callback function.
     */
    protected void Init(OnEventListener listener) {
        mEventListener = listener;
        //mContext.registerReceiver(mBroadcastReceiver, new IntentFilter(BluetoothDevice.ACTION_BOND_STATE_CHANGED));
    }

    /**
     * Deinitialization
     */
    protected void Deinit() {
        mEventListener = null;
        //mContext.unregisterReceiver(mBroadcastReceiver);
    }

    boolean mBondStateReceiverEnabled = false;

    /**
     * Enable broadcast receiver for bonding state.
     * @param enable true for enable, or false to disable
     */
    protected synchronized void EnableBondStateReceiver(boolean enable) {
        if (mBondStateReceiverEnabled == enable)
            return;

        mBondStateReceiverEnabled = enable;
        if (enable) {
            mContext.registerReceiver(mBroadcastReceiver, new IntentFilter(BluetoothDevice.ACTION_BOND_STATE_CHANGED));
            mContext.registerReceiver(mBroadcastReceiver, new IntentFilter("android.bluetooth.device.action.PAIRING_REQUEST"));
        } else {
            mContext.unregisterReceiver(mBroadcastReceiver);
        }
    }

    /**
     * Broadcast receiver for bonding state.
     */
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
            } else if ("android.bluetooth.device.action.PAIRING_REQUEST".equals(intent.getAction())) {
                try {
                    BluetoothDevice device = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);

                    byte[] pin = (byte[]) BluetoothDevice.class.getMethod("convertPinToBytes", String.class).invoke(BluetoothDevice.class, "0000");
                    Method m = device.getClass().getMethod("setPin", byte[].class);
                    m.invoke(device, pin);
                    device.getClass().getMethod("setPairingConfirmation", boolean.class).invoke(device, true);

                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }

    };

    /**
     * Callback interface used to deliver LE scan results.
     */
    private BluetoothAdapter.LeScanCallback mLeScanResult = new BluetoothAdapter.LeScanCallback() {
        @Override
        public void onLeScan(final BluetoothDevice device, int rssi, byte[] scanRecord) {
            if (mEventListener != null) {
                String name = device.getName();
                String address = device.getAddress();

                if (name == null || address == null)
                    return;

                Log("onLeScan Name " + name + " Address " + address);
                mEventListener.onScanResult(name, address, device.getBondState() == BluetoothDevice.BOND_BONDED, rssi);
            }
        }
    };

    /**
     * Start/Stop a scan for Bluetooth LE devices.
     * @param enable true for start, or false to stop.
     * @return Upon successful completion return true. Otherwise, return false.
     */
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

    /**
     * Get the bond state of the remote device.
     * @param address MAC address of remote device.
     * @return Bond state
     */
    public boolean GetBondState(String address) {
        if (mBluetoothAdapter == null)
            return false;

        BluetoothDevice dev = mBluetoothAdapter.getRemoteDevice(address);

        return dev.getBondState() == BluetoothDevice.BOND_BONDED;
    }

    /**
     * Connect to GATT server by mDeviceAddress.
     * @return Upon successful completion return true. Otherwise, return false.
     */
    public synchronized boolean Connect() {
        if (mBluetoothAdapter == null || mDeviceAddress == null) {
            Log("mBluetoothAdapter == null or address == null");
            return false;
        }
        Log("Connect()+");

        mBonding = true;
        mTaskQueue.reset();

        BluetoothDevice dev = mBluetoothAdapter.getRemoteDevice(mDeviceAddress);
/*
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
*/
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

    /**
     * Connect to GATT server by address.
     * @param address MAC address of the remote device
     * @return Upon successful completion return true. Otherwise, return false.
     */
    public boolean Connect(String address) {
        mDeviceAddress = address;
        return Connect();
    }

    /**
     * Close this Bluetooth GATT client.
     * @return Upon successful completion return true. Otherwise, return false.
     */
    private boolean Close() {
        if (mBluetoothGatt == null)
            return false;

        mBluetoothGatt.close();
        mBluetoothGatt = null;

        return true;
    }

    /**
     * Disconnects an established connection, or cancels a connection attempt
     * currently in progress.
     * @return Upon successful completion return true. Otherwise, return false.
     */
    public synchronized boolean Disconnect() {
        if(mBluetoothGatt == null)
            return false;

        Log("Disconnect()+");
        mConnecting = false;
        mBluetoothGatt.disconnect();
        mDeviceAddress = null;


        String name = mBluetoothGatt.getDevice().getName();
        String address = mBluetoothGatt.getDevice().getAddress();

        Close();
        if (mEventListener != null)
            mEventListener.onConnectionStateChange(name, address, false, false);

        return true;
    }

    /**
     * Put the read request in the task queue.
     * @param service The UUID for GATT service that offer characteristic.
     * @param characteristic The UUID for characteristic.
     * @return Upon successful completion return true. Otherwise, return false.
     */
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

    /**
     * Put the write request in the task queue.
     * @param service The UUID for GATT service that offer characteristic.
     * @param characteristic The UUID for characteristic.
     * @param value The value to be write
     * @return Upon successful completion return true. Otherwise, return false.
     */
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

    private boolean IsConnected() {
        return (mBluetoothGatt != null && mConnectionState == BluetoothProfile.STATE_CONNECTED && !mDiscovering);
    }

    /**
     * BluetoothGatt callback.
     */
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
            if (Build.VERSION.SDK_INT >= 21) {
                mBluetoothGatt.requestConnectionPriority(CONNECTION_PRIORITY_HIGH);
            }
            if (mEventListener != null)
                mEventListener.onServiceDiscovered();
        }

        @Override
        public void onCharacteristicRead(BluetoothGatt gatt, BluetoothGattCharacteristic characteristic, int status) {
            if (mEventListener != null) {
                UUID uuidServ = characteristic.getService().getUuid();
                UUID uuidChar = characteristic.getUuid();
                byte[] value = characteristic.getValue();

                //Log("R- : " + uuidChar.toString() + " < " + bytesToHex(value));

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

                //Log("W- : " + uuidChar.toString() + " > " + bytesToHex(characteristic.getValue()));

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

    /**
     * The interface is used to implement BLEControl callbacks.
     */
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

    /**
     * Task queue item.
     */
    private class TaskItem {
        Object mObject;
        boolean mWrite;

        TaskItem(Object o, boolean write) {
            mObject = o;
            mWrite = write;
        }
    }

    /**
     * 對BLE裝置讀寫的工作佇列
     */
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
                    //Log("W+ : " + ((BluetoothGattCharacteristic)item.mObject).getUuid().toString() + " " + bytesToHex(((BluetoothGattCharacteristic)item.mObject).getValue()));
                    mBluetoothGatt.writeCharacteristic((BluetoothGattCharacteristic) item.mObject);
                } else {
                    //Log("R+ : " + ((BluetoothGattCharacteristic)item.mObject).getUuid().toString());
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

    private static String bytesToHex(byte[] in) {
        if (in == null || in.length == 0)
            return "";

        final StringBuilder builder = new StringBuilder();
        for (byte b : in) {
            builder.append(String.format("0x%02X ", b));
        }
        return builder.toString();
    }
}
