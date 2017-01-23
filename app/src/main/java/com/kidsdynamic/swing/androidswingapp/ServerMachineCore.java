package com.kidsdynamic.swing.androidswingapp;

import android.content.Context;

import com.android.volley.Request;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by weichigio on 2017/1/24.
 */

public class ServerMachineCore extends ServerMachine {
    public ServerMachineCore(Context context) {
        super(context);
    }

    Runnable responser = new Runnable() {
        @Override
        public void run() {
            if (mResponse != null) {
                mResponse.onResponse(true, 200, "");
            }
        }
    };

    ResponseListener mResponse = null;

    @Override
    public void userLogin(ResponseListener response, String email, String password) {
        mHandler.postDelayed(responser, 1000);
    }

    @Override
    public void userRegister(ResponseListener response, String email, String password, String firstName, String lastName, String phoneNumber, String zipCode) {
    }

    @Override
    public void userIsTokenValid(ResponseListener response, String email, String token) {
    }

    @Override
    public void userIsMailAvailableToRegister(ResponseListener response, String email) {
    }

    @Override
    public void userUpdateProfile(ResponseListener response, String firstName, String lastName, String phoneNumber, String zipCode) {
    }

    @Override
    public void userRetrieveUserProfile(ResponseListener response) {
    }

    @Override
    public void userAvatarUpload(ResponseListener response, String filePath) {
    }

    @Override
    public void userAvatarUploadKid(ResponseListener response, String kidId, String filePath) {
    }

    @Override
    public void kidsAdd(ResponseListener response, String firstName, String lastName, String macId) {
    }

    @Override
    public void kidsUpdate(ResponseListener response, String kidId, String firstName, String lastName) {
    }

    @Override
    public void kidsWhoRegisteredMacID(ResponseListener response, String macId) {
    }

    @Override
    public void activityUploadRawData(ResponseListener response, String indoorActivity, String outdoorActivity, String time, String macId) {
    }

    @Override
    public void activityRetrieveData(ResponseListener response, String kidId, String period) {
    }

    @Override
    public void eventAdd(ResponseListener response, int kidId, String name, String startDate, String endDate,
                         String color, String description, int alert, String city, String state, String repeat,
                         int timezoneOffset, List<String> todo) {
    }

    @Override
    public void eventUpdate(ResponseListener response, int eventId, String name, String startDate, String endDate,
                            String color, String description, int alert, String city, String state, String repeat,
                            int timezoneOffset, List<String> todo) {
    }

    @Override
    public void eventDelete(ResponseListener response, String eventId) {
    }

    @Override
    public void eventRetrieveEvents(ResponseListener response, String period, String date) {
    }

    @Override
    public void eventRetrieveAllEventsWithTodo(ResponseListener response) {
    }

    @Override
    public void subhostAdd(ResponseListener response, int hostId) {
    }

    @Override
    public void subhostAccept(ResponseListener response, int subHostId, List<Integer> KidId) {
    }

    @Override
    public void subhostDeny(ResponseListener response, int subHostId) {
    }

    @Override
    public void subhostList(ResponseListener response, String status) {
    }

}
