package com.example.xuqi.qqdemo.view;

import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.widget.EditText;

import com.example.xuqi.qqdemo.R;

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
            String content = etMail.getText().toString();
            String mail = etFeedBackContent.getText().toString();
            if(item.getItemId() == R.id.edit){
                if(!TextUtils.isEmpty(mail) & !TextUtils.isEmpty(content)){

                }
            }
            return true;
        });

    }
}
