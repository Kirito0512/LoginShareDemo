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
import com.example.xuqi.qqdemo.bean.FavoriteNews;
import com.example.xuqi.qqdemo.bean.NewsInfo;
import com.example.xuqi.qqdemo.bean.NewsUser;
import com.example.xuqi.qqdemo.util.L;
import com.example.xuqi.qqdemo.util.SnackbarUtil;
import com.example.xuqi.qqdemo.widget.CustomItemTouchHelperCallback;
import com.example.xuqi.qqdemo.widget.DefaultItemTouchHelper;

import java.util.ArrayList;
import java.util.List;

import cn.bmob.v3.BmobQuery;
import cn.bmob.v3.exception.BmobException;
import cn.bmob.v3.listener.FindListener;
import cn.bmob.v3.listener.UpdateListener;

import static com.example.xuqi.qqdemo.R.id.rv_fav_news_list;

/**
 * Created by xuqi 17/5/1
 * 新闻收藏列表
 * 支持滑动删除
 */

public class FavNewsListActivity extends BaseActivity implements FavNewsRecyclerViewAdapter.OnItemClickListener {
    private Toolbar toolbar;
    private RecyclerView mRecyclerView;
    private FavNewsRecyclerViewAdapter mAdapter;
    private LinearLayoutManager mLayoutManager;
    private List<NewsInfo> mFavList = new ArrayList<>();
    private List<String> favNewsIdList = new ArrayList<>();
    private NewsUser currentUser;
    private LoadingDialog dialog;
    private CustomItemTouchHelperCallback.OnItemTouchCallbackListener onItemTouchCallbackListener = new CustomItemTouchHelperCallback.OnItemTouchCallbackListener() {
        // 滑动Item
        @Override
        public void onSwiped(int adapterPosition) {
            // 滑动删除的时候，从数据源移除，并刷新这个Item
            if (mFavList != null) {
                mFavList.remove(adapterPosition);
                new deleteDataThread(favNewsIdList.get(adapterPosition)).run();
                favNewsIdList.remove(adapterPosition);
                mAdapter.notifyItemRemoved(adapterPosition);
            }
        }

        // 拖拽Item
        @Override
        public boolean onMove(int srcPosition, int targetPosition) {
//            if (mFavList != null) {
//                mFavList = changeListItemPosition(mFollowList, srcPosition, targetPosition);
////                Collections.swap(mFollowList, srcPosition, targetPosition);
//                // 更新UI中的Item的位置，主要是给用户看到交互效果
//                mAdapter.notifyItemMoved(srcPosition, targetPosition);
//                return true;
//            }
            return false;
        }

        @Override
        public void onSelectedChanged(int actionState) {

        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_fav_news_list);
        currentUser = NewsUser.getCurrentUser(NewsUser.class);
        initViews();
    }

    // 从新闻详情Activity finish过来时，重新加载新闻收藏夹
    @Override
    protected void onRestart() {
        L.d("restart");
        favNewsIdList.clear();
        mFavList.clear();
        new requestDataThread().run();
        super.onRestart();
    }

    private void initViews() {
        dialog = new LoadingDialog(this);
        toolbar = (Toolbar) findViewById(R.id.toolbar_fav_news_list);
        mRecyclerView = (RecyclerView) findViewById(rv_fav_news_list);
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

        DefaultItemTouchHelper itemTouchHelper = new DefaultItemTouchHelper(onItemTouchCallbackListener);
        itemTouchHelper.attachToRecyclerView(mRecyclerView);
        itemTouchHelper.setDragEnable(false);
        itemTouchHelper.setSwipeEnable(true);
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
            BmobQuery<FavoriteNews> query = new BmobQuery<FavoriteNews>();
            query.addWhereEqualTo("userId", currentUser.getObjectId());
            query.findObjects(new FindListener<FavoriteNews>() {
                @Override
                public void done(List<FavoriteNews> list, BmobException e) {
                    if (e == null) {
                        showToast("个数为" + list.size());
                        for (int i = 0; i < list.size(); i++) {
                            FavoriteNews favNews = list.get(i);
                            favNewsIdList.add(favNews.getObjectId());
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

    public class deleteDataThread implements Runnable {
        String objectId;

        public deleteDataThread(String id) {
            objectId = id;
        }

        @Override
        public void run() {
            FavoriteNews favNews = new FavoriteNews();
            favNews.setObjectId(objectId);
            favNews.delete(new UpdateListener() {

                @Override
                public void done(BmobException e) {
                    if (e == null) {
                        L.d("bmob删除成功");
                    } else {
                        L.d("bmob失败：" + e.getMessage() + "," + e.getErrorCode());
                    }
                }
            });
        }
    }
}
