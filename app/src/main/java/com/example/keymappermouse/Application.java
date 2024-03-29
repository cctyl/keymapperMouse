package com.example.keymappermouse;

import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Environment;
import android.util.Log;



import java.io.File;
import java.util.List;

public class Application extends android.app.Application {


    private static final String TAG = "Application";
    private static Application mApplication;

    public static Application getInstance() {
        return mApplication;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        mApplication = this;
    }

}
