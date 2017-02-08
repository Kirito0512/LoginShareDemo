package com.example.xuqi.qqdemo.util;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.widget.Toast;

import com.example.xuqi.qqdemo.R;
import com.example.xuqi.qqdemo.UserInfoActivity;
import com.tencent.connect.UserInfo;
import com.tencent.connect.share.QQShare;
import com.tencent.connect.share.QzoneShare;
import com.tencent.tauth.IUiListener;
import com.tencent.tauth.Tencent;
import com.tencent.tauth.UiError;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

import static com.example.xuqi.qqdemo.Constants.QQ_AppId;
import static com.example.xuqi.qqdemo.Constants.QQ_SCOPE;
import static com.tencent.connect.share.QzoneShare.SHARE_TO_QZONE_TYPE_IMAGE_TEXT;

/**
 * Created by xuqi on 17/2/8.
 */

public class TencentPlatform extends Platform {
    protected Tencent mTencent;
    private Activity activity;
    private Context mContext;
    public TencentPlatform(Context mContext) {
        mTencent = Tencent.createInstance(QQ_AppId, mContext);
    }

    @Override
    public void authrize(Activity activity) {
        this.activity = activity;
        mTencent.logout(activity);
        mTencent.login(activity,QQ_SCOPE,loginListener);
    }

    public void logout(Activity activity){
        mTencent.logout(activity);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        Tencent.onActivityResultData(requestCode, resultCode, data, loginListener);
    }

    public void shareToQQ(Activity activity) {
        this.activity = activity;
        Bundle bundle = new Bundle();
        bundle.putString(QQShare.SHARE_TO_QQ_TITLE, "火星");
        bundle.putString(QQShare.SHARE_TO_QQ_TARGET_URL, "http://changba.com?");
        bundle.putString(QQShare.SHARE_TO_QQ_IMAGE_URL, "http://imgcache.qq.com/qzone/space_item/pre/0/66768.gif");
        bundle.putString(QQShare.SHARE_TO_QQ_SUMMARY, "大家快来一起玩吧！");

        // qq 分享必须设置分享的类型,SHARE_TO_QQ_TYPE_DEFAULT为图文混排默认类型.
        bundle.putInt(com.tencent.connect.share.QQShare.SHARE_TO_QQ_KEY_TYPE, com.tencent.connect.share.QQShare.SHARE_TO_QQ_TYPE_DEFAULT);
        mTencent.shareToQQ(activity, bundle, shareListener);
    }


    public void shareToQzone(Activity activity) {
        this.activity = activity;
        Bundle params = new Bundle();
        //分享类型
        params.putInt(QzoneShare.SHARE_TO_QZONE_KEY_TYPE, SHARE_TO_QZONE_TYPE_IMAGE_TEXT);
        params.putString(QzoneShare.SHARE_TO_QQ_TITLE, "标题");//必填
        params.putString(QzoneShare.SHARE_TO_QQ_SUMMARY, "摘要");//选填
        params.putString(QzoneShare.SHARE_TO_QQ_TARGET_URL, "http://changba.com?");//必填
        params.putStringArrayList(QzoneShare.SHARE_TO_QQ_IMAGE_URL,
                new ArrayList<String>() {{
                    add("http://imgcache.qq.com/qzone/space_item/pre/0/66768.gif");
                }});
        mTencent.shareToQzone(activity, params, shareListener);
    }

    // QQ登录回调
    public IUiListener loginListener = new IUiListener() {
        @Override
        public void onComplete(Object o) {
            //登录成功后回调该方法,可以跳转相关的页面
//            Toast.makeText(MainActivity.this, "登录成功", Toast.LENGTH_SHORT).show();
            JSONObject object = (JSONObject) o;
            try {
                String accessToken = object.getString("access_token");
                String expires = object.getString("expires_in");
                String openID = object.getString("openid");
                mTencent.setAccessToken(accessToken, expires);
                mTencent.setOpenId(openID);
                UserInfo info = new UserInfo(activity, mTencent.getQQToken());
                // 获取用户信息
                info.getUserInfo(new IUiListener() {
                    @Override
                    public void onComplete(Object o) {
                        try {
                            JSONObject info = (JSONObject) o;
                            String nickName = info.getString("nickname");//获取用户昵称
                            String iconUrl = info.getString("figureurl_qq_2");//获取用户头像的url
                            Toast.makeText(activity, "昵称：" + nickName, Toast.LENGTH_SHORT).show();
                            UserInfoActivity.showActivity(activity, nickName, iconUrl, "QQ");
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
                if (null != listener) {
                    listener.onComplete(TencentPlatform.this, accessToken,Platform.QQ);
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }

        public void onError(UiError uiError) {
            listener.onCancel(TencentPlatform.this);
        }

        public void onCancel() {
            listener.onCancel(TencentPlatform.this);
        }

    };

    //QQ分享回调
    public IUiListener shareListener = new IUiListener() {

        @Override
        public void onError(UiError e) {
            Toast.makeText(activity, "error", Toast.LENGTH_SHORT).show();
        }

        @Override
        public void onCancel() {
            Toast.makeText(activity, activity.getString(R.string.sharecancel), Toast.LENGTH_SHORT).show();
        }

        @Override
        public void onComplete(Object arg0) {
            Toast.makeText(activity, "complete", Toast.LENGTH_SHORT).show();
        }
    };
}
