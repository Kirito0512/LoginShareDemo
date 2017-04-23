package com.example.xuqi.qqdemo.fragment;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import com.android.volley.Request;
import com.android.volley.toolbox.JsonObjectRequest;
import com.example.xuqi.qqdemo.R;
import com.example.xuqi.qqdemo.adapter.MyRecyclerViewAdapter;
import com.example.xuqi.qqdemo.application.BaseApplication;
import com.example.xuqi.qqdemo.bean.NewsInfo;
import com.example.xuqi.qqdemo.netdata.GsonData;
import com.example.xuqi.qqdemo.util.L;
import com.example.xuqi.qqdemo.util.SnackbarUtil;
import com.example.xuqi.qqdemo.view.NewsContentActivity;
import com.example.xuqi.qqdemo.widget.RVDividerItemDecoration;
import org.json.JSONException;
import java.lang.ref.WeakReference;
import java.util.List;
import java.util.Random;
import static android.content.ContentValues.TAG;
import static com.example.xuqi.qqdemo.Constants.NEWS_API_ADDRESS;
import static com.example.xuqi.qqdemo.Constants.NEWS_APP_KEY;


/**
 * Created by xuqi on 17/3/9.
 * 新闻Fragment
 */

public class NewsFragment extends BaseNewsFragment {

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
    // 下拉刷新获取的newsList
    private List<NewsInfo> newsList;
    // 上拉刷新将newsList分隔成一页页
    private List<NewsInfo> subNewsList;
    private Handler handler = new MyHandler(this);
    public static final String ARGUMENT = "argument";
    private String newsType;


    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        mView = inflater.inflate(R.layout.frag_main, container, false);
        return mView;
    }

    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        Bundle bundle = getArguments();
        // 获取从MainActivity传入的值，即新闻类别
        if (bundle != null)
            newsType = bundle.getString(ARGUMENT);

        mSwipeRefreshLayout = (SwipeRefreshLayout) mView.findViewById(R.id.id_swiperefreshlayout);
        // 设置刷新时，指示器旋转时的颜色
        mSwipeRefreshLayout.setColorSchemeResources(R.color.main_blue_light, R.color.main_blue_dark);
        mSwipeRefreshLayout.setOnRefreshListener(this);

        mRecyclerView = (RecyclerView) mView.findViewById(R.id.id_recyclerview);
        mLayoutManager = new LinearLayoutManager(getActivity(), LinearLayoutManager.VERTICAL, false);

        mRecyclerViewAdapter = new MyRecyclerViewAdapter(getActivity());
        // RecyclerView的item的点击事件监听
        mRecyclerViewAdapter.setOnItemClickListener(this);
        // 绑定Adapter
        mRecyclerView.setAdapter(mRecyclerViewAdapter);
        // RecyclerView加上分割线
        mRecyclerView.addItemDecoration(new RVDividerItemDecoration(getActivity(), RVDividerItemDecoration.VERTICAL_LIST));
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
        // 监听recyclerview的滑动状态 && 上拉刷新
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
                Log.d(TAG, "onScrollStateChanged: lastVisibleItem = " + lastVisibleItem);
                // lastVisibleItem > 0 可以修复刚打开APP就下拉刷新crash的bug
                if (newState == RecyclerView.SCROLL_STATE_IDLE && lastVisibleItem + 1 == mRecyclerViewAdapter.getItemCount() && lastVisibleItem > 0) {
                    if (newsList.size() == 0 || newsList == null)
                        return;
                    if (newsList.size() < 10) {
                        mRecyclerViewAdapter.setMoreStatus(0);
                        subNewsList = newsList.subList(0, newsList.size());
                        // 最后几个item,则不再显示上拉刷新
                        mRecyclerViewAdapter.setMoreStatus(2);
                    } else {
                        mRecyclerViewAdapter.setMoreStatus(0);
                        subNewsList = newsList.subList(0, 10);
                    }

                    new Handler().postDelayed(() -> {
                        Log.d(TAG, "run: 上拉刷新");
                        // 子item不足10个，说明也是最后几个item了
                        if (subNewsList.size() < 10) {
                            mRecyclerViewAdapter.addMoreItem(subNewsList);
                            subNewsList.clear();
                        } else {
                            mRecyclerViewAdapter.addMoreItem(subNewsList);
                            // 隐藏上拉刷新item
                            mRecyclerViewAdapter.setMoreStatus(2);
                            subNewsList.clear();
                        }
                    }, 1000);
                }
            }

            @Override
            // 滚动时回调
            // dx : 水平滚动距离
            // dy : 垂直滚动距离
            // dx > 0时为手指向左滚动, 列表滚动显示右面的内容
            // dx < 0时为手指向右滚动, 列表滚动显示左面的内容
            // dy > 0时为手指向上滚动, 列表滚动显示下面的内容
            // dy < 0时为手指向下滚动, 列表滚动显示上面的内容
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
                lastVisibleItem = mLayoutManager.findLastVisibleItemPosition();
                Log.d(TAG, "onScrolled: lastVisibleItem = " + lastVisibleItem);
            }
        });
        // 刷新出内容
        mSwipeRefreshLayout.setEnabled(false);
        new Thread(new PullDownRefreshThread()).start();
    }

    @Override
    public void onRefresh() {
        // 刷新时模拟数据的变化
        new Handler().postDelayed(() -> {
            mSwipeRefreshLayout.setRefreshing(false);
            new Thread(new PullDownRefreshThread()).start();
        }, 1000);
    }


    /**
     * 启动Fragment
     * 传入需要的参数，设置给arguments
     *
     * @param argument
     * @return
     */
    public static NewsFragment newInstance(String argument) {
        Bundle bundle = new Bundle();
        bundle.putString(ARGUMENT, argument);
        NewsFragment mNewsFragment = new NewsFragment();
        mNewsFragment.setArguments(bundle);
        return mNewsFragment;
    }

    // 子线程中执行网络访问，获取JSON数据的操作
    public class PullDownRefreshThread implements Runnable {
        @Override
        public void run() {
            BaseApplication baseApplication = (BaseApplication) getActivity().getApplication();
            // 创建Volley的JsonObjectRequest
            JsonObjectRequest request = (JsonObjectRequest) getNewsListJson(newsType);
            // 添加到Volley的Queue中
            baseApplication.addToRequestQueue(request, TAG);
        }
    }

    // Volley通过新闻type参数获取json
    public Request getNewsListJson(final String newsType) {
        JsonObjectRequest jsonObjectRequest = null;
        if (!TextUtils.isEmpty(newsType)) {
            L.d(NEWS_API_ADDRESS + newsType + NEWS_APP_KEY);
            jsonObjectRequest = new JsonObjectRequest(NEWS_API_ADDRESS + newsType + NEWS_APP_KEY, null,
                    response -> {
                        Log.d("xuqi", response.toString());
                        try {
                            List<NewsInfo> newsList = GsonData.parseJSONToList(response.toString());
                            Message msg = new Message();
                            msg.what = MyHandler.REQUEST_NEWS_LIST;
                            msg.obj = newsList;
                            handler.sendMessage(msg);
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }, error -> Log.e("xuqi", error.getMessage(), error));
        }
        return jsonObjectRequest;
    }

    private class MyHandler extends Handler {
        // 对Fragment的弱引用
        private final WeakReference<NewsFragment> reference;

        // Fragment请求JSON数据
        public static final int REQUEST_NEWS_LIST = 1;

        public MyHandler(NewsFragment fragment) {
            reference = new WeakReference<>(fragment);
        }

        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case REQUEST_NEWS_LIST:
                    // 新获得的new data
                    newsList = (List<NewsInfo>) msg.obj;
                    // 新获得的news数据，后尾加上原有的数据,通过上拉刷新在一页一页加回到adapter中
                    if (mRecyclerViewAdapter.getListItem() != null && mRecyclerViewAdapter.getListItem().size() > 0)
                        newsList.addAll(mRecyclerViewAdapter.getListItem());
                    mRecyclerViewAdapter.deleteAllItem();

                    Random random = new Random();
                    newsList.get(0).setTitle("test" + random.nextInt(10));
                    newsList.get(1).setTitle("test" + random.nextInt(10));
                    newsList.get(2).setTitle("test" + random.nextInt(10));
                    // 先把第一页内容添加进去
                    subNewsList = newsList.subList(0, 10);
                    mRecyclerViewAdapter.addMoreItem(subNewsList);
                    subNewsList.clear();
                    // 第一次打开APP时，为了防止刷新时用户手动下拉刷新，setEnable(false)，在这里设置true
                    mSwipeRefreshLayout.setEnabled(true);
                    break;
            }
        }
    }

    // 点击事件监听
    @Override
    public void onItemClick(View view, int position) {
        // 获取点击的item的url
        String newsUrl = mRecyclerViewAdapter.newsList.get(position).getUrl();
        String newsTitle = mRecyclerViewAdapter.newsList.get(position).getAuthor_name();
        // 将url放入intent
        Intent intent = new Intent(getActivity(), NewsContentActivity.class);
        // 将新闻url与author传递到NewsContentActivity
        intent.putExtra("newsUrl", newsUrl);
        intent.putExtra("newsTitle", newsTitle);
        // 跳转到NewsContentActivity
        startActivity(intent);
        SnackbarUtil.show(mRecyclerView, getString(R.string.item_clicked), 0);
    }

    // 长按事件监听
    @Override
    public void onItemLongClick(View view, int position) {
        showAlertDialog(position);
        //SnackbarUtil.show(mRecyclerView, getString(R.string.item_longclicked), 0);
    }

    public void showAlertDialog(final int position) {
        new AlertDialog.Builder(getActivity()).setTitle("列表框")
                .setItems(getResources().getStringArray(R.array.choose_log_click), (dialog, which) -> {
                    if (which == 0) {
                        SnackbarUtil.show(getView(), "收藏", 0);
                    } else if (which == 1) {
                        mRecyclerViewAdapter.deleteItem(position);
                    }
                    dialog.dismiss();
                }).show();
    }
}
