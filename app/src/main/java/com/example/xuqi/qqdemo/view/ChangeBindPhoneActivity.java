package com.example.xuqi.qqdemo.view;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.example.xuqi.qqdemo.R;
import com.example.xuqi.qqdemo.bean.NewsUser;
import com.example.xuqi.qqdemo.util.L;
import com.example.xuqi.qqdemo.util.SnackbarUtil;

import cn.bmob.sms.BmobSMS;
import cn.bmob.sms.exception.BmobException;
import cn.bmob.sms.listener.RequestSMSCodeListener;
import cn.bmob.sms.listener.VerifySMSCodeListener;
import cn.bmob.v3.listener.UpdateListener;

public class ChangeBindPhoneActivity extends AppCompatActivity implements View.OnClickListener {
    private EditText et_phone, et_vercode;
    private Button btn_confrim;
    private TextView tv_request_vercode;
    private Dialog dialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_change_bind_phone);
        initViews();
    }

    private void initViews() {
        et_phone = (EditText) findViewById(R.id.change_bind_phone_et);
        et_vercode = (EditText) findViewById(R.id.change_bind_vercode_et);
        btn_confrim = (Button) findViewById(R.id.change_bind_phone_btn);
        tv_request_vercode = (TextView) findViewById(R.id.request_change_vercode);
        dialog = new Dialog(this);

        tv_request_vercode.setOnClickListener(this);
        btn_confrim.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        final String phone_number = et_phone.getText().toString().trim();
        String phone_vercode = et_vercode.getText().toString().trim();
        switch (v.getId()) {
            case R.id.request_change_vercode:
                if (TextUtils.isEmpty(phone_number) || phone_number.length() != 11) {
                    SnackbarUtil.show(v, "请输入手机号", 0);
                } else {
                    L.d("电话号码正确");
                    //进行获取验证码和倒计时1分钟操作
                    // "SmsDemo"为短信模版名称
                    BmobSMS.requestSMSCode(this, phone_number, "SmsDemo", new RequestSMSCodeListener() {
                        @Override
                        public void done(Integer integer, BmobException e) {
                            if (e == null) {
                                // 发送成功时，让获取验证码按钮不可点击，且为灰色
                                tv_request_vercode.setClickable(false);
                                tv_request_vercode.setTextColor(Color.GRAY);
                                Toast.makeText(ChangeBindPhoneActivity.this, "验证码发送成功，请尽快使用", Toast.LENGTH_SHORT).show();
                                /**
                                 * 倒计时一分钟的操作
                                 *
                                 */
                                new CountDownTimer(60000, 1000) {
                                    @Override
                                    public void onTick(long millisUntilFinished) {
                                        tv_request_vercode.setText(millisUntilFinished / 1000 + "秒");
                                    }

                                    @Override
                                    public void onFinish() {
                                        tv_request_vercode.setClickable(true);
                                        tv_request_vercode.setText("重新发送");
                                    }
                                }.start();
                            } else {
                                L.d("xuqi  " + e.toString());
                                Toast.makeText(ChangeBindPhoneActivity.this, "验证码发送失败", Toast.LENGTH_SHORT).show();
                            }
                        }
                    });
                }
                break;
            case R.id.change_bind_phone_btn:
                // 验证手机号码和验证码
                BmobSMS.verifySmsCode(ChangeBindPhoneActivity.this, phone_number, phone_vercode, new VerifySMSCodeListener() {
                    @Override
                    public void done(BmobException e) {
                        if (e == null) {
                            L.d("验证通过");
                            NewsUser user = new NewsUser();
                            user.setMobilePhoneNumber(phone_number);
                            user.setMobilePhoneNumberVerified(true);
                            NewsUser currentUser = NewsUser.getCurrentUser(NewsUser.class);
                            user.update(currentUser.getObjectId(), new UpdateListener() {
                                @Override
                                public void done(cn.bmob.v3.exception.BmobException e) {
                                    if (e == null) {
                                        L.d("绑定成功");
                                        PersonalSettingActivity.showActivity(ChangeBindPhoneActivity.this);
                                    } else {
                                        L.d("绑定失败");
                                    }
                                }
                            });
                        } else {
                            Toast.makeText(ChangeBindPhoneActivity.this,"绑定失败，请更换手机号再试",Toast.LENGTH_SHORT).show();
                            L.d("验证失败");
                        }
                    }
                });
                break;
        }
    }

    public static void startActivity(Context mContext) {
        Intent intent = new Intent(mContext, ChangeBindPhoneActivity.class);
        mContext.startActivity(intent);
    }
}
