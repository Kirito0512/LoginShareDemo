package com.example.xuqi.qqdemo.bean;

import cn.bmob.v3.BmobUser;
import cn.bmob.v3.datatype.BmobFile;

/**
 * Created by xuq on 17/3/29.
 * 用户类
 */


public class NewsUser extends BmobUser {

    private BmobFile image;

    private String imageUrl;

    private Boolean sex;

    // 关注的新闻Tab
    private String[] mTabs;

    public Boolean getSex() {
        return sex;
    }

    public void setSex(Boolean sex) {
        this.sex = sex;
    }

    public BmobFile getImage() {
        return image;
    }

    public void setImage(BmobFile image) {
        this.image = image;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    public String[] getmTabs() {
        return mTabs;
    }

    public void setmTabs(String[] mTabs) {
        this.mTabs = mTabs;
    }
}
