package com.example.xuqi.qqdemo.view;

import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.example.xuqi.qqdemo.R;
import com.example.xuqi.qqdemo.bean.NewsUser;
import com.example.xuqi.qqdemo.util.L;

import cn.bmob.v3.exception.BmobException;
import cn.bmob.v3.listener.UpdateListener;

public class EditPersonalInfoPageActivity extends AppCompatActivity implements View.OnClickListener {

    private Toolbar toolbar;
    private EditText et_usernamae, et_email;
    private TextView et_sex;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_personal_info_page);
        initViews();
    }

    private void initViews() {
        toolbar = (Toolbar) findViewById(R.id.toolbar_edit_personal_info);
        et_usernamae = (EditText) findViewById(R.id.et_name);
        et_email = (EditText) findViewById(R.id.et_email);
        et_sex = (TextView) findViewById(R.id.et_sex);
        et_sex.setOnClickListener(this);
        // 为toolbar加载menu样式
        toolbar.inflateMenu(R.menu.edit_personal_menu);
        // ToolBar返回键
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        // 设置toolbar menu里的点击事件
        toolbar.setOnMenuItemClickListener(new Toolbar.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                switch (item.getItemId()) {
                    case R.id.check:
                        /**
                         *  获取用户填写的信息
                         */
                        String username = et_usernamae.getText().toString().trim();
                        String mail = et_email.getText().toString().trim();
                        String sex = et_sex.getText().toString().trim();
                        // 更新用户信息
                        updateInfo(username, mail, sex);
                        break;
                }
                return true;
            }
        });
    }

    private void updateInfo(String username, String mail, String sex) {
        final LoadingDialog dialog = new LoadingDialog(this);
        NewsUser user = new NewsUser();
        if (!TextUtils.isEmpty(username)) {
            user.setUsername(username);
        }
        if (!TextUtils.isEmpty(mail)) {
            user.setEmail(mail);
        }
        if (!TextUtils.isEmpty(sex)) {
            // 男
            if (sex.equals(getString(R.string.male)))
                user.setSex(true);
                // 女
            else if (sex.equals(getString(R.string.female)))
                user.setSex(false);
        }
        // 更新Bmob用户信息
        NewsUser currentUser = NewsUser.getCurrentUser(NewsUser.class);
        // 显示dialog
        dialog.show();
        user.update(currentUser.getObjectId(), new UpdateListener() {
            @Override
            public void done(BmobException e) {
                if (e == null) {
                    Toast.makeText(EditPersonalInfoPageActivity.this, R.string.Bmob_update, Toast.LENGTH_SHORT).show();
                    // 关闭dialog
                    dialog.dismiss();
                    // 关闭Activity
                    finish();
                } else {
                    L.d(e.toString());
                    Toast.makeText(EditPersonalInfoPageActivity.this, R.string.Bmob_error, Toast.LENGTH_SHORT).show();
                    // 关闭dialog
                    dialog.dismiss();
                }
            }
        });
    }

    public static void showActivity(Context mContext) {
        Intent intent = new Intent(mContext, EditPersonalInfoPageActivity.class);
        mContext.startActivity(intent);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.et_sex:
                // 点击输入性别
                Dialog dialog = showChooseSexDialog();
                dialog.show();
        }
    }

    private Dialog showChooseSexDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle(R.string.choose_sex);
        builder.setItems(R.array.sex_array, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                if (which == 0) {
                    et_sex.setText(R.string.male);
                } else if (which == 1) {
                    et_sex.setText(R.string.female);
                }
            }
        });
        return builder.create();
    }
}
