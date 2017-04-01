package com.example.xuqi.qqdemo.view;

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
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.example.xuqi.qqdemo.GlideRoundTransform;
import com.example.xuqi.qqdemo.R;
import com.example.xuqi.qqdemo.util.SinaWeiboPlatform;
import com.example.xuqi.qqdemo.util.TencentPlatform;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

import static android.R.attr.path;
import static android.view.View.GONE;

public class UserInfoActivity extends AppCompatActivity implements View.OnClickListener {
    private ImageView icon;
    private Bitmap head;
    private Button share_Button, share_Zone_Button, share_Sina_Button, share_SinaFriend_Button;
    public String name, url, type;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_info);
        icon = (ImageView) findViewById(R.id.head_photo);

        Intent intent = getIntent();
        name = intent.getStringExtra("name");
        url = intent.getStringExtra("url");
        type = intent.getStringExtra("type");

        share_Zone_Button = (Button) findViewById(R.id.share_QQZone_button);
        share_Button = (Button) findViewById(R.id.share_QQ_button);
        share_Sina_Button = (Button) findViewById(R.id.share_Sina_button);
        share_SinaFriend_Button = (Button) findViewById(R.id.share_SinaFriend_button);

        icon.setOnClickListener(this);
        share_Button.setOnClickListener(this);
        share_Zone_Button.setOnClickListener(this);
        share_Sina_Button.setOnClickListener(this);
        share_SinaFriend_Button.setOnClickListener(this);

        if (type.equals("QQ")) {
            share_Sina_Button.setVisibility(GONE);
            share_SinaFriend_Button.setVisibility(GONE);
            share_Button.setVisibility(View.VISIBLE);
            share_Zone_Button.setVisibility(View.VISIBLE);
        } else if (type.equals("Sina")) {
            share_Button.setVisibility(GONE);
            share_Zone_Button.setVisibility(GONE);
            share_Sina_Button.setVisibility(View.VISIBLE);
            share_SinaFriend_Button.setVisibility(View.VISIBLE);
        } else {
            share_Button.setText(getString(R.string.buttonsharetoWX));
            share_Zone_Button.setText(getString(R.string.buttonsharetoWXSNS));
        }
        Glide.with(UserInfoActivity.this).load(url).transform(new GlideRoundTransform(getApplicationContext(), 10)).
                placeholder(R.drawable.ic_cloud_download_black_24dp).
                error(R.drawable.ic_delete_forever_black_24dp).into(icon);//Glide解析获取用户头像
    }


    private void showTypeDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        final AlertDialog dialog = builder.create();
        View view = View.inflate(this, R.layout.dialog, null);
        TextView tv_select_gallery = (TextView) view.findViewById(R.id.take_photo);
        TextView tv_select_camera = (TextView) view.findViewById(R.id.choose_album);
        tv_select_gallery.setOnClickListener(new View.OnClickListener() {// 在相册中选取
            @Override
            public void onClick(View v) {
                Intent intent1 = new Intent(Intent.ACTION_PICK, null);
                intent1.setDataAndType(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, "image/*");
                startActivityForResult(intent1, 1);
                dialog.dismiss();
            }
        });
        tv_select_camera.setOnClickListener(new View.OnClickListener() {// 调用照相机
            @Override
            public void onClick(View v) {
                Intent intent2 = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                intent2.putExtra(MediaStore.EXTRA_OUTPUT,
                        Uri.fromFile(new File(Environment.getExternalStorageDirectory(), "head.jpg")));
                startActivityForResult(intent2, 2);// 采用ForResult打开
                dialog.dismiss();
            }
        });
        dialog.setView(view);
        dialog.show();
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.head_photo :
                showTypeDialog();
                break;
            case R.id.share_QQ_button:
                (new TencentPlatform(this)).shareToQQ(this);
                break;
            case R.id.share_QQZone_button:
                (new TencentPlatform(this)).shareToQzone(this);
                break;
            case R.id.share_Sina_button:
                (new SinaWeiboPlatform(this)).shareToSina(this);
                break;
            case R.id.share_SinaFriend_button:
                (new SinaWeiboPlatform(this)).shareToSinafriend(this);
//                startActivity(new Intent(UserInfoActivity.this, WBShareToMessageFriendActivity.class));
            default:
                break;
        }
    }

    public static void showActivity(Context context, String name, String url, String type) {
        Intent intent = new Intent(context, UserInfoActivity.class);
        intent.putExtra("name", name);
        intent.putExtra("url", url);
        intent.putExtra("type", type);
        context.startActivity(intent);
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
                        Glide.with(UserInfoActivity.this).load(file).transform(new GlideRoundTransform(getApplicationContext(), 10)).
                                placeholder(R.drawable.ic_cloud_download_black_24dp).
                                error(R.drawable.ic_delete_forever_black_24dp).into(icon);//Glide解析获取用户头像
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
