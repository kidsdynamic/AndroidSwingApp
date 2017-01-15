package com.kidsdynamic.swing.androidswingapp;

/**
 * Created by weichigio on 2017/1/10.
 */

import android.util.Log;

import com.android.volley.AuthFailureError;
import com.android.volley.NetworkResponse;
import com.android.volley.ParseError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.HttpHeaderParser;

import java.io.UnsupportedEncodingException;
import java.util.Map;

public class VolleyRequest extends Request<NetworkResponse> {
    private ActivityMain mActivity;
    private String mUrl;
    private Map<String, String> mMap;
    private Response.Listener<NetworkResponse> mListener;

    public VolleyRequest(ActivityMain activity, int method, String url, Response.Listener<NetworkResponse> listener, Response.ErrorListener errorListener, Map<String, String> map) {
        super(method, url, errorListener);

        mActivity = activity;
        mUrl = url;
        mListener = listener;
        mMap = map;
    }

    @Override
    protected Map<String, String> getParams() throws AuthFailureError {
        return mMap;
    }

    @Override
    protected Response<NetworkResponse> parseNetworkResponse(NetworkResponse response) {
        return Response.success(response, HttpHeaderParser.parseCacheHeaders(response));
        /*
        try {
            return Response.success(

                    new String(
                            new String(
                                    response.data,
                                    HttpHeaderParser.parseCharset(response.headers)
                            ).getBytes("ISO-8859-1"),
                            "utf-8"
                    ),
                    HttpHeaderParser.parseCacheHeaders(response)
            );
        } catch (UnsupportedEncodingException e) {
            Log.d("xxx E1 <<<", new ParseError(e).getMessage());
            return Response.error(new ParseError(e));
        } catch (Exception je) {
            Log.d("xxx E2 <<<", new ParseError(je).getMessage());
            return Response.error(new ParseError(je));
        }
        */
    }

    @Override
    protected void deliverResponse(NetworkResponse response) {
        //DebugDashboard.push(mActivity, DebugDashboard.TYPE_DOWNLOAD, response);
        mListener.onResponse(response);
    }

    @Override
    public void deliverError(VolleyError error) {
        //DebugDashboard.push(mActivity, DebugDashboard.TYPE_ERROR, error.getMessage());
        super.deliverError(error);
    }

    @Override
    public Request<?> setRequestQueue(RequestQueue requestQueue) {
        //DebugDashboard.push(mActivity, DebugDashboard.TYPE_UNDEFINE, "Connect:" + mUrl);
        //DebugDashboard.push(mActivity, DebugDashboard.TYPE_UPLOAD, mMap.toString());
        return super.setRequestQueue(requestQueue);
    }
}