package com.kidsdynamic.swing.androidswingapp;

import android.content.Context;
import android.os.Handler;

import java.util.List;

/**
 * Created by weichigio on 2017/1/24.
 */

public class ServerMachineDebug extends ServerMachine {
    private Handler mHandler = new Handler();
    private int mExpectResponseCode = 200;
    private String mCurrentCommand;
    private Object mCurrentResponseListener;
    private String mResponseString = "";


    public ServerMachineDebug(Context context) {
        super(context);
    }

    public void setExceptResponse(int expect) {
        mExpectResponseCode = expect;
    }

    Runnable mRunnable = new Runnable() {
        @Override
        public void run() {
            switch(mCurrentCommand) {
                case CMD_USER_LOGIN:
                    if (mExpectResponseCode == 200)
                        ((userLoginListener) mCurrentResponseListener).onSuccess(mExpectResponseCode, ServerGson.user.login.fromJson(mResponseString));
                    else
                        ((userLoginListener) mCurrentResponseListener).onFail(mExpectResponseCode);
                    break;

                case CMD_USER_REGISTER:
                    if (mExpectResponseCode == 200)
                        ((userRegisterListener) mCurrentResponseListener).onSuccess(mExpectResponseCode);
                    else
                        ((userRegisterListener) mCurrentResponseListener).onFail(mExpectResponseCode, null);
                    break;

                case CMD_USER_IS_TOKEN_VALID:
                    if (mExpectResponseCode == 200)
                        ((userIsTokenValidListener) mCurrentResponseListener).onValidState(true);
                    else if (mExpectResponseCode == 403)
                        ((userIsTokenValidListener) mCurrentResponseListener).onValidState(false);
                    else
                        ((userIsTokenValidListener) mCurrentResponseListener).onFail(mExpectResponseCode);
                    break;

                case CMD_USER_IS_MAIL_AVAILABLE_TO_REGISTER:
                    if (mExpectResponseCode == 200)
                        ((userIsMailAvailableToRegisterListener) mCurrentResponseListener).onValidState(true);
                    else if (mExpectResponseCode == 409)
                        ((userIsMailAvailableToRegisterListener) mCurrentResponseListener).onValidState(false);
                    else
                        ((userIsMailAvailableToRegisterListener) mCurrentResponseListener).onFail(mExpectResponseCode);
                    break;

                case CMD_USER_UPDATE_PROFILE: // userUpdateProfileListener
                    if (mExpectResponseCode == 200)
                        ((userUpdateProfileListener) mCurrentResponseListener).onSuccess(mExpectResponseCode, ServerGson.user.updateProfile.fromJson(mResponseString));
                    else if (mExpectResponseCode == 400)
                        ((userUpdateProfileListener) mCurrentResponseListener).onFail(mExpectResponseCode, null);
                    else
                        ((userUpdateProfileListener) mCurrentResponseListener).onFail(mExpectResponseCode, null);

                    break;

                case CMD_USER_RETRIEVE_USER_PROFILE:
                    if (mExpectResponseCode == 200)
                        ((userRetrieveUserProfileListener) mCurrentResponseListener).onSuccess(mExpectResponseCode, ServerGson.user.retrieveUserProfile.fromJson(mResponseString));
                    else if (mExpectResponseCode == 400)
                        ((userRetrieveUserProfileListener) mCurrentResponseListener).onFail(mExpectResponseCode);
                    else
                        ((userRetrieveUserProfileListener) mCurrentResponseListener).onFail(mExpectResponseCode);
                    break;

                case CMD_AVATAR_UPLOAD:
                    if (mExpectResponseCode == 200)
                        ((userAvatarUploadListener) mCurrentResponseListener).onSuccess(mExpectResponseCode, ServerGson.user.avatar.upload.fromJson(mResponseString));
                    else if (mExpectResponseCode == 400)
                        ((userAvatarUploadListener) mCurrentResponseListener).onFail(mExpectResponseCode);
                    else
                        ((userAvatarUploadListener) mCurrentResponseListener).onFail(mExpectResponseCode);
                    break;

                case CMD_AVATAR_UPLOAD_KID:
                    if (mExpectResponseCode == 200)
                        ((userAvatarUploadKidListener) mCurrentResponseListener).onSuccess(mExpectResponseCode, ServerGson.user.avatar.uploadKid.fromJson(mResponseString));
                    else if (mExpectResponseCode == 400)
                        ((userAvatarUploadKidListener) mCurrentResponseListener).onFail(mExpectResponseCode);
                    else
                        ((userAvatarUploadKidListener) mCurrentResponseListener).onFail(mExpectResponseCode);
                    break;

                case CMD_KIDS_ADD:
                    if (mExpectResponseCode == 200)
                        ((kidsAddListener) mCurrentResponseListener).onSuccess(mExpectResponseCode, ServerGson.kids.add.fromJson(mResponseString));
                    else if (mExpectResponseCode == 400)
                        ((kidsAddListener) mCurrentResponseListener).onFail(mExpectResponseCode);
                    else if (mExpectResponseCode == 409)
                        ((kidsAddListener) mCurrentResponseListener).onConflict(mExpectResponseCode);
                    else
                        ((kidsAddListener) mCurrentResponseListener).onFail(mExpectResponseCode);
                    break;

                case CMD_KIDS_UPDATE:
                    if (mExpectResponseCode == 200)
                        ((kidsUpdateListener) mCurrentResponseListener).onSuccess(mExpectResponseCode, ServerGson.kids.update.fromJson(mResponseString));
                    else if (mExpectResponseCode == 400)
                        ((kidsUpdateListener) mCurrentResponseListener).onFail(mExpectResponseCode);
                    else
                        ((kidsUpdateListener) mCurrentResponseListener).onFail(mExpectResponseCode);
                    break;

                case CMD_KIDS_DELETE:
                    if (mExpectResponseCode == 200)
                        ((kidsDeleteListener) mCurrentResponseListener).onSuccess(mExpectResponseCode);
                    else if (mExpectResponseCode == 400)
                        ((kidsDeleteListener) mCurrentResponseListener).onFail(mExpectResponseCode);
                    else
                        ((kidsDeleteListener) mCurrentResponseListener).onFail(mExpectResponseCode);
                    break;

                case CMD_KIDS_WHO_REGISTERED_MAC_ID:
                    if (mExpectResponseCode == 200)
                        ((kidsWhoRegisteredMacIDListener) mCurrentResponseListener).onSuccess(mExpectResponseCode, ServerGson.kids.whoRegisteredMacID.fromJson(mResponseString));
                    else if (mExpectResponseCode == 400)
                        ((kidsWhoRegisteredMacIDListener) mCurrentResponseListener).onFail(mExpectResponseCode);
                    else if (mExpectResponseCode == 404)
                        ((kidsWhoRegisteredMacIDListener) mCurrentResponseListener).onNotRegistered(mExpectResponseCode);
                    else
                        ((kidsWhoRegisteredMacIDListener) mCurrentResponseListener).onFail(mExpectResponseCode);
                    break;

                case CMD_ACTIVITY_UPLOAD_RAW_DATA:
                    if (mExpectResponseCode == 200)
                        ((activityUploadRawDataListener) mCurrentResponseListener).onSuccess(mExpectResponseCode);
                    else if (mExpectResponseCode == 400)
                        ((activityUploadRawDataListener) mCurrentResponseListener).onFail(mExpectResponseCode);
                    else if (mExpectResponseCode == 409)
                        ((activityUploadRawDataListener) mCurrentResponseListener).onConflict(mExpectResponseCode);
                    else
                        ((activityUploadRawDataListener) mCurrentResponseListener).onFail(mExpectResponseCode);
                    break;

                case CMD_ACTIVITY_RETRIEVE_DATA:
                    if (mExpectResponseCode == 200)
                        ((activityRetrieveDataListener) mCurrentResponseListener).onSuccess(mExpectResponseCode, ServerGson.activity.retrieveData.fromJson(mResponseString));
                    else if (mExpectResponseCode == 400)
                        ((activityRetrieveDataListener) mCurrentResponseListener).onFail(mExpectResponseCode);
                    else
                        ((activityRetrieveDataListener) mCurrentResponseListener).onFail(mExpectResponseCode);
                    break;

                case CMD_ACTIVITY_RETRIEVE_DATA_BY_TIME:
                    if (mExpectResponseCode == 200)
                        ((activityRetrieveDataByTimeListener) mCurrentResponseListener).onSuccess(mExpectResponseCode, ServerGson.activity.retrieveDataByTime.fromJson(mResponseString));
                    else if (mExpectResponseCode == 400)
                        ((activityRetrieveDataByTimeListener) mCurrentResponseListener).onFail(mExpectResponseCode);
                    else
                        ((activityRetrieveDataByTimeListener) mCurrentResponseListener).onFail(mExpectResponseCode);
                    break;

                case CMD_EVENT_ADD:
                    if (mExpectResponseCode == 200)
                        ((eventAddListener) mCurrentResponseListener).onSuccess(mExpectResponseCode, ServerGson.event.add.fromJson(mResponseString));
                    else if (mExpectResponseCode == 400)
                        ((eventAddListener) mCurrentResponseListener).onFail(mExpectResponseCode);
                    else
                        ((eventAddListener) mCurrentResponseListener).onFail(mExpectResponseCode);
                    break;

                case CMD_EVENT_UPDATE:
                    if (mExpectResponseCode == 200)
                        ((eventUpdateListener) mCurrentResponseListener).onSuccess(mExpectResponseCode, ServerGson.event.update.fromJson(mResponseString));
                    else if (mExpectResponseCode == 400)
                        ((eventUpdateListener) mCurrentResponseListener).onFail(mExpectResponseCode);
                    else
                        ((eventUpdateListener) mCurrentResponseListener).onFail(mExpectResponseCode);
                    break;

                case CMD_EVENT_DELETE:
                    if (mExpectResponseCode == 200)
                        ((eventDeleteListener) mCurrentResponseListener).onSuccess(mExpectResponseCode);
                    else if (mExpectResponseCode == 400)
                        ((eventDeleteListener) mCurrentResponseListener).onFail(mExpectResponseCode);
                    else
                        ((eventDeleteListener) mCurrentResponseListener).onFail(mExpectResponseCode);
                    break;

                case CMD_EVENT_RETRIEVE_EVENTS:
                    if (mExpectResponseCode == 200)
                        ((eventRetrieveEventsListener) mCurrentResponseListener).onSuccess(mExpectResponseCode, ServerGson.event.retrieveEvents.fromJson(mResponseString));
                    else if (mExpectResponseCode == 400)
                        ((eventRetrieveEventsListener) mCurrentResponseListener).onFail(mExpectResponseCode);
                    else
                        ((eventRetrieveEventsListener) mCurrentResponseListener).onFail(mExpectResponseCode);
                    break;

                case CMD_EVENT_RETRIEVE_ALL_EVENTS_WITH_TODO:
                    if (mExpectResponseCode == 200)
                        ((eventRetrieveAllEventsWithTodoListener) mCurrentResponseListener).onSuccess(mExpectResponseCode, ServerGson.event.retrieveEventsWithTodo.fromJson(mResponseString));
                    else
                        ((eventRetrieveAllEventsWithTodoListener) mCurrentResponseListener).onFail(mExpectResponseCode);
                    break;

                case CMD_EVENT_TODO_DONE:
                    if (mExpectResponseCode == 200)
                        ((eventTodoDoneListener) mCurrentResponseListener).onSuccess(mExpectResponseCode);
                    else
                        ((eventTodoDoneListener) mCurrentResponseListener).onFail(mExpectResponseCode);
                    break;

                case CMD_SUBHOST_ADD:
                    if (mExpectResponseCode == 200)
                        ((subHostAddListener) mCurrentResponseListener).onSuccess(mExpectResponseCode, ServerGson.subHost.add.fromJson(mResponseString));
                    else if (mExpectResponseCode == 400)
                        ((subHostAddListener) mCurrentResponseListener).onFail(mExpectResponseCode);
                    else if (mExpectResponseCode == 409)
                        ((subHostAddListener) mCurrentResponseListener).onConflict(mExpectResponseCode);
                    else
                        ((subHostAddListener) mCurrentResponseListener).onFail(mExpectResponseCode);
                    break;

                case CMD_SUBHOST_ACCEPT:
                    if (mExpectResponseCode == 200)
                        ((subHostAcceptListener) mCurrentResponseListener).onSuccess(mExpectResponseCode, ServerGson.subHost.accept.fromJson(mResponseString));
                    else if (mExpectResponseCode == 400)
                        ((subHostAcceptListener) mCurrentResponseListener).onFail(mExpectResponseCode);
                    else
                        ((subHostAcceptListener) mCurrentResponseListener).onFail(mExpectResponseCode);
                    break;

                case CMD_SUBHOST_DENY:
                    if (mExpectResponseCode == 200)
                        ((subHostDenyListener) mCurrentResponseListener).onSuccess(mExpectResponseCode, ServerGson.subHost.deny.fromJson(mResponseString));
                    else if (mExpectResponseCode == 400)
                        ((subHostDenyListener) mCurrentResponseListener).onFail(mExpectResponseCode);
                    else
                        ((subHostDenyListener) mCurrentResponseListener).onFail(mExpectResponseCode);
                    break;

                case CMD_SUBHOST_LIST:
                    if (mExpectResponseCode == 200)
                        ((subHostListListener) mCurrentResponseListener).onSuccess(mExpectResponseCode, ServerGson.subHost.list.fromJson(mResponseString));
                    else
                        ((subHostListListener) mCurrentResponseListener).onFail(mExpectResponseCode);
                    break;
            }

            //mCurrentResponseListener.onResponse(true, mExpectResponseCode, responseString);
        }

    };

