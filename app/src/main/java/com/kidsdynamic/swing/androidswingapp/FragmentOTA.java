package com.kidsdynamic.swing.androidswingapp;


import android.Manifest;
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
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;
import java.util.UUID;

import util.ConstValue;
import util.Conversion;

public class FragmentOTA extends AppCompatActivity {
    private TextView mViewProgressText;

    private boolean mProgramming = false;
    private String mDeviceAddress = null;
    private boolean gotImageInfo = false;
    private String imageType = "A";
    private BluetoothAdapter mBluetoothAdapter;
    private BluetoothGatt mBluetoothGatt = null;

    private BluetoothGattService mOadService;
    private BluetoothGattCharacteristic mCharIdentify = null;
    private BluetoothGattCharacteristic mCharBlock = null;
    private IntentFilter mIntentFilter;
    private Button goBackButton;

    private boolean mDiscovering = false;
    private boolean mConnecting = false;

    private Handler mHandler;

    // Programming
    private final byte[] mFileBuffer = new byte[ConstValue.FILE_BUFFER_SIZE];
    private final byte[] mOadBuffer = new byte[ConstValue.OAD_BUFFER_SIZE];
    private ImgHdr mFileImgHdr = new ImgHdr();
    private ImgHdr mTargImgHdr = new ImgHdr();
    private Timer mTimer = null;
    private ProgInfo mProgInfo = new ProgInfo();
    private TimerTask mTimerTask = null;

    private String macAddress;


    private class ImgHdr {
        short ver;
        short len;
        Character imgType;
        byte[] uid = new byte[4];
    }

    private class ProgInfo {
        int iBytes = 0; // Number of bytes programmed
        short iBlocks = 0; // Number of blocks programmed
        short nBlocks = 0; // Total number of blocks
        int iTimeElapsed = 0; // Time elapsed in milliseconds

        void reset() {
            iBytes = 0;
            iBlocks = 0;
            iTimeElapsed = 0;
            nBlocks = (short) (mFileImgHdr.len / (ConstValue.OAD_BLOCK_SIZE / ConstValue.HAL_FLASH_WORD_SIZE));
        }
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.fragment_profile_ota);
        DisplayLog("On onCreate");

        goBackButton = (Button) findViewById(R.id.goBackButton);

        goBackButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
        mViewProgressText = (TextView) findViewById(R.id.progressText);
        Bundle bundle = this.getIntent().getExtras();
        if (bundle != null) {
            macAddress = bundle.getString("mac_address", null);
        }
        startScan();
    }

    public void startScan(){
        Log("On start sacn");
        try {
            Thread.sleep(1000);
        } catch(Exception e) {
            e.printStackTrace();
        }

        getApplicationContext().registerReceiver(mBroadcastReceiver, new IntentFilter(BluetoothDevice.ACTION_BOND_STATE_CHANGED));

        mBluetoothAdapter = ((BluetoothManager) getSystemService(Context.BLUETOOTH_SERVICE)).getAdapter();
        while (!mBluetoothAdapter.isEnabled()) {
            mBluetoothAdapter.enable();
            try {
                Thread.sleep(10);
            } catch (Exception e) {
                DisplayLog("Error: " + e.toString());
                e.printStackTrace();
            }
        }

        if (mBluetoothAdapter == null || !mBluetoothAdapter.isEnabled()) {
            Intent enableBtIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
            startActivityForResult(enableBtIntent, ConstValue.REQUEST_ENABLE_BT);
        }

        initIntentFilter();
        registerReceiver(mBroadcastReceiver, mIntentFilter);

        if(macAddress != null) {
            Connect(macAddress);
        } else {
            Scan(true);
        }

    }



    @Override
    public void onResume() {
        registerReceiver(mBroadcastReceiver, mIntentFilter);
        super.onResume();
//        startScan();

    }

    @Override
    public void onPause() {
//        getFragmentManager().popBackStack();
//        getApplicationContext().unregisterReceiver(mBroadcastReceiver);
//        unregisterReceiver(mBroadcastReceiver);

/*        mBluetoothGatt.disconnect();
        mBluetoothAdapter.cancelDiscovery();
        mBluetoothGatt = null;
        mBluetoothAdapter = null;
        Close();*/
        DisplayLog("On Pause");
        super.onPause();
    }

    @Override
    public void onStop(){
        super.onStop();
//        getApplicationContext().unregisterReceiver(mBroadcastReceiver);
//        unregisterReceiver(mBroadcastReceiver);

        mBluetoothGatt.disconnect();
        mBluetoothAdapter.cancelDiscovery();
        mBluetoothGatt = null;
        mBluetoothAdapter = null;
        Close();
        DisplayLog("On Stop");
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }


    private void initIntentFilter() {
        mIntentFilter = new IntentFilter();
        mIntentFilter.addAction(ConstValue.ACTION_DATA_NOTIFY);
        mIntentFilter.addAction(ConstValue.ACTION_DATA_WRITE);
    }

    public boolean Scan(boolean enable) {
        final long SCAN_PERIOD = 10000;

        if (enable) {
            mBluetoothAdapter.startLeScan(mLeScanResult);
            DisplayLog("Start scan...");
        } else {
            mBluetoothAdapter.stopLeScan(mLeScanResult);
            DisplayLog("Stop scan");
        }

        return true;
    }

    private void readFirmeware() {
        BluetoothGattService service = mBluetoothGatt.getService(ConstValue.FIRMWARE_SERVICE);
        if (service == null) {
            Log("Error. Can't find firmware service");
            return;
        }

        BluetoothGattCharacteristic character = service.getCharacteristic(ConstValue.FIRMWARE_UUID);
        if (character == null) {
            Log("Error. Can't find firmware character");
            return;
        }

        mBluetoothGatt.readCharacteristic(character);

    }

    private void setupCharacteristic(){
        mOadService = mBluetoothGatt.getService(ConstValue.oadService_UUID);
        mCharIdentify = mOadService.getCharacteristic(ConstValue.oadImageNotify_UUID);
        mCharBlock = mOadService.getCharacteristic(ConstValue.oadBlockRequest_UUID);
        mCharBlock.setWriteType(BluetoothGattCharacteristic.WRITE_TYPE_NO_RESPONSE);
        mBluetoothGatt.setCharacteristicNotification(mCharBlock, true);

        BluetoothGattDescriptor descriptor = mCharBlock.getDescriptor(ConstValue.CLIENT_CHARACTERISTIC_CONFIG);
        descriptor.setValue(BluetoothGattDescriptor.ENABLE_NOTIFICATION_VALUE);
        mBluetoothGatt.writeDescriptor(descriptor);

        getTargetImageInfo();
        new Thread(new Runnable() {
            @Override
            public void run() {
                while(true) {
                    if(gotImageInfo) {
                        String imageFile;
                        if(imageType.equals("A")){
                            imageFile = ConstValue.FW_FILE_A;
                        } else {
                            imageFile = ConstValue.FW_FILE_B;
                        }
                        if(loadFile(imageFile, true)){
                            startProgramming();
                        }
                        break;
                    }
                    try{
                        Thread.sleep(10);
                    } catch(Exception e) {
                        e.printStackTrace();
                    }

                }

            }
        }).start();

    }

    private boolean loadFile(String filepath, boolean isAsset) {
        // Load binary file
        try {
            // Read the file raw into a buffer
            InputStream stream;
            if (isAsset) {
                stream = getAssets().open(filepath);
            } else {
                File f = new File(filepath);
                stream = new FileInputStream(f);
            }
            stream.read(mFileBuffer, 0, mFileBuffer.length);
            stream.close();
        } catch (IOException e) {
            // Handle exceptions here
            DisplayLog("File open failed: " + filepath + "\n");
            return false;
        }

        // Show image info
        mFileImgHdr.ver = Conversion.buildUint16(mFileBuffer[5], mFileBuffer[4]);
        mFileImgHdr.len = Conversion.buildUint16(mFileBuffer[7], mFileBuffer[6]);
        mFileImgHdr.imgType = ((mFileImgHdr.ver & 1) == 1) ? 'B' : 'A';
        System.arraycopy(mFileBuffer, 8, mFileImgHdr.uid, 0, 4);
        displayImageInfo(mFileImgHdr);

        // Verify image types
        boolean ready = mFileImgHdr.imgType != mTargImgHdr.imgType;

        // Log
        DisplayLog("Image " + mFileImgHdr.imgType + " selected.\n");
        DisplayLog(ready ? "Ready to program device!\n" : "Incompatible image, select alternative!\n");

        return true;
    }

    private void getTargetImageInfo() {
        // Enable notification
        mCharIdentify.setWriteType(BluetoothGattCharacteristic.WRITE_TYPE_DEFAULT);
        mBluetoothGatt.setCharacteristicNotification(mCharIdentify, true);
        BluetoothGattDescriptor descriptor = mCharIdentify.getDescriptor(ConstValue.CLIENT_CHARACTERISTIC_CONFIG);
        descriptor.setValue(BluetoothGattDescriptor.ENABLE_NOTIFICATION_VALUE);
        mBluetoothGatt.writeDescriptor(descriptor);
        // Prepare data for request (try image A and B respectively, only one of
        // them will give a notification with the image info)

        while(!gotImageInfo) {
            new Thread(new Runnable() {
                @Override
                public void run() {
                    if(mBluetoothGatt == null) {
                        gotImageInfo = true;
                        return;
                    }
                    try{
                        Thread.sleep(500);
                    }catch(Exception e){
                        e.printStackTrace();
                    }
                    DisplayLog("working on First one");
                    byte[] val = new byte[1];
                    val[0] = (byte) 0;
                    mCharIdentify.setValue(val);
                    mBluetoothGatt.writeCharacteristic(mCharIdentify);

                }
            }).start();

            new Thread(new Runnable() {
                @Override
                public void run() {
                    try{
                        Thread.sleep(2000);
                    }catch(Exception e){
                        e.printStackTrace();
                    }
                    if(!gotImageInfo) {
                        imageType = "B";
                        mBluetoothGatt.setCharacteristicNotification(mCharIdentify, true);
                        DisplayLog("working on second one");
                        byte[] val = new byte[1];
                        val[0] = (byte) 1;
                        mCharIdentify.setValue(val);
                        mBluetoothGatt.writeCharacteristic(mCharIdentify);

                    }


                }
            }).start();
            try{
                Thread.sleep(4000);
            }catch(Exception e){
                e.printStackTrace();
            }
        }

    }

    private void startProgramming() {
        DisplayLog("Programming started\n");
        mProgramming = true;

        // Prepare image notification
        byte[] buf = new byte[ConstValue.OAD_IMG_HDR_SIZE + 2 + 2];
        buf[0] = Conversion.loUint16(mFileImgHdr.ver);
        buf[1] = Conversion.hiUint16(mFileImgHdr.ver);
        buf[2] = Conversion.loUint16(mFileImgHdr.len);
        buf[3] = Conversion.hiUint16(mFileImgHdr.len);
        System.arraycopy(mFileImgHdr.uid, 0, buf, 4, 4);

        // Send image notification
        mCharIdentify.setValue(buf);
        mBluetoothGatt.requestConnectionPriority(BluetoothGatt.CONNECTION_PRIORITY_HIGH);
        mBluetoothGatt.writeCharacteristic(mCharIdentify);

        // Initialize stats
        mProgInfo.reset();


    }

    private BluetoothAdapter.LeScanCallback mLeScanResult = new BluetoothAdapter.LeScanCallback() {
        @Override
        public void onLeScan(final BluetoothDevice device, int rssi, byte[] scanRecord) {

            final String name = device.getName();
            final String address = device.getAddress();


            DisplayLog("Found Device Name: " + name + "  Address: " + address);
            if (name == null || address == null)
                return;

            final int rssii = rssi;

            if (name.contains("SWING")) {
                Scan(false);
                UpdateText("Found Device \n " + address);
                DisplayLog("Found - Device Name: " + name + " Device Address: " + address);

                try {
                    Connect(device.getAddress());
                    UpdateText("Connecting to \n " + address);
                } catch (Exception e) {
                    e.printStackTrace();
                    DisplayLog(e.getMessage());
                }

            }


        }
    };

    private final BroadcastReceiver mBroadcastReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {

            final String action = intent.getAction();
            DisplayLog("Action data notified : " + action);
            if (ConstValue.ACTION_DATA_NOTIFY.equals(action)) {
                byte[] value = intent.getByteArrayExtra(ConstValue.EXTRA_DATA);
                String uuidStr = intent.getStringExtra(ConstValue.EXTRA_UUID);

                if (uuidStr.equals(mCharIdentify.getUuid().toString())) {
                    DisplayLog("Action data notified - mCharIdentify");
                    // Image info notification
                    mTargImgHdr.ver = Conversion.buildUint16(value[1], value[0]);
                    mTargImgHdr.imgType = ((mTargImgHdr.ver & 1) == 1) ? 'B' : 'A';
                    mTargImgHdr.len = Conversion.buildUint16(value[3], value[2]);
                    displayImageInfo(mTargImgHdr);

/*                    if(loadFile(mTargImgHdr.imgType, true)){
                        startProgramming();
                    }*/
                }
                if (uuidStr.equals(mCharBlock.getUuid().toString())) {
                    if(!gotImageInfo){
                        gotImageInfo = true;
/*                        try{
                            Thread.sleep(50);
                        } catch(Exception e) {
                            e.printStackTrace();
                        }*/
                    }
                    DisplayLog("Action data notified - mCharBlock");
                    DisplayLog(String.format("NB: %02x%02x", value[1], value[0]));
                    if (mProgramming == true)
                        programBlock(((value[1] << 8) & 0xff00) + (value[0] & 0x00ff));

                }
            } else if (ConstValue.ACTION_DATA_WRITE.equals(action)) {
                int status = intent.getIntExtra(ConstValue.EXTRA_STATUS, BluetoothGatt.GATT_SUCCESS);
                if (status != BluetoothGatt.GATT_SUCCESS) {
                    Toast.makeText(context, "GATT error: status=" + status, Toast.LENGTH_SHORT).show();
                }

            }
        }

    };

    private void broadcastUpdate(final String action, final BluetoothGattCharacteristic characteristic, final int status) {
        final Intent intent = new Intent(action);
        intent.putExtra(ConstValue.EXTRA_UUID, characteristic.getUuid().toString());
        intent.putExtra(ConstValue.EXTRA_DATA, characteristic.getValue());
        intent.putExtra(ConstValue.EXTRA_STATUS, status);
        sendBroadcast(intent);
    }

    private final BluetoothGattCallback mGattCallback = new BluetoothGattCallback() {
        @Override
        public void onConnectionStateChange(BluetoothGatt gatt, int status, int newState) {
            switch (newState) {
                case BluetoothProfile.STATE_DISCONNECTED:
                    DisplayLog("STATE_DISCONNECTED Address : " + gatt.getDevice().getAddress());
                    if(mProgInfo.iBlocks+10 < mProgInfo.nBlocks) {
                        UpdateText("Device Disconnected for some reason");
                    }

                    if(mConnecting) {
                        Connect(gatt.getDevice().getAddress());
                    }
                    mDiscovering = false;
                    break;
                case BluetoothProfile.STATE_CONNECTED:
                    DisplayLog("STATE_CONNECTED Address : " + gatt.getDevice().getAddress());
                    mDiscovering = true;
                    mConnecting = false;
                    UpdateText("Connected...");
                    if(mBluetoothGatt != null) {
                        mBluetoothGatt.discoverServices();
                    }

                    break;
            }

        }

        @Override
        public void onServicesDiscovered(BluetoothGatt gatt, int status) {
            mDiscovering = false;
            DisplayLog("Discovered");

//            enableDevice();
            readFirmeware();
//            displayGattServices(gatt.getServices());
        }


        @Override
        public void onCharacteristicRead(BluetoothGatt gatt, BluetoothGattCharacteristic characteristic, int status) {

            UUID uuidServ = characteristic.getService().getUuid();
            UUID uuidChar = characteristic.getUuid();
            byte[] value = characteristic.getValue();
            DisplayLog("On Characters Read. Service: " + uuidServ.toString() + "   Character: " + uuidChar.toString() + " Status: " + status);
            try {


                if (uuidChar.toString().equals(ConstValue.FIRMWARE_UUID.toString())) {
                    String version = new String(value);

                    DisplayLog("Firmware Version: " + version);
                    UpdateText("Firmware Version \n " + version + "\n Getting Start...");
                    new Thread(new Runnable() {
                        @Override
                        public void run() {
                            try{
                                Thread.sleep(50);
                            }catch(Exception e) {
                                e.printStackTrace();
                            }
                            setupCharacteristic();
                        }
                    }).start();


                }


            } catch (Exception e) {
                DisplayLog("Error: " + e.toString());
                e.printStackTrace();
            }

        }

        @Override
        public void onCharacteristicChanged(BluetoothGatt gatt, BluetoothGattCharacteristic characteristic) {
            UUID uuidServ = characteristic.getService().getUuid();
            UUID uuidChar = characteristic.getUuid();
            DisplayLog("On Characters Changed. Service: " + uuidServ.toString() + "   Character: " + uuidChar.toString());

            broadcastUpdate(ConstValue.ACTION_DATA_NOTIFY, characteristic, BluetoothGatt.GATT_SUCCESS);
        }


        @Override
        public void onCharacteristicWrite(BluetoothGatt gatt, BluetoothGattCharacteristic characteristic, int status) {
            UUID uuidServ = characteristic.getService().getUuid();
            UUID uuidChar = characteristic.getUuid();
            DisplayLog("On Characters Write. Service: " + uuidServ.toString() + "   Character: " + uuidChar.toString() + " Status: " + status);


        }

        @Override
        public void onDescriptorRead(BluetoothGatt gatt, BluetoothGattDescriptor descriptor, int status) {
            DisplayLog("On onDescriptorRead: " + status);

        }

        @Override
        public void onDescriptorWrite(BluetoothGatt gatt, BluetoothGattDescriptor descriptor, int status) {
            DisplayLog("On onDescriptorWrite: " + status);
        }

        @Override
        public void onReadRemoteRssi(BluetoothGatt gatt, int rssi, int status) {
            final int finalRssi = rssi;
            DisplayLog(String.valueOf(finalRssi));
        }

    };

          /*
   * Called when a notification with the current image info has been received
   */

    private void programBlock(int block) {
        if (!mProgramming)
            return;

        DisplayLog("iBlocks: " + mProgInfo.iBlocks + "  nBlocks: " + mProgInfo.nBlocks);

        if (mProgInfo.iBlocks < mProgInfo.nBlocks) {
            mProgramming = true;
            String msg = new String();

            mProgInfo.iBlocks = (short) block;

            // Prepare block
            mOadBuffer[0] = Conversion.loUint16(mProgInfo.iBlocks);
            mOadBuffer[1] = Conversion.hiUint16(mProgInfo.iBlocks);
            System.arraycopy(mFileBuffer, mProgInfo.iBytes, mOadBuffer, 2, ConstValue.OAD_BLOCK_SIZE);

            // Send block
            mCharBlock.setValue(mOadBuffer);
            DisplayLog("FwUpdateActivity" + String.format("TX Block %02x%02x", mOadBuffer[1], mOadBuffer[0]));

            boolean success = mBluetoothGatt.writeCharacteristic(mCharBlock);

            if (success) {
                // Update stats
                mProgInfo.iBlocks++;
                mProgInfo.iBytes += ConstValue.OAD_BLOCK_SIZE;
                String progressStep = "Progress: " + (mProgInfo.iBlocks * 100) / mProgInfo.nBlocks + "%";
//                DisplayLog("Progress: " + (mProgInfo.iBlocks * 100) / mProgInfo.nBlocks);
                UpdateText(progressStep);
                if (mProgInfo.iBlocks == mProgInfo.nBlocks) {
                    DisplayLog("Programming finished!!!!!!!!!!");
                }
            } else {
                mProgramming = false;
                msg = "GATT writeCharacteristic failed\n";
                Log(msg);
                UpdateText("Fail to update Firmware");
                mBluetoothGatt.disconnect();
                mBluetoothAdapter.cancelDiscovery();
            }
            if (!success) {
                DisplayLog(msg);
            }
        } else {
            mProgramming = false;
        }

        if (!mProgramming) {
            DisplayLog("Stopped Programming");
        }
    }


    public boolean Connect(String address) {
        mDeviceAddress = address;
        mConnecting = true;
        DisplayLog("Connecting to: " + mDeviceAddress);
        return Connect();
    }

    public synchronized boolean Connect() {
        if (mBluetoothAdapter == null || mDeviceAddress == null) {
            DisplayLog("mBluetoothAdapter == null or address == null");
            return false;
        }
        DisplayLog("Connecting");

        BluetoothDevice dev = mBluetoothAdapter.getRemoteDevice(mDeviceAddress);

        Close();
        mBluetoothGatt = dev.connectGatt(this, false, mGattCallback);

        return true;
    }

    private void displayImageInfo(ImgHdr h) {
        int imgVer = (h.ver) >> 1;
        int imgSize = h.len * 4;
        DisplayLog(String.format("Type: %c Ver.: %d Size: %d", h.imgType, imgVer, imgSize));

    }

    private boolean Close() {
        if (mBluetoothGatt == null)
            return false;

        mBluetoothGatt.close();
        mBluetoothGatt = null;

        return true;
    }

    private void DisplayLog(final String text) {
/*
        getActivity().runOnUiThread(new Runnable() {
            @Override
            public void run() {
                mViewProgressText.setText(text);
            }
        });


        logScroll.postDelayed(new Runnable() {
            @Override
            public void run() {
                logScroll.fullScroll(ScrollView.FOCUS_DOWN);

            }
        }, 1000);*/

        Log(text);
    }

    private void UpdateText(final String text) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                mViewProgressText.setText(text);
            }
        });
    }

    private void Log(String text) {
        Log.d(" DEBUG ", text);
    }

    final protected static char[] hexArray = "0123456789ABCDEF".toCharArray();

    public static String bytesToHex(byte[] bytes) {
        char[] hexChars = new char[bytes.length * 2];
        for (int j = 0; j < bytes.length; j++) {
            int v = bytes[j] & 0xFF;
            hexChars[j * 2] = hexArray[v >>> 4];
            hexChars[j * 2 + 1] = hexArray[v & 0x0F];
        }
        return new String(hexChars);
    }
}
