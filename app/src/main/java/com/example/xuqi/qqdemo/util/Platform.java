package com.example.xuqi.qqdemo.util;

import android.app.Activity;
import android.content.Intent;

/**
 * Created by xuqi on 17/2/8.
 */

public abstract class Platform {
    public static final int QQ = 101;
    public static final int Sina = 102;
    protected PlatformActionListener listener;

    public Platform setPlatformActionListener(PlatformActionListener listener) {
        this.listener = listener;
        return this;
    }

    public abstract void authrize(Activity activity);

    public abstract void onActivityResult(int requestCode, int resultCode, Intent data);
}
