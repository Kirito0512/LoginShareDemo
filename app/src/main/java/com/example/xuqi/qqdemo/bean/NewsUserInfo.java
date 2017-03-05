package com.example.xuqi.qqdemo.bean;

import java.io.Serializable;

import cn.bmob.v3.BmobObject;

/**
 * Created by xuqi on 17/2/24.
 */

public class NewsUserInfo extends BmobObject implements Serializable {
    private int userId;
    private String name;
    private String headPhoto;
    // 第三方平台
    private String source;

    public String getSource() {
        return source;
    }

    public void setSource(String source) {
        this.source = source;
    }

    public int getUserId() {

        return userId;
    }

    public void setUserId(int userId) {
        this.userId = userId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getHeadPhoto() {
        return headPhoto;
    }

    public void setHeadPhoto(String headPhoto) {
        this.headPhoto = headPhoto;
    }
}
