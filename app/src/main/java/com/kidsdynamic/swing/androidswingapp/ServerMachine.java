package com.kidsdynamic.swing.androidswingapp;

import android.content.Context;
import android.os.Handler;
import android.util.Log;

import com.android.volley.NetworkResponse;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.HttpHeaderParser;
import com.android.volley.toolbox.Volley;

import java.util.HashMap;
import java.util.Map;
import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;

/**
 * Created by weichigio on 2017/1/17.
 */

public class ServerMachine {
    private final static String SERVER_ADDRESS = "https://childrenlab.com:8111/v1";

    private final static String CMD_USER_LOGIN = SERVER_ADDRESS + "/user/userLogin";
    private final static String CMD_USER_REGISTER = SERVER_ADDRESS + "/user/userRegister";
    private final static String CMD_USER_IS_TOKEN_VALID = SERVER_ADDRESS + "/user/isTokenValid";
    private final static String CMD_USER_IS_MAIL_AVAILABLE_TO_REGISTER = SERVER_ADDRESS + "/user/isEmailAvailableToRegister";
    private final static String CMD_USER_UPDATE_PROFILE = SERVER_ADDRESS + "/user/updateProfile";
    private final static String CMD_USER_RETRIEVE_USER_PROFILE = SERVER_ADDRESS + "/user/retrieveUserProfile";

    private final static String CMD_AVATAR_UPLOAD = SERVER_ADDRESS + "/user/avatar/upload";
    private final static String CMD_AVATAR_UPLOAD_KID = SERVER_ADDRESS + "/user/avatar/uploadKid";

    private final static String CMD_KIDS_ADD = SERVER_ADDRESS + "/kids/add";
    private final static String CMD_KIDS_UPDATE = SERVER_ADDRESS + "/kids/update";
    private final static String CMD_KIDS_WHO_REGISTERED_MAC_ID = SERVER_ADDRESS + "/kids/whoRegisteredMacID";

    private final static String CMD_ACTIVITY_UPLOAD_RAW_DATA = SERVER_ADDRESS + "/activity/uploadRawData";
    private final static String CMD_ACTIVITY_RETRIEVE_DATA = SERVER_ADDRESS + "/activity/retrieveData";

    private final static String CMD_EVENT_ADD = SERVER_ADDRESS + "/event/add";
    private final static String CMD_EVENT_UPDATE = SERVER_ADDRESS + "/event/update";
    private final static String CMD_EVENT_DELETE = SERVER_ADDRESS + "/event/delete";
    private final static String CMD_EVENT_RETRIEVE_EVENTS = SERVER_ADDRESS + "/event/retrieveEvents";
    private final static String CMD_EVENT_RETRIEVE_ALL_EVENTS_WITH_TODO = SERVER_ADDRESS + "/event/retrieveAllEventsWithTodo";

    private final static String CMD_SUBHOST_ADD = SERVER_ADDRESS + "subHost/add";
    private final static String CMD_SUBHOST_ACCEPT = SERVER_ADDRESS + "subHost/accept";
    private final static String CMD_SUBHOST_DENY = SERVER_ADDRESS + "subHost/deny";
    private final static String CMD_SUBHOST_LIST = SERVER_ADDRESS + "subHost/list";

    public RequestQueue mRequestQueue;
    Queue<TaskItem> mTaskQueue = new ConcurrentLinkedQueue<>();
    private Handler mHandler = new Handler();
    private Context mContext;
    private String mAuthToken = null;

    private void Log(String msg) {
        Log.i("ServerMachine", msg);
    }

    public ServerMachine(Context context) {
        mContext = context;
        mRequestQueue = Volley.newRequestQueue(context.getApplicationContext());
    }

    public boolean Start() {
        mState = 0;
        mTaskQueue.clear();
        mHandler.postDelayed(stateMachine, 100);
        return true;
    }

    public boolean Stop() {
        mHandler.removeCallbacks(stateMachine);
        return true;
    }

    public interface ResponseListener {
        void onResponse(boolean success, int resultCode, String result);
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
        return new ServerRequest(mContext, method, address, mSuccessListener, mErrorListener, map, filename, mAuthToken);
    }

    public void setAuthToken(String token) {
        mAuthToken = token;
    }

    public String getAuthToken() {
        return mAuthToken;
    }

    public void userLogin(ResponseListener response, String email, String password) {
        Map<String, String> map = new HashMap<>();
        map.put("json", ServerGson.getUserLogin(email, password));
        mTaskQueue.add(new TaskItem(NewRequest(Request.Method.POST, CMD_USER_LOGIN, map, null), response));
    }

