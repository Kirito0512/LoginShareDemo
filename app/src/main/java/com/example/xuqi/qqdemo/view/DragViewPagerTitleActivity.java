package com.example.xuqi.qqdemo.view;

import android.graphics.Color;
import android.os.Bundle;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.view.View;
import android.widget.Button;

import com.example.xuqi.qqdemo.R;
import com.example.xuqi.qqdemo.ViewHolder.NewsTabViewHolder;
import com.example.xuqi.qqdemo.adapter.NewsTabRecyclerViewAdapter;
import com.example.xuqi.qqdemo.application.BaseApplication;
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
public class DragViewPagerTitleActivity extends BaseActivity implements View.OnClickListener {
    private RecyclerView mRecyclerView;
    private RecyclerView mRestRecyclerView;
    private RecyclerView.LayoutManager mLayoutManager, mRestLayoutManager;
    private Button mButton;
    // 已有Tab的适配器和，未选择的Tab的适配器
    private NewsTabRecyclerViewAdapter mRecyclerViewAdapter, mRestRecyclerViewAdapter;
    // 所有Tab
    List<String> mList;
    // 已关注的Tab
    List<String> mFollowList;
    // 删去已有Tab之后剩余的Tab
    List<String> mRestList;

    private boolean isMove = false;

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
            swapListItemPosition(mFollowList, srcPosition, targetPosition);
            // 更新UI中的Item的位置，主要是给用户看到交互效果
            mRecyclerViewAdapter.notifyItemMoved(srcPosition, targetPosition);
            return true;
        }

        @Override
        public void onSelectedChanged(RecyclerView.ViewHolder viewHolder, int actionState) {
            /**
             * 一个Item拖拽的过程中，只会经历两个状态
             * 1. 长按选中拖动的过程中，actionState为ACTION_STATE_DRAG
             * 2. 手指释放，actionState为ACTION_STATE_IDLE
             */

            // A View is currently being dragged.
            if (actionState == ItemTouchHelper.ACTION_STATE_DRAG) {
                // 不是空闲状态
                isMove = true;
                // 设置选中的item的背景色
                NewsTabViewHolder mNewsTabViewHolder = (NewsTabViewHolder) viewHolder;
                mNewsTabViewHolder.tvNewsTab.setBackgroundColor(Color.LTGRAY);
            }
            /**
             * ItemTouchHelper is in idle state.
             * At this state, either there is no related motion event by the user
             * or latest motion events have not yet triggered a swipe or drag.
             */
            else if (actionState == ItemTouchHelper.ACTION_STATE_IDLE) {
                if (isMove) {
                    // 重点，拖拽后手指释放，notifyDataSetChanged会让适配器里的数据重新bind
                    // 解决了拖拽移动项目后，position位置不正确的bug
                    mRecyclerViewAdapter.notifyDataSetChanged();
                    isMove = false;
                }
            }
        }

        @Override
        public void clearView(RecyclerView.ViewHolder viewHolder) {
            // 将选中的item的背景色恢复
            NewsTabViewHolder mNewsTabViewHolder = (NewsTabViewHolder) viewHolder;
            mNewsTabViewHolder.tvNewsTab.setBackground(getResources().getDrawable(R.drawable.news_tab));
        }
    };

    // 将List中src位置的数据，调整到target位置
    private void swapListItemPosition(List<String> mFollowList, int srcPosition, int targetPosition) {
        if (mFollowList == null || srcPosition < 0 || targetPosition < 0) {
            return;
        } else {
            if (srcPosition < targetPosition) {
                for (int i = srcPosition; i < targetPosition; i++) {
                    Collections.swap(mFollowList, i, i + 1);
                }
            } else {
                for (int i = srcPosition; i > targetPosition; i--) {
                    Collections.swap(mFollowList, i, i - 1);
                }
            }
        }
    }

    // 将List中src位置的数据，调整到target位置
    private List<String> changeListItemPosition(List<String> mFollowList, int srcPosition, int targetPosition) {
        if (mFollowList == null || srcPosition < 0 || targetPosition < 0) {
            return null;
        }
        // 更换数据源中的数据Item的位置
        List<String> tempList = new ArrayList<>();
        if (srcPosition != targetPosition) {
            // 源位置在目标位置后面
            if (srcPosition > targetPosition) {
                for (int i = 0; i < targetPosition; i++) {
                    tempList.add(mFollowList.get(i));
                }
                tempList.add(mFollowList.get(srcPosition));
                for (int i = targetPosition; i < srcPosition; i++) {
                    tempList.add(mFollowList.get(i));
                }
                for (int i = srcPosition + 1; i < mFollowList.size(); i++) {
                    tempList.add(mFollowList.get(i));
                }
            } else {
                // 源位置在目标位置前面
                for (int i = 0; i < srcPosition; i++) {
                    tempList.add(mFollowList.get(i));
                }
                for (int i = srcPosition + 1; i < targetPosition + 1; i++) {
                    tempList.add(mFollowList.get(i));
                }
                tempList.add(mFollowList.get(srcPosition));
                for (int i = targetPosition + 1; i < mFollowList.size(); i++) {
                    tempList.add(mFollowList.get(i));
                }
            }
        }
        return tempList;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_drag_view_pager_title);
        initViews();
    }

    private void initViews() {
        mButton = (Button) findViewById(R.id.bt_drag_tab);
        mButton.setOnClickListener(this);
        Bundle bundle = getIntent().getExtras();
        // 获取传递过来的Title数据
        if (bundle != null) {
            /**
             * 这个需要注意，如果只是单纯的Arrays.asList的话返回的不是ArrayList，而是Arrays$ArrayLis，
             * 没有重写remove，add等方法，调用时会报错 UnsupportedOperationException
             */
            mFollowList = new ArrayList<>(Arrays.asList(bundle.getStringArray("title")));
        }
        // 求差集，即用户当前还未关注的Tab
        mRestList = removeList(mFollowList);
        /**
         * 以下
         */
        mRecyclerView = (RecyclerView) findViewById(R.id.rv_drag_title);
        mLayoutManager = new GridLayoutManager(this, 4, GridLayoutManager.VERTICAL, false);
        mRecyclerViewAdapter = new NewsTabRecyclerViewAdapter(this, mFollowList);
        // 已关注的Tab
        mRecyclerViewAdapter.setOnItemClickListener(new NewsTabRecyclerViewAdapter.OnItemClickListener() {
            // item点击事件
            @Override
            public void onItemClick(View view, int position) {
                // 要删除的新闻Tab标题
                String deleteItem = mRecyclerViewAdapter.getDatas().get(position);
                // 从已关注列表中删除
                mRecyclerViewAdapter.deleteListItem(position);
                // 添加到未关注列表
                mRestRecyclerViewAdapter.addItem(deleteItem);
                SnackbarUtil.show(view, "删除成功", 1);
            }

            // item长按事件
            @Override
            public void onItemLongClick(View view, int position) {
                SnackbarUtil.show(view, "长按后拖拽移动位置", 0);
            }
        });

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
        // 未选择的Tab的recyclerview中的item点击事件
        mRestRecyclerViewAdapter.setOnItemClickListener(new NewsTabRecyclerViewAdapter.OnItemClickListener() {
            // 点击Item操作
            @Override
            public void onItemClick(View view, int position) {
                // 要添加的新闻Tab标题
                String deleteItem = mRestRecyclerViewAdapter.getDatas().get(position);
                // 从未关注列表中删除
                mRestRecyclerViewAdapter.deleteListItem(position);
                // 添加到关注列表
                mRecyclerViewAdapter.addItem(deleteItem);
                SnackbarUtil.show(view, "添加成功", 1);
            }

            // 长按操作
            @Override
            public void onItemLongClick(View view, int position) {
                return;
            }
        });
        mRestRecyclerView.setAdapter(mRestRecyclerViewAdapter);
        mRestRecyclerView.setLayoutManager(mRestLayoutManager);
    }

    private List<String> removeList(List<String> mFollowList) {
        // 给List重新赋值为所有Tab的List
        mList = new ArrayList<>(Arrays.asList(BaseApplication.getInstance().getmTabs()));
        List<String> result = null;
        if (mList != null && mFollowList != null) {
            mList.removeAll(mFollowList);
            result = mList;
        }
        return result;
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.bt_drag_tab:
                Bundle bundle = new Bundle();
                bundle.putStringArrayList("changeTab", (ArrayList<String>) mRecyclerViewAdapter.getDatas());
                showActivity(MainActivity.class, bundle);
                break;
        }
    }
}