    @Override
    public void userLogin(userLoginListener listener, String email, String password) {
        mCurrentCommand = CMD_USER_LOGIN;
        mCurrentResponseListener = listener;
        mHandler.postDelayed(mRunnable, 1000);
        mResponseString = "{\n" +
                "  \"access_token\": \"2f69e1d858a04b4f35dd477c76921b34\",\n" +
                "  \"username\": \"teste\"\n" +
                "}";
    }

    @Override
    public void userRegister(userRegisterListener listener, String email, String password, String firstName, String lastName, String phoneNumber, String zipCode) {
        mCurrentCommand = CMD_USER_REGISTER;
        mCurrentResponseListener = listener;
        mHandler.postDelayed(mRunnable, 1000);
    }

    @Override
    public void userIsTokenValid(userIsTokenValidListener listener, String email, String token) {
        mCurrentCommand = CMD_USER_IS_TOKEN_VALID;
        mCurrentResponseListener = listener;
        mHandler.postDelayed(mRunnable, 1000);
    }

    @Override
    public void userIsMailAvailableToRegister(userIsMailAvailableToRegisterListener listener, String email) {
        mCurrentCommand = CMD_USER_IS_MAIL_AVAILABLE_TO_REGISTER;
        mCurrentResponseListener = listener;
        mHandler.postDelayed(mRunnable, 1000);
    }

