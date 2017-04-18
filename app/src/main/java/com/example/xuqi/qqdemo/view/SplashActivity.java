package com.example.xuqi.qqdemo.view;

import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.widget.TextView;

import com.example.xuqi.qqdemo.Constants;
import com.example.xuqi.qqdemo.R;
import com.example.xuqi.qqdemo.util.ShareUtils;
import com.example.xuqi.qqdemo.util.UtilTools;

import static com.example.xuqi.qqdemo.Constants.HANDLER_SPLASH;

public class SplashActivity extends BaseActivity {

    /**
     * 1.延时2000ms
     * 2.判断程序是否第一次运行
     * 3.自定义字体
     * 4.Activity全屏主题
     *
     * @param savedInstanceState
     */

    private TextView tv_splash;

    private Handler handler = new Handler() {
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                case HANDLER_SPLASH:
                    // 判断程序是否是第一次运行
                    if (isFirst()) {
                        // guideActivity
                        showActivity(GuidActivity.class);
                    } else {
                        // 不是第一次运行，进入MainActivity
                        showActivity(MainActivity.class);
                    }
                    finish();
                    break;
            }
        }
    };

    // 判断程序是否是第一次运行
    private boolean isFirst() {
        boolean isFirst = ShareUtils.getBoolean(this, Constants.SHARE_IS_FIRST, true);
        if (isFirst) {
            ShareUtils.putBoolean(this, Constants.SHARE_IS_FIRST, false);
            //是第一次运行
            return true;
        } else {
            return false;
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);
        initView();
    }

    private void initView() {
        handler.sendEmptyMessageDelayed(HANDLER_SPLASH, 2000);
        tv_splash = (TextView) findViewById(R.id.tv_splash);
        UtilTools.setFont(this, tv_splash);
    }

    // 重写返回键处理方法
    // 注释掉处理方法
    @Override
    public void onBackPressed() {
        //super.onBackPressed();
    }
}
