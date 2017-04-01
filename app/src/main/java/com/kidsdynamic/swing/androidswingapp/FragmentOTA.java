package com.kidsdynamic.swing.androidswingapp;


import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.TextView;

import com.polidea.rxandroidble.RxBleClient;
import com.polidea.rxandroidble.RxBleConnection;
import com.polidea.rxandroidble.RxBleDevice;
import com.polidea.rxandroidble.internal.RxBleLog;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Timer;
import java.util.TimerTask;

import rx.Subscription;
import util.ConstValue;
import util.Conversion;

public class FragmentOTA extends AppCompatActivity {
    private TextView mViewProgressText;

    RxBleDevice rxBleDevice;
    RxBleClient rxBleClient;
    Subscription scanSubscription;
    Subscription connectSubscription;
    RxBleConnection rxBleConnection;

    private Button goBackButton;
    private boolean gotImageType = false;
    private boolean mProgramming = false;

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

        rxBleClient = RxBleClient.create(this);

        goBackButton = (Button) findViewById(R.id.goBackButton);

        goBackButton.setOnClickListener(view -> {
            Disconnect();
            finish();
        });
        mViewProgressText = (TextView) findViewById(R.id.progressText);
        Bundle bundle = this.getIntent().getExtras();
        if (bundle != null) {
            macAddress = bundle.getString("mac_address", null);
            rxBleDevice = rxBleClient.getBleDevice(macAddress);
            Connect();

        }
        Start();
    }

    public void Start(){
        scanSubscription = rxBleClient.scanBleDevices()
                .subscribe(
                        rxBleScanResult -> {

                            if (rxBleScanResult.getBleDevice().getName() != null) {
                                if (rxBleScanResult.getBleDevice().getMacAddress().equals(macAddress)) {
                                    Connect(rxBleScanResult.getBleDevice());
                                    StopScan();
                                }
                            }

                        },
                        throwable -> ErrorHandler(throwable, "Error on scanning device. " + throwable.toString())
                );
    }


    private void Connect(RxBleDevice device) {
        this.rxBleDevice = device;
        Connect();
    }

    private void Connect() {
        connectSubscription = rxBleDevice.establishConnection(false)
                .subscribe(
                        rxBleConnection -> {
                            this.rxBleConnection = rxBleConnection;
                            StopScan();
                            ReadFirmware();

                        },
                        throwable -> ErrorHandler(throwable, "Error on connecting device")
                );
    }


    private void ReadFirmware() {
        rxBleConnection.readCharacteristic(ConstValue.FIRMWARE_UUID)
                .subscribe(
                        characteristicValue -> {
                            // All GATT operations are done through the rxBleConnection.
                            DisplayLog("Received From Value: " + new String(characteristicValue));
                            setConnectionParameters();
                        },
                        throwable -> ErrorHandler(throwable, "Error on getting F. " + throwable.toString())
                );
    }

    private void SetupNotification() {
        rxBleConnection.setupNotification(ConstValue.oadImageNotify_UUID)
                .doOnNext(notificationObservable -> {
                    // Notification has been set up
                    DisplayLog("Completed setup notification for notify");
                    GetTargetImageInfo();


                })
                .flatMap(notificationObservable -> notificationObservable)
                .subscribe(
                        bytes -> {

                            if(!gotImageType) {
                                DisplayLog("Action data notified - mCharIdentify");
                                gotImageType = true;
                                mTargImgHdr.ver = Conversion.buildUint16(bytes[1], bytes[0]);
                                mTargImgHdr.imgType = ((mTargImgHdr.ver & 1) == 1) ? 'B' : 'A';
                                mTargImgHdr.len = Conversion.buildUint16(bytes[3], bytes[2]);
                                SetupBlockNotification();
                                if (loadFile(mTargImgHdr.imgType == 'B' ? ConstValue.FW_FILE_A : ConstValue.FW_FILE_B, true)) {
                                    startProgramming();
                                }
                            }


                        },
                        throwable -> ErrorHandler(throwable, "Error on identify notification")

                );
    }

    private void SetupBlockNotification() {
        rxBleConnection.setupNotification(ConstValue.oadBlockRequest_UUID)
                .flatMap(notificationObservable -> notificationObservable)
                .subscribe(
                        bytes -> {
                            DisplayLog("Action data notified - mCharBlock");
                            DisplayLog(String.format("NB: %02x%02x", bytes[1], bytes[0]));
                            if(mProgramming) {
                                programBlock(((bytes[1] << 8) & 0xff00) + (bytes[0] & 0x00ff));
                            }

                        },
                        throwable -> ErrorHandler(throwable, "Error on block notification")

                );
    }

    private void programBlock(int block) {
        DisplayLog("iBlocks: " + mProgInfo.iBlocks + "  nBlocks: " + mProgInfo.nBlocks);

        if (mProgInfo.iBlocks < mProgInfo.nBlocks) {
            mProgramming = true;

            mProgInfo.iBlocks = (short) block;

            // Prepare block
            mOadBuffer[0] = Conversion.loUint16(mProgInfo.iBlocks);
            mOadBuffer[1] = Conversion.hiUint16(mProgInfo.iBlocks);
            System.arraycopy(mFileBuffer, mProgInfo.iBytes, mOadBuffer, 2, ConstValue.OAD_BLOCK_SIZE);

            // Send block
            DisplayLog("FwUpdateActivity" + String.format("TX Block %02x%02x", mOadBuffer[1], mOadBuffer[0]));

            rxBleConnection.writeCharacteristic(ConstValue.oadBlockRequest_UUID, mOadBuffer)
                    .subscribe(
                            characteristicValue -> {
                                // Characteristic value confirmed.
                                DisplayLog("Sent block success");
                                mProgInfo.iBlocks++;
                                mProgInfo.iBytes += ConstValue.OAD_BLOCK_SIZE;
                                final String message = "Progress: " + (mProgInfo.iBlocks * 100) / mProgInfo.nBlocks;
                                UpdateText(message);
                                if (mProgInfo.iBlocks == mProgInfo.nBlocks) {
                                    DisplayLog("Programming finished!!!!!!!!!!");
                                }

                            },
                            throwable -> ErrorHandler(throwable, "Error on sending block")
                    );

        } else {
            mProgramming = false;
        }

        if (!mProgramming) {
            DisplayLog("Stopped Programming");
        }
    }

    private void GetTargetImageInfo() {
        byte[] val = new byte[1];
        val[0] = (byte) 0;
        rxBleConnection.writeCharacteristic(ConstValue.oadImageNotify_UUID, val)
                .subscribe(
                        characteristicValue -> {
                            DisplayLog("Check - A");

                        },
                        throwable -> ErrorHandler(throwable, "Error on Checking A")
                );


        new Thread(() -> {
            try{
                Thread.sleep(1500);
                if(!gotImageType) {
                    byte[] vl = new byte[1];
                    vl[0] = (byte) 1;
                    rxBleConnection.writeCharacteristic(ConstValue.oadImageNotify_UUID, vl)
                            .subscribe(
                                    characteristicValue -> {
                                        DisplayLog("Check - B");

                                    },
                                    throwable -> ErrorHandler(throwable, "Error on Checking B")
                            );
                }


            } catch (Exception e ){
                e.printStackTrace();
                ErrorHandler(null, "Error on sending second Check - B");
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
        rxBleConnection.writeCharacteristic(ConstValue.oadImageNotify_UUID, buf)
                .subscribe(
                        characteristicValue -> {
                            // Characteristic value confirmed.
                            DisplayLog("Sent image notification");

                        },
                        throwable -> ErrorHandler(throwable, "Error on sending image notification")
                );

        // Initialize stats
        mProgInfo.reset();
    }

    private void displayImageInfo(ImgHdr h) {
        int imgVer = (h.ver) >> 1;
        int imgSize = h.len * 4;
        DisplayLog(String.format("Type: %c Ver.: %d Size: %d", h.imgType, imgVer, imgSize));

    }

    private boolean setConnectionParameters() {
        // Make sure connection interval is long enough for OAD (Android default connection interval is 7.5 ms)
        byte[] value = {Conversion.loUint16(ConstValue.OAD_CONN_INTERVAL), Conversion.hiUint16(ConstValue.OAD_CONN_INTERVAL), Conversion.loUint16(ConstValue.OAD_CONN_INTERVAL),
                Conversion.hiUint16(ConstValue.OAD_CONN_INTERVAL), 0, 0, Conversion.loUint16(ConstValue.OAD_SUPERVISION_TIMEOUT), Conversion.hiUint16(ConstValue.OAD_SUPERVISION_TIMEOUT)};

        rxBleConnection.writeCharacteristic(ConstValue.oadBlockRequest_UUID, value)
                .subscribe(
                        characteristicValue -> {
                            // Characteristic value confirmed.
                            DisplayLog("write connection parameters successfully");
                            DisplayLog(new String(characteristicValue));
                            SetupNotification();

                        },
                        throwable -> ErrorHandler(throwable, "Error on writing connection parameters")
                );

        return true;
    }



    private void StopScan() {
        if(scanSubscription != null && !scanSubscription.isUnsubscribed()) {
            scanSubscription.unsubscribe();
        }

    }

    private void Disconnect() {
        if(scanSubscription != null && !scanSubscription.isUnsubscribed()) {
            scanSubscription.unsubscribe();
        }
        if(connectSubscription != null && !connectSubscription.isUnsubscribed()) {
            connectSubscription.unsubscribe();
        }

    }


    @Override
    public void onResume() {
        super.onResume();
        DisplayLog("On Resume");
        if(connectSubscription != null && connectSubscription.isUnsubscribed()) {
            UpdateText("Reconnecting to your watch");
            Connect();
        }

    }

    @Override
    public void onPause() {
        DisplayLog("On Pause");
        Disconnect();

        super.onPause();
    }

    @Override
    public void onStop(){
        super.onStop();
        Disconnect();
        DisplayLog("On Stop");
    }

    @Override
    public void onDestroy() {
        super.onDestroy();

        DisplayLog("On Destory");
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

    private void ErrorHandler(Throwable throwable, String message) {
        throwable.printStackTrace();
        DisplayLog(message);
        Disconnect();
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
