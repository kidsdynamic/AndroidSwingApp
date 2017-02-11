package com.kidsdynamic.swing.androidswingapp;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Environment;
import android.os.Handler;
import android.util.Log;

import com.android.volley.NetworkResponse;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.HttpHeaderParser;
import com.android.volley.toolbox.Volley;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;

/**
 * Created by weichigio on 2017/1/17.
 */

public class ServerMachine {
    private final static String SERVER_ADDRESS = "https://childrenlab.com/v1";

    final static String CMD_USER_LOGIN = SERVER_ADDRESS + "/user/login";
    final static String CMD_USER_REGISTER = SERVER_ADDRESS + "/user/register";
    final static String CMD_USER_IS_TOKEN_VALID = SERVER_ADDRESS + "/user/isTokenValid";
    final static String CMD_USER_IS_MAIL_AVAILABLE_TO_REGISTER = SERVER_ADDRESS + "/user/isEmailAvailableToRegister";
    final static String CMD_USER_UPDATE_PROFILE = SERVER_ADDRESS + "/user/updateProfile";
    final static String CMD_USER_RETRIEVE_USER_PROFILE = SERVER_ADDRESS + "/user/retrieveUserProfile";
    final static String CMD_USER_FIND_BY_EMAIL = SERVER_ADDRESS + "/user/findByEmail";

    final static String CMD_AVATAR_UPLOAD = SERVER_ADDRESS + "/user/avatar/upload";
    final static String CMD_AVATAR_UPLOAD_KID = SERVER_ADDRESS + "/user/avatar/uploadKid";

    final static String CMD_KIDS_ADD = SERVER_ADDRESS + "/kids/add";
    final static String CMD_KIDS_UPDATE = SERVER_ADDRESS + "/kids/update";
    final static String CMD_KIDS_DELETE = SERVER_ADDRESS + "/kids/delete";
    final static String CMD_KIDS_WHO_REGISTERED_MAC_ID = SERVER_ADDRESS + "/kids/whoRegisteredMacID";

    final static String CMD_ACTIVITY_UPLOAD_RAW_DATA = SERVER_ADDRESS + "/activity/uploadRawData";
    final static String CMD_ACTIVITY_RETRIEVE_DATA = SERVER_ADDRESS + "/activity/retrieveData";
    final static String CMD_ACTIVITY_RETRIEVE_DATA_BY_TIME = SERVER_ADDRESS + "/activity/retrieveDataByTime";

    final static String CMD_EVENT_ADD = SERVER_ADDRESS + "/event/add";
    final static String CMD_EVENT_UPDATE = SERVER_ADDRESS + "/event/update";
    final static String CMD_EVENT_DELETE = SERVER_ADDRESS + "/event/delete";
    final static String CMD_EVENT_RETRIEVE_EVENTS = SERVER_ADDRESS + "/event/retrieveEvents";
    final static String CMD_EVENT_RETRIEVE_ALL_EVENTS_WITH_TODO = SERVER_ADDRESS + "/event/retrieveAllEventsWithTodo";
    final static String CMD_EVENT_RETRIEVE_ALL_EVENTS_BY_KID = SERVER_ADDRESS + "/event/retrieveAllEventsByKid";

    final static String CMD_EVENT_TODO_DONE = SERVER_ADDRESS + "/event/todo/done";

    final static String CMD_SUBHOST_ADD = SERVER_ADDRESS + "/subHost/add";
    final static String CMD_SUBHOST_ACCEPT = SERVER_ADDRESS + "/subHost/accept";
    final static String CMD_SUBHOST_DENY = SERVER_ADDRESS + "/subHost/deny";
    final static String CMD_SUBHOST_LIST = SERVER_ADDRESS + "/subHost/list";

    final static String CMD_GET_AVATAR = "https://s3.amazonaws.com/childrenlab/userProfile/";

    final static String REQUEST_TAG = "SERVER_MACHINE";
    final static String REQUEST_UPLOAD_TAG = "REQUEST_UPLOAD_TAG";

    private String mTag = "";
    private RequestQueue mRequestQueue;
    Queue<TaskItem> mTaskQueue = new ConcurrentLinkedQueue<>();
    private Handler mHandler = new Handler();
    private Context mContext;
    private String mAuthToken = null;

    private void Log(String msg) {
        Log.i("ServerMachine", msg);
    }

    public ServerMachine(Context context, String tag) {
        mContext = context;
        mRequestQueue = Volley.newRequestQueue(context.getApplicationContext());
        mTag = tag;
    }

    boolean Start() {
        mState = 0;
        mTaskQueue.clear();
        mHandler.postDelayed(stateMachine, 100);
        return true;
    }

    boolean Stop() {
        mRequestQueue.cancelAll(REQUEST_TAG);
        mHandler.removeCallbacks(stateMachine);
        return true;
    }

    private int mState;
    private TaskItem mCurrentTask;

    private Runnable stateMachine = new Runnable() {
        @Override
        public void run() {
            switch (mState) {
                case 0:
                    if (!mTaskQueue.isEmpty()) {
                        mCurrentTask = mTaskQueue.poll();
                        mState = 1;
                        mRequestQueue.add(mCurrentTask.mRequest);
                    }
                    break;
                case 1:
                    if (mCurrentTask == null) {
                        mState = 0;
                    }
                    break;
            }

            mHandler.postDelayed(this, 100);
        }
    };

    private Request<NetworkResponse> NewRequest(int method, String address, Map<String, String> map, String filename) {
        Request<NetworkResponse> newRequest = new ServerRequest(mContext, method, address, mSuccessListener, mErrorListener, map, filename, mAuthToken);
        newRequest.setTag(REQUEST_TAG);
        return newRequest;
    }

