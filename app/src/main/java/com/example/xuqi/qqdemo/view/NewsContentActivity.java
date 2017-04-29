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
import com.example.xuqi.qqdemo.util.L;
import com.example.xuqi.qqdemo.widget.NestedScrollWebView;

public class NewsContentActivity extends BaseActivity {
    private TextView tvNewsContentTitle;
    private Toolbar tlNewsContent;
    private WebView wbNewsContent;
    private ProgressBar progressBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_news_content);
        initViews();
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
            public boolean onMenuItemClick(MenuItem item) {
                switch (item.getItemId()) {
                    case R.id.newscon_nav_fav:
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

        String url = getIntent().getStringExtra("newsUrl");
        L.d("xuqi" + url);
        String title = getIntent().getStringExtra("newsTitle");
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
}
