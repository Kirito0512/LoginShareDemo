package com.example.xuqi.qqdemo.view;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.KeyEvent;
import android.view.MenuItem;
import android.view.View;
import android.webkit.WebChromeClient;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.example.xuqi.qqdemo.R;
import com.example.xuqi.qqdemo.bean.FavoriteNews;
import com.example.xuqi.qqdemo.bean.NewsInfo;
import com.example.xuqi.qqdemo.bean.NewsUser;
import com.example.xuqi.qqdemo.util.L;
import com.example.xuqi.qqdemo.widget.NestedScrollWebView;

import java.util.List;

import cn.bmob.v3.BmobQuery;
import cn.bmob.v3.exception.BmobException;
import cn.bmob.v3.listener.FindListener;
import cn.bmob.v3.listener.SaveListener;
import cn.bmob.v3.listener.UpdateListener;

/**
 * 新闻详情界面
 */
public class NewsContentActivity extends BaseActivity {
    private TextView tvNewsContentTitle;
    private Toolbar tlNewsContent;
    private WebView wbNewsContent;
    private ProgressBar progressBar;
    // 当前新闻bean
    private NewsInfo newsInfo;
    // 当前用户
    private NewsUser currentUser;
    // 当前收藏新闻这条数据的ID
    private String FavNewsId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_news_content);
        initViews();
        currentUser = NewsUser.getCurrentUser(NewsUser.class);
        checkIsFav(currentUser.getObjectId(), newsInfo);
    }

    @SuppressLint("SetJavaScriptEnabled")
    private void initViews() {
        tvNewsContentTitle = (TextView) findViewById(R.id.tv_news_content);
        tlNewsContent = (Toolbar) findViewById(R.id.toolbar_news_content);
        wbNewsContent = (NestedScrollWebView) findViewById(R.id.wv_news_content);
        progressBar = (ProgressBar) findViewById(R.id.pb_news_content);

        tlNewsContent.inflateMenu(R.menu.news_content_menu);
        // 返回键点击
        tlNewsContent.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        // 菜单栏点击
        tlNewsContent.setOnMenuItemClickListener(new Toolbar.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(final MenuItem item) {
                switch (item.getItemId()) {
                    // 取消收藏
                    case R.id.newscon_cancel_fav:
                        DeleteNewsThread deleteThread = new DeleteNewsThread(FavNewsId);
                        new Thread(deleteThread).start();
                        break;

                    // 收藏按钮
                    case R.id.newscon_nav_fav:
                        AddNewsThread addThread = new AddNewsThread(currentUser.getObjectId(), newsInfo);
                        new Thread(addThread).start();
                        break;
                    case R.id.newscon_day_night:
                        break;
                }
                return true;
            }
        });


        wbNewsContent.getSettings().setJavaScriptEnabled(true);
        wbNewsContent.setWebViewClient(new WebViewClient());

        progressBar.setMax(100);
        wbNewsContent.setWebChromeClient(new WebChromeClient() {
            @Override
            public void onProgressChanged(WebView view, int newProgress) {
                progressBar.setProgress(newProgress);
                if (newProgress >= 100) {
                    progressBar.setVisibility(View.GONE);
                }
            }
        });
        wbNewsContent.setWebViewClient(new WebViewClient() {
            @Override
            public boolean shouldOverrideUrlLoading(WebView view, String url) {
                view.loadUrl(url);
                return true;
            }
        });

        // 从Intent中获取新闻bean
        newsInfo = (NewsInfo) getIntent().getExtras().getSerializable("newsInfo");
        String url = newsInfo.getUrl();
        String title = newsInfo.getAuthor_name();
        tvNewsContentTitle.setText(title);
        wbNewsContent.loadUrl(url);
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        // Check if the key event was the Back button and if there's history
        if ((keyCode == KeyEvent.KEYCODE_BACK) && wbNewsContent.canGoBack()) {
            wbNewsContent.goBack();
            return true;
        }
        // If it wasn't the Back key or there's no web page history, bubble up to the default
        // system behavior (probably exit the activity)
        return super.onKeyDown(keyCode, event);
    }

    // 向新闻收藏数据表中查询当前用户和当前新闻这条数据
    public void checkIsFav(String userId, NewsInfo newsInfo) {
        BmobQuery<FavoriteNews> query = new BmobQuery<FavoriteNews>();
        query.addWhereEqualTo("userId", userId);
        query.addWhereEqualTo("uniquekey", newsInfo.getUniquekey());
        query.findObjects(new FindListener<FavoriteNews>() {
            @Override
            public void done(List<FavoriteNews> list, BmobException e) {
                if (e == null) {
                    showToast("个数为" + list.size());
                    if (list.size() == 1) {
                        // 保存收藏新闻这条数据的id
                        FavNewsId = list.get(0).getObjectId();
                        tlNewsContent.getMenu().findItem(R.id.newscon_cancel_fav).setVisible(true);
                    } else {
                        tlNewsContent.getMenu().findItem(R.id.newscon_nav_fav).setVisible(true);
                    }
                } else
                    showToast("查询失败");

            }
        });
    }

    // 添加收藏
    class AddNewsThread implements Runnable {
        String userId;
        NewsInfo newsInfo;

        public AddNewsThread(String id, NewsInfo info) {
            userId = id;
            newsInfo = info;
        }

        @Override
        public void run() {
            FavoriteNews favNews = new FavoriteNews(userId, newsInfo.getUniquekey(), newsInfo.getTitle(), newsInfo.getDate(), newsInfo.getAuthor_name(), newsInfo.getUrl(), newsInfo.getThumbnail_pic_s(), newsInfo.getThumbnail_pic_s02(), newsInfo.getThumbnail_pic_s03());
            favNews.save(new SaveListener<String>() {
                @Override
                public void done(String objectId, BmobException e) {
                    if (e == null) {
                        L.d("add线程 " + Thread.currentThread().toString());
                        showToast("收藏成功");
                        // 添加新闻数据成功后保存新闻ID
                        FavNewsId = objectId;
                        // done方法是在主线程中执行的，所以可以直接更新UI
                        tlNewsContent.getMenu().findItem(R.id.newscon_nav_fav).setVisible(false);
                        tlNewsContent.getMenu().findItem(R.id.newscon_cancel_fav).setVisible(true);
                    } else {
                        showToast("收藏失败" + e.toString());
                    }
                }
            });
        }
    }

    // 取消收藏操作
    class DeleteNewsThread implements Runnable {

        public String id;

        public DeleteNewsThread(String FavNewsId) {
            id = FavNewsId;
        }

        @Override
        public void run() {
            FavoriteNews favNews = new FavoriteNews();
            favNews.setObjectId(id);
            favNews.delete(new UpdateListener() {
                @Override
                public void done(BmobException e) {
                    if (e == null) {
                        showToast("取消收藏成功");
                        // done方法是在主线程中执行的，所以可以直接更新UI
                        tlNewsContent.getMenu().findItem(R.id.newscon_nav_fav).setVisible(true);
                        tlNewsContent.getMenu().findItem(R.id.newscon_cancel_fav).setVisible(false);
                    } else {
                        showToast("取消收藏失败" + e.toString());
                    }
                }
            });
        }
    }

}
