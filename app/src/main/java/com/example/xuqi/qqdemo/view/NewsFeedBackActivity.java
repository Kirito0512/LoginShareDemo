package com.example.xuqi.qqdemo.view;

import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.widget.EditText;

import com.example.xuqi.qqdemo.R;
import com.example.xuqi.qqdemo.bean.NewsUser;
import com.example.xuqi.qqdemo.bean.NewsUserFeedBack;

import cn.bmob.v3.exception.BmobException;
import cn.bmob.v3.listener.SaveListener;

/**
 * Created by xuqi on 17/4/23
 * 用户反馈Activity
 */

public class NewsFeedBackActivity extends BaseActivity {
    private Toolbar tlFeedBack;
    private EditText etFeedBackContent, etMail;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_news_feed_back);
        initViews();
    }

    private void initViews() {
        tlFeedBack = (Toolbar) findViewById(R.id.toolbar_feed_back);
        etFeedBackContent = (EditText) findViewById(R.id.et_personal_suggestion_content);
        etMail = (EditText) findViewById(R.id.et_personal_suggestion_mail);

        // 加载menu样式
        tlFeedBack.inflateMenu(R.menu.personal_menu);
        tlFeedBack.setNavigationOnClickListener(v -> {
            showActivity(MainActivity.class);
        });

        tlFeedBack.setOnMenuItemClickListener(item -> {
            // 获取用户填写的数据
            String content = etMail.getText().toString();
            String mail = etFeedBackContent.getText().toString();
            if (item.getItemId() == R.id.edit) {
                if (!TextUtils.isEmpty(mail) & !TextUtils.isEmpty(content)) {
                    // 将用户反馈添加到Bmob后台服务器
                    NewsUserFeedBack feed = new NewsUserFeedBack();
                    NewsUser user = NewsUser.getCurrentUser(NewsUser.class);
                    feed.setUsername(user.getUsername());
                    feed.setMobilePhoneNumber(user.getMobilePhoneNumber());
                    feed.setFeedBackContent(content);
                    feed.setMail(mail);
                    feed.save(new SaveListener<String>() {
                        @Override
                        public void done(String objectId, BmobException e) {
                            if (e == null) {
                                showToast(getResources().getString(R.string.feedbackok));
                                // 跳转回MainActivity
                                showActivity(MainActivity.class);
                            } else {
                                showToast("创建数据失败：" + e.getMessage());
                            }
                        }
                    });
                } else {
                    showToast("内容不能为空");
                }
            }
            return true;
        });

    }
}
