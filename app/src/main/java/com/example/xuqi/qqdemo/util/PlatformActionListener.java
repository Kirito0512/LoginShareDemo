package com.example.xuqi.qqdemo.util;


public abstract interface PlatformActionListener {

    public abstract void onStart(Platform platform, int type);

    public abstract void onComplete(Platform platform, Object obj, int type);

    public abstract void onError(Platform platform, Throwable paramThrowable);

    public abstract void onCancel(Platform platform);
}