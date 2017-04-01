package com.example.xuqi.qqdemo.fragment;

import android.content.DialogInterface;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.xuqi.qqdemo.R;
import com.example.xuqi.qqdemo.adapter.MyRecyclerViewAdapter;
import com.example.xuqi.qqdemo.util.SnackbarUtil;

import java.util.ArrayList;
import java.util.List;

import static android.content.ContentValues.TAG;


/**
 * Created by xuqi on 17/3/9.
 */

public class MainSlidingFragment extends Fragment implements SwipeRefreshLayout.OnRefreshListener, MyRecyclerViewAdapter.OnItemClickListener {

    private View mView;
    private SwipeRefreshLayout mSwipeRefreshLayout;
    // 左滑呼出删除按钮的RecyclerView
//    private LeftSwipeMenuRecyclerView mLeftSwipeMenuRecyclerView;
//    private LinearLayoutManager mLayoutManager;
//    private SlideRecyclerViewAdapter mRecyclerViewAdapter;

    // 正常的RecyclerView样式
    private RecyclerView mRecyclerView;
    private LinearLayoutManager mLayoutManager;
    private MyRecyclerViewAdapter mRecyclerViewAdapter;
    private int lastVisibleItem;

    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        mView = inflater.inflate(R.layout.frag_main, container, false);
        return mView;
    }
    
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        mSwipeRefreshLayout = (SwipeRefreshLayout) mView.findViewById(R.id.id_swiperefreshlayout);
        // 设置刷新时，指示器旋转时的颜色
        mSwipeRefreshLayout.setColorSchemeResources(R.color.main_blue_light, R.color.main_blue_dark);
        mSwipeRefreshLayout.setOnRefreshListener(this);

        // mLeftSwipeMenuRecyclerView = (LeftSwipeMenuRecyclerView) mView.findViewById(R.id.slide_recyclerview);
        mRecyclerView = (RecyclerView) mView.findViewById(R.id.id_recyclerview);
        mLayoutManager = new LinearLayoutManager(getActivity(), LinearLayoutManager.VERTICAL, false);

        mRecyclerViewAdapter = new MyRecyclerViewAdapter(getActivity());
        mRecyclerViewAdapter.setOnItemClickListener(this);
        // 绑定Adapter
        mRecyclerView.setAdapter(mRecyclerViewAdapter);
        // 设置LayoutManager
        mRecyclerView.setLayoutManager(mLayoutManager);
//        mRecyclerViewAdapter = new SlideRecyclerViewAdapter(getActivity(), list);
//        mLeftSwipeMenuRecyclerView.setAdapter(mRecyclerViewAdapter);
//        mLeftSwipeMenuRecyclerView.setOnItemActionListener(new OnItemActionListener() {
//            @Override
//            public void OnItemClick(int position) {
//
//            }
//
//            @Override
//            public void OnItemTop(int position) {
//
//            }
//
//            @Override
//            public void OnItemDelete(int position) {
//
//            }
//        });
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            mRecyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
                @Override
                // 滚动状态变化时回调

                // newState表示当前滚动状态
                // newState有三个值
                // SCROLL_STATE_IDLE = 0 表示静止没有滚动
                // SCROLL_STATE_DRAGGING = 1 表示正在被外部拖拽，一般为用户正在用手指滚动
                // SCROLL_STATE_SETTLING = 2 表示自动滚动
                public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
                    super.onScrollStateChanged(recyclerView, newState);
                    Log.d(TAG, "onScrollStateChanged: lastVisibleItem = "+lastVisibleItem);
                    if(newState == RecyclerView.SCROLL_STATE_IDLE && lastVisibleItem + 1 == mRecyclerViewAdapter.getItemCount()) {
                        new Handler().postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                Log.d(TAG, "run: 上拉刷新");
                                List<String> newDatas = new ArrayList<String>();
                                for(int i=0; i < 5;i++){
                                    int index = i+1;
                                    newDatas.add("more time" + index);
                                }
                                mRecyclerViewAdapter.addMoreItem(newDatas);
                            }
                        },1000);
                    }
                }

                @Override
                // 滚动时回调

//                dx : 水平滚动距离
//                dy : 垂直滚动距离
//                dx > 0时为手指向左滚动, 列表滚动显示右面的内容
//                dx < 0时为手指向右滚动, 列表滚动显示左面的内容
//                dy > 0时为手指向上滚动, 列表滚动显示下面的内容
//                dy < 0时为手指向下滚动, 列表滚动显示上面的内容
                public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                    super.onScrolled(recyclerView,dx,dy);
                    lastVisibleItem = mLayoutManager.findLastVisibleItemPosition();
                    Log.d(TAG, "onScrolled: lastVisibleItem = "+lastVisibleItem);
                }
            });
        }
    }

    @Override
    public void onRefresh() {

        // 刷新时模拟数据的变化
        new Handler().postDelayed(new Runnable() {
            @Override public void run() {
                mSwipeRefreshLayout.setRefreshing(false);
                int temp = (int) (Math.random() * 10);
//                if (flag != STAGGERED_GRID) {
                mRecyclerViewAdapter.mDatas.add(0, "new" + temp);
                mRecyclerViewAdapter.notifyDataSetChanged();
//                } else {
//                    mStaggeredAdapter.mDatas.add(0, "new" + temp);
//                    mStaggeredAdapter.mHeights.add(0, (int) (Math.random() * 300) + 200);
//                    mStaggeredAdapter.notifyDataSetChanged();
//                }
            }
        }, 1000);
    }

    @Override
    public void onItemClick(View view, int position) {
        SnackbarUtil.show(mRecyclerView, getString(R.string.item_clicked), 0);
    }

    @Override
    public void onItemLongClick(View view, int position) {
//        mRecyclerViewAdapter.deleteItem(position);
        showAlertDialog(position);
        //SnackbarUtil.show(mRecyclerView, getString(R.string.item_longclicked), 0);
    }

    public void showAlertDialog (final int position) {
        new AlertDialog.Builder(getActivity()).setTitle("列表框")
                .setItems(getResources().getStringArray(R.array.choose_log_click), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        if (which == 0) {
                            SnackbarUtil.show(getView(),"收藏",0);
                        } else if (which == 1) {
                            mRecyclerViewAdapter.deleteItem(position);
                        }
                        dialog.dismiss();
                    }
                }).show();
                //.setNegativeButton("确定", null).show();
    }
}
