package com.example.xuqi.qqdemo.view;

import android.app.AlertDialog;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.example.xuqi.qqdemo.R;
import com.example.xuqi.qqdemo.bean.NewsUser;
import com.example.xuqi.qqdemo.bean.NewsUserInfo;
import com.example.xuqi.qqdemo.util.L;
import com.example.xuqi.qqdemo.util.UserSessionManager;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

import cn.bmob.v3.datatype.BmobFile;
import cn.bmob.v3.exception.BmobException;
import cn.bmob.v3.listener.UpdateListener;
import cn.bmob.v3.listener.UploadFileListener;

import static android.R.attr.path;

/**
 * 个人信息页面
 */
public class PersonalPageActivity extends AppCompatActivity implements View.OnClickListener {
    private ImageView icon;
    private Bitmap head;
    private TextView name, mail, phone, logout;
    private Toolbar toolBar;
    private NewsUser user;
    public static final int CAMERA_REQUEST_CODE = 100;
    public static final int GALLERY_REQUEST_CODE = 101;
    public static final int CROP_REQUEST_CODE = 102;
    public String imgPath = "";
    LoadingDialog dialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_personal_page);
        initViews();
        initDatas();
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        setIntent(intent);
        initDatas();
    }

    @Override
    protected void onRestart() {
        initDatas();
        super.onRestart();
    }

    private void initDatas() {
        // 第三方登录
        if (UserSessionManager.isAleadyLogin()) {
            NewsUserInfo user = UserSessionManager.getCurrentUser();
            name.setText(user.getName());
            Glide.with(this).load(UserSessionManager.getCurrentUser().getHeadPhoto()).into(icon);
        }
        // Bmob用户登录
        else if (NewsUser.getCurrentUser() != null) {
            NewsUser user = NewsUser.getCurrentUser(NewsUser.class);
            // 设置昵称
            if (!TextUtils.isEmpty(user.getUsername()))
                name.setText(user.getUsername());
            // 设置Email
            if (!TextUtils.isEmpty(user.getEmail()))
                mail.setText(user.getEmail());
            // 设置手机号码
            if (!TextUtils.isEmpty(user.getMobilePhoneNumber()))
                phone.setText(user.getMobilePhoneNumber());
            // 设置用户头像
            if (user.getImageUrl() != null)
                Glide.with(this).load(user.getImageUrl()).into(icon);
            if (user.getSex() != null) {
                if (user.getSex() == true) {
                    Drawable sex = getResources().getDrawable(R.drawable.male);
                    sex.setBounds(0, 0, 36, 36);
                    name.setCompoundDrawables(null, null, sex, null);
                } else {
                    Drawable sex = getResources().getDrawable(R.drawable.female);
                    sex.setBounds(0, 0, 36, 36);
                    name.setCompoundDrawables(null, null, sex, null);
                }
            }
        }
    }

    private void initViews() {
        icon = (ImageView) findViewById(R.id.personalpage_icon_image);
        name = (TextView) findViewById(R.id.personal_page_name);
        mail = (TextView) findViewById(R.id.personal_page_mail);
        phone = (TextView) findViewById(R.id.personal_page_phone);
        logout = (TextView) findViewById(R.id.logout_button);
        toolBar = (Toolbar) findViewById(R.id.toolbar_personal_info);

        logout.setOnClickListener(this);
        icon.setOnClickListener(this);
        // 加载menu样式
        toolBar.inflateMenu(R.menu.personal_menu);
        // 设置Toolbar返回键
        toolBar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        // 设置toolbar扩展栏的点击事件
        toolBar.setOnMenuItemClickListener(new Toolbar.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                switch (item.getItemId()) {
                    case R.id.edit:
                        EditPersonalInfoPageActivity.showActivity(PersonalPageActivity.this);
                        break;
                }
                return true;
            }
        });
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
                    L.d("第三方用户退出登录");
                } else if (NewsUser.getCurrentUser(NewsUser.class) != null) {
                    // Bmob用户退出登录
                    NewsUser.logOut();
                    MainActivity.showActivity(this);
                    L.d("Bmob用户退出登录");
                }
                break;

            case R.id.personalpage_icon_image:
                if (UserSessionManager.isAleadyLogin() || NewsUser.getCurrentUser(NewsUser.class) != null) {
                    showTypeDialog();
                }
                break;
        }
    }

    // 拍照｜｜图库 dialog
    private void showTypeDialog() {
        final AlertDialog.Builder builder = new AlertDialog.Builder(this);
        final AlertDialog alertDialog = builder.create();
        View view = View.inflate(this, R.layout.dialog, (ViewGroup) findViewById(R.id.dialog));
        TextView tv_select_gallery = (TextView) view.findViewById(R.id.take_photo);
        TextView tv_select_camera = (TextView) view.findViewById(R.id.choose_album);
        TextView tv_cancel = (TextView) view.findViewById(R.id.cancel_choose);
        // 图库
        tv_select_gallery.setOnClickListener(new View.OnClickListener() {// 在相册中选取
            @Override
            public void onClick(View v) {
                Intent intent2 = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                intent2.putExtra(MediaStore.EXTRA_OUTPUT,
                        Uri.fromFile(new File(Environment.getExternalStorageDirectory(), "head.jpg")));
                startActivityForResult(intent2, GALLERY_REQUEST_CODE);// 采用ForResult打开
                alertDialog.dismiss();
            }
        });
        // 拍照
        tv_select_camera.setOnClickListener(new View.OnClickListener() {// 调用照相机
            @Override
            public void onClick(View v) {
                Intent intent1 = new Intent(Intent.ACTION_PICK, null);
                intent1.setDataAndType(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, "image/*");
                startActivityForResult(intent1, CAMERA_REQUEST_CODE);
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
            case CAMERA_REQUEST_CODE:
                if (resultCode == RESULT_OK) {
                    cropPhoto(data.getData());// 裁剪图片
                }
                break;
            case GALLERY_REQUEST_CODE:
                if (resultCode == RESULT_OK) {
                    File temp = new File(Environment.getExternalStorageDirectory() + "/head.jpg");
                    cropPhoto(Uri.fromFile(temp));// 裁剪图片
                }

                break;
            case CROP_REQUEST_CODE:
                if (data != null) {
                    Bundle extras = data.getExtras();
                    head = extras.getParcelable("data");
                    if (head != null) {
                        /**
                         * 上传服务器代码
                         */
                        File file = saveImageToGallery(getApplicationContext(), head);// 保存在SD卡中
                        // 上传图片到Bmob
                        dialog = new LoadingDialog(this);
                        dialog.show();
                        uploadImage(imgPath);
//                        Glide.with(PersonalPageActivity.this).load(file).transform(new GlideRoundTransform(getApplicationContext(), 10)).
//                                placeholder(R.drawable.ic_cloud_download_black_24dp).
//                                error(R.drawable.ic_delete_forever_black_24dp).into(icon);//Glide解析获取用户头像
                        //(new TencentPlatform(this)).doSetAvatar(this, file.toString());
                    }
                }
                break;
            default:
                break;

        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    private void uploadImage(String img_path) {
        if (img_path == null) {
            Toast.makeText(this, "图片路径为空", Toast.LENGTH_SHORT).show();
            return;
        }
        final BmobFile iconFile = new BmobFile(new File(img_path));
        iconFile.upload(new UploadFileListener() {
            @Override
            public void done(BmobException e) {
                if (e == null) {
                    //上传头像文件
                    String url = iconFile.getFileUrl();
                    L.d("xuqi  " + url);
                    NewsUser newUser = new NewsUser();
                    newUser.setImageUrl(url);
                    NewsUser user = NewsUser.getCurrentUser(NewsUser.class);
                    newUser.update(user.getObjectId(), new UpdateListener() {
                        @Override
                        public void done(BmobException e) {
                            if (e == null) {
                                Toast.makeText(PersonalPageActivity.this, "更新数据成功", Toast.LENGTH_SHORT).show();
                                // 更新个人页面的头像
                                String image = NewsUser.getCurrentUser(NewsUser.class).getImageUrl();
                                Glide.with(PersonalPageActivity.this).load(image).into(icon);
                            } else {
                                Toast.makeText(PersonalPageActivity.this, "更新数据失败", Toast.LENGTH_SHORT).show();
                            }
                            dialog.dismiss();
                        }
                    });

                    //iconFile.getFileUrl()--返回上传文件的完整地址
                    Toast.makeText(PersonalPageActivity.this, "上传文件成功", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(PersonalPageActivity.this, "上传文件失败", Toast.LENGTH_SHORT).show();
                }
            }
        });
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
        startActivityForResult(intent, CROP_REQUEST_CODE);
    }

    public File saveImageToGallery(Context context, Bitmap bmp) {
        // 首先保存图片
        File appDir = new File(Environment.getExternalStorageDirectory(), "mypic");
        if (!appDir.exists()) {
            appDir.mkdir();
        }
        String fileName = System.currentTimeMillis() + ".jpg";
        File file = new File(appDir, fileName);
        imgPath = appDir + "/" + fileName;
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

    private String getRealImagePath() {
        String img_path = null;
        Uri uri = getImageUri();
        if (uri == null)
            return null;
        String scheme = uri.getScheme();
        if (scheme == null)
            return uri.getPath();
        else if (ContentResolver.SCHEME_FILE.equals(scheme)) {
            return uri.getPath();
        } else if ((ContentResolver.SCHEME_CONTENT.equals(scheme))) {
            String[] imgs = {MediaStore.Images.Media.DATA};//将图片URI转换成存储路径
            Cursor cursor = getContentResolver().query(uri, imgs, null, null, null);
            if (cursor != null) {
                if (cursor.moveToFirst()) {
                    int index = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
                    if (index > -1)
                        img_path = cursor.getString(index);
                }
                cursor.close();
            }
        }
        return img_path;
    }


    public Uri getImageUri() {
        File temp = new File(Environment.getExternalStorageDirectory() + "/head.jpg");
        return Uri.fromFile(temp);
    }
}