    @Override
    public void userUpdateProfile(userUpdateProfileListener listener, String firstName, String lastName, String phoneNumber, String zipCode) {
        mCurrentCommand = CMD_USER_UPDATE_PROFILE;
        mCurrentResponseListener = listener;
        mHandler.postDelayed(mRunnable, 1000);
        mResponseString = "{\n" +
                "    \"id\": 29,\n" +
                "    \"email\": \"lwz1@swing.com\",\n" +
                "    \"firstName\": \"KIDLLE\",\n" +
                "    \"lastName\": \"YES\",\n" +
                "    \"lastUpdate\": \"2016-12-18T21:24:57Z\",\n" +
                "    \"dateCreated\": \"2016-12-06T00:40:10Z\",\n" +
                "    \"zipCode\": \"11111\",\n" +
                "    \"phoneNumber\": \"3444943214\",\n" +
                "    \"profile\": \"\"\n" +
                "  }";
    }

    @Override
    public void userRetrieveUserProfile(userRetrieveUserProfileListener listener) {
        mCurrentCommand = CMD_USER_RETRIEVE_USER_PROFILE;
        mCurrentResponseListener = listener;
        mHandler.postDelayed(mRunnable, 1000);
        mResponseString = "{\n" +
                "  \"kids\": [\n" +
                "    {\n" +
                "      \"id\": 18,\n" +
                "      \"firstName\": \"Jay\",\n" +
                "      \"lastName\": \"Chen\",\n" +
                "      \"dateCreated\": \"2016-12-11T22:37:15Z\",\n" +
                "      \"macId\": \"13031FCFE5E02\",\n" +
                "      \"profile\": \"\"\n" +
                "    },\n" +
                "    {\n" +
                "      \"id\": 19,\n" +
                "      \"firstName\": \"KIDLLE\",\n" +
                "      \"lastName\": \"YES\",\n" +
                "      \"dateCreated\": \"2016-12-18T04:17:35Z\",\n" +
                "      \"macId\": \"hgweorahgbkljwhnpi\",\n" +
                "      \"profile\": \"\"\n" +
                "    },\n" +
                "    {\n" +
                "      \"id\": 20,\n" +
                "      \"firstName\": \"KIDLLE\",\n" +
                "      \"lastName\": \"YES\",\n" +
                "      \"dateCreated\": \"2016-12-18T21:19:54Z\",\n" +
                "      \"macId\": \"hgweorahgbkljwhnpi2\",\n" +
                "      \"profile\": \"\"\n" +
                "    }\n" +
                "  ],\n" +
                "  \"user\": {\n" +
                "    \"id\": 29,\n" +
                "    \"email\": \"lwz1@swing.com\",\n" +
                "    \"firstName\": \"KIDLLE\",\n" +
                "    \"lastName\": \"YES\",\n" +
                "    \"lastUpdate\": \"2016-12-18T21:24:57Z\",\n" +
                "    \"dateCreated\": \"2016-12-06T00:40:10Z\",\n" +
                "    \"zipCode\": \"11111\",\n" +
                "    \"phoneNumber\": \"\",\n" +
                "    \"profile\": \"\"\n" +
                "  }\n" +
                "}";

    }

    @Override
    public void userAvatarUpload(userAvatarUploadListener listener, String filePath) {
        mCurrentCommand = CMD_AVATAR_UPLOAD;
        mCurrentResponseListener = listener;
        mHandler.postDelayed(mRunnable, 1000);
        mResponseString = "{\n" +
                "    \"id\": 29,\n" +
                "    \"email\": \"lwz1@swing.com\",\n" +
                "    \"firstName\": \"KIDLLE\",\n" +
                "    \"lastName\": \"YES\",\n" +
                "    \"lastUpdate\": \"2016-12-18T04:11:02Z\",\n" +
                "    \"dateCreated\": \"2016-12-06T00:40:10Z\",\n" +
                "    \"zipCode\": \"11111\",\n" +
                "    \"phoneNumber\": \"\",\n" +
                "    \"profile\": \"avatar_29.jpg\"\n" +
                "  }";
    }

