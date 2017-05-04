package com.example.xuqi.qqdemo.view;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.NavigationView;
import android.support.design.widget.Snackbar;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.view.GravityCompat;
import android.support.v4.view.ViewPager;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.view.KeyEvent;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.example.xuqi.qqdemo.R;
import com.example.xuqi.qqdemo.adapter.MyViewPagerAdapter;
import com.example.xuqi.qqdemo.application.BaseApplication;
import com.example.xuqi.qqdemo.bean.NewsUser;
import com.example.xuqi.qqdemo.bean.NewsUserInfo;
import com.example.xuqi.qqdemo.fragment.NewsFragment;
import com.example.xuqi.qqdemo.util.SnackbarUtil;
import com.example.xuqi.qqdemo.util.UserSessionManager;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import cn.bmob.v3.exception.BmobException;
import cn.bmob.v3.listener.UpdateListener;
import de.hdodenhof.circleimageview.CircleImageView;

import static android.support.design.widget.TabLayout.MODE_SCROLLABLE;


public class MainActivity extends BaseActivity {
    private DrawerLayout mDrawerLayout;
    private TextView name, mail;
    private CircleImageView icon;
    private ViewPager mViewPager;
    // ViewPager顶部Tab
    private TabLayout mTabLayout;
    // TabLayout中的tab标题
    private String[] mTitles;
    // tab标题的对应newsType
    private String[] mTitlesCN;
    // 文字与拼音对应的map
    private Map titleMap = new HashMap<String, String>();
    // 填充到ViewPager中的Fragment
    private List<Fragment> mFragments;
    // ViewPager的数据适配器
    private MyViewPagerAdapter mViewPagerAdapter;
    // 首页Tab右侧的加号
    private ImageView mImageViewAddTab;

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
        // 若想getIntent获取正确，那么onNewIntent种需要setIntent
        Bundle bundle = getIntent().getExtras();
        if (bundle != null) {
            // 从拖拽修改Tabs的Activity跳转回来，获取新的关注Tabs List
            List mTitleList = bundle.getStringArrayList("changeTab");
            mTitles = new String[mTitleList.size()];
            mTitleList.toArray(mTitles);

            // 保存到Bmob中
            NewsUser newUser = new NewsUser();
            newUser.setmTabs(mTitles);
            NewsUser currentUser = NewsUser.getCurrentUser(NewsUser.class);
            newUser.update(currentUser.getObjectId(), new UpdateListener() {
                @Override
                public void done(BmobException e) {
                    if (e == null) {
                        showToast("更新用户信息成功");
                    } else {
                        showToast("更新用户信息失败:" + e.getMessage());
                    }
                }
            });
        } else {
            if (NewsUser.getCurrentUser() != null)
                mTitles = NewsUser.getCurrentUser(NewsUser.class).getmTabs();
            else
                mTitles = BaseApplication.getInstance().getmTabs();
        }

        //初始化填充到ViewPager中的Fragment集合
        mFragments = new ArrayList<>();
        for (int i = 0; i < mTitles.length; i++) {
            String title = BaseApplication.getInstance().getTitleMap().get(mTitles[i]);
            NewsFragment mFragment = new NewsFragment().newInstance(title);
            mFragments.add(i, mFragment);
        }
    }

    private void initViews() {
        mDrawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
        mTabLayout = (TabLayout) findViewById(R.id.id_tablayout);
        mViewPager = (ViewPager) findViewById(R.id.id_viewpager);
        mImageViewAddTab = (ImageView) findViewById(R.id.iv_add_title);
        // 将当前用户关注的新闻Title传递保存到Bundle中
        final Bundle bundle = new Bundle();
        bundle.putStringArray("title", mTitles);
        // 加号的点击事件，跳转到修改新闻关注栏目修改Activity
        mImageViewAddTab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showActivity(DragViewPagerTitleActivity.class, bundle);
            }
        });
        // 为ToolBar设置导航按键
        // 初始化Toolbar
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar_main);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mDrawerLayout.openDrawer(GravityCompat.START);
            }
        });

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
//        navView.setCheckedItem(R.id.nav_fav);
        navView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                switch (item.getItemId()) {
                    // 设置按钮
                    case R.id.nav_set:
                        showActivity(PersonalSettingActivity.class);
                        break;

                    // 设置反馈按钮
                    case R.id.nav_suggest:
                        showActivity(NewsFeedBackActivity.class);
                        break;

                    // 设置收藏按钮
                    case R.id.nav_fav:
                        showActivity(FavNewsListActivity.class);
                        break;
                }
                mDrawerLayout.closeDrawers();
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
                    showActivity(PersonalPageActivity.class);
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
//        mViewPager.addOnPageChangeListener(this);

        mTabLayout.setTabMode(MODE_SCROLLABLE);
        // 将TabLayout和ViewPager进行关联，让两者联动起来
        mTabLayout.setupWithViewPager(mViewPager);
    }

    // SingleTask启动模式，从别的Activity通过showActivity跳转过来时触发
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        // 不使用setIntent，会使得getIntent出现问题
        setIntent(intent);
    }

    @Override
    protected void onRestart() {
        super.onRestart();
        processIntent();
    }

    private void processIntent() {
        // 不等于null时是从NewsContentActivity跳转来的，无需进行逻辑操作
        if(getIntent().getExtras().getString("isFrom") == null){
            initViewPagerData();
            initViews();
            checkIsLogin();
        }
    }

    // 检查是否登录&更新信息
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
//
//    // viewpager滑动监听的三个方法
//    @Override
//    public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
//
//    }
//
//    @Override
//    public void onPageSelected(int position) {
//
//    }
//
//    @Override
//    public void onPageScrollStateChanged(int state) {
//
//    }

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
