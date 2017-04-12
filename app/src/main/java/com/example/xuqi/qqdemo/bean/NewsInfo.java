package com.example.xuqi.qqdemo.bean;

import cn.bmob.v3.BmobObject;

/**
 * Created by xuqi on 17/4/10.
 */

public class NewsInfo extends BmobObject {
    private String uniquekey;
    private String title;
    private String date;
    private String author_name;
    private String url;

    public String getUniquekey() {
        return uniquekey;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getAuthor_name() {
        return author_name;
    }

    public void setAuthor_name(String author_name) {
        this.author_name = author_name;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public void setUniquekey(String uniquekey) {
        this.uniquekey = uniquekey;

    }
}
