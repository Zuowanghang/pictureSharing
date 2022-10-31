package com.example.picturesharing.MyToast;


import android.app.Application;
import android.content.Context;

/**
 * Created by root on 15-10-1.
 */
public class MyApplication extends Application {

    private static Context context;

    @Override
    public void onCreate() {
        super.onCreate();
        context = getApplicationContext();
    }

    public static Context getContext() {
        return context;
    }
}