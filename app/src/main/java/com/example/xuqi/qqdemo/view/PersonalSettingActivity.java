package com.example.xuqi.qqdemo.view;

import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.example.xuqi.qqdemo.R;

public class PersonalSettingActivity extends BaseActivity implements View.OnClickListener {

    private Toolbar toolbar;
    private RelativeLayout relativeLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_personal_setting);
        initViews();
    }

    private void initViews() {
        toolbar = (Toolbar) findViewById(R.id.toolbar_set_personal_info);
        relativeLayout = (RelativeLayout) findViewById(R.id.rl_set);

        // 设置返回键点击事件
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        relativeLayout.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.rl_set:
                showActivity(ChangeBindPhoneActivity.class);
                Toast.makeText(this, "click", Toast.LENGTH_SHORT).show();
                break;
        }
    }
}