    public void setAuthToken(String token) {
        mAuthToken = token;
    }

    public String getAuthToken() {
        return mAuthToken;
    }

    public interface userLoginListener {
        void onSuccess(int statusCode, ServerGson.user.login.response result);

        void onFail(int statusCode);
    }

    public void userLogin(userLoginListener listener, String email, String password) {
        Map<String, String> map = new HashMap<>();
        map.put("json", ServerGson.user.login.toJson(email, password));
        mTaskQueue.add(new TaskItem(NewRequest(Request.Method.POST, CMD_USER_LOGIN, map, null), CMD_USER_LOGIN, listener));
    }

    public interface userRegisterListener {
        void onSuccess(int statusCode);

        void onFail(int statusCode, ServerGson.error.e1 error);
    }

    public void userRegister(userRegisterListener listener, String email, String password, String firstName, String lastName, String phoneNumber, String zipCode) {
        Map<String, String> map = new HashMap<>();
        map.put("json", ServerGson.user.register.toJson(email, password, firstName, lastName, phoneNumber, zipCode));
        mTaskQueue.add(new TaskItem(NewRequest(Request.Method.POST, CMD_USER_REGISTER, map, null), CMD_USER_REGISTER, listener));
    }

    public interface userIsTokenValidListener {
        void onValidState(boolean valid);

        void onFail(int statusCode);
    }

    public void userIsTokenValid(userIsTokenValidListener listener, String email, String token) {
        Map<String, String> map = new HashMap<>();
        String addressForGet = CMD_USER_IS_TOKEN_VALID + "?";
        addressForGet += "email=" + email;
        addressForGet += "&token=" + token;

//        map.put("json", ServerGson.user.isTokenValid.toJson(email, token));
        mTaskQueue.add(new TaskItem(NewRequest(Request.Method.GET, addressForGet, map, null), CMD_USER_IS_TOKEN_VALID, listener));
    }

    public interface userIsMailAvailableToRegisterListener {
        void onValidState(boolean valid);

        void onFail(int statusCode);
    }

    public void userIsMailAvailableToRegister(userIsMailAvailableToRegisterListener listener, String email) {
        Map<String, String> map = new HashMap<>();
        String addressForGet = CMD_USER_IS_MAIL_AVAILABLE_TO_REGISTER + "?";
        addressForGet += "email=" + email;
        mTaskQueue.add(new TaskItem(NewRequest(Request.Method.GET, addressForGet, map, null), CMD_USER_IS_MAIL_AVAILABLE_TO_REGISTER, listener));
    }

    public interface userUpdateProfileListener {
        void onSuccess(int statusCode, ServerGson.userData response);

        void onFail(int statusCode, ServerGson.error.e1 error);
    }

    public void userUpdateProfile(userUpdateProfileListener listener, String firstName, String lastName, String phoneNumber, String zipCode) {
        Map<String, String> map = new HashMap<>();
        map.put("json", ServerGson.user.updateProfile.toJson(firstName, lastName, phoneNumber, zipCode));
        mTaskQueue.add(new TaskItem(NewRequest(Request.Method.PUT, CMD_USER_UPDATE_PROFILE, map, null), CMD_USER_UPDATE_PROFILE, listener));
    }

    public interface userRetrieveUserProfileListener {
        void onSuccess(int statusCode, ServerGson.user.retrieveUserProfile.response response);

        void onFail(int statusCode);
    }

    public void userRetrieveUserProfile(userRetrieveUserProfileListener listener) {
        Map<String, String> map = new HashMap<>();
        mTaskQueue.add(new TaskItem(NewRequest(Request.Method.GET, CMD_USER_RETRIEVE_USER_PROFILE, map, null), CMD_USER_RETRIEVE_USER_PROFILE, listener));
    }

    public interface userFindByEmailListener {
        void onSuccess(int statusCode, ServerGson.userData response);

        void onFail(int statusCode);
    }

    public void userFindByEmail(userFindByEmailListener listener, String email) {
        Map<String, String> map = new HashMap<>();
        String addressForGet = CMD_USER_FIND_BY_EMAIL + "?";
        addressForGet += "email=" + email;
        mTaskQueue.add(new TaskItem(NewRequest(Request.Method.GET, addressForGet, map, null), CMD_USER_FIND_BY_EMAIL, listener));
    }

    public interface userAvatarUploadListener {
        void onSuccess(int statusCode, ServerGson.user.avatar.upload.response response);

        void onFail(int statusCode);
    }

    public void userAvatarUpload(userAvatarUploadListener listener, String filePath) {
        Map<String, String> map = new HashMap<>();
        mTaskQueue.add(new TaskItem(NewRequest(Request.Method.POST, CMD_AVATAR_UPLOAD, map, filePath), CMD_AVATAR_UPLOAD, listener));
    }

    public interface userAvatarUploadKidListener {
        void onSuccess(int statusCode, ServerGson.user.avatar.uploadKid.response response);

        void onFail(int statusCode);
    }

    public void userAvatarUploadKid(userAvatarUploadKidListener listener, String kidId, String filePath) {
        Map<String, String> map = new HashMap<>();
        map.put("kidId", kidId);
        mTaskQueue.add(new TaskItem(NewRequest(Request.Method.POST, CMD_AVATAR_UPLOAD_KID, map, filePath), CMD_AVATAR_UPLOAD_KID, listener));
    }

    public interface kidsAddListener {
        void onSuccess(int statusCode, ServerGson.kidData response);

        void onConflict(int statusCode);

        void onFail(int statusCode);
    }

