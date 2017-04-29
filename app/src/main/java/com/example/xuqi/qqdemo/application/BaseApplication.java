package com.example.xuqi.qqdemo.application;

import android.app.Application;
import android.content.Context;
import android.text.TextUtils;
import android.util.Log;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.example.xuqi.qqdemo.Constants;
import com.example.xuqi.qqdemo.R;
import com.example.xuqi.qqdemo.util.ShareUtils;
import com.tencent.bugly.crashreport.CrashReport;

import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

import cn.bmob.sms.BmobSMS;
import cn.bmob.v3.Bmob;

import static com.example.xuqi.qqdemo.Constants.Bmob_Application_Id;
import static com.example.xuqi.qqdemo.Constants.NEWS_API_ADDRESS;
import static com.example.xuqi.qqdemo.Constants.NEWS_APP_KEY;

/**
 * Created by xuqi on 17/3/22.
 */

public class BaseApplication extends Application {
    private RequestQueue queues;
    public static final String TAG = "MyApplication";
    private static BaseApplication instance;
    // 所有新闻Tab的中文数组
    private String[] mTabs;
    // mTabs数组对应的拼音
    private String[] mTabsCN;
    // 文字与拼音对应的map
    private Map titleMap = new HashMap<String, String>();

    @Override
    public void onCreate() {
        super.onCreate();
        instance = this;
        // 初始化Bmob
        Bmob.initialize(this, Bmob_Application_Id);
        // Dmob短信服务
        BmobSMS.initialize(this, Bmob_Application_Id);
        // 初始化Bugly
        CrashReport.initCrashReport(getApplicationContext(), Constants.BUGLY_APP_ID, true);
        // 获取全部的新闻Tab字符串
        mTabs = getResources().getStringArray(R.array.tab_titles);
        // tab标题对应的拼音（获取新闻内容要用）
        mTabsCN = getResources().getStringArray(R.array.tab_CN_titles);
        for (int i = 0; i < mTabs.length; i++) {
            titleMap.put(mTabs[i], mTabsCN[i]);
        }
    }

    public static synchronized BaseApplication getInstance() {
        return instance;
    }

    public String[] getmTabs() {
        return mTabs;
    }

    public Map<String, String> getTitleMap() {
        return titleMap;
    }

    public RequestQueue getRequestQueue() {
        if (queues == null) {
            queues = Volley.newRequestQueue(getApplicationContext());
        }
        return queues;
    }

    // 添加request并加上tag
    public <T> void addToRequestQueue(Request<T> req, String tag) {
        req.setTag(TextUtils.isEmpty(tag) ? TAG : tag);
        getRequestQueue().add(req);
    }

    // 添加request
    public <T> void addToRequestQueue(Request<T> req) {
        req.setTag(TAG);
        getRequestQueue().add(req);
    }

    // 取消request
    public void cancelPendingRequest(Object tag) {
        if (queues != null) {
            queues.cancelAll(tag);
        }
    }

    // Volley获取json
    public static Request getNewsListJson(final String newsType, final Context mContext) {
        JsonObjectRequest jsonObjectRequest = null;
        JsonArrayRequest jsonArrayRequest = null;
        if (!TextUtils.isEmpty(newsType)) {

            jsonObjectRequest = new JsonObjectRequest(NEWS_API_ADDRESS + newsType + NEWS_APP_KEY, null,
                    new Response.Listener<JSONObject>() {
                        @Override
                        public void onResponse(JSONObject response) {
                            Log.d("xuqi", response.toString());
                            ShareUtils.putString(mContext, newsType, response.toString());
                        }
                    }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    Log.e("xuqi", error.getMessage(), error);
                }
            });
//            jsonArrayRequest = new JsonArrayRequest(NEWS_API_ADDRESS + newsType + NEWS_APP_KEY,
//                    // JsonArrayRequest的第二个参数
//                    new Response.Listener<JSONArray>() {
//                        @Override
//                        public void onResponse(JSONArray response) {
//                            Log.d("TAG", response.toString());
//                        }
//                    },
//                    // JsonArrayRequest的第三个参数
//                    new Response.ErrorListener() {
//                        @Override
//                        public void onErrorResponse(VolleyError error) {
//                            Log.e("TAG", error.getMessage(), error);
//                        }
//                    });
        }
        return jsonObjectRequest;
    }
}