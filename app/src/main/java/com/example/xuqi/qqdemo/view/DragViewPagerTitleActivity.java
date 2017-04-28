package com.example.xuqi.qqdemo.view;

import android.os.Bundle;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;

import com.example.xuqi.qqdemo.R;
import com.example.xuqi.qqdemo.adapter.NewsTabRecyclerViewAdapter;
import com.example.xuqi.qqdemo.util.SnackbarUtil;
import com.example.xuqi.qqdemo.widget.CustomItemTouchHelperCallback;
import com.example.xuqi.qqdemo.widget.DefaultItemTouchHelper;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

/**
 * Created by xuqi on 2017/4/27
 * 拖拽新闻栏目item的Activity
 */
public class DragViewPagerTitleActivity extends BaseActivity implements NewsTabRecyclerViewAdapter.OnItemClickListener {
    private RecyclerView mRecyclerView;
    private RecyclerView mRestRecyclerView;
    private RecyclerView.LayoutManager mLayoutManager, mRestLayoutManager;
    // 已有Tab的适配器和，未选择的Tab的适配器
    private NewsTabRecyclerViewAdapter mRecyclerViewAdapter, mRestRecyclerViewAdapter;
    // 所有Tab
    List<String> mList;
    // 已关注的Tab
    List<String> mFollowList;
    // 删去已有Tab之后剩余的Tab
    List<String> mRestList;
    // String数组 全部Tab
    String[] str;
    // 实现拖拽效果的回调函数
    private CustomItemTouchHelperCallback.OnItemTouchCallbackListener onItemTouchCallbackListener = new CustomItemTouchHelperCallback.OnItemTouchCallbackListener() {
        // 滑动Item
        @Override
        public void onSwiped(int adapterPosition) {
            // 滑动删除的时候，从数据源移除，并刷新这个Item
            if (mFollowList != null) {
                mFollowList.remove(adapterPosition);
                mRecyclerViewAdapter.notifyItemRemoved(adapterPosition);
            }
        }

        // 拖拽Item
        @Override
        public boolean onMove(int srcPosition, int targetPosition) {
            if (mFollowList != null) {
                // 更换数据源中的数据Item的位置
                Collections.swap(mFollowList, srcPosition, targetPosition);
                // 更新UI中的Item的位置，主要是给用户看到交互效果
                mRecyclerViewAdapter.notifyItemMoved(srcPosition, targetPosition);
                return true;
            }
            return false;
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_drag_view_pager_title);
        initViews();
    }

    private void initViews() {
        Bundle bundle = getIntent().getExtras();
        // 获取传递过来的Title数据
        if (bundle != null) {
            /**
             * 这个需要注意，如果只是单纯的Arrays.asList的话返回的不是ArrayList，而是Arrays$ArrayLis，
             * 没有重写remove，add等方法，调用时会报错 UnsupportedOperationException
             */
            mFollowList = new ArrayList<>(Arrays.asList(bundle.getStringArray("title")));
        }
        str = getResources().getStringArray(R.array.tab_titles);
        // 所有Tab的list
        mList = new ArrayList<>(Arrays.asList(str));
        // 求差集，即用户当前还未关注的Tab
        mRestList = removeList(mList, mFollowList);

        mRecyclerView = (RecyclerView) findViewById(R.id.rv_drag_title);
        mLayoutManager = new GridLayoutManager(this, 4, GridLayoutManager.VERTICAL, false);
        mRecyclerViewAdapter = new NewsTabRecyclerViewAdapter(this, mFollowList);
        mRecyclerViewAdapter.setOnItemClickListener(this);

        DefaultItemTouchHelper itemTouchHelper = new DefaultItemTouchHelper(onItemTouchCallbackListener);
        itemTouchHelper.attachToRecyclerView(mRecyclerView);
        itemTouchHelper.setDragEnable(true);
        itemTouchHelper.setSwipeEnable(false);
        mRecyclerView.setAdapter(mRecyclerViewAdapter);
        mRecyclerView.setLayoutManager(mLayoutManager);

        // 剩余Tab
        mRestRecyclerView = (RecyclerView) findViewById(R.id.rv_rest_title);
        mRestLayoutManager = new GridLayoutManager(this, 4, GridLayoutManager.VERTICAL, false);
        mRestRecyclerViewAdapter = new NewsTabRecyclerViewAdapter(this, mRestList);
        mRestRecyclerViewAdapter.setOnItemClickListener(new NewsTabRecyclerViewAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(View view, int position) {
                mRestRecyclerViewAdapter.deleteListItem(position);
                mRestList = mRestRecyclerViewAdapter.mDatas;
                mFollowList = removeList(mList, mRestList);
                mRecyclerViewAdapter.addAfterdeleteAllItem(mFollowList);
                SnackbarUtil.show(view, "添加成功", 1);
            }

            @Override
            public void onItemLongClick(View view, int position) {

            }
        });
        mRestRecyclerView.setAdapter(mRestRecyclerViewAdapter);
        mRestRecyclerView.setLayoutManager(mRestLayoutManager);
    }

    private List<String> removeList(List<String> mList, List<String> mFollowList) {
        // 给List重新赋值为所有Tab的List
        mList = new ArrayList<>(Arrays.asList(str));
        List<String> result = null;
        if (mList != null && mFollowList != null) {
            mList.removeAll(mFollowList);
            result = mList;
        }
        return result;
    }

    // item点击事件
    @Override
    public void onItemClick(View view, int position) {
        mRecyclerViewAdapter.deleteListItem(position);
        mFollowList = mRecyclerViewAdapter.mDatas;
        mRestList = removeList(mList, mFollowList);
        mRestRecyclerViewAdapter.addAfterdeleteAllItem(mRestList);
        SnackbarUtil.show(view, "删除成功", 1);
    }

    // item长按事件
    @Override
    public void onItemLongClick(View view, int position) {
        SnackbarUtil.show(view, "长按后拖拽移动位置", 0);
    }
}
