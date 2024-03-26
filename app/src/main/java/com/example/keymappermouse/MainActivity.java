package com.example.keymappermouse;

import androidx.appcompat.app.AppCompatActivity;

import android.accessibilityservice.AccessibilityServiceInfo;
import android.app.Instrumentation;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.SystemClock;
import android.provider.Settings;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.view.accessibility.AccessibilityManager;
import android.webkit.WebView;
import android.widget.ImageView;
import android.widget.Toast;

import java.util.List;
import java.util.concurrent.BlockingDeque;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.LinkedBlockingDeque;
import java.util.concurrent.LinkedBlockingQueue;

public class MainActivity extends AppCompatActivity {


    private static final String TAG = "MainActivity";
    private int mouseX = 0;
    private int mouseY = 0;
    private Instrumentation instrumentation;


    private static final ExecutorService executorService = Executors.newFixedThreadPool(2);

    public void runAsync(Runnable runnable) {
        executorService.submit(runnable);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        startFloatView();


        checkAndOpenFuzhu();




        // 初始化Instrumentation对象
        instrumentation = new Instrumentation();
    }

    private void startFloatView() {
        // 创建一个 Intent 对象，指定要启动的 Service 类
        Intent serviceIntent = new Intent(this, FloatViewService.class);
        // 调用 startService() 方法启动 Service
        startService(serviceIntent);
    }

    private void checkAndOpenFuzhu() {

        AccessibilityManager am = (AccessibilityManager) this.getSystemService((ACCESSIBILITY_SERVICE));
        List<AccessibilityServiceInfo> serviceinfos = am.getEnabledAccessibilityServiceList(AccessibilityServiceInfo.FEEDBACK_GENERIC);
        String id = null;
        boolean isServiceOpen = false;
        for (AccessibilityServiceInfo info : serviceinfos) {
            id = info.getId();
            if (id.contains("keyService")) {
                isServiceOpen = true;
                break;
            }
        }
        if (!isServiceOpen) {
            Toast.makeText(getApplicationContext(), "xxx辅助功能未开启!", Toast.LENGTH_SHORT).show();
            applyRoot();
            openFuzhu();
        }else {
            Toast.makeText(getApplicationContext(), "辅助功能已开启", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public boolean onGenericMotionEvent(MotionEvent event) {
        Log.d(TAG, "onGenericMotionEvent: ");
        if (event.getAction() == MotionEvent.ACTION_HOVER_MOVE) {

        }
        return super.onGenericMotionEvent(event);
    }


    private void applyRoot() {
        //申请root权限的
//        String apkRoot="chmod 777 "+getPackageCodePath();
//        ALPermissionManager .RootCommand(apkRoot);
    }

    /**
     * 开启辅助功能
     */
    private void openFuzhu() {
        Intent intent = new Intent(Settings.ACTION_ACCESSIBILITY_SETTINGS);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);

    }

    @Override
    public boolean onKeyUp(int keyCode, KeyEvent event) {

        Log.d(TAG, "按下按键，keyCode=" + keyCode);
        Log.d(TAG, event.toString());

        return super.onKeyDown(keyCode, event);
    }

    @Override
    public boolean dispatchKeyEvent(KeyEvent event) {
        Log.d(TAG, "dispatchKeyEvent: " + mouseX + "," + mouseY);
        if (event.getAction() == KeyEvent.ACTION_DOWN) {
            switch (event.getKeyCode()) {
                case KeyEvent.KEYCODE_DPAD_UP:
                    // 向上移动鼠标
                    mouseY -= 10;
                    break;
                case KeyEvent.KEYCODE_DPAD_DOWN:
                    // 向下移动鼠标
                    mouseY += 10;
                    break;
                case KeyEvent.KEYCODE_DPAD_LEFT:
                    // 向左移动鼠标
                    mouseX -= 10;
                    break;
                case KeyEvent.KEYCODE_DPAD_RIGHT:
                    // 向右移动鼠标
                    mouseX += 10;
                    break;
                case KeyEvent.KEYCODE_ENTER:

                    Log.d(TAG, "按下屏幕: ");

                    runAsync(new Runnable() {
                        @Override
                        public void run() {
                            try {
                                // 将鼠标移动到指定位置
                                instrumentation.sendPointerSync(MotionEvent.obtain(
                                        SystemClock.uptimeMillis(),
                                        SystemClock.uptimeMillis(),
                                        MotionEvent.ACTION_MOVE,
                                        mouseX,
                                        mouseY,
                                        0
                                ));

                                // 模拟点击屏幕
                                instrumentation.sendPointerSync(MotionEvent.obtain(
                                        SystemClock.uptimeMillis(),
                                        SystemClock.uptimeMillis(),
                                        MotionEvent.ACTION_DOWN,
                                        mouseX,
                                        mouseY,
                                        0
                                ));
                                instrumentation.sendPointerSync(MotionEvent.obtain(
                                        SystemClock.uptimeMillis(),
                                        SystemClock.uptimeMillis(),
                                        MotionEvent.ACTION_UP,
                                        mouseX,
                                        mouseY,
                                        0
                                ));
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                        }
                    });

                    break;
            }
        }




        return super.dispatchKeyEvent(event);
    }



}