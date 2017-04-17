package com.example.xuqi.qqdemo.view;

import android.app.Dialog;
import android.content.Context;
import android.text.TextUtils;
import android.view.View;
import android.widget.TextView;

import com.example.xuqi.qqdemo.R;

/**
 * Created by xuqi on 17/4/2.
 * 自定义LoadingDialog
 */

public class LoadingDialog extends Dialog {
    private View view;
    private TextView tv_msg;

    public LoadingDialog(Context mContext) {
//        this(mContext, 100, 100, R.layout.dialog_loding, R.style.Theme_loading_dialog, Gravity.CENTER, R.style.pop_anim_style);
        super(mContext, R.style.Theme_loading_dialog);
        //设置属性
        setContentView(R.layout.dialog_loding);
        view = getLayoutInflater().inflate(R.layout.dialog_loding, null);
//        Window window = getWindow();
//        WindowManager.LayoutParams layoutParams = window.getAttributes();
//        layoutParams.width = WindowManager.LayoutParams.MATCH_PARENT;
//        layoutParams.height = WindowManager.LayoutParams.MATCH_PARENT;
//        layoutParams.gravity = gravity;
//        window.setAttributes(layoutParams);
//        window.setWindowAnimations(anim);

        tv_msg = (TextView) view.findViewById(R.id.tv_loading_msg);
    }

    public LoadingDialog(Context mContext, String title) {
        super(mContext, R.style.Theme_loading_dialog);
        //设置属性
        setContentView(R.layout.dialog_loding);
        view = getLayoutInflater().inflate(R.layout.dialog_loding, null);
        tv_msg = (TextView) view.findViewById(R.id.tv_loading_msg);
        tv_msg.setText(title);
    }

    // 设置Dialog的message
    public void setMessage(String message) {
        if (TextUtils.isEmpty(message)) {
            tv_msg.setVisibility(View.GONE);
            return;
        }
        if (tv_msg.getVisibility() != View.VISIBLE) {
            tv_msg.setVisibility(View.VISIBLE);
        }
        tv_msg.setText(message);
    }
}
