package com.example.xuqi.qqdemo;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.example.xuqi.qqdemo.Sinaapi.AccessTokenKeeper;
import com.example.xuqi.qqdemo.Sinaapi.LogoutAPI;
import com.example.xuqi.qqdemo.Sinaapi.UsersAPI;
import com.example.xuqi.qqdemo.Sinaapi.Util;
import com.sina.weibo.sdk.auth.AuthInfo;
import com.sina.weibo.sdk.auth.Oauth2AccessToken;
import com.sina.weibo.sdk.auth.WeiboAuthListener;
import com.sina.weibo.sdk.auth.sso.SsoHandler;
import com.sina.weibo.sdk.exception.WeiboException;
import com.sina.weibo.sdk.net.RequestListener;
import com.tencent.connect.UserInfo;
import com.tencent.connect.common.Constants;
import com.tencent.mm.opensdk.modelmsg.SendMessageToWX;
import com.tencent.mm.opensdk.modelmsg.WXMediaMessage;
import com.tencent.mm.opensdk.modelmsg.WXWebpageObject;
import com.tencent.mm.opensdk.openapi.IWXAPI;
import com.tencent.mm.opensdk.openapi.WXAPIFactory;
import com.tencent.tauth.IUiListener;
import com.tencent.tauth.Tencent;
import com.tencent.tauth.UiError;

import org.json.JSONException;
import org.json.JSONObject;


