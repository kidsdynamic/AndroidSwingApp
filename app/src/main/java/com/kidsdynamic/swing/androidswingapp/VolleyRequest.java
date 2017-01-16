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
import java.util.Map;

public class VolleyRequest extends Request<NetworkResponse> {
    private Context mContext;
    private String mUrl;
    private Map<String, String> mMap;
    private Response.Listener<NetworkResponse> mListener;
    private HttpEntity mHttpEntity = null;

    public VolleyRequest(Context context, int method, String url, Response.Listener<NetworkResponse> listener, Response.ErrorListener errorListener, Map<String, String> map, String filePath) {
        super(method, url, errorListener);

        mContext = context;
        mUrl = url;
        mListener = listener;
        mMap = map;

        if (filePath != null && !filePath.equals("")) {
            File file = new File(filePath);
            if (file!=null) {
                if (method != Method.POST) {
                    Log.d("VolleyRequest", "Upload file must use POST method.");
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
            builder.addPart("file1", new FileBody(file));
        mHttpEntity = builder.build();
    }

    @Override
    public String getBodyContentType() {
        if (mHttpEntity == null)
            return "application/x-www-form-urlencoded; charset=" + getParamsEncoding();
        return mHttpEntity.getContentType().getValue();
    }

    @Override
    public byte[] getBody() throws AuthFailureError {
        if (mHttpEntity == null) {
            StringBuilder encodedParams = new StringBuilder();
            try {
                for (Map.Entry<String, String> entry : mMap.entrySet()) {
                    encodedParams.append(URLEncoder.encode(entry.getKey(), getParamsEncoding()));
                    encodedParams.append('=');
                    encodedParams.append(URLEncoder.encode(entry.getValue(), getParamsEncoding()));
                    encodedParams.append('&');
                }
                return encodedParams.toString().getBytes(getParamsEncoding());
            } catch (UnsupportedEncodingException uee) {
                throw new RuntimeException("Encoding not supported: " + getParamsEncoding(), uee);
            }
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