    public void kidsAdd(kidsAddListener listener, String name, String macId) {
        Map<String, String> map = new HashMap<>();
        map.put("json", ServerGson.kids.add.toJson(name, macId));
        mTaskQueue.add(new TaskItem(NewRequest(Request.Method.POST, CMD_KIDS_ADD, map, null), CMD_KIDS_ADD, listener));
    }

    public interface kidsUpdateListener {
        void onSuccess(int statusCode, ServerGson.kids.update.response response);

        void onFail(int statusCode);
    }

    public void kidsUpdate(kidsUpdateListener listener, String kidId, String firstName, String lastName) {
        Map<String, String> map = new HashMap<>();
        map.put("json", ServerGson.kids.update.toJson(kidId, firstName, lastName));
        mTaskQueue.add(new TaskItem(NewRequest(Request.Method.PUT, CMD_KIDS_UPDATE, map, null), CMD_KIDS_UPDATE, listener));
    }

    public interface kidsDeleteListener {
        void onSuccess(int statusCode);

        void onFail(int statusCode);
    }

    public void kidsDelete(kidsDeleteListener listener, String kidId) {
        Map<String, String> map = new HashMap<>();
        String addressForGet = CMD_KIDS_DELETE + "?";
        addressForGet += "kidId=" + kidId;
        mTaskQueue.add(new TaskItem(NewRequest(Request.Method.DELETE, addressForGet, map, null), CMD_KIDS_DELETE, listener));
    }

    public interface kidsWhoRegisteredMacIDListener {
        void onSuccess(int statusCode, ServerGson.kids.whoRegisteredMacID.response response);

        void onNotRegistered(int statusCode);

        void onFail(int statusCode);
    }

    public void kidsWhoRegisteredMacID(kidsWhoRegisteredMacIDListener listener, String macId) {
        Map<String, String> map = new HashMap<>();
        String addressForGet = CMD_KIDS_WHO_REGISTERED_MAC_ID + "?";
        addressForGet += "macId=" + macId;
        mTaskQueue.add(new TaskItem(NewRequest(Request.Method.GET, addressForGet, map, null), CMD_KIDS_WHO_REGISTERED_MAC_ID, listener));
    }

    public interface activityUploadRawDataListener {
        void onSuccess(int statusCode);

        void onConflict(int statusCode);

        void onFail(int statusCode);
    }

    public void activityUploadRawData(activityUploadRawDataListener listener, String indoorActivity, String outdoorActivity, int time, String macId) {
        Map<String, String> map = new HashMap<>();
        map.put("json", ServerGson.activity.uploadRawData.toJson(indoorActivity, outdoorActivity, time, macId));
        mTaskQueue.add(new TaskItem(NewRequest(Request.Method.POST, CMD_ACTIVITY_UPLOAD_RAW_DATA, map, null), CMD_ACTIVITY_UPLOAD_RAW_DATA, listener));
    }

    public interface activityRetrieveDataListener {
        void onSuccess(int statusCode, ServerGson.activity.retrieveData.response response);

        void onFail(int statusCode);
    }

    public void activityRetrieveData(activityRetrieveDataListener listener, String kidId, String period) {
        Map<String, String> map = new HashMap<>();
        String addressForGet = CMD_ACTIVITY_RETRIEVE_DATA + "?";
        addressForGet += "kidId=" + kidId;
        addressForGet += "&period=" + period;
        mTaskQueue.add(new TaskItem(NewRequest(Request.Method.GET, addressForGet, map, null), CMD_ACTIVITY_RETRIEVE_DATA, listener));
    }

    public interface activityRetrieveDataByTimeListener {
        void onSuccess(int statusCode, ServerGson.activity.retrieveDataByTime.response response);

        void onFail(int statusCode);
    }

    public void activityRetrieveDataByTime(activityRetrieveDataByTimeListener listener, long start, long end, int kidId) {
        Map<String, String> map = new HashMap<>();
        String addressForGet = CMD_ACTIVITY_RETRIEVE_DATA_BY_TIME + "?";
        addressForGet += "start=" + start;
        addressForGet += "&end=" + end;
        addressForGet += "&kidId=" + kidId;
        mTaskQueue.add(new TaskItem(NewRequest(Request.Method.GET, addressForGet, map, null), CMD_ACTIVITY_RETRIEVE_DATA_BY_TIME, listener));
    }

    public interface eventAddListener {
        void onSuccess(int statusCode, ServerGson.event.add.response response);

        void onFail(int statusCode);
    }

    public void eventAdd(eventAddListener listener, int kidId, String name, String startDate, String endDate,
                         String color, String description, int alert, String city, String state, String repeat,
                         int timezoneOffset, List<String> todo) {
        Map<String, String> map = new HashMap<>();
        map.put("json", ServerGson.event.add.toJson(kidId, name, startDate, endDate, color, description, alert, city, state, repeat, timezoneOffset, todo));
        mTaskQueue.add(new TaskItem(NewRequest(Request.Method.POST, CMD_EVENT_ADD, map, null), CMD_EVENT_ADD, listener));
    }

    public interface eventUpdateListener {
        void onSuccess(int statusCode, ServerGson.event.update.response response);

        void onFail(int statusCode);
    }

    public void eventUpdate(eventUpdateListener listener, int eventId, String name, String startDate, String endDate,
                            String color, String description, int alert, String city, String state, String repeat,
                            int timezoneOffset, List<String> todo) {
        Map<String, String> map = new HashMap<>();
        map.put("json", ServerGson.event.update.toJson(eventId, name, startDate, endDate, color, description, alert, city, state, repeat, timezoneOffset, todo));
        mTaskQueue.add(new TaskItem(NewRequest(Request.Method.PUT, CMD_EVENT_UPDATE, map, null), CMD_EVENT_UPDATE, listener));
    }

