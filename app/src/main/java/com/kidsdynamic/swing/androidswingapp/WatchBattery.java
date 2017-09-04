package com.kidsdynamic.swing.androidswingapp;

import java.util.Calendar;

/**
 * Created by yen-chiehchen on 8/24/17.
 */

public class WatchBattery {
    int batteryLife;
    String macId;
    int dateReceived;
    private int mOffset = 0;

    WatchBattery() {

    }

    WatchBattery(int batteryLife, String macId, byte[] time) {
        this.batteryLife = batteryLife;
        this.macId = macId;

        Calendar now = Calendar.getInstance();
        mOffset = (now.getTimeZone().getOffset(now.getTimeInMillis()))/1000;

        this.dateReceived = (byteToDec(time[0], time[1], time[2], time[3]) - mOffset);
    }

    @Override
    public String toString() {
        return "Battery Life: " + batteryLife + "\nMacID: " + macId + "\nTime: " + dateReceived;
    }

    private int byteToDec(byte b0, byte b1, byte b2, byte b3) {
        int dec;

        dec = b0 & 0xFF;
        dec |= (b1 << 8) & 0xFF00;
        dec |= (b2 << 16) & 0xFF0000;
        dec |= (b3 << 24) & 0xFF000000;

        return dec;
    }
}
