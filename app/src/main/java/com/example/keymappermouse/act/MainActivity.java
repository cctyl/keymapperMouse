package com.example.keymappermouse.act;

import androidx.appcompat.app.AppCompatActivity;

import android.accessibilityservice.AccessibilityServiceInfo;
import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.provider.Settings;
import android.util.Log;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.view.accessibility.AccessibilityManager;
import android.widget.Button;
import android.widget.Toast;

import com.example.keymappermouse.R;
import com.example.keymappermouse.server.SocketClient;
import com.example.keymappermouse.service.FloatViewService;
import com.example.keymappermouse.util.RootShellCmd;

import java.util.List;

public class MainActivity extends AppCompatActivity {

    private static final String TAG = "MainActivity";
    private Button btn_root;
    private Button btn_fuzhu;
    private Button btn_adb;
    private Button btn_adb_stop;



    @Override
    protected void onCreate(Bundle savedInstanceState) {

        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


        btn_fuzhu = findViewById(R.id.btn_fuzhu);
        btn_fuzhu.setOnClickListener(view -> {
            checkAndOpenFuzhu();
        });

        btn_root = findViewById(R.id.btn_root);
        btn_root.setOnClickListener(view -> {
            RootShellCmd.root = true;
        });

        btn_adb = findViewById(R.id.btn_adb);
        btn_adb.setOnClickListener(view -> {
            RootShellCmd.root = false;
            SocketClient.initSocketClient();
        });

        btn_adb_stop = findViewById(R.id.btn_adb_stop);
        btn_adb_stop.setOnClickListener(view -> {
            RootShellCmd.root = true;
            SocketClient.disconnectSocket();
        });

        startFloatView();

    }

    private String getApkPath() throws PackageManager.NameNotFoundException {
        String packageName = getPackageName(); // 获取当前应用的包名
            PackageManager pm = getPackageManager();
            ApplicationInfo ai = pm.getApplicationInfo(packageName, 0); // 获取应用信息
            String apkPath = ai.sourceDir; // 获取APK文件路径
            Log.d("APKPath", "APK路径: " + apkPath);
            return apkPath;

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
            openFuzhu();
        } else {
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
    protected void onResume() {
        super.onResume();

    }
}