package com.kidsdynamic.swing.androidswingapp;

import java.util.ArrayList;

/**
 * Created by 03543 on 2017/1/23.
 */

public class WatchOperator {
    private ActivityMain mActivityMain;

    public ArrayList<WatchContact.Device> mListDevice;
    public ArrayList<WatchContact.Device> mListShared;
    public ArrayList<WatchContact.Person> mListRequest;

    public WatchOperator(ActivityMain activiey) {
        mActivityMain = activiey;

        mListDevice = new ArrayList<>();
        mListShared = new ArrayList<>();
        mListRequest = new ArrayList<>();
    }

    public ArrayList<WatchContact.Device> getDeviceList() {
        return mListDevice;
    }

    public ArrayList<WatchContact.Device> getSharedList() {
        return mListShared;
    }

    public ArrayList<WatchContact.Person> getRequestList() {
        return mListRequest;
    }
}
