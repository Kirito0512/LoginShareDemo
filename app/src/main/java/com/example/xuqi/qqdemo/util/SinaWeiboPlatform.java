package com.example.xuqi.qqdemo.util;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.widget.Toast;

import com.example.xuqi.qqdemo.Constants;
import com.example.xuqi.qqdemo.view.MainActivity;
import com.example.xuqi.qqdemo.R;
import com.example.xuqi.qqdemo.Sinaapi.AccessTokenKeeper;
import com.example.xuqi.qqdemo.Sinaapi.ErrorInfo;
import com.example.xuqi.qqdemo.Sinaapi.LogoutAPI;
import com.example.xuqi.qqdemo.Sinaapi.Status;
import com.example.xuqi.qqdemo.Sinaapi.StatusList;
import com.example.xuqi.qqdemo.Sinaapi.StatusesAPI;
import com.example.xuqi.qqdemo.Sinaapi.UsersAPI;
import com.example.xuqi.qqdemo.Sinaapi.WBShareToMessageFriendActivity;
import com.example.xuqi.qqdemo.bean.NewsUserInfo;
import com.sina.weibo.sdk.auth.AuthInfo;
import com.sina.weibo.sdk.auth.Oauth2AccessToken;
import com.sina.weibo.sdk.auth.WeiboAuthListener;
import com.sina.weibo.sdk.auth.sso.SsoHandler;
import com.sina.weibo.sdk.exception.WeiboException;
import com.sina.weibo.sdk.net.RequestListener;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by xuqi on 17/2/8.
 */

public class SinaWeiboPlatform extends Platform {
    private SsoHandler mSsoHandler;
    private Oauth2AccessToken mAccessToken;
    private Activity activity;
    private UsersAPI mUsersAPI;

    public SinaWeiboPlatform(Activity activity) {
        this.activity = activity;
    }


