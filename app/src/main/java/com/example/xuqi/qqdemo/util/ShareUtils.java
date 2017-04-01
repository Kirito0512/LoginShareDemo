package com.example.xuqi.qqdemo.util;

import android.content.Context;
import android.content.SharedPreferences;

/**
 * Created by xuqi on 17/3/23.
 */
//封装SharedPreferences工具类
public class ShareUtils {
    public static final String NAME = "config";

    //
    public static void putString(Context mContext, String key, String value) {
        SharedPreferences sp = mContext.getSharedPreferences(NAME, mContext.MODE_PRIVATE);
        sp.edit().putString(key, value).commit();
    }

    // defValue是默认值，如果sharedPreferences
    public static String getString(Context mContext, String key, String defValue) {
        SharedPreferences sp = mContext.getSharedPreferences(NAME, mContext.MODE_PRIVATE);
        return sp.getString(key, defValue);
    }

    //
    public static void putInt(Context mContext, String key, int value) {
        SharedPreferences sp = mContext.getSharedPreferences(NAME, mContext.MODE_PRIVATE);
        sp.edit().putInt(key, value).commit();
    }

    // defValue是默认值，如果sharedPreferences
    public static int getInt(Context mContext, String key, int defValue) {
        SharedPreferences sp = mContext.getSharedPreferences(NAME, mContext.MODE_PRIVATE);
        return sp.getInt(key, defValue);
    }

    //
    public static void putBoolean(Context mContext, String key, boolean value) {
        SharedPreferences sp = mContext.getSharedPreferences(NAME, mContext.MODE_PRIVATE);
        sp.edit().putBoolean(key, value).commit();
    }

    // defValue是默认值，如果sharedPreferences
    public static Boolean getBoolean(Context mContext, String key, boolean defValue) {
        SharedPreferences sp = mContext.getSharedPreferences(NAME, mContext.MODE_PRIVATE);
        return sp.getBoolean(key, defValue);
    }

    // 删除 单个
    public static void deleShare(Context mContext, String key) {
        SharedPreferences sp = mContext.getSharedPreferences(NAME, mContext.MODE_PRIVATE);
        sp.edit().remove(key).commit();
    }

    // 删除 全部
    public static void deleAll(Context mContext) {
        SharedPreferences sp = mContext.getSharedPreferences(NAME, mContext.MODE_PRIVATE);
        sp.edit().clear().commit();
    }
}
