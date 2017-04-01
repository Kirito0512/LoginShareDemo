package com.example.xuqi.qqdemo.application;

import android.app.Application;

import com.example.xuqi.qqdemo.Constants;
import com.tencent.bugly.crashreport.CrashReport;

import cn.bmob.sms.BmobSMS;
import cn.bmob.v3.Bmob;

import static com.example.xuqi.qqdemo.Constants.Bmob_Application_Id;

/**
 * Created by xuqi on 17/3/22.
 */

public class BaseApplication extends Application {
    @Override
    public void onCreate() {
        super.onCreate();
        // 初始化Bmob
        Bmob.initialize(this, Bmob_Application_Id);
        // Dmob短信服务
        BmobSMS.initialize(this,Bmob_Application_Id);
        // 初始化Bugly
        CrashReport.initCrashReport(getApplicationContext(), Constants.BUGLY_APP_ID, true);

    }
}