package com.example.xuqi.qqdemo.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.xuqi.qqdemo.R;
import com.example.xuqi.qqdemo.ViewHolder.NewsTabViewHolder;
import com.example.xuqi.qqdemo.util.L;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by xuqi on 17/4/27.
 * 修改新闻tab的recyclerview的适配器
 */

public class NewsTabRecyclerViewAdapter extends RecyclerView.Adapter<NewsTabViewHolder> {

    public interface OnItemClickListener {
        void onItemClick(View view, int position);

        void onItemLongClick(View view, int position);
    }

    public OnItemClickListener mOnItemClickListener;

    public void setOnItemClickListener(OnItemClickListener listener) {
        this.mOnItemClickListener = listener;
    }

    public Context mContext;
    private List<String> mDatas;
    public LayoutInflater mLayoutInflater;

    public NewsTabRecyclerViewAdapter(Context mContext, List<String> mList) {
        this.mContext = mContext;
        mLayoutInflater = LayoutInflater.from(mContext);
        mDatas = new ArrayList<>();
        mDatas = mList;
    }

    /**
     * 创建ViewHolder
     */
    @Override
    public NewsTabViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View mView = mLayoutInflater.inflate(R.layout.item_news_tab, parent, false);
        NewsTabViewHolder mViewHolder = new NewsTabViewHolder(mView);
        return mViewHolder;
    }

    /**
     * 绑定ViewHoler，给item中的控件设置数据
     */
    @Override
    public void onBindViewHolder(final NewsTabViewHolder holder, final int position) {
        if (mOnItemClickListener != null) {
            holder.itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    mOnItemClickListener.onItemClick(holder.itemView, position);
                }
            });

            holder.itemView.setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View v) {
                    mOnItemClickListener.onItemLongClick(holder.itemView, position);
                    return true;
                }
            });
        }

        holder.tvNewsTab.setText(mDatas.get(position));
    }

    @Override
    public int getItemCount() {
        return mDatas.size();
    }

    public void deleteListItem(int position) {
        if (position > -1 && position < mDatas.size()) {
            try {
                mDatas.remove(position);
                notifyDataSetChanged();
            } catch (Exception e) {
                L.d("e.tostring" + e.toString());
            }
        }
    }

    public void addAfterdeleteAllItem(List<String> list) {
        if (list != null) {
            mDatas.clear();
            mDatas.addAll(list);
            notifyDataSetChanged();
        }
    }

    // 添加一个tab
    public void addItem(String item) {
        if (item != null) {
            mDatas.add(item);
            notifyDataSetChanged();
        }
    }

    public List<String> getDatas() {
        return mDatas;
    }

    public void setListItems(List<String> mList) {
        if (mList != null)
            mDatas = mList;
    }
}
