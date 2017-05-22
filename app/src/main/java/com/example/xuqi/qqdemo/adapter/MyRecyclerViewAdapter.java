/*
 *
 *  *
 *  *  *
 *  *  *  * ===================================
 *  *  *  * Copyright (c) 2016.
 *  *  *  * 作者：安卓猴
 *  *  *  * 微博：@安卓猴
 *  *  *  * 博客：http://sunjiajia.com
 *  *  *  * Github：https://github.com/opengit
 *  *  *  *
 *  *  *  * 注意**：如果您使用或者修改该代码，请务必保留此版权信息。
 *  *  *  * ===================================
 *  *  *
 *  *  *
 *  *
 *
 */

package com.example.xuqi.qqdemo.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.RecyclerView.ViewHolder;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.bumptech.glide.Glide;
import com.example.xuqi.qqdemo.R;
import com.example.xuqi.qqdemo.ViewHolder.MyRecyclerViewFootHolder;
import com.example.xuqi.qqdemo.ViewHolder.NewsRefreshViewholder;
import com.example.xuqi.qqdemo.ViewHolder.OnePicRecyclerViewHolder;
import com.example.xuqi.qqdemo.ViewHolder.ThreePicRecyclerViewHolder;
import com.example.xuqi.qqdemo.bean.NewsInfo;
import com.example.xuqi.qqdemo.util.L;

import java.util.ArrayList;
import java.util.List;

import static android.view.View.GONE;

/**
 * Created by Monkey on 2015/6/29.
 */
public class MyRecyclerViewAdapter extends RecyclerView.Adapter<ViewHolder> {

    // 上拉加载更多
    public static final int PULLUP_LOAD_MORE = 0;
    // 正在加载中
    public static final int LOADING_MORE = 1;
    // 隐藏上拉加载
    public static final int HIDE_PULLUP = 2;
    // 上拉加载更多状态 默认为0
    private int load_more_status = 0;

    // 普通ItemView
    private static final int TYPE_ITEM = 0;
    // 底部FootView
    private static final int TYPE_FOOTER = 1;
    // 新闻列表中  "左图右文字"  类型的Item
    private static final int TYPE_ONE_PIC_ITEM = TYPE_FOOTER + 1;
    // 新闻列表中  "下面三张图上面一行文字"  类型的Item
    private static final int TYPE_THREE_PIC_ITEM = TYPE_ONE_PIC_ITEM + 1;
    // 初次打开显示正在加载界面
    private static final int TYPE_REFRESH = TYPE_THREE_PIC_ITEM + 1;

    public Context mContext;
    public List<NewsInfo> newsList;
    public LayoutInflater mLayoutInflater;

    public interface OnItemClickListener {
        void onItemClick(View view, int position);

        void onItemLongClick(View view, int position);
    }

    // item点击事件监听器
    public OnItemClickListener mOnItemClickListener;

    public void setOnItemClickListener(OnItemClickListener listener) {
        this.mOnItemClickListener = listener;
    }

    public MyRecyclerViewAdapter(Context mContext) {
        this.mContext = mContext;
        mLayoutInflater = LayoutInflater.from(mContext);
        newsList = new ArrayList<>();
    }

