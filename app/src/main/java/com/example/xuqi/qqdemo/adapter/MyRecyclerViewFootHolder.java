package com.example.xuqi.qqdemo.adapter;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.TextView;

import com.example.xuqi.qqdemo.R;

/**
 * Created by xuqi on 17/3/18.
 */

public class MyRecyclerViewFootHolder extends RecyclerView.ViewHolder {
    public TextView footview_item;
    public MyRecyclerViewFootHolder(View itemView) {
        super(itemView);
        footview_item = (TextView) itemView.findViewById(R.id.foot_view_item);
    }
}
