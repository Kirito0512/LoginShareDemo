package com.example.xuqi.qqdemo.adapter;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.xuqi.qqdemo.R;

/**
 * Created by xuqi on 17/4/12.
 * 新闻列表中  "下面三张图上面一行文字"  类型的Item
 */

public class ThreePicRecyclerViewHolder extends RecyclerView.ViewHolder {
    private TextView tvThrPicTitle, tvThrPicAuthor, tvThrPicTime;
    private ImageView ivThrPicOne, ivThrPicTwo, ivThrPicThree;

    public ThreePicRecyclerViewHolder(View itemView) {
        super(itemView);
        tvThrPicTitle = (TextView) itemView.findViewById(R.id.tv_three_pic_author);
        tvThrPicTime = (TextView) itemView.findViewById(R.id.tv_three_pic_time);
        tvThrPicAuthor = (TextView) itemView.findViewById(R.id.tv_three_pic_author);

        ivThrPicOne = (ImageView) itemView.findViewById(R.id.iv_three_pic_one);
        ivThrPicTwo = (ImageView) itemView.findViewById(R.id.iv_three_pic_two);
        ivThrPicThree = (ImageView) itemView.findViewById(R.id.iv_three_pic_three);

    }
}
