package com.example.xuqi.qqdemo.bean;

import cn.bmob.v3.BmobObject;

/**
 * Created by xuqi on 17/4/21.
 * 用户反馈
 */

public class NewsUserFeedBack extends BmobObject {
    private String username;
    private String mobilePhoneNumber;
    private String mail;
    private String feedBackContent;

    public String getFeedBackContent() {
        return feedBackContent;
    }

    public void setFeedBackContent(String feedBackContent) {
        this.feedBackContent = feedBackContent;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getMobilePhoneNumber() {
        return mobilePhoneNumber;
    }

    public void setMobilePhoneNumber(String mobilePhoneNumber) {
        this.mobilePhoneNumber = mobilePhoneNumber;
    }

    public String getMail() {
        return mail;
    }

    public void setMail(String mail) {
        this.mail = mail;
    }
}
