package com.me.guanpj.iocsample;

import android.app.Application;

import com.me.guanpj.ioclib.runtime.ActivityBuilder;

public class MyApplication extends Application {
    @Override
    public void onCreate() {
        super.onCreate();
        ActivityBuilder.INSTANCE.init(this);
    }
}
