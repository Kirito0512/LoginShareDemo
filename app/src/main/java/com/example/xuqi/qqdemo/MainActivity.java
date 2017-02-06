package com.example.xuqi.qqdemo;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.SystemClock;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.tencent.connect.UserInfo;
import com.tencent.connect.common.Constants;
import com.tencent.connect.share.QQShare;
import com.tencent.connect.share.QzoneShare;
import com.tencent.tauth.IUiListener;
import com.tencent.tauth.Tencent;
import com.tencent.tauth.UiError;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

import static com.tencent.connect.share.QzoneShare.SHARE_TO_QZONE_TYPE_IMAGE_TEXT;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {
    public final static String SHARE_TITLE = "title";//分享的标题, 最长30个字符。
    public final static String SHARE_CONTENT = "summary";//分享的消息摘要，最长40个字。
    public final static String SHARE_TARGET_URL = "targetUrl";//分享出去后，点击内容跳转到的目标地址 QQ,QZone,Weixin,WeixinSNS
    public final static String SHARE_IMAGE_URL = "imageUrl";//分享图片的URL或者本地路径
    public final static String SHARE_IMAGE_LOCAL_URL = "imageLocalUrl";//一般放截屏分享的路径,本地图片路径,imageUrl同时存在时，优先使用imageLocalUrl
    public final static String SHARE_IMAGE_DATA = "imageData"; // 微信分享使用的图片数据
    private Button button,share_QQ_Button,share_Zone_Button;
    private Tencent mTencent;
    private ImageView icon;
    public static final String AppId = "1105896371";
    private static boolean isServerSideLogin = false;
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
                button.setText("注销");
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
    public IUiListener shareListener = new IUiListener() {

        @Override
        public void onError(UiError e) {
            Toast.makeText(MainActivity.this,"error",Toast.LENGTH_SHORT).show();
        }

        @Override
        public void onCancel() {
            Toast.makeText(MainActivity.this,getString(R.string.sharecancel),Toast.LENGTH_SHORT).show();
        }

        @Override
        public void onComplete(Object arg0) {
            Toast.makeText(MainActivity.this,"complete",Toast.LENGTH_SHORT).show();
        }
    };
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mTencent = Tencent.createInstance(AppId, this.getApplicationContext());
        icon = (ImageView) findViewById(R.id.head_photo);
        share_Zone_Button = (Button) findViewById(R.id.share_QQZone_button);
        share_QQ_Button = (Button) findViewById(R.id.share_QQ_button);
        button = (Button) findViewById(R.id.login_button);

        button.setOnClickListener(this);
        share_QQ_Button.setOnClickListener(this);
        share_Zone_Button.setOnClickListener(this);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {

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
                            Toast.makeText(MainActivity.this,"昵称："+nickName, Toast.LENGTH_SHORT).show();
                            Glide.with(MainActivity.this).load(iconUrl).transform(new GlideRoundTransform(getApplicationContext(),10)).
                                    placeholder(R.drawable.ic_cloud_download_black_24dp).
                                    error(R.drawable.ic_delete_forever_black_24dp).into(icon);//Glide解析获取用户头像

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
    }

    @Override
    public void onClick(View view) {
        switch(view.getId()){
            case R.id.login_button:
                if(button.getText().equals("登录"))
                    login();
                else{
                    mTencent.logout(MainActivity.this);
                    button.setText("登录");
                }
                break;
            case R.id.share_QQ_button:
                doShareToQQ();
                break;
            case R.id.share_QQZone_button:
                shareToQzone();
                break;
                default:
                    break;
        }
    }


    private void doShareToQQ() {
        Bundle bundle = new Bundle();

        bundle.putString(QQShare.SHARE_TO_QQ_TITLE, "火星");
        bundle.putString(QQShare.SHARE_TO_QQ_TARGET_URL, "http://changba.com?");
        bundle.putString(QQShare.SHARE_TO_QQ_IMAGE_URL,"http://imgcache.qq.com/qzone/space_item/pre/0/66768.gif");
        bundle.putString(QQShare.SHARE_TO_QQ_SUMMARY, "大家快来一起玩吧！");

        // qq 分享必须设置分享的类型,SHARE_TO_QQ_TYPE_DEFAULT为图文混排默认类型.
        bundle.putInt(com.tencent.connect.share.QQShare.SHARE_TO_QQ_KEY_TYPE, com.tencent.connect.share.QQShare.SHARE_TO_QQ_TYPE_DEFAULT);
        share(this,bundle);
    }

    private void share(Activity activity, Bundle params) {
        mTencent.shareToQQ(activity, params, shareListener);
    }

    private void shareToQzone () {
        Bundle params = new Bundle();
        //分享类型
        params.putInt(QzoneShare.SHARE_TO_QZONE_KEY_TYPE,SHARE_TO_QZONE_TYPE_IMAGE_TEXT );
        params.putString(QzoneShare.SHARE_TO_QQ_TITLE, "标题");//必填
        params.putString(QzoneShare.SHARE_TO_QQ_SUMMARY, "摘要");//选填
        params.putString(QzoneShare.SHARE_TO_QQ_TARGET_URL, "http://changba.com?");//必填
        params.putStringArrayList(QzoneShare.SHARE_TO_QQ_IMAGE_URL,
                new ArrayList<String>(){{
                    add("http://imgcache.qq.com/qzone/space_item/pre/0/66768.gif");
                    add("http://image.baidu.com/search/detail?ct=503316480&z=0&ipn=false&word=头像&hs=0&pn=3&spn=0&di=143942644890&pi=0&rn=1&tn=baiduimagedetail&is=0%2C0&ie=utf-8&oe=utf-8&cl=2&lm=-1&cs=1365184907%2C920260653&os=2236587395%2C1886995582&simid=4249720609%2C617641245&adpicid=0&lpn=0&ln=30&fr=ala&fm=&sme=&cg=head&bdtype=0&oriquery=头像&objurl=http%3A%2F%2Ftx.haiqq.com%2Fuploads%2Fallimg%2F140612%2F13300A442-38.png&fromurl=ippr_z2C%24qAzdH3FAzdH3Fpx_z%26e3Biwtqq_z%26e3Bv54AzdH3Fqtg2sep57xtwg2AzdH3Fda89am8dAzdH3Fd09m_d_z%26e3Bip4s&gsm=0");}});
        mTencent.shareToQzone(this, params, shareListener);
    }

    private void login() {
        if (!mTencent.isSessionValid())
        {
            mTencent.login(this, SCOPE, loginListener);
            isServerSideLogin = false;
        }else {
            if (isServerSideLogin) { // Server-Side 模式的登陆, 先退出，再进行SSO登陆
                mTencent.logout(this);
                mTencent.login(this, SCOPE, loginListener);
                isServerSideLogin = false;
                Log.d("SDKQQAgentPref", "FirstLaunch_SDK:" + SystemClock.elapsedRealtime());
                return;
            }
            mTencent.logout(this);
//            updateUserInfo();
//            updateLoginButton();
        }
    }
}