    public interface eventDeleteListener {
        void onSuccess(int statusCode);

        void onFail(int statusCode);
    }

    public void eventDelete(eventDeleteListener listener, String eventId) {
        Map<String, String> map = new HashMap<>();
        map.put("eventId", eventId);
        mTaskQueue.add(new TaskItem(NewRequest(Request.Method.DELETE, CMD_EVENT_DELETE, map, null), CMD_EVENT_DELETE, listener));
    }

    public interface eventRetrieveEventsListener {
        void onSuccess(int statusCode, ServerGson.event.retrieveEvents.response response);

        void onFail(int statusCode);
    }

    public void eventRetrieveEvents(eventRetrieveEventsListener listener, String period, String date) {
        Map<String, String> map = new HashMap<>();
        String addressForGet = CMD_EVENT_RETRIEVE_EVENTS + "?";
        addressForGet += "period=" + period;
        addressForGet += "&date=" + date;
        mTaskQueue.add(new TaskItem(NewRequest(Request.Method.GET, addressForGet, map, null), CMD_EVENT_RETRIEVE_EVENTS, listener));
    }

    public interface eventRetrieveAllEventsWithTodoListener {
        void onSuccess(int statusCode, List<ServerGson.eventData> response);

        void onFail(int statusCode);
    }

    public void eventRetrieveAllEventsWithTodo(eventRetrieveAllEventsWithTodoListener listener) {
        Map<String, String> map = new HashMap<>();
        mTaskQueue.add(new TaskItem(NewRequest(Request.Method.GET, CMD_EVENT_RETRIEVE_ALL_EVENTS_WITH_TODO, map, null), CMD_EVENT_RETRIEVE_ALL_EVENTS_WITH_TODO, listener));
    }

    public interface eventRetrieveAllEventsByKidListener {
        void onSuccess(int statusCode, List<ServerGson.eventData> response);

        void onFail(int statusCode);
    }

    public void eventRetrieveAllEventsByKid(eventRetrieveAllEventsByKidListener listener, int kidId) {
        Map<String, String> map = new HashMap<>();
        String addressForGet = CMD_EVENT_RETRIEVE_EVENTS + "?";
        addressForGet += "kidId=" + kidId;

        mTaskQueue.add(new TaskItem(NewRequest(Request.Method.GET, addressForGet, map, null), CMD_EVENT_RETRIEVE_ALL_EVENTS_BY_KID, listener));
    }

    public interface eventTodoDoneListener {
        void onSuccess(int statusCode);

        void onFail(int statusCode);
    }

    public void eventTodoDone(eventTodoDoneListener listener, int eventId, int todoId) {
        Map<String, String> map = new HashMap<>();
        map.put("json", ServerGson.event.todo.done.toJson(eventId, todoId));
        mTaskQueue.add(new TaskItem(NewRequest(Request.Method.PUT, CMD_EVENT_TODO_DONE, map, null), CMD_EVENT_TODO_DONE, listener));
    }

    public interface subHostAddListener {
        void onSuccess(int statusCode, ServerGson.hostData response);

        void onConflict(int statusCode);

        void onFail(int statusCode);
    }

    public void subHostAdd(subHostAddListener listener, int hostId) {
        Map<String, String> map = new HashMap<>();
        map.put("json", ServerGson.subHost.add.toJson(hostId));
        mTaskQueue.add(new TaskItem(NewRequest(Request.Method.POST, CMD_SUBHOST_ADD, map, null), CMD_SUBHOST_ADD, listener));
    }

    public interface subHostAcceptListener {
        void onSuccess(int statusCode, ServerGson.hostData response);

        void onFail(int statusCode);
    }

    public void subHostAccept(subHostAcceptListener listener, int subHostId, List<Integer> KidId) {
        Map<String, String> map = new HashMap<>();
        map.put("json", ServerGson.subHost.accept.toJson(subHostId, KidId));
        mTaskQueue.add(new TaskItem(NewRequest(Request.Method.PUT, CMD_SUBHOST_ACCEPT, map, null), CMD_SUBHOST_ACCEPT, listener));
    }

    public interface subHostDenyListener {
        void onSuccess(int statusCode, ServerGson.hostData response);

        void onFail(int statusCode);
    }

    public void subHostDeny(subHostDenyListener listener, int subHostId) {
        Map<String, String> map = new HashMap<>();
        map.put("json", ServerGson.subHost.deny.toJson(subHostId));
        mTaskQueue.add(new TaskItem(NewRequest(Request.Method.PUT, CMD_SUBHOST_DENY, map, null), CMD_SUBHOST_DENY, listener));
    }

    public interface subHostListListener {
        void onSuccess(int statusCode, ServerGson.subHost.list.response response);

        void onFail(int statusCode);
    }

    public void subHostList(subHostListListener listener, String status) {
        Map<String, String> map = new HashMap<>();
        String addressForGet = CMD_SUBHOST_LIST + "?";
        addressForGet += "status=" + status;
        mTaskQueue.add(new TaskItem(NewRequest(Request.Method.GET, addressForGet, map, null), CMD_SUBHOST_LIST, listener));
    }

    public interface getAvatarListener {
        void onSuccess(Bitmap avatar, String filename);

        void onFail(int statusCode);
    }

    public void getAvatar(getAvatarListener listener, String filename) {
        Map<String, String> map = new HashMap<>();
        String addressForGet = CMD_GET_AVATAR + filename;
        mTaskQueue.add(new TaskItem(NewRequest(Request.Method.GET, addressForGet, map, null), CMD_GET_AVATAR, listener, filename));
    }

