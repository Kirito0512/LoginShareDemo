package com.example.xuqi.qqdemo;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.NavigationView;
import android.support.design.widget.Snackbar;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.example.xuqi.qqdemo.bean.NewsUserInfo;
import com.example.xuqi.qqdemo.util.UserSessionManager;

import cn.bmob.v3.Bmob;
import de.hdodenhof.circleimageview.CircleImageView;

import static com.example.xuqi.qqdemo.LoginActivity.Bmob_Application_Id;


public class MainActivity extends AppCompatActivity {
    private DrawerLayout mDrawerLayout;
    private TextView name;
    private CircleImageView icon;
    private static final String TAG = "MainActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        // 初始化Toolbar
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        //第一：默认初始化
        Bmob.initialize(this, Bmob_Application_Id);

        mDrawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
        // ActionBar
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            // 显示导航按钮(HomeAsUp按钮)
            actionBar.setDisplayHomeAsUpEnabled(true);
            // 设置一个导航按钮图标
            actionBar.setHomeAsUpIndicator(R.drawable.ic_menu_black_24dp);
        }

        // 悬浮按钮
        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // SnackBar可以允许用户对当前情况进行简单的处理
                Snackbar.make(v, "Data deleted", Snackbar.LENGTH_SHORT).setAction("撤销",
                        new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                Toast.makeText(MainActivity.this, "Data restored", Toast.LENGTH_SHORT).show();
                            }
                        }).show();
            }
        });

        // 导航栏
        final NavigationView navView = (NavigationView) findViewById(R.id.nav_view);
        navView.setCheckedItem(R.id.nav_call);
        navView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                mDrawerLayout.closeDrawers();
                return true;
            }
        });

        // 导入NavigationView的头部布局文件
        View headerView = navView.getHeaderView(0);
        name = (TextView) headerView.findViewById(R.id.name);
        icon = (CircleImageView) headerView.findViewById(R.id.icon_image);
        icon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (UserSessionManager.isAleadyLogin()) {
                    Intent intent = new Intent(MainActivity.this, PersonalPageActivity.class);
                    startActivity(intent);
                } else {
                    Intent intent = new Intent(MainActivity.this, LoginActivity.class);
                    startActivity(intent);
                }
            }
        });

//
//        NewsUserInfo user = new NewsUserInfo();
//        user.setName("xuqi");
//        user.setHeadPhoto("http://image.baidu.com/search/detail?ct=503316480&z=0&ipn=false&word=头像&hs=0&pn=0&spn=0&di=122313459840&pi=0&rn=1&tn=baiduimagedetail&is=0%2C0&ie=utf-8&oe=utf-8&cl=2&lm=-1&cs=1140958647%2C2055517506&os=3955454218%2C713474665&simid=4190247156%2C606480520&adpicid=0&lpn=0&ln=30&fr=ala&fm=&sme=&cg=head&bdtype=0&oriquery=头像&objurl=http%3A%2F%2Fup.qqya.com%2Fallimg%2F201710-t%2F17-101803_106599.jpg&fromurl=ippr_z2C%24qAzdH3FAzdH3Fooo_z%26e3Bqqyw_z%26e3Bv54AzdH3FpAzdH3Flc9a_d_z%26e3Bip4s&gsm=0");
//        // 保存用户信息到本地
//        UserSessionManager.storeUserToDisk(user);
//        user.save(this, new SaveListener() {
//            @Override
//            public void onSuccess() {
//                Log.d(TAG, "onSuccess: ");
//            }
//
//            @Override
//            public void onFailure(int i, String s) {
//                Log.d(TAG, "onFailure: ");
//            }
//        });
        checkIsLogin();

    }

    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        setIntent(intent);
        processIntent();
    }

    // ActionBar点击
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            // HomeAsUp的默认ID
            case android.R.id.home:
                mDrawerLayout.openDrawer(GravityCompat.START);
                break;
            default:
        }
        return true;
    }

    private void processIntent() {
        checkIsLogin();
        resetViews();
    }

    private void checkIsLogin() {
        if (UserSessionManager.isAleadyLogin()) {
            NewsUserInfo user = UserSessionManager.getCurrentUser();
            name.setText(user.getName());
            Glide.with(this).load(UserSessionManager.getCurrentUser().getHeadPhoto()).into(icon);
        }
    }

    public static void showActivity(Context context) {
        Intent intent = new Intent(context, MainActivity.class);
//        intent.putExtra("name", name);
//        intent.putExtra("url", url);
//        intent.putExtra("type", type);
        context.startActivity(intent);
    }

    public void resetViews(){
        if(!UserSessionManager.isAleadyLogin()) {
            name.setText("董小姐");
            icon.setImageResource(R.drawable.nav_icon);
        }
    }
}
