package com.example.xuqi.qqdemo;

import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.example.xuqi.qqdemo.bean.NewsUserInfo;
import com.example.xuqi.qqdemo.util.TencentPlatform;
import com.example.xuqi.qqdemo.util.UserSessionManager;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

import static android.R.attr.path;

public class PersonalPageActivity extends AppCompatActivity implements View.OnClickListener {
    private ImageView icon;
    private Bitmap head;
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
        icon.setOnClickListener(this);
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

            case R.id.personalpage_icon_image:
                if (UserSessionManager.isAleadyLogin()) {
//                    MyDialog dialog = new MyDialog(this);
//                    dialog.setContentView(R.layout.dialog);
//                    dialog.setCanceledOnTouchOutside(true);// 点击外部区域关闭
//                    dialog.show();
                    showTypeDialog();
                }
                break;
        }
    }

    private void showTypeDialog() {
        final AlertDialog.Builder builder = new AlertDialog.Builder(this);
//        builder.setPositiveButton("取消", null);
//        builder.setTitle(R.string.choose_head);
        final AlertDialog alertDialog = builder.create();
        View view = View.inflate(this, R.layout.dialog, (ViewGroup) findViewById(R.id.dialog));
        TextView tv_select_gallery = (TextView) view.findViewById(R.id.take_photo);
        TextView tv_select_camera = (TextView) view.findViewById(R.id.choose_album);
        TextView tv_cancel = (TextView) view.findViewById(R.id.cancel_choose);
        tv_select_gallery.setOnClickListener(new View.OnClickListener() {// 在相册中选取
            @Override
            public void onClick(View v) {
                Intent intent2 = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                intent2.putExtra(MediaStore.EXTRA_OUTPUT,
                        Uri.fromFile(new File(Environment.getExternalStorageDirectory(), "head.jpg")));
                startActivityForResult(intent2, 2);// 采用ForResult打开
                alertDialog.dismiss();
            }
        });
        tv_select_camera.setOnClickListener(new View.OnClickListener() {// 调用照相机
            @Override
            public void onClick(View v) {
                Intent intent1 = new Intent(Intent.ACTION_PICK, null);
                intent1.setDataAndType(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, "image/*");
                startActivityForResult(intent1, 1);
                alertDialog.dismiss();
            }
        });
        tv_cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                alertDialog.dismiss();
            }
        });
        alertDialog.setView(view);
        alertDialog.show();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch (requestCode) {
            case 1:
                if (resultCode == RESULT_OK) {
                    cropPhoto(data.getData());// 裁剪图片
                }
                break;
            case 2:
                if (resultCode == RESULT_OK) {
                    File temp = new File(Environment.getExternalStorageDirectory() + "/head.jpg");
                    cropPhoto(Uri.fromFile(temp));// 裁剪图片
                }

                break;
            case 3:
                if (data != null) {
                    Bundle extras = data.getExtras();
                    head = extras.getParcelable("data");
                    if (head != null) {
                        /**
                         * 上传服务器代码
                         */
                        File file = saveImageToGallery(getApplicationContext(),head);// 保存在SD卡中
//                        Glide.with(PersonalPageActivity.this).load(file).transform(new GlideRoundTransform(getApplicationContext(), 10)).
//                                placeholder(R.drawable.ic_cloud_download_black_24dp).
//                                error(R.drawable.ic_delete_forever_black_24dp).into(icon);//Glide解析获取用户头像
                        Glide.with(this).load(file).into(icon);
                        (new TencentPlatform(this)).doSetAvatar(this,file.toString());
                    }
                }
                break;
            default:
                break;

        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    /**
     * 调用系统的裁剪功能
     *
     * @param uri
     */
    public void cropPhoto(Uri uri) {
        Intent intent = new Intent("com.android.camera.action.CROP");
        intent.setDataAndType(uri, "image/*");
        intent.putExtra("crop", "true");
        // aspectX aspectY 是宽高的比例
        intent.putExtra("aspectX", 1);
        intent.putExtra("aspectY", 1);
        // outputX outputY 是裁剪图片宽高
        intent.putExtra("outputX", 150);
        intent.putExtra("outputY", 150);
        intent.putExtra("return-data", true);
        startActivityForResult(intent, 3);
    }

    public static File saveImageToGallery(Context context, Bitmap bmp) {
        // 首先保存图片
        File appDir = new File(Environment.getExternalStorageDirectory(), "mypic");
        if (!appDir.exists()) {
            appDir.mkdir();
        }
        String fileName = System.currentTimeMillis() + ".jpg";
        File file = new File(appDir, fileName);
        try {
            FileOutputStream fos = new FileOutputStream(file);
            bmp.compress(Bitmap.CompressFormat.JPEG, 100, fos);
            fos.flush();
            fos.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

        // 其次把文件插入到系统图库
        try {
            MediaStore.Images.Media.insertImage(context.getContentResolver(),
                    file.getAbsolutePath(), fileName, null);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        // 最后通知图库更新
        context.sendBroadcast(new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE, Uri.parse("file://" + path)));
        return file;
    }
}
