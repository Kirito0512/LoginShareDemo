package com.example.xuqi.qqdemo.adapter;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.xuqi.qqdemo.R;

/**
 * Created by xuqi on 17/4/12.
 * 新闻列表中  "左图右文字"  类型的Item
 */

public class OnePicRecyclerViewHolder extends RecyclerView.ViewHolder {
    private TextView tvOnePicTitle,tvOnePicAuthor,tvOnePicTime;
    private ImageView imOnePicImage;
    public OnePicRecyclerViewHolder(View itemView) {
        super(itemView);
        tvOnePicAuthor = (TextView) itemView.findViewById(R.id.tv_one_pic_item_author);
        tvOnePicTime = (TextView) itemView.findViewById(R.id.tv_one_pic_time);
        tvOnePicTitle = (TextView) itemView.findViewById(R.id.tv_one_pic_item_title);
    }
}