    /**
     * 创建ViewHolder
     */
    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        if (viewType == TYPE_ONE_PIC_ITEM) {
            View mView = mLayoutInflater.inflate(R.layout.item_one_pic_recyclerview, parent, false);
            OnePicRecyclerViewHolder mViewHolder = new OnePicRecyclerViewHolder(mView);
            return mViewHolder;
        } else if (viewType == TYPE_THREE_PIC_ITEM) {
            View mView = mLayoutInflater.inflate(R.layout.item_three_pic_recyclerview, parent, false);
            ThreePicRecyclerViewHolder mViewHolder = new ThreePicRecyclerViewHolder(mView);
            return mViewHolder;
        } else if (viewType == TYPE_FOOTER) {  // footItem
            View mView = mLayoutInflater.inflate(R.layout.foot_item_recyclerview, parent, false);
            MyRecyclerViewFootHolder mViewHolder = new MyRecyclerViewFootHolder(mView);
            return mViewHolder;
        } else if (viewType == TYPE_REFRESH) {
            View mView = mLayoutInflater.inflate(R.layout.item_refresh_recyclerview, parent, false);
            NewsRefreshViewholder mViewHolder = new NewsRefreshViewholder(mView);
            return mViewHolder;
        }
        return null;
    }

    /**
     * 绑定ViewHoler，给item中的控件设置数据
     */
    @Override
    public void onBindViewHolder(final ViewHolder holder, final int position) {
        // 新闻列表中  "左图右文字"  类型的Item
        if (holder instanceof OnePicRecyclerViewHolder) {
            final OnePicRecyclerViewHolder itemholder = (OnePicRecyclerViewHolder) holder;
            // 设置item的点击事件 点击&长按
            if (mOnItemClickListener != null) {
                itemholder.itemView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        mOnItemClickListener.onItemClick(itemholder.itemView, position);

                    }
                });

                itemholder.itemView.setOnLongClickListener(new View.OnLongClickListener() {
                    @Override
                    public boolean onLongClick(View v) {
                        mOnItemClickListener.onItemLongClick(itemholder.itemView, position);
                        return true;
                    }
                });
            }
            // 加载信息
            itemholder.tvOnePicTitle.setText(newsList.get(position).getTitle());
            itemholder.tvOnePicAuthor.setText(newsList.get(position).getAuthor_name());
            itemholder.tvOnePicTime.setText(newsList.get(position).getDate());
            String url = newsList.get(position).getThumbnail_pic_s();
            Glide.with(mContext).load(url).into(itemholder.imOnePicImage);
        }
        // 新闻列表中  "下面三张图上面一行文字"  类型的Item
        else if (holder instanceof ThreePicRecyclerViewHolder) {
            final ThreePicRecyclerViewHolder itemholder = (ThreePicRecyclerViewHolder) holder;
            // 设置item的点击事件 点击&长按
            if (mOnItemClickListener != null) {
                itemholder.itemView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        mOnItemClickListener.onItemClick(itemholder.itemView, position);
                    }
                });

                itemholder.itemView.setOnLongClickListener(new View.OnLongClickListener() {
                    @Override
                    public boolean onLongClick(View v) {
                        mOnItemClickListener.onItemLongClick(itemholder.itemView, position);
                        return true;
                    }
                });
            }
            // 加载新闻文字信息
            itemholder.tvThrPicTitle.setText(newsList.get(position).getTitle());
            itemholder.tvThrPicAuthor.setText(newsList.get(position).getAuthor_name());
            itemholder.tvThrPicTime.setText(newsList.get(position).getDate());
            // 加载三张图片
            Glide.with(mContext).load(newsList.get(position).getThumbnail_pic_s()).into(itemholder.ivThrPicOne);
            Glide.with(mContext).load(newsList.get(position).getThumbnail_pic_s02()).into(itemholder.ivThrPicTwo);
            Glide.with(mContext).load(newsList.get(position).getThumbnail_pic_s03()).into(itemholder.ivThrPicThree);
        } else if (holder instanceof MyRecyclerViewFootHolder) {
            // 转型为 MyRecyclerViewFootHolder
            MyRecyclerViewFootHolder footViewHolder = (MyRecyclerViewFootHolder) holder;
            switch (load_more_status) {
                case PULLUP_LOAD_MORE:
                    if (footViewHolder.tv_footview.getVisibility() == GONE)
                        footViewHolder.tv_footview.setVisibility(View.VISIBLE);

                    if (footViewHolder.pb_footview.getVisibility() == GONE)
                        footViewHolder.pb_footview.setVisibility(View.VISIBLE);

                    footViewHolder.tv_footview.setText("上拉加载更多......");
                    break;
                case LOADING_MORE:
                    footViewHolder.tv_footview.setText("正在加载更多数据......");
                    break;
                case HIDE_PULLUP:
                    footViewHolder.tv_footview.setVisibility(GONE);
                    footViewHolder.pb_footview.setVisibility(GONE);
            }
        }
    }

    @Override
    public int getItemViewType(int position) {
        L.d("itemCount = " + getItemCount());
        // 刚打开APP，还未加载上数据时显示的进度条
        if (getItemCount() == 1) {
            return TYPE_REFRESH;
        }
        // 底部上拉刷新item
        else if (position + 1 == getItemCount()) {
            return TYPE_FOOTER;
        }
        // 一张图的item
        else if (newsList.get(position).getThumbnail_pic_s03() == null) {
            return TYPE_ONE_PIC_ITEM;
        }
        // 三张图的item
        else if (newsList.get(position).getThumbnail_pic_s03() != null) {
            return TYPE_THREE_PIC_ITEM;
        }
        return -1;
    }

    /*
        *上拉加载更多
        * PULL_LOAD_MORE = 0
        * 正在加载更多
        * LOADING_MORE = 1
        * 加载完成，没有更多数据了
        * NO_MORE_DATA = 2
       */
    public void setMoreStatus(int status) {
        load_more_status = status;
        // 必须要有这句代码
        notifyDataSetChanged();
    }

    public void addMoreItem(List<NewsInfo> newDatas) {
        newsList.addAll(newDatas);
        notifyDataSetChanged();
    }

    // 下拉刷新时，为了能够一页一页的加载item，我们先把原有的list与刷新得到的list合并，再通过上拉刷新，一页一页通过addMoreItem加回来，原有的list我们直接clear掉
    public void deleteAllItem() {
        newsList.clear();
        notifyDataSetChanged();
    }

    // 获取adapter里的news item数据
    public List<NewsInfo> getListItem() {
        if (newsList != null)
            return newsList;
        return null;
    }

    @Override
    public int getItemCount() {
        return newsList.size() + 1;
    }

    public void deleteItem(int position) {
        if (position > -1 && position < newsList.size()) {
            newsList.remove(position);
            notifyDataSetChanged();
        }
    }
}
