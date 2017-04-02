package com.example.xuqi.qqdemo.view;

import android.app.Dialog;
import android.content.Context;
import android.view.Gravity;
import android.view.Window;
import android.view.WindowManager;

import com.example.xuqi.qqdemo.R;

/**
 * Created by xuqi on 17/4/2.
 * 自定义LoadingDialog
 */

public class LoadingDialog extends Dialog {

    public LoadingDialog(Context mContext) {
        this(mContext, 100, 100, R.layout.dialog_loding, R.style.Theme_loading_dialog, Gravity.CENTER, R.style.pop_anim_style);
    }

    public LoadingDialog(Context context, int width, int height, int layout, int style, int gravity, int anim) {
        super(context, style);
        //设置属性
        setContentView(layout);
        Window window = getWindow();
        WindowManager.LayoutParams layoutParams = window.getAttributes();
        layoutParams.width = WindowManager.LayoutParams.MATCH_PARENT;
        layoutParams.height = WindowManager.LayoutParams.WRAP_CONTENT;
        layoutParams.gravity = gravity;
        window.setAttributes(layoutParams);
        window.setWindowAnimations(anim);
    }
}
