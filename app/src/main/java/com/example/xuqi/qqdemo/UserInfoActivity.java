package com.example.xuqi.qqdemo;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.example.xuqi.qqdemo.util.SinaWeiboPlatform;
import com.example.xuqi.qqdemo.util.TencentPlatform;

import static android.view.View.GONE;

public class UserInfoActivity extends AppCompatActivity implements View.OnClickListener {
    private ImageView icon;
    private Button share_Button, share_Zone_Button, share_Sina_Button, share_SinaFriend_Button;
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
        share_SinaFriend_Button = (Button) findViewById(R.id.share_SinaFriend_button);

        share_Button.setOnClickListener(this);
        share_Zone_Button.setOnClickListener(this);
        share_Sina_Button.setOnClickListener(this);
        share_SinaFriend_Button.setOnClickListener(this);

        if (type.equals("QQ")) {
            share_Sina_Button.setVisibility(GONE);
            share_SinaFriend_Button.setVisibility(GONE);
            share_Button.setVisibility(View.VISIBLE);
            share_Zone_Button.setVisibility(View.VISIBLE);
        } else if (type.equals("Sina")) {
            share_Button.setVisibility(GONE);
            share_Zone_Button.setVisibility(GONE);
            share_Sina_Button.setVisibility(View.VISIBLE);
            share_SinaFriend_Button.setVisibility(View.VISIBLE);
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
                (new TencentPlatform(this)).shareToQQ(this);
                break;
            case R.id.share_QQZone_button:
                (new TencentPlatform(this)).shareToQzone(this);
                break;
            case R.id.share_Sina_button:
                (new SinaWeiboPlatform(this)).shareToSina(this);
                break;
            case R.id.share_SinaFriend_button:
                (new SinaWeiboPlatform(this)).shareToSinafriend(this);
//                startActivity(new Intent(UserInfoActivity.this, WBShareToMessageFriendActivity.class));
            default:
                break;
        }
    }

    public static void showActivity(Context context, String name, String url, String type) {
        Intent intent = new Intent(context, UserInfoActivity.class);
        intent.putExtra("name", name);
        intent.putExtra("url", url);
        intent.putExtra("type", type);
        context.startActivity(intent);
    }
}
