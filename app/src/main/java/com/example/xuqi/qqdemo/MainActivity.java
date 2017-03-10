package com.example.xuqi.qqdemo;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
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
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.example.xuqi.qqdemo.adapter.MyViewPagerAdapter;
import com.example.xuqi.qqdemo.bean.NewsUserInfo;
import com.example.xuqi.qqdemo.fragment.MainSlidingFragment;
import com.example.xuqi.qqdemo.util.UserSessionManager;

import java.util.ArrayList;
import java.util.List;

import cn.bmob.v3.Bmob;
import de.hdodenhof.circleimageview.CircleImageView;

import static android.support.design.widget.TabLayout.MODE_SCROLLABLE;
import static com.example.xuqi.qqdemo.LoginActivity.Bmob_Application_Id;


public class MainActivity extends AppCompatActivity implements ViewPager.OnPageChangeListener {
    private DrawerLayout mDrawerLayout;
    private CoordinatorLayout mCoodinatorLayout;
    private TextView name;
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

    private static final String TAG = "MainActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // 初始化Bmob
        Bmob.initialize(this, Bmob_Application_Id);

        initViewPagerData();

        configViews();
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

    private void configViews() {
        // 初始化Toolbar
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        mCoodinatorLayout = (CoordinatorLayout) findViewById(R.id.id_coordinatorlayout);
        mDrawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
        mTabLayout = (TabLayout) findViewById(R.id.id_tablayout);
        mViewPager = (ViewPager) findViewById(R.id.id_viewpager);
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

        // 初始化ViewPager的适配器，并设置给它
        mViewPagerAdapter = new MyViewPagerAdapter(getSupportFragmentManager(), mTitles, mFragments);
        mViewPager.setAdapter(mViewPagerAdapter);
        // 设置ViewPager最大缓存的页面个数
        mViewPager.setOffscreenPageLimit(5);
        // 给ViewPager添加页面动态监听器（为了让Toolbar中的Title可以变化相应的Tab的标题）
        mViewPager.addOnPageChangeListener(this);

        mTabLayout.setTabMode(MODE_SCROLLABLE);
        // 将TabLayout和ViewPager进行关联，让两者联动起来
        mTabLayout.setupWithViewPager(mViewPager);
        // 设置Tablayout的Tab显示ViewPager的适配器中的getPageTitle函数获取到的标题
        mTabLayout.setTabsFromPagerAdapter(mViewPagerAdapter);
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

    @Override
    public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

    }

    @Override
    public void onPageSelected(int position) {

    }

    @Override
    public void onPageScrollStateChanged(int state) {

    }
}