    @Override
    public void userAvatarUploadKid(userAvatarUploadKidListener listener, String kidId, String filePath) {
        mCurrentCommand = CMD_AVATAR_UPLOAD_KID;
        mCurrentResponseListener = listener;
        mHandler.postDelayed(mRunnable, 1000);
        mResponseString = "{\n" +
                "    \"id\": 19,\n" +
                "    \"firstName\": \"KIDLLE\",\n" +
                "    \"lastName\": \"YES\",\n" +
                "    \"dateCreated\": \"2016-12-18T04:17:35Z\",\n" +
                "    \"macId\": \"\",\n" +
                "    \"profile\": \"kid_avatar_19.jpg\"\n" +
                "  }";
    }

    @Override
    public void kidsAdd(kidsAddListener listener, String name, String macId) {
        mCurrentCommand = CMD_KIDS_ADD;
        mCurrentResponseListener = listener;
        mHandler.postDelayed(mRunnable, 1000);
        mResponseString = "{\n" +
                "  \"id\": 2,\n" +
                "  \"name\": \"Kids3\",\n" +
                "  \"dateCreated\": \"2017-02-02T01:14:16Z\",\n" +
                "  \"macId\": \"Mac_ID4\",\n" +
                "  \"profile\": \"\",\n" +
                "  \"parent\": {\n" +
                "    \"id\": 2,\n" +
                "    \"email\": \"jack08300@gmail.com\",\n" +
                "    \"firstName\": \"Jay\",\n" +
                "    \"lastName\": \"Chen\",\n" +
                "    \"lastUpdate\": \"0001-01-01T00:00:00Z\",\n" +
                "    \"dateCreated\": \"0001-01-01T00:00:00Z\",\n" +
                "    \"zipCode\": \"\",\n" +
                "    \"phoneNumber\": \"\",\n" +
                "    \"profile\": \"\"\n" +
                "  }\n" +
                "}";
    }

    @Override
    public void kidsUpdate(kidsUpdateListener listener, String kidId, String firstName, String lastName) {
        mCurrentCommand = CMD_KIDS_UPDATE;
        mCurrentResponseListener = listener;
        mHandler.postDelayed(mRunnable, 1000);
        mResponseString = "{\n" +
                "  \"kid\": {\n" +
                "    \"id\": 19,\n" +
                "    \"firstName\": \"KIDLLE\",\n" +
                "    \"lastName\": \"yes\",\n" +
                "    \"dateCreated\": \"2016-12-18T04:17:35Z\",\n" +
                "    \"macId\": \"\",\n" +
                "    \"profile\": \"\"\n" +
                "  }\n" +
                "}";
    }

    @Override
    public void kidsDelete(kidsDeleteListener listener, String kidId) {
        mCurrentCommand = CMD_KIDS_DELETE;
        mCurrentResponseListener = listener;
        mHandler.postDelayed(mRunnable, 1000);
    }

    @Override
    public void kidsWhoRegisteredMacID(kidsWhoRegisteredMacIDListener listener, String macId) {
        mCurrentCommand = CMD_KIDS_WHO_REGISTERED_MAC_ID;
        mCurrentResponseListener = listener;
        mHandler.postDelayed(mRunnable, 1000);
        mResponseString = "{\n" +
                "  \"kid\": {\n" +
                "    \"id\": 20,\n" +
                "    \"firstName\": \"KIDLLE\",\n" +
                "    \"lastName\": \"YES\",\n" +
                "    \"dateCreated\": \"2016-12-21T03:24:57Z\",\n" +
                "    \"macId\": \"\",\n" +
                "    \"profile\": \"\",\n" +
                "    \"ParentID\": 29\n" +
                "  },\n" +
                "  \"user\": {\n" +
                "    \"id\": 29,\n" +
                "    \"email\": \"lwz1@swing.com\",\n" +
                "    \"firstName\": \"q\",\n" +
                "    \"lastName\": \"w\",\n" +
                "    \"lastUpdate\": \"2016-12-19T22:28:07Z\",\n" +
                "    \"dateCreated\": \"2016-12-06T00:40:10Z\",\n" +
                "    \"zipCode\": \"\",\n" +
                "    \"phoneNumber\": \"555\",\n" +
                "    \"profile\": \"\"\n" +
                "  }\n" +
                "}";
    }

    @Override
    public void activityUploadRawData(activityUploadRawDataListener listener, String indoorActivity, String outdoorActivity, int time, String macId) {
        mCurrentCommand = CMD_ACTIVITY_UPLOAD_RAW_DATA;
        mCurrentResponseListener = listener;
        mHandler.postDelayed(mRunnable, 1000);
    }

    @Override
    public void activityRetrieveData(activityRetrieveDataListener listener, String kidId, String period) {
        mCurrentCommand = CMD_ACTIVITY_RETRIEVE_DATA;
        mCurrentResponseListener = listener;
        mHandler.postDelayed(mRunnable, 1000);
        mResponseString = "{\n" +
                "  \"activities\": [\n" +
                "    {\n" +
                "      \"id\": 121,\n" +
                "      \"macId\": \"hgweorahgbkljwhnpi2\",\n" +
                "      \"kidId\": \"20\",\n" +
                "      \"type\": \"INDOOR\",\n" +
                "      \"steps\": 3298,\n" +
                "      \"distance\": 0,\n" +
                "      \"receivedDate\": \"2016-12-13T19:55:02Z\"\n" +
                "    },\n" +
                "    {\n" +
                "      \"id\": 122,\n" +
                "      \"macId\": \"hgweorahgbkljwhnpi2\",\n" +
                "      \"kidId\": \"20\",\n" +
                "      \"type\": \"OUTDOOR\",\n" +
                "      \"steps\": 42,\n" +
                "      \"distance\": 0,\n" +
                "      \"receivedDate\": \"2016-12-13T19:55:02Z\"\n" +
                "    }\n" +
                "  ]\n" +
                "}";
    }

