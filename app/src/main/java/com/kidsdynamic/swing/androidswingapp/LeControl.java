package com.kidsdynamic.swing.androidswingapp;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.util.Log;

import java.util.UUID;

/**
 * Created by 03542 on 2016/12/29.
 */

public class LeControl {
    private BluetoothAdapter mBluetoothAdapter;
    private String mDeviceAddress = null;

    private Context mContext;
    private OnEventListener mEventListener = null;

    private boolean mScanning = false;


    private void Log(String msg) {
        Log.i("LeControl", msg);
    }


    public LeControl(Context context, OnEventListener listener) {
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


    public interface OnEventListener {
        void onBondStateChange(int bondState);

        void onScanResult(String name, String address, boolean bonded, int rssi);

        void onConnectionStateChange(String name, String address, boolean bonded, boolean connected);

        void onServiceDiscover();

        void onCharacteristicRead(UUID service, UUID characteristic, byte[] value);

        void onCharacteristicChange(UUID service, UUID characteristic, byte[] value);

        void onCharacteristicWrite(UUID service, UUID characteristic);

        void onRssiUpdate(int rssi);
    }
}
