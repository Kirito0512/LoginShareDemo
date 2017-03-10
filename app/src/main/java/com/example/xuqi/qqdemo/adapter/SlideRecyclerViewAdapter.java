package com.example.xuqi.qqdemo.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.example.xuqi.qqdemo.R;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Administrator on 2016/8/15.
 */
public class SlideRecyclerViewAdapter extends RecyclerView.Adapter<SlideRecyclerViewAdapter.Holder> {
    public List<String> list = new ArrayList<String>();
    private Context context;

    public SlideRecyclerViewAdapter(Context context, List<String> list) {
        this.list = list;
        this.context = context;
    }


    @Override
    public Holder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_slide_recyclerview, parent, false);
        Holder holder = new Holder(view);
        return holder;
    }

    @Override
    public void onBindViewHolder(Holder holder, int position) {
        holder.tvContent.setText(list.get(position));
    }


    @Override
    public int getItemCount() {
        return list.size();
    }

    public class Holder extends RecyclerView.ViewHolder {
        public TextView tvContent;
        public TextView tvDelete;
        public TextView tvTop;
        public LinearLayout llLayout;

        public Holder(View itemView) {
            super(itemView);
            tvContent = (TextView) itemView.findViewById(R.id.recycler_Content);
            tvDelete = (TextView) itemView.findViewById(R.id.recycler_Delete);
            tvTop = (TextView) itemView.findViewById(R.id.tvTop);
            llLayout= (LinearLayout) itemView.findViewById(R.id.recycler_layout);
        }
    }
}
