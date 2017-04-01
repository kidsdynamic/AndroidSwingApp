package util;

import android.os.Environment;

import java.util.UUID;

/**
 * Created by yen-chiehchen on 3/24/17.
 */


public class ConstValue {
    public static final UUID CLIENT_CHARACTERISTIC_CONFIG = UUID.fromString("00002902-0000-1000-8000-00805f9b34fb");
    public final static UUID FIRMWARE_SERVICE = UUID.fromString("0000180a-0000-1000-8000-00805f9b34fb");
    public final static UUID FIRMWARE_UUID = UUID.fromString("00002a26-0000-1000-8000-00805f9b34fb");

    public static final UUID oadService_UUID = UUID.fromString("f000ffc0-0451-4000-b000-000000000000");
    public static final UUID oadImageNotify_UUID = UUID.fromString("f000ffc1-0451-4000-b000-000000000000");
    public static final UUID oadBlockRequest_UUID = UUID.fromString("f000ffc2-0451-4000-b000-000000000000");

    public final static String EXTRA_DATA = "com.kidsdynamic.ota.EXTRA_DATA";
    public final static String EXTRA_UUID = "com.kidsdynamic.ota.EXTRA_UUID";
    public final static String EXTRA_STATUS = "com.kidsdynamic.ota.EXTRA_STATUS";
    public final static String EXTRA_ADDRESS = "com.kidsdynamic.ota.EXTRA_ADDRESS";
    public final static String ACTION_DATA_NOTIFY = "com.kidsdynamic.ota.ACTION_DATA_NOTIFY";
    public final static String ACTION_DATA_WRITE = "com.kidsdynamic.ota.ACTION_DATA_WRITE";

    public final static String ACTION_GATT_CONNECTED = "com.kidsdynamic.ota.ACTION_GATT_CONNECTED";
    public final static String ACTION_GATT_DISCONNECTED = "com.kidsdynamic.ota.ACTION_GATT_DISCONNECTED";
    public final static String ACTION_GATT_SERVICES_DISCOVERED = "com.kidsdynamic.ota.ACTION_GATT_SERVICES_DISCOVERED";
    public final static String ACTION_DATA_READ = "com.kidsdynamic.ota.ACTION_DATA_READ";

    public static final short OAD_CONN_INTERVAL = 25; // 15 milliseconds
    public static final short OAD_SUPERVISION_TIMEOUT = 500; // 500 milliseconds
    public static final int GATT_WRITE_TIMEOUT = 300; // Milliseconds

    public final static int BLUETOOTH_PERMISSION = 0x1000;
    public final static int BLUETOOTH_ADMIN_PERMISSION = 0x1001;
    public final static int PERMISSION_REQUEST_COARSE_LOCATION = 1;
    public final static int BLUETOOTH_PRIVILEGED_PERMISSION = 1;
    public final static int REQUEST_ENABLE_BT = 1;


    // Programming parameters

    public static final int FILE_BUFFER_SIZE = 0x40000;
    public static final String FW_CUSTOM_DIRECTORY = Environment.DIRECTORY_DOWNLOADS;
    public static final String FW_FILE_A = "a_022317.bin";
    public static final String FW_FILE_B = "b_022317.bin";

    public static final int OAD_BLOCK_SIZE = 16;
    public static final int HAL_FLASH_WORD_SIZE = 4;
    public static final int OAD_BUFFER_SIZE = 2 + OAD_BLOCK_SIZE;
    public static final int OAD_IMG_HDR_SIZE = 8;
    public static final long TIMER_INTERVAL = 1000;
    public static final int SEND_INTERVAL = 20; // Milliseconds (make sure this is longer than the connection interval)
    public static final int BLOCKS_PER_CONNECTION = 1; // May sent up to four blocks per connection

}
