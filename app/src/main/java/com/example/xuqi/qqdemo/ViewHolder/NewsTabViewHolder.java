package com.example.xuqi.qqdemo.ViewHolder;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.TextView;

import com.example.xuqi.qqdemo.R;

/**
 * Created by xuqi on 17/4/27.
 * 拖拽调整，增删新闻Tab的界面的RecyclerView的item布局
 */

public class NewsTabViewHolder extends RecyclerView.ViewHolder {
    public TextView tvNewsTab;

    public NewsTabViewHolder(View itemView) {
        super(itemView);
        tvNewsTab = (TextView) itemView.findViewById(R.id.tv_item_news_tab);
    }
}
