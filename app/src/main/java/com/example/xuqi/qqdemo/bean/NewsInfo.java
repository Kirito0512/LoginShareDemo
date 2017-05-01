package com.example.xuqi.qqdemo.bean;

import cn.bmob.v3.BmobObject;

/**
 * Created by xuqi on 17/4/10.
 * 聚合新闻头条API对应的Bean
 */

public class NewsInfo extends BmobObject {
    private String uniquekey;
    private String title;
    private String date;
    private String author_name;
    private String url;
    // 三张图片，可能存在只有一张图，或只有两张图的情况
    private String thumbnail_pic_s;
    private String thumbnail_pic_s02;
    private String thumbnail_pic_s03;

    public NewsInfo(String key, String title, String date, String name, String url, String pic0, String pic1, String pic2) {
        this.uniquekey = key;
        this.title = title;
        this.date = date;
        this.author_name = name;
        this.url = url;
        this.thumbnail_pic_s = pic0;
        this.thumbnail_pic_s02 = pic1;
        this.thumbnail_pic_s03 = pic2;
    }

    public NewsInfo() {

    }


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

    public String getThumbnail_pic_s() {
        return thumbnail_pic_s;
    }

    public void setThumbnail_pic_s(String thumbnail_pic_s) {
        this.thumbnail_pic_s = thumbnail_pic_s;
    }

    public String getThumbnail_pic_s02() {
        return thumbnail_pic_s02;
    }

    public void setThumbnail_pic_s02(String thumbnail_pic_s02) {
        this.thumbnail_pic_s02 = thumbnail_pic_s02;
    }

    public String getThumbnail_pic_s03() {
        return thumbnail_pic_s03;
    }

    public void setThumbnail_pic_s03(String thumbnail_pic_s03) {
        this.thumbnail_pic_s03 = thumbnail_pic_s03;
    }
}
