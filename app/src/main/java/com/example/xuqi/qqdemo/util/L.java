package com.example.xuqi.qqdemo.util;

import android.util.Log;

/**
 * Created by xuqi on 17/3/23.
 */


//封装Log工具类
public class L {
    // 开关
    public static final boolean DEBUG = true;
    // TAG
    public static final String TAG = "QQdemo";

    //五个等级 DIWE
    public static void d(String text) {
        if (DEBUG) {
            Log.d(TAG, "xuqi"+text);
        }
    }

    public static void i(String text) {
        if (DEBUG) {
            Log.d(TAG, text);
        }
    }

    public static void w(String text) {
        if (DEBUG) {
            Log.d(TAG, text);
        }
    }

    public static void e(String text) {
        if (DEBUG) {
            Log.d(TAG, text);
        }
    }
}