    @Override
    public void activityRetrieveDataByTime(activityRetrieveDataByTimeListener listener, long start, long end, int kidId) {
        mCurrentCommand = CMD_ACTIVITY_RETRIEVE_DATA_BY_TIME;
        mCurrentResponseListener = listener;
        mHandler.postDelayed(mRunnable, 1000);
        mResponseString = "{\n" +
                "  \"activities\": [\n" +
                "    {\n" +
                "      \"id\": 5,\n" +
                "      \"macId\": \"hgweorahgbkljwhnpi3\",\n" +
                "      \"kidId\": \"1\",\n" +
                "      \"type\": \"INDOOR\",\n" +
                "      \"steps\": 10,\n" +
                "      \"distance\": 0,\n" +
                "      \"receivedDate\": \"2017-05-03T10:58:10Z\"\n" +
                "    },\n" +
                "    {\n" +
                "      \"id\": 6,\n" +
                "      \"macId\": \"hgweorahgbkljwhnpi3\",\n" +
                "      \"kidId\": \"1\",\n" +
                "      \"type\": \"OUTDOOR\",\n" +
                "      \"steps\": 24,\n" +
                "      \"distance\": 0,\n" +
                "      \"receivedDate\": \"2017-06-21T23:51:30Z\"\n" +
                "    }\n" +
                "  ]\n" +
                "}";
    }

    @Override
    public void eventAdd(eventAddListener listener, int kidId, String name, String startDate, String endDate,
                         String color, String description, int alert, String city, String state, String repeat,
                         int timezoneOffset, List<String> todo) {
        mCurrentCommand = CMD_EVENT_ADD;
        mCurrentResponseListener = listener;
        mHandler.postDelayed(mRunnable, 1000);
        mResponseString = "{\n" +
                "  \"event\": {\n" +
                "    \"id\": 398,\n" +
                "    \"userId\": 29,\n" +
                "    \"kidId\": 20,\n" +
                "    \"name\": \"Test event name\",\n" +
                "    \"startDate\": \"2015-08-30T08:20:00Z\",\n" +
                "    \"endDate\": \"2015-08-31T08:20:00Z\",\n" +
                "    \"color\": \"#F05D25\",\n" +
                "    \"status\": \"OPEN\",\n" +
                "    \"description\": \"Hahah\",\n" +
                "    \"alert\": 49,\n" +
                "    \"city\": \"New York\",\n" +
                "    \"state\": \"New York\",\n" +
                "    \"repeat\": \"\",\n" +
                "    \"timezoneOffset\": 300,\n" +
                "    \"dateCreated\": \"2016-12-26T06:25:19Z\",\n" +
                "    \"lastUpdated\": \"2016-12-26T06:25:19Z\",\n" +
                "    \"todo\": [\n" +
                "      {\n" +
                "        \"id\": 38,\n" +
                "        \"text\": \"test todo 1\",\n" +
                "        \"status\": \"PENDING\",\n" +
                "        \"dateCreated\": \"2016-12-26T06:25:19Z\",\n" +
                "        \"lastUpdated\": \"2016-12-26T06:25:19Z\"\n" +
                "      },\n" +
                "      {\n" +
                "        \"id\": 39,\n" +
                "        \"text\": \"test todo 2\",\n" +
                "        \"status\": \"PENDING\",\n" +
                "        \"dateCreated\": \"2016-12-26T06:25:19Z\",\n" +
                "        \"lastUpdated\": \"2016-12-26T06:25:19Z\"\n" +
                "      }\n" +
                "    ]\n" +
                "  }\n" +
                "}";
    }

    @Override
    public void eventUpdate(eventUpdateListener listener, int eventId, String name, String startDate, String endDate,
                            String color, String description, int alert, String city, String state, String repeat,
                            int timezoneOffset, List<String> todo) {
        mCurrentCommand = CMD_EVENT_UPDATE;
        mCurrentResponseListener = listener;
        mHandler.postDelayed(mRunnable, 1000);
        mResponseString = "{\n" +
                "  \"event\": {\n" +
                "    \"id\": 414,\n" +
                "    \"userId\": 29,\n" +
                "    \"kidId\": 20,\n" +
                "    \"name\": \"Test event name2\",\n" +
                "    \"startDate\": \"2015-08-30T08:20:00Z\",\n" +
                "    \"endDate\": \"2015-08-31T08:20:00Z\",\n" +
                "    \"color\": \"#F05D25\",\n" +
                "    \"status\": \"OPEN\",\n" +
                "    \"description\": \"Hahah\",\n" +
                "    \"alert\": 49,\n" +
                "    \"city\": \"New York\",\n" +
                "    \"state\": \"New York\",\n" +
                "    \"repeat\": \"\",\n" +
                "    \"timezoneOffset\": 300,\n" +
                "    \"dateCreated\": \"2016-12-26T21:21:53Z\",\n" +
                "    \"lastUpdated\": \"2016-12-27T00:59:28Z\",\n" +
                "    \"todo\": [\n" +
                "      {\n" +
                "        \"id\": 83,\n" +
                "        \"text\": \"test todo 1\",\n" +
                "        \"status\": \"PENDING\",\n" +
                "        \"dateCreated\": \"2016-12-27T00:59:28Z\",\n" +
                "        \"lastUpdated\": \"2016-12-27T00:59:28Z\"\n" +
                "      }\n" +
                "    ]\n" +
                "  }\n" +
                "}";
    }

    @Override
    public void eventDelete(eventDeleteListener listener, String eventId) {
        mCurrentCommand = CMD_EVENT_DELETE;
        mCurrentResponseListener = listener;
        mHandler.postDelayed(mRunnable, 1000);
    }

