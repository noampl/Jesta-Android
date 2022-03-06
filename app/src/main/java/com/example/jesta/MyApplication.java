package com.example.jesta;

import android.annotation.SuppressLint;
import android.app.Application;
import android.content.Context;

public class MyApplication extends Application {
    @SuppressLint("StaticFieldLeak")
    static Context context;

    public void onCreate() {
        super.onCreate();
      context = getApplicationContext();
    }

    public static Context getAppContext() {
        return MyApplication.context;
    }
}