public class MainActivity extends AppCompatActivity implements View.OnClickListener {
    private Button QQbutton, Sinabutton;
    private Oauth2AccessToken mAccessToken;
    private UsersAPI mUsersAPI;
    public static Tencent mTencent;
    public static IWXAPI wxapi;
    private SsoHandler mSsoHandler;
    public static final String AppId = "1105896371";
    private static final String WX_APPId = "wx88888888";
    private String type = "";
    private static final String SCOPE = "get_simple_userinfo,get_user_info,get_user_profile,get_app_friends,add_share,get_idollist,add_topic,list_album,upload_pic,add_album,add_t,add_pic_t";//
    public IUiListener loginListener = new IUiListener() {
        @Override
        public void onComplete(Object o) {
            //登录成功后回调该方法,可以跳转相关的页面
            Toast.makeText(MainActivity.this, "登录成功", Toast.LENGTH_SHORT).show();
            JSONObject object = (JSONObject) o;
            try {
                String accessToken = object.getString("access_token");
                String expires = object.getString("expires_in");
                String openID = object.getString("openid");
                mTencent.setAccessToken(accessToken, expires);
                mTencent.setOpenId(openID);
                QQbutton.setText("注销");
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }

        public void onError(UiError uiError) {
            Toast.makeText(MainActivity.this, "ERROR", Toast.LENGTH_SHORT).show();
        }

        public void onCancel() {
            Toast.makeText(MainActivity.this, "CANCEL", Toast.LENGTH_SHORT).show();
        }

    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Log.d("xuqi", "onCreate: " + getPackageName());
        QQbutton = (Button) findViewById(R.id.QQ_login_button);
        Sinabutton = (Button) findViewById(R.id.Sina_login_button);
        QQbutton.setOnClickListener(this);
        Sinabutton.setOnClickListener(this);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (type.equals("QQ")) {
            if (requestCode == Constants.REQUEST_LOGIN) {
                if (resultCode == -1) {
                    Tencent.onActivityResultData(requestCode, resultCode, data, loginListener);
                    UserInfo info = new UserInfo(this, mTencent.getQQToken());
                    info.getUserInfo(new IUiListener() {
                        @Override
                        public void onComplete(Object o) {
                            try {
                                JSONObject info = (JSONObject) o;
                                String nickName = info.getString("nickname");//获取用户昵称
                                String iconUrl = info.getString("figureurl_qq_2");//获取用户头像的url
                                Toast.makeText(MainActivity.this, "昵称：" + nickName, Toast.LENGTH_SHORT).show();
                                UserInfoActivity.showActivity(MainActivity.this, nickName, iconUrl, type);
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                        }

                        @Override

                        public void onError(UiError uiError) {

                        }

                        @Override
                        public void onCancel() {
                        }
                    });
                }
            }
        } else if (type.equals("Sina")) {
            super.onActivityResult(requestCode, resultCode, data);

            // SSO 授权回调
            // 重要：发起 SSO 登陆的 Activity 必须重写 onActivityResults
            if (mSsoHandler != null) {
                mSsoHandler.authorizeCallBack(requestCode, resultCode, data);
            }
        }
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.QQ_login_button:
                type = "QQ";
                if(QQbutton.getText().equals("QQ"))
                loginQQ();
                else{
                    mTencent.logout(MainActivity.this);
                    QQbutton.setText("QQ");
                }
                break;
            case R.id.Sina_login_button:
                type = "Sina";
                if (Sinabutton.getText().equals("SINA"))
                    loginSina();
                else {
                    new LogoutAPI(MainActivity.this, com.example.xuqi.qqdemo.Constants.SINA_APP_KEY,
                            AccessTokenKeeper.readAccessToken(MainActivity.this)).logout(new LogOutRequestListener());
                    Sinabutton.setText("SINA");
                }
                break;
            default:
                break;
        }
    }

    //微博登录
    private void loginSina() {
        // 微博授权
        AuthInfo mAuthInfo = new AuthInfo(this, com.example.xuqi.qqdemo.Constants.SINA_APP_KEY, com.example.xuqi.qqdemo.Constants.SINA_REDIRECT_URL, com.example.xuqi.qqdemo.Constants.SINA_SCOPE);
        mSsoHandler = new SsoHandler(this, mAuthInfo);
        mSsoHandler.authorize(new AuthListener());
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

    private void loginQQ() {
        mTencent = Tencent.createInstance(AppId, this.getApplicationContext());
        if (!mTencent.isSessionValid()) {
            mTencent.login(this, SCOPE, loginListener);
        }
    }

    // Sina回调
    class AuthListener implements WeiboAuthListener {

        @Override
        public void onComplete(Bundle values) {
            // 从 Bundle 中解析 Token
            mAccessToken = Oauth2AccessToken.parseAccessToken(values);
            //从这里获取用户输入的 电话号码信息
            String phoneNum = mAccessToken.getPhoneNum();
            Log.d("xuqi", "onComplete: phone = "+phoneNum);
            if (mAccessToken.isSessionValid()) {
                // 保存 Token 到 SharedPreferences
                AccessTokenKeeper.writeAccessToken(MainActivity.this, mAccessToken);
                Toast.makeText(MainActivity.this,
                        R.string.weibosdk_demo_toast_auth_success, Toast.LENGTH_SHORT).show();
                //获取用户信息
                mUsersAPI = new UsersAPI(MainActivity.this, com.example.xuqi.qqdemo.Constants.SINA_APP_KEY, mAccessToken);
                long uid = Long.parseLong(mAccessToken.getUid());
                mUsersAPI.show(uid, mListener);
                Sinabutton.setText("注销");
            } else {
                // 以下几种情况，您会收到 Code：
                // 1. 当您未在平台上注册的应用程序的包名与签名时；
                // 2. 当您注册的应用程序包名与签名不正确时；
                // 3. 当您在平台上注册的包名和签名与您当前测试的应用的包名和签名不匹配时。
                String code = values.getString("code");
                String message = getString(R.string.weibosdk_demo_toast_auth_failed);
                if (!TextUtils.isEmpty(code)) {
                    message = message + "\nObtained the code: " + code;
                }
                Toast.makeText(MainActivity.this, message, Toast.LENGTH_LONG).show();
            }
        }

        @Override
        public void onCancel() {
            Toast.makeText(MainActivity.this,
                    R.string.weibosdk_demo_toast_auth_canceled, Toast.LENGTH_LONG).show();
        }

        @Override
        public void onWeiboException(WeiboException e) {
            Toast.makeText(MainActivity.this,
                    "Auth exception : " + e.getMessage(), Toast.LENGTH_LONG).show();
        }
    }

    /**
     * 微博 OpenAPI 回调接口。（获取信息）
     */
    private RequestListener mListener = new RequestListener() {
        @Override
        public void onComplete(String response) {
            if (!TextUtils.isEmpty(response)) {
                //LogUtil.i(TAG, response);User.java
                // 调用 User#parse 将JSON串解析成User对象
                try {
                    JSONObject json = new JSONObject(response);
                    if (json != null) {
//                        Toast.makeText(MainActivity.this,
//                                "获取User信息成功，用户昵称：" + json.getString("screen_name"),
//                                Toast.LENGTH_LONG).show();
                        String nickName = json.getString("screen_name");//获取用户昵称
                        String iconUrl = json.getString("profile_image_url");//获取用户头像的url
                        Toast.makeText(MainActivity.this, "昵称：" + nickName, Toast.LENGTH_SHORT).show();
                        UserInfoActivity.showActivity(MainActivity.this, nickName, iconUrl, type);
                    } else {
                        Toast.makeText(MainActivity.this, response, Toast.LENGTH_LONG).show();
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }

            }
        }

        @Override
        public void onWeiboException(WeiboException e) {
            //LogUtil.e(TAG, e.getMessage());
//            ErrorInfo info = ErrorInfo.parse(e.getMessage());
//            Toast.makeText(MainActivity.this, info.toString(), Toast.LENGTH_LONG).show();
            Toast.makeText(MainActivity.this, "获取信息错误", Toast.LENGTH_SHORT).show();
        }
    };

    /**
     * 登出按钮的监听器，接收登出处理结果。（API 请求结果的监听器）
     */
    private class LogOutRequestListener implements RequestListener {
        @Override
        public void onComplete(String response) {
            if (!TextUtils.isEmpty(response)) {
                try {
                    JSONObject obj = new JSONObject(response);
                    String value = obj.getString("result");

                    if ("true".equalsIgnoreCase(value)) {
                        AccessTokenKeeper.clear(MainActivity.this);
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }

        @Override
        public void onWeiboException(WeiboException e) {
        }
    }
}
