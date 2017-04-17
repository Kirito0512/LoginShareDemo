package com.example.xuqi.qqdemo.netdata;

import android.content.Context;
import android.text.TextUtils;
import android.util.Log;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.example.xuqi.qqdemo.util.L;
import com.example.xuqi.qqdemo.util.ShareUtils;

import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

import static com.example.xuqi.qqdemo.Constants.NEWS_API_ADDRESS;
import static com.example.xuqi.qqdemo.Constants.NEWS_APP_KEY;

/**
 * Created by xuqi on 17/4/13.
 */

public class HttpUtil {
    private static final String TAG = "HttpUtil";
    public static String sendHttpRequest(final String address) {
        HttpURLConnection connection = null;
        StringBuffer response = null;
        try {
            URL url = new URL(address);
            Log.d(TAG, "sendHttpRequest: address = " + address);
            connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod("GET");
            connection.setConnectTimeout(16000);
            connection.setRequestProperty("User-Agent", "Mozilla/5.0");

            if (connection.getResponseCode() == HttpURLConnection.HTTP_OK) {
                InputStream in = connection.getInputStream();
                BufferedReader reader = new BufferedReader(new InputStreamReader(in));
                response = new StringBuffer();
                String line;
                while ((line = reader.readLine()) != null) {
                    response.append(line);
                }
                return response.toString();
            } else {
                throw new IOException("Network Error - response code: " + connection.getResponseCode());
            }

        } catch (IOException e) {
            e.printStackTrace();
            return e.getMessage();
        } finally {
            if (connection != null)
                connection.disconnect();
        }

    }

    // Volley获取json
    public static Request getNewsListJson(final String newsType, final Context mContext) {
        JsonObjectRequest jsonObjectRequest = null;
        if (!TextUtils.isEmpty(newsType)) {
            L.d(NEWS_API_ADDRESS + newsType + NEWS_APP_KEY);
            jsonObjectRequest = new JsonObjectRequest(NEWS_API_ADDRESS + newsType + NEWS_APP_KEY, null,
                    new Response.Listener<JSONObject>() {
                        @Override
                        public void onResponse(JSONObject response) {
                            Log.d("xuqi", response.toString());
                            String json = ShareUtils.getString(mContext,newsType,"");
                            // 保存json数据
//                            try {
//                                List<NewsInfo> list = GsonData.parseJSONToList(json);
//                            } catch (JSONException e) {
//                                e.printStackTrace();
//                            }
                        }
                    }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    Log.e("xuqi", error.getMessage(), error);
                }
            });
        }
        return jsonObjectRequest;
    }
}