    public void userRegister(ResponseListener response, String email, String password, String firstName, String lastName, String phoneNumber, String zipCode) {
        Map<String, String> map = new HashMap<>();
        map.put("json", ServerGson.getUserRegister(email, password, firstName, lastName, phoneNumber, zipCode));
        mTaskQueue.add(new TaskItem(NewRequest(Request.Method.POST, CMD_USER_REGISTER, map, null), response));
    }

    public void userIsTokenValid(ResponseListener response, String email, String token) {
        Map<String, String> map = new HashMap<>();
        map.put("json", ServerGson.getUserIsTokenValid(email, token));
        mTaskQueue.add(new TaskItem(NewRequest(Request.Method.POST, CMD_USER_IS_TOKEN_VALID, map, null), response));
    }

    public void userIsMailAvailableToRegister(ResponseListener response, String email) {
        Map<String, String> map = new HashMap<>();
        String addressForGet = CMD_USER_IS_MAIL_AVAILABLE_TO_REGISTER + "?";
        addressForGet += "email=" + email;
        mTaskQueue.add(new TaskItem(NewRequest(Request.Method.GET, addressForGet, map, null), response));
    }

    public void userUpdateProfile(ResponseListener response, String firstName, String lastName, String phoneNumber, String zipCode) {
        Map<String, String> map = new HashMap<>();
        map.put("json", ServerGson.getUserUpdateProfile(firstName, lastName, phoneNumber, zipCode));
        mTaskQueue.add(new TaskItem(NewRequest(Request.Method.PUT, CMD_USER_UPDATE_PROFILE, map, null), response));
    }

    public void userRetrieveUserProfile(ResponseListener response) {
        Map<String, String> map = new HashMap<>();
        mTaskQueue.add(new TaskItem(NewRequest(Request.Method.GET, CMD_USER_RETRIEVE_USER_PROFILE, map, null), response));
    }

    public void userAvatarUpload(ResponseListener response, String filePath) {
        Map<String, String> map = new HashMap<>();
        mTaskQueue.add(new TaskItem(NewRequest(Request.Method.POST, CMD_AVATAR_UPLOAD, map, filePath), response));
    }

    public void userAvatarUploadKid(ResponseListener response, String kidId, String filePath) {
        Map<String, String> map = new HashMap<>();
        map.put("kidId", kidId);
        mTaskQueue.add(new TaskItem(NewRequest(Request.Method.POST, CMD_AVATAR_UPLOAD_KID, map, filePath), response));
    }

    public void kidsAdd(ResponseListener response, String firstName, String lastName, String macId) {
        Map<String, String> map = new HashMap<>();
        map.put("firstName", firstName);
        map.put("lastName", lastName);
        map.put("macId", macId);
        mTaskQueue.add(new TaskItem(NewRequest(Request.Method.POST, CMD_KIDS_ADD, map, null), response));
    }

    public void kidsUpdate(ResponseListener response, String kidId, String firstName, String lastName) {
        Map<String, String> map = new HashMap<>();
        map.put("kidId", kidId);
        map.put("firstName", firstName);
        map.put("lastName", lastName);
        mTaskQueue.add(new TaskItem(NewRequest(Request.Method.PUT, CMD_KIDS_UPDATE, map, null), response));
    }

    public void kidsWhoRegisteredMacID(ResponseListener response, String macId) {
        Map<String, String> map = new HashMap<>();
        String addressForGet = CMD_KIDS_WHO_REGISTERED_MAC_ID + "?";
        addressForGet += "macId=" + macId;
        mTaskQueue.add(new TaskItem(NewRequest(Request.Method.GET, addressForGet, map, null), response));
    }

    public void activityUploadRawData(ResponseListener response, String indoorActivity, String outdoorActivity, String time, String macId) {
        Map<String, String> map = new HashMap<>();
        map.put("indoorActivity", indoorActivity);
        map.put("outdoorActivity", outdoorActivity);
        map.put("time", time);
        map.put("macId", macId);
        mTaskQueue.add(new TaskItem(NewRequest(Request.Method.POST, CMD_ACTIVITY_UPLOAD_RAW_DATA, map, null), response));
    }

    public void activityRetrieveData(ResponseListener response, String kidId, String period) {
        Map<String, String> map = new HashMap<>();
        String addressForGet = CMD_ACTIVITY_RETRIEVE_DATA + "?";
        addressForGet += "kidId=" + kidId;
        addressForGet += "&period=" + period;
        mTaskQueue.add(new TaskItem(NewRequest(Request.Method.GET, addressForGet, map, null), response));
    }

