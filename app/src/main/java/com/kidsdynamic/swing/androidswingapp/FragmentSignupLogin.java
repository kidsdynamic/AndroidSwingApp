package com.kidsdynamic.swing.androidswingapp;

import android.app.Fragment;
import android.os.Bundle;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Toast;

import com.android.volley.NetworkResponse;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.HttpHeaderParser;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by 03543 on 2016/12/30.
 */

public class FragmentSignupLogin extends ViewFragment {
    private ActivityMain mActivityMain;
    private View mViewMain;

    private Button mButtonLogin;
    private Button mButtonFacebook;
    private Button mButtonGoogle;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mActivityMain = (ActivityMain) getActivity();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        mViewMain = inflater.inflate(R.layout.fragment_signup_login, container, false);

        mButtonLogin = (Button) mViewMain.findViewById(R.id.signup_login_login);
        mButtonLogin.setOnClickListener(mOnLoginListener);

        mButtonFacebook = (Button) mViewMain.findViewById(R.id.signup_login_facebook);
        mButtonFacebook.setOnClickListener(mOnFacebookListener);

        mButtonGoogle = (Button) mViewMain.findViewById(R.id.signup_login_google);
        mButtonGoogle.setOnClickListener(mOnGoogleListener);

        return mViewMain;
    }

    @Override
    public ViewFragmentConfig getConfig() {
        return new ViewFragmentConfig("Sign up", false, false,
                ActivityMain.RESOURCE_IGNORE, ActivityMain.RESOURCE_IGNORE, ActivityMain.RESOURCE_IGNORE);
    }

    private Button.OnClickListener mOnLoginListener = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            String mail = mActivityMain.mConfig.getString(Config.KEY_MAIL);
            String password = mActivityMain.mConfig.getString(Config.KEY_PASSWORD);

            if (mail.equals("") || password.equals(""))
                mActivityMain.selectFragment(FragmentSignupAccount.class.getName(), null);
            else
                mActivityMain.selectFragment(FragmentSyncNow.class.getName(), null);
        }
    };

    private Button.OnClickListener mOnFacebookListener = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            Toast.makeText(mActivityMain, "Login failed!", Toast.LENGTH_SHORT).show();
            mActivityMain.selectFragment(FragmentSignupAccount.class.getName(), null);
        }
    };

    private Button.OnClickListener mOnGoogleListener = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            Toast.makeText(mActivityMain, "Login failed!", Toast.LENGTH_SHORT).show();
            mActivityMain.selectFragment(FragmentSignupAccount.class.getName(), null);
        }
    };

    private void requestLogin(String username, String password) {
        Map<String, String> map = new HashMap<String, String>();

        map.put("username", username);
        map.put("password", password);

        Request<NetworkResponse> request = new NormalRequest(
                mActivityMain,
                Request.Method.POST,
                "https://childrenlab.com:8111/v1/user/login",
                mLoginRequestListener, mLoginRequestErrorListener, map);
        mActivityMain.mRequestQueue.add(request);
    }

    Response.Listener<NetworkResponse> mLoginRequestListener = new Response.Listener<NetworkResponse>() {
        @Override
        public void onResponse(NetworkResponse response) {
            try {
                String responseString = new String(
                        new String(
                                response.data,
                                HttpHeaderParser.parseCharset(response.headers)
                        ).getBytes("ISO-8859-1"),
                        "utf-8"
                );
                int responseCode = response.statusCode;
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    };

    Response.ErrorListener mLoginRequestErrorListener = new Response.ErrorListener() {
        @Override
        public void onErrorResponse(VolleyError error) {
        }
    };

}
