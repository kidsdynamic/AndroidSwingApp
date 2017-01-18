package com.kidsdynamic.swing.androidswingapp;

import android.content.Context;
import android.os.Handler;
import android.os.Message;
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
    private final static String SERVER_ADDRESS = "https://childrenlab.com:8111";

    public final static int MSG_LOGIN = 0x20000001;
    public final static int MSG_REGISTER = 0x20000002;

    public RequestQueue mRequestQueue;
    Queue<TaskItem> mTaskQueue = new ConcurrentLinkedQueue<>();
    private Handler mHandler = new Handler();
    private Context mContext;

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
        void onResponse(boolean success, int command, int resultCode, String result);
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

    public void Login(ResponseListener response, String username, String password) {
        Map<String, String> map = new HashMap<>();
        map.put("username", username);
        map.put("password", password);
        Request<NetworkResponse> request = new ServerRequest(
                mContext,
                Request.Method.POST,
                SERVER_ADDRESS + "/v1/user/login",
                mSuccessListener, mErrorListener, map, null);
        mTaskQueue.add(new TaskItem(MSG_LOGIN, request, response));
    }

    public void Register(ResponseListener response, String email, String password, String firstName, String lastName, String phoneNumber, String zipCode) {
        Map<String, String> map = new HashMap<>();
        map.put("email", email);
        map.put("password", password);
        map.put("firstName", firstName);
        map.put("lastName", lastName);
        map.put("phoneNumber", phoneNumber);
        map.put("zipCode", zipCode);
        Request<NetworkResponse> request = new ServerRequest(
                mContext,
                Request.Method.POST,
                SERVER_ADDRESS + "/v1/user/register",
                mSuccessListener, mErrorListener, map, null);
        mTaskQueue.add(new TaskItem(MSG_REGISTER, request, response));
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
                mCurrentTask.mResponseListener.onResponse(true, mCurrentTask.mCommand, responseCode, responseString);

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
                mCurrentTask.mResponseListener.onResponse(false, mCurrentTask.mCommand, result, error.getMessage());

            mCurrentTask = null;
        }
    };

    private class TaskItem {
        int mCommand;
        Request<NetworkResponse> mRequest;
        ResponseListener mResponseListener;

        TaskItem(int command, Request<NetworkResponse> request, ResponseListener response) {
            mCommand = command;
            mRequest = request;
            mResponseListener = response;
        }
    }

}