    @Override
    public void authrize(final Activity activity) {
        try {
            this.activity = activity;
            listener.onStart(SinaWeiboPlatform.this,Platform.Sina);
            // 创建微博授权类对象
            AuthInfo mAuthInfo = new AuthInfo(activity, Constants.SINA_APP_KEY, Constants.SINA_REDIRECT_URL, Constants.SINA_SCOPE);
            // 创建SsoHandler对象
            mSsoHandler = new SsoHandler(activity, mAuthInfo);
            mSsoHandler.authorize(new AuthListener());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void logout(Activity activity) {
        this.activity = activity;
        new LogoutAPI(activity, com.example.xuqi.qqdemo.Constants.SINA_APP_KEY,
                AccessTokenKeeper.readAccessToken(activity)).logout(new LogOutRequestListener());
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        // SSO 授权回调
        // 重要：发起 SSO 登陆的 Activity 必须重写 onActivityResults
        if (mSsoHandler != null) {
            mSsoHandler.authorizeCallBack(requestCode, resultCode, data);
        }
    }

    //Sina微博分享
    /*
        upload方法分享本地图片
         uploadUrlText方法分享网络图片，需要更高的权限
         update方法只分享文字
         content参数里面可以加上url,会自动生成 网页链接
     */
    public void shareToSina(Activity activity) {
        this.activity = activity;
        Oauth2AccessToken accessToken = AccessTokenKeeper.readAccessToken(activity);
        StatusesAPI mStatusesAPI = new StatusesAPI(activity, Constants.SINA_APP_KEY, accessToken);
//        Drawable drawable = activity.getResources().getDrawable(R.drawable.ic_com_sina_weibo_sdk_logo);
//        Bitmap bitmap = ((BitmapDrawable) drawable).getBitmap();
        Bitmap bitmap = BitmapFactory.decodeResource(activity.getResources(),R.drawable.ic_com_sina_weibo_sdk_logo);
        String content = "挑剔如我赶脚【雪:儿】棒棒哒！请吃下我这颗安利https://mars.changba.com/s/VnJU8DRVBwgemAmjV7RWYeSHuf2korNz?&code=DRuXOvems0Fv3q5j76knjtxHbJfwWq5JK8_m_yEyLigkvf6jEx9lfITc7wSSA--7";
        String imageUrl = "http://aliimg.changbalive.com/photo/154/882419ce25136b67_200_200.jpg";
        mStatusesAPI.upload(content, bitmap, null, null, mmListener);
        //mStatusesAPI.update(content,null,null,mListener);
    }

    public void shareToSinafriend(Activity activity){
        activity.startActivity(new Intent(activity, WBShareToMessageFriendActivity.class));
    }

    /*＊
    ＊Sina登录回调
    */
    private class AuthListener implements WeiboAuthListener {

        @Override
        public void onComplete(Bundle values) {
            // 从 Bundle 中解析 Token
            mAccessToken = Oauth2AccessToken.parseAccessToken(values);
            //从这里获取用户输入的 电话号码信息
            String phoneNum = mAccessToken.getPhoneNum();
            Log.d("xuqi", "onComplete: phone = " + phoneNum);
            if (mAccessToken.isSessionValid()) {
                // 保存 Token 到 SharedPreferences
                AccessTokenKeeper.writeAccessToken(activity, mAccessToken);
                Toast.makeText(activity,
                        R.string.weibosdk_demo_toast_auth_success, Toast.LENGTH_SHORT).show();
                //获取用户信息
                mUsersAPI = new UsersAPI(activity, com.example.xuqi.qqdemo.Constants.SINA_APP_KEY, mAccessToken);
                long uid = Long.parseLong(mAccessToken.getUid());
                mUsersAPI.show(uid, mListener);
                listener.onComplete(SinaWeiboPlatform.this, mAccessToken, Platform.Sina);
            } else {
                // 以下几种情况，您会收到 Code：
                // 1. 当您未在平台上注册的应用程序的包名与签名时；
                // 2. 当您注册的应用程序包名与签名不正确时；
                // 3. 当您在平台上注册的包名和签名与您当前测试的应用的包名和签名不匹配时。
                String code = values.getString("code");
                String message = activity.getString(R.string.weibosdk_demo_toast_auth_failed);
                if (!TextUtils.isEmpty(code)) {
                    message = message + "\nObtained the code: " + code;
                }
                Toast.makeText(activity, message, Toast.LENGTH_LONG).show();
            }
            listener.onComplete(SinaWeiboPlatform.this,mAccessToken,Platform.Sina);
        }

        @Override
        public void onCancel() {
            Toast.makeText(activity,
                    R.string.weibosdk_demo_toast_auth_canceled, Toast.LENGTH_LONG).show();
        }

        @Override
        public void onWeiboException(WeiboException e) {
            Toast.makeText(activity,
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
                //LogUtil.i(TAG, response)
                // 调用 User#parse 将JSON串解析成User对象
                try {
                    JSONObject json = new JSONObject(response);
                    if (json != null) {
                        String nickName = json.getString("screen_name");//获取用户昵称
                        String iconUrl = json.getString("profile_image_url");//获取用户头像的url
                        Toast.makeText(activity, "昵称：" + nickName, Toast.LENGTH_SHORT).show();
                        NewsUserInfo user = new NewsUserInfo();
                        user.setUserId(10001);
                        user.setName(nickName);
                        user.setHeadPhoto(iconUrl);
                        user.setSource("SINA");
                        UserSessionManager.setCurrentUser(user);
                        MainActivity.showActivity(activity);
                    } else {
                        Toast.makeText(activity, response, Toast.LENGTH_LONG).show();
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
            Toast.makeText(activity, "获取信息错误", Toast.LENGTH_SHORT).show();
        }
    };

    /**
     * 微博 OpenAPI 回调接口。(无需打开微博，分享动态)
     */
    private RequestListener mmListener = new RequestListener() {
        @Override
        public void onComplete(String response) {
            if (!TextUtils.isEmpty(response)) {
                if (response.startsWith("{\"statuses\"")) {
                    // 调用 StatusList#parse 解析字符串成微博列表对象
                    StatusList statuses = StatusList.parse(response);
                    if (statuses != null && statuses.total_number > 0) {
                        Toast.makeText(activity,
                                "获取微博信息流成功, 条数: " + statuses.statusList.size(),
                                Toast.LENGTH_LONG).show();
                    }
                } else if (response.startsWith("{\"created_at\"")) {
                    // 调用 Status#parse 解析字符串成微博对象
                    Status status = Status.parse(response);
                    Toast.makeText(activity,
                            "发送一送微博成功, id = " + status.id,
                            Toast.LENGTH_LONG).show();
                } else {
                    Toast.makeText(activity, response, Toast.LENGTH_LONG).show();
                }
            }
        }

        @Override
        public void onWeiboException(WeiboException e) {
            ErrorInfo info = ErrorInfo.parse(e.getMessage());
            Toast.makeText(activity, info.toString(), Toast.LENGTH_LONG).show();
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
                        AccessTokenKeeper.clear(activity);
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
