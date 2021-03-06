package com.kidsdynamic.swing.androidswingapp;

import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;

import java.util.ArrayList;
import java.util.StringTokenizer;

/**
 * AutivityConfig
 * 保存系統設置的 SharedPreferences
 */

public class ActivityConfig {
    private SharedPreferences mSharedPreferences;
    private SharedPreferences.OnSharedPreferenceChangeListener mOnChangeListener = null;
    private Context mContext;

    final static String DEF_APPNAME = "ActivityConfig";
    final static String DEF_APPVERSION = "0001-0100-0010";
    final static String PREFS_SPLIT = "__BPPS:__";

    public final static String KEY_APPNAME = "KEY_NAME";
    public final static String KEY_APPVERSION = "KEY_VERSION";

    public final static String KEY_LANGUAGE = "KEY_LANGUAGE";
    public final static String KEY_REGION = "KEY_REGION";
    public final static String KEY_MAIL = "KEY_MAIL";
    public final static String KEY_PASSWORD = "KEY_PASSWORD";
    public final static String KEY_AUTH_TOKEN = "KEY_AUTH_TOKEN";

    private void LOG(String message) {
        Log.d("ActivityConfig", message);
    }

    public ActivityConfig(Context context, SharedPreferences.OnSharedPreferenceChangeListener onChangeListener) {
        mContext = context;
        mSharedPreferences = mContext.getSharedPreferences("AndroidSwingApp", Context.MODE_PRIVATE);

        String name = mSharedPreferences.getString(KEY_APPNAME, "__ERROR__");
        String version = mSharedPreferences.getString(KEY_APPVERSION, "__ERROR__");
        // 當DEF_APPNAME, DEF_APPVERSION與SharedPreference中不同時, 載入預設值.
        // 若要在新版本發行時, 要求用戶端重新載入配置時, 應修改DEV_APVERSION
        if (!name.equals(DEF_APPNAME) || !version.equals(DEF_APPVERSION)) {
            loadDefaultTable();
        }

        if (onChangeListener != null)
            mSharedPreferences.registerOnSharedPreferenceChangeListener(onChangeListener);
    }

    public void loadDefaultTable() {
        SharedPreferences.Editor editor = mSharedPreferences.edit();
        editor.clear();
        editor.putString(KEY_APPNAME, DEF_APPNAME);
        editor.putString(KEY_APPVERSION, DEF_APPVERSION);

        LOG("loadDefaultTable");

        editor.apply();
    }

    public boolean getBoolean(String key) {
        return mSharedPreferences.getBoolean(key, false);
    }

    public void setBoolean(String key, boolean value) {
        mSharedPreferences.edit().
                putBoolean(key, value).
                apply();
    }

    public int getInt(String key) {
        return mSharedPreferences.getInt(key, 0);
    }

    public void setInt(String key, int value) {
        mSharedPreferences.edit().
                putInt(key, value).
                apply();
    }

    public long getLong(String key) {
        return mSharedPreferences.getLong(key, 0);
    }

    public void setLong(String key, long value) {
        mSharedPreferences.edit().
                putLong(key, value).
                apply();
    }

    public double getDouble(String key) {
        long tt = Double.doubleToLongBits(0);
        return Double.longBitsToDouble(mSharedPreferences.getLong(key, Double.doubleToLongBits(0.0f)));
    }

    public void setDouble(String key, double value) {
        mSharedPreferences.edit().
                putLong(key, Double.doubleToRawLongBits(value)).
                apply();
    }

    public String getString(String key) {
        return mSharedPreferences.getString(key, "");
    }

    public void setString(String key, String value) {
        mSharedPreferences.edit().
                putString(key, value).
                apply();
    }

    public ArrayList<String> getStringList(String key) {
        // 保存字串陣列時, 以組合字串保存
        String pref = getString(key);
        ArrayList<String> list = new ArrayList<>();
        StringTokenizer st = new StringTokenizer(pref, PREFS_SPLIT);

        while (st.hasMoreTokens()) {
            list.add(st.nextToken());
        }

        return list;
    }

    public void setStringList(String key, ArrayList<String> list) {
        String pref = "";
        int idx, count = list.size();

        if (count != 0)
            pref = list.get(0);

        for (idx = 1; idx < count; idx++)
            pref += PREFS_SPLIT + list.get(idx);

        setString(key, pref);
    }
}
