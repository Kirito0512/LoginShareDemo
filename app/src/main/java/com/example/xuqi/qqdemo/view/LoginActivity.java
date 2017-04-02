package com.example.xuqi.qqdemo.view;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.example.xuqi.qqdemo.R;
import com.example.xuqi.qqdemo.Sinaapi.Util;
import com.example.xuqi.qqdemo.bean.NewsUser;
import com.example.xuqi.qqdemo.util.L;
import com.example.xuqi.qqdemo.util.Platform;
import com.example.xuqi.qqdemo.util.PlatformActionListener;
import com.example.xuqi.qqdemo.util.SinaWeiboPlatform;
import com.example.xuqi.qqdemo.util.SnackbarUtil;
import com.example.xuqi.qqdemo.util.TencentPlatform;
import com.example.xuqi.qqdemo.widget.MyLoadingDialog;
import com.tencent.mm.opensdk.modelmsg.SendMessageToWX;
import com.tencent.mm.opensdk.modelmsg.WXMediaMessage;
import com.tencent.mm.opensdk.modelmsg.WXWebpageObject;
import com.tencent.mm.opensdk.openapi.IWXAPI;
import com.tencent.mm.opensdk.openapi.WXAPIFactory;

import cn.bmob.sms.BmobSMS;
import cn.bmob.sms.exception.BmobException;
import cn.bmob.sms.listener.RequestSMSCodeListener;
import cn.bmob.v3.listener.SaveListener;

import static com.example.xuqi.qqdemo.Constants.WX_APPId;

public class LoginActivity extends AppCompatActivity implements View.OnClickListener {
    private TextView QQbutton, Sinabutton;
    public static IWXAPI wxapi;
    private String type = "";
    public static TencentPlatform mTencentPlatform;
    public static SinaWeiboPlatform mSinaWeiboPlatform;
    private MyLoadingDialog mProgressDialog;
    private EditText et_phone, et_vercode;
    private TextView tv_send;
    private Button btn_login;
    private com.example.xuqi.qqdemo.view.LoadingDialog dialog;

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
        mProgressDialog = new MyLoadingDialog(LoginActivity.this);
        et_phone = (EditText) findViewById(R.id.register_login_phone_et);
        et_vercode = (EditText) findViewById(R.id.register_login_vercode_et);
        tv_send = (TextView) findViewById(R.id.request_vercode);
        tv_send.setOnClickListener(this);
        btn_login = (Button) findViewById(R.id.register_login_phone_btn);
        btn_login.setOnClickListener(this);
        dialog = new LoadingDialog(this);
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
        final String ph_number = et_phone.getText().toString();
        final String ph_vercode = et_vercode.getText().toString();
        switch (view.getId()) {
            case R.id.QQ_login_button:
                type = "QQ";
                if (QQbutton.getText().equals("QQ"))
                    mTencentPlatform.setPlatformActionListener(mPlatformActionListener).authrize(this);
                break;
            case R.id.Sina_login_button:
                type = "Sina";
                if (Sinabutton.getText().equals("SINA"))
                    mSinaWeiboPlatform.setPlatformActionListener(mPlatformActionListener).authrize(this);
                break;
            case R.id.request_vercode:
                if (ph_number == null && ph_number.length() != 11) {
                    SnackbarUtil.show(view, "请输入手机号", 0);
                } else {
                    L.d("电话号码正确");
                    //进行获取验证码和倒计时1分钟操作
                    // "SmsDemo"为短信模版名称
                    BmobSMS.requestSMSCode(this, ph_number, "SmsDemo", new RequestSMSCodeListener() {
                        @Override
                        public void done(Integer integer, BmobException e) {
                            if (e == null) {
                                // 发送成功时，让获取验证码按钮不可点击，且为灰色
                                tv_send.setClickable(false);
                                tv_send.setBackgroundColor(Color.GRAY);
                                Toast.makeText(LoginActivity.this, "验证码发送成功，请尽快使用", Toast.LENGTH_SHORT).show();
                                /**
                                 * 倒计时一分钟的操作
                                 *
                                 */
                                new CountDownTimer(6000, 1000) {
                                    @Override
                                    public void onTick(long millisUntilFinished) {
                                        tv_send.setText(millisUntilFinished / 1000 + "秒");
                                    }

                                    @Override
                                    public void onFinish() {
                                        tv_send.setClickable(true);
                                        tv_send.setText("重新发送");
                                    }
                                }.start();
                            } else {
                                L.d("xuqi  " + e.toString());
                                Toast.makeText(LoginActivity.this, "验证码发送失败", Toast.LENGTH_SHORT).show();
                            }
                        }
                    });
                }
                break;
            case R.id.register_login_phone_btn:
                if (ph_number.length() == 0 || ph_number.length() != 11 || ph_vercode.length() == 0) {
                    Toast.makeText(LoginActivity.this, "不合法输入", Toast.LENGTH_SHORT).show();
                } else {
                    // Bmob手机号码一键注册或登录
                    dialog.setCancelable(false);
                    dialog.show();
                    NewsUser user = new NewsUser();
                    user.setMobilePhoneNumber(ph_number);
                    user.setUsername("董小姐" + ph_number);
                    user.setPassword("666666");
                    user.signOrLogin(ph_vercode, new SaveListener<NewsUser>() {
                        @Override
                        public void done(NewsUser bmobUser, cn.bmob.v3.exception.BmobException e) {
                            if (e == null) {
                                dialog.dismiss();
                                Toast.makeText(LoginActivity.this, "注册成功", Toast.LENGTH_SHORT).show();
                            } else {
                                Toast.makeText(LoginActivity.this, "注册失败", Toast.LENGTH_SHORT).show();
                            }
                        }
                    });
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
            MyLoadingDialog.showProgressDialog("正在登录", LoginActivity.this);
            if (type == Platform.QQ) {
                //showLoadingDialog(getString(R.string.register_login_loging));
            } else if (type == Platform.Sina) {

            }
        }

        @Override
        public void onComplete(Platform platform, Object obj, int kind) {
            MyLoadingDialog.hideProgressDialog(LoginActivity.this);
            Toast.makeText(LoginActivity.this, "登录成功", Toast.LENGTH_SHORT).show();
//            if (kind == Platform.QQ) {
//                if (obj != null) {
//                    QQbutton.setText("注销");
//                }
//            } else if (kind == Platform.Sina) {
//                Sinabutton.setText("注销");
//            }
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
