package com.example.keymappermouse;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;


import androidx.annotation.NonNull;

import com.example.keymappermouse.server.SocketClient;

import java.io.File;
import java.util.List;

public class Application extends android.app.Application {


    private static final String TAG = "Application";
    private static Application mApplication;
    private static int activityCount = 0;
    public static Application getInstance() {
        return mApplication;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        mApplication = this;

        registerActivityLifecycleCallbacks(new ActivityLifecycleCallbacks() {
            @Override
            public void onActivityCreated(Activity activity, Bundle savedInstanceState) {
                activityCount++;
            }

            @Override
            public void onActivityStarted(@NonNull Activity activity) {

            }

            @Override
            public void onActivityResumed(@NonNull Activity activity) {

            }

            @Override
            public void onActivityPaused(@NonNull Activity activity) {

            }

            @Override
            public void onActivityStopped(@NonNull Activity activity) {

            }

            @Override
            public void onActivitySaveInstanceState(@NonNull Activity activity, @NonNull Bundle outState) {

            }

            // 其他生命周期回调...

            @Override
            public void onActivityDestroyed(Activity activity) {
                activityCount--;
                if (activityCount == 0) {
                    // 所有Activity都被销毁了，这时可以做一些清理工作或标记应用即将退出
                    SocketClient.disconnectSocket();
                }
            }
        });
    }








}
