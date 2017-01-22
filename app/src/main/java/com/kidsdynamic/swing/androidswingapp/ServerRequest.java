package com.kidsdynamic.swing.androidswingapp;

/**
 * Created by weichigio on 2017/1/10.
 */

import android.content.Context;
import android.util.Log;

import com.android.volley.AuthFailureError;
import com.android.volley.NetworkResponse;
import com.android.volley.ParseError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.VolleyLog;
import com.android.volley.toolbox.HttpHeaderParser;

import org.apache.http.HttpEntity;
import org.apache.http.entity.mime.HttpMultipartMode;
import org.apache.http.entity.mime.MultipartEntityBuilder;
import org.apache.http.entity.mime.content.FileBody;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.Exchanger;

public class ServerRequest extends Request<NetworkResponse> {
    private Context mContext;
    private String mUrl;
    private int mMethod;
    private Map<String, String> mMap;
    private Response.Listener<NetworkResponse> mListener;
    private HttpEntity mHttpEntity = null;
    protected String mAuthToken = null;

    public ServerRequest(Context context, int method, String url, Response.Listener<NetworkResponse> listener, Response.ErrorListener errorListener, Map<String, String> map, String filePath, String token) {
        super(method, url, errorListener);

        mContext = context;
        mMethod = method;
        mUrl = url;
        mListener = listener;
        mMap = map;
        mAuthToken = token;

        if (filePath != null && !filePath.equals("")) {
            File file = new File(filePath);
            if (file!=null) {
                if (method != Method.POST) {
                    Log.d("ServerRequest", "Upload file must use POST method.");
                }
                buildMultipartEntity(file, map);
            }
        }
    }

    private void buildMultipartEntity(File file, Map<String, String> map) {
        MultipartEntityBuilder builder = MultipartEntityBuilder.create();

        builder.setMode(HttpMultipartMode.BROWSER_COMPATIBLE);

        for (Map.Entry entry : map.entrySet()) {
            builder.addTextBody(entry.getKey().toString(), entry.getValue().toString());
        }

        if (file != null)
            builder.addPart("upload", new FileBody(file));
        mHttpEntity = builder.build();
    }

    @Override
    public String getBodyContentType() {
        if (mHttpEntity == null) {
            switch(mMethod) {
                case Method.POST:
                    return "application/json";
                case Method.GET:
                    return "";
                case Method.PUT:
                    return "application/json";
                case Method.DELETE:
                    return "";
                default:
                    return "application/x-www-form-urlencoded; charset=" + getParamsEncoding();
            }
        }
        return mHttpEntity.getContentType().getValue();
    }

    @Override
    public byte[] getBody() throws AuthFailureError {
        StringBuilder encodedParams = new StringBuilder();
        byte[] rtn = null;

        if (mHttpEntity == null) {
            switch(mMethod) {
                case Method.PUT:
                case Method.POST: {
                    try {
                        for (Map.Entry<String, String> entry : mMap.entrySet()) {
                            if (entry.getKey().equals("json")) {
                                encodedParams.append(entry.getValue());
                                break;
                            }
                        }
                        rtn = encodedParams.toString().getBytes(getParamsEncoding());
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                } break;

                case Method.GET:
                case Method.DELETE:
                default: {
                    try {
                        for (Map.Entry<String, String> entry : mMap.entrySet()) {
                            encodedParams.append(URLEncoder.encode(entry.getKey(), getParamsEncoding()));
                            encodedParams.append('=');
                            encodedParams.append(URLEncoder.encode(entry.getValue(), getParamsEncoding()));
                            encodedParams.append('&');
                        }
                        rtn = encodedParams.toString().getBytes(getParamsEncoding());
                    } catch (UnsupportedEncodingException uee) {
                        throw new RuntimeException("Encoding not supported: " + getParamsEncoding(), uee);
                    }
                } break;
            }
            return rtn;
        } else {
            ByteArrayOutputStream bos = new ByteArrayOutputStream();
            try {
                mHttpEntity.writeTo(bos);
            } catch (IOException e) {
                VolleyLog.e("IOException writing to ByteArrayOutputStream");
            }
            return bos.toByteArray();
        }
    }

    @Override
    public Map<String, String> getHeaders() throws AuthFailureError {

        if (mAuthToken!=null && !mAuthToken.equals("")) {
            Map<String,String> map = new HashMap<>();
            map.put("x-auth-token", mAuthToken);
            return map;
        } else {
            return super.getHeaders();
        }
    }

    @Override
    protected Response<NetworkResponse> parseNetworkResponse(NetworkResponse response) {
        return Response.success(response, HttpHeaderParser.parseCacheHeaders(response));
    }

    @Override
    protected void deliverResponse(NetworkResponse response) {
        mListener.onResponse(response);
    }

    @Override
    public void deliverError(VolleyError error) {
        super.deliverError(error);
    }

    @Override
    public Request<?> setRequestQueue(RequestQueue requestQueue) {
        return super.setRequestQueue(requestQueue);
    }
}