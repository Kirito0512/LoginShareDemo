package com.example.xuqi.qqdemo.ViewHolder;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.example.xuqi.qqdemo.R;

/**
 * Created by xuqi on 17/3/18.
 * 上拉刷新
 */

public class MyRecyclerViewFootHolder extends RecyclerView.ViewHolder {
    public TextView tv_footview;
    public ProgressBar pb_footview;

    public MyRecyclerViewFootHolder(View itemView) {
        super(itemView);
        tv_footview = (TextView) itemView.findViewById(R.id.tv_foot_view_item);
        pb_footview = (ProgressBar) itemView.findViewById(R.id.pb_foot_view_item);
    }
}