    public void eventAdd(ResponseListener response, String kidId, String name, String startDate, String endDate,
                         String color, String description, String alert, String city, String state, String repeat,
                         String timezoneOffset, String todo) {
        Map<String, String> map = new HashMap<>();
        map.put("kidId", kidId);
        map.put("name", name);
        map.put("startDate", startDate);
        map.put("endDate", endDate);
        map.put("color", color);
        map.put("description", description);
        map.put("alert", alert);
        map.put("city", city);
        map.put("state", state);
        map.put("repeat", repeat);
        map.put("timezoneOffset", timezoneOffset);
        map.put("todo", todo);
        mTaskQueue.add(new TaskItem(NewRequest(Request.Method.POST, CMD_EVENT_ADD, map, null), response));
    }

    public void eventUpdate(ResponseListener response, String eventId, String name, String startDate, String endDate,
                            String color, String description, String alert, String city, String state, String repeat,
                            String timezoneOffset, String todo) {
        Map<String, String> map = new HashMap<>();
        map.put("eventId", eventId);
        map.put("name", name);
        map.put("startDate", startDate);
        map.put("endDate", endDate);
        map.put("color", color);
        map.put("description", description);
        map.put("alert", alert);
        map.put("city", city);
        map.put("state", state);
        map.put("repeat", repeat);
        map.put("timezoneOffset", timezoneOffset);
        map.put("todo", todo);
        mTaskQueue.add(new TaskItem(NewRequest(Request.Method.PUT, CMD_EVENT_UPDATE, map, null), response));
    }

    public void eventDelete(ResponseListener response, String eventId) {
        Map<String, String> map = new HashMap<>();
        map.put("eventId", eventId);
        mTaskQueue.add(new TaskItem(NewRequest(Request.Method.DELETE, CMD_EVENT_DELETE, map, null), response));
    }

    public void eventRetrieveEvents(ResponseListener response, String period, String date) {
        Map<String, String> map = new HashMap<>();
        String addressForGet = CMD_EVENT_RETRIEVE_EVENTS + "?";
        addressForGet += "period=" + period;
        addressForGet += "&date=" + date;
        mTaskQueue.add(new TaskItem(NewRequest(Request.Method.GET, addressForGet, map, null), response));
    }

    public void eventRetrieveAllEventsWithTodo(ResponseListener response) {
        Map<String, String> map = new HashMap<>();
        mTaskQueue.add(new TaskItem(NewRequest(Request.Method.GET, CMD_EVENT_RETRIEVE_ALL_EVENTS_WITH_TODO, map, null), response));
    }

    public void subhostAdd(ResponseListener response, String hostId) {
        Map<String, String> map = new HashMap<>();
        map.put("hostId", hostId);
        mTaskQueue.add(new TaskItem(NewRequest(Request.Method.POST, CMD_SUBHOST_ADD, map, null), response));
    }

    public void subhostAccept(ResponseListener response, String subHostId, String KidId) {
        Map<String, String> map = new HashMap<>();
        map.put("subHostId", subHostId);
        map.put("KidId", KidId);
        mTaskQueue.add(new TaskItem(NewRequest(Request.Method.PUT, CMD_SUBHOST_ACCEPT, map, null), response));
    }

    public void subhostDeny(ResponseListener response, String subHostId) {
        Map<String, String> map = new HashMap<>();
        map.put("subHostId", subHostId);
        mTaskQueue.add(new TaskItem(NewRequest(Request.Method.PUT, CMD_SUBHOST_DENY, map, null), response));
    }

    public void subhostList(ResponseListener response, String status) {
        Map<String, String> map = new HashMap<>();
        String addressForGet = CMD_SUBHOST_LIST + "?";
        addressForGet += "status=" + status;
        mTaskQueue.add(new TaskItem(NewRequest(Request.Method.GET, addressForGet, map, null), response));
    }

    Response.Listener<NetworkResponse> mSuccessListener = new Response.Listener<NetworkResponse>() {
        @Override
        public void onResponse(NetworkResponse response) {
            String responseString = "";
            int responseCode = 0;
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

            if (mCurrentTask.mResponseListener != null)
                mCurrentTask.mResponseListener.onResponse(true, responseCode, responseString);

            mCurrentTask = null;
        }
    };

    Response.ErrorListener mErrorListener = new Response.ErrorListener() {
        @Override
        public void onErrorResponse(VolleyError error) {
            int result = 0;
            if (error.networkResponse!=null)
                result = error.networkResponse.statusCode;
            if (mCurrentTask.mResponseListener != null)
                mCurrentTask.mResponseListener.onResponse(false, result, error.getMessage());

            mCurrentTask = null;
        }
    };

    private class TaskItem {
        Request<NetworkResponse> mRequest;
        ResponseListener mResponseListener;
        String mAuthToken;

        TaskItem(Request<NetworkResponse> request, ResponseListener response) {
            mRequest = request;
            mResponseListener = response;
        }
    }

}
