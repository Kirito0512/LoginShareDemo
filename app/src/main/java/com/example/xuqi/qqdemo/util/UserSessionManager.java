package com.example.xuqi.qqdemo.util;

import android.app.Activity;
import android.os.Environment;
import android.util.Log;

import com.example.xuqi.qqdemo.bean.NewsUserInfo;
import com.google.gson.Gson;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;

import static android.content.ContentValues.TAG;
import static com.example.xuqi.qqdemo.LoginActivity.mSinaWeiboPlatform;
import static com.example.xuqi.qqdemo.LoginActivity.mTencentPlatform;

/**
 * Created by xuqi on 17/2/24.
 */

public class UserSessionManager {
    private static UserSessionManager instance = null;
    public NewsUserInfo currentUser;
    private final static String STORE_FILE_EN = "i.dat";
    private final static String DES_KEY = "236ebd59848e95c80468ac4f6ebab136";


    public static synchronized UserSessionManager getInstance() {
        if (instance == null) {
            instance = new UserSessionManager();
            instance.currentUser = instance.loadFromDisk();
        }
        return instance;
    }

    public synchronized static void setCurrentUser(NewsUserInfo user){
        if (user == null)
            return;
        getInstance().currentUser = user;
        getInstance().storeUserToDisk(user);
    }

    public static NewsUserInfo getCurrentUser() {
        return getInstance().currentUser;
    }

    public static boolean isAleadyLogin() {
        return getCurrentUser() != null && getCurrentUser().getName() != null && getCurrentUser().getName() != "";
    }

    public static File getConfigDir(){
        File easylive = new File(Environment.getExternalStorageDirectory(), ".news/config");
        if (!easylive.exists())
            easylive.mkdirs();
        return easylive;
    }

    public static String loadTextFromFile(File file) {
        final StringBuilder sb = new StringBuilder();
        if (file != null && file.exists()) {
            BufferedReader fr = null;
            try {
                fr = new BufferedReader(new FileReader(file));
                String s;
                while ((s = fr.readLine()) != null) {
                    sb.append(s);
                }
            } catch (FileNotFoundException e1) {
                // 不会发生.
            } catch (IOException e) {
                return "";
            } finally {
                try {
                    if (fr != null)
                        fr.close();
                } catch (IOException e1) {
                }
            }
        }
        return sb.toString();
    }

    public NewsUserInfo loadFromDisk(){
        File dir1 = getConfigDir();
        File loginData1= new File(dir1, STORE_FILE_EN);
        if(loginData1 != null && loginData1.exists()){
            try {
//                String json = new String(Base64.decode(loadTextFromFile(loginData1),Base64.DEFAULT));
                Gson gson = new Gson();
                Log.d("xuqi", "loadFromDisk: "+loadTextFromFile(loginData1));
                NewsUserInfo user = gson.fromJson(loadTextFromFile(loginData1), NewsUserInfo.class);
                return user;
            } catch (Exception e) {
                return fakeUser();
            }
        }
        return null;
    }

    public static void storeUserToDisk(NewsUserInfo user){

//        File dir = new File(easylive, STORE_FILE_EN);

        Gson gson = new Gson();
        String json = gson.toJson(user);
        File dir1 = getConfigDir();
        File loginData = new File(dir1, STORE_FILE_EN);
//        json = Base64.encodeToString(json.getBytes(), Base64.DEFAULT);
        Log.d(TAG, "storeUserToDisk: "+json);
        BufferedWriter output = null;
        try {
            output = new BufferedWriter(new FileWriter(loginData));
            output.write(json);
            output.flush();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                if (output != null)
                    output.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

    }

    public synchronized static void reSetCurrentUser() {
        getInstance().currentUser = getInstance().fakeUser();
        File dir = getConfigDir();
        File loginData = new File(dir, STORE_FILE_EN);
        if (loginData.exists()) {
            loginData.delete();
        }
    }

    public static void logout(Activity activity) {
        if (getCurrentUser().getSource().equals("QQ")) {
            mTencentPlatform.logout(activity);
        } else if (getCurrentUser().equals("SINA")) {
            mSinaWeiboPlatform.logout(activity);
        }
    }

    public static boolean isMySelf(int userid) {
        return userid > 0 && getCurrentUser().getUserId() == userid;
    }


    public static boolean isMySelf(NewsUserInfo user) {
        return user != null && user.getUserId() > 0 && getCurrentUser().getUserId() == user.getUserId();
    }

    private NewsUserInfo fakeUser() {
        NewsUserInfo user = new NewsUserInfo();
        user.setUserId(0);// userid==0 表示没有登录
        user.setName("");
        user.setHeadPhoto("");
        return user;
    }
}
