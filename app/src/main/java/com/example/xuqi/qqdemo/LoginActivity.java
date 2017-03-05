package com.example.xuqi.qqdemo;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.example.xuqi.qqdemo.Sinaapi.Util;
import com.example.xuqi.qqdemo.util.Platform;
import com.example.xuqi.qqdemo.util.PlatformActionListener;
import com.example.xuqi.qqdemo.util.SinaWeiboPlatform;
import com.example.xuqi.qqdemo.util.TencentPlatform;
import com.example.xuqi.qqdemo.widget.LoadingDialog;
import com.tencent.mm.opensdk.modelmsg.SendMessageToWX;
import com.tencent.mm.opensdk.modelmsg.WXMediaMessage;
import com.tencent.mm.opensdk.modelmsg.WXWebpageObject;
import com.tencent.mm.opensdk.openapi.IWXAPI;
import com.tencent.mm.opensdk.openapi.WXAPIFactory;

import static com.example.xuqi.qqdemo.Constants.WX_APPId;

public class LoginActivity extends AppCompatActivity implements View.OnClickListener {
    private TextView QQbutton, Sinabutton;
    public static IWXAPI wxapi;
    private String type = "";
    private TencentPlatform mTencentPlatform;
    private SinaWeiboPlatform mSinaWeiboPlatform;
    private LoadingDialog mProgressDialog;
    public static final String Bmob_Application_Id = "3f6b441de3147f470ca306a4f4bf0a23";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.register_activity_login);
        initView();
    }

    private void initView() {
        mTencentPlatform = new TencentPlatform(LoginActivity.this);
        mSinaWeiboPlatform = new SinaWeiboPlatform(this);
        QQbutton = (TextView) findViewById(R.id.QQ_login_button);
        Sinabutton = (TextView) findViewById(R.id.Sina_login_button);
        QQbutton.setOnClickListener(this);
        Sinabutton.setOnClickListener(this);
        mProgressDialog = new LoadingDialog(LoginActivity.this);
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (type.equals("QQ")) {
            if (requestCode == com.tencent.connect.common.Constants.REQUEST_LOGIN) {
                if (resultCode == -1) {
                    mTencentPlatform.onActivityResult(requestCode, resultCode, data);
                }
            }
        } else if (type.equals("Sina")) {
            super.onActivityResult(requestCode, resultCode, data);
            mSinaWeiboPlatform.onActivityResult(requestCode, resultCode, data);
        }
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.QQ_login_button:
                type = "QQ";
                if (QQbutton.getText().equals("QQ")) {
                    mTencentPlatform.setPlatformActionListener(mPlatformActionListener).authrize(this);
                } else {
                    mTencentPlatform.logout(this);
                    QQbutton.setText("QQ");
                }
                break;
            case R.id.Sina_login_button:
                type = "Sina";
                if (Sinabutton.getText().equals("SINA"))
                    mSinaWeiboPlatform.setPlatformActionListener(mPlatformActionListener).authrize(this);
                else {
                    mSinaWeiboPlatform.logout(this);
                    Sinabutton.setText("SINA");
                }
                break;
            default:
                break;
        }
    }

    public void registeToWx() {
        wxapi = WXAPIFactory.createWXAPI(this, WX_APPId, true);
        //将应用的appid注册到微信
        wxapi.registerApp(WX_APPId);
    }

    private void shareToWXSNS() {
        if (wxapi == null)
            registeToWx();
        WXWebpageObject webpage = new WXWebpageObject();
        webpage.webpageUrl = "http://changba.com";

        //用WXWebpageMessageObject对象初始化一个WXMediaMessage对象，填写标题，描述
        WXMediaMessage msg = new WXMediaMessage(webpage);
        msg.title = "标题 唱吧";
        msg.description = "内容描述";
        Bitmap thumb = BitmapFactory.decodeResource(getResources(), R.drawable.send_music_thumb);
        //TODO
        msg.thumbData = Util.bmpToByteArray(thumb, true);
        //构造一个Req
        SendMessageToWX.Req req = new SendMessageToWX.Req();
        //transaction字段用于唯一标示一个请求
        req.transaction = String.valueOf(System.currentTimeMillis());
        Log.d("xuqi", "shareToWX: " + System.currentTimeMillis());
        req.message = msg;
        req.scene = SendMessageToWX.Req.WXSceneTimeline;
        // 调用api接口发送数据到微信
        wxapi.sendReq(req);
    }

    private void shareToWX() {
        if (wxapi == null)
            registeToWx();
        WXWebpageObject webpage = new WXWebpageObject();
        webpage.webpageUrl = "http://changba.com";

        //用WXWebpageMessageObject对象初始化一个WXMediaMessage对象，填写标题，描述
        WXMediaMessage msg = new WXMediaMessage(webpage);
        msg.title = "标题 唱吧";
        msg.description = "内容描述";
        Bitmap thumb = BitmapFactory.decodeResource(getResources(), R.drawable.send_music_thumb);
        //TODO
        msg.thumbData = Util.bmpToByteArray(thumb, true);
        //构造一个Req
        SendMessageToWX.Req req = new SendMessageToWX.Req();
        //transaction字段用于唯一标示一个请求
        req.transaction = String.valueOf(System.currentTimeMillis());
        Log.d("xuqi", "shareToWX: " + System.currentTimeMillis());
        req.message = msg;
        req.scene = SendMessageToWX.Req.WXSceneSession;
        // 调用api接口发送数据到微信
        wxapi.sendReq(req);
    }

    private PlatformActionListener mPlatformActionListener = new PlatformActionListener() {
        @Override
        public void onStart(Platform platform, int type) {
            LoadingDialog.showProgressDialog("正在登录", LoginActivity.this);
            if (type == Platform.QQ) {
                //showLoadingDialog(getString(R.string.register_login_loging));
            } else if (type == Platform.Sina) {

            }
        }

        @Override
        public void onComplete(Platform platform, Object obj, int kind) {
            LoadingDialog.hideProgressDialog(LoginActivity.this);
            Toast.makeText(LoginActivity.this, "登录成功", Toast.LENGTH_SHORT).show();
            if (kind == Platform.QQ) {
                if (obj != null) {
                    QQbutton.setText("注销");
                }
            } else if (kind == Platform.Sina) {
                Sinabutton.setText("注销");
            }
        }

        @Override
        public void onError(Platform platform, Throwable paramThrowable) {
//            hideLoadingDialog();
            Toast.makeText(LoginActivity.this, "ERROR", Toast.LENGTH_SHORT).show();
        }

        @Override
        public void onCancel(Platform platform) {
//            hideLoadingDialog();
            Toast.makeText(LoginActivity.this, "CANCEL", Toast.LENGTH_SHORT).show();
        }
    };


}
