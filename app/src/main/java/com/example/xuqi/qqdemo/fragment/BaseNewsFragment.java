package com.example.xuqi.qqdemo.fragment;

import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.view.View;

import com.example.xuqi.qqdemo.adapter.MyRecyclerViewAdapter;

/**
 * Created by xuqi on 17/4/19.
 */

public class BaseNewsFragment extends Fragment implements SwipeRefreshLayout.OnRefreshListener, MyRecyclerViewAdapter.OnItemClickListener{
    @Override
    public void onRefresh() {

    }

    @Override
    public void onItemClick(View view, int position) {

    }

    @Override
    public void onItemLongClick(View view, int position) {

    }
}
