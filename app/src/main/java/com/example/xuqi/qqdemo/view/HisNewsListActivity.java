package com.example.xuqi.qqdemo.view;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.View;

import com.example.xuqi.qqdemo.R;
import com.example.xuqi.qqdemo.adapter.FavNewsRecyclerViewAdapter;
import com.example.xuqi.qqdemo.bean.HistoryNews;
import com.example.xuqi.qqdemo.bean.NewsInfo;
import com.example.xuqi.qqdemo.bean.NewsUser;
import com.example.xuqi.qqdemo.util.L;
import com.example.xuqi.qqdemo.util.SnackbarUtil;

import java.util.ArrayList;
import java.util.List;

import cn.bmob.v3.BmobQuery;
import cn.bmob.v3.exception.BmobException;
import cn.bmob.v3.listener.FindListener;

public class HisNewsListActivity extends BaseActivity implements FavNewsRecyclerViewAdapter.OnItemClickListener {
    private NewsUser currentUser;
    private Toolbar toolbar;
    private RecyclerView mRecyclerView;
    private FavNewsRecyclerViewAdapter mAdapter;
    private LinearLayoutManager mLayoutManager;
    private LoadingDialog dialog;
    private List<String> hisNewsIdList = new ArrayList<>();
    private List<NewsInfo> mFavList = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_his_news_list);
        currentUser = NewsUser.getCurrentUser(NewsUser.class);
        initViews();
    }

    private void initViews() {
        dialog = new LoadingDialog(this);
        toolbar = (Toolbar) findViewById(R.id.toolbar_his_news_list);
        mRecyclerView = (RecyclerView) findViewById(R.id.rv_his_news_list);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            toolbar.setNavigationOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    finish();
                }
            });
        }
        mLayoutManager = new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false);
        mAdapter = new FavNewsRecyclerViewAdapter(this);
        dialog.show();
        new requestDataThread().run();
        mAdapter.setOnItemClickListener(this);
        mRecyclerView.setAdapter(mAdapter);
        mRecyclerView.setLayoutManager(mLayoutManager);
    }

    @Override
    public void onItemClick(View view, int position) {
        // 获取点击的item的url
        NewsInfo newsInfo = mAdapter.newsList.get(position);
        // 将url放入intent
        Intent intent = new Intent(this, NewsContentActivity.class);
        Bundle bundle = new Bundle();
        bundle.putSerializable("newsInfo", newsInfo);
        intent.putExtras(bundle);
        // 跳转到NewsContentActivity
        startActivity(intent);
        SnackbarUtil.show(mRecyclerView, getString(R.string.item_clicked), 0);
    }

    @Override
    public void onItemLongClick(View view, int position) {

    }

    public class requestDataThread implements Runnable {
        @Override
        public void run() {
            BmobQuery<HistoryNews> query = new BmobQuery<HistoryNews>();
            query.addWhereEqualTo("userId", currentUser.getObjectId());
            query.findObjects(new FindListener<HistoryNews>() {
                @Override
                public void done(List<HistoryNews> list, BmobException e) {
                    if (e == null) {
                        showToast("个数为" + list.size());
                        for (int i = 0; i < list.size(); i++) {
                            HistoryNews favNews = list.get(i);
                            hisNewsIdList.add(favNews.getObjectId());
                            mFavList.add(new NewsInfo(favNews.getUniquekey(), favNews.getTitle(), favNews.getDate(), favNews.getAuthor_name(), favNews.getUrl(), favNews.getThumbnail_pic_s(), favNews.getThumbnail_pic_s02(), favNews.getThumbnail_pic_s03()));
                        }
                        mAdapter.setAdapterData(mFavList);
                    } else {
                        showToast("查询失败");
                        L.d("收藏" + e.toString());
                    }
                    dialog.dismiss();
                }
            });
        }
    }
}
