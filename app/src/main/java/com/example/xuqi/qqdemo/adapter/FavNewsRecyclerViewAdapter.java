package com.example.xuqi.qqdemo.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.bumptech.glide.Glide;
import com.example.xuqi.qqdemo.R;
import com.example.xuqi.qqdemo.ViewHolder.OnePicRecyclerViewHolder;
import com.example.xuqi.qqdemo.ViewHolder.ThreePicRecyclerViewHolder;
import com.example.xuqi.qqdemo.bean.NewsInfo;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by xuqi on 17/5/1.
 */

public class FavNewsRecyclerViewAdapter extends RecyclerView.Adapter {

    public Context mContext;
    public List<NewsInfo> newsList;
    public LayoutInflater mLayoutInflater;
    // item点击事件监听器
    public OnItemClickListener mOnItemClickListener;

    private static final int TYPE_ONE_PIC_ITEM = 100;
    private static final int TYPE_THREE_PIC_ITEM = 101;


    public interface OnItemClickListener {
        void onItemClick(View view, int position);

        void onItemLongClick(View view, int position);
    }

    public void setOnItemClickListener(OnItemClickListener listener) {
        this.mOnItemClickListener = listener;
    }

    // Adapter构造方法
    public FavNewsRecyclerViewAdapter(Context mContext) {
        this.mContext = mContext;
        mLayoutInflater = LayoutInflater.from(mContext);
        newsList = new ArrayList<>();
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        if (viewType == TYPE_ONE_PIC_ITEM) {
            View mView = mLayoutInflater.inflate(R.layout.item_one_pic_recyclerview, parent, false);
            OnePicRecyclerViewHolder mViewHolder = new OnePicRecyclerViewHolder(mView);
            return mViewHolder;
        } else if (viewType == TYPE_THREE_PIC_ITEM) {
            View mView = mLayoutInflater.inflate(R.layout.item_three_pic_recyclerview, parent, false);
            ThreePicRecyclerViewHolder mViewHolder = new ThreePicRecyclerViewHolder(mView);
            return mViewHolder;
        }
        return null;
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, final int position) {
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
        }
    }

    @Override
    public int getItemCount() {
        return newsList.size();
    }

    @Override
    public int getItemViewType(int position) {
        // 一张图的item
        if (newsList.get(position).getThumbnail_pic_s03() == null) {
            return TYPE_ONE_PIC_ITEM;
        }
        // 三张图的item
        else if (newsList.get(position).getThumbnail_pic_s03() != null) {
            return TYPE_THREE_PIC_ITEM;
        }
        return -1;
    }

    public void deleteItem(int position) {
        if (position > -1 && position < newsList.size()) {
            newsList.remove(position);
            notifyDataSetChanged();
        }
    }

    public void setAdapterData(List<NewsInfo> mList) {
        this.newsList = mList;
        notifyDataSetChanged();
    }
}