    @Override
    public void eventRetrieveEvents(eventRetrieveEventsListener listener, String period, String date) {
        mCurrentCommand = CMD_EVENT_RETRIEVE_EVENTS;
        mCurrentResponseListener = listener;
        mHandler.postDelayed(mRunnable, 1000);
        mResponseString = "{\n" +
                "  \"events\": [\n" +
                "    {\n" +
                "      \"id\": 410,\n" +
                "      \"userId\": 29,\n" +
                "      \"kidId\": 20,\n" +
                "      \"name\": \"Test event name\",\n" +
                "      \"startDate\": \"2016-12-27T21:21:53Z\",\n" +
                "      \"endDate\": \"2015-08-31T08:20:00Z\",\n" +
                "      \"color\": \"#F05D25\",\n" +
                "      \"status\": \"OPEN\",\n" +
                "      \"description\": \"Hahah\",\n" +
                "      \"alert\": 49,\n" +
                "      \"city\": \"New York\",\n" +
                "      \"state\": \"New York\",\n" +
                "      \"repeat\": \"\",\n" +
                "      \"timezoneOffset\": 300,\n" +
                "      \"dateCreated\": \"2016-12-26T21:17:30Z\",\n" +
                "      \"lastUpdated\": \"2016-12-26T21:17:30Z\",\n" +
                "      \"todo\": [\n" +
                "        {\n" +
                "          \"id\": 66,\n" +
                "          \"text\": \"test todo 1\",\n" +
                "          \"status\": \"PENDING\",\n" +
                "          \"dateCreated\": \"2016-12-26T21:17:30Z\",\n" +
                "          \"lastUpdated\": \"2016-12-26T21:17:30Z\"\n" +
                "        },\n" +
                "        {\n" +
                "          \"id\": 67,\n" +
                "          \"text\": \"test todo 2\",\n" +
                "          \"status\": \"PENDING\",\n" +
                "          \"dateCreated\": \"2016-12-26T21:17:30Z\",\n" +
                "          \"lastUpdated\": \"2016-12-26T21:17:30Z\"\n" +
                "        }\n" +
                "      ]\n" +
                "    },\n" +
                "    {\n" +
                "      \"id\": 411,\n" +
                "      \"userId\": 29,\n" +
                "      \"kidId\": 20,\n" +
                "      \"name\": \"Test event name\",\n" +
                "      \"startDate\": \"2016-12-26T21:21:53Z\",\n" +
                "      \"endDate\": \"2015-08-31T08:20:00Z\",\n" +
                "      \"color\": \"#F05D25\",\n" +
                "      \"status\": \"OPEN\",\n" +
                "      \"description\": \"Hahah\",\n" +
                "      \"alert\": 49,\n" +
                "      \"city\": \"New York\",\n" +
                "      \"state\": \"New York\",\n" +
                "      \"repeat\": \"\",\n" +
                "      \"timezoneOffset\": 300,\n" +
                "      \"dateCreated\": \"2016-12-26T21:17:35Z\",\n" +
                "      \"lastUpdated\": \"2016-12-26T21:17:35Z\",\n" +
                "      \"todo\": [\n" +
                "        {\n" +
                "          \"id\": 68,\n" +
                "          \"text\": \"test todo 1\",\n" +
                "          \"status\": \"PENDING\",\n" +
                "          \"dateCreated\": \"2016-12-26T21:17:35Z\",\n" +
                "          \"lastUpdated\": \"2016-12-26T21:17:35Z\"\n" +
                "        },\n" +
                "        {\n" +
                "          \"id\": 69,\n" +
                "          \"text\": \"test todo 2\",\n" +
                "          \"status\": \"PENDING\",\n" +
                "          \"dateCreated\": \"2016-12-26T21:17:35Z\",\n" +
                "          \"lastUpdated\": \"2016-12-26T21:17:35Z\"\n" +
                "        }\n" +
                "      ]\n" +
                "    },\n" +
                "    {\n" +
                "      \"id\": 412,\n" +
                "      \"userId\": 29,\n" +
                "      \"kidId\": 20,\n" +
                "      \"name\": \"Test event name\",\n" +
                "      \"startDate\": \"2016-12-26T21:21:53Z\",\n" +
                "      \"endDate\": \"2015-08-31T08:20:00Z\",\n" +
                "      \"color\": \"#F05D25\",\n" +
                "      \"status\": \"OPEN\",\n" +
                "      \"description\": \"Hahah\",\n" +
                "      \"alert\": 49,\n" +
                "      \"city\": \"\",\n" +
                "      \"state\": \"\",\n" +
                "      \"repeat\": \"\",\n" +
                "      \"timezoneOffset\": 300,\n" +
                "      \"dateCreated\": \"2016-12-26T21:17:45Z\",\n" +
                "      \"lastUpdated\": \"2016-12-26T21:17:45Z\",\n" +
                "      \"todo\": [\n" +
                "        {\n" +
                "          \"id\": 70,\n" +
                "          \"text\": \"test todo 1\",\n" +
                "          \"status\": \"PENDING\",\n" +
                "          \"dateCreated\": \"2016-12-26T21:17:45Z\",\n" +
                "          \"lastUpdated\": \"2016-12-26T21:17:45Z\"\n" +
                "        },\n" +
                "        {\n" +
                "          \"id\": 71,\n" +
                "          \"text\": \"test todo 2\",\n" +
                "          \"status\": \"PENDING\",\n" +
                "          \"dateCreated\": \"2016-12-26T21:17:45Z\",\n" +
                "          \"lastUpdated\": \"2016-12-26T21:17:45Z\"\n" +
                "        }\n" +
                "      ]\n" +
                "    },\n" +
                "    {\n" +
                "      \"id\": 413,\n" +
                "      \"userId\": 29,\n" +
                "      \"kidId\": 20,\n" +
                "      \"name\": \"Test event name\",\n" +
                "      \"startDate\": \"2016-12-26T21:21:53Z\",\n" +
                "      \"endDate\": \"2015-08-31T08:20:00Z\",\n" +
                "      \"color\": \"#F05D25\",\n" +
                "      \"status\": \"OPEN\",\n" +
                "      \"description\": \"Hahah\",\n" +
                "      \"alert\": 49,\n" +
                "      \"city\": \"New York\",\n" +
                "      \"state\": \"New York\",\n" +
                "      \"repeat\": \"\",\n" +
                "      \"timezoneOffset\": 300,\n" +
                "      \"dateCreated\": \"2016-12-26T21:19:16Z\",\n" +
                "      \"lastUpdated\": \"2016-12-26T21:19:16Z\",\n" +
                "      \"todo\": [\n" +
                "        {\n" +
                "          \"id\": 72,\n" +
                "          \"text\": \"test todo 1\",\n" +
                "          \"status\": \"PENDING\",\n" +
                "          \"dateCreated\": \"2016-12-26T21:19:16Z\",\n" +
                "          \"lastUpdated\": \"2016-12-26T21:19:16Z\"\n" +
                "        },\n" +
                "        {\n" +
                "          \"id\": 73,\n" +
                "          \"text\": \"test todo 2\",\n" +
                "          \"status\": \"PENDING\",\n" +
                "          \"dateCreated\": \"2016-12-26T21:19:16Z\",\n" +
                "          \"lastUpdated\": \"2016-12-26T21:19:16Z\"\n" +
                "        }\n" +
                "      ]\n" +
                "    }\n" +
                "  ]\n" +
                "}";
    }

