package com.example.xuqi.qqdemo.widget;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.view.Gravity;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.TextView;

import com.example.xuqi.qqdemo.R;

import java.io.File;

import static com.example.xuqi.qqdemo.R.layout.dialog;

/**
 * Created by xuqi on 17/3/5.
 */

public class MyDialog extends Dialog implements View.OnClickListener {
    private TextView take_photo, choose_album, cancel_choose;
    // 构造方法中预加载样式
    public MyDialog(Context context) {
        this(context, R.style.MyDialog);
    }

    protected MyDialog(Context context, boolean cancelable, OnCancelListener cancelListener) {
        super(context, cancelable, cancelListener);
    }

    public MyDialog(Context context, int themeResId) {
        super(context, themeResId);
        initViews();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // 预先设置Dialog的一些属性
        Window dialogWindow = getWindow();
        WindowManager.LayoutParams p = dialogWindow.getAttributes();
//        p.width = ViewGroup.LayoutParams.WRAP_CONTENT;
//        p.height = ViewGroup.LayoutParams.WRAP_CONTENT;
//        dialogWindow.setGravity(Gravity.CENTER);
//        dialogWindow.setAttributes(p);

//        p.x = 0;
//        p.y = 100;
        p.gravity = Gravity.CENTER;
        dialogWindow.setAttributes(p);

    }

    private void initViews() {
        View contentView = getLayoutInflater().inflate(dialog, null);
        take_photo = (TextView) contentView.findViewById(R.id.take_photo);
        choose_album = (TextView) contentView.findViewById(R.id.choose_album);
//        cancel_choose = (TextView) contentView.findViewById(R.id.cancel_choose);
        take_photo.setOnClickListener(this);
        choose_album.setOnClickListener(this);
//        cancel_choose.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.take_photo :
                Intent intent1 = new Intent(Intent.ACTION_PICK, null);
                intent1.setDataAndType(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, "image/*");
//                startActivityForResult(intent1, 1);
                dismiss();
                break;
            case R.id.choose_album :
                Intent intent2 = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                intent2.putExtra(MediaStore.EXTRA_OUTPUT,
                        Uri.fromFile(new File(Environment.getExternalStorageDirectory(), "head.jpg")));
//                startActivityForResult(intent2, 2);// 采用ForResult打开
                dismiss();
                break;

        }
    }
}