    Response.Listener<NetworkResponse> mSuccessListener = new Response.Listener<NetworkResponse>() {
        @Override
        public void onResponse(NetworkResponse response) {
            String responseString = "";
            Bitmap bitmap = null;
            int responseCode = 0;
            if (mCurrentTask.mCommand.equals(CMD_GET_AVATAR)) {
                BitmapFactory.Options decodeOptions = new BitmapFactory.Options();
                decodeOptions.inPreferredConfig = Bitmap.Config.RGB_565;
                bitmap = BitmapFactory.decodeByteArray(response.data, 0, response.data.length, decodeOptions);
            } else {
                try {
                    responseString = new String(
                            new String(
                                    response.data,
                                    HttpHeaderParser.parseCharset(response.headers)
                            ).getBytes("ISO-8859-1"),
                            "utf-8"
                    );
                    responseCode = response.statusCode;
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

            if (mCurrentTask.mResponseListener != null) {
                Log("Request OK. Command:" + mCurrentTask.mCommand + " code:" + responseCode);
                Log("Content: " + responseString);
                switch (mCurrentTask.mCommand) {
                    case CMD_USER_LOGIN:
                        if (responseCode == 200)
                            ((userLoginListener) mCurrentTask.mResponseListener).onSuccess(responseCode, ServerGson.user.login.fromJson(responseString));
                        else
                            ((userLoginListener) mCurrentTask.mResponseListener).onFail(responseCode);
                        break;

                    case CMD_USER_REGISTER:
                        if (responseCode == 200)
                            ((userRegisterListener) mCurrentTask.mResponseListener).onSuccess(responseCode);
                        else
                            ((userRegisterListener) mCurrentTask.mResponseListener).onFail(responseCode, ServerGson.error.E1FromJson(responseString));
                        break;

                    case CMD_USER_IS_TOKEN_VALID:
                        if (responseCode == 200)
                            ((userIsTokenValidListener) mCurrentTask.mResponseListener).onValidState(true);
                        else if (responseCode == 403)
                            ((userIsTokenValidListener) mCurrentTask.mResponseListener).onValidState(false);
                        else
                            ((userIsTokenValidListener) mCurrentTask.mResponseListener).onFail(responseCode);
                        break;

                    case CMD_USER_IS_MAIL_AVAILABLE_TO_REGISTER:
                        if (responseCode == 200)
                            ((userIsMailAvailableToRegisterListener) mCurrentTask.mResponseListener).onValidState(true);
                        else if (responseCode == 409)
                            ((userIsMailAvailableToRegisterListener) mCurrentTask.mResponseListener).onValidState(false);
                        else
                            ((userIsMailAvailableToRegisterListener) mCurrentTask.mResponseListener).onFail(responseCode);
                        break;

                    case CMD_USER_UPDATE_PROFILE: // userUpdateProfileListener
                        if (responseCode == 200)
                            ((userUpdateProfileListener) mCurrentTask.mResponseListener).onSuccess(responseCode, ServerGson.user.updateProfile.fromJson(responseString));
                        else if (responseCode == 400)
                            ((userUpdateProfileListener) mCurrentTask.mResponseListener).onFail(responseCode, ServerGson.error.E1FromJson(responseString));
                        else
                            ((userUpdateProfileListener) mCurrentTask.mResponseListener).onFail(responseCode, ServerGson.error.E1FromJson(responseString));

                        break;

                    case CMD_USER_RETRIEVE_USER_PROFILE:
                        if (responseCode == 200)
                            ((userRetrieveUserProfileListener) mCurrentTask.mResponseListener).onSuccess(responseCode, ServerGson.user.retrieveUserProfile.fromJson(responseString));
                        else if (responseCode == 400)
                            ((userRetrieveUserProfileListener) mCurrentTask.mResponseListener).onFail(responseCode);
                        else
                            ((userRetrieveUserProfileListener) mCurrentTask.mResponseListener).onFail(responseCode);
                        break;

                    case CMD_USER_FIND_BY_EMAIL:
                        if (responseCode == 200)
                            ((userFindByEmailListener) mCurrentTask.mResponseListener).onSuccess(responseCode, ServerGson.user.findByEmail.fromJson(responseString));
                        else
                            ((userFindByEmailListener) mCurrentTask.mResponseListener).onFail(responseCode);
                        break;

                    case CMD_AVATAR_UPLOAD:
                        if (responseCode == 200)
                            ((userAvatarUploadListener) mCurrentTask.mResponseListener).onSuccess(responseCode, ServerGson.user.avatar.upload.fromJson(responseString));
                        else if (responseCode == 400)
                            ((userAvatarUploadListener) mCurrentTask.mResponseListener).onFail(responseCode);
                        else
                            ((userAvatarUploadListener) mCurrentTask.mResponseListener).onFail(responseCode);
                        break;

                    case CMD_AVATAR_UPLOAD_KID:
                        if (responseCode == 200)
                            ((userAvatarUploadKidListener) mCurrentTask.mResponseListener).onSuccess(responseCode, ServerGson.user.avatar.uploadKid.fromJson(responseString));
                        else if (responseCode == 400)
                            ((userAvatarUploadKidListener) mCurrentTask.mResponseListener).onFail(responseCode);
                        else
                            ((userAvatarUploadKidListener) mCurrentTask.mResponseListener).onFail(responseCode);
                        break;

                    case CMD_KIDS_ADD:
                        if (responseCode == 200)
                            ((kidsAddListener) mCurrentTask.mResponseListener).onSuccess(responseCode, ServerGson.kids.add.fromJson(responseString));
                        else if (responseCode == 400)
                            ((kidsAddListener) mCurrentTask.mResponseListener).onFail(responseCode);
                        else if (responseCode == 409)
                            ((kidsAddListener) mCurrentTask.mResponseListener).onConflict(responseCode);
                        else
                            ((kidsAddListener) mCurrentTask.mResponseListener).onFail(responseCode);
                        break;

                    case CMD_KIDS_UPDATE:
                        if (responseCode == 200)
                            ((kidsUpdateListener) mCurrentTask.mResponseListener).onSuccess(responseCode, ServerGson.kids.update.fromJson(responseString));
                        else if (responseCode == 400)
                            ((kidsUpdateListener) mCurrentTask.mResponseListener).onFail(responseCode);
                        else
                            ((kidsUpdateListener) mCurrentTask.mResponseListener).onFail(responseCode);
                        break;

                    case CMD_KIDS_DELETE:
                        if (responseCode == 200)
                            ((kidsDeleteListener) mCurrentTask.mResponseListener).onSuccess(responseCode);
                        else if (responseCode == 400)
                            ((kidsDeleteListener) mCurrentTask.mResponseListener).onFail(responseCode);
                        else
                            ((kidsDeleteListener) mCurrentTask.mResponseListener).onFail(responseCode);
                        break;

                    case CMD_KIDS_WHO_REGISTERED_MAC_ID:
                        if (responseCode == 200)
                            ((kidsWhoRegisteredMacIDListener) mCurrentTask.mResponseListener).onSuccess(responseCode, ServerGson.kids.whoRegisteredMacID.fromJson(responseString));
                        else if (responseCode == 400)
                            ((kidsWhoRegisteredMacIDListener) mCurrentTask.mResponseListener).onFail(responseCode);
                        else
                            ((kidsWhoRegisteredMacIDListener) mCurrentTask.mResponseListener).onFail(responseCode);
                        break;

                    case CMD_ACTIVITY_UPLOAD_RAW_DATA:
                        if (responseCode == 200)
                            ((activityUploadRawDataListener) mCurrentTask.mResponseListener).onSuccess(responseCode);
                        else if (responseCode == 400)
                            ((activityUploadRawDataListener) mCurrentTask.mResponseListener).onFail(responseCode);
                        else if (responseCode == 409)
                            ((activityUploadRawDataListener) mCurrentTask.mResponseListener).onConflict(responseCode);
                        else
                            ((activityUploadRawDataListener) mCurrentTask.mResponseListener).onFail(responseCode);
                        break;

                    case CMD_ACTIVITY_RETRIEVE_DATA:
                        if (responseCode == 200)
                            ((activityRetrieveDataListener) mCurrentTask.mResponseListener).onSuccess(responseCode, ServerGson.activity.retrieveData.fromJson(responseString));
                        else if (responseCode == 400)
                            ((activityRetrieveDataListener) mCurrentTask.mResponseListener).onFail(responseCode);
                        else
                            ((activityRetrieveDataListener) mCurrentTask.mResponseListener).onFail(responseCode);
                        break;

                    case CMD_ACTIVITY_RETRIEVE_DATA_BY_TIME:
                        if (responseCode == 200)
                            ((activityRetrieveDataByTimeListener) mCurrentTask.mResponseListener).onSuccess(responseCode, ServerGson.activity.retrieveDataByTime.fromJson(responseString));
                        else if (responseCode == 400)
                            ((activityRetrieveDataByTimeListener) mCurrentTask.mResponseListener).onFail(responseCode);
                        else
                            ((activityRetrieveDataByTimeListener) mCurrentTask.mResponseListener).onFail(responseCode);
                        break;

                    case CMD_EVENT_ADD:
                        if (responseCode == 200)
                            ((eventAddListener) mCurrentTask.mResponseListener).onSuccess(responseCode, ServerGson.event.add.fromJson(responseString));
                        else if (responseCode == 400)
                            ((eventAddListener) mCurrentTask.mResponseListener).onFail(responseCode);
                        else
                            ((eventAddListener) mCurrentTask.mResponseListener).onFail(responseCode);
                        break;

                    case CMD_EVENT_UPDATE:
                        if (responseCode == 200)
                            ((eventUpdateListener) mCurrentTask.mResponseListener).onSuccess(responseCode, ServerGson.event.update.fromJson(responseString));
                        else if (responseCode == 400)
                            ((eventUpdateListener) mCurrentTask.mResponseListener).onFail(responseCode);
                        else
                            ((eventUpdateListener) mCurrentTask.mResponseListener).onFail(responseCode);
                        break;

                    case CMD_EVENT_DELETE:
                        if (responseCode == 200)
                            ((eventDeleteListener) mCurrentTask.mResponseListener).onSuccess(responseCode);
                        else if (responseCode == 400)
                            ((eventDeleteListener) mCurrentTask.mResponseListener).onFail(responseCode);
                        else
                            ((eventDeleteListener) mCurrentTask.mResponseListener).onFail(responseCode);
                        break;

                    case CMD_EVENT_RETRIEVE_EVENTS:
                        if (responseCode == 200)
                            ((eventRetrieveEventsListener) mCurrentTask.mResponseListener).onSuccess(responseCode, ServerGson.event.retrieveEvents.fromJson(responseString));
                        else if (responseCode == 400)
                            ((eventRetrieveEventsListener) mCurrentTask.mResponseListener).onFail(responseCode);
                        else
                            ((eventRetrieveEventsListener) mCurrentTask.mResponseListener).onFail(responseCode);
                        break;

                    case CMD_EVENT_RETRIEVE_ALL_EVENTS_WITH_TODO:
                        if (responseCode == 200)
                            ((eventRetrieveAllEventsWithTodoListener) mCurrentTask.mResponseListener).onSuccess(responseCode, ServerGson.event.retrieveAllEventsWithTodo.fromJson(responseString));
                        else
                            ((eventRetrieveAllEventsWithTodoListener) mCurrentTask.mResponseListener).onFail(responseCode);
                        break;

                    case CMD_EVENT_RETRIEVE_ALL_EVENTS_BY_KID:
                        if (responseCode == 200)
                            ((eventRetrieveAllEventsByKidListener) mCurrentTask.mResponseListener).onSuccess(responseCode, ServerGson.event.retrieveAllEventsById.fromJson(responseString));
                        else
                            ((eventRetrieveAllEventsByKidListener) mCurrentTask.mResponseListener).onFail(responseCode);

                        break;

                    case CMD_EVENT_TODO_DONE:
                        if (responseCode == 200)
                            ((eventTodoDoneListener) mCurrentTask.mResponseListener).onSuccess(responseCode);
                        else
                            ((eventTodoDoneListener) mCurrentTask.mResponseListener).onFail(responseCode);
                        break;

                    case CMD_SUBHOST_ADD:
                        if (responseCode == 200)
                            ((subHostAddListener) mCurrentTask.mResponseListener).onSuccess(responseCode, ServerGson.subHost.add.fromJson(responseString));
                        else if (responseCode == 400)
                            ((subHostAddListener) mCurrentTask.mResponseListener).onFail(responseCode);
                        else if (responseCode == 409)
                            ((subHostAddListener) mCurrentTask.mResponseListener).onConflict(responseCode);
                        else
                            ((subHostAddListener) mCurrentTask.mResponseListener).onFail(responseCode);
                        break;

                    case CMD_SUBHOST_ACCEPT:
                        if (responseCode == 200)
                            ((subHostAcceptListener) mCurrentTask.mResponseListener).onSuccess(responseCode, ServerGson.subHost.accept.fromJson(responseString));
                        else if (responseCode == 400)
                            ((subHostAcceptListener) mCurrentTask.mResponseListener).onFail(responseCode);
                        else
                            ((subHostAcceptListener) mCurrentTask.mResponseListener).onFail(responseCode);
                        break;

                    case CMD_SUBHOST_DENY:
                        if (responseCode == 200)
                            ((subHostDenyListener) mCurrentTask.mResponseListener).onSuccess(responseCode, ServerGson.subHost.deny.fromJson(responseString));
                        else if (responseCode == 400)
                            ((subHostDenyListener) mCurrentTask.mResponseListener).onFail(responseCode);
                        else
                            ((subHostDenyListener) mCurrentTask.mResponseListener).onFail(responseCode);
                        break;

                    case CMD_SUBHOST_LIST:
                        if (responseCode == 200)
                            ((subHostListListener) mCurrentTask.mResponseListener).onSuccess(responseCode, ServerGson.subHost.list.fromJson(responseString));
                        else
                            ((subHostListListener) mCurrentTask.mResponseListener).onFail(responseCode);
                        break;

                    case CMD_GET_AVATAR:
                        ((getAvatarListener) mCurrentTask.mResponseListener).onSuccess(bitmap, mCurrentTask.mImageFile);
                        break;
                }
            }

            mCurrentTask = null;
        }
    };

    private Response.ErrorListener mErrorListener = new Response.ErrorListener() {
        @Override
        public void onErrorResponse(VolleyError error) {
            int responseCode = 0;
            if (error.networkResponse != null)
                responseCode = error.networkResponse.statusCode;

            if (mCurrentTask.mResponseListener != null) {
                Log("Request ERROR. Command:" + mCurrentTask.mCommand + " code:" + responseCode);
                switch (mCurrentTask.mCommand) {
                    case CMD_USER_LOGIN:
                        ((userLoginListener) mCurrentTask.mResponseListener).onFail(responseCode);
                        break;

                    case CMD_USER_REGISTER:
                        ((userRegisterListener) mCurrentTask.mResponseListener).onFail(responseCode, null);
                        break;

                    case CMD_USER_IS_TOKEN_VALID:
                        ((userIsTokenValidListener) mCurrentTask.mResponseListener).onFail(responseCode);
                        break;

                    case CMD_USER_IS_MAIL_AVAILABLE_TO_REGISTER:
                        if (responseCode == 409)
                            ((userIsMailAvailableToRegisterListener) mCurrentTask.mResponseListener).onValidState(false);
                        else
                            ((userIsMailAvailableToRegisterListener) mCurrentTask.mResponseListener).onFail(responseCode);
                        break;

                    case CMD_USER_UPDATE_PROFILE:
                        ((userUpdateProfileListener) mCurrentTask.mResponseListener).onFail(responseCode, null);
                        break;

                    case CMD_USER_RETRIEVE_USER_PROFILE:
                        ((userRetrieveUserProfileListener) mCurrentTask.mResponseListener).onFail(responseCode);
                        break;

                    case CMD_USER_FIND_BY_EMAIL:
                        ((userFindByEmailListener) mCurrentTask.mResponseListener).onFail(responseCode);
                        break;

                    case CMD_AVATAR_UPLOAD:
                        ((userAvatarUploadListener) mCurrentTask.mResponseListener).onFail(responseCode);
                        break;

                    case CMD_AVATAR_UPLOAD_KID:
                        ((userAvatarUploadKidListener) mCurrentTask.mResponseListener).onFail(responseCode);
                        break;

                    case CMD_KIDS_ADD:
                        ((kidsAddListener) mCurrentTask.mResponseListener).onFail(responseCode);
                        break;

                    case CMD_KIDS_UPDATE:
                        ((kidsUpdateListener) mCurrentTask.mResponseListener).onFail(responseCode);
                        break;

                    case CMD_KIDS_DELETE:
                        ((kidsDeleteListener) mCurrentTask.mResponseListener).onFail(responseCode);
                        break;

                    case CMD_KIDS_WHO_REGISTERED_MAC_ID:
                        if (responseCode == 404)
                            ((kidsWhoRegisteredMacIDListener) mCurrentTask.mResponseListener).onNotRegistered(responseCode);
                        else
                            ((kidsWhoRegisteredMacIDListener) mCurrentTask.mResponseListener).onFail(responseCode);
                        break;

                    case CMD_ACTIVITY_UPLOAD_RAW_DATA:
                        ((activityUploadRawDataListener) mCurrentTask.mResponseListener).onFail(responseCode);
                        break;

                    case CMD_ACTIVITY_RETRIEVE_DATA:
                        ((activityRetrieveDataListener) mCurrentTask.mResponseListener).onFail(responseCode);
                        break;

                    case CMD_ACTIVITY_RETRIEVE_DATA_BY_TIME:
                        ((activityRetrieveDataByTimeListener) mCurrentTask.mResponseListener).onFail(responseCode);
                        break;

                    case CMD_EVENT_ADD:
                        ((eventAddListener) mCurrentTask.mResponseListener).onFail(responseCode);
                        break;

                    case CMD_EVENT_UPDATE:
                        ((eventUpdateListener) mCurrentTask.mResponseListener).onFail(responseCode);
                        break;

                    case CMD_EVENT_DELETE:
                        ((eventDeleteListener) mCurrentTask.mResponseListener).onFail(responseCode);
                        break;

                    case CMD_EVENT_RETRIEVE_EVENTS:
                        ((eventRetrieveEventsListener) mCurrentTask.mResponseListener).onFail(responseCode);
                        break;

                    case CMD_EVENT_RETRIEVE_ALL_EVENTS_WITH_TODO:
                        ((eventRetrieveAllEventsWithTodoListener) mCurrentTask.mResponseListener).onFail(responseCode);
                        break;

                    case CMD_EVENT_RETRIEVE_ALL_EVENTS_BY_KID:
                        ((eventRetrieveAllEventsByKidListener) mCurrentTask.mResponseListener).onFail(responseCode);
                        break;

                    case CMD_EVENT_TODO_DONE:
                        ((eventTodoDoneListener) mCurrentTask.mResponseListener).onFail(responseCode);
                        break;

                    case CMD_SUBHOST_ADD:
                        ((subHostAddListener) mCurrentTask.mResponseListener).onFail(responseCode);
                        break;

                    case CMD_SUBHOST_ACCEPT:
                        ((subHostAcceptListener) mCurrentTask.mResponseListener).onFail(responseCode);
                        break;

                    case CMD_SUBHOST_DENY:
                        ((subHostDenyListener) mCurrentTask.mResponseListener).onFail(responseCode);
                        break;

                    case CMD_SUBHOST_LIST:
                        ((subHostListListener) mCurrentTask.mResponseListener).onFail(responseCode);
                        break;

                    case CMD_GET_AVATAR:
                        ((getAvatarListener) mCurrentTask.mResponseListener).onFail(responseCode);
                        break;

                }
            }

            mCurrentTask = null;
        }
    };

    private class TaskItem {
        String mCommand;
        Request<NetworkResponse> mRequest;
        Object mResponseListener;
        String mImageFile;

        TaskItem(Request<NetworkResponse> request, String command, Object responseListener) {
            mRequest = request;
            mCommand = command;
            mResponseListener = responseListener;
        }

        TaskItem(Request<NetworkResponse> request, String command, Object responseListener, String imageFile) {
            mRequest = request;
            mCommand = command;
            mResponseListener = responseListener;
            mImageFile = imageFile;
        }
    }

    static String getMacID(String macAddress) {
        String[] separated = macAddress.split(":");
        String macId = "";
        for (String s : separated)
            macId += s;

        //macId = "1A2B3C4D5F01";
        return macId;
    }

    static String getMacAddress(String macId) {
        if (macId.length() < 12)
            return "00:00:00:00:00:00";

        return String.format("%c%c:%c%c:%c%c:%c%c:%c%c:%c%c",
                macId.charAt(0), macId.charAt(1), macId.charAt(2), macId.charAt(3),
                macId.charAt(4), macId.charAt(5), macId.charAt(6), macId.charAt(7),
                macId.charAt(8), macId.charAt(9), macId.charAt(10), macId.charAt(11)
        );
    }

    static String GetAvatarFilePath() {
        File sdCard = Environment.getExternalStorageDirectory();
        return sdCard.getAbsolutePath() + "/Swing";
    }

    static void ResetAvatar() {
        File dir = new File(GetAvatarFilePath());
        if (dir.isDirectory()) {
            String[] children = dir.list();
            for (String child : children)
                new File(dir, child).delete();
        }
    }

    static String createAvatarFile(Bitmap bitmap, String filename, String extension) {
        String avatarFilename = null;
        FileOutputStream out = null;
        try {
            File dir = new File(GetAvatarFilePath());
            if (dir.mkdirs())
                Log.d("swing", "false");

            avatarFilename = dir + "/" + filename + extension;
            out = new FileOutputStream(avatarFilename);
            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, out);

        } catch (Exception e) {
            avatarFilename = null;
            e.printStackTrace();
        } finally {
            try {
                if (out != null) {
                    out.close();
                }
            } catch (IOException e) {
                avatarFilename = null;
                e.printStackTrace();
            }
        }
        Log.d("createAvatarFile", "Filename " + avatarFilename);
        return avatarFilename;
    }
}
