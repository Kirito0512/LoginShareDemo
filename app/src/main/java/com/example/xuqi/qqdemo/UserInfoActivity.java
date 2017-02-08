package com.example.xuqi.qqdemo;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.example.xuqi.qqdemo.Sinaapi.AccessTokenKeeper;
import com.example.xuqi.qqdemo.Sinaapi.ErrorInfo;
import com.example.xuqi.qqdemo.Sinaapi.Status;
import com.example.xuqi.qqdemo.Sinaapi.StatusList;
import com.example.xuqi.qqdemo.Sinaapi.StatusesAPI;
import com.sina.weibo.sdk.auth.Oauth2AccessToken;
import com.sina.weibo.sdk.exception.WeiboException;
import com.sina.weibo.sdk.net.RequestListener;
import com.tencent.connect.share.QQShare;
import com.tencent.connect.share.QzoneShare;
import com.tencent.tauth.IUiListener;
import com.tencent.tauth.UiError;

import java.util.ArrayList;

import static android.view.View.GONE;
import static com.example.xuqi.qqdemo.MainActivity.mTencent;
import static com.tencent.connect.share.QzoneShare.SHARE_TO_QZONE_TYPE_IMAGE_TEXT;

public class UserInfoActivity extends AppCompatActivity implements View.OnClickListener {
    private ImageView icon;
    private Button share_Button, share_Zone_Button, share_Sina_Button;
    public String name, url, type;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_info);
        icon = (ImageView) findViewById(R.id.head_photo);

        Intent intent = getIntent();
        name = intent.getStringExtra("name");
        url = intent.getStringExtra("url");
        type = intent.getStringExtra("type");

        share_Zone_Button = (Button) findViewById(R.id.share_QQZone_button);
        share_Button = (Button) findViewById(R.id.share_QQ_button);
        share_Sina_Button = (Button) findViewById(R.id.share_Sina_button);
        share_Button.setOnClickListener(this);
        share_Zone_Button.setOnClickListener(this);
        share_Sina_Button.setOnClickListener(this);
        if (type.equals("QQ")) {
            share_Sina_Button.setVisibility(GONE);
            share_Button.setVisibility(View.VISIBLE);
            share_Zone_Button.setVisibility(View.VISIBLE);
        } else if (type.equals("Sina")) {
            share_Button.setVisibility(GONE);
            share_Zone_Button.setVisibility(GONE);
            share_Sina_Button.setVisibility(View.VISIBLE);
        } else {
            share_Button.setText(getString(R.string.buttonsharetoWX));
            share_Zone_Button.setText(getString(R.string.buttonsharetoWXSNS));
        }
        Glide.with(UserInfoActivity.this).load(url).transform(new GlideRoundTransform(getApplicationContext(), 10)).
                placeholder(R.drawable.ic_cloud_download_black_24dp).
                error(R.drawable.ic_delete_forever_black_24dp).into(icon);//Glide解析获取用户头像
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.share_QQ_button:
                    shareToQQ();
                break;
            case R.id.share_QQZone_button:
                    shareToQzone();
                break;
            case R.id.share_Sina_button:
                    shareToSina();
                break;
            default:
                break;
        }
    }

    //Sina微博分享
    /*
        upload方法分享本地图片
         uploadUrlText方法分享网络图片，需要更高的权限
         update方法只分享文字
         content参数里面可以加上url,会自动生成 网页链接
     */
    private void shareToSina() {
        Oauth2AccessToken accessToken = AccessTokenKeeper.readAccessToken(this);
        StatusesAPI mStatusesAPI = new StatusesAPI(this, Constants.SINA_APP_KEY, accessToken);

        Drawable drawable = getResources().getDrawable(R.drawable.ic_com_sina_weibo_sdk_logo);
        Bitmap bitmap = ((BitmapDrawable) drawable).getBitmap();
        String content = "挑剔如我赶脚【雪:儿】棒棒哒！请吃下我这颗安利https://mars.changba.com/s/VnJU8DRVBwgemAmjV7RWYeSHuf2korNz?&code=DRuXOvems0Fv3q5j76knjtxHbJfwWq5JK8_m_yEyLigkvf6jEx9lfITc7wSSA--7";
        String imageUrl = "http://aliimg.changbalive.com/photo/154/882419ce25136b67_200_200.jpg";
        mStatusesAPI.upload(content, bitmap, null, null, mListener);
        //mStatusesAPI.update(content,null,null,mListener);
    }

    private void shareToQQ() {
        Bundle bundle = new Bundle();
        bundle.putString(QQShare.SHARE_TO_QQ_TITLE, "火星");
        bundle.putString(QQShare.SHARE_TO_QQ_TARGET_URL, "http://changba.com?");
        bundle.putString(QQShare.SHARE_TO_QQ_IMAGE_URL, "http://imgcache.qq.com/qzone/space_item/pre/0/66768.gif");
        bundle.putString(QQShare.SHARE_TO_QQ_SUMMARY, "大家快来一起玩吧！");

        // qq 分享必须设置分享的类型,SHARE_TO_QQ_TYPE_DEFAULT为图文混排默认类型.
        bundle.putInt(com.tencent.connect.share.QQShare.SHARE_TO_QQ_KEY_TYPE, com.tencent.connect.share.QQShare.SHARE_TO_QQ_TYPE_DEFAULT);
        mTencent.shareToQQ(this, bundle, shareListener);
    }

    private void shareToQzone() {
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
        mTencent.shareToQzone(this, params, shareListener);
    }

    //QQ分享回调
    public IUiListener shareListener = new IUiListener() {

        @Override
        public void onError(UiError e) {
            Toast.makeText(UserInfoActivity.this, "error", Toast.LENGTH_SHORT).show();
        }

        @Override
        public void onCancel() {
            Toast.makeText(UserInfoActivity.this, getString(R.string.sharecancel), Toast.LENGTH_SHORT).show();
        }

        @Override
        public void onComplete(Object arg0) {
            Toast.makeText(UserInfoActivity.this, "complete", Toast.LENGTH_SHORT).show();
        }
    };

    public static void showActivity(Context context, String name, String url, String type) {
        Intent intent = new Intent(context, UserInfoActivity.class);
        intent.putExtra("name", name);
        intent.putExtra("url", url);
        intent.putExtra("type", type);
        context.startActivity(intent);
    }

    /**
     * 微博 OpenAPI 回调接口。(无需打开微博，分享动态)
     */
    private RequestListener mListener = new RequestListener() {
        @Override
        public void onComplete(String response) {
            if (!TextUtils.isEmpty(response)) {
                if (response.startsWith("{\"statuses\"")) {
                    // 调用 StatusList#parse 解析字符串成微博列表对象
                    StatusList statuses = StatusList.parse(response);
                    if (statuses != null && statuses.total_number > 0) {
                        Toast.makeText(UserInfoActivity.this,
                                "获取微博信息流成功, 条数: " + statuses.statusList.size(),
                                Toast.LENGTH_LONG).show();
                    }
                } else if (response.startsWith("{\"created_at\"")) {
                    // 调用 Status#parse 解析字符串成微博对象
                    Status status = Status.parse(response);
                    Toast.makeText(UserInfoActivity.this,
                            "发送一送微博成功, id = " + status.id,
                            Toast.LENGTH_LONG).show();
                } else {
                    Toast.makeText(UserInfoActivity.this, response, Toast.LENGTH_LONG).show();
                }
            }
        }

        @Override
        public void onWeiboException(WeiboException e) {
            ErrorInfo info = ErrorInfo.parse(e.getMessage());
            Toast.makeText(UserInfoActivity.this, info.toString(), Toast.LENGTH_LONG).show();
        }
    };
}
