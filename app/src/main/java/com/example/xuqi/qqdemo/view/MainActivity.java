package com.example.xuqi.qqdemo.view;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.NavigationView;
import android.support.design.widget.Snackbar;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.view.GravityCompat;
import android.support.v4.view.ViewPager;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.view.KeyEvent;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.example.xuqi.qqdemo.R;
import com.example.xuqi.qqdemo.adapter.MyViewPagerAdapter;
import com.example.xuqi.qqdemo.bean.NewsUser;
import com.example.xuqi.qqdemo.bean.NewsUserInfo;
import com.example.xuqi.qqdemo.fragment.MainSlidingFragment;
import com.example.xuqi.qqdemo.util.SnackbarUtil;
import com.example.xuqi.qqdemo.util.UserSessionManager;

import java.util.ArrayList;
import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

import static android.support.design.widget.TabLayout.MODE_SCROLLABLE;


public class MainActivity extends AppCompatActivity implements ViewPager.OnPageChangeListener {
    private DrawerLayout mDrawerLayout;
    private CoordinatorLayout mCoodinatorLayout;
    private TextView name, mail;
    private CircleImageView icon;
    private ViewPager mViewPager;
    // ViewPager顶部Tab
    private TabLayout mTabLayout;
    // TabLayout中的tab标题
    private String[] mTitles;
    // 填充到ViewPager中的Fragment
    private List<Fragment> mFragments;
    // ViewPager的数据适配器
    private MyViewPagerAdapter mViewPagerAdapter;

    private boolean mIsExit = false;
    private static final String TAG = "MainActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        initViewPagerData();

        initViews();
        checkIsLogin();
    }

    private void initViewPagerData() {
        // Tab的标题采用string-array的方法保存，在res/values/arrays.xml中写
        mTitles = getResources().getStringArray(R.array.tab_titles);
        //初始化填充到ViewPager中的Fragment集合
        mFragments = new ArrayList<>();
        for (int i = 0; i < mTitles.length; i++) {
            Bundle mBundle = new Bundle();
            mBundle.putInt("flag", i);
//            MyFragment mFragment = new MyFragment();
//            mFragment.setArguments(mBundle);
//            mFragments.add(i, mFragment);
            MainSlidingFragment mFragment = new MainSlidingFragment();
            mFragments.add(i, mFragment);
        }
    }

    private void initViews() {

        mCoodinatorLayout = (CoordinatorLayout) findViewById(R.id.id_coordinatorlayout);
        mDrawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
        mTabLayout = (TabLayout) findViewById(R.id.id_tablayout);
        mViewPager = (ViewPager) findViewById(R.id.id_viewpager);
        // 为ToolBar设置导航按键
        // 初始化Toolbar
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar_main);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mDrawerLayout.openDrawer(GravityCompat.START);
            }
        });
//        toolbar.setOnMenuItemClickListener(new Toolbar.OnMenuItemClickListener() {
//            @Override
//            public boolean onMenuItemClick(MenuItem item) {
//                switch (item.getItemId()) {
//                    case R.id.home:
//                        mDrawerLayout.openDrawer(GravityCompat.START);
//                        break;
//                }
//                return true;
//            }
//        });

        // 悬浮按钮初始化&点击事件
        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // SnackBar可以允许用户对当前情况进行简单的处理
                SnackbarUtil.show(v, "Data deleted", Snackbar.LENGTH_SHORT);
            }
        });

        // 导航页NavigationView初始化&点击事件
        final NavigationView navView = (NavigationView) findViewById(R.id.nav_view);
        // 将navigationview默认选中这一项
        navView.setCheckedItem(R.id.nav_fav);
        navView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                switch (item.getItemId()) {
                    // 设置按钮
                    case R.id.nav_set:
                        PersonalSettingActivity.showActivity(MainActivity.this);
                        mDrawerLayout.closeDrawers();
                        break;
                }
                return true;
            }
        });

        // 导入NavigationView的头部布局文件
        View headerView = navView.getHeaderView(0);
        name = (TextView) headerView.findViewById(R.id.name);
        mail = (TextView) headerView.findViewById(R.id.mail);
        icon = (CircleImageView) headerView.findViewById(R.id.icon_image);
        // 导航页用户头像点击
        icon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (UserSessionManager.isAleadyLogin() || NewsUser.getCurrentUser(NewsUser.class) != null) {
                    Intent intent = new Intent(MainActivity.this, PersonalPageActivity.class);
                    startActivity(intent);
                } else {
                    Intent intent = new Intent(MainActivity.this, LoginActivity.class);
                    startActivity(intent);
                }
            }
        });

        // 初始化ViewPager的适配器，并设置给它
        mViewPagerAdapter = new MyViewPagerAdapter(getSupportFragmentManager(), mTitles, mFragments);
        mViewPager.setAdapter(mViewPagerAdapter);
        // 设置ViewPager最大缓存的页面个数
        mViewPager.setOffscreenPageLimit(5);
        // 给ViewPager添加页面滑动动态监听器（为了让Toolbar中的Title可以变化相应的Tab的标题）
        mViewPager.addOnPageChangeListener(this);

        mTabLayout.setTabMode(MODE_SCROLLABLE);
        // 将TabLayout和ViewPager进行关联，让两者联动起来
        mTabLayout.setupWithViewPager(mViewPager);
    }

    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        setIntent(intent);
        processIntent();
    }

    @Override
    protected void onRestart() {
        checkIsLogin();
        super.onRestart();
    }

    private void processIntent() {
        checkIsLogin();
    }

    private void checkIsLogin() {
        // 第三方登录
        if (UserSessionManager.isAleadyLogin()) {
            NewsUserInfo user = UserSessionManager.getCurrentUser();
            name.setText(user.getName());
            Glide.with(this).load(UserSessionManager.getCurrentUser().getHeadPhoto()).into(icon);
        }
        // Bmob用户登录
        else if (NewsUser.getCurrentUser() != null) {
            NewsUser bmobUser = NewsUser.getCurrentUser(NewsUser.class);
            if (!TextUtils.isEmpty(bmobUser.getUsername()))
                name.setText(bmobUser.getUsername());
            if (!TextUtils.isEmpty(bmobUser.getEmail()))
                mail.setText(bmobUser.getEmail());
            if (bmobUser.getImageUrl() != null) {
                Glide.with(this).load(bmobUser.getImageUrl()).into(icon);
            }
        } else {
            // 未登录
            name.setText("董小姐");
            icon.setImageResource(R.drawable.nav_icon);
        }
    }

    public static void showActivity(Context context) {
        Intent intent = new Intent(context, MainActivity.class);
        context.startActivity(intent);
    }

    // viewpager滑动监听的三个方法
    @Override
    public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

    }

    @Override
    public void onPageSelected(int position) {

    }

    @Override
    public void onPageScrollStateChanged(int state) {

    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            if (mIsExit) {
                this.finish();
            } else {
                SnackbarUtil.show(mViewPager, "再按一次退出", 0);
                mIsExit = true;
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        mIsExit = false;
                    }
                }, 2000);
            }
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }


}
