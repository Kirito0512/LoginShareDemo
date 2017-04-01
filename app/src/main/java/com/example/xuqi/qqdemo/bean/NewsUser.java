package com.example.xuqi.qqdemo.bean;

import cn.bmob.v3.BmobUser;

/**
 * Created by xuqi on 17/3/29.
 */

public class NewsUser extends BmobUser {
    public String getImageurl() {
        return imageurl;
    }

    public void setImageurl(String imageurl) {
        this.imageurl = imageurl;
    }

    private String imageurl;


}
