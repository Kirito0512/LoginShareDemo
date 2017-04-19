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
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.bumptech.glide.Glide;
import com.example.xuqi.qqdemo.R;
import com.example.xuqi.qqdemo.bean.NewsInfo;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Monkey on 2015/6/29.
 */
public class MyRecyclerViewAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    // 上拉加载更多
    public static final int PULLUP_LOAD_MORE = 0;
    // 正在加载中
    public static final int LOADING_MORE = 1;
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

    public interface OnItemClickListener {
        void onItemClick(View view, int position);

        void onItemLongClick(View view, int position);
    }

    public OnItemClickListener mOnItemClickListener;

    public void setOnItemClickListener(OnItemClickListener listener) {
        this.mOnItemClickListener = listener;
    }

    public Context mContext;
    public List<String> mDatas;
    public List<NewsInfo> newsList;
    public LayoutInflater mLayoutInflater;

    public MyRecyclerViewAdapter(Context mContext) {
        this.mContext = mContext;
        mLayoutInflater = LayoutInflater.from(mContext);
        mDatas = new ArrayList<>();
        newsList = new ArrayList<>();
        // 共58项
        for (int i = 'A'; i <= 'z'; i++) {
            mDatas.add((char) i + "");
        }
    }

    /**
     * 创建ViewHolder
     */
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        // 普通Item
//        if (viewType == TYPE_ITEM) {
//            View mView = mLayoutInflater.inflate(R.layout.item_recyclerview, parent, false);
//            MyRecyclerViewHolder mViewHolder = new MyRecyclerViewHolder(mView);
//            return mViewHolder;
//        }
        if (viewType == TYPE_ONE_PIC_ITEM) {
            View mView = mLayoutInflater.inflate(R.layout.item_one_pic_recyclerview, parent, false);
            OnePicRecyclerViewHolder mViewHolder = new OnePicRecyclerViewHolder(mView);
            return mViewHolder;
        } else if(viewType == TYPE_THREE_PIC_ITEM){
            View mView = mLayoutInflater.inflate(R.layout.item_three_pic_recyclerview, parent, false);
            ThreePicRecyclerViewHolder mViewHolder = new ThreePicRecyclerViewHolder(mView);
            return mViewHolder;
        } else if (viewType == TYPE_FOOTER) {  // footItem
            View mView = mLayoutInflater.inflate(R.layout.foot_item_recyclerview, parent, false);
            MyRecyclerViewFootHolder mViewHolder = new MyRecyclerViewFootHolder(mView);
            return mViewHolder;
        }
        return null;
    }

    /**
     * 绑定ViewHoler，给item中的控件设置数据
     */
    @Override
    public void onBindViewHolder(final RecyclerView.ViewHolder holder, final int position) {
//        if (holder instanceof MyRecyclerViewHolder) {
//            // 转型为MyRecyclerViewHolder
//            final MyRecyclerViewHolder itemholder = (MyRecyclerViewHolder) holder;
//            if (mOnItemClickListener != null) {
//                itemholder.itemView.setOnClickListener(new View.OnClickListener() {
//                    @Override
//                    public void onClick(View v) {
//                        mOnItemClickListener.onItemClick(itemholder.itemView, position);
//                    }
//                });
//
//                itemholder.itemView.setOnLongClickListener(new View.OnLongClickListener() {
//                    @Override
//                    public boolean onLongClick(View v) {
//                        mOnItemClickListener.onItemLongClick(itemholder.itemView, position);
//                        return true;
//                    }
//                });
//            }
//            itemholder.mTextView.setText(mDatas.get(position));
//        }
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
            itemholder.tvThrPicTitle.setText(newsList.get(position).getTitle());
            itemholder.tvThrPicAuthor.setText(newsList.get(position).getAuthor_name());
            itemholder.tvThrPicTime.setText(newsList.get(position).getDate());
            Glide.with(mContext).load(newsList.get(position).getThumbnail_pic_s()).into(itemholder.ivThrPicOne);
            Glide.with(mContext).load(newsList.get(position).getThumbnail_pic_s02()).into(itemholder.ivThrPicTwo);
            Glide.with(mContext).load(newsList.get(position).getThumbnail_pic_s03()).into(itemholder.ivThrPicThree);
        } else if (holder instanceof MyRecyclerViewFootHolder) {
            // 转型为 MyRecyclerViewFootHolder
            MyRecyclerViewFootHolder footViewHolder = (MyRecyclerViewFootHolder) holder;
            switch (load_more_status) {
                case PULLUP_LOAD_MORE:
                    footViewHolder.footview_item.setText("上拉加载更多......");
                    break;
                case LOADING_MORE:
                    footViewHolder.footview_item.setText("正在加载更多数据......");
                    break;
            }
        }
    }

    @Override
    public int getItemViewType(int position) {
        // 将最后一个Item设置为footView
//        if (position + 1 == getItemCount()) {
//            return TYPE_FOOTER;
//        } else {
//            return TYPE_ITEM;
//        }
        // 底部上拉刷新item
        if (position + 1 == getItemCount()) {
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
    public void changeMoreStatus(int status) {
        load_more_status = status;
        notifyDataSetChanged();
    }

    public void addMoreItem(List<NewsInfo> newDatas) {
        newsList.addAll(newDatas);
        notifyDataSetChanged();
    }

    // 添加NewsInfo的list进去
    public void deleteAndAddItem(List<NewsInfo> newsDatas) {
        newsList.clear();
        newsList.addAll(newsDatas);
        notifyDataSetChanged();
    }

    @Override
    public int getItemCount() {
//        return mDatas.size() + 1;
        return newsList.size() + 1;
    }

    public void deleteItem(int position) {
//        if (position > -1 && position < mDatas.size()) {
//            mDatas.remove(position);
//            notifyDataSetChanged();
//        }
        if (position > -1 && position < newsList.size()) {
            newsList.remove(position);
            notifyDataSetChanged();
        }
    }
}