    @Override
    public void eventRetrieveAllEventsWithTodo(eventRetrieveAllEventsWithTodoListener listener) {
        mCurrentCommand = CMD_EVENT_RETRIEVE_ALL_EVENTS_WITH_TODO;
        mCurrentResponseListener = listener;
        mHandler.postDelayed(mRunnable, 1000);
        mResponseString = "{\n" +
                "  \"events\": [\n" +
                "    {\n" +
                "      \"id\": 410,\n" +
                "      \"userId\": 29,\n" +
                "      \"kidId\": 20,\n" +
                "      \"name\": \"Test event name\",\n" +
                "      \"startDate\": \"2016-12-27T21:21:53Z\",\n" +
                "      \"endDate\": \"2015-08-31T08:20:00Z\",\n" +
                "      \"color\": \"#F05D25\",\n" +
                "      \"status\": \"OPEN\",\n" +
                "      \"description\": \"Hahah\",\n" +
                "      \"alert\": 49,\n" +
                "      \"city\": \"New York\",\n" +
                "      \"state\": \"New York\",\n" +
                "      \"repeat\": \"\",\n" +
                "      \"timezoneOffset\": 300,\n" +
                "      \"dateCreated\": \"2016-12-26T21:17:30Z\",\n" +
                "      \"lastUpdated\": \"2016-12-26T21:17:30Z\",\n" +
                "      \"todo\": [\n" +
                "        {\n" +
                "          \"id\": 66,\n" +
                "          \"text\": \"test todo 1\",\n" +
                "          \"status\": \"PENDING\",\n" +
                "          \"dateCreated\": \"2016-12-26T21:17:30Z\",\n" +
                "          \"lastUpdated\": \"2016-12-26T21:17:30Z\"\n" +
                "        },\n" +
                "        {\n" +
                "          \"id\": 67,\n" +
                "          \"text\": \"test todo 2\",\n" +
                "          \"status\": \"PENDING\",\n" +
                "          \"dateCreated\": \"2016-12-26T21:17:30Z\",\n" +
                "          \"lastUpdated\": \"2016-12-26T21:17:30Z\"\n" +
                "        }\n" +
                "      ]\n" +
                "    },\n" +
                "    {\n" +
                "      \"id\": 411,\n" +
                "      \"userId\": 29,\n" +
                "      \"kidId\": 20,\n" +
                "      \"name\": \"Test event name\",\n" +
                "      \"startDate\": \"2016-12-26T21:21:53Z\",\n" +
                "      \"endDate\": \"2015-08-31T08:20:00Z\",\n" +
                "      \"color\": \"#F05D25\",\n" +
                "      \"status\": \"OPEN\",\n" +
                "      \"description\": \"Hahah\",\n" +
                "      \"alert\": 49,\n" +
                "      \"city\": \"New York\",\n" +
                "      \"state\": \"New York\",\n" +
                "      \"repeat\": \"\",\n" +
                "      \"timezoneOffset\": 300,\n" +
                "      \"dateCreated\": \"2016-12-26T21:17:35Z\",\n" +
                "      \"lastUpdated\": \"2016-12-26T21:17:35Z\",\n" +
                "      \"todo\": [\n" +
                "        {\n" +
                "          \"id\": 68,\n" +
                "          \"text\": \"test todo 1\",\n" +
                "          \"status\": \"PENDING\",\n" +
                "          \"dateCreated\": \"2016-12-26T21:17:35Z\",\n" +
                "          \"lastUpdated\": \"2016-12-26T21:17:35Z\"\n" +
                "        },\n" +
                "        {\n" +
                "          \"id\": 69,\n" +
                "          \"text\": \"test todo 2\",\n" +
                "          \"status\": \"PENDING\",\n" +
                "          \"dateCreated\": \"2016-12-26T21:17:35Z\",\n" +
                "          \"lastUpdated\": \"2016-12-26T21:17:35Z\"\n" +
                "        }\n" +
                "      ]\n" +
                "    },\n" +
                "    {\n" +
                "      \"id\": 412,\n" +
                "      \"userId\": 29,\n" +
                "      \"kidId\": 20,\n" +
                "      \"name\": \"Test event name\",\n" +
                "      \"startDate\": \"2016-12-26T21:21:53Z\",\n" +
                "      \"endDate\": \"2015-08-31T08:20:00Z\",\n" +
                "      \"color\": \"#F05D25\",\n" +
                "      \"status\": \"OPEN\",\n" +
                "      \"description\": \"Hahah\",\n" +
                "      \"alert\": 49,\n" +
                "      \"city\": \"\",\n" +
                "      \"state\": \"\",\n" +
                "      \"repeat\": \"\",\n" +
                "      \"timezoneOffset\": 300,\n" +
                "      \"dateCreated\": \"2016-12-26T21:17:45Z\",\n" +
                "      \"lastUpdated\": \"2016-12-26T21:17:45Z\",\n" +
                "      \"todo\": [\n" +
                "        {\n" +
                "          \"id\": 70,\n" +
                "          \"text\": \"test todo 1\",\n" +
                "          \"status\": \"PENDING\",\n" +
                "          \"dateCreated\": \"2016-12-26T21:17:45Z\",\n" +
                "          \"lastUpdated\": \"2016-12-26T21:17:45Z\"\n" +
                "        },\n" +
                "        {\n" +
                "          \"id\": 71,\n" +
                "          \"text\": \"test todo 2\",\n" +
                "          \"status\": \"PENDING\",\n" +
                "          \"dateCreated\": \"2016-12-26T21:17:45Z\",\n" +
                "          \"lastUpdated\": \"2016-12-26T21:17:45Z\"\n" +
                "        }\n" +
                "      ]\n" +
                "    },\n" +
                "    {\n" +
                "      \"id\": 413,\n" +
                "      \"userId\": 29,\n" +
                "      \"kidId\": 20,\n" +
                "      \"name\": \"Test event name\",\n" +
                "      \"startDate\": \"2016-12-26T21:21:53Z\",\n" +
                "      \"endDate\": \"2015-08-31T08:20:00Z\",\n" +
                "      \"color\": \"#F05D25\",\n" +
                "      \"status\": \"OPEN\",\n" +
                "      \"description\": \"Hahah\",\n" +
                "      \"alert\": 49,\n" +
                "      \"city\": \"New York\",\n" +
                "      \"state\": \"New York\",\n" +
                "      \"repeat\": \"\",\n" +
                "      \"timezoneOffset\": 300,\n" +
                "      \"dateCreated\": \"2016-12-26T21:19:16Z\",\n" +
                "      \"lastUpdated\": \"2016-12-26T21:19:16Z\",\n" +
                "      \"todo\": [\n" +
                "        {\n" +
                "          \"id\": 72,\n" +
                "          \"text\": \"test todo 1\",\n" +
                "          \"status\": \"PENDING\",\n" +
                "          \"dateCreated\": \"2016-12-26T21:19:16Z\",\n" +
                "          \"lastUpdated\": \"2016-12-26T21:19:16Z\"\n" +
                "        },\n" +
                "        {\n" +
                "          \"id\": 73,\n" +
                "          \"text\": \"test todo 2\",\n" +
                "          \"status\": \"PENDING\",\n" +
                "          \"dateCreated\": \"2016-12-26T21:19:16Z\",\n" +
                "          \"lastUpdated\": \"2016-12-26T21:19:16Z\"\n" +
                "        }\n" +
                "      ]\n" +
                "    }\n" +
                "  ]\n" +
                "}";
    }

