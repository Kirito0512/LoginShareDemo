package com.example.xuqi.qqdemo;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.example.xuqi.qqdemo.bean.NewsUserInfo;
import com.example.xuqi.qqdemo.util.UserSessionManager;

public class PersonalPageActivity extends AppCompatActivity implements View.OnClickListener {
    private ImageView icon;
    private TextView name, mail, logout;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_personal_page);
        initViews();
        if (UserSessionManager.isAleadyLogin()) {
            NewsUserInfo user = UserSessionManager.getCurrentUser();
            name.setText(user.getName());
            Glide.with(this).load(UserSessionManager.getCurrentUser().getHeadPhoto()).into(icon);
        }
    }

    private void initViews() {
        icon = (ImageView) findViewById(R.id.personalpage_icon_image);
        name = (TextView) findViewById(R.id.personal_page_name);
        mail = (TextView) findViewById(R.id.personal_page_mail);
        logout = (TextView) findViewById(R.id.logout_button);

        logout.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.logout_button:
                if (UserSessionManager.isAleadyLogin()) {
                    UserSessionManager.logout(PersonalPageActivity.this);
                    // 删除i.dat
                    UserSessionManager.reSetCurrentUser();
                    MainActivity.showActivity(this);
                }
                break;
        }
    }
}