    @Override
    public void eventTodoDone(eventTodoDoneListener listener, int eventId, int todoId) {
        mCurrentCommand = CMD_EVENT_TODO_DONE;
        mCurrentResponseListener = listener;
        mHandler.postDelayed(mRunnable, 1000);
    }

    @Override
    public void subHostAdd(subHostAddListener listener, int hostId) {
        mCurrentCommand = CMD_SUBHOST_ADD;
        mCurrentResponseListener = listener;
        mHandler.postDelayed(mRunnable, 1000);
        mResponseString = "{\n" +
                "  \"id\": 2,\n" +
                "  \"macId\": \"\",\n" +
                "  \"requestFromID\": 29,\n" +
                "  \"requestToID\": 35,\n" +
                "  \"status\": \"PENDING\",\n" +
                "  \"createdDate\": \"2017-01-09T01:47:16Z\",\n" +
                "  \"lastUpdated\": \"2017-01-09T01:47:16Z\",\n" +
                "  \"kid\": null\n" +
                "}";
    }

    @Override
    public void subHostAccept(subHostAcceptListener listener, int subHostId, List<Integer> KidId) {
        mCurrentCommand = CMD_SUBHOST_ACCEPT;
        mCurrentResponseListener = listener;
        mHandler.postDelayed(mRunnable, 1000);
        mResponseString = "{\n" +
                "  \"id\": 2,\n" +
                "  \"macId\": \"\",\n" +
                "  \"requestFromID\": 29,\n" +
                "  \"requestToID\": 35,\n" +
                "  \"status\": \"ACCEPTED\",\n" +
                "  \"createdDate\": \"2017-01-09T01:47:16Z\",\n" +
                "  \"lastUpdated\": \"2017-01-09T01:49:28Z\",\n" +
                "  \"kid\": [\n" +
                "    {\n" +
                "      \"id\": 22,\n" +
                "      \"firstName\": \"KIDLLE123\",\n" +
                "      \"lastName\": \"YES_NO\",\n" +
                "      \"dateCreated\": \"2016-12-27T02:06:01Z\",\n" +
                "      \"macId\": \"\",\n" +
                "      \"profile\": \"\",\n" +
                "      \"ParentID\": 29\n" +
                "    }\n" +
                "  ]\n" +
                "}";
    }

    @Override
    public void subHostDeny(subHostDenyListener listener, int subHostId) {
        mCurrentCommand = CMD_SUBHOST_DENY;
        mCurrentResponseListener = listener;
        mHandler.postDelayed(mRunnable, 1000);
        mResponseString = "{\n" +
                "  \"id\": 2,\n" +
                "  \"macId\": \"\",\n" +
                "  \"requestFromID\": 29,\n" +
                "  \"requestToID\": 35,\n" +
                "  \"status\": \"DENIED\",\n" +
                "  \"createdDate\": \"2017-01-09T01:47:16Z\",\n" +
                "  \"lastUpdated\": \"2017-01-09T02:02:05Z\",\n" +
                "  \"kid\": [\n" +
                "    {\n" +
                "      \"id\": 22,\n" +
                "      \"firstName\": \"KIDLLE123\",\n" +
                "      \"lastName\": \"YES_NO\",\n" +
                "      \"dateCreated\": \"2016-12-27T02:06:01Z\",\n" +
                "      \"macId\": \"\",\n" +
                "      \"profile\": \"\",\n" +
                "      \"ParentID\": 29\n" +
                "    }\n" +
                "  ]\n" +
                "}";
    }

    @Override
    public void subHostList(subHostListListener listener, String status) {
        mCurrentCommand = CMD_SUBHOST_LIST;
        mCurrentResponseListener = listener;
        mHandler.postDelayed(mRunnable, 1000);
        mResponseString = "[\n" +
                "  {\n" +
                "    \"id\": 2,\n" +
                "    \"macId\": \"\",\n" +
                "    \"requestFromID\": 29,\n" +
                "    \"requestToID\": 35,\n" +
                "    \"status\": \"DENIED\",\n" +
                "    \"createdDate\": \"2017-01-09T01:47:16Z\",\n" +
                "    \"lastUpdated\": \"2017-01-09T02:02:05Z\",\n" +
                "    \"kid\": [\n" +
                "      {\n" +
                "        \"id\": 22,\n" +
                "        \"firstName\": \"KIDLLE123\",\n" +
                "        \"lastName\": \"YES_NO\",\n" +
                "        \"dateCreated\": \"2016-12-27T02:06:01Z\",\n" +
                "        \"macId\": \"\",\n" +
                "        \"profile\": \"\",\n" +
                "        \"ParentID\": 29\n" +
                "      }\n" +
                "    ]\n" +
                "  }\n" +
                "]";
    }

